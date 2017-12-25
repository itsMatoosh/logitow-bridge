package bridge.event.devicemanager;

import bridge.communication.LogitowDeviceManager;
import bridge.event.Event;

/**
 * An event concerning a device manager.
 */
public abstract class DeviceManagerEvent extends Event {
    /**
     * The manager.
     */
    public LogitowDeviceManager manager;

    public DeviceManagerEvent(LogitowDeviceManager manager) {
        this.manager = manager;
    }
}
