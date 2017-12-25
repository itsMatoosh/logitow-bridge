package com.logitow.bridge.build.block;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a LOGITOW block.
 */
public class Block {
    /**
     * The id of the block.
     */
    public int id;

    /**
     * The blocks attached to each side of this block.
     */
    public Block[] attachedSides = new Block[6];

    /**
     * Gets the color of the block.
     */
    public BlockColor getBlockColor() {
        throw new NotImplementedException();
    }
}
