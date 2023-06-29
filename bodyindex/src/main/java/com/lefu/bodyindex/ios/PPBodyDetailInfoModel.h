//
//  PPBodyDetailInfoModel.h
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/6/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


typedef NS_ENUM(NSInteger, PPBodyDetailInfoModelType) {
    
    PPBodyDetailInfoModelTypeIngredient,
    PPBodyDetailInfoModelTypeAnalyze,
    PPBodyDetailInfoModelTypeEvaluate,
};

@interface PPBodyDetailInfoModel : NSObject


// 名称
@property (nonatomic,strong) NSString  *bodyParamNameString;

// 对名称的详细说明
@property (nonatomic,strong) NSString  *introductionString;

// 范围节点 [0,18.5,25,30,42]
@property (nonatomic,strong) NSArray  *standardArray;

// 当前值 没有单位
@property (nonatomic,assign) CGFloat currentValue;

// 当前值坐落的区间
@property (nonatomic,assign) NSInteger currentStandard;

// 对节点区间的描述 (瘦,普通,偏胖,肥胖)
@property (nonatomic,strong) NSArray<NSString *>  *standardTitleArray;

@property (nonatomic,strong) NSString  *standardTitle;


// 节点区间的颜色 (#F5A623,#14CCAD,#F43F31,#C23227)
@property (nonatomic,strong) NSArray <NSString *> *colorArray;

@property (nonatomic,strong) NSString  *standColor;

// 对当前所在区间的评价(BMI_leve1_evaluation,BMI_leve2_evaluation,BMI_leve3_evaluation,BMI_leve4_evaluation)
@property (nonatomic,strong) NSArray <NSString *> *evaluationArray;

@property (nonatomic,strong) NSString *standeValuation;

// 对当前所在区间的建议(BMI_leve1_suggestion,BMI_leve2_suggestion,BMI_leve3_suggestion,BMI_leve4_suggestion)
@property (nonatomic,strong) NSArray <NSString *> *suggestionArray;

@property (nonatomic,strong) NSString *standSuggestion;

// 指标的类型
@property (nonatomic,assign) PPBodyDetailInfoModelType type;

// 是否有区间
@property (nonatomic,assign) BOOL hasStandard;

//自定义参数
@property (nonatomic,strong) id  extendParam;

- (instancetype)initWithDic:(NSDictionary *)dic
                 andKeyName:(NSString *)key
              standardArray:(NSArray <NSNumber *>*)standardArray
               currentValue:(CGFloat)currentValue
                hasStandard:(BOOL)hasStandard;

@end

NS_ASSUME_NONNULL_END
