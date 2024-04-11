package com.example.lukittu_lemmikki

import android.content.Context

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

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
}