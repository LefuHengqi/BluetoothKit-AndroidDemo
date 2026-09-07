//
//  PPCAInfoModel.h
//  Pods
//
//  Created by lefu on 2026/8/31
//  


#import <Foundation/Foundation.h>


@interface PPCAInfoModel : NSObject
@property (nonatomic, copy) NSString *expirationDate; // 过期时间
@property (nonatomic, copy) NSString *caFingerprint; // 证书指纹
@end

