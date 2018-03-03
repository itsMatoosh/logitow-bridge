package com.logitow.bridge.communication;

import java.io.Serializable;

/**
 * Contains information about a LOGITOW device.
 */
public class DeviceInfo implements Serializable{
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
