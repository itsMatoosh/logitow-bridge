package com.logitow.bridge.build;

import com.logitow.bridge.build.block.Block;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockSide;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a single Logitow structure.
 */
public class Structure {
    /**
     * Unique id of the structure.
     */
    public UUID uuid;

    /**
     * The blocks within this structure.
     */
    public ArrayList<Block> blocks = new ArrayList<>();

    /**
     * The device of the structure.
     */
    public Device device;

    /**
     * Constructs a new structure.
     */
    public Structure() {
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
    }
    public Structure(Device device) {
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
        this.device = device;
    }

    /**
     * Saves the structure to file.
     */
    public void saveToFile() {
        throw new NotImplementedException();
    }

    /**
     * Loads structure data from file.
     */
    public void loadFromFile() {
        throw new NotImplementedException();
    }

    /**
     * Called when a build operation is received from a connected logitow device.
     * @param operation
     */
    public void onBuildOperation(BlockOperation operation) {
        if(operation.operationType == BlockOperationType.BLOCK_ADD) {
            blockAddedHandler(operation);
        } else {
            operation.blockB = operation.blockA.attachedSides[operation.blockSide.getValue()];
            blockRemovedHandler(operation);
        }

        EventManager.callEvent(new BlockOperationEvent(device, operation));
    }

    /**
     * Handles adding of a block.
     * @param operation
     */
    private void blockAddedHandler(BlockOperation operation) {
        //Adding block to structure.
        blocks.add(operation.blockB);

        //Adding reference to the previous block.
        operation.blockA.attachedSides[operation.blockSide.getValue()] = operation.blockB;

        //Updating structure info on the block.
        operation.blockB.onAttached(this, operation.blockA, operation.blockSide);
    }

    /**
     * Handles removal of a block.
     * @param operation
     */
    private void blockRemovedHandler(BlockOperation operation) {
        //Removing the block from the structure.
        blocks.remove(operation.blockB);

        //Removing block a reference to the removed block.
        for (int i = 0; i < operation.blockA.attachedSides.length; i++) {
            if(operation.blockA.attachedSides[i] == operation.blockB) {
                operation.blockA.attachedSides[i] = null;
            }
        }

        //Recursively removing blocks.
        for (int i = 0; i < operation.blockB.attachedSides.length; i++) {
            if(operation.blockA.attachedSides[i] != null) {
                onBuildOperation(new BlockOperation(operation.blockB, BlockSide.valueOf(i), null, BlockOperationType.BLOCK_REMOVE));
            }
        }
    }
}
