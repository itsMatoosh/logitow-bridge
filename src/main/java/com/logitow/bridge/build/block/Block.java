package com.logitow.bridge.build.block;

import com.logitow.bridge.build.Structure;
import com.logitow.bridge.build.Vec3;

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
    public Vec3 coordinate;
    /**
     * Rotation of the block relative to the structure.
     */
    public Vec3 rotation;

    /**
     * The structure the block is a part of.
     */
    public Structure structure;

    /**
     * The block that this block is attached to.
     */
    public Block attachedTo;
    /**
     * The side that this block is attached to.
     * Relative to the block.
     */
    public BlockSide attachedSide;

    /**
     * The structure relative side the block is attached to.
     */
    public BlockSide relativeAttachedSide;

    /**
     * Mappings of block sides based on rotations.
     */
    public static BlockSide[][][] sidesMappings = {
            //-1,-1,-1
        {
                {BlockSide.UNDEFINED, BlockSide.LEFT   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                {BlockSide.UNDEFINED, BlockSide.RIGHT, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
        },
            //-1,-1,0
        {
                {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
        },
            //-1,-1,1
        {
                {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
        },
            //-1,0,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //-1,0,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //-1,0,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //-1,1,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //-1,1,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //-1,1,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,-1,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,-1,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,-1,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,0,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,0,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,0,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,1,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,1,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //0,1,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,-1,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,-1,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,-1,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,0,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,0,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,0,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,1,-1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,1,0
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            },
            //1,1,1
            {
                    {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
                    {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
                    {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
            }
    };

    /**
     * Block side arrangement with rotation = 0.
     */
    public static BlockSide[][] blockSidesReference = {
            {BlockSide.UNDEFINED, BlockSide.TOP   , BlockSide.UNDEFINED, BlockSide.UNDEFINED},
            {BlockSide.LEFT     , BlockSide.FRONT , BlockSide.RIGHT    , BlockSide.BACK},
            {BlockSide.UNDEFINED, BlockSide.BOTTOM, BlockSide.UNDEFINED, BlockSide.UNDEFINED}
    };


    /**
     * Creates a block instance given block id.
     * @param id
     */
    public Block(int id) {
        this.id = id;
        this.coordinate = Vec3.zero();
        this.rotation = Vec3.zero();
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
     * Calculates the coordinates of the block.
     * @param structure the structure this block is a part of.
     * @param attachedTo the block to which this block is attached.
     * @param attachedSide the side to which the block is attached.
     */
    public void calculateCoordinates(Structure structure, Block attachedTo, BlockSide attachedSide) {
        //Setting variables.
        this.attachedTo = attachedTo;
        this.attachedSide = attachedSide;
        this.structure = structure;

        //Calculating the block rotation.
        System.out.println("Calculating the rotation of the attached block... \nParent rotation: " + attachedTo.rotation + "\nAttached side: " + attachedSide);
        this.rotation = new Vec3(this.attachedTo.rotation.x, this.attachedTo.rotation.y, this.attachedTo.rotation.z);
        this.rotation = this.rotation.add(attachedSide.addedRotationOffset);
        System.out.println("Rotation of the attached block: " + this.rotation);

        //Resolving the structure relative side.
        this.relativeAttachedSide = this.attachedSide;//TODO: Resolve based on the rotation.

        System.out.println("Structure relative side: " + this.relativeAttachedSide);

        //Getting the coords.
        switch(relativeAttachedSide) {
            case TOP:
                this.coordinate = new Vec3(attachedTo.coordinate.x,attachedTo.coordinate.y + 1,attachedTo.coordinate.z);
                break;
            case BOTTOM:
                this.coordinate = new Vec3(attachedTo.coordinate.x,attachedTo.coordinate.y - 1,attachedTo.coordinate.z);
                break;
            case FRONT:
                this.coordinate = new Vec3(attachedTo.coordinate.x,attachedTo.coordinate.y,attachedTo.coordinate.z + 1);
                break;
            case BACK:
                this.coordinate = new Vec3(attachedTo.coordinate.x,attachedTo.coordinate.y,attachedTo.coordinate.z - 1);

                break;
            case LEFT:
                this.coordinate = new Vec3(attachedTo.coordinate.x+1,attachedTo.coordinate.y,attachedTo.coordinate.z);

                break;
            case RIGHT:
                this.coordinate = new Vec3(attachedTo.coordinate.x-1,attachedTo.coordinate.y,attachedTo.coordinate.z);

                break;
            case UNDEFINED:
                System.out.println("UNDEFINED side!");
                break;
        }
    }

    @Override
    public String toString() {
        return this.id + "";
    }
}