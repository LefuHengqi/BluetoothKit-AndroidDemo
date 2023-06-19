//
//  PPBodyDetailModel.m
//  PPBluetoothKit
//
//  Created by  lefu on 2023/6/12.
//

#import "PPBodyDetailModel.h"

@interface PPBodyDetailModel()

@property (nonatomic,strong) PPBodyFatModel *fatModel;

@property (nonatomic,strong) NSDictionary *configDic;

@end

@implementation PPBodyDetailModel

- (instancetype)initWithBodyFatModel:(PPBodyFatModel*)fatModel{

    self = [super init];
    if (self)
    
    
    self.fatModel = fatModel;
    
    NSString *filePath = [[NSBundle bundleForClass:[self class]] pathForResource:@"BodyParam" ofType:@"json"];
    NSData *jsonData = [NSData dataWithContentsOfFile:filePath];
    NSError *error;
    NSDictionary *jsonDic = [NSJSONSerialization JSONObjectWithData:jsonData options:kNilOptions error:&error];
    
    self.configDic = jsonDic;
    
    [self fetchAllPPBodyDetailInfo];
    
    return self;
}

+ (NSArray *)sortKeysToDictionary:(NSDictionary *)dictionary{
    
    NSArray *sortedKeys = [dictionary.allKeys sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
        NSNumber *value1 = dictionary[obj1];
        NSNumber *value2 = dictionary[obj2];
        
        if ([value1 integerValue] < [value2 integerValue]) {
            return NSOrderedAscending;
        } else if ([value1 integerValue] > [value2 integerValue]) {
            return NSOrderedDescending;
        } else {
            return NSOrderedSame;
        }
    }];

    
    return sortedKeys;
}


+ (NSArray *)sortValuesToDictionary:(NSDictionary *)dictionary{
    
    NSArray *sortedKeys = [dictionary.allValues sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2) {
     
        
        if ([obj1 integerValue] < [obj2 integerValue]) {
               return NSOrderedAscending;
           } else if ([obj1 integerValue] > [obj2 integerValue]) {
               return NSOrderedDescending;
           } else {
               return NSOrderedSame;
           }
    }];

    return sortedKeys;
}

