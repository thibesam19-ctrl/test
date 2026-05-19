package com.axiom.aicoach.ai.vision.food

/**
 * Static nutrition lookup table.
 *
 * All macros are expressed **per 100 g** of the food so calorie math is
 * consistent regardless of portion size.  [estimatedGrams] provides a
 * sensible default portion when the camera cannot infer quantity.
 *
 * [findByLabel] performs case-insensitive substring matching so it tolerates
 * the label strings returned by ML Kit Image Labeling (e.g. "Cooked rice",
 * "Roti bread", "Chicken tikka masala").
 */
object FoodNutritionDatabase {

    // ── Sri Lankan foods ──────────────────────────────────────────────────────

    private val riceAndCurry = DetectedFood(
        name = "rice_and_curry",
        displayName = "Rice and Curry",
        caloriesPer100g = 112,          // 450 kcal / 400 g plate
        proteinPer100g = 3f,
        carbsPer100g = 20.5f,
        fatPer100g = 2f,
        estimatedGrams = 400,
        confidence = 0.85f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val polSambol = DetectedFood(
        name = "pol_sambol",
        displayName = "Pol Sambol (Coconut Sambol)",
        caloriesPer100g = 360,          // 180 kcal / 50 g
        proteinPer100g = 4f,
        carbsPer100g = 10f,
        fatPer100g = 36f,
        estimatedGrams = 50,
        confidence = 0.80f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val dhalCurry = DetectedFood(
        name = "dhal_curry",
        displayName = "Dhal Curry (Parippu)",
        caloriesPer100g = 90,           // 180 kcal / 200 ml ≈ 200 g
        proteinPer100g = 4.5f,
        carbsPer100g = 14f,
        fatPer100g = 1.5f,
        estimatedGrams = 200,
        confidence = 0.80f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val stringHoppers = DetectedFood(
        name = "string_hoppers",
        displayName = "String Hoppers (Idiyappam)",
        caloriesPer100g = 180,          // 180 kcal / ~100 g (3 pieces)
        proteinPer100g = 4f,
        carbsPer100g = 38f,
        fatPer100g = 1f,
        estimatedGrams = 100,
        confidence = 0.82f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val hoppers = DetectedFood(
        name = "hoppers",
        displayName = "Hoppers (Appam)",
        caloriesPer100g = 167,          // 200 kcal / ~120 g (2 hoppers)
        proteinPer100g = 4.2f,
        carbsPer100g = 31.7f,
        fatPer100g = 3.3f,
        estimatedGrams = 120,
        confidence = 0.82f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val kottuRoti = DetectedFood(
        name = "kottu_roti",
        displayName = "Kottu Roti",
        caloriesPer100g = 173,          // 520 kcal / 300 g portion
        proteinPer100g = 6f,
        carbsPer100g = 24f,
        fatPer100g = 5.3f,
        estimatedGrams = 300,
        confidence = 0.88f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val fishCurry = DetectedFood(
        name = "fish_curry",
        displayName = "Fish Curry",
        caloriesPer100g = 120,          // 240 kcal / 200 g
        proteinPer100g = 16f,
        carbsPer100g = 4f,
        fatPer100g = 4.5f,
        estimatedGrams = 200,
        confidence = 0.78f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val pittu = DetectedFood(
        name = "pittu",
        displayName = "Pittu",
        caloriesPer100g = 157,          // 220 kcal / 140 g (1 serving)
        proteinPer100g = 3.6f,
        carbsPer100g = 32.1f,
        fatPer100g = 1.4f,
        estimatedGrams = 140,
        confidence = 0.80f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val kiribath = DetectedFood(
        name = "kiribath",
        displayName = "Kiribath (Milk Rice)",
        caloriesPer100g = 160,          // 320 kcal / 200 g
        proteinPer100g = 3f,
        carbsPer100g = 29f,
        fatPer100g = 4f,
        estimatedGrams = 200,
        confidence = 0.82f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val lamprais = DetectedFood(
        name = "lamprais",
        displayName = "Lamprais",
        caloriesPer100g = 195,          // 780 kcal / 400 g portion
        proteinPer100g = 7f,
        carbsPer100g = 24.5f,
        fatPer100g = 7f,
        estimatedGrams = 400,
        confidence = 0.75f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val watalappam = DetectedFood(
        name = "watalappam",
        displayName = "Watalappam",
        caloriesPer100g = 280,          // 280 kcal / 100 g
        proteinPer100g = 6f,
        carbsPer100g = 38f,
        fatPer100g = 12f,
        estimatedGrams = 100,
        confidence = 0.78f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val cashewCurry = DetectedFood(
        name = "cashew_curry",
        displayName = "Cashew Curry",
        caloriesPer100g = 213,          // 320 kcal / 150 g
        proteinPer100g = 6.7f,
        carbsPer100g = 14.7f,
        fatPer100g = 14.7f,
        estimatedGrams = 150,
        confidence = 0.76f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val jackfruitCurry = DetectedFood(
        name = "jackfruit_curry",
        displayName = "Jackfruit Curry",
        caloriesPer100g = 100,          // 180 kcal / 180 g
        proteinPer100g = 1.7f,
        carbsPer100g = 15.6f,
        fatPer100g = 3.3f,
        estimatedGrams = 180,
        confidence = 0.76f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    private val coconutRoti = DetectedFood(
        name = "coconut_roti",
        displayName = "Coconut Roti",
        caloriesPer100g = 200,          // 240 kcal / 120 g (2 pieces)
        proteinPer100g = 3.3f,
        carbsPer100g = 26.7f,
        fatPer100g = 10f,
        estimatedGrams = 120,
        confidence = 0.80f,
        cuisineType = CuisineType.SRI_LANKAN,
    )

    // ── Indian foods ──────────────────────────────────────────────────────────

    private val idli = DetectedFood(
        name = "idli",
        displayName = "Idli",
        caloriesPer100g = 116,          // 180 kcal / 155 g (3 pieces)
        proteinPer100g = 3.9f,
        carbsPer100g = 23.2f,
        fatPer100g = 0.6f,
        estimatedGrams = 155,
        confidence = 0.82f,
        cuisineType = CuisineType.INDIAN,
    )

    private val dosa = DetectedFood(
        name = "dosa",
        displayName = "Dosa (Plain)",
        caloriesPer100g = 168,          // 168 kcal / 100 g (1 plain dosa)
        proteinPer100g = 4f,
        carbsPer100g = 32f,
        fatPer100g = 3f,
        estimatedGrams = 100,
        confidence = 0.85f,
        cuisineType = CuisineType.INDIAN,
    )

    private val biryani = DetectedFood(
        name = "biryani",
        displayName = "Biryani",
        caloriesPer100g = 155,          // 620 kcal / 400 g plate
        proteinPer100g = 7f,
        carbsPer100g = 22f,
        fatPer100g = 4.5f,
        estimatedGrams = 400,
        confidence = 0.88f,
        cuisineType = CuisineType.INDIAN,
    )

    private val paneerTikka = DetectedFood(
        name = "paneer_tikka",
        displayName = "Paneer Tikka",
        caloriesPer100g = 187,          // 280 kcal / 150 g
        proteinPer100g = 12f,
        carbsPer100g = 5.3f,
        fatPer100g = 13.3f,
        estimatedGrams = 150,
        confidence = 0.80f,
        cuisineType = CuisineType.INDIAN,
    )

    private val dalMakhani = DetectedFood(
        name = "dal_makhani",
        displayName = "Dal Makhani",
        caloriesPer100g = 110,          // 220 kcal / 200 g
        proteinPer100g = 5f,
        carbsPer100g = 12f,
        fatPer100g = 5f,
        estimatedGrams = 200,
        confidence = 0.80f,
        cuisineType = CuisineType.INDIAN,
    )

    private val roti = DetectedFood(
        name = "roti",
        displayName = "Roti / Chapati",
        caloriesPer100g = 100,          // 200 kcal / 200 g (2 chapati)
        proteinPer100g = 3f,
        carbsPer100g = 20f,
        fatPer100g = 1f,
        estimatedGrams = 200,
        confidence = 0.83f,
        cuisineType = CuisineType.INDIAN,
    )

    private val samosa = DetectedFood(
        name = "samosa",
        displayName = "Samosa",
        caloriesPer100g = 267,          // 320 kcal / 120 g (2 pieces)
        proteinPer100g = 5f,
        carbsPer100g = 33.3f,
        fatPer100g = 13.3f,
        estimatedGrams = 120,
        confidence = 0.85f,
        cuisineType = CuisineType.INDIAN,
    )

    private val chickenTikka = DetectedFood(
        name = "chicken_tikka",
        displayName = "Chicken Tikka",
        caloriesPer100g = 153,          // 230 kcal / 150 g
        proteinPer100g = 22.7f,
        carbsPer100g = 2.7f,
        fatPer100g = 6f,
        estimatedGrams = 150,
        confidence = 0.83f,
        cuisineType = CuisineType.INDIAN,
    )

    private val masoorDal = DetectedFood(
        name = "masoor_dal",
        displayName = "Masoor Dal",
        caloriesPer100g = 116,          // 230 kcal / 198 g (1 cup cooked)
        proteinPer100g = 9.1f,
        carbsPer100g = 20.2f,
        fatPer100g = 0.5f,
        estimatedGrams = 198,
        confidence = 0.78f,
        cuisineType = CuisineType.INDIAN,
    )

    private val palakPaneer = DetectedFood(
        name = "palak_paneer",
        displayName = "Palak Paneer",
        caloriesPer100g = 140,          // 280 kcal / 200 g
        proteinPer100g = 7f,
        carbsPer100g = 6f,
        fatPer100g = 10f,
        estimatedGrams = 200,
        confidence = 0.80f,
        cuisineType = CuisineType.INDIAN,
    )

    // ── Western / International foods ─────────────────────────────────────────

    private val oatmeal = DetectedFood(
        name = "oatmeal",
        displayName = "Oatmeal",
        caloriesPer100g = 64,           // 154 kcal / 240 g (1 cup cooked)
        proteinPer100g = 2.1f,
        carbsPer100g = 11.3f,
        fatPer100g = 1.3f,
        estimatedGrams = 240,
        confidence = 0.85f,
        cuisineType = CuisineType.WESTERN,
    )

    private val grilledChickenBreast = DetectedFood(
        name = "grilled_chicken_breast",
        displayName = "Grilled Chicken Breast",
        caloriesPer100g = 165,          // 248 kcal / 150 g
        proteinPer100g = 30.7f,
        carbsPer100g = 0f,
        fatPer100g = 3.3f,
        estimatedGrams = 150,
        confidence = 0.88f,
        cuisineType = CuisineType.WESTERN,
    )

    private val greekYogurt = DetectedFood(
        name = "greek_yogurt",
        displayName = "Greek Yogurt",
        caloriesPer100g = 59,           // 100 kcal / 170 g
        proteinPer100g = 10f,
        carbsPer100g = 3.5f,
        fatPer100g = 0f,
        estimatedGrams = 170,
        confidence = 0.82f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val banana = DetectedFood(
        name = "banana",
        displayName = "Banana",
        caloriesPer100g = 89,           // 105 kcal / 118 g (medium)
        proteinPer100g = 1.1f,
        carbsPer100g = 23f,
        fatPer100g = 0.3f,
        estimatedGrams = 118,
        confidence = 0.92f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val apple = DetectedFood(
        name = "apple",
        displayName = "Apple",
        caloriesPer100g = 52,           // 95 kcal / 182 g (medium)
        proteinPer100g = 0.3f,
        carbsPer100g = 14f,
        fatPer100g = 0.2f,
        estimatedGrams = 182,
        confidence = 0.92f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val brownRice = DetectedFood(
        name = "brown_rice",
        displayName = "Brown Rice",
        caloriesPer100g = 112,          // 216 kcal / 195 g (1 cup cooked)
        proteinPer100g = 2.6f,
        carbsPer100g = 23.2f,
        fatPer100g = 0.9f,
        estimatedGrams = 195,
        confidence = 0.82f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val egg = DetectedFood(
        name = "egg",
        displayName = "Egg",
        caloriesPer100g = 155,          // 78 kcal / 50 g (1 large egg)
        proteinPer100g = 12f,
        carbsPer100g = 1.1f,
        fatPer100g = 10.6f,
        estimatedGrams = 50,
        confidence = 0.90f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val salmon = DetectedFood(
        name = "salmon",
        displayName = "Salmon",
        caloriesPer100g = 187,          // 280 kcal / 150 g
        proteinPer100g = 26.7f,
        carbsPer100g = 0f,
        fatPer100g = 8.7f,
        estimatedGrams = 150,
        confidence = 0.85f,
        cuisineType = CuisineType.WESTERN,
    )

    private val broccoli = DetectedFood(
        name = "broccoli",
        displayName = "Broccoli",
        caloriesPer100g = 34,           // 55 kcal / 160 g (1 cup)
        proteinPer100g = 2.5f,
        carbsPer100g = 7f,
        fatPer100g = 0.4f,
        estimatedGrams = 160,
        confidence = 0.88f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val almonds = DetectedFood(
        name = "almonds",
        displayName = "Almonds",
        caloriesPer100g = 577,          // 173 kcal / 30 g
        proteinPer100g = 21.2f,
        carbsPer100g = 21.7f,
        fatPer100g = 49.9f,
        estimatedGrams = 30,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val bread = DetectedFood(
        name = "bread",
        displayName = "Bread (White/Whole Wheat)",
        caloriesPer100g = 267,          // 160 kcal / 60 g (2 slices)
        proteinPer100g = 10f,
        carbsPer100g = 50f,
        fatPer100g = 3.3f,
        estimatedGrams = 60,
        confidence = 0.85f,
        cuisineType = CuisineType.WESTERN,
    )

    private val pasta = DetectedFood(
        name = "pasta",
        displayName = "Pasta",
        caloriesPer100g = 131,          // 220 kcal / 168 g (1 cup cooked)
        proteinPer100g = 4.8f,
        carbsPer100g = 25.6f,
        fatPer100g = 0.6f,
        estimatedGrams = 168,
        confidence = 0.85f,
        cuisineType = CuisineType.WESTERN,
    )

    private val burger = DetectedFood(
        name = "burger",
        displayName = "Burger",
        caloriesPer100g = 270,          // 540 kcal / 200 g (standard burger)
        proteinPer100g = 15f,
        carbsPer100g = 20f,
        fatPer100g = 14f,
        estimatedGrams = 200,
        confidence = 0.88f,
        cuisineType = CuisineType.WESTERN,
    )

    private val pizza = DetectedFood(
        name = "pizza",
        displayName = "Pizza",
        caloriesPer100g = 266,          // 285 kcal / ~107 g (1 slice)
        proteinPer100g = 11.2f,
        carbsPer100g = 33.6f,
        fatPer100g = 9.3f,
        estimatedGrams = 107,
        confidence = 0.90f,
        cuisineType = CuisineType.WESTERN,
    )

    private val mixedGreenSalad = DetectedFood(
        name = "mixed_green_salad",
        displayName = "Mixed Green Salad",
        caloriesPer100g = 17,           // 35 kcal / 200 g
        proteinPer100g = 1f,
        carbsPer100g = 3f,
        fatPer100g = 0.2f,
        estimatedGrams = 200,
        confidence = 0.80f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    // ── Additional common items ────────────────────────────────────────────────

    private val sweetPotato = DetectedFood(
        name = "sweet_potato",
        displayName = "Sweet Potato",
        caloriesPer100g = 86,
        proteinPer100g = 1.6f,
        carbsPer100g = 20.1f,
        fatPer100g = 0.1f,
        estimatedGrams = 150,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val lentilsSoup = DetectedFood(
        name = "lentil_soup",
        displayName = "Lentil Soup",
        caloriesPer100g = 71,
        proteinPer100g = 4.5f,
        carbsPer100g = 11.7f,
        fatPer100g = 0.8f,
        estimatedGrams = 250,
        confidence = 0.78f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val avocado = DetectedFood(
        name = "avocado",
        displayName = "Avocado",
        caloriesPer100g = 160,
        proteinPer100g = 2f,
        carbsPer100g = 8.5f,
        fatPer100g = 14.7f,
        estimatedGrams = 100,
        confidence = 0.88f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val orangeJuice = DetectedFood(
        name = "orange_juice",
        displayName = "Orange Juice",
        caloriesPer100g = 45,
        proteinPer100g = 0.7f,
        carbsPer100g = 10.4f,
        fatPer100g = 0.2f,
        estimatedGrams = 240,
        confidence = 0.80f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val coffee = DetectedFood(
        name = "coffee",
        displayName = "Coffee (Black)",
        caloriesPer100g = 2,
        proteinPer100g = 0.3f,
        carbsPer100g = 0f,
        fatPer100g = 0f,
        estimatedGrams = 240,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val milkTeaWithSugar = DetectedFood(
        name = "milk_tea",
        displayName = "Milk Tea",
        caloriesPer100g = 38,
        proteinPer100g = 1.5f,
        carbsPer100g = 5.4f,
        fatPer100g = 1.2f,
        estimatedGrams = 240,
        confidence = 0.80f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val tuna = DetectedFood(
        name = "tuna",
        displayName = "Tuna (Canned)",
        caloriesPer100g = 116,
        proteinPer100g = 25.5f,
        carbsPer100g = 0f,
        fatPer100g = 1f,
        estimatedGrams = 100,
        confidence = 0.82f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val cottage_cheese = DetectedFood(
        name = "cottage_cheese",
        displayName = "Cottage Cheese",
        caloriesPer100g = 98,
        proteinPer100g = 11.1f,
        carbsPer100g = 3.4f,
        fatPer100g = 4.3f,
        estimatedGrams = 150,
        confidence = 0.80f,
        cuisineType = CuisineType.WESTERN,
    )

    private val peanutButter = DetectedFood(
        name = "peanut_butter",
        displayName = "Peanut Butter",
        caloriesPer100g = 588,
        proteinPer100g = 25f,
        carbsPer100g = 20f,
        fatPer100g = 50f,
        estimatedGrams = 32,
        confidence = 0.82f,
        cuisineType = CuisineType.WESTERN,
    )

    private val spinach = DetectedFood(
        name = "spinach",
        displayName = "Spinach",
        caloriesPer100g = 23,
        proteinPer100g = 2.9f,
        carbsPer100g = 3.6f,
        fatPer100g = 0.4f,
        estimatedGrams = 90,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val whiteRice = DetectedFood(
        name = "white_rice",
        displayName = "White Rice",
        caloriesPer100g = 130,          // cooked
        proteinPer100g = 2.7f,
        carbsPer100g = 28.6f,
        fatPer100g = 0.3f,
        estimatedGrams = 195,
        confidence = 0.88f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val friedRice = DetectedFood(
        name = "fried_rice",
        displayName = "Fried Rice",
        caloriesPer100g = 163,
        proteinPer100g = 4.5f,
        carbsPer100g = 27f,
        fatPer100g = 4f,
        estimatedGrams = 250,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val noodles = DetectedFood(
        name = "noodles",
        displayName = "Noodles",
        caloriesPer100g = 138,
        proteinPer100g = 4.5f,
        carbsPer100g = 25f,
        fatPer100g = 2f,
        estimatedGrams = 200,
        confidence = 0.83f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val chocolate = DetectedFood(
        name = "chocolate",
        displayName = "Chocolate",
        caloriesPer100g = 546,
        proteinPer100g = 4.9f,
        carbsPer100g = 60f,
        fatPer100g = 31.3f,
        estimatedGrams = 40,
        confidence = 0.88f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    private val iceCream = DetectedFood(
        name = "ice_cream",
        displayName = "Ice Cream",
        caloriesPer100g = 207,
        proteinPer100g = 3.5f,
        carbsPer100g = 23.6f,
        fatPer100g = 11f,
        estimatedGrams = 100,
        confidence = 0.85f,
        cuisineType = CuisineType.INTERNATIONAL,
    )

    // ── Master catalogue ──────────────────────────────────────────────────────

    val all: List<DetectedFood> = listOf(
        // Sri Lankan
        riceAndCurry, polSambol, dhalCurry, stringHoppers, hoppers,
        kottuRoti, fishCurry, pittu, kiribath, lamprais,
        watalappam, cashewCurry, jackfruitCurry, coconutRoti,
        // Indian
        idli, dosa, biryani, paneerTikka, dalMakhani,
        roti, samosa, chickenTikka, masoorDal, palakPaneer,
        // Western / International
        oatmeal, grilledChickenBreast, greekYogurt, banana, apple,
        brownRice, egg, salmon, broccoli, almonds,
        bread, pasta, burger, pizza, mixedGreenSalad,
        // Additional
        sweetPotato, lentilsSoup, avocado, orangeJuice, coffee,
        milkTeaWithSugar, tuna, cottage_cheese, peanutButter, spinach,
        whiteRice, friedRice, noodles, chocolate, iceCream,
    )

    /**
     * Looks up a [DetectedFood] by matching [mlKitLabel] against food names,
     * display names, and common synonyms using case-insensitive substring
     * matching.  Returns the first match, or null if no match is found.
     */
    fun findByLabel(mlKitLabel: String): DetectedFood? {
        val query = mlKitLabel.lowercase().trim()

        // Direct name / displayName hit
        val direct = all.firstOrNull { food ->
            food.name.lowercase().contains(query) ||
                food.displayName.lowercase().contains(query) ||
                query.contains(food.name.lowercase()) ||
                query.contains(food.displayName.lowercase().substringBefore(" (").lowercase())
        }
        if (direct != null) return direct

        // Synonym / keyword mapping for common ML Kit label strings
        val synonymMap = mapOf(
            "rice" to whiteRice,
            "curry" to riceAndCurry,
            "coconut" to polSambol,
            "lentil" to dhalCurry,
            "dal" to dhalCurry,
            "dhal" to dhalCurry,
            "noodle" to stringHoppers,
            "appam" to hoppers,
            "hopper" to hoppers,
            "kottu" to kottuRoti,
            "roti" to roti,
            "chapati" to roti,
            "fish" to fishCurry,
            "seafood" to fishCurry,
            "jackfruit" to jackfruitCurry,
            "cashew" to cashewCurry,
            "milk rice" to kiribath,
            "idiyappam" to stringHoppers,
            "biryani" to biryani,
            "rice dish" to biryani,
            "paneer" to paneerTikka,
            "tikka" to chickenTikka,
            "chicken tikka masala" to chickenTikka,
            "samosa" to samosa,
            "dosa" to dosa,
            "crepe" to dosa,
            "idli" to idli,
            "oat" to oatmeal,
            "porridge" to oatmeal,
            "chicken" to grilledChickenBreast,
            "yogurt" to greekYogurt,
            "yoghurt" to greekYogurt,
            "banana" to banana,
            "apple" to apple,
            "fruit" to apple,
            "egg" to egg,
            "omelette" to egg,
            "scrambled" to egg,
            "salmon" to salmon,
            "fish fillet" to salmon,
            "broccoli" to broccoli,
            "vegetable" to broccoli,
            "almond" to almonds,
            "nut" to almonds,
            "bread" to bread,
            "toast" to bread,
            "sandwich" to bread,
            "pasta" to pasta,
            "spaghetti" to pasta,
            "burger" to burger,
            "hamburger" to burger,
            "pizza" to pizza,
            "salad" to mixedGreenSalad,
            "lettuce" to mixedGreenSalad,
            "sweet potato" to sweetPotato,
            "avocado" to avocado,
            "tuna" to tuna,
            "chocolate" to chocolate,
            "ice cream" to iceCream,
            "dessert" to iceCream,
            "fried rice" to friedRice,
            "noodles" to noodles,
            "spinach" to spinach,
        )

        for ((keyword, food) in synonymMap) {
            if (query.contains(keyword)) return food
        }

        return null
    }
}
