package com.logitow.bridge.event.devicemanager;

import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.Event;

/**
 * An event concerning a device manager.
 */
public abstract class DeviceManagerEvent extends Event {
    /**
     * The manager.
     */
    public LogitowDeviceManager manager;

    public DeviceManagerEvent(LogitowDeviceManager manager) {
        this.manager = manager;
    }
}
