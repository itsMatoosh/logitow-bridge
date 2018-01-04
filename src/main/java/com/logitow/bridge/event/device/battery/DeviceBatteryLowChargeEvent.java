package com.logitow.bridge.event.device.battery;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when the device battery reaches critical 5% or less charge.
 */
public class DeviceBatteryLowChargeEvent extends DeviceBatteryEvent {

    /**
     * Constructs a device battery event given device.
     *
     * @param device
     */
    public DeviceBatteryLowChargeEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.warn("Device {} battery is low!", device);
    }
}
