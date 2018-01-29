package com.logitow.bridge.build;

import com.google.gson.Gson;
import com.logitow.bridge.build.block.Block;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.block.BlockOperationEvent;

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
     * Unique id of the structure.
     */
    public UUID uuid;

    /**
     * Rotation of the structure.
     */
    public Vec3 rotation;

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
            //Checking duplicates.
            for (Block b :
                    blocks) {
                if (b.id == operation.blockB.id) {
                    onBuildOperation(new BlockOperation(b.attachedTo, b.attachedSide, b, BlockOperationType.BLOCK_REMOVE));
                } else if (b.coordinate == operation.blockB.coordinate) {
                    onBuildOperation(new BlockOperation(b.attachedTo, b.attachedSide, b, BlockOperationType.BLOCK_REMOVE));
                }
            }
            blockAddedHandler(operation);

        } else {
            if(operation.blockB == null) {
                for (Block b :
                        blocks) {
                    if (b.attachedTo != null && b.attachedTo.id == operation.blockA.id && b.attachedSide == operation.blockSide) {
                        System.out.println("Removing block " + b.id);
                        operation.blockB = b;
                        break;
                    }
                }
            }
            if(operation.blockB  == null) return;
            blockRemovedHandler(operation);
        }

        EventManager.callEvent(new BlockOperationEvent(device, operation));
    }

    /**
     * Handles adding of a block.
     * @param operation
     */
    private void blockAddedHandler(BlockOperation operation) {
        //Updating structure info on the block.
        operation.blockB.calculateCoordinates(this, operation.blockA, operation.blockSide);

        //Adding block to structure.
        blocks.add(operation.blockB);
    }

    /**
     * Handles removal of a block.
     * @param operation
     */
    private void blockRemovedHandler(BlockOperation operation) {
        //Removing the block from the structure.
        blocks.remove(operation.blockB);

        //Deleting remains on the same coords.
        for (Block b :
                blocks) {
            if (b.coordinate == operation.blockB.coordinate) {
                onBuildOperation(new BlockOperation(operation.blockB, b.attachedSide, null, BlockOperationType.BLOCK_REMOVE));
            }
        }

        //Recursively removing blocks.
        for (Block b : blocks) {
            if(b.attachedTo != null && b.attachedTo.id == operation.blockB.id) {
                onBuildOperation(new BlockOperation(operation.blockB, b.attachedSide, null, BlockOperationType.BLOCK_REMOVE));
            }
        }
    }
}
