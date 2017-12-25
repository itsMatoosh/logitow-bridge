using System;
using System.Collections.Generic;
using Windows.Devices.Enumeration;

namespace LogitowWindowsNative
{
    /// <summary>
    /// Used for scanning for active LOGITOW devices.
    /// </summary>
    public class Scanner
    {
        /// <summary>
        /// Device watcher.
        /// </summary>
        public DeviceWatcher deviceWatcher;
        /// <summary>
        /// List of unknown discovered devices.
        /// Will be unknown until the name is not updated.
        /// </summary>
        public List<DeviceInformation> unknownDevices = new List<DeviceInformation>();
        /// <summary>
        /// The currently discovered devices.
        /// </summary>
        public List<LogitowDevice> discoveredDevices = new List<LogitowDevice>();

        /// <summary>
        /// The instance of the scanner.
        /// </summary>
        public static Scanner Instance;

        /// <summary>
        /// Default constructor.
        /// </summary>
        public Scanner()
        {
            if(Instance == null)
            {
                Instance = this;
            }
        }

        #region DeviceDiscovery

        /// <summary>
        /// Starts a device watcher that looks for all nearby Bluetooth devices (paired or unpaired). 
        /// Attaches event handlers to populate the device collection.
        /// </summary>
        public void StartBleDeviceWatcher()
        {
            Console.WriteLine("Starting Logitow device discovery!");

            //Additional properties we would like about the device.
            //Getting the device address, whether it is currently connected and if the device is a BLE device.
            string[] requestedProperties = { "System.Devices.Aep.DeviceAddress", "System.Devices.Aep.IsConnected", "System.Devices.Aep.Bluetooth.Le.IsConnectable", "System.Devices.Aep.IsPaired" };

            //Filtering the uuids of the logitow blocks.
            //string logitowServiceUUIDFilter = "(System.Devices.Aep.ProtocolId:=\"{" + DEVICE_UUID + "}\")";

            //Instantiating the device watcher.
            deviceWatcher = DeviceInformation.CreateWatcher (
                        "",
                        requestedProperties,
                        DeviceInformationKind.AssociationEndpoint);

            //Registering watcher events.
            deviceWatcher.Added += DeviceWatcher_Added;
            deviceWatcher.Updated += DeviceWatcher_Updated;
            deviceWatcher.Removed += DeviceWatcher_Removed;
            deviceWatcher.EnumerationCompleted += DeviceWatcher_EnumerationCompleted;
            deviceWatcher.Stopped += DeviceWatcher_Stopped;

            //Start over with an empty collection.
            discoveredDevices.Clear();
        
            //Start the watcher.
            deviceWatcher.Start();
        }

        /// <summary>
        /// Stops watching for all nearby Bluetooth devices.
        /// </summary>
        public void StopBleDeviceWatcher()
        {
            if (deviceWatcher != null)
            {
                // Unregister the event handlers.
                deviceWatcher.Added -= DeviceWatcher_Added;
                deviceWatcher.Updated -= DeviceWatcher_Updated;
                deviceWatcher.Removed -= DeviceWatcher_Removed;
                deviceWatcher.EnumerationCompleted -= DeviceWatcher_EnumerationCompleted;
                deviceWatcher.Stopped -= DeviceWatcher_Stopped;

                // Stop the watcher.
                deviceWatcher.Stop();
                deviceWatcher = null;
            }
        }

        /// <summary>
        /// Gets a discovered logitow ble device given uuid.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        private LogitowDevice FindLogitowBleDevice(string id)
        {
            foreach (LogitowDevice logitowDevice in discoveredDevices)
            {
                if (logitowDevice.deviceInfo.Id == id)
                {
                    return logitowDevice;
                }
            }
            return null;
        }
        /// <summary>
        /// Gets an unknown ble device given uuid.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        private DeviceInformation FindUnknownDevice(string id)
        {
            foreach (DeviceInformation unknownDevice in unknownDevices)
            {
                if (unknownDevice.Id == id)
                {
                    return unknownDevice;
                }
            }
            return null;
        }