- (void)fetchAllPPBodyDetailInfo{
    
    self.PPBodyParam_Weight = [self fetchPPBodyParam_Weight];
    self.PPBodyParam_Bodystandard = [self fetchPPBodyParam_Bodystandard];
    self.PPBodyParam_BodyControl = [self fetchPPBodyParam_BodyControl];
    self.PPBodyParam_BodyLBW = [self fetchPPBodyParam_BodyLBW];
    self.PPBodyParam_BodyFat = [self fetchPPBodyParam_BodyFat];
    self.PPBodyParam_BodyFatKg = [self fetchPPBodyParam_BodyFatKg];
    self.PPBodyParam_Water = [self fetchPPBodyParam_Water];
    self.PPBodyParam_MusRate = [self fetchPPBodyParam_MusRate];
    self.PPBodyParam_Mus = [self fetchPPBodyParam_Mus];
    self.PPBodyParam_Bone = [self fetchPPBodyParam_Bone];
    self.PPBodyParam_BMR = [self fetchPPBodyParam_BMR];
    self.PPBodyParam_BMI = [self fetchPPBodyParam_BMI];
    self.PPBodyParam_VisFat = [self fetchPPBodyParam_VisFat];
    self.PPBodyParam_physicalAgeValue = [self fetchPPBodyParam_physicalAgeValue];
    self.PPBodyParam_proteinPercentage = [self fetchPPBodyParam_proteinPercentage];
    self.PPBodyParam_BodyType = [self fetchPPBodyParam_BodyType];
    self.PPBodyParam_BodyScore = [self fetchPPBodyParam_BodyScore];
    self.PPBodyParam_BodySubcutaneousFat = [self fetchPPBodyParam_BodySubcutaneousFat];
    self.PPBodyParam_BodySkeletal = [self fetchPPBodyParam_BodySkeletal];
    self.PPBodyParam_MuscleControl = [self fetchPPBodyParam_MuscleControl];
    self.PPBodyParam_BodyControlLiang = [self fetchPPBodyParam_BodyControlLiang];
    self.PPBodyParam_FatGrade = [self fetchPPBodyParam_FatGrade];
    self.PPBodyParam_BodyHealth = [self fetchPPBodyParam_BodyHealth];
    self.PPBodyParam_heart = [self fetchPPBodyParam_heart];
    self.PPBodyParam_waterKg = [self fetchPPBodyParam_waterKg];
    self.PPBodyParam_proteinKg = [self fetchPPBodyParam_proteinKg];
    self.PPBodyParam_bodyFatSubCutKg = [self fetchPPBodyParam_bodyFatSubCutKg];
    self.PPBodyParam_cellMassKg = [self fetchPPBodyParam_cellMassKg];
    self.PPBodyParam_dCI = [self fetchPPBodyParam_dCI];
    self.PPBodyParam_mineralKg = [self fetchPPBodyParam_mineralKg];
    self.PPBodyParam_obesity = [self fetchPPBodyParam_obesity];
    self.PPBodyParam_waterECWKg = [self fetchPPBodyParam_waterECWKg];
    self.PPBodyParam_waterICWKg = [self fetchPPBodyParam_waterICWKg];
    self.PPBodyParam_bodyFatKgLeftArm = [self fetchPPBodyParam_bodyFatKgLeftArm];
    self.PPBodyParam_bodyFatKgLeftLeg = [self fetchPPBodyParam_bodyFatKgLeftLeg];
    self.PPBodyParam_bodyFatKgRightArm = [self fetchPPBodyParam_bodyFatKgRightArm];
    self.PPBodyParam_bodyFatKgRightLeg = [self fetchPPBodyParam_bodyFatKgRightLeg];
    self.PPBodyParam_bodyFatKgTrunk = [self fetchPPBodyParam_bodyFatKgTrunk];
    self.PPBodyParam_bodyFatRateLeftArm = [self fetchPPBodyParam_bodyFatRateLeftArm];
    self.PPBodyParam_bodyFatRateLeftLeg = [self fetchPPBodyParam_bodyFatRateLeftLeg];
    self.PPBodyParam_bodyFatRateRightArm = [self fetchPPBodyParam_bodyFatRateRightArm];
    self.PPBodyParam_bodyFatRateRightLeg = [self fetchPPBodyParam_bodyFatRateRightLeg];
    self.PPBodyParam_bodyFatRateTrunk = [self fetchPPBodyParam_bodyFatRateTrunk];
    self.PPBodyParam_muscleKgLeftArm = [self fetchPPBodyParam_muscleKgLeftArm];
    self.PPBodyParam_muscleKgLeftLeg = [self fetchPPBodyParam_muscleKgLeftLeg];
    self.PPBodyParam_muscleKgRightArm = [self fetchPPBodyParam_muscleKgRightArm];
    self.PPBodyParam_muscleKgRightLeg = [self fetchPPBodyParam_muscleKgRightLeg];
    self.PPBodyParam_muscleKgTrunk = [self fetchPPBodyParam_muscleKgTrunk];

}


#pragma mark - property

static void weightCalculate(PPBodyDetailModel *object, CGFloat *currentValue, NSArray **standardArray) {
    

}


#pragma mark -体重

