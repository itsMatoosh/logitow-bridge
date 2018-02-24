//
//  LogiTowLib.m
//  LogiTowLib
//
//  Created by Trychen on 17/12/10.
//  Modified by itsMatoosh on 18/01/03
//
//

#import "com_logitow_bridge_communication_platform_mac_MacDeviceManager.h"
#import "LogiTowLib.h"
#import "BabyBluetooth.h"
#import <CoreBluetooth/CoreBluetooth.h>

@implementation LogiTowLib

@end

JNIEXPORT void JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_setup
(JNIEnv *env, jclass class){
    NSLog(@"Setting up the native mac implementation.");
    [[Controller sharedController] setupJNI:env ble_class:class];
}

JNIEXPORT jint JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_getNativeBluetoothState
(JNIEnv *env, jclass class){
    return [[Controller sharedController] bluetoothState];
}

JNIEXPORT jboolean JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_startScanDevice
(JNIEnv *env, jclass class)
{
    [[Controller sharedController] startScan];
    
    return true;
}
JNIEXPORT void JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_stopScanDevice
(JNIEnv *env, jclass class) {
    [[Controller sharedController] stopScan];
}

JNIEXPORT jboolean JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_connect
(JNIEnv *env, jclass class, jstring uuid)
{
    const char *chars = (*env)->GetStringUTFChars(env, uuid, 0);
    
    NSString *deviceUUID = [NSString stringWithUTF8String:chars];
    
    bool result = [[Controller sharedController] connect:deviceUUID];
    
    (*env)->ReleaseStringUTFChars(env, uuid, chars);
    
    return result;
}

JNIEXPORT void JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_disconnect
(JNIEnv *env, jclass class, jstring uuid) {
    const char *chars = (*env)->GetStringUTFChars(env, uuid, 0);
    
    NSString *deviceUUID = [NSString stringWithUTF8String:chars];
    
    [[Controller sharedController] disconnect:deviceUUID];
    
    (*env)->ReleaseStringUTFChars(env, uuid, chars);
}

JNIEXPORT jboolean JNICALL Java_com_logitow_bridge_communication_platform_mac_MacDeviceManager_writeToGetVoltage(JNIEnv *env, jclass class, jstring uuid) {
    const char *chars = (*env)->GetStringUTFChars(env, uuid, 0);
    
    NSString *deviceUUID = [NSString stringWithUTF8String:chars];
    
    BOOL R = [[Controller sharedController] writeToGetVoltage: deviceUUID];
    
    (*env)->ReleaseStringUTFChars(env, uuid, chars);
    
    return R;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javavm, void *reserved) {
    JNIEnv *env;
   
    if ((*javavm)->GetEnv(javavm, (void**)&env, JNI_VERSION_1_4)) {
        return JNI_ERR;
    }
    
    return JNI_VERSION_1_4;
}
