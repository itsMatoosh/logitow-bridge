package com.logitow.bridge.event.devicemanager;

import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when an error occurs with a device manager.
 */
public class DeviceManagerErrorEvent extends DeviceManagerEvent {
    /**
     * The exception occured.
     */
    public Exception exception;

    public DeviceManagerErrorEvent(LogitowDeviceManager manager, Exception error) {
        super(manager);
        this.exception = error;
    }

    @Override
    public void onCalled() {
        System.out.println(exception.getMessage());
        exception.printStackTrace();
    }
}
