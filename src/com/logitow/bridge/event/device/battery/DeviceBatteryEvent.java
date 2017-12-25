package bridge.event.device.battery;

import bridge.communication.Device;
import bridge.communication.DeviceBattery;
import bridge.event.device.DeviceEvent;

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
