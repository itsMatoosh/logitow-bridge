package com.logitow.bridge.build.block;

import java.io.Serializable;

/**
 * Block side numbers.
 */
public enum BlockSide implements Serializable {
    BACK(1),
    FRONT(2),
    BOTTOM(3),
    LEFT(4),
    TOP(5),
    RIGHT(6),
    UNDEFINED(0);


    /**
     * The logitow id of the side.
     */
    public final int sideId;


    BlockSide(int sideId) {
        this.sideId = sideId;
    }

    /**
     * Gets a block side based on its id.
     * @param sideId
     * @return
     */
    public static BlockSide getBlockSide(int sideId){
        for (BlockSide side : values()) {
            if (side.sideId == sideId) return side;
        }
        return BlockSide.UNDEFINED;
    }

    /**
     * Copies a blockside array.
     * @param original
     * @return
     */
    public static BlockSide[][] copyBlocksideArray(BlockSide[][] original) {
        BlockSide[][] copy = new BlockSide[3][4];
        for(int i=0; i<original.length; i++)
        {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }

        return copy;
    }

    /**
     * Gets the side opposite to the given side.
     * @param a
     * @return
     */
    public static BlockSide getOpposite(BlockSide a) {
        switch(a) {
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            case FRONT:
                return BACK;
            case BACK:
                return FRONT;
            case UNDEFINED:
                return UNDEFINED;
        }
        return UNDEFINED;
    }
}
