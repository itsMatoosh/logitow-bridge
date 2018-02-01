package com.logitow.bridge.build.block;

import com.logitow.bridge.build.Vec3;

/**
 * Block side numbers.
 */
public enum BlockSide {
    BACK(1, new Vec3(0,0,0),Vec3.zero()), //Side connecting to the previous block in 0,0,0 rotation.
    FRONT(2, Vec3.zero(),Vec3.zero()),
    BOTTOM(3, new Vec3(90,180,0),Vec3.zero())/*new Vec3(0,1,1))*/,
    LEFT(4, new Vec3(0, -90, -90),Vec3.zero()),
    TOP(5, new Vec3(-90,0,0),Vec3.zero()),
    RIGHT(6, new Vec3(0, 90, 90),Vec3.zero()),
    UNDEFINED(0, Vec3.zero(),Vec3.zero());


    /**
     * The logitow id of the side.
     */
    public final int sideId;
    /**
     * The flip axis of the side.
     */
    public final Vec3 flipAxis;
    /**
     * The amount of rotation adding a block to this side inflicts.
     */
    public final Vec3 addedRotationOffset;


    BlockSide(int sideId, Vec3 addedRotationOffset, Vec3 flipAxis) {
        this.flipAxis = flipAxis;
        this.sideId = sideId;
        this.addedRotationOffset = addedRotationOffset;
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
