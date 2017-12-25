package bridge.event.block;

import bridge.communication.Device;
import bridge.build.block.Block;
import bridge.build.block.BlockOperation;
import bridge.event.Event;

/**
 * Event concerning a block update.
 */
public class BlockEvent extends Event {
    /**
     * The device which sent in the event.
     */
    public Device device;
    /**
     * The block to which the block b is attach or removed from.
     */
    public Block blockA;
    /**
     * The block b.
     */
    public Block blockB;
    /**
     * The operation type on the blocks.
     */
    public BlockOperation operation;


    /**
     * Default constructor.
     * @param device
     * @param blockA
     * @param blockB
     * @param operation
     */
    public BlockEvent(Device device, Block blockA, Block blockB, BlockOperation operation) {
        this.device = device;
        this.blockA = blockA;
        this.blockB = blockB;
        this.operation = operation;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
