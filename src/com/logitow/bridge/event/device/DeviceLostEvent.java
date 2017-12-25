package com.logitow.bridge.event.device;

import com.logitow.bridge.communication.Device;

/**
 * Called when the device is lost once discovered.
 */
public class DeviceLostEvent extends DeviceEvent {
    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceLostEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        System.out.println("Device " + device + " no longer available.");
    }
}
