package com.logitow.bridge.build.block;

import com.logitow.bridge.build.Vec3;

/**
 * Block side numbers.
 */
public enum BlockSide {
    BACK(1, new Vec3(0,180,0)), //Side connecting to the previous block in 0,0,0 rotation.
    FRONT(2, Vec3.zero()),
    BOTTOM(3, new Vec3(90,180,0)),
    LEFT(4, new Vec3(-90, 0, -90)),
    TOP(5, new Vec3(-90,0,0)),
    RIGHT(6, new Vec3(-90, 0, 90)),
    UNDEFINED(0, Vec3.zero());


    /**
     * The logitow id of the side.
     */
    public final int sideId;
    /**
     * The amount of rotation adding a block to this side inflicts.
     */
    public final Vec3 addedRotationOffset;

    /**
     * Block side arrangement with rotation = 0.
     */
    static BlockSide[][] blockSidesReference = {
            {UNDEFINED, TOP   , UNDEFINED, UNDEFINED},
            {LEFT     , FRONT , RIGHT    , BACK},
            {UNDEFINED, BOTTOM, UNDEFINED, UNDEFINED}
    };

    BlockSide(int sideId, Vec3 addedRotationOffset) {
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
     * Gets the blockside relative to the structure by subtracting the offset.
     * @param a
     * @param rotationOffset
     * @return
     */
    public static BlockSide addRotationOffset(BlockSide a, Vec3 rotationOffset) {
        //Getting a copy of the sides references to manipulate.
        BlockSide[][] rotatedSides = copyBlocksideArray(blockSidesReference);

        //rotating on each axis.
        if(rotationOffset.x != 0) { //x-axis
            int rSteps = rotationOffset.x/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[1][1] = original[0][1];
                    rotatedSides[0][1] = original [1][3];
                    rotatedSides[1][3] = original [2][1];
                    rotatedSides[2][1] = original [1][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[1][1] = original[2][1];
                    rotatedSides[0][1] = original [1][1];
                    rotatedSides[1][3] = original [0][1];
                    rotatedSides[2][1] = original [1][3];
                }
            }
        }
        if(rotationOffset.y != 0) { //y-axis
            int rSteps = rotationOffset.y/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[1][0] = original[1][3];
                    rotatedSides[1][1] = original [1][0];
                    rotatedSides[1][2] = original [1][1];
                    rotatedSides[1][3] = original [1][2];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[1][0] = original[1][1];
                    rotatedSides[1][1] = original [1][2];
                    rotatedSides[1][2] = original [1][3];
                    rotatedSides[1][3] = original [1][0];
                }
            }
        }
        if(rotationOffset.z != 0) { //z-axis
            int rSteps = rotationOffset.z/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[0][1] = original[1][0];
                    rotatedSides[1][0] = original [2][1];
                    rotatedSides[2][1] = original [1][2];
                    rotatedSides[1][2] = original [0][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[0][1] = original[1][2];
                    rotatedSides[1][2] = original [2][1];
                    rotatedSides[2][1] = original [1][0];
                    rotatedSides[1][0] = original [0][1];
                }
            }
        }


        //Comparing the result with reference.
        for (int i = 0; i < blockSidesReference.length; i++) {
            for (int j = 0; j < blockSidesReference[i].length; j++) {
                System.out.println("Rotated sides: i: " + i + " j: " + j + " : " + rotatedSides[i][j] + " reference: " + blockSidesReference[i][j]);
                if(blockSidesReference[i][j] == a) {
                    return rotatedSides[i][j];
                }
            }
        }

        return UNDEFINED;
    }

    /**
     * Gets the blockside relative to a specific block by adding the block's local rotation.
     * @param a
     * @param rotationOffset
     * @return
     */
    public static BlockSide subtractRotationOffset(BlockSide a, Vec3 rotationOffset) {
        rotationOffset = new Vec3(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

        //Getting a copy of the sides references to manipulate.
        BlockSide[][] rotatedSides = copyBlocksideArray(blockSidesReference);

        //rotating on each axis.
        if(rotationOffset.z != 0) { //z-axis
            int rSteps = rotationOffset.z/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[0][1] = original[1][0];
                    rotatedSides[1][0] = original [2][1];
                    rotatedSides[2][1] = original [1][2];
                    rotatedSides[1][2] = original [0][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[0][1] = original[1][2];
                    rotatedSides[1][2] = original [2][1];
                    rotatedSides[2][1] = original [1][0];
                    rotatedSides[1][0] = original [0][1];
                }
            }
        }
        if(rotationOffset.y != 0) { //y-axis
            int rSteps = rotationOffset.y/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[1][0] = original[1][3];
                    rotatedSides[1][1] = original [1][0];
                    rotatedSides[1][2] = original [1][1];
                    rotatedSides[1][3] = original [1][2];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[1][0] = original[1][1];
                    rotatedSides[1][1] = original [1][2];
                    rotatedSides[1][2] = original [1][3];
                    rotatedSides[1][3] = original [1][0];
                }
            }
        }
        if(rotationOffset.x != 0) { //x-axis
            int rSteps = rotationOffset.x/90;

            for (int i = 0; i < Math.abs(rSteps); i++) {
                BlockSide[][] original = copyBlocksideArray(rotatedSides);

                if(rSteps > 0) {
                    //Positive transformation.
                    rotatedSides[1][1] = original[0][1];
                    rotatedSides[0][1] = original [1][3];
                    rotatedSides[1][3] = original [2][1];
                    rotatedSides[2][1] = original [1][1];
                } else if(rSteps < 0) {
                    //Negative transformation.
                    rotatedSides[1][1] = original[2][1];
                    rotatedSides[0][1] = original [1][1];
                    rotatedSides[1][3] = original [0][1];
                    rotatedSides[2][1] = original [1][3];
                }
            }
        }

        //Comparing the result with reference.
        for (int i = 0; i < blockSidesReference.length; i++) {
            for (int j = 0; j < blockSidesReference[i].length; j++) {
                System.out.println("Rotated sides: i: " + i + " j: " + j + " : " + rotatedSides[i][j] + " reference: " + blockSidesReference[i][j]);
                if(blockSidesReference[i][j] == a) {
                    return rotatedSides[i][j];
                }
            }
        }

        return UNDEFINED;
    }

    /**
     * Copies a blockside array.
     * @param original
     * @return
     */
    private static BlockSide[][] copyBlocksideArray(BlockSide[][] original) {
        BlockSide[][] copy = new BlockSide[3][4];
        for(int i=0; i<original.length; i++)
        {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }

        return copy;
    }
}
