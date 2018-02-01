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
     * Block side arrangement with block's current rotation.
     */
    public BlockSide[][] blockRelativeSides;

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

        System.out.println("Calculating sides of the attached block. attach block side: " + this.attachedSide);
        if(this.attachedTo.blockRelativeSides == null) {
            System.out.println("Parent doesnt have blockRelativeSides, copying the reference...");
            this.attachedTo.blockRelativeSides = BlockSide.copyBlocksideArray(blockSidesReference);
            this.blockRelativeSides = BlockSide.copyBlocksideArray(blockSidesReference);
            this.attachedTo.relativeAttachedSide = BlockSide.FRONT;
            this.relativeAttachedSide = attachedSide;
        } else {
            //Getting the relative attach side.
            System.out.println("Getting the structure relative version of: " + this.attachedSide + " from the relative sides of the attachedTo block.");
            this.relativeAttachedSide = BlockSide.UNDEFINED;
            for (int i = 0; i < this.attachedTo.blockRelativeSides.length; i++) {
                for (int j = 0; j < this.attachedTo.blockRelativeSides[i].length; j++) {
                    System.out.println("i: " + i + " j: " + j + ", Parent relative sides: " + this.attachedTo.blockRelativeSides[i][j] + ", Reference sides: " + blockSidesReference[i][j]);
                    if(this.attachedTo.blockRelativeSides[i][j] == this.attachedSide) {
                        System.out.println("I: " + i + " J: " + j + ",sides:\n a: " + this.attachedSide + "\n p: " + this.attachedTo.blockRelativeSides[i][j] + "\n ref: " + blockSidesReference[i][j]);
                        this.relativeAttachedSide = blockSidesReference[i][j];
                        break;
                    }
                }
            }
        }

        if(this.attachedSide != BlockSide.FRONT) {
            //Normal transformation.
            System.out.println("Block attached not to front, adding offset to parents side configuration...");
            calculateSides(this.attachedSide.addedRotationOffset, this.attachedSide.flipAxis, this.attachedTo.blockRelativeSides);

            if(this.attachedTo.attachedSide == BlockSide.RIGHT || this.attachedTo.attachedSide == BlockSide.LEFT) {
                calculateSides(new Vec3(0,90,0), Vec3.zero(), this.blockRelativeSides);
            } else if(this.attachedTo.attachedSide == BlockSide.BOTTOM || this.attachedTo.attachedSide == BlockSide.TOP) {
                calculateSides(new Vec3(0,0,0), Vec3.zero(), this.blockRelativeSides);
            } else if (this.attachedTo.attachedSide == BlockSide.BACK) {

            }
            /*if(this.attachedTo.attachedSide == BlockSide.BOTTOM) {
                calculateSides(new Vec3(0,-180,0), Vec3.zero(), this.blockRelativeSides);
            }*/
            /*if(this.attachedSide == BlockSide.BOTTOM) {
                if(this.attachedTo != null && this.attachedTo.attachedTo != null) {
                    if(this.attachedTo.attachedSide == BlockSide.RIGHT) {
                        calculateSides(new Vec3(0,-90,90),new Vec3(1,0,1), this.blockRelativeSides); //r
                    } else if(this.attachedTo.attachedSide == BlockSide.LEFT) {
                        calculateSides(new Vec3(0,-90,90),new Vec3(0,0,0), this.blockRelativeSides); //l
                    }
                }
            } else if(this.attachedSide == BlockSide.TOP) {
                if(this.attachedTo != null && this.attachedTo.attachedTo != null) {
                    if(this.attachedTo.attachedSide == BlockSide.RIGHT) {
                        calculateSides(new Vec3(0,90,90),new Vec3(0,1,1), this.blockRelativeSides); //r
                    } else if(this.attachedTo.attachedSide == BlockSide.LEFT) {
                        calculateSides(new Vec3(0,90,-90),new Vec3(0,0,0), this.blockRelativeSides); //l
                    }
                }
            }
            else if(this.attachedSide == BlockSide.LEFT) {
                if(this.attachedTo != null && this.attachedTo.attachedTo != null) {
                    if(this.attachedTo.attachedSide == BlockSide.RIGHT) {
                        calculateSides(new Vec3(0,180,180),new Vec3(0,0,0), this.blockRelativeSides); //r
                    } else if(this.attachedTo.attachedSide == BlockSide.LEFT) {
                        calculateSides(new Vec3(0,0,180),new Vec3(0,0,0), this.blockRelativeSides); //l
                    }
                }
            }*/
            /*else if(this.attachedSide == BlockSide.RIGHT) {
                if(this.attachedTo != null && this.attachedTo.attachedTo != null) {
                    if(this.attachedTo.attachedSide == BlockSide.RIGHT) {
                        calculateSides(new Vec3(0,90,90),new Vec3(0,1,1), this.blockRelativeSides); //r
                    } else if(this.attachedTo.attachedSide == BlockSide.LEFT) {
                        calculateSides(new Vec3(0,90,-90),new Vec3(0,0,0), this.blockRelativeSides); //l
                    }
                }
            }*/
            //Getting the structure relative version of the attachedSide.
        } else {
            //Attached to front, preserving side rotation.
            System.out.println("Block attached to the front side, preserving the side structure...");
            this.blockRelativeSides = BlockSide.copyBlocksideArray(this.attachedTo.blockRelativeSides);
        }


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

    /**
     * Calculates relative sides of this block based on its parent block.
     * @param rotation used to get the rotation offset of the side.
     * @param flipAxis
     * @param attachedToRotations modified based on the rotation offset.
     */
    public void calculateSides(Vec3 rotation, Vec3 flipAxis, BlockSide[][] attachedToRotations) {
        //Getting a copy of the sides references to manipulate.
        this.blockRelativeSides = BlockSide.copyBlocksideArray(attachedToRotations);

        Vec3 rotationOffset = new Vec3(rotation.x, rotation.y, rotation.z);

        //flipping axis.
        if(flipAxis.x > 0) {
            BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);
            blockRelativeSides[1][0] = original[1][2];
            blockRelativeSides[1][2] = original[1][0];
        }
        if(flipAxis.y > 0) {
            BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);
            blockRelativeSides[0][1] = original[2][1];
            blockRelativeSides[2][1] = original[0][1];
        }
        if(flipAxis.z > 0) {
            BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);
            blockRelativeSides[1][1] = original[1][3];
            blockRelativeSides[1][3] = original[1][1];
        }

        //rotating on each axis.
        if(rotationOffset.z != 0) { //z-axis
            int rSteps = rotationOffset.z/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    blockRelativeSides[0][1] = original[1][0];
                    blockRelativeSides[1][0] = original [2][1];
                    blockRelativeSides[2][1] = original [1][2];
                    blockRelativeSides[1][2] = original [0][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    blockRelativeSides[0][1] = original[1][2];
                    blockRelativeSides[1][2] = original [2][1];
                    blockRelativeSides[2][1] = original [1][0];
                    blockRelativeSides[1][0] = original [0][1];
                }
            }
        }
        if(rotationOffset.x != 0) { //x-axis
            int rSteps = rotationOffset.x/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    blockRelativeSides[1][1] = original[0][1];
                    blockRelativeSides[0][1] = original [1][3];
                    blockRelativeSides[1][3] = original [2][1];
                    blockRelativeSides[2][1] = original [1][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    blockRelativeSides[1][1] = original[2][1];
                    blockRelativeSides[2][1] = original [1][3];
                    blockRelativeSides[1][3] = original [0][1];
                    blockRelativeSides[0][1] = original [1][1];
                }
            }
        }
        if(rotationOffset.y != 0) { //y-axis
            int rSteps = rotationOffset.y/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = BlockSide.copyBlocksideArray(blockRelativeSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    blockRelativeSides[1][0] = original[1][3];
                    blockRelativeSides[1][1] = original [1][0];
                    blockRelativeSides[1][2] = original [1][1];
                    blockRelativeSides[1][3] = original [1][2];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    blockRelativeSides[1][0] = original[1][1];
                    blockRelativeSides[1][1] = original [1][2];
                    blockRelativeSides[1][2] = original [1][3];
                    blockRelativeSides[1][3] = original [1][0];
                }
            }
        }

        System.out.println("Calculated relative sides of the block: " + this.id + ":");
        for (int i = 0; i < blockRelativeSides.length; i++) {
            for (BlockSide side :
                    blockRelativeSides[i]) {
                System.out.println(" " + side);
            }
            System.out.println("------");
        }
    }

    @Override
    public String toString() {
        return this.id + "";
    }
}