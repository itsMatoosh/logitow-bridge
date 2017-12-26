package com.logitow.bridge.util;

/**
 * Provides info on the currently running os.
 */
public class OSValidator {

    /**
     * System name information.
     */
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Returns whether the system is windows.
     * @return
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * Returns whether the system is mac.
     * @return
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Checks whether the system is unix.
     * @return
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    /**
     * Checks whether the system is solaris.
     * @return
     */
    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    /**
     * Returns whether the current system is supported.
     * Bluetooth might still not work due to a lack of adapter.
     * @return
     */
    public static boolean isSupported() {
        if(isWindows()) {
            if(OS.contains("10") || OS.contains("8.1")) {
                return true;
            }
        } else if(isMac()) {
            return true;
        } else if(isUnix()) {
            return true;
        }
        return false;
    }

}
