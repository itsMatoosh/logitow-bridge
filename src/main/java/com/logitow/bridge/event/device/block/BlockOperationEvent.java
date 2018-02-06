package com.logitow.bridge.event.device.block;

import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.event.device.DeviceEvent;

/**
 * Called when a block operation is received from the logitow device.
 */
public class BlockOperationEvent extends DeviceEvent {

    /**
     * The operation.
     */
    public BlockOperation operation;

    /**
     * Constructs a device event given device.
     *
     * @param device
     */
    public BlockOperationEvent(Device device, BlockOperation operation) {
        super(device);
        this.operation = operation;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        LogitowDeviceManager.current.logger.info("{} update received, Block A: {}, Block B: {}, insert face: {}, device: {}", operation.operationType, operation.blockA, operation.blockB, operation.blockB.relativeAttachDir, device);
    }
}