- (PPBodyDetailInfoModel *)fetchPPBodyParam_Weight{
    
    NSArray *standardArray = [self fetchPPBodyParam_Weight_StandartArray];
    CGFloat currentValue = self.fatModel.ppWeightKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_Weight" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -标准体重

- (PPBodyDetailInfoModel *)fetchPPBodyParam_Bodystandard{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyStandardWeightKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_Bodystandard" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -体重控制
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyControl{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppControlWeightKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyControl" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -去脂体重
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyLBW{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppLoseFatWeightKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyLBW" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -体脂率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyFat{
    
    NSArray *standardArray = [self fetchPPBodyParam_BodyFat_StandartArray];
    CGFloat currentValue = self.fatModel.ppFat;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyFat" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyFatKg{
    
    //使用脂肪率转换
    NSArray *kgArray = [self fetchPPBodyParam_BodyFat_StandartArray];

    NSArray *standardArray = [self percent2kg:kgArray];
    CGFloat currentValue = self.fatModel.ppBodyfatKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyFatKg" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -水分率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_Water{
    
    NSArray *standardArray = [self fetchPPBodyParam_Water_StandartArray];
    CGFloat currentValue = self.fatModel.ppWaterPercentage;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_Water" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
    
}

#pragma mark -肌肉率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_MusRate{
    
    //使用肌肉量转换
    NSArray *percentArr = [self fetchPPBodyParam_Mus_StandartArray];

    NSArray *standardArray = [self percent2kg:percentArr];
    CGFloat currentValue = self.fatModel.ppMusclePercentage;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_MusRate" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_Mus{
    
    NSArray *standardArray = [self fetchPPBodyParam_Mus_StandartArray];
    CGFloat currentValue = self.fatModel.ppMuscleKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_Mus" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -骨量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_Bone{
    
    NSArray *standardArray = [self fetchPPBodyParam_Bone_StandartArray];
    CGFloat currentValue = self.fatModel.ppBoneKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_Bone" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -BMR
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BMR{
  
    NSArray *standardArray = [self fetchPPBodyParam_BMR_StandartArray];
    CGFloat currentValue = self.fatModel.ppBMR;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BMR" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -BMI
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BMI{

    NSArray *standardArray = [self fetchPPBodyParam_BMI_StandartArray];
    CGFloat currentValue = self.fatModel.ppBMI;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BMI" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -内脏脂肪等级
- (PPBodyDetailInfoModel *)fetchPPBodyParam_VisFat{

    NSArray *standardArray = [self fetchPPBodyParam_VisFat_StandartArray];
    CGFloat currentValue = self.fatModel.ppVisceralFat;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_VisFat" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -身体年龄
- (PPBodyDetailInfoModel *)fetchPPBodyParam_physicalAgeValue{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyAge;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_physicalAgeValue" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -蛋白质率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_proteinPercentage{

    NSArray *standardArray = [self fetchPPBodyParam_proteinPercentage_StandartArray];
    CGFloat currentValue = self.fatModel.ppProteinPercentage;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_proteinPercentage" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}


#pragma mark -身体类型
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyType{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyType;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyType" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -身体得分
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyScore{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyScore;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyScore" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -皮下脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodySubcutaneousFat{

    NSArray *standardArray = [self fetchPPBodyParam_BodySubcutaneousFat_StandartArray];
    CGFloat currentValue = self.fatModel.ppVfPercentageKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodySubcutaneousFat" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -骨骼肌率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodySkeletal{
   
    NSArray *standardArray = [self fetchPPBodyParam_BodySkeletal_StandartArray];
    CGFloat currentValue = self.fatModel.ppBodySkeletal;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodySkeletal" standardArray:standardArray currentValue:currentValue hasStandard:YES];

    return infoModel;
}

#pragma mark -肌肉控制量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_MuscleControl{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyMuscleControl;
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_MuscleControl" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -脂肪控制量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyControlLiang{
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppFatControlKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyControlLiang" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -肥胖等级
- (PPBodyDetailInfoModel *)fetchPPBodyParam_FatGrade{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppFatGrade;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_FatGrade" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -健康评估
- (PPBodyDetailInfoModel *)fetchPPBodyParam_BodyHealth{
    
  
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyHealth;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_BodyHealth" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
    
}


#pragma mark -心率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_heart{
    
    NSArray *standardArray = [self fetchPPBodyParam_heart_StandartArray];
    CGFloat currentValue = self.fatModel.ppHeartRate;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_heart" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -体水分量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_waterKg{
    
    NSArray *percentArr = [self fetchPPBodyParam_Water_StandartArray];

    NSArray *standardArray = [self percent2kg:percentArr];
    CGFloat currentValue = self.fatModel.ppWaterKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_waterKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}

#pragma mark -蛋白质量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_proteinKg{
    
    NSArray *percentArr = [self fetchPPBodyParam_proteinPercentage_StandartArray];
    
    NSArray *standardArray = [self percent2kg:percentArr];
    CGFloat currentValue = self.fatModel.ppProteinKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_proteinKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -皮下脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatSubCutKg{
    
    NSArray *percentArr = [self fetchPPBodyParam_BodySubcutaneousFat_StandartArray];

    NSArray *standardArray = [self percent2kg:percentArr];
    CGFloat currentValue = self.fatModel.ppBodyFatSubCutKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatSubCutKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -身体细胞量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_cellMassKg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppCellMassKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_cellMassKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -建议卡路里摄入量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_dCI{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppDCI;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_dCI" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -无机盐量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_mineralKg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMineralKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_mineralKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}

#pragma mark - 肥胖度
- (PPBodyDetailInfoModel *)fetchPPBodyParam_obesity{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppObesity;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_obesity" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -细胞外水量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_waterECWKg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppWaterECWKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_waterECWKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -细胞内水量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_waterICWKg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppWaterICWKg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_waterICWKg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左手脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatKgLeftArm{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatKgLeftArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatKgLeftArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左脚脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatKgLeftLeg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatKgLeftLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatKgLeftLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右手脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatKgRightArm{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatKgRightArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatKgRightArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右脚脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatKgRightLeg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatKgRightLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatKgRightLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -躯干脂肪量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatKgTrunk{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatKgTrunk;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatKgTrunk" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左手脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatRateLeftArm{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatRateLeftArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatRateLeftArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左脚脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatRateLeftLeg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatRateLeftLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatRateLeftLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右手脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatRateRightArm{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatRateRightArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatRateRightArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右脚脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatRateRightLeg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatRateRightLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatRateRightLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -躯干脂肪率
- (PPBodyDetailInfoModel *)fetchPPBodyParam_bodyFatRateTrunk{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppBodyFatRateTrunk;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_bodyFatRateTrunk" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左手肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_muscleKgLeftArm{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMuscleKgLeftArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_muscleKgLeftArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -左脚肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_muscleKgLeftLeg{

    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMuscleKgLeftLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_muscleKgLeftLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右手肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_muscleKgRightArm{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMuscleKgRightArm;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_muscleKgRightArm" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark -右脚肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_muscleKgRightLeg{
  
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMuscleKgRightLeg;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_muscleKgRightLeg" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
    
}

#pragma mark -躯干肌肉量
- (PPBodyDetailInfoModel *)fetchPPBodyParam_muscleKgTrunk{
    
    NSArray *standardArray = @[];
    CGFloat currentValue = self.fatModel.ppMuscleKgTrunk;
    
    PPBodyDetailInfoModel *infoModel = [[PPBodyDetailInfoModel alloc] initWithDic:self.configDic andKeyName:@"BodyParam_muscleKgTrunk" standardArray:standardArray currentValue:currentValue hasStandard:NO];

    return infoModel;
}


#pragma mark - StandartArray

- (NSArray *)fetchPPBodyParam_Weight_StandartArray{
    
    NSArray *standardArray = @[];
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    NSInteger height = self.fatModel.ppHeightCm;
    
    CGFloat standValue;
    if (gender == PPDeviceGenderTypeMale) {
        
        standValue = (height - 80) * 0.7;
    }else{
        
        standValue = (height - 70) * 0.6;
    }
    CGFloat point0 = standValue * 0.7f;
    CGFloat point1 = standValue * 0.9f;
    CGFloat point2 = standValue * 1.1f;
    CGFloat point3 = standValue * 1.3f;
    
    standardArray = @[@(point0),@(point1),@(point2),@(point3)];
    return standardArray;
}


- (NSArray *)fetchPPBodyParam_BodyFat_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    NSInteger age = self.fatModel.ppAge;
    NSArray *standardArray = @[];
    if (gender == PPDeviceGenderTypeMale){
        
        if (age <= 7 ){
            
            standardArray = @[@(0),@(7),@(16),@(25),@(30),@(35)];
        }else if (age <= 11){
            
            standardArray = @[@(0),@(7),@(16),@(26),@(30),@(35)];
        }else if (age <= 13){
            
            standardArray = @[@(0),@(7),@(16),@(25),@(30),@(35)];
        }else if (age <= 14){

            standardArray = @[@(0),@(7),@(15),@(25),@(29),@(34)];
        }else if (age <= 15){
            
            standardArray = @[@(0),@(8),@(15),@(24),@(29),@(34)];
        }else if (age <= 16){
            
            standardArray = @[@(0),@(8),@(16),@(24),@(28),@(33)];
        }else if (age <= 17){
            
            standardArray = @[@(0),@(9),@(16),@(23),@(28),@(33)];
        }else if (age <= 39){
            
            standardArray = @[@(0),@(11),@(17),@(22),@(27),@(32)];
        }else if (age <= 59){
            
            standardArray = @[@(0),@(12),@(18),@(23),@(28),@(33)];
        }else{
            
            standardArray = @[@(0),@(14),@(20),@(25),@(30),@(35)];
        }
        
    }else{
        if (age <= 6 ){
            
            standardArray = @[@(0),@(8),@(16),@(25),@(29),@(34)];
        }else if (age <= 7){
            
            standardArray = @[@(0),@(9),@(17),@(25),@(30),@(35)];
        }else if (age <= 8){
            
            standardArray = @[@(0),@(10),@(18),@(26),@(31),@(36)];
        }else if (age <= 9){

            standardArray = @[@(0),@(10),@(19),@(28),@(32),@(37)];
        }else if (age <= 10){
            
            standardArray = @[@(0),@(11),@(20),@(29),@(33),@(3)];
        }else if (age <= 11){
            
            standardArray = @[@(0),@(13),@(22),@(31),@(35),@(40)];
        }else if (age <= 12){
            
            standardArray = @[@(0),@(14),@(23),@(32),@(36),@(41)];
        }else if (age <= 13){

            standardArray = @[@(0),@(15),@(25),@(34),@(38),@(43)];
        }else if (age <= 14){
            
            standardArray = @[@(0),@(17),@(26),@(35),@(39),@(44)];
        }else if (age <= 15){
            
            standardArray = @[@(0),@(18),@(27),@(36),@(40),@(45)];
        }else if (age <= 16){
            
            standardArray = @[@(0),@(19),@(28),@(37),@(41),@(46)];
        }else if (age <= 17){
            
            standardArray = @[@(0),@(20),@(28),@(37),@(41),@(46)];
        }else if (age <= 39){
            
            standardArray = @[@(0),@(21),@(28),@(35),@(40),@(45)];
        }else if (age <= 59){
            
            standardArray = @[@(0),@(22),@(29),@(36),@(41),@(46)];
        }else{
            
            standardArray = @[@(0),@(23),@(30),@(37),@(42),@(47)];
        }
        
    }
    
    return standardArray;
}


- (NSArray *)fetchPPBodyParam_Water_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    if (gender == PPDeviceGenderTypeMale){
        
        return @[@(45),@(55),@(65),@(75)];
    }else{
        return @[@(30),@(45),@(60),@(75)];
        
    }
}

- (NSArray *)fetchPPBodyParam_Mus_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    NSInteger height = self.fatModel.ppHeightCm;
    CGFloat standValue = 0;
    CGFloat difference = 0;
    if (gender == PPDeviceGenderTypeMale) {
        if (height < 160) {
            
            standValue = 42.5;
            difference = 4.0;
            
        }else if (height > 170){
            
            standValue = 54.4;
            difference = 5.0;
            
        }else{
            
            standValue = 48.2;
            difference = 4.2;

        }
    }
    else{
        if (height < 150) {
            
            standValue = 31.9;
            difference = 2.8;

        }else if (height > 160){
            
            standValue = 39.5;
            difference = 3.0;

        }else{
     
            standValue = 35.2;
            difference = 2.3;
        }
    }
    
    CGFloat point0 = standValue - difference * 3;
    CGFloat point1 = standValue - difference;
    CGFloat point2 = standValue + difference;
    CGFloat point3 = standValue + difference * 3;
    
    return @[@(point0),@(point1),@(point2),@(point3)];
}

- (NSArray *)fetchPPBodyParam_Bone_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    CGFloat weight  = self.fatModel.ppWeightKg;
    
    CGFloat standValue = 0;
    CGFloat difference = 0.1;
    if (gender == PPDeviceGenderTypeMale) {
        
        if (weight < 60.0) {
            
            standValue = 2.5;
        }else if (weight > 75.0){
            
            standValue = 3.2;
            
        }else{
            standValue = 2.9;
        }
    }
    else{
        if (weight < 45.0) {
            
            standValue = 1.8;
            
        }else if (weight > 60.0){
            
            standValue = 2.5;
            
        }else{
            standValue = 2.2;
        }
    }
    
    CGFloat point0 = standValue - difference * 3;
    CGFloat point1 = standValue - difference;
    CGFloat point2 = standValue + difference;
    CGFloat point3 = standValue + difference * 3;
    
    return @[@(point0),@(point1),@(point2),@(point3)];
}

- (NSArray *)fetchPPBodyParam_BMR_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    CGFloat weight  = self.fatModel.ppWeightKg;
    CGFloat age  = self.fatModel.ppAge;

    CGFloat standValue = 0;

    if (gender == PPDeviceGenderTypeMale) {
        
        if (age < 29) {
            
            standValue = weight * 24;
        }
        else if (age < 49){
            
            standValue = weight * 22.3;
        }
        else if (age < 69){
            
            standValue = weight * 21.5;
        }
        else{
            
            standValue = weight * 21.5;
        }
    }
    else{
        if (age < 29) {
            
            standValue = weight * 23.6;
        }
        else if (age < 49){
            
            standValue = weight * 21.7;
        }
        else if (age < 69){
            
            standValue = weight * 20.7;
        }
        else{
            standValue = weight * 20.7;
        }
    }
    
    CGFloat point0 = 0;
    CGFloat point1 = standValue;
    CGFloat point2 = standValue * 2;
  
    
    return @[@(point0),@(point1),@(point2)];
}


- (NSArray *)fetchPPBodyParam_BMI_StandartArray{
    
    return @[@(13),@(18.5),@(25),@(30),@(35)];

}

- (NSArray *)fetchPPBodyParam_VisFat_StandartArray{
    
    return @[@(0),@(10),@(15),@(25)];

}

- (NSArray *)fetchPPBodyParam_proteinPercentage_StandartArray{
    
    return @[@(14),@(16),@(18),@(20)];

}


- (NSArray *)fetchPPBodyParam_BodySubcutaneousFat_StandartArray{
    
    PPDeviceGenderType gender = self.fatModel.ppSex;
    if (gender == PPDeviceGenderTypeMale){
        
        return @[@(0),@(8.6),@(16.7),@(24.8)];
    }else{
        
        return @[@(10.3),@(18.5),@(26.7),@(34.9)];
    }
}


- (NSArray *)fetchPPBodyParam_BodySkeletal_StandartArray{
    
    return @[@(5),@(20),@(35),@(50)];

}

- (NSArray *)fetchPPBodyParam_heart_StandartArray{
    
    return @[@(0),@(40),@(60),@(100),@(160),@(180)];

}


- (NSArray *)percent2kg:(NSArray *)source{
    
    NSMutableArray *temp = @[].mutableCopy;
    for (NSNumber *n in source) {
        
        CGFloat kg = self.fatModel.ppWeightKg * n.floatValue;
        [temp addObject:@(kg)];
    }
    
    NSArray *standardArray = temp.copy;
    return standardArray;
}

- (NSArray *)kg2percent:(NSArray *)source{
    
    NSMutableArray *temp = @[].mutableCopy;
    for (NSNumber *n in source) {
        
        CGFloat percent = n.floatValue / self.fatModel.ppWeightKg * 100;
        [temp addObject:@(percent)];
    }
    
    NSArray *standardArray = temp.copy;
    return standardArray;
}

@end
