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
import com.logitow.bridge.event.structure.StructureLoadEvent;
import com.logitow.bridge.event.structure.StructureSaveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a single Logitow structure.
 */
public class Structure implements Serializable {
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
    public ArrayList<Vec3> rotations;

    /**
     * The device of the structure.
     */
    public transient Device device;

    /**
     * Constructs a new structure.
     */
    public Structure() {
        rotations = new ArrayList<>();
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
    }

    /**
     * Constructs a new structure given device.
     * @param device
     */
    public Structure(Device device) {
        rotations = new ArrayList<>();
        uuid = UUID.randomUUID();
        blocks.add(new Block(0)); //Adding the base block.
        this.device = device;
    }

    /**
     * Saves a structure to file inside the structure dir of the lib.
     */
    public static void saveToFile(Structure structure) throws IOException {
        saveToFile(structure, Paths.get(getStructureSaveDir().getPath(), structure.uuid.toString()).toString() + ".logitow");
    }

    /**
     * Saves a structure to file.
     */
    public static void saveToFile(Structure structure, String path) throws IOException {
        logger.info("Saving structure: {}, to: {}", structure, path);

        //Serializing
        Gson serializer = new Gson();
        serializer.toJson(structure);
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.print(serializer.toJson(structure));
        }

        //Calling event.
        EventManager.callEvent(new StructureSaveEvent(structure, path));
    }

    /**
     * Saves the structure to a file in the structures directory.
     */
    public void saveToFile() throws IOException {
        Structure.saveToFile(this);
    }

    /**
     * Saves the structure to the specified path.
     * @param path
     */
    public void saveToFile(String path) throws IOException {
        Structure.saveToFile(this, path);
    }

    /**
     * Loads a structure from the logitow folder given its uuid.
     * @param uuid
     * @return
     */
    public static Structure loadByUuid(String uuid) throws IOException {
        logger.info("Loading structure: {} from the structures folder...", uuid);

        for (File file :
                getStructureSaveDir().listFiles()) {
            if(file.getName().contains(uuid)) {
                return loadFromFile(file.getPath());
            }
        }
        return null;
    }
    /**
     * Loads structure data from file.
     */
    public static Structure loadFromFile(String path) throws IOException {
        logger.info("Loading structure from: {}", path);

        //Getting the file.
        File file = new File(path);
        if(!file.exists()) {
            throw new FileNotFoundException();
        }

        //Deserializing.
        FileReader fileReader = new FileReader(path);
        Gson deserializer = new Gson();
        Structure loaded = deserializer.fromJson(fileReader, Structure.class);
        EventManager.callEvent(new StructureLoadEvent(loaded, path));
        return loaded;
    }
    /**
     * Gets the save dir of the structure files.
     * @return
     */
    public static File getStructureSaveDir() {
        File directory = new File(new File("").getAbsolutePath() + "/structures/");
        if(!directory.exists()) {
            directory.mkdir();
        }
        return directory;
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
                    if (relativeSide.sideId-1 == i) {
                        logger.info(" [{}] Found child: {}", i, operation.blockA.children[i]);
                        operation.blockB = this.getBlockById(operation.blockA.children[i]);
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
        logger.info("Handling block: {} addition to structure: {}", operation.blockB, this.uuid);

        //Updating structure info on the block.
        operation.blockB.calculateCoordinates(this, operation.blockA, operation.blockSide);

        logger.info("Block A children:");
        for (int child :
                operation.blockA.children) {
            logger.info(" {}", child);
        }

        //Rotating the added block.
        for (Vec3 rotation :
                rotations) {
            rotateBlockRelative(operation.blockB, rotation);
        }

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
        if(operation.blockA != null) {
            operation.blockA.children[operation.blockB.parentAttachSide.sideId-1] = -10;
        }
        operation.blockB.parent = -10;

        //Recursively removing children.
        for (int child : operation.blockB.children) {
            if(child!=-10) {
                removeBlock(this.getBlockById(child));
            }
        }
    }

    /**
     * Removes the specified block from the structure.
     * @param b
     */
    public void removeBlock(Block b) {
        if(b == null) return;
        onBuildOperation(new BlockOperation(null, b.parentAttachSide, b, BlockOperationType.BLOCK_REMOVE));
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
        if(angles.getX() % 90 != 0 || angles.getY() % 90 != 0 || angles.getZ() % 90 != 0) {
            return false;
        }

        //Rotating every block.
        rotateZ(angles.getZ());
        rotateY(angles.getY());
        rotateX(angles.getX());

        //Adding the rotation.
        addRotation(angles);

        return true;
    }

    /**
     *  Rotates a block by the specified angle.
     * @param angles
     * @return
     */
    private boolean rotateBlockRelative(Block a, Vec3 angles) {
        logger.info("Rotating block: {}, by {}", a.coordinate, angles);

        //Checking the angles.
        if(angles.getX() % 90 != 0 || angles.getY() % 90 != 0 || angles.getZ() % 90 != 0) {
            return false;
        }

        //Rotating on every axis.
        rotateBlockRelativeZ(a,angles.getZ());
        rotateBlockRelativeY(a,angles.getY());
        rotateBlockRelativeX(a,angles.getX());

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
            int y = b.coordinate.getY();
            int z = b.coordinate.getZ();

            b.coordinate.setY((int)Math.round(y*cosTheta-z*sinTheta));
            b.coordinate.setZ((int)Math.round(z*cosTheta+y*sinTheta));
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
            int x = b.coordinate.getX();
            int z = b.coordinate.getZ();

            b.coordinate.setX((int)Math.round(x*cosTheta-z*sinTheta));
            b.coordinate.setZ((int)Math.round(z*cosTheta+x*sinTheta));
        }
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateZ(int theta) {
        if(theta == 0) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        for (Block b:
             blocks) {
            if(b==null)continue;
            int x = b.coordinate.getX();
            int y = b.coordinate.getY();

            b.coordinate.setX((int)Math.round(x*cosTheta-y*sinTheta));
            b.coordinate.setY((int)Math.round(y*cosTheta+x*sinTheta));
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

        int y = b.coordinate.getY();
        int z = b.coordinate.getZ();

        b.coordinate.setY((int)Math.round(y*cosTheta-z*sinTheta));
        b.coordinate.setZ((int)Math.round(z*cosTheta+y*sinTheta));
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateBlockRelativeY(Block b, int theta) {
        if(theta == 0) return;
        if(b==null) return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        int x = b.coordinate.getX();
        int z = b.coordinate.getZ();

        b.coordinate.setX((int)Math.round(x*cosTheta-z*sinTheta));
        b.coordinate.setZ((int)Math.round(z*cosTheta+x*sinTheta));
    }
    /**
     * Rotates the structure along x axis.
     */
    private void rotateBlockRelativeZ(Block b, int theta) {
        if(theta == 0) return;
        if(b==null)return;

        double sinTheta = Math.sin(Math.toRadians(theta));
        double cosTheta = Math.cos(Math.toRadians(theta));

        int x = b.coordinate.getX();
        int y = b.coordinate.getY();

        b.coordinate.setX((int)Math.round(x*cosTheta-y*sinTheta));
        b.coordinate.setY((int)Math.round(y*cosTheta+x*sinTheta));
    }

    /**
     * Adds rotation to the structure.
     * @param rotation
     */
    private void addRotation(Vec3 rotation) {
        int rotAxisA = 0; //0 = no rotation; 4 = more than 1 axis.
        if(rotation.getX() != 0) {
            rotAxisA = 1;
        }
        if(rotation.getY() != 0) {
            if(rotAxisA > 0) {
                rotAxisA = 4;
            } else {
                rotAxisA = 2;
            }
        }
        if(rotation.getZ() != 0) {
            if(rotAxisA > 0) {
                rotAxisA = 4;
            } else {
                rotAxisA = 3;
            }
        }

        Vec3 lastRot = null;
        if(rotations.size() > 0) {
            lastRot = this.rotations.get(rotations.size()-1);
        }
        if(lastRot == null) {
            //No previous records, adding a new one.
            this.rotations.add(rotation);
            return;
        }

        int rotAxisB = 0; //0 = no rotation; 4 = more than 1 axis.
        if(lastRot.getX() != 0) {
            rotAxisB = 1;
        }
        if(lastRot.getY() != 0) {
            if(rotAxisB > 0) {
                rotAxisB = 4;
            } else {
                rotAxisB = 2;
            }
        }
        if(lastRot.getZ() != 0) {
            if(rotAxisB > 0) {
                rotAxisB = 4;
            } else {
                rotAxisB = 3;
            }
        }

        //Adding up.
        if(rotAxisA == 0) return; //Nothing to add.
        if(rotAxisA == 4 || rotAxisB == 4) {
            //Just adding another record.
            this.rotations.add(rotation);
            return;
        }
        if(rotAxisA == rotAxisB) {
            //Same axis rotated, combining rotations.
            this.rotations.remove(rotations.size()-1);
            Vec3 sum = lastRot.add(rotation);
            if(sum.getX() >= 360) {
                sum.setX(sum.getX() % 360);
            }
            if(sum.getY() >= 360) {
                sum.setY(sum.getY() % 360);
            }
            if(sum.getZ() >= 360) {
                sum.setZ(sum.getZ() % 360);
            }
            this.rotations.add(lastRot.add(rotation));
        } else {
            //Different axis rotated, adding another record.
            this.rotations.add(rotation);
        }
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
