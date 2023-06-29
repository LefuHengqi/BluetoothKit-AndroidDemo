//
//  PPBodyDetailInfoModel.m
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/6/15.
//

#import "PPBodyDetailInfoModel.h"

@implementation PPBodyDetailInfoModel

- (instancetype)initWithDic:(NSDictionary *)dic
                 andKeyName:(NSString *)key
              standardArray:(NSArray <NSNumber *>*)standardArray
               currentValue:(CGFloat)currentValue
                hasStandard:(BOOL)hasStandard
{
    
    self = [super init];
    
    if (self){
        
        NSDictionary *d = [dic valueForKey:key];
        self.suggestionArray = [d valueForKey:@"suggestionArray"];
        self.evaluationArray = [d valueForKey:@"evaluationArray"];
        self.standardTitleArray = [d valueForKey:@"standardTitleArray"];
        self.introductionString = [d valueForKey:@"introductionString"];
        self.bodyParamNameString = [d valueForKey:@"bodyParamNameString"];
        self.colorArray = [d valueForKey:@"colorArray"];
        
        //当前值
        self.currentValue = currentValue;
        
        // 是否有区间标准
        self.hasStandard = hasStandard;
        
        if (hasStandard){
            
            [self fillWithStandardArray:standardArray currentValue:currentValue];

        }
        
    }
    
    return self;
    
}

- (void)fillWithStandardArray:(NSArray <NSNumber *>*)standardArray
                        currentValue:(CGFloat)currentValue{
 
    //范围节点
    self.standardArray = standardArray;
   
    // 当前值坐落的区间
    NSInteger currentStandard = [self calculateCurrentStandardWithValue:currentValue andStandardArray:standardArray];
    self.currentStandard = currentStandard;
    // 对节点区间的描述
    self.standardTitle = [self safeFetchListValueByIndex:currentStandard andList:self.standardTitleArray];
    // 节点区间的颜色
    self.standColor = [self safeFetchListValueByIndex:currentStandard andList:self.colorArray];
    // 对当前所在区间的评价
    self.standSuggestion = [self safeFetchListValueByIndex:currentStandard andList:self.suggestionArray];
    // 对当前所在区间的建议
    self.standeValuation = [self safeFetchListValueByIndex:currentStandard andList:self.evaluationArray];
    
    
}

- (NSInteger)calculateCurrentStandardWithValue:(CGFloat)number andStandardArray:(NSArray *)array{
    
    NSInteger count = array.count;
     
    float first = [[array firstObject] floatValue];
    if (number < first) {
        return 0;
    }
    float last = [[array lastObject] floatValue];
    if (number >= last) {
        return count - 2;
    }
    for (NSInteger i = 1; i < count; i++) {
        float prev = [[array objectAtIndex:i-1] floatValue];
        float current = [[array objectAtIndex:i] floatValue];
        if (number >= prev && number < current) {
           return i - 1;
        }
    }
    return 0;
}

- (NSString *)safeFetchListValueByIndex:(NSInteger)index andList:(NSArray <NSString *>*)list{
    
    if (index < list.count){
        return [list objectAtIndex:index];
    }

    return list.firstObject;
}


@end
