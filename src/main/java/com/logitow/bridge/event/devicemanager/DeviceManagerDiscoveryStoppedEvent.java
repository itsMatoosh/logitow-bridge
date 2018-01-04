package com.logitow.bridge.event.devicemanager;

import com.logitow.bridge.communication.LogitowDeviceManager;

public class DeviceManagerDiscoveryStoppedEvent extends DeviceManagerEvent{
    public DeviceManagerDiscoveryStoppedEvent(LogitowDeviceManager manager) {
        super(manager);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
