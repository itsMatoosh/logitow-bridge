package com.logitow.bridge.communication.platform.mac;

import com.logitow.bridge.communication.platform.NativeUtils;
import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
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
        loadNative();

        //Calling the device manager created event.
        EventManager.callEvent(new DeviceManagerCreatedEvent(this));
        return true;
    }

    /**
     * Loads the mac native lib.
     */
    private void loadNative() {
        try {
            System.loadLibrary("logitow");
            setup();
        } catch (Throwable error) {
            try {
                NativeUtils.loadLibraryFromJar("/logitow.jnilib");
                setup();
            } catch (UnsatisfiedLinkError|IOException err) {
                //Called when the system is unable to find a usable lib for mac.
                EventManager.callEvent(new DeviceManagerErrorEvent(this, new Exception("Couldn't find a usable native logitow lib for current system.")));
            }
        }
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
        //The current implementation connects to the first device automatically.
        //TODO: Change that.
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
        //The current implementation supports only 1 device.
        //TODO: Support more devices.
        disconnect(true);
        return false;
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
     * @param scanForOtherDevice restart scanning
     */
    public static native void disconnect(boolean scanForOtherDevice);
    /**
     * Gets the native bluetooth state.
     * Unknown = 0,
     * Resetting,
     * Unsupported,
     * Unauthorized,
     * PoweredOff,
     * PoweredOn,
     * };</code>
     */
    public static native int getNativeBluetoothState();

    //Notify methods.
    /**
     * Used by the native implementation to notify of voltage updates.
     */
    public void notifyVoltage(float voltage) {
        current.onBatteryVoltageInfoReceived(current.connectedDevices.get(0).info.uuid, voltage);
    }

    /**
     * Used by the native implementation to notify of block state updates.
     */
    private static void notifyBlockData(byte[] data) {
        current.onBlockInfoReceived(current.connectedDevices.get(0).info.uuid, data);
    }

    /**
     * Used by the native implementation to notify of device connecting.
     * @param uuid
     */
    private static void notifyConnected(String uuid) {
        current.onDeviceConnected(uuid);
        current.onDeviceDiscoveryStopped();
    }
    /**
     * Used by the native implementation to notify of device disconnecting.
     * @param isScanning whether scanning for new devices continues.
     */
    private static void notifyDisconnected(boolean isScanning) {
        //TODO: Add multi device support for mac.
        current.onDeviceDisconnected(current.connectedDevices.get(0).info.uuid);

        if(isScanning) {
            current.onDeviceDiscoveryStarted();
        }
    }
}
