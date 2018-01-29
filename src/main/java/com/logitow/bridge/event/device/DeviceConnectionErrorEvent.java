package com.logitow.bridge.event.device;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when an error occurs during connection with device.
 */
public class DeviceConnectionErrorEvent extends DeviceEvent {
    public Exception exception;

    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceConnectionErrorEvent(Device device, Exception exception) {
        super(device);
        this.exception = exception;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.info("Couldn't connect to {}! \n{}", device, exception.getMessage());
        device.info.isConnected = false;
    }
}
