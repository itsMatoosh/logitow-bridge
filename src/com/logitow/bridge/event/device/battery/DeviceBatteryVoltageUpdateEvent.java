package bridge.event.device.battery;

import bridge.communication.Device;

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
        System.out.println("Device " + device.info.uuid + " voltage updated: " + battery.voltage + "\n Current charge %: " + battery.getChargePercent());
    }
}
