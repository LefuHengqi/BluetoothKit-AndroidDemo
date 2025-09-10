package com.lefu.ppblutoothkit.device.foodscale.vo

import com.peng.ppscale.vo.PPKorreFoodInfo
import com.peng.ppscale.vo.nameList1
import java.io.Serializable

data class XinmiaoFoodScaleDefault(
    val foodId: String? = "",
    val foodName: String? = "",
    var imageIndex: Int? = 255,
    val brandName: String? = "",
    val servingQty: Int? = 0,
    val servingUnit: String? = "",
    val servingWeightGrams: Double? = 0.0,
    val lfCalories: Double? = 0.0,
    val lfTotalFat: Double? = 0.0,
    val lfSaturatedFat: Double? = 0.0,
    val lfCholesterol: Double? = 0.0,
    val lfSodium: Double? = 0.0,
    val lfTotalCarbohydrate: Double? = 0.0,
    val lfDietaryFiber: Double? = 0.0,
    val lfSugars: Double? = 0.0,
    val lfProtein: Double? = 0.0,
    val lfPotassium: Double? = 0.0,
    val lfP: Double? = 0.0,
    val upc: String? = "",
    val lat: Double? = 0.0,
    val lng: Double? = 0.0,
    val mealType: Int? = 0,
    val subRecipeId: String? = "",
    val brickCode: String? = "",
    val thumb: String? = "",
    val highres: String? = "",
    val nfIngredientStatement: String? = "",
    val locales: String = "",
    val isRawFood: Int = 0,
    val isCollect: Boolean? = false,
    val fullNutrients: List<Nutrient>? = emptyList(),
    val measures: List<Measure>? = emptyList(),
    val claims: List<Claim>? = emptyList()
) : Serializable

data class Nutrient(
    val nutrientId: Int,
    val nutrientName: String,
    val nutrientUnit: String,
    val nutrientValue: Double
) : Serializable

data class Measure(
    val servingWeight: Double,
    val measure: String,
    val qty: Int
) : Serializable

data class Claim(
    val name: String,
    val alias: String,
    val description: String
) : Serializable


fun XinmiaoFoodScaleDefault.toPPKorreFoodInfo(foodNo: Int): PPKorreFoodInfo {
    return PPKorreFoodInfo(foodNo = foodNo).apply {
        // 遍历fullNutrients并映射到对应字段
        this@toPPKorreFoodInfo.fullNutrients?.forEach { nutrient ->
            val nutritionKey = nameList1[nutrient.nutrientId] ?: return@forEach
            when (nutritionKey) {
                "calcium" -> calciumMg = nutrient.nutrientValue
                "energy" -> calories = nutrient.nutrientValue
                "carbohydrate" -> totalCarbohydrates = nutrient.nutrientValue
                "cholesterol" -> cholesterol = nutrient.nutrientValue
                "copper" -> copperMg = nutrient.nutrientValue
                "fat" -> totalFat = nutrient.nutrientValue
                "fiberDietary" -> dietaryFiber = nutrient.nutrientValue
                "iron" -> ironMg = nutrient.nutrientValue
                "magnesium" -> magnesiumMg = nutrient.nutrientValue
                "manganese" -> manganeseMg = nutrient.nutrientValue
                "niacin" -> niacinMg = nutrient.nutrientValue
                "phosphor" -> phosphorusMg = nutrient.nutrientValue
                "potassium" -> potassiumMg = nutrient.nutrientValue
                "protein" -> protein = nutrient.nutrientValue
                "retinol" -> vitaminAG = nutrient.nutrientValue
                "riboflavin" -> vitaminB2G = nutrient.nutrientValue
                "selenium" -> seleniumMg = nutrient.nutrientValue
                "sodium" -> sodium = nutrient.nutrientValue
                "thiamine" -> vitaminB1G = nutrient.nutrientValue
                "vitaminB12" -> vitaminB12G = nutrient.nutrientValue
                "vitaminB6" -> vitaminB6G = nutrient.nutrientValue
                "vitaminC" -> vitaminCG = nutrient.nutrientValue
                "vitaminD" -> vitaminDG = nutrient.nutrientValue
                "vitaminE" -> vitaminEG = nutrient.nutrientValue
                "zinc" -> zincMg = nutrient.nutrientValue
                "transFat" -> transFat = nutrient.nutrientValue
                "saturatedFat" -> saturatedFat = nutrient.nutrientValue
                "sugars" -> sugars = nutrient.nutrientValue
            }
        }
        foodName = this@toPPKorreFoodInfo.foodName
        imageIndex = this@toPPKorreFoodInfo.imageIndex
        foodRemoteId = this@toPPKorreFoodInfo.foodId
        foodWeight = this@toPPKorreFoodInfo.servingWeightGrams
    }
}

