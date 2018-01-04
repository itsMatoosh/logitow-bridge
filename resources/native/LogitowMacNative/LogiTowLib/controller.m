//
//  controller.m
//  LogiTowLib
//
//  Created by Trychen on 17/12/10.
//  Modified by itsMatoosh on 17/01/03.
//
//

#import <Foundation/Foundation.h>
#include "controller.h"
#import "LogiTowLib.h"

@implementation Controller : NSObject {
    BabyBluetooth *baby;
    JavaVM *jvm;
    jclass jni_ble_class;
    CBCharacteristic *write;
}

static CBUUID *BLOCK_DATA_SERVICE_UUID,
*BLOCK_DATA_CHARACTERISTICS_READ_UUID,
*BLOCK_DATA_CHARACTERISTICS_WRITE_UUID,
*VOLTAGE_SERVICE_UUID,
*VOLTAGE_CHARACTERISTICS_WRITE_UUID,
*VOLTAGE_CHARACTERISTICS_READ_UUID;

+ (instancetype)sharedController {
    static Controller *share = nil;
    static dispatch_once_t oneToken;
    dispatch_once(&oneToken, ^{
        share = [[Controller alloc]init];
    });
    return share;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        //初始化BabyBluetooth 蓝牙库
        baby = [BabyBluetooth shareBabyBluetooth];
        //设置蓝牙委托
        [self babyDelegate];
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            BLOCK_DATA_SERVICE_UUID = [CBUUID UUIDWithString:@"69400001-b5a3-f393-e0a9-e50e24dcca99"];
            BLOCK_DATA_CHARACTERISTICS_READ_UUID = [CBUUID UUIDWithString:@"69400002-b5a3-f393-e0A9-e50e24dcca99"];
            BLOCK_DATA_CHARACTERISTICS_WRITE_UUID = [CBUUID UUIDWithString:@"69400003-b5a3-f393-e0a9-e50e24dcca99"];
            VOLTAGE_SERVICE_UUID = [CBUUID UUIDWithString:@"7f510004-b5a3-f393-e0a9-e50e24dcca9e"];
            VOLTAGE_CHARACTERISTICS_WRITE_UUID = [CBUUID UUIDWithString:@"7f510005-b5a3-f393-e0a9-e50e24dcca9e"];
            VOLTAGE_CHARACTERISTICS_READ_UUID = [CBUUID UUIDWithString:@"7f510006-b5a3-f393-e0a9-e50e24dcca9e"];
        });
    }
    return self;
}

/*
 开始搜索
 */
- (void)startScan {
    baby.scanForPeripherals().connectToPeripherals().discoverServices().discoverCharacteristics().begin();
    NSLog(@"Started scaning for logitow devices");
}

/*
 停止搜索
 */
- (void)stopScan {
    [baby cancelScan];
    NSLog(@"Cry off scaning");
}

/*
 停止搜索
 */
- (void)setWrite:(CBCharacteristic *)c {
    write = c;
}

/*
 断开连接，并重新搜索？
 */
- (void) disconnect: (NSString *) deviceUUID {
    for (CBPeripheral *peripheral in [baby findConnectedPeripherals]) {
        if ([peripheral.identifier.UUIDString isEqual:[deviceUUID uppercaseString]]) {
            [baby cancelPeripheralConnection:peripheral];
        }
    }
}

- (BOOL) writeToGetVoltage: (NSString *) deviceUUID {
    static Byte bytes[] = {0xAD, 0x02};
    NSData *data = [[NSData alloc] initWithBytes:bytes length:sizeof(bytes)];
    for (CBPeripheral *peripheral in [baby findConnectedPeripherals]) {
        if ([peripheral.identifier.UUIDString isEqual:[deviceUUID uppercaseString]]) {
            for (CBService *service in peripheral.services) {
                if (![service.UUID isEqual:VOLTAGE_SERVICE_UUID]) continue;
                for (CBCharacteristic *characteristic in service.characteristics) {
                    if (![characteristic.UUID isEqual:VOLTAGE_CHARACTERISTICS_WRITE_UUID]) continue;
                    [peripheral writeValue:data forCharacteristic:characteristic type:CBCharacteristicWriteWithResponse];
                    return YES;
                }
            }
        }
    }
    NSLog(@"Could't find device %@ to get voltage", [deviceUUID uppercaseString]);
    return NO;
}

