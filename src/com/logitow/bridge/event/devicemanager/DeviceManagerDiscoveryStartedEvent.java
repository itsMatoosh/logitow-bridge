package bridge.event.devicemanager;

import bridge.communication.LogitowDeviceManager;

public class DeviceManagerDiscoveryStartedEvent extends DeviceManagerEvent {
    public DeviceManagerDiscoveryStartedEvent(LogitowDeviceManager manager) {
        super(manager);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        System.out.println("Device discovery started!");
    }
}
