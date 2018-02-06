package com.logitow.bridge.build;

import com.google.gson.Gson;
import com.logitow.bridge.build.block.Block;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockSide;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.block.BlockOperationErrorEvent;
import com.logitow.bridge.event.device.block.BlockOperationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a single Logitow structure.
 */
public class Structure {
    /**
     * The logger of the class.
     */
    public static Logger logger = LogManager.getLogger(Structure.class);

    /**
     * Unique id of the structure.
     */
    public UUID uuid;

    /**
     * The blocks within this structure.
     */
    public ArrayList<Block> blocks = new ArrayList<>();

    /**
     * The device of the structure.
     */
    public Device device;

    /**
     * Constructs a new structure.
     */
    public Structure() {
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
    }

    /**
     * Constructs a new structure given device.
     * @param device
     */
    public Structure(Device device) {
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
        this.device = device;
    }

    /**
     * Saves a structure to file inside the structure dir of the lib.
     */
    public static void saveToFile(Structure structure) {
        try {
            saveToFile(structure, Paths.get(getStructureSaveDir().getPath(), structure.uuid.toString()).toString());
        } catch (IOException e) { //Lib has access to the dir, so this shouldn't be called.
            e.printStackTrace();
        }
    }

    /**
     * Saves a structure to file.
     */
    public static void saveToFile(Structure structure, String path) throws IOException {
        System.out.println("Saving structure: " + structure + " to: " + path);

        //Serializing
        Gson serializer = new Gson();
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.print(serializer.toJson(structure));
        }
    }

    /**
     * Loads a structure from the logitow folder given its uuid.
     * @param uuid
     * @return
     */
    public static Structure loadByUuid(String uuid) {
        for (File file :
                getStructureSaveDir().listFiles()) {
            if(file.getName().contains(uuid)) {
                try {
                    return loadFromFile(file.getPath());
                } catch (IOException e) { //Lib has access to the dir, so this shouldn't be called.
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * Loads structure data from file.
     */
    public static Structure loadFromFile(String path) throws IOException {
        System.out.println("Loading structure from: " + path);

        //Getting the file.
        File file = new File(path);
        if(!file.exists()) {
            throw new FileNotFoundException();
        }

        //Deserializing.
        FileReader fileReader = new FileReader(path);
        Gson deserializer = new Gson();
        return deserializer.fromJson(fileReader, Structure.class);
    }
    /**
     * Gets the save dir of the structure files.
     * @return
     */
    public static File getStructureSaveDir() {
        try {
            File directory = new File(new File(Structure.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath() + "/structures/");
            if(!directory.exists()) {
                directory.mkdir();
            }
            return directory;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Called when a build operation is received from a connected logitow device.
     * @param operation
     */
    public void onBuildOperation(BlockOperation operation) {
        if(operation.operationType == BlockOperationType.BLOCK_ADD) {
            blockAddedHandler(operation);
        } else {
            if(operation.blockB == null) {
                for (int i = 0; i < operation.blockA.children.length; i++) {
                    if (operation.blockSide.sideId-1 == i) {
                        operation.blockB = operation.blockA.children[i];
                        break;
                    }
                }
            }
            if(operation.blockB  == null) {
                logger.warn("Block removal failed! Block B not found!");
                EventManager.callEvent(new BlockOperationErrorEvent(device, this));
                return;
            }
            blockRemovedHandler(operation);
        }

        EventManager.callEvent(new BlockOperationEvent(device, operation));
    }

    /**
     * Handles adding of a block.
     * @param operation
     */
    private void blockAddedHandler(BlockOperation operation) {
        logger.info("Handling block: {} addition to structure: {}", operation.blockB.id, this.uuid);

        //Updating structure info on the block.
        operation.blockB.calculateCoordinates(this, operation.blockA, operation.blockSide);

        //Removing duplicates.
        removeDuplicates(operation.blockB);

        //Adding block to structure.
        blocks.add(operation.blockB);
    }

    /**
     * Handles removal of a block.
     * @param operation
     */
    private void blockRemovedHandler(BlockOperation operation) {
        logger.info("Handling block: {} removal from the structure: {}", operation.blockB.id, this.uuid);

        //Removing the block from the structure.
        blocks.remove(operation.blockB);

        //Deleting remains on the same coords.
        removeDuplicates(operation.blockB);

        //Removing reference from parent.
        operation.blockA.children[operation.blockB.parentAttachSide.sideId-1] = null;
        operation.blockB.parent = null;

        //Recursively removing children.
        for (Block child : operation.blockB.children) {
            if(child!=null) {
                removeBlock(child);
            }
        }
    }

    /**
     * Removes the specified block from the structure.
     * @param b
     */
    public void removeBlock(Block b) {
        onBuildOperation(new BlockOperation(b.parent, b.parentAttachSide, b, BlockOperationType.BLOCK_REMOVE));
    }

    /**
     * Gets a block in the structure by position.
     * @param position
     */
    public Block getBlockByPosition(Vec3 position) {
        for (Block b :
                blocks) {
            if (b.coordinate == position) {
                return b;
            }
        }

        return null;
    }

    /**
     * Gets a block in the structure by id.
     * @param id
     * @return
     */
    public Block getBlockById(int id) {
        for (Block b :
                blocks) {
            if (b.id == id) {
                return b;
            }
        }
        return null;
    }

    /**
     * Removes any duplicates of the specified block within the structure.
     */
    public void removeDuplicates(Block block) {
        Block duplicate = getBlockByPosition(block.coordinate);
        if(duplicate != null) {
            logger.info("Removing duplicate of block: {}", block.id);
            removeBlock(duplicate);
        }
        duplicate = getBlockById(block.id);
        if(duplicate != null) {
            logger.info("Removing duplicate of block: {}", block.id);
            removeBlock(duplicate);
        }
    }

    /**
     * Rotates the structure to a certain direction.
     * Discards all the blocks.
     * @param direction
     */
    public void rotate(BlockSide direction) {
        //Removing all the blocks.
        //TODO
    }
}
