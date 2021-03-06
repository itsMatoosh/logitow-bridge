package com.logitow.bridge.build.block;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * Represents a LOGITOW block.
 */
public class Block implements Serializable{
    /**
     * Class logger.
     */
    public static Logger logger = LogManager.getLogger(Block.class);

    /**
     * The id of the block.
     */
    public int id;

    /**
     * Local coordinates within the current structure.
     */
    public Vec3 coordinate;

    /**
     * Coordinate without the structure rotation.
     */
    public Vec3 localCoords;

    /**
     * Side that the block has been attached to relative to the structure.
     */
    public BlockSide relativeAttachDir;

    /**
     * Side of the parent block that this block has been attached to.
     */
    public BlockSide parentAttachSide;

    /**
     * The structure the block is a part of.
     */
    public transient Structure structure;

    /**
     * The id of the block that this block is attached to.
     */
    public int parent;
    /**
     * The ids of blocks attached to this block.
     */
    public int[] children;
    /**
     * The side mappings for this block.
     */
    public BlockSide[] sides = {
            BlockSide.BACK,
            BlockSide.FRONT,
            BlockSide.BOTTOM,
            BlockSide.LEFT,
            BlockSide.TOP,
            BlockSide.RIGHT
    };

    /**
     * First block side mappings.
     */
    final static int[][] firstChildFaces = {
            {0,0,0,0,0,0},
            {1,2,3,4,5,6},
            {3,5,2,4,1,6},
            {3,5,6,2,4,1},
            {3,5,1,6,2,4},
            {3,5,4,1,6,2}
    };
    /**
     * Children blocks side mappings.
     */
    final static int[][] childSameFaces = {
            {0,0,0,0,0,0},
            {1,2,3,4,5,6},
            {5,3,2,6,1,4},
            {5,3,4,2,6,1},
            {5,3,1,4,2,6},
            {5,3,6,1,4,2}
    };
    /**
     * Directions relative to structure mappings.
     */
    final static BlockSide[] sideDirectionMapping = {
            BlockSide.BOTTOM,
            BlockSide.TOP,
            BlockSide.BACK,
            BlockSide.RIGHT,
            BlockSide.FRONT,
            BlockSide.LEFT
    };

    /**
     * Creates a block instance given block id.
     * @param id
     */
    public Block(int id) {
        this.id = id;
        this.coordinate = Vec3.zero();
        this.localCoords = Vec3.zero();
        this.parent = -10;
        this.children = new int[]{-10,-10,-10,-10,-10,-10,-10};
    }

    /**
     * Gets the type of the block.
     */
    public BlockType getBlockType() {
        return getBlockType(this.id);
    }
    /**
     * Gets the type of the block given its id.
     */
    public static BlockType getBlockType(int id) {
        if(id == 0 || id == -1) {
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
     * Calculates the coordinates of the block.
     * Called when the block is attached to another block.
     * @param structure the structure this block is a part of.
     * @param attachedTo the block to which this block is attached.
     * @param attachedSide the side to which the block is attached.
     */
    public void calculateCoordinates(Structure structure, Block attachedTo, BlockSide attachedSide) {
        //Setting variables.
        this.parent = attachedTo.id;
        this.structure = structure;

        //Getting the relative attach direction.
        parentAttachSide = attachedTo.getRelativeDirection(attachedSide);

        //Checking whether a block has already been attached to the same direction.
        if(attachedTo.children[parentAttachSide.sideId-1] != -10) {
            logger.warn("Overriding block: {}", this);
            structure.removeBlock(structure.getBlockById(attachedTo.children[parentAttachSide.sideId-1]));
        }

        //Setting the attached block as child of the parent.
        attachedTo.children[parentAttachSide.sideId-1] = this.id;

        //Getting the coords.
        this.relativeAttachDir = sideDirectionMapping[parentAttachSide.sideId-1];
        switch(this.relativeAttachDir) {
            case TOP:
                this.coordinate = new Vec3(attachedTo.localCoords.getX(),attachedTo.localCoords.getY() + 1,attachedTo.localCoords.getZ());
                break;
            case BOTTOM:
                this.coordinate = new Vec3(attachedTo.localCoords.getX(),attachedTo.localCoords.getY() - 1,attachedTo.localCoords.getZ());
                break;
            case FRONT:
                this.coordinate = new Vec3(attachedTo.localCoords.getX(),attachedTo.localCoords.getY(),attachedTo.localCoords.getZ() + 1);
                break;
            case BACK:
                this.coordinate = new Vec3(attachedTo.localCoords.getX(),attachedTo.localCoords.getY(),attachedTo.localCoords.getZ() - 1);
                break;
            case LEFT:
                this.coordinate = new Vec3(attachedTo.localCoords.getX()+1,attachedTo.localCoords.getY(),attachedTo.localCoords.getZ());
                break;
            case RIGHT:
                this.coordinate = new Vec3(attachedTo.localCoords.getX()-1,attachedTo.localCoords.getY(),attachedTo.localCoords.getZ());
                break;
            default:
                System.out.println("UNDEFINED side!");
                break;
        }

        //Assigning the child face ids, based on the attachment face.
        for (int parentFaceID = 0; parentFaceID < 6; parentFaceID++) {
            for (int dirID = 0; dirID < 6; dirID++) {
                if (parentFaceID + 1 == attachedTo.sides[dirID].sideId) {
                    if (attachedTo.id == 0){
                        this.sides[dirID] = BlockSide.getBlockSide(firstChildFaces[attachedSide.sideId - 1][parentFaceID]);
                    } else {
                        this.sides[dirID] = BlockSide.getBlockSide(childSameFaces[attachedSide.sideId - 1][parentFaceID]);
                    }
                    break;
                }
            }
        }

        this.localCoords = this.coordinate.clone();
    }

    /**
     * Gets the attach direction relative to the block based on the attached side.
     * @param attachSide
     * @return
     */
    public BlockSide getRelativeDirection(BlockSide attachSide) {
        for (int i = 0; i < this.sides.length; i++) {
            if(attachSide == this.sides[i]) {
                return BlockSide.getBlockSide(i+1);
            }
        }
        return BlockSide.UNDEFINED;
    }

    @Override
    public String toString() {
        return this.id + "";
    }
}