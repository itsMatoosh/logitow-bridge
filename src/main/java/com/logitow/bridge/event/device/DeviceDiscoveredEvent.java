package com.logitow.bridge.event.device;

import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;

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
        LogitowDeviceManager.current.logger.info("Logitow device {} discovered!", device);
    }
}
