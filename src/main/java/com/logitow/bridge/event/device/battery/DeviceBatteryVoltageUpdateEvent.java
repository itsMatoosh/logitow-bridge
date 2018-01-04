package com.logitow.bridge.event.device.battery;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;

/**
 * Called when a device voltage update is received.
 */
public class DeviceBatteryVoltageUpdateEvent extends DeviceBatteryEvent {
    /**
     * Constructs a device battery event given device.
     *
     * @param device
     */
    public DeviceBatteryVoltageUpdateEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.info("Device {} battery voltage updated: {} \nCurrent charge: {}%", device, battery.voltage, battery.getChargePercent());
    }
}
