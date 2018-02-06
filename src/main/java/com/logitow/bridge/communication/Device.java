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

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Device) {
            Device device = (Device) obj;
            if(device.info.uuid == this.info.uuid) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
