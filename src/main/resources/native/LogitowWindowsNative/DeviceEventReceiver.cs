using Windows.Devices.Bluetooth.GenericAttributeProfile;

namespace LogitowWindowsNative
{
    public interface DeviceEventReceiver
    {
        /// <summary>
        /// Called when a device is discovered.
        /// </summary>
        void OnDeviceDiscovered(string uuid);
        /// <summary>
        /// Called when a device is lost.
        /// </summary>
        /// <param name="device"></param>
        void OnDeviceLost(string uuid);

        /// <summary>
        /// Called when the device scan has been started.
        /// </summary>
        void OnScanStarted();
        /// <summary>
        /// Called when the device scan has finished.
        /// </summary>
        void OnScanStopped();

        /// <summary>
        /// Called when a LOGITOW device is connected.
        /// </summary>
        void OnDeviceConnected(string uuid);
        /// <summary>
        /// Called when a LOGITOW device is disconnected.
        /// </summary>
        void OnDeviceDisconnected(string uuid);

        /// <summary>
        /// Called when block data is received from a device.
        /// </summary>
        /// <param name="uuid"></param>
        void OnBlockDataReceived(string uuid, byte[] data);
        /// <summary>
        /// Called when battery info is received from a device.
        /// </summary>
        /// <param name="uuid"></param>
        void OnBatteryInfoReceived(string uuid, float battery);

        /// <summary>
        /// Called when an error occurs with connection to a device.
        /// </summary>
        void OnConnectionError(string uuid, GattCommunicationStatus communicationStatus);
    }
}
