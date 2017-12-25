package bridge.communication.platform.linux;

import bridge.communication.BluetoothState;
import bridge.communication.Device;
import bridge.communication.LogitowDeviceManager;
import bridge.communication.platform.PlatformType;
import bridge.event.EventManager;
import bridge.event.devicemanager.DeviceManagerCreatedEvent;

/**
 * Manages native Linux device communication.
 */
public class LinuxDeviceManager extends LogitowDeviceManager {
    /**
     * Sets up the device manager.
     */
    @Override
    public boolean initialize() {
        this.platform = PlatformType.LINUX;

        //Calling the device manager created event.
        EventManager.callEvent(new DeviceManagerCreatedEvent(this));
        return false;
    }

    /**
     * Starts LOGITOW device discovery.
     */
    @Override
    public boolean startDeviceDiscovery() {
        return false;
    }

    /**
     * Stops LOGITOW device discovery.
     */
    @Override
    public boolean stopDeviceDiscovery() {
        return false;
    }

    /**
     * Connects to the specified device.
     *
     * @param device
     * @return
     */
    @Override
    public boolean connectDevice(Device device) {
        return false;
    }

    /**
     * Disconnects the specified device.
     *
     * @param device
     * @return
     */
    @Override
    public boolean disconnectDevice(Device device) {
        return false;
    }

    /**
     * Gets the current bluetooth adapter state.
     *
     * @return
     */
    @Override
    public BluetoothState getBluetoothState() {
        return null;
    }
}
