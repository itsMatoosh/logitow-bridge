package com.logitow.bridge.communication.platform.windows;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.communication.platform.PlatformType;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.devicemanager.DeviceManagerCreatedEvent;

/**
 * Manages the native windows device communication.
 */
public class WindowsDeviceManager extends LogitowDeviceManager {
    /**
     * Sets up the device manager.
     */
    @Override
    public boolean initialize() {
        this.platform = PlatformType.WINDOWS;
        // create com.logitow.bridge, with default initialize
        // it will lookup jni4net.n.dll next to jni4net.j.jar
        /*Bridge.setVerbose(true);

        try {
            Bridge.init();
        } catch(IOException e) {
            System.out.println("Error establishing .Net com.logitow.bridge");
        }
        */

        //Calling the device manager created event.
        EventManager.callEvent(new DeviceManagerCreatedEvent(this));

        return true;
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
