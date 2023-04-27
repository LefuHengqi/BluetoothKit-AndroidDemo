package com.ppscale.data_range.vo;

import android.content.Context;

import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.util.UserUtil;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.ppscale.data_range.R;
import com.ppscale.data_range.util.BodyAdviceUtil;
import com.ppscale.data_range.util.BodyLevelUtils;
import com.ppscale.data_range.util.FatDescribeUtil;
import com.ppscale.data_range.util.StripedStand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PPBodyFatDetailModel {

    public final static String FLAG_WEIGHTKG = "flagWeightKg";
    public static final String FLAG_BMI = "flagBMI";
    public static final String FLAG_BODYFAT_PERCENTAGE = "flagBodyfatPercentage";
    public static final String FLAG_MUSCLE_KG = "flagMuscleKg";
    public static final String FLAG_WATER_PERCENTAGE = "flagWaterPercentage";
    public static final String FLAG_VFAL = "flagVFAL";
    public static final String FLAG_BONE_KG = "flagBoneKg";
    public static final String FLAG_BMR = "flagBMR";
    public static final String FLAG_VF_PERCENTAGE = "flagVFPercentage";
    public static final String FLAG_BODY_AGE = "flagBodyAge";
    public static final String FLAG_BODY_SCORE = "flagBodyScore";
    public static final String FLAG_LOSE_FAT_WEIGHT_KG = "flagLoseFatWeightKg";
    public static final String FLAG_BODY_TYPE = "flagBodyType";
    public static final String FLAG_BODYSTANDARD = "flagBodystandard";
    public static final String FLAG_BODYFAT_KG = "flagBodyfatKg";
    public static final String FLAG_PROTEIN_PERCENTAGE = "flagProteinPercentage";
    public static final String FLAG_FAT_GRADE = "flagFatGrade";
    public static final String FLAG_HEARTRATE = "flagheartRate";

    Map<String, BodyItem> bodyItems = new HashMap<>();

    public PPBodyFatDetailModel(Context context, PPBodyFatModel bodyFat, PPUnitType unitType, int accuracyType) {
        initBodyIndex(context, bodyFat, unitType, accuracyType);
    }

    public Map<String, BodyItem> getBodyItems() {
        return bodyItems;
    }

    /**
     * flagWeightKg; || 体重
     * flagBMI; || BMI 人体质量指数, 分辨率0.1, 范围10.0 ~ 90.0
     * flagBodyfatPercentage; || 脂肪率(%), 分辨率0.1, 范围5.0% ~ 75.0%
     * flagMuscleKg; || 肌肉量(kg), 分辨率0.1, 范围10.0 ~ 120.0
     * flagWaterPercentage; || 水分率(%), 分辨率0.1, 范围35.0% ~ 75.0%
     * flagVFAL; || Visceral fat area leverl内脏脂肪, 分辨率1, 范围1 ~ 60
     * flagBoneKg; || 骨量(kg), 分辨率0.1, 范围0.5 ~ 8.0
     * flagBMR; || Basal Metabolic Rate基础代谢, 分辨率1, 范围500 ~ 10000
     * flagProteinPercentage; || 蛋白质,分辨率0.1, 范围2.0% ~ 30.0%
     * flagVFPercentage; || 皮下脂肪(%)
     * flagFatGrade; || 肥胖等级
     * flagBodyfatKg; || 脂肪量(kg)
     * flagLoseFatWeightKg; || 去脂体重(kg)
     * flagBodyAge; || 身体年龄,6~99岁
     * flagBodyType; || 身体类型
     *
     * @param context
     * @param bodyFat
     * @return
     */
    private void initBodyIndex(Context context, PPBodyFatModel bodyFat, PPUnitType unitType, int accuracyType) {

        int sex = UserUtil.getEnumSex(bodyFat.getPpSex());
        int height = (int) bodyFat.getPpHeightCm();
        int age = bodyFat.getPpAge();

        //0.体重
        BodyItem weight = new BodyItem();
        weight.setId(0);
        weight.setName(context.getString(R.string.weight));
        weight.setCode(FLAG_WEIGHTKG);
        int index = StripedStand.weightLevel(sex, height, bodyFat.getPpWeightKg());
        weight.setLevelIndex(index);
        weight.setDataVal(PPUtil.getWeight(unitType, bodyFat.getPpWeightKg(), accuracyType));
        weight.setValue(PPUtil.getWeightValue(unitType, bodyFat.getPpWeightKg(), accuracyType));
        weight.setUnit(PPUtil.getWeightUnit(unitType));
        FatDescribeEntity weightAdvice = BodyAdviceUtil.getWeightAdvice(index);
        weight.setSide(context.getString(weightAdvice.side));
        weight.setAdvice("");//体重没有妙招
        List<FatLevelEntity> weightList = BodyLevelUtils.getWeightList(context, sex, height);
        weight.setLevel(weightList.get(index).levelName);
        weight.setLevelEntitys(weightList);
        bodyItems.put(FLAG_WEIGHTKG, weight);

        //16.心率
        BodyItem heartRate = new BodyItem();
        heartRate.setId(16);
        heartRate.setName(context.getString(R.string.heart_name));
        heartRate.setCode(FLAG_HEARTRATE);
        int indexHeartRate = StripedStand.heartRateLevelInt(bodyFat.getPpHeartRate());
        heartRate.setLevelIndex(indexHeartRate);
        heartRate.setDataVal(String.valueOf(bodyFat.getPpHeartRate()));
        heartRate.setValue(bodyFat.getPpHeartRate());
        heartRate.setUnit("");
        FatDescribeEntity weightAdviceHeartRate = BodyAdviceUtil.getHeartAdvice(indexHeartRate);
        heartRate.setSide(context.getString(weightAdviceHeartRate.side));
        heartRate.setAdvice(context.getString(weightAdviceHeartRate.advice));
        List<FatLevelEntity> weightListHeartRate = BodyLevelUtils.getHeartList(context);
        heartRate.setLevel(weightListHeartRate.get(indexHeartRate).levelName);
        heartRate.setLevelEntitys(weightListHeartRate);
        bodyItems.put(FLAG_HEARTRATE, heartRate);

        //1.BMI(身体健康指数)
        BodyItem bmi = new BodyItem();
        bmi.setCode(FLAG_BMI);
        double lfBMI = bodyFat.getPpBMI();
        bmi.setName(context.getString(R.string.bmi));
        int indexBmi = StripedStand.bmiString(lfBMI);
        bmi.setLevelIndex(indexBmi);
        bmi.setDataVal(String.format(Locale.US, "%.1f", lfBMI));
        bmi.setValue(PPUtil.keepPoint1(lfBMI));
        bmi.setUnit("");
        FatDescribeEntity weightAdviceBmi = BodyAdviceUtil.getBmiAdvice(indexBmi);
        bmi.setSide(context.getString(weightAdviceBmi.side));
        bmi.setAdvice(context.getString(weightAdviceBmi.advice));
        List<FatLevelEntity> weightListBmi = BodyLevelUtils.getBMIList(context);
        bmi.setLevel(weightListBmi.get(indexBmi).levelName);
        bmi.setLevelEntitys(weightListBmi);
        bodyItems.put(FLAG_BMI, bmi);

        //2.脂肪率
        BodyItem fat = new BodyItem();
        fat.setCode(FLAG_BODYFAT_PERCENTAGE);
        double lfFat = bodyFat.getPpBodyfatPercentage();
        fat.setName(context.getString(R.string.bodyFat));
        int indexFat = StripedStand.bftInt(sex, age, lfFat);
        fat.setLevelIndex(indexFat);
        fat.setDataVal(String.format(Locale.US, "%.1f", lfFat));
        fat.setValue(PPUtil.keepPoint1(lfFat));
        fat.setUnit("%");
        FatDescribeEntity weightAdviceFat = BodyAdviceUtil.getFatAdvice(indexFat);
        fat.setSide(context.getString(weightAdviceFat.side));
        fat.setAdvice(context.getString(weightAdviceFat.advice));
        List<FatLevelEntity> weightListFat = BodyLevelUtils.getFatList(context, sex, age);
        fat.setLevel(weightListFat.get(indexFat).levelName);
        fat.setLevelEntitys(weightListFat);
        bodyItems.put(FLAG_BODYFAT_PERCENTAGE, fat);

        //3.肌肉含量kg
        BodyItem muscle = new BodyItem();
        muscle.setCode(FLAG_MUSCLE_KG);
        muscle.setUnit("kg");
        muscle.setName(context.getString(R.string.muscleMass));
        muscle.setCode(FLAG_WEIGHTKG);
        int indexMuscle = StripedStand.muscleString(sex, height, bodyFat.getPpMuscleKg());
        muscle.setLevelIndex(indexMuscle);
        muscle.setDataVal(PPUtil.getWeight(unitType, bodyFat.getPpMuscleKg()));
        muscle.setValue(PPUtil.getWeightValue(unitType, bodyFat.getPpMuscleKg()));
        muscle.setUnit(PPUtil.getWeightUnit(unitType));
        FatDescribeEntity weightAdviceMuscle = BodyAdviceUtil.getMuscleAdvice(indexMuscle);
        muscle.setSide(context.getString(weightAdviceMuscle.side));
        muscle.setAdvice(context.getString(weightAdviceMuscle.advice));//体重没有妙招
        List<FatLevelEntity> weightListMuscle = BodyLevelUtils.getWeightList(context, sex, height);
        muscle.setLevel(weightListMuscle.get(indexMuscle).levelName);
        muscle.setLevelEntitys(weightListMuscle);
        bodyItems.put(FLAG_MUSCLE_KG, muscle);


        //4.水分率
        BodyItem water = new BodyItem();
        water.setCode(FLAG_WATER_PERCENTAGE);
        water.setUnit("%");
        double lfWater = bodyFat.getPpWaterPercentage();
        water.setName(context.getString(R.string.BodyMoistureRate));
        int indexWater = StripedStand.water(sex, lfWater);
        water.setLevelIndex(indexWater);
        water.setDataVal(String.format(Locale.US, "%.1f", lfFat));
        water.setValue(PPUtil.keepPoint1(lfFat));
        FatDescribeEntity weightAdviceWater = BodyAdviceUtil.getWaterAdvice(indexWater);
        water.setSide(context.getString(weightAdviceWater.side));
        water.setAdvice(context.getString(weightAdviceWater.advice));
        List<FatLevelEntity> weightListWater = BodyLevelUtils.getFatList(context, sex, age);
        water.setLevel(weightListWater.get(indexWater).levelName);
        water.setLevelEntitys(weightListWater);

        bodyItems.put(FLAG_WATER_PERCENTAGE, water);

        //5.内脏脂肪等级
        BodyItem vfal = new BodyItem();
        vfal.setName(context.getString(R.string.visceralFat));
        vfal.setCode(FLAG_VFAL);
        vfal.setDataVal(bodyFat.getPpVFAL() + "");
        vfal.setUnit("%");
        double lfVFAL = bodyFat.getPpVFAL();
        int indexVFAL = StripedStand.visceralFatInt(lfVFAL);
        vfal.setLevelIndex(indexVFAL);
        vfal.setValue(PPUtil.keepPoint1(lfFat));
        FatDescribeEntity weightAdviceVFAL = BodyAdviceUtil.getVFALAdvice(indexVFAL);
        vfal.setSide(context.getString(weightAdviceVFAL.side));
        vfal.setAdvice(context.getString(weightAdviceVFAL.advice));
        List<FatLevelEntity> weightListVFAL = BodyLevelUtils.getFatList(context, sex, age);
        vfal.setLevel(weightListVFAL.get(indexVFAL).levelName);
        vfal.setLevelEntitys(weightListVFAL);

        bodyItems.put(FLAG_VFAL, vfal);

        //6.骨量含量
        BodyItem bone = new BodyItem();
        bone.setCode(FLAG_BONE_KG);
        bone.setName(context.getString(R.string.bone_mass));

        double lfBone = bodyFat.getPpBoneKg();
        bone.setDataVal(PPUtil.getWeight(unitType, lfBone));
        bone.setValue(PPUtil.getWeightValue(unitType, lfBone));
        bone.setUnit(PPUtil.getWeightUnit(unitType));
        int indexBone = StripedStand.boneInt(sex, bodyFat.getPpWeightKg(), age);
        bone.setLevelIndex(indexBone);
        FatDescribeEntity weightAdviceBone = BodyAdviceUtil.getBoneAdvice(indexBone);
        bone.setSide(context.getString(weightAdviceBone.side));
        bone.setAdvice(context.getString(weightAdviceBone.advice));
        List<FatLevelEntity> weightListBone = BodyLevelUtils.getBoneList(context, bodyFat.getPpWeightKg(), sex);
        bone.setLevel(weightListBone.get(indexBone).levelName);
        bone.setLevelEntitys(weightListBone);

        bodyItems.put(FLAG_BONE_KG, bone);

        //7.BMR(基础代谢)
        BodyItem bmr = new BodyItem();
        bmr.setCode(FLAG_BMR);
        int lfBmr = bodyFat.getPpBMR();
        bmr.setDataVal(lfBmr + "");
        bmr.setUnit("kcal");
        bmr.setName(context.getString(R.string.basicMetabolism));
        bmr.setValue(PPUtil.getWeightValue(unitType, lfBmr));
        int indexBmr = StripedStand.bmrInt(sex, age, bodyFat.getPpWeightKg(), lfBmr);
        bmr.setLevelIndex(indexBmr);
        FatDescribeEntity weightAdviceBmr = BodyAdviceUtil.getBMRAdvice(indexBmr);
        bmr.setSide(context.getString(weightAdviceBmr.side));
        bmr.setAdvice(context.getString(weightAdviceBmr.advice));
        List<FatLevelEntity> weightListBmr = BodyLevelUtils.getBMRList(context, sex, age, bodyFat.getPpWeightKg(), lfBmr);
        bmr.setLevel(weightListBmr.get(indexBmr).levelName);
        bmr.setLevelEntitys(weightListBmr);

        bodyItems.put(FLAG_BMR, bmr);

        //8.蛋白质含量
        BodyItem protein = new BodyItem();
        protein.setCode(FLAG_PROTEIN_PERCENTAGE);
        protein.setUnit("%");

        double lfProtein = bodyFat.getPpProteinPercentage();
        protein.setDataVal(String.format(Locale.UK, "%.1f", lfProtein));
        protein.setName(context.getString(R.string.protein));
        protein.setValue(PPUtil.getWeightValue(unitType, lfProtein));
        int indexProtein = StripedStand.proteinInt(lfProtein);
        protein.setLevelIndex(indexProtein);
        FatDescribeEntity weightAdviceProtein = BodyAdviceUtil.getProteinAdvice(indexProtein);
        protein.setSide(context.getString(weightAdviceProtein.side));
        protein.setAdvice(context.getString(weightAdviceProtein.advice));
        List<FatLevelEntity> weightListProtein = BodyLevelUtils.getProteinList(context);
        protein.setLevel(weightListProtein.get(indexProtein).levelName);
        protein.setLevelEntitys(weightListProtein);

        bodyItems.put(FLAG_PROTEIN_PERCENTAGE, protein);

        //9.肥胖等级
        BodyItem obeLevel = new BodyItem();
        obeLevel.setCode(FLAG_FAT_GRADE);
        obeLevel.setUnit("");

        double lfObeLevel = bodyFat.getPpBMI();
        obeLevel.setDataVal(String.format(Locale.UK, "%.1f", lfObeLevel));
        obeLevel.setName(context.getString(R.string.obesityLevels));
        obeLevel.setValue(PPUtil.getWeightValue(unitType, lfObeLevel));
        int indexObeLevel = StripedStand.obsInt(lfObeLevel);
        obeLevel.setLevelIndex(indexObeLevel);
        FatDescribeEntity weightAdviceObeLevel = BodyAdviceUtil.getObsAdvice(indexObeLevel);
        obeLevel.setSide(context.getString(weightAdviceObeLevel.side));
        obeLevel.setAdvice(context.getString(weightAdviceObeLevel.advice));
        List<FatLevelEntity> weightListObeLevel = BodyLevelUtils.getObsLevelList(context);
        obeLevel.setLevel(weightListObeLevel.get(indexObeLevel).levelName);
        obeLevel.setLevelEntitys(weightListObeLevel);
        bodyItems.put(FLAG_FAT_GRADE, obeLevel);

        //10.皮下脂肪率
        BodyItem subFat = new BodyItem();
        subFat.setCode(FLAG_VF_PERCENTAGE);
        subFat.setUnit("%");
        double lfSubFat = bodyFat.getPpVFPercentage();
        subFat.setDataVal(String.format(Locale.UK, "%.1f", lfSubFat));
        subFat.setName(context.getString(R.string.subcutaneousFat));
        subFat.setValue(PPUtil.getWeightValue(unitType, lfSubFat));
        int indexObeSubFat = StripedStand.subFatInt(sex, lfSubFat);
        subFat.setLevelIndex(indexObeSubFat);
        FatDescribeEntity weightAdviceSubFat = BodyAdviceUtil.getSubFatAdvice(indexObeSubFat);
        subFat.setSide(context.getString(weightAdviceSubFat.side));
        subFat.setAdvice(context.getString(weightAdviceSubFat.advice));
        List<FatLevelEntity> weightListSubFat = BodyLevelUtils.getSubFatList(context, sex);
        subFat.setLevel(weightListSubFat.get(indexObeSubFat).levelName);
        subFat.setLevelEntitys(weightListSubFat);

        bodyItems.put(FLAG_VF_PERCENTAGE, subFat);

        //11.身体年龄
        BodyItem bodyAge = new BodyItem();
        bodyAge.setCode(FLAG_BODY_AGE);
        bodyAge.setDataVal(bodyFat.getPpBodyAge() + "");
        bodyAge.setUnit(context.getString(R.string.yearsOld));
        double lfBodyAge = bodyFat.getPpBodyAge();
        bodyAge.setName(context.getString(R.string.bodyAge));
        bodyAge.setValue(PPUtil.getWeightValue(unitType, lfBodyAge));
        int indexBodyAge = StripedStand.bodyAgeInt(age, lfBodyAge);
        bodyAge.setLevelIndex(indexBodyAge);
        FatDescribeEntity weightAdviceBodyAge = BodyAdviceUtil.getBodyAgeAdvice(indexBodyAge);
        bodyAge.setSide(context.getString(weightAdviceBodyAge.side));
        bodyAge.setAdvice(context.getString(weightAdviceBodyAge.advice));
        List<FatLevelEntity> weightListBodyAge = BodyLevelUtils.getBodyAgeList(context, age);
        bodyAge.setLevel(weightListBodyAge.get(indexBodyAge).levelName);
        bodyAge.setLevelEntitys(weightListBodyAge);

        bodyItems.put(FLAG_BODY_AGE, bodyAge);

        //12.身体得分
        BodyItem bodyGrade = new BodyItem();
        bodyGrade.setCode(FLAG_BODY_SCORE);
        bodyGrade.setDataVal(bodyFat.getPpBodyScore() + "");
        bodyGrade.setUnit(context.getString(R.string.soofacye_fen));
        double lfBodyGrade = bodyFat.getPpBodyScore();
        bodyGrade.setName(context.getString(R.string.bodyScore));
        bodyGrade.setValue(PPUtil.getWeightValue(unitType, lfBodyGrade));
        int indexBodyGrade = StripedStand.bodyScoreInt(lfBodyGrade);
        bodyGrade.setLevelIndex(indexBodyGrade);
        FatDescribeEntity weightAdviceGrade = BodyAdviceUtil.getBodyScoreAdvice(indexBodyGrade);
        bodyGrade.setSide(context.getString(weightAdviceGrade.side));
        bodyGrade.setAdvice(context.getString(weightAdviceGrade.advice));
        List<FatLevelEntity> weightListBodyGrade = BodyLevelUtils.getBodyScoreList(context);
        bodyGrade.setLevel(weightListBodyGrade.get(indexBodyGrade).levelName);
        bodyGrade.setLevelEntitys(weightListBodyGrade);

        bodyItems.put(FLAG_BODY_SCORE, bodyGrade);

        //13.去脂体重
        BodyItem noFatWeight = new BodyItem();
        noFatWeight.setCode(FLAG_LOSE_FAT_WEIGHT_KG);
        noFatWeight.setDataVal(PPUtil.getWeight(unitType, bodyFat.getPpBodyScore()));
        noFatWeight.setUnit(PPUtil.getWeightUnit(unitType));
        double lfNoFatWeight = bodyFat.getPpLoseFatWeightKg();
        noFatWeight.setName(context.getString(R.string.leanBodyMass));
        noFatWeight.setValue(PPUtil.getWeightValue(unitType, lfNoFatWeight));
        noFatWeight.setDescribe(context.getString(R.string.noFatWeight_describe));
//        int indexNoFatWeight = StripedStand.bftLevelOld(lfNoFatWeight);
//        noFatWeight.setLevelIndex(indexNoFatWeight);
//        FatDescribeEntity weightAdviceNoFatWeight = BodyAdviceUtil.getSubFatAdvice(indexNoFatWeight);
//        noFatWeight.setSide(context.getString(weightAdviceNoFatWeight.side));
//        noFatWeight.setAdvice(context.getString(weightAdviceNoFatWeight.advice));
//        List<FatLevelEntity> weightListNoFatWeight = BodyLevelUtils.getSubFatList(context, sex);
//        noFatWeight.setLevel(weightListNoFatWeight.get(indexNoFatWeight).levelName);
//        noFatWeight.setLevelEntitys(weightListNoFatWeight);

        bodyItems.put(FLAG_LOSE_FAT_WEIGHT_KG, noFatWeight);

        //14.身体类型
        BodyItem bodyType = new BodyItem();
        bodyType.setCode(FLAG_BODY_TYPE);
        bodyType.setDataVal(StripedStand.getBodyfatType(context, bodyFat.getPpBodyType()));
        bodyType.setUnit(PPUtil.getWeightUnit(unitType));
        bodyType.setName(context.getString(R.string.bodyType));
        bodyType.setValue(bodyFat.getPpBodyType().getType());
//        int indexBodyType = StripedStand.visceralFatInt(lfBodyType);
//        bodyType.setLevelIndex(indexBodyType);
//        FatDescribeEntity weightAdviceBodyType = BodyAdviceUtil.getWeightAdvice(indexBodyType);
//        bodyType.setSide(context.getString(weightAdviceBodyType.side));
//        bodyType.setAdvice(context.getString(weightAdviceBodyType.advice));

        bodyItems.put(FLAG_BODY_TYPE, bodyType);

        //15.标准体重
        BodyItem idealWeight = new BodyItem();
        idealWeight.setCode(FLAG_BODYSTANDARD);
        double lfIdealWeight = bodyFat.getPpBodystandard();
        idealWeight.setDataVal(PPUtil.getWeight(unitType, lfIdealWeight));
        idealWeight.setUnit(PPUtil.getWeightUnit(unitType));
        idealWeight.setName(context.getString(R.string.standardWeight));
        idealWeight.setValue(PPUtil.getWeightValue(unitType, lfIdealWeight));
//        int indexIdealWeight = StripedStand.visceralFatInt(lfIdealWeight);
//        idealWeight.setLevelIndex(indexIdealWeight);
//        FatDescribeEntity weightAdviceIdealWeight = BodyAdviceUtil.getWeightAdvice(indexIdealWeight);
//        idealWeight.setSide(context.getString(weightAdviceIdealWeight.side));
//        idealWeight.setAdvice(context.getString(weightAdviceIdealWeight.advice));
//        List<FatLevelEntity> weightListIdealWeight = BodyLevelUtils.getBodyScoreList(context);
//        idealWeight.setLevel(weightListIdealWeight.get(indexIdealWeight).levelName);
//        idealWeight.setLevelEntitys(weightListIdealWeight);

        bodyItems.put(FLAG_BODY_TYPE, idealWeight);

        //17. flagBodyfatKg; || 脂肪量(kg)
//        BodyItem bodyFatKg = new BodyItem();
//        bodyFatKg.setCode(FLAG_BODYFAT_KG);
//
//        double lfBodyFatKg = bodyFat.getPpBodyfatPercentage() * bodyFat.getPpWeightKg();
//        bodyFatKg.setDataVal(PPUtil.getWeight(unitType, lfBodyFatKg));
//        bodyFatKg.setUnit(PPUtil.getWeightUnit(unitType));
//        bodyFatKg.setName(context.getString(R.string.bodyFat));
//        bodyFatKg.setValue(PPUtil.getWeightValue(unitType, lfBodyFatKg));
//        int indexBodyFatKg = StripedStand.visceralFatInt(lfBodyFatKg);
//        bodyFatKg.setLevelIndex(indexBodyFatKg);
//        FatDescribeEntity weightAdviceBodyFatKg = BodyAdviceUtil.getWeightAdvice(indexBodyFatKg);
//        bodyFatKg.setSide(context.getString(weightAdviceBodyFatKg.side));
//        bodyFatKg.setAdvice(context.getString(weightAdviceBodyFatKg.advice));
//        List<FatLevelEntity> weightListBodyFatKg = BodyLevelUtils.getFatList(context, sex, age);
//        bodyFatKg.setLevel(weightListBodyFatKg.get(indexBodyFatKg).levelName);
//        bodyFatKg.setLevelEntitys(weightListBodyFatKg);

//        bodyItems.put(FLAG_BODYFAT_KG, bodyFatKg);
    }

}
