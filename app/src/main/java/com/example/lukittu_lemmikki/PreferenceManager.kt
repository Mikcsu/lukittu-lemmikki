package com.example.lukittu_lemmikki

import android.content.Context

data class Model(val id: Int, val name: String, var cost: Int, var bought: Boolean = false)

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    //Wardrobe model buy logic
    fun saveModelBought(modelId: Int, isBought: Boolean) {
        sharedPreferences.edit().putBoolean("model_$modelId", isBought).apply()
    }

    fun isModelBought(modelId: Int): Boolean {
        return sharedPreferences.getBoolean("model_$modelId", false)
    }


    //Player progress logic
    fun saveLevel(level: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("level", level)
        editor.apply()
    }

    fun getLevel(): Int {
        return sharedPreferences.getInt("level", 1)
    }

    fun saveProgress(progress: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat("progress", progress)
        editor.apply()
    }
    /*fun addProgress(additionalProgress: Float) {
        val currentProgress = getProgress()
        val newProgress = currentProgress + additionalProgress
        saveProgress(newProgress)
    }

    fun getProgress(): Float {
        return sharedPreferences.getFloat("progress", 0.0f)
    }*/

    fun saveSteps(steps: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("Steps", steps)
        editor.apply()
    }

    fun getSteps(): Int {
        return sharedPreferences.getInt("Steps", 0)
    }

    fun saveMoney(money: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("Money", money)
        editor.apply()
    }

    fun getMoney(): Int {
        return sharedPreferences.getInt("Money", 0)
    }

    fun saveTotalStepsAtLevelStart(startSteps: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("TotalStepsAtLevelStart", startSteps)
        editor.apply()
    }

    fun getTotalStepsAtLevelStart(): Int {
        return sharedPreferences.getInt("TotalStepsAtLevelStart", 0)
    }

    //Wardrobe model selection logic
    fun saveSelectedModel(model: String) {
        val editor = sharedPreferences.edit()
        editor.putString("SelectedModel", model)
        editor.apply()
    }

    fun getSelectedModel(): String? {
        return sharedPreferences.getString("SelectedModel", null)
    }

    //Dark theme
    fun saveDarkTheme(isDarkTheme: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("DarkTheme", isDarkTheme)
        editor.apply()
    }
    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("DarkTheme", false) // Default value is false
    }
    //Reset the applications sharedPreferences
    fun reset() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    //Bonus setting
    fun saveSkinwalkerMode(isSkinwalkerMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("skinwalker_mode", isSkinwalkerMode)
        editor.apply()
    }

    fun getSkinwalkerMode(): Boolean {
        return sharedPreferences.getBoolean("skinwalker_mode", false)
    }
    //Model hat selection
    fun saveSelectedHat(hat: String) {
        val editor = sharedPreferences.edit()
        editor.putString("SelectedHat", hat)
        editor.apply()
    }

    fun getSelectedHat(): String? {
        return sharedPreferences.getString("SelectedHat", null)
    }
}

