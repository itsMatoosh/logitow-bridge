package com.logitow.bridge.event.devicemanager;

import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when a platform specific logitow device manager is instantiated.
 */
public class DeviceManagerCreatedEvent extends DeviceManagerEvent {

    public DeviceManagerCreatedEvent(LogitowDeviceManager manager) {
        super(manager);
    }

    @Override
    public void onCalled() {
        System.out.println(this.manager.platform + " LOGITOW device manager instantiated.");
    }
}
