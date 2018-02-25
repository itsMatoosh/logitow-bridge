package com.logitow.bridge.communication.platform.windows;

import com.logitow.bridge.communication.BluetoothState;
import com.logitow.bridge.communication.Device;
import com.logitow.bridge.communication.LogitowDeviceManager;
import com.logitow.bridge.communication.platform.PlatformType;
import com.logitow.bridge.event.EventManager;
import com.logitow.bridge.event.device.DeviceLostEvent;
import com.logitow.bridge.event.devicemanager.DeviceManagerCreatedEvent;
import logitowwindowsnative.DeviceEventReceiver;
import logitowwindowsnative.Scanner;
import net.sf.jni4net.Bridge;
import org.apache.logging.log4j.LogManager;
import system.Enum;

import java.io.*;
import java.net.URISyntaxException;

/**
 * Manages the native windows device communication.
 */
public class WindowsDeviceManager extends LogitowDeviceManager {

    /**
     * Represents the windows native scanner.
     */
    public logitowwindowsnative.Scanner scanner;

    /**
     * Natives used by jni4net.
     */
    public static String[] JNI_WIN_NATIVES = new String[] {
            "/jni4net.n.w32.v40-0.8.8.0.dll",
            "/jni4net.n.w64.v40-0.8.8.0.dll",
            "/jni4net.n.w32.v20-0.8.8.0.dll",
            "/jni4net.n.w64.v20-0.8.8.0.dll",
            "/jni4net.n-0.8.8.0.dll",
            "/native/LogitowWindowsNative.j4n.dll",
            "/native/LogitowWindowsNative.dll"
    };

    /**
     * Sets up the device manager.
     */
    @Override
    public boolean initialize() {
        this.platform = PlatformType.WINDOWS;
        logger = LogManager.getLogger(WindowsDeviceManager.class);
        // create com.logitow.bridge, with default initialize
        // it will lookup jni4net.n.dll next to jni4net.j.jar
        Bridge.setVerbose(true);

        try {
            //Initializing the bridge. Checking 32/64 bit.
            Bridge.setVerbose(true);
            File nativesDir = null;
            for (String nativeDll :
            JNI_WIN_NATIVES){
                nativesDir = extract(nativeDll);
                System.out.println(nativesDir);
            }
            nativesDir = nativesDir.getParentFile();

            Bridge.init(nativesDir);

            //Loading the native windows ble library.
            File file = new File(nativesDir.getAbsolutePath() + "/LogitowWindowsNative.j4n.dll");
            if(!file.exists()) {
                System.err.println("Couldn't find the logitow windows native DLL!");
            } else {
                System.out.println("Found the logitow windows native DLL!");
            }
            Bridge.LoadAndRegisterAssemblyFrom(file);
        } catch (Exception e) {
            System.out.println("Error establishing .Net com.logitow.bridge");
            e.printStackTrace();

            return false;
        }

        //Instantiating the device scanner.
        scanner = new Scanner();
        if(scanner == null) {
            System.err.println("Couldn't initialize the Windows Device Scanner!");
            return false;
        }
        scanner.SetEventListener(new DeviceEventReceiver()
        //Calls different events.
        {
            @Override
            public void OnDeviceDiscovered(String uuid) {
                onDeviceDiscovered(uuid);
            }

            @Override
            public void OnDeviceLost(String uuid) {
                onDeviceLost(uuid);
            }

            @Override
            public void OnScanStarted() {
                onDeviceDiscoveryStarted();
            }

            @Override
            public void OnScanStopped() {
                onDeviceDiscoveryStopped();
            }

            @Override
            public void OnDeviceConnected(String uuid) {
                onDeviceConnected(uuid);
            }

            @Override
            public void OnDeviceDisconnected(String uuid) {
                onDeviceDisconnected(uuid);
            }

            @Override
            public void OnBlockDataReceived(String uuid, byte[] data) {
                onBlockInfoReceived(uuid, data);
            }

            @Override
            public void OnBatteryInfoReceived(String uuid, float battery) {
                onBatteryVoltageInfoReceived(uuid, battery);
            }

            @Override
            public void OnConnectionError(String uuid, Enum communicationStatus) {
                onDeviceConnectionError(uuid, communicationStatus.toString());
                disconnectDevice(Device.getConnectedFromUuid(uuid));
            }

            public void OnConnectionError(String uuid, java.lang.Enum communicationStatus) {
                onDeviceConnectionError(uuid, communicationStatus.toString());
                disconnectDevice(Device.getConnectedFromUuid(uuid));
            }
        });

        //Calling the device manager created event.
        EventManager.callEvent(new DeviceManagerCreatedEvent(this));

        return true;
    }

    /**
     * Extract the specified resource from inside the jar to the local file system.
     * @param jarFilePath absolute path to the resource
     * @return full file system path if file successfully extracted, else null on error
     */
    public static File extract(String jarFilePath) throws URISyntaxException {

        if(jarFilePath == null)
            return null;

        // Alright, we don't have the file, let's extract it
        try {
            // Read the file we're looking for
            InputStream fileStream = LogitowDeviceManager.class.getResourceAsStream(jarFilePath);

            // Was the resource found?
            if(fileStream == null)
                return null;

            // Grab the file name
            String[] chopped = jarFilePath.split("\\/");
            String fileName = chopped[chopped.length-1];

            // Create our temp file (first param is just random bits)
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

            // Set this file to be deleted on VM exit
            tempFile.deleteOnExit();

            // Create an output stream to barf to the temp file
            OutputStream out = new FileOutputStream(tempFile);

            // Write the file to the temp file
            byte[] buffer = new byte[1024];
            int len = fileStream.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = fileStream.read(buffer);
            }

            // Close the streams
            fileStream.close();
            out.close();

            // Return the path of this sweet new file
            return tempFile;

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Starts LOGITOW device discovery.
     */
    @Override
    public boolean startDeviceDiscovery() {
        //Calling lost on every previously discovered device.
        for (int i = 0; i < discoveredDevices.size(); i++) {
            //Calling event.
            EventManager.callEvent(new DeviceLostEvent(discoveredDevices.get(i)));
            discoveredDevices.remove(i);
        }

        if(!isScanning) {
            scanner.StartBleDeviceWatcher();
            isScanning = true;
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
            scanner.StopBleDeviceWatcher();
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
        logger.info("Connecting device: " + device);
        for (Device d :
                connectedDevices) {
            if (device == d) {
                logger.warn("Cannot connect to: {}! Device already connected!", device);
                return false;
            }
        }
        scanner.Connect(scanner.GetDiscoveredLogitowDevice(device.info.uuid));
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
        logger.info("Disconnecting device: " + device);
        scanner.Disconnect(scanner.GetConnectedLogitowDevice(device.info.uuid));
        return true;
    }

    /**
     * Gets the current bluetooth adapter state.
     *
     * @return
     */
    @Override
    public BluetoothState getBluetoothState() {
        if(scanner.GetBluetoothSupported()) {
            if(scanner.GetBluetoothEnabled()) {
                return BluetoothState.PoweredOn;
            } else {
                return BluetoothState.PoweredOff;
            }
        } else {
            return BluetoothState.Unsupported;
        }
    }

    /**
     * Requests a battery voltage update from a device.
     *
     * @param device
     */
    @Override
    public boolean requestBatteryVoltageUpdate(Device device) {
        scanner.GetConnectedLogitowDevice(device.info.uuid).RequestDeviceBatteryStatusUpdate();
        return true;
    }
}
