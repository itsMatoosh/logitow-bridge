package bridge.event.device;

import bridge.communication.Device;

/**
 * Called when a LOGITOW device is connected.
 */
public class DeviceConnectedEvent extends DeviceEvent {
    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public DeviceConnectedEvent(Device device) {
        super(device);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
