package com.logitow.bridge.communication;

import com.logitow.bridge.build.block.Block;
import com.logitow.bridge.build.block.BlockOperation;
import com.logitow.bridge.build.block.BlockOperationType;
import com.logitow.bridge.build.block.BlockSide;
import com.logitow.bridge.communication.platform.PlatformType;
import com.logitow.bridge.communication.platform.linux.LinuxDeviceManager;
import com.logitow.bridge.communication.platform.mac.MacDeviceManager;
import com.logitow.bridge.communication.platform.windows.WindowsDeviceManager;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.DeviceConnectedEvent;
import com.logitow.bridge.event.device.DeviceDisconnectedEvent;
import com.logitow.bridge.event.device.DeviceDiscoveredEvent;
import com.logitow.bridge.event.device.DeviceLostEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryLowChargeEvent;
import com.logitow.bridge.event.device.battery.DeviceBatteryVoltageUpdateEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerCreatedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStartedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerDiscoveryStoppedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerErrorEvent;
import com.logitow.bridge.util.OSValidator;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages connection to LOGITOW devices.
 */
public abstract class LogitowDeviceManager {
    /**
     * The currently used device manager.
     */
    public static LogitowDeviceManager current;

    /**
     * The list of currently discovered LOGITOW devices.
     */
    public ArrayList<Device> discoveredDevices = new ArrayList<Device>();
    /**
     * The list of currently connected LOGITOW devices.
     */
    public ArrayList<Device> connectedDevices = new ArrayList<Device>();

    /**
     * The platform this manager supports.
     */
    public PlatformType platform;

    /**
     * Whether the manager is currently scanning.
     */
    public boolean isScanning = false;

    /**
     * Used for avoiding the verification message processing.
     */
    public byte[] cacheData;

    /**
     * The class logger.
     */
    public Logger logger;

    /**
     * Initializes the appropriate platform-specific device manager implementation.
     */
    public static boolean boot() {
        //Checking if the current OS is supported.
        if(!OSValidator.isSupported()) {
            return false;
        }

        //Registering events.
        registerEvents();

        //Choosing right device manager.
        if(OSValidator.isWindows()) {
            current = new WindowsDeviceManager();
        } else if(OSValidator.isMac()) {
            current = new MacDeviceManager();
        } else if(OSValidator.isUnix()) {
            current = new LinuxDeviceManager();
        }

        //Running device manager initialize.
        if(current != null) {
            if(current.initialize()) {
                return true;
            } else {
                EventManager.callEvent(new DeviceManagerErrorEvent(current, new Exception("Device manager for the current platform couldn't be initialized!")));
                return false;
            }
        } else {
            EventManager.callEvent(new DeviceManagerErrorEvent(current, new Exception("Device manager for the current platform couldn't be initialized!")));
            return false;
        }
    }

    /**
     * Registers events.
     */
    private static void registerEvents() {
        EventManager.registerEvent(DeviceManagerCreatedEvent.class);
        EventManager.registerEvent(DeviceManagerDiscoveryStartedEvent.class);
        EventManager.registerEvent(DeviceManagerDiscoveryStoppedEvent.class);
        EventManager.registerEvent(DeviceManagerErrorEvent.class);
        EventManager.registerEvent(DeviceConnectedEvent.class);
        EventManager.registerEvent(DeviceDisconnectedEvent.class);
        EventManager.registerEvent(DeviceDiscoveredEvent.class);
        EventManager.registerEvent(DeviceLostEvent.class);
        EventManager.registerEvent(DeviceBatteryLowChargeEvent.class);
        EventManager.registerEvent(DeviceBatteryVoltageUpdateEvent.class);
    }

    /**
     * Sets up the device manager.
     */
    public abstract boolean initialize();

    /**
     * Starts LOGITOW device discovery.
     */
    public abstract boolean startDeviceDiscovery();

    /**
     * Stops LOGITOW device discovery.
     */
    public abstract boolean stopDeviceDiscovery();

    /**
     * Connects to the specified device.
     * @param device
     * @return
     */
    public abstract boolean connectDevice(Device device);

    /**
     * Disconnects the specified device.
     * @param device
     * @return
     */
    public abstract boolean disconnectDevice(Device device);

    /**
     * Gets the current bluetooth adapter state.
     * @return
     */
    public abstract BluetoothState getBluetoothState();

    /**
     * Requests a battery voltage update from a device.
     * @param device
     */
    public abstract boolean requestBatteryVoltageUpdate(Device device);


