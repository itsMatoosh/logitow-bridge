package com.logitow.bridge.event.device;

import com.logitow.bridge.communication.Device;

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

    }
}
