package com.logitow.bridge.build.block;

import com.logitow.bridge.build.Coordinate;
import com.logitow.bridge.build.Structure;

/**
 * Represents a LOGITOW block.
 */
public class Block {
    /**
     * The id of the block.
     */
    public int id;

    /**
     * Local coordinates within the current structure.
     */
    public Coordinate coordinate;

    /**
     * The structure the block is a part of.
     */
    public Structure structure;

    /**
     * The blocks attached to each side of this block.
     */
    public Block[] attachedSides = new Block[6];

    /**
     * Creates a block instance given block id.
     * @param id
     */
    public Block(int id) {
        this.id = id;
        this.coordinate = new Coordinate();
    }

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

    /**
     * Assigns the structure information to the block.
     * @param structure the structure this block is a part of.
     * @param attachedTo the block to which this block is attached.
     * @param attachedSide the side to which the block is attached.
     */
    public void onAttached(Structure structure, Block attachedTo, BlockSide attachedSide) {
        this.attachedSides[attachedSide.getValue()] = attachedTo;
        this.structure = structure;

        //Getting the coords.
        this.coordinate = attachedTo.coordinate;
        switch(attachedSide) {
            case TOP:
                this.coordinate.y++;
                break;
            case BOTTOM:
                this.coordinate.y--;
                break;
            case FRONT:
                this.coordinate.x++;
                break;
            case BACK:
                this.coordinate.x--;
                break;
            case LEFT:
                this.coordinate.z++;
                break;
            case RIGHT:
                this.coordinate.z--;
                break;
        }
    }
}