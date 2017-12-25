package bridge.event.device;

import bridge.communication.DeviceBattery;
import bridge.communication.Device;

/**
 * Called when a device voltage update is received.
 */
public class DeviceVoltageUpdateEvent extends DeviceEvent{
    /**
     * The current voltage of the device battery.
     */
    public DeviceBattery voltage;

    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceVoltageUpdateEvent(Device device, DeviceBattery deviceBattery) {
        super(device);
        this.voltage = deviceBattery;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        System.out.println("Device " + device.info.uuid + " voltage updated: " + voltage);
    }
}
