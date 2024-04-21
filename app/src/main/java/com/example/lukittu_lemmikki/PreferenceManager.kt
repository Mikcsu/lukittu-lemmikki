package com.example.lukittu_lemmikki

import android.content.Context

data class Model(val id: Int, val name: String, var cost: Int, var bought: Boolean = false)

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)


    // Functions for managing individual models
    fun saveModelBought(modelId: Int, isBought: Boolean) {
        sharedPreferences.edit().putBoolean("model_$modelId", isBought).apply()
    }

    fun isModelBought(modelId: Int): Boolean {
        return sharedPreferences.getBoolean("model_$modelId", false)
    }

    // Function for saving the entire model list
    fun saveModelList(models: List<Model>) {
        val editor = sharedPreferences.edit()
        for (model in models) {
            editor.putBoolean("model_${model.id}", model.bought)
        }
        editor.apply()
    }

    // Function for retrieving the entire model list
    fun getModelList(): List<Model> {
        // Define your list of models here, with their respective IDs
        val modelIds = listOf(R.drawable.gekko, R.drawable.deer, R.drawable.fish, R.drawable.hamster, R.drawable.monkey, R.drawable.octopus, R.drawable.snake)
        val models = mutableListOf<Model>()
        for (id in modelIds) {
            val bought = sharedPreferences.getBoolean("model_$id", false)
            models.add(Model(id, "model_$id", 100, bought))
        }
        return models
    }

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
    fun addProgress(additionalProgress: Float) {
        val currentProgress = getProgress()
        val newProgress = currentProgress + additionalProgress
        saveProgress(newProgress)
    }

    fun getProgress(): Float {
        return sharedPreferences.getFloat("progress", 0.0f)
    }

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

    fun saveSelectedModel(model: String) {
        val editor = sharedPreferences.edit()
        editor.putString("SelectedModel", model)
        editor.apply()
    }

    fun getSelectedModel(): String? {
        return sharedPreferences.getString("SelectedModel", null)
    }

    fun saveDarkTheme(isDarkTheme: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("DarkTheme", isDarkTheme)
        editor.apply()
    }

    fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("DarkTheme", false) // Default value is false
    }

    fun reset() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveSkinwalkerMode(isSkinwalkerMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("skinwalker_mode", isSkinwalkerMode)
        editor.apply()
    }

    fun getSkinwalkerMode(): Boolean {
        return sharedPreferences.getBoolean("skinwalker_mode", false)
    }
}

