package bridge.event.device.battery;

import bridge.communication.Device;

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
        System.out.println("Device " + device.info.uuid + " battery is low! Please recharge!");
    }
}
