// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package logitowwindowsnative;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class DeviceEventReceiver_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return logitowwindowsnative.DeviceEventReceiver_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        logitowwindowsnative.DeviceEventReceiver_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __DeviceEventReceiver extends system.Object implements logitowwindowsnative.DeviceEventReceiver {
    
    protected __DeviceEventReceiver(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void OnDeviceDiscovered(java.lang.String uuid);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void OnDeviceLost(java.lang.String uuid);
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void OnScanStarted();
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void OnScanStopped();
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void OnDeviceConnected(java.lang.String uuid);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void OnDeviceDisconnected(java.lang.String uuid);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;[B)V")
    public native void OnBlockDataReceived(java.lang.String uuid, byte[] data);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;F)V")
    public native void OnBatteryInfoReceived(java.lang.String uuid, float battery);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LWindows/Devices/Bluetooth/GenericAttributeProfile/GattCommunicationStatus;)V")
    public native void OnConnectionError(java.lang.String uuid, system.Enum communicationStatus);
}
//</generated-proxy>