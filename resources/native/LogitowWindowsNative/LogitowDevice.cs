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

        S

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
                OnConnectionStatusUpdate(bluetoothLEDevice, BluetoothConnectionStatus.Connected);
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
            Console.WriteLine("Disconnecting the LOGITOW device: " + deviceInfo.Id);
            bluetoothLEDevice.Dispose();
        }
        /// <summary>
        /// Requests the device to send battery status.
        /// </summary>
        /// <returns></returns>
        public async Task RequestDeviceBatteryStatusUpdate()
        {
            if(bluetoothLEDevice != null && bluetoothLEDevice.ConnectionStatus == BluetoothConnectionStatus.Connected)
            {
                Console.WriteLine("Requesting LOGITOW: " + deviceInfo.Id + " battery status...");
                
                //Getting the device module driver service.
                DataWriter writer = new DataWriter();
                writer.WriteByte(0xAD);
                writer.WriteByte(0x02);
                await deviceModuleDriverReadWriteCharacteristic.WriteValueAsync(writer.DetachBuffer());
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
                    //Registering characteristics for data service.
                    if (service.Uuid == Guid.Parse(DEVICE_DATA_SERVICE_UUID))
                    {
                        await RegisterDataServiceCharacteristicsAsync(service);
                        continue;
                    }

                    //Registering characteristics for device module driver service.
                    if (service.Uuid == Guid.Parse(DEVICE_MODULE_DRIVER_SERVICE_UUID))
                    {
                        await RegisterDeviceModuleDriverServiceCharacteristicsAsync(service);
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
                    if (characteristic.Uuid == Guid.Parse(DEVICE_READ_DATA_CHARACTERTISTICS_UUID))
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
                    if (characteristic.Uuid == Guid.Parse(DEVICE_READ_WRITE_MODULE_DRIVER_CHARACTERISTICS_UUID))
                    {
                        //Caching the device module driver
                        deviceModuleDriverReadWriteCharacteristic = characteristic;

                        //Registering block state changed indicator.
                        GattCommunicationStatus commStatus = await characteristic.WriteClientCharacteristicConfigurationDescriptorAsync(GattClientCharacteristicConfigurationDescriptorValue.Notify);
                        if (commStatus == GattCommunicationStatus.Success)
                        {
                            characteristic.ValueChanged += OnDeviceBatteryInfoUpdated;
                            Console.WriteLine("Successfully registered notify for battery read.");
                            await RequestDeviceBatteryStatusUpdate();
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
        }

        /// <summary>
        /// Called when a block state update is received.
        /// </summary>
        private void OnBlockStateInfoUpdate(GattCharacteristic sender, GattValueChangedEventArgs args)
        {
            var reader = DataReader.FromBuffer(args.CharacteristicValue);
            byte[] data = new byte[7];
            reader.ReadBytes(data);

            //Interpreting the data.
            Console.WriteLine("Block A: " + data[0] + "" + data[1] + "" + data[2]);
            Console.WriteLine("Side: " + data[3]);
            Console.WriteLine("Block B: " + data[4] + "" + data[5] + "" + data[6]);
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
            } else
            {
                Console.WriteLine("LOGITOW device: " + deviceInfo.Id + " connected!");
                connectedDevices.Add(this);
            }
        }
        /// <summary>
        /// Called when a connection error occurs.
        /// </summary>
        private void OnConnectionError(Exception e)
        {
            //Initial connection error.
            Console.WriteLine("Connection error with device: " + deviceInfo.Id + ", msg: " + e.Message + "\n" + e.StackTrace);
        }
        /// <summary>
        /// Called when a connection error occurs.
        /// </summary>
        /// <param name="communicationStatus"></param>
        private void OnConnectionError(GattCommunicationStatus communicationStatus)
        {
            //Connection error while communicating.
            Console.WriteLine("Communication error with device: " + deviceInfo.Id + ", status: " + communicationStatus);
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