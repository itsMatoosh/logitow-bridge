package com.logitow.bridge.event.device.block;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.device.DeviceEvent;

/**
 * Called when there is an error with a block operation.
 */
public class BlockOperationErrorEvent extends DeviceEvent{

    /**
     * The newest state of the structure.
     */
    public Structure structure;

    /**
     * Constructs a device event given device.
     *
     * @param device
     * @param structure
     */
    public BlockOperationErrorEvent(Device device, Structure structure) {
        super(device);
        this.structure = structure;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.warn("Build operation error occurred on device {}!", device);
    }
}