        /// <summary>
        /// Called when a new ble device is discovered.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="deviceInfo"></param>
        private void DeviceWatcher_Added(DeviceWatcher sender, DeviceInformation deviceInfo)
        {
            //Updating the discovered devices collection.
            lock (this)
            {
                Console.WriteLine(String.Format("Discovered {0}, {1}", deviceInfo.Id, deviceInfo.Name));

                //Making sure that we are processing device from the current device watcher.
                if (sender == deviceWatcher)
                {
                    //Making sure device isn't already present in the list.
                    if (FindLogitowBleDevice(deviceInfo.Id) == null)
                    {
                        if(String.IsNullOrEmpty(deviceInfo.Name))
                        {
                            unknownDevices.Add(deviceInfo);
                        } else if (deviceInfo.Name == "LOGITOW")
                        {
                            Console.WriteLine(String.Format("Added {0} to the discovered LOGITOW bricks list.", deviceInfo.Id));
                            //If device has name LOGITOW, adding it to list.
                            var logitowDevice = new LogitowDevice(deviceInfo);
                            discoveredDevices.Add(logitowDevice);
                            ConnectOrReconnect(logitowDevice);
                        }
                    }

                }
            }
        }
        
        /// <summary>
        /// Called when a device info is updated.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="deviceInfoUpdate"></param>
        private void DeviceWatcher_Updated(DeviceWatcher sender, DeviceInformationUpdate deviceInfoUpdate)
        {
            Console.WriteLine(String.Format("Updated {0}, {1}", deviceInfoUpdate.Id, ""));

            //Making sure that we are processing device from the current device watcher.
            if (sender == deviceWatcher)
            {
                LogitowDevice logitowDevice = FindLogitowBleDevice(deviceInfoUpdate.Id);
                if (logitowDevice != null)
                {
                    //Device has already been discovered. Updating the info.
                    logitowDevice.OnBluetoothInfoUpdate(deviceInfoUpdate);
                    return;
                } else
                {
                    //Checking in the unknown devices list.
                    DeviceInformation information = FindUnknownDevice(deviceInfoUpdate.Id);
                    
                    //Checking if the name has been fetched and if it LOGITOW.
                    if(!String.IsNullOrEmpty(information.Name) && information.Name == "LOGITOW")
                    {
                        unknownDevices.Remove(information);
                        information.Update(deviceInfoUpdate);
                        LogitowDevice device = new LogitowDevice(information);
                        Console.WriteLine(String.Format("Added {0} to the discovered LOGITOW bricks list.", information.Id));
                        //If device has name LOGITOW, adding it to list.
                        discoveredDevices.Add(logitowDevice);
                        ConnectOrReconnect(logitowDevice);
                    }
                }
            }
        }

        /// <summary>
        /// Called when a BLE device is removed from the system.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="deviceInfoUpdate"></param>
        private void DeviceWatcher_Removed(DeviceWatcher sender, DeviceInformationUpdate deviceInfoUpdate)
        {
            Console.WriteLine(String.Format("Removed {0}, {1}", deviceInfoUpdate.Id, ""));

            //Making sure that we are processing device from the current device watcher.
            if (sender == deviceWatcher)
            {
                //Find the corresponding DeviceInformation in the collection and remove it.
                LogitowDevice logitowDevice = FindLogitowBleDevice(deviceInfoUpdate.Id);
                if (logitowDevice != null)
                {
                    discoveredDevices.Remove(logitowDevice);
                }
            }
        }

        /// <summary>
        /// Called when the device enumeration concludes.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeviceWatcher_EnumerationCompleted(DeviceWatcher sender, object e)
        {
            //Making sure that we are processing device from the current device watcher.
            if (sender == deviceWatcher)
            {
                Console.WriteLine($"{discoveredDevices.Count} devices found. Scanning completed!");
            }
        }

        /// <summary>
        /// Called when the device wathcer is stopped.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeviceWatcher_Stopped(DeviceWatcher sender, object e)
        {
            //Making sure that we are processing device from the current device watcher.
            if (sender == deviceWatcher)
            {
                Console.WriteLine("Device discovery stopped.");
            }
        }

        /// <summary>
        /// Called when a connection error occurs with a device.
        /// </summary>
        /// <param name="device"></param>
        internal void OnDeviceDisconnected(LogitowDevice device)
        {
            //Restarting the scanner.
            StopBleDeviceWatcher();
            StartBleDeviceWatcher();
        }

        /// <summary>
        /// Connects or reconnects to a logitow brick.
        /// </summary>
        private void ConnectOrReconnect(LogitowDevice device)
        {
            foreach(LogitowDevice connected in LogitowDevice.connectedDevices)
            {
                if(connected.deviceInfo.Id == device.deviceInfo.Id)
                {
                    Console.WriteLine("Reconnecting...");
                    device.Disconnect();
                }
            }

            device.ConnectAsync();
        }
        #endregion
    }
}