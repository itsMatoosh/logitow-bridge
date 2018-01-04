package com.logitow.bridge.build;

import com.google.gson.Gson;
import com.logitow.bridge.build.block.Block;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockSide;
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
            operation.blockB = operation.blockA.attachedSides[operation.blockSide.getValue()];
            blockRemovedHandler(operation);
        }

        EventManager.callEvent(new BlockOperationEvent(device, operation));
    }

    /**
     * Handles adding of a block.
     * @param operation
     */
    private void blockAddedHandler(BlockOperation operation) {
        //Adding block to structure.
        blocks.add(operation.blockB);

        //Adding reference to the previous block.
        operation.blockA.attachedSides[operation.blockSide.getValue()] = operation.blockB;

        //Updating structure info on the block.
        operation.blockB.onAttached(this, operation.blockA, operation.blockSide);
    }

    /**
     * Handles removal of a block.
     * @param operation
     */
    private void blockRemovedHandler(BlockOperation operation) {
        //Removing the block from the structure.
        blocks.remove(operation.blockB);

        //Removing block a reference to the removed block.
        for (int i = 0; i < operation.blockA.attachedSides.length; i++) {
            if(operation.blockA.attachedSides[i] == operation.blockB) {
                operation.blockA.attachedSides[i] = null;
            }
        }

        //Recursively removing blocks.
        for (int i = 0; i < operation.blockB.attachedSides.length; i++) {
            if(operation.blockA.attachedSides[i] != null) {
                onBuildOperation(new BlockOperation(operation.blockB, BlockSide.valueOf(i), null, BlockOperationType.BLOCK_REMOVE));
            }
        }
    }
}
