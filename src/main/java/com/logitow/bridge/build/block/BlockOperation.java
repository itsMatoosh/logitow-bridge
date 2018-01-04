package com.logitow.bridge.build.block;

/**
 * Defines a block operation.
 */
public class BlockOperation {
    /**
     * The type of the operation.
     */
    public BlockOperationType operationType;

    /**
     * Block a.
     */
    public Block blockA;

    /**
     * The side of block a, block b was attached to or detached from.
     */
    public BlockSide blockSide;

    /**
     * Block b.
     */
    public Block blockB;

    /**
     * Creates a block operation instance.
     * @param blockA
     * @param side
     * @param blockB
     * @param operationType
     */
    public BlockOperation(Block blockA, BlockSide side, Block blockB, BlockOperationType operationType) {
        this.blockA = blockA;
        this.blockB = blockB;
        this.blockSide = side;
        this.operationType = operationType;
    }
}
