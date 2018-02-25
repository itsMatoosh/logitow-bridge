using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Windows.Devices.Bluetooth;
using Windows.Devices.Enumeration;
using Windows.Devices.Radios;

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
        /// The default bluetooth adapter.
        /// </summary>
        public BluetoothAdapter bleAdapter;
        /// <summary>
        /// The currently available radios.
        /// </summary>
        public IReadOnlyList<Radio> availableRadios;
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
        /// The currently registered event listener.
        /// </summary>
        public DeviceEventReceiver eventListener;

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
                Setup();
            }
        }

        /// <summary>
        /// Sets up the scanner.
        /// </summary>
        public async Task Setup()
        {
            Console.WriteLine("Setting up the windows native device manager...");
            bleAdapter = await BluetoothAdapter.GetDefaultAsync();
            availableRadios = await Radio.GetRadiosAsync();
            Console.WriteLine("Set up the windows native device manager");
        }

        #region General
        /// <summary>
        /// Sets up the scanner with an event listener.
        /// </summary>
        /// <param name="eventListener"></param>
        public void SetEventListener(DeviceEventReceiver eventListener)
        {
            this.eventListener = eventListener;
        }

        /// <summary>
        /// Gets an instance of LogitowDevice based on its uuid.
        /// </summary>
        /// <param name="uuid"></param>
        public LogitowDevice GetConnectedLogitowDevice(string uuid)
        {
            foreach(LogitowDevice device in LogitowDevice.connectedDevices)
            {
                if(device.deviceInfo.Id.Equals(uuid))
                {
                    return device;
                }
            }
            return null;
        }
        /// <summary>
        /// Gets a discovered logitow ble device given uuid.
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public LogitowDevice GetDiscoveredLogitowDevice(string id)
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
        private DeviceInformation GetUnknownDevice(string id)
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
        /// Checks whether bluetooth is available on the device.
        /// </summary>
        /// <returns></returns>
        public bool GetBluetoothSupported()
        {
            if (bleAdapter == null) return false;
            if (!bleAdapter.IsCentralRoleSupported) return false;

            return availableRadios.FirstOrDefault(radio => radio.Kind == RadioKind.Bluetooth) != null;
        }
        /// <summary>
        /// Checks whether bluetooth is enabled on the device.
        /// </summary>
        /// <returns></returns>
        public bool GetBluetoothEnabled()
        {
            var bluetoothRadio = availableRadios.FirstOrDefault(radio => radio.Kind == RadioKind.Bluetooth);
            return bluetoothRadio != null && bluetoothRadio.State == RadioState.On;
        }
        #endregion
        #region DeviceDiscovery
        /// <summary>
        /// Starts a device watcher that looks for all nearby Bluetooth devices (paired or unpaired). 
        /// Attaches event handlers to populate the device collection.
        /// </summary>
        public void StartBleDeviceWatcher()
        {
            Console.WriteLine("Starting Logitow device discovery!");

            //Setting up the device watcher.
            if(deviceWatcher == null)
            {
                //Additional properties we would like about the device.
                //Getting the device address, whether it is currently connected and if the device is a BLE device.
                string[] requestedProperties = { "System.Devices.Aep.DeviceAddress", "System.Devices.Aep.IsConnected", "System.Devices.Aep.Bluetooth.Le.IsConnectable", "System.Devices.Aep.IsPaired" };

                //Instantiating the device watcher.
                deviceWatcher = DeviceInformation.CreateWatcher(
                            "",
                            requestedProperties,
                            DeviceInformationKind.AssociationEndpoint);

                //Registering watcher events.
                deviceWatcher.Added += DeviceWatcher_Added;
                deviceWatcher.Updated += DeviceWatcher_Updated;
                deviceWatcher.Removed += DeviceWatcher_Removed;
                deviceWatcher.EnumerationCompleted += DeviceWatcher_EnumerationCompleted;
                deviceWatcher.Stopped += DeviceWatcher_Stopped;
            }

            //Start over with an empty collection.
            discoveredDevices.Clear();
            unknownDevices.Clear();
        
            //Start the watcher.
            deviceWatcher.Start();

            //Notifying the jvm.
            Instance.eventListener.OnScanStarted();
        }

        /// <summary>
        /// Stops watching for all nearby Bluetooth devices.
        /// </summary>
        public void StopBleDeviceWatcher()
        {
            if (deviceWatcher != null)
            {
                //Stopping the watcher.
                deviceWatcher.Stop();
            }
        }

        /// <summary>
        /// Called when a new ble device is discovered.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="deviceInfo"></param>
        private void DeviceWatcher_Added(DeviceWatcher sender, DeviceInformation deviceInfo)
        {
            lock (this)
            {
                //Making sure that we are processing device from the current device watcher.
                if (sender == deviceWatcher)
                {
                    //Making sure device isn't already present in the list.
                    if (GetDiscoveredLogitowDevice(deviceInfo.Id) == null)
                    {
                        //Checking if the device has info or not.
                        bool connectable = false;
                        Boolean.TryParse(deviceInfo.Properties["System.Devices.Aep.Bluetooth.Le.IsConnectable"].ToString(), out connectable);
                        if (System.String.IsNullOrEmpty(deviceInfo.Name))
                        {
                            unknownDevices.Add(deviceInfo);
                        }
                        else if (deviceInfo.Name == "LOGITOW" && connectable)
                        {
                            Console.WriteLine(System.String.Format("Discovered LOGITOW device: {0}", deviceInfo.Id));
                            
                            //If device has name LOGITOW, adding it to list.
                            var logitowDevice = new LogitowDevice(deviceInfo);
                            discoveredDevices.Add(logitowDevice);
                            eventListener.OnDeviceDiscovered(logitowDevice.deviceInfo.Id);
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
            lock(this)
            {
                Console.WriteLine(System.String.Format("Updated {0}, {1}", deviceInfoUpdate.Id, ""));

                //Making sure that we are processing device from the current device watcher.
                if (sender == deviceWatcher)
                {
                    LogitowDevice logitowDevice = GetDiscoveredLogitowDevice(deviceInfoUpdate.Id);
                    if (logitowDevice != null)
                    {
                        //Device has already been discovered. Updating the info.
                        logitowDevice.OnBluetoothInfoUpdate(deviceInfoUpdate);
                        return;
                    }
                    else
                    {
                        //Checking in the unknown devices list.
                        DeviceInformation information = GetUnknownDevice(deviceInfoUpdate.Id);

                        //Updating the device info.
                        if(information != null)
                        {
                            information.Update(deviceInfoUpdate);

                            //Checking if the name has been fetched and if it is LOGITOW.
                            if (!System.String.IsNullOrEmpty(information.Name) && information.Name == "LOGITOW" && (bool)information.Properties["System.Devices.Aep.Bluetooth.Le.IsConnectable"] == true)
                            {
                                unknownDevices.Remove(information);
                                LogitowDevice device = new LogitowDevice(information);
                                Console.WriteLine(System.String.Format("Added {0} to the discovered LOGITOW bricks list.", information.Id));
                                discoveredDevices.Add(logitowDevice);
                                eventListener.OnDeviceDiscovered(device.deviceInfo.Id);
                            }
                        }
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
            lock(this)
            {
                Console.WriteLine(System.String.Format("Removed {0}, {1}", deviceInfoUpdate.Id, ""));

                //Making sure that we are processing device from the current device watcher.
                if (sender == deviceWatcher)
                {
                    //Find the corresponding DeviceInformation in the collection and remove it.
                    LogitowDevice logitowDevice = GetDiscoveredLogitowDevice(deviceInfoUpdate.Id);
                    if (logitowDevice != null)
                    {
                        discoveredDevices.Remove(logitowDevice);
                        eventListener.OnDeviceLost(logitowDevice.deviceInfo.Id);
                    }
                    DeviceInformation unknownDevice = GetUnknownDevice(deviceInfoUpdate.Id);
                    if (unknownDevice != null)
                    {
                        unknownDevices.Remove(unknownDevice);
                    }
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

                eventListener.OnScanStopped();
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

                Instance.eventListener.OnScanStopped();
            }
        }

        /// <summary>
        /// Connects or reconnects to a logitow brick.
        /// </summary>
        public void Connect(LogitowDevice device)
        {
            device.ConnectAsync();
        }
        /// <summary>
        /// Disconnects the specified device.
        /// </summary>
        /// <param name="device"></param>
        public void Disconnect(LogitowDevice device) {
            device.DisconnectAsync();
        }
        #endregion
    }
}