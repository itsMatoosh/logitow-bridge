package bridge.event.device;

import bridge.communication.Device;

/**
 * Called when a LOGITOW device is discovered through BLE.
 */
public class DeviceDiscoveredEvent extends DeviceEvent {

    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceDiscoveredEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
