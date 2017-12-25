package com.logitow.bridge.event.device.battery;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.DeviceBattery;
import com.logitow.bridge.event.device.DeviceEvent;

public abstract class DeviceBatteryEvent extends DeviceEvent {
    /**
     * The battery info of the device.
     */
    public DeviceBattery battery;

    /**
     * Constructs a device battery event given device.
     *
     * @param device
     */
    public DeviceBatteryEvent(Device device) {
        super(device);
        this.battery = device.deviceBattery;
    }
}
