package com.logitow.bridge.communication;

import com.logitow.bridge.build.Structure;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single LOGITOW device.
 */
public class Device {
    /**
     * Info about the device.
     */
    public DeviceInfo info;

    /**
     * The battery info of the device.
     */
    public DeviceBattery deviceBattery;

    /**
     * The currently developed block structure for this device.
     */
    public Structure currentStructure;

    /**
     * UUIDs and their respective friendly names.
     */
    static Map<String, String> friendlyNames =  new HashMap<>();

    /**
     * Gets a device instance from uuid.
     * @param uuid
     * @return
     */
    public static Device getConnectedFromUuid(String uuid) {
        //Getting the device reference from the connected devices.
        Device device = null;
        for (Device d :
                LogitowDeviceManager.current.connectedDevices) {
            if(d.info.uuid.equals(uuid)) {
                device = d;
            }
        }
        return device;
    }

    /**
     * Instantiates a device given bluetooth uuid.
     * @param uuid
     */
    public Device(String uuid) {
        LogitowDeviceManager.current.logger.info("Registering device: {}", uuid);
        this.info = new DeviceInfo();
        this.info.uuid = uuid;

        //Reconnect
        if(friendlyNames.containsKey(uuid)) {
            this.info.friendlyName = friendlyNames.get(uuid);
        }
        //New connection this sesh
        else {
            String friendlyName = "LOGITOW - " + friendlyNames.size();
            this.info.friendlyName = friendlyName;
            friendlyNames.put(uuid, friendlyName);
        }
        this.currentStructure = new Structure(this);
    }

    /**
     * Connects to the device.
     * @return
     */
    public boolean connect() {
        return LogitowDeviceManager.current.connectDevice(this);
    }

    /**
     * Disconnects the device.
     * @return
     */
    public boolean disconnect() {
        return LogitowDeviceManager.current.disconnectDevice(this);
    }

    /**
     * Requests a battery voltage update.
     */
    public void requestBatteryInfoUpdate(){LogitowDeviceManager.current.requestBatteryVoltageUpdate(this);}

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
        if(info.friendlyName != null && info.friendlyName != "") {
            return info.friendlyName;
        }
        return info.uuid;

    }
}
