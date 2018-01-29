package com.logitow.bridge.event.device;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when a device is disconnected.
 */
public class DeviceDisconnectedEvent extends DeviceEvent {
    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceDisconnectedEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.info("Logitow device {} disconnected!", device);
        device.info.isConnected = false;
    }
}