/*
 配置JNI
 */
- (void)setupJNI:(JNIEnv *)env ble_class:(jclass)class {
    if (jni_ble_class != NULL && jvm != NULL) {
        (*env)->DeleteGlobalRef(env, jni_ble_class);
    }
    
    if (jvm == NULL) (*env)->GetJavaVM(env, &jvm);
    
    jni_ble_class = (*env)->NewGlobalRef(env, class);
    
    (*env)->DeleteLocalRef(env, class);
}

/*
 通知 BLE Stack 已连接成功
 */
- (void)notifyConnected: (NSString *) uuid {
    if (jvm == NULL) {
        NSLog(@"Could't find JVM to get JNIEnv for notifyConnected");
        return;
    }
    
    JNIEnv *env;
    //Checking if the thread is attached.
    int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        NSLog(@"GetEnv: not attached to Java thread!");
        if ((*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL) != 0) {
            NSLog(@"GetEnv: failed to attach!");
        }
    } else if (getEnvStat == JNI_OK) {
        NSLog(@"GetEnv: attached successfully");
    } else if (getEnvStat == JNI_EVERSION) {
        NSLog(@"GetEnv: version not supported");
    }
    
    jmethodID notify_connected_funid = (*env)->GetStaticMethodID(env, jni_ble_class,"notifyConnected","(Ljava/lang/String;)V");
    if (notify_connected_funid == NULL) {
        NSLog(@"Could't get methodid for notifyConnected()V while notifyConnected");
        return;
    }
    
    (*env)->CallStaticVoidMethod(env, jni_ble_class, notify_connected_funid, [Controller newJStringFromeNSString:uuid env:env]);
    
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
    }
    
    (*jvm)->DetachCurrentThread(jvm);
}

/*
 通知 BLE Stack 已断开连接
 */
- (void)notifyDisconnected: (jboolean)rescan device_uuid:(NSString *) uuid {
    if (jvm == NULL) {
        NSLog(@"Could't find JVM to get JNIEnv while notifyDisconnected");
        return;
    }
    
    JNIEnv *env;
    //Checking if the thread is attached.
    int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        NSLog(@"GetEnv: not attached to Java thread!");
        if ((*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL) != 0) {
            NSLog(@"GetEnv: failed to attach!");
        }
    } else if (getEnvStat == JNI_OK) {
        NSLog(@"GetEnv: attached successfully");
    } else if (getEnvStat == JNI_EVERSION) {
        NSLog(@"GetEnv: version not supported");
    }
    jmethodID notify_disconnected_funid = (*env)->GetStaticMethodID(env, jni_ble_class,"notifyDisconnected","(Ljava/lang/String;Z)V");
    if (notify_disconnected_funid == NULL) {
        NSLog(@"Could't get methodid for notifyDisconnected(Z)V while notifyDisconnected");
        return;
    }
    (*env)->CallStaticVoidMethod(env, jni_ble_class, notify_disconnected_funid, [Controller newJStringFromeNSString:uuid env:env] , rescan);
    
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
    }
    
    (*jvm)->DetachCurrentThread(jvm);
}


/*
 通知 BLE Stack 获取到了方块数据
 */
