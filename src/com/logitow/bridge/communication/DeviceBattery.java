package bridge.communication;

/**
 * Represents a device battery.
 */
public class DeviceBattery {
    /**
     * The current voltage of the battery.
     * Min: 1.5V
     * Max: 2.1V
     */
    public float voltage;

    /**
     * Returns the current battery charge percentage.
     * @return value from 0.0f to 1.0f
     */
    public float getChargePercent() {
        return (voltage / getMaxVoltage());
    }

    /**
     * Gets the minimum device voltage.
     * @return
     */
    public float getMinVolatege() {return 1.5f;}

    /**
     * Gets the maximum device voltage.
     * @return
     */
    public float getMaxVoltage() {return 2.1f;}

    /**
     * Checks whether the device is running low on charge.
     * @return
     */
    public boolean isLowCharge() {
        if(getChargePercent() <= 0.05f) {
            return true;
        } else {
            return false;
        }
    }
}
