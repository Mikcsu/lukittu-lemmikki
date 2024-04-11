package com.example.lukittu_lemmikki

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun Settings(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    selectedModel: String,
    onModelChange: (String) -> Unit,
    onMainButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    Column {
        Text(text = "Settings")

        // Dark theme switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Dark Theme")
            Switch(
                checked = darkTheme,
                onCheckedChange = { isChecked ->
                    onDarkThemeChange(isChecked)
                    preferencesManager.saveDarkTheme(isChecked)
                    // Update the app's theme
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            )
        }

        // Button to add "sw" in front of the model name
        Button(onClick = {
            val newModel = "sw$selectedModel"
            onModelChange(newModel)
            preferencesManager.saveSelectedModel(newModel)
        }) {
            Text(text = "Skinwalker Mode")
        }

        // Reset preferences button
        Button(onClick = {
            preferencesManager.reset()
            onDarkThemeChange(false)
            onModelChange("deer")
        }) {
            Text(text = "Reset Preferences")
        }
        // Button to return to main view
        Button(onClick = onMainButtonClick) {
            Text(text = "Return to Main")
        }
    }
}