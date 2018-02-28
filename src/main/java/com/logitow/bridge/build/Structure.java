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
     * The current rotation of the structure.
     */
    public Vec3 rotation = Vec3.zero();

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
                logger.info("Finding Block B...");
                BlockSide relativeSide = operation.blockA.getRelativeDirection(operation.blockSide);
                for (int i = 0; i < operation.blockA.children.length; i++) {
                    logger.info(" [{}] Found child: {}", i, operation.blockA.children[i]);
                    if (relativeSide.sideId-1 == i) {
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

        //Rotating the added block.
        rotateBlockRelative(operation.blockB, this.rotation);

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
     * Rotates the structure by specified angles.
     * @param angles
     */
    public boolean rotate(Vec3 angles) {
        logger.info("Rotating structure: {} by: {}", uuid, angles);

        //Checking the angles.
        if(angles.x % 90 != 0 || angles.y % 90 != 0 || angles.z % 90 != 0) {
            return false;
        }

        //Rotating every block.
        rotateZ(angles.z);
        rotateY(angles.y);
        rotateX(angles.x);

        //Adding the rotation.
        this.rotation = this.rotation.add(angles);

        return true;
    }

    /**
     *  Rotates a block by the specified angle.
     * @param angles
     * @return
     */
    private boolean rotateBlockRelative(Block b, Vec3 angles) {
        //Checking the angles.
        if(angles.x % 90 != 0 || angles.y % 90 != 0 || angles.z % 90 != 0) {
            return false;
        }

        //Rotating on every axis.
        rotateBlockRelativeZ(b,angles.z);
        rotateBlockRelativeY(b,angles.y);
        rotateBlockRelativeX(b,angles.x);

        return true;
    }

    /**
     * Rotates the structure along x axis.
     */
    private void rotateX(int theta) {
        if(theta == 0) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        for (Block b:
                blocks) {
            if(b==null)continue;
            int y = b.coordinate.y;
            int z = b.coordinate.z;

            b.coordinate.y = (int)Math.round(y*cosTheta-z*sinTheta);
            b.coordinate.z = (int)Math.round(z*cosTheta+y*sinTheta);
        }
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateY(int theta) {
        if(theta == 0) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        for (Block b:
                blocks) {
            if(b==null)continue;
            int x = b.coordinate.x;
            int z = b.coordinate.z;

            b.coordinate.x = (int)Math.round(x*cosTheta-z*sinTheta);
            b.coordinate.z = (int)Math.round(z*cosTheta+x*sinTheta);
        }
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateZ(int theta) {
        if(theta == 0) return;

        double sinTheta = Math.sin(Math.toRadians(theta)); //1
        double cosTheta = Math.cos(Math.toRadians(theta)); //0

        for (Block b:
             blocks) {
            if(b==null)continue;
            int x = b.coordinate.x; //-1
            int y = b.coordinate.y; //1

            b.coordinate.x = (int)Math.round(x*cosTheta-y*sinTheta);
            b.coordinate.y = (int)Math.round(y*cosTheta+x*sinTheta);
        }
    }


    /**
     * Rotates the structure along x axis.
     */
    private void rotateBlockRelativeX(Block b, int theta) {
        if(theta == 0) return;
        if(b==null) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        int y = b.coordinate.y;
        int z = b.coordinate.z;

        b.coordinate.y = (int)Math.round(y*cosTheta-z*sinTheta);
        b.coordinate.z = (int)Math.round(z*cosTheta+y*sinTheta);
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateBlockRelativeY(Block b, int theta) {
        if(theta == 0) return;
        if(b==null) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        int x = b.coordinate.x;
        int z = b.coordinate.z;

        b.coordinate.x = (int)Math.round(x*cosTheta-z*sinTheta);
        b.coordinate.z = (int)Math.round(z*cosTheta+x*sinTheta);
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateBlockRelativeZ(Block b, int theta) {
        if(theta == 0) return;
        if(b==null)return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        int x = b.coordinate.x;
        int y = b.coordinate.y;

        b.coordinate.x = (int)Math.round(x*cosTheta-y*sinTheta);
        b.coordinate.y = (int)Math.round(y*cosTheta+x*sinTheta);
    }


    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Structure:{"+this.uuid.toString()+"}";
    }
}