- (void)notifyBlockData: (const void *)data device_uuid:(NSString *) uuid{
    if (jvm == NULL) {
        NSLog(@"Could't find JVM to get JNIEnv while notifyBlockData");
        return;
    }

    JNIEnv *env;
    //Checking if the thread is attached.
    int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        NSLog(@"GetEnv: not attached to Java thread!");
        if ((*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL) != 0) {
            NSLog(@"GetEnv: failed to attach!");
        }
    } else if (getEnvStat == JNI_OK) {
        NSLog(@"GetEnv: attached successfully");
    } else if (getEnvStat == JNI_EVERSION) {
        NSLog(@"GetEnv: version not supported");
    }
    
    jmethodID notify_connected_funid = (*env)->GetStaticMethodID(env, jni_ble_class,"notifyBlockData","(Ljava/lang/String;[B)V");
    if (notify_connected_funid == NULL) {
        NSLog(@"Could't get methodid for notifyBlockData(Ljava/lang/String;[B)V while notifyBlockData");
        return;
    }
    jbyteArray bytes = (*env)->NewByteArray(env, 7);
    if (bytes == NULL) {
        NSLog(@"Could't new byte array while notifyBlockData");
        return;
    }
    (*env)->SetByteArrayRegion(env, bytes, 0, 7, data);
    (*env)->CallStaticVoidMethod(env, jni_ble_class, notify_connected_funid, [Controller newJStringFromeNSString:uuid env:env], bytes);
    
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
    }
    
    (*jvm)->DetachCurrentThread(jvm);
}


/*
 通知 BLE Stack 获取到了电量数据
 */
- (void)notifyVoltageData: (const void *)data device_uuid:(NSString *) uuid{
    if (jvm == NULL) {
        NSLog(@"Could't find JVM to get JNIEnv while notifyVoltageData");
        return;
    }
    
    JNIEnv *env;
    //Checking if the thread is attached.
    int getEnvStat = (*jvm)->GetEnv(jvm, (void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        NSLog(@"GetEnv: not attached to Java thread!");
        if ((*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL) != 0) {
            NSLog(@"GetEnv: failed to attach!");
        }
    } else if (getEnvStat == JNI_OK) {
        NSLog(@"GetEnv: attached successfully");
    } else if (getEnvStat == JNI_EVERSION) {
        NSLog(@"GetEnv: version not supported");
    }
    
    jmethodID notify_funid = (*env)->GetStaticMethodID(env, jni_ble_class,"notifyVoltage","(Ljava/lang/String;[B)V");
    if (notify_funid == NULL) {
        NSLog(@"Could't get methodid for notifyVoltage(Ljava/lang/String;F)V while notifyVoltageData");
        return;
    }
    jbyteArray bytes = (*env)->NewByteArray(env, 2);
    if (bytes == NULL) {
        NSLog(@"Could't new byte array while notifyVoltageData");
        return;
    }
    (*env)->SetByteArrayRegion(env, bytes, 0, 2, data);
    (*env)->CallStaticVoidMethod(env, jni_ble_class, notify_funid, [Controller newJStringFromeNSString:uuid env:env], bytes);
    
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
    }
    
    (*jvm)->DetachCurrentThread(jvm);
}
/*
 创建jstring
 */
+ (jstring) newJStringFromeNSString: (NSString *) string env:(JNIEnv *)env{
    int length = [string length];
    
    unichar uniString[length];
    
    [string getCharacters: uniString];
    
    return (*env)->NewString(env, uniString, length);
    
}

+ (float)calcDistByRSSI:(int)rssi
{
    int iRssi = abs(rssi);
    float power = (iRssi-59)/(10*2.0);
    return pow(10, power);
}

/*
 获取蓝牙状态
 */
- (CBCentralManagerState) bluetoothState {
    return [[baby centralManager] state];
}

/*
 蓝牙网关初始化和委托方法设置
 */
