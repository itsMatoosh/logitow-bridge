// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package logitowwindowsnative;

@net.sf.jni4net.attributes.ClrType
public class Scanner extends system.Object {
    
    //<generated-proxy>
    private static system.Type staticType;
    
    protected Scanner(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrConstructor("()V")
    public Scanner() {
            super(((net.sf.jni4net.inj.INJEnv)(null)), 0);
        logitowwindowsnative.Scanner.__ctorScanner0(this);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    private native static void __ctorScanner0(net.sf.jni4net.inj.IClrProxy thiz);
    
    @net.sf.jni4net.attributes.ClrMethod("(LLogitowWindowsNative/DeviceEventReceiver;)V")
    public native void SetEventListener(logitowwindowsnative.DeviceEventReceiver eventListener);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)LLogitowWindowsNative/LogitowDevice;")
    public native logitowwindowsnative.LogitowDevice GetConnectedLogitowDevice(java.lang.String uuid);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)LLogitowWindowsNative/LogitowDevice;")
    public native logitowwindowsnative.LogitowDevice GetDiscoveredLogitowDevice(java.lang.String id);
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean GetBluetoothSupported();
    
    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean GetBluetoothEnabled();
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void StartBleDeviceWatcher();
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void StopBleDeviceWatcher();
    
    @net.sf.jni4net.attributes.ClrMethod("(LLogitowWindowsNative/LogitowDevice;)V")
    public native void ConnectOrReconnect(logitowwindowsnative.LogitowDevice device);
    
    public static system.Type typeof() {
        return logitowwindowsnative.Scanner.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        logitowwindowsnative.Scanner.staticType = staticType;
    }
    //</generated-proxy>
}