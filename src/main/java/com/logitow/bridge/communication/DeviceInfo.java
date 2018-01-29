package com.logitow.bridge.communication;

/**
 * Contains information about a LOGITOW device.
 */
public class DeviceInfo {
    /**
     * The bluetooth uuid of the device.
     */
    public String uuid;

    /**
     * The friendly name for the device.
     */
    public String friendlyName;

    /**
     * Whether the bluetooth device is connected.
     */
    public boolean isConnected;
}
