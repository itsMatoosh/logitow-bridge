//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:4.0.30319.42000
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace LogitowWindowsNative {
    
    
    #region Component Designer generated code 
    public partial class LogitowDevice_ {
        
        public static global::java.lang.Class _class {
            get {
                return global::LogitowWindowsNative.@__LogitowDevice.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::LogitowWindowsNative.LogitowDevice), typeof(global::LogitowWindowsNative.LogitowDevice_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::LogitowWindowsNative.LogitowDevice), typeof(global::LogitowWindowsNative.LogitowDevice_))]
    internal sealed partial class @__LogitowDevice : global::java.lang.Object {
        
        internal new static global::java.lang.Class staticClass;
        
        private @__LogitowDevice(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::LogitowWindowsNative.@__LogitowDevice.staticClass = @__class;
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__LogitowDevice);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "ConnectAsync", "ConnectAsync0", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "DisconnectAsync", "DisconnectAsync1", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "RequestDeviceBatteryStatusUpdate", "RequestDeviceBatteryStatusUpdate2", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "__ctorLogitowDevice0", "__ctorLogitowDevice0", "(Lnet/sf/jni4net/inj/IClrProxy;Lsystem/MarshalByRefObject;)V"));
            return methods;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle ConnectAsync0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()LSystem/Threading/Tasks/Task;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::LogitowWindowsNative.LogitowDevice @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::LogitowWindowsNative.LogitowDevice>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Threading.Tasks.Task>(@__env, @__real.ConnectAsync());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle DisconnectAsync1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()LSystem/Threading/Tasks/Task;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::LogitowWindowsNative.LogitowDevice @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::LogitowWindowsNative.LogitowDevice>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Threading.Tasks.Task>(@__env, @__real.DisconnectAsync());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static void RequestDeviceBatteryStatusUpdate2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::LogitowWindowsNative.LogitowDevice @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::LogitowWindowsNative.LogitowDevice>(@__env, @__obj);
            @__real.RequestDeviceBatteryStatusUpdate();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void @__ctorLogitowDevice0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle deviceInformation) {
            // (Lsystem/MarshalByRefObject;)V
            // (LWindows/Devices/Enumeration/DeviceInformation;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::LogitowWindowsNative.LogitowDevice @__real = new global::LogitowWindowsNative.LogitowDevice(global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::Windows.Devices.Enumeration.DeviceInformation>(@__env, deviceInformation));
            global::net.sf.jni4net.utils.Convertor.InitProxy(@__env, @__obj, @__real);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::LogitowWindowsNative.@__LogitowDevice(@__env);
            }
        }
    }
    #endregion
}
