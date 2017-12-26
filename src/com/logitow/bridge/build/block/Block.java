package com.logitow.bridge.build.block;

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
     * Gets the type of the block.
     */
    public BlockType getBlockType() {
        if(id == 0) {
            return BlockType.BASE;
        } else if (id >= 1038576 && id <= 2097151) {
            return BlockType.WHITE;
        } else if (id >= 2097152 && id <= 3145727) {
            return BlockType.BLACK;
        } else if (id >= 3145728 && id <= 4194303) {
            return BlockType.RED;
        } else if(id >= 4194304 && id <= 5242879) {
            return BlockType.ORANGE;
        } else if (id >= 5242880 && id <= 6291455) {
            return BlockType.YELLOW;
        } else if (id >= 6291456 && id <= 7340031) {
            return BlockType.GREEN;
        } else if (id >= 7340032 && id <= 8388607) {
            return BlockType.INDIGO;
        } else if (id >= 8388608 && id <= 9437183) {
            return BlockType.BLUE;
        } else if(id >= 9437184 && id <= 10485759) {
            return BlockType.PURPLE;
        } else if (id >= 10485760 && id <= 11534335) {
            return BlockType.PINK;
        } else if (id == 16777215) {
            return BlockType.END;
        }
        return BlockType.WHITE;
    }
}