    //Native callbacks.
    /**
     * Called when the device discovery is started.
     */
    public void onDeviceDiscoveryStarted() {
        isScanning = true;

        //Calling event.
        EventManager.callEvent(new DeviceManagerDiscoveryStartedEvent(this));
    }

    /**
     * Called when the device discovery is stopped.
     */
    public void onDeviceDiscoveryStopped() {
        isScanning = false;

        //Calling event.
        EventManager.callEvent(new DeviceManagerDiscoveryStoppedEvent(this));
    }

    /**
     * Called by the native implementation when a new device is discovered.
     */
    public void onDeviceDiscovered(String uuid) {
        //Adding the device to the list of discovered devices.
        Device device = new Device(uuid);
        discoveredDevices.add(device);

        //Calling the device connected event.
        EventManager.callEvent(new DeviceDiscoveredEvent(device));
    }

    /**
     * Called by the native implementation when a device is no longer available.
     */
    public void onDeviceLost(String uuid) {
        //Removing the device from the list of discovered devices.
        Device device = null;
        for (Device d :
                discoveredDevices) {
            if (d.info.uuid == uuid) {
                device = d;
            }
        }

        if(device != null) {
            //Removing the device from discovered.
            discoveredDevices.remove(device);
            //Calling event.
            EventManager.callEvent(new DeviceLostEvent(device));
        }
    }

    /**
     * Called by the native implementation when a device is connected.
     *
     * @param uuid
     */
    public void onDeviceConnected(String uuid) {
        //Adding the device to the list of connected devices.
        isScanning = false;
        Device device = new Device(uuid);
        connectedDevices.add(device);

        //Calling the device connected event.
        EventManager.callEvent(new DeviceConnectedEvent(device));
    }

    /**
     * Called by the native implementation when a device is disconnected.
     *
     * @param uuid
     */
    public void onDeviceDisconnected(String uuid) {
        Device device = Device.getConnectedFromUuid(uuid);

        //Making sure we have the device reference.
        if(device != null) {
            //Removing device from list of connected devices.
            connectedDevices.remove(device);

            //Calling the device disconnected event.
            EventManager.callEvent(new DeviceDisconnectedEvent(device));
        }
    }

    /**
     * Called by the native implementation when a block state update is received from a device.
     *
     * @param deviceUuid
     * @param blockInfo
     */
    public void onBlockInfoReceived(String deviceUuid, byte[] blockInfo) {
        //Getting the device reference.
        Device device = Device.getConnectedFromUuid(deviceUuid);

        //Ignoring the verification packets.
        if (current.cacheData != null && Arrays.equals(blockInfo, current.cacheData)) {
            current.cacheData = null;
            return;
        }
        current.cacheData = blockInfo;

        //Handling the received block data.
        int blockAID = blockInfo[2] & 0xFF |
                (blockInfo[1] & 0xFF) << 8 |
                (blockInfo[0] & 0xFF) << 16;

        int insertFace = blockInfo[3] & 0xFF;

        int blockBID = blockInfo[6] & 0xFF |
                (blockInfo[5] & 0xFF) << 8 |
                (blockInfo[4] & 0xFF) << 16;

        //Calling operation event on the device current structure.
        Block blockA = null; //Getting the block a reference.
        for (Block b :
                device.currentStructure.blocks) {
            if (b.id == blockAID) {
                blockA = b;
            }
        }

        BlockOperationType operationType = BlockOperationType.BLOCK_ADD;
        if(blockBID == 0) {
            operationType = BlockOperationType.BLOCK_REMOVE;
        }
        if(blockA!=null) {
            if(operationType == BlockOperationType.BLOCK_ADD) {
                device.currentStructure.onBuildOperation(new BlockOperation(blockA, BlockSide.valueOf(insertFace), new Block(blockBID), operationType));
            } else {
                device.currentStructure.onBuildOperation(new BlockOperation(blockA, BlockSide.valueOf(insertFace), null, operationType));
            }
        }
    }

    /**
     * Called by the native implementation when a voltage update is received from a device.
     * @param deviceUuid
     * @param voltage
     */
    public void onBatteryVoltageInfoReceived(String deviceUuid, float voltage) {
        //Getting the only connected device.
        Device device = Device.getConnectedFromUuid(deviceUuid);

        //Updating voltage info for the device.
        device.deviceBattery.voltage = voltage;

        //Calling battery voltage change event.
        EventManager.callEvent(new DeviceBatteryVoltageUpdateEvent(device));

        //Checking if the voltage is critical.
        if(device.deviceBattery.isLowCharge()) {
            EventManager.callEvent(new DeviceBatteryLowChargeEvent(device));
        }
    }
}
