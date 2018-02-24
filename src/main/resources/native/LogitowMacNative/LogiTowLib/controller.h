//
//  controller.h
//  LogiTowLib
//
//  Created by Trychen on 17/12/10.
//
//

#include "BabyBluetooth.h"
#import <JavaVM/jni.h>

#ifndef controller_h
#define controller_h

@interface Controller : NSObject

@property BabyBluetooth *baby;
@property JavaVM *jvm;
@property jclass jni_ble_class;

+ (instancetype) sharedController;

- (void) startScan;

- (void) stopScan;

- (bool) connect: (NSString *) deviceUUID;

- (void) disconnect: (NSString *) deviceUUID;

- (CBCentralManagerState) bluetoothState;

- (void) setupJNI:(JNIEnv)env ble_class:(jclass) class;

- (BOOL) writeToGetVoltage: (NSString *) deviceUUID;

@end

#endif /* controller_h */