- (void)babyDelegate{
    __weak typeof(baby) weakBaby = baby;
    __weak typeof(self) weakSelf = self;
    
    // 设置查找设备的过滤器
    [baby setFilterOnDiscoverPeripherals:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
        // 设置查找规则是名称为LOGITOW
        if ([peripheralName isEqualToString:@"LOGITOW"]) {
            return YES;
        }
        return NO;
    }];
    
    // 连接过滤器
    [baby setFilterOnConnectToPeripherals:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
        // 排除没有数据传送服务服务的
        if (![[advertisementData allKeys] containsObject:@"kCBAdvDataServiceUUIDs"]) return NO;
        if (![[advertisementData objectForKey:@"kCBAdvDataServiceUUIDs"] containsObject:[CBUUID UUIDWithString:@"69400001-b5a3-f393-e0a9-e50e24dcca99"]]) return NO;
        // 连接第一个设备
        NSLog(@"Found logitow devices");
        return YES;
    }];
    
    //设置设备连接成功的委托
    [baby setBlockOnConnected:^(CBCentralManager *central, CBPeripheral *peripheral) {
        // 设置连接成功的block
        NSLog(@"Succeed in connecting to %@ with uuid %s",peripheral.name, [peripheral.identifier UUIDString].UTF8String);
        
        [weakSelf notifyConnected: peripheral.identifier.UUIDString];
    }];
    
    //设置发现设备的Services的委托
    [baby setBlockOnDiscoverServices:^(CBPeripheral *peripheral, NSError *error) {
        for (CBService *service in peripheral.services) {
            NSLog(@"Found Services: %@", service.UUID.UUIDString);
        }
    }];
    
    // 设置发现设service的Characteristics的委托
    [baby setBlockOnDiscoverCharacteristics:^(CBPeripheral *peripheral, CBService *service, NSError *error) {
        NSLog(@"Finding characteristices in Service %@", service.UUID.UUIDString);
        
        if ([service.UUID isEqual:BLOCK_DATA_SERVICE_UUID]) {
            // 数据传送服务
            for (CBCharacteristic *c in service.characteristics) {
                if ([c.UUID isEqual:BLOCK_DATA_CHARACTERISTICS_WRITE_UUID]) {
                    // 写
                }
                if ([c.UUID isEqual:BLOCK_DATA_CHARACTERISTICS_WRITE_UUID]) {
                    // 读
                    NSLog(@"Found characteristice to read block data with UUID %@", c.UUID.UUIDString);
                    [weakBaby cancelNotify:peripheral characteristic:c];
                    [weakBaby notify:peripheral
                      characteristic:c
                               block:^(CBPeripheral *peripheral, CBCharacteristic *characteristics, NSError *error) {
                                   //接收到值会进入这个方法
                                   [weakSelf notifyBlockData:[characteristics.value bytes] device_uuid:peripheral.identifier.UUIDString];
                               }];
                }
            }
        } else if ([service.UUID isEqual:VOLTAGE_SERVICE_UUID]){
            // 模块驱动服务
            for (CBCharacteristic *c in service.characteristics) {
                if ([c.UUID isEqual:VOLTAGE_CHARACTERISTICS_WRITE_UUID]) {
                    // 读
                    NSLog(@"Found characteristice to read voltage with UUID %@", c.UUID.UUIDString);
                    [weakBaby cancelNotify:peripheral characteristic:c];
                    [weakBaby notify:peripheral
                      characteristic:c
                               block:^(CBPeripheral *peripheral, CBCharacteristic *characteristic, NSError *error) {
                                   Byte bytes[2];
                                   [characteristic.value getBytes:&bytes length:2];
                                   if(characteristic.value != NULL) [weakSelf notifyVoltageData:bytes device_uuid:peripheral.identifier.UUIDString];
                               }];
                }
            }
        }
    }];
    
    [baby setBlockOnDisconnect:^(CBCentralManager *central, CBPeripheral *peripheral, NSError *error){
        NSLog(@"Disconnected Device %@", peripheral.identifier.UUIDString);
        [weakSelf notifyDisconnected:true device_uuid:peripheral.identifier.UUIDString];
    }];
}

@end
