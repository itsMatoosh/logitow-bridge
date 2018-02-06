using System;
using Windows.Devices.Bluetooth;
using Windows.Devices.Bluetooth.GenericAttributeProfile;
using Windows.Devices.Enumeration;
using Windows.Storage.Streams;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace LogitowWindowsNative
{
    /// <summary>
    /// Represents a single Logitow device.
    /// </summary>
    public class LogitowDevice
    {
        /// <summary>
        /// List of all currently connected LOGITOW devices.
        /// </summary>
        public static List<LogitowDevice> connectedDevices = new List<LogitowDevice>();

        //BLE uuids.
        public static Guid DEVICE_DATA_SERVICE_UUID = Guid.Parse("69400001-b5a3-f393-e0a9-e50e24dcca99");
        public static Guid DEVICE_DATA_READ_CHARACTERISTIC_UUID = Guid.Parse("69400003-b5a3-f393-e0a9-e50e24dcca99");
        public static Guid DEVICE_DATA_WRITE_CHARACTERISTIC_UUID = Guid.Parse("69400002-b5a3-f393-e0a9-e50e24dcca99");
        public static Guid DEVICE_BATTERY_SERVICE_UUID = Guid.Parse("7f510004-b5a3-f393-e0a9-e50e24dcca9e");
        public static Guid DEVICE_BATTERY_READ_CHARACTERISTIC_UUID = Guid.Parse("7f510006-b5a3-f393-e0a9-e50e24dcca9e");
        public static Guid DEVICE_BATTERY_WRITE_CHARACTERISTIC_UUID = Guid.Parse("7f510005-b5a3-f393-e0a9-e50e24dcca9e");

        /// <summary>
        /// BLE info about the device.
        /// </summary>
        public DeviceInformation deviceInfo;

        /// <summary>
        /// The ble device handle of this device.
        /// </summary>
        public BluetoothLEDevice bluetoothLEDevice;

        /// <summary>
        /// The ble chareacteristics for requesting battery status.
        /// </summary>
        public GattCharacteristic deviceModuleDriverReadWriteCharacteristic;

        /// <summary>
        /// Constructs a logitow device instance given device BLE info.
        /// </summary>
        /// <param name="deviceInformation"></param>
        public LogitowDevice(DeviceInformation deviceInformation)
        {
            this.deviceInfo = deviceInformation;        
        }

        /// <summary>
        /// Connects to the device.
        /// </summary>
        public async Task ConnectAsync()
        {
            Console.WriteLine("Connecting to LOGITOW device: " + deviceInfo.Id);
            try
            {
                bluetoothLEDevice = await BluetoothLEDevice.FromIdAsync(deviceInfo.Id);
                await RegisterNotificationsAsync();
            }
            catch (Exception e)
            {
                OnConnectionError(e);
            }
        }
        /// <summary>
        /// Disconnects the device.
        /// </summary>
        /// <returns></returns>
        public void Disconnect()
        {
            if (deviceInfo != null && bluetoothLEDevice != null)
            {
                Console.WriteLine("Disconnecting the LOGITOW device: " + deviceInfo.Id);
                Scanner.Instance.eventListener.OnDeviceDisconnected(this.deviceInfo.Id);
                if (bluetoothLEDevice == null) return;
                bluetoothLEDevice.Dispose();
                bluetoothLEDevice = null;
            }
            connectedDevices.Remove(this);
        }
        /// <summary>
        /// Requests the device to send battery status.
        /// </summary>
        /// <returns></returns>
        public void RequestDeviceBatteryStatusUpdate()
        {
            if(bluetoothLEDevice != null && bluetoothLEDevice.ConnectionStatus == BluetoothConnectionStatus.Connected)
            {
                Console.WriteLine("Requesting LOGITOW: " + deviceInfo.Id + " battery status...");
                
                //Getting the device module driver service.
                DataWriter writer = new DataWriter();
                writer.WriteByte(0xAD);
                writer.WriteByte(0x02);
                deviceModuleDriverReadWriteCharacteristic.WriteValueAsync(writer.DetachBuffer());
            } else
            {
                Console.WriteLine("Can't request battery level updates, as the device: " + deviceInfo.Id + "is not connected.");
            }
        }

        /// <summary>
        /// Registers handlers for device notifications.
        /// </summary>
        internal async Task RegisterNotificationsAsync()
        {
            Console.WriteLine("Registering notifications for: " + deviceInfo.Id);

            //Registering connection notifications.
            bluetoothLEDevice.ConnectionStatusChanged += OnConnectionStatusUpdate;

            //Getting the data service.
            GattDeviceServicesResult servicesPullResult = await bluetoothLEDevice.GetGattServicesAsync();
            if(servicesPullResult.Status == GattCommunicationStatus.Success)
            {
                //Registering the notifications one after another with compliance with the class A block limitation.
                foreach (GattDeviceService service in servicesPullResult.Services)
                {
                    //Registering characteristics for device module driver service.
                    if (service.Uuid == DEVICE_BATTERY_SERVICE_UUID)
                    {
                        await RegisterDeviceModuleDriverServiceCharacteristicsAsync(service);
                        continue;
                    }

                    //Registering characteristics for data service.
                    if (service.Uuid == DEVICE_DATA_SERVICE_UUID)
                    {
                        await RegisterDataServiceCharacteristicsAsync(service);
                        continue;
                    }
                }
            } else
            {
                OnConnectionError(servicesPullResult.Status);
            }
        }

        /// <summary>
        /// Registers characteristic handlers for the device data service.
        /// </summary>
        /// <param name="service"></param>
        private async Task RegisterDataServiceCharacteristicsAsync(GattDeviceService service)
        {
            //Getting the characteristics of the service.
            GattCharacteristicsResult results = await service.GetCharacteristicsAsync();
            if (results.Status == GattCommunicationStatus.Success)
            {
                foreach (GattCharacteristic characteristic in results.Characteristics)
                {
                    if (characteristic.Uuid ==DEVICE_DATA_READ_CHARACTERISTIC_UUID)
                    {
                        //Registering block state changed indicator.
                        GattCommunicationStatus commStatus = await characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        if (commStatus == GattCommunicationStatus.Success)
                        {
                            characteristic.ValueChanged += OnBlockStateInfoUpdate;
                            Console.WriteLine("Successfully registered notify for data read.");
                        }
                        else
                        {
                            OnConnectionError(commStatus);
                        }
                    }
                }
            } else
            {
                OnConnectionError(results.Status);
            }
        }

        /// <summary>
        /// Registers characteristic handlers for the device driver module service.
        /// </summary>
        /// <param name="service"></param>
        private async Task RegisterDeviceModuleDriverServiceCharacteristicsAsync(GattDeviceService service)
        {
            //Getting the characteristics of the service.
            GattCharacteristicsResult results = await service.GetCharacteristicsAsync();
            if (results.Status == GattCommunicationStatus.Success)
            {
                foreach (GattCharacteristic characteristic in results.Characteristics)
                {
                    if (characteristic.Uuid == DEVICE_BATTERY_WRITE_CHARACTERISTIC_UUID)
                    {
                        //Caching the device module driver
                        deviceModuleDriverReadWriteCharacteristic = characteristic;

                        //Registering block state changed indicator.
                        GattCommunicationStatus commStatus = await characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        if (commStatus == GattCommunicationStatus.Success)
                        {
                            characteristic.ValueChanged += OnDeviceBatteryInfoUpdated;
                            Console.WriteLine("Successfully registered notify for battery read.");


                            OnConnectionStatusUpdate(bluetoothLEDevice, BluetoothConnectionStatus.Connected);
                        }
                        else
                        {
                            OnConnectionError(commStatus);
                        }
                    }
                }
            }
            else
            {
                OnConnectionError(results.Status);
            }
        }

        /// <summary>
        /// Called when battery info update is received.
        /// </summary>
        private void OnDeviceBatteryInfoUpdated(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            var reader = DataReader.FromBuffer(args.CharacteristicValue);
            byte[] data = new byte[2];
            reader.ReadBytes(data);
            uint i = BitConverter.ToUInt32(new byte[] { data[0] }, 0);
            uint j = BitConverter.ToUInt32(data, 1);

            Console.WriteLine("Device battery level updated: " + i + "." + j + "V");

            Scanner.Instance.eventListener.OnBatteryInfoReceived(this.deviceInfo.Id, float.Parse(i + "." + j));
        }

        /// <summary>
        /// Called when a block state update is received.
        /// </summary>
        private void OnBlockStateInfoUpdate(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            lock(this)
            {
                var reader = DataReader.FromBuffer(args.CharacteristicValue);
                byte[] data = new byte[7];
                reader.ReadBytes(data);

                //Interpreting the data.
                Console.WriteLine("Block A: " + data[0] + "" + data[1] + "" + data[2]);
                Console.WriteLine("Side: " + data[3]);
                Console.WriteLine("Block B: " + data[4] + "" + data[5] + "" + data[6]);

                Scanner.Instance.eventListener.OnBlockDataReceived(this.deviceInfo.Id, data);
            }
        }

        /// <summary>
        /// Called when a connection status changes.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnConnectionStatusUpdate(BluetoothLEDevice sender, object args)
        {
            //The device was disconnected.
            if((BluetoothConnectionStatus) args == BluetoothConnectionStatus.Disconnected)
            {
                Console.WriteLine("LOGITOW device: " + deviceInfo.Id + " disconnected!");
                connectedDevices.Remove(this);
                Scanner.Instance.OnDeviceDisconnected(this);
                Scanner.Instance.eventListener.OnDeviceDisconnected(this.deviceInfo.Id);
            } else
            {
                Console.WriteLine("LOGITOW device: " + deviceInfo.Id + " connected!");
                connectedDevices.Add(this);
                Scanner.Instance.eventListener.OnDeviceConnected(this.deviceInfo.Id);
            }
        }
        /// <summary>
        /// Called when a connection error occurs.
        /// </summary>
        private void OnConnectionError(Exception e)
        {
            //Initial connection error.
            Console.WriteLine("Connection error with device: " + deviceInfo.Id + ", msg: " + e.Message + "\n" + e.StackTrace);

            Scanner.Instance.eventListener.OnConnectionError(this.deviceInfo.Id, GattCommunicationStatus.ProtocolError);

            Disconnect();
        }
        /// <summary>
        /// Called when a connection error occurs.
        /// </summary>
        /// <param name="communicationStatus"></param>
        private void OnConnectionError(GattCommunicationStatus communicationStatus)
        {
            //Connection error while communicating.
            Console.WriteLine("Communication error with device: " + deviceInfo.Id + ", status: " + communicationStatus);

            //Calling event.
            Scanner.Instance.eventListener.OnConnectionError(this.deviceInfo.Id, communicationStatus);

            Disconnect();
        }

        /// <summary>
        /// Called when the device info for this device has been updated.
        /// </summary>
        /// <param name="deviceInformation"></param>
        internal void OnBluetoothInfoUpdate(DeviceInformationUpdate deviceInformation)
        {
            this.deviceInfo.Update(deviceInformation);
        }
    }
}