package com.logitow.bridge.communication.platform.mac;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.communication.platform.NativeUtils;
import com.logitow.bridge.communication.platform.PlatformType;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.devicemanager.DeviceManagerCreatedEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerErrorEvent;

import java.io.IOException;

/**
 * Manages native mac device communication.
 */
public class MacDeviceManager extends LogitowDeviceManager {
    /**
     * Sets up the device manager.
     */
    @Override
    public boolean initialize() {
        this.platform = PlatformType.MAC;

        //Loading the native library.
        if(!loadNative()) {
            return false;
        }

        //Calling the device manager created event.
        EventManager.callEvent(new DeviceManagerCreatedEvent(this));
        return true;
    }

    /**
     * Loads the mac native lib.
     */
    private boolean loadNative() {
        try {
            System.loadLibrary("logitow");
            setup();
        } catch (Throwable error) {
            try {
                NativeUtils.loadLibraryFromJar("/native/" + "liblogitow.jnilib");
                setup();
            } catch (UnsatisfiedLinkError|IOException err) {
                //Called when the system is unable to find a usable lib for mac.
                EventManager.callEvent(new DeviceManagerErrorEvent(this, new Exception("Couldn't find a usable native logitow lib for current system.")));
                err.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Starts LOGITOW device discovery.
     */
    @Override
    public boolean startDeviceDiscovery() {
        if(!isScanning) {
            isScanning = startScanDevice();
            if(isScanning) {
                onDeviceDiscoveryStarted();
            }
        }
        return isScanning;
    }

    /**
     * Stops LOGITOW device discovery.
     */
    @Override
    public boolean stopDeviceDiscovery() {
        if(isScanning) {
            isScanning = false;
            stopScanDevice();
            onDeviceDiscoveryStopped();
        }
        return !isScanning;
    }

    /**
     * Connects to the specified device.
     *
     * @param device
     * @return
     */
    @Override
    public boolean connectDevice(Device device) {
        //The current implementation connects to the discovered devices automatically.
        return true;
    }

    /**
     * Disconnects the specified device.
     *
     * @param device
     * @return
     */
    @Override
    public boolean disconnectDevice(Device device) {
        disconnect(device.info.uuid);
        return true;
    }

    /**
     * Gets the current state of the bluetooth adapter.
     * @return
     */
    @Override
    public BluetoothState getBluetoothState() {
        return BluetoothState.values()[getNativeBluetoothState()];
    }

    /**
     * Requests a battery voltage update from a device.
     *
     * @param device
     */
    @Override
    public boolean requestBatteryVoltageUpdate(Device device) {
        return writeToGetVoltage(device.info.uuid);
    }

    /**
     * Setting up the native lib with a reference to the MacDeviceManager class.
     */
    private static native void setup();
    /**
     * Starts device discovery.
     */
    private static native boolean startScanDevice();
    /**
     * Stops device discovery.
     */
    private static native void stopScanDevice();
    /**
     * Disconnects the device.
     *
     * @param uuid the uuid of the device to disconnect.
     */
    public static native void disconnect(String uuid);
    /**
     * Gets the native bluetooth state.
     * Unknown = 0,
     * Resetting,
     * Unsupported,
     * Unauthorized,
     * PoweredOff,
     * PoweredOn
     */
    public static native int getNativeBluetoothState();

    /**
     * Writes to get voltage from a specified device.
     * @param uuid
     * @return
     */
    private static native boolean writeToGetVoltage(String uuid);

    //Notify methods.
    /**
     * Used by the native implementation to notify of voltage updates.
     */
    public void notifyVoltage(String uuid, byte[] data) {
        float voltage = (Byte.toUnsignedInt(data[0]) * 1f) + (Byte.toUnsignedInt(data[1])*0.1f);
        current.onBatteryVoltageInfoReceived(uuid, voltage);
    }

    /**
     * Used by the native implementation to notify of block state updates.
     */
    private static void notifyBlockData(String uuid, byte[] data) {
        current.onBlockInfoReceived(uuid, data);
    }

    /**
     * Used by the native implementation to notify of device connecting.
     * @param uuid the uuid of the connected device.
     */
    private static void notifyConnected(String uuid) {
        System.out.println("Called notify connected!");
        current.onDeviceConnected(uuid);
        //current.onDeviceDiscoveryStopped();
    }
    /**
     * Used by the native implementation to notify of device disconnecting.
     * @param uuid the uuid of the disconnected device.
     * @param isScanning whether scanning for new devices continues.
     */
    private static void notifyDisconnected(String uuid, boolean isScanning) {
        current.onDeviceDisconnected(uuid);

        if(isScanning) {
            current.onDeviceDiscoveryStarted();
        }
    }
}
