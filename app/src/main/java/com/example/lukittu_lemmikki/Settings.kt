package com.example.lukittu_lemmikki

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@Composable
fun Settings(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    selectedModel: String,
    onModelChange: (String) -> Unit,
    isSkinwalkerMode: Boolean,
    onSkinwalkerModeChange: (Boolean) -> Unit,
    onMainButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            Toast.makeText(context, "Dark theme enabled", Toast.LENGTH_SHORT).show()
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            Toast.makeText(context, "Dark theme disabled", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Skinwalker mode switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Skinwalker Mode")
                Switch(
                    checked = isSkinwalkerMode,
                    onCheckedChange = { isChecked ->
                        onSkinwalkerModeChange(isChecked)
                        preferencesManager.saveSkinwalkerMode(isChecked)
                        Toast.makeText(context, "Skinwalker mode ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Reset preferences button
            Button(onClick = {
                preferencesManager.reset()
                onDarkThemeChange(false)
                onModelChange("deer")
                onSkinwalkerModeChange(false)
                Toast.makeText(context, "Preferences reset", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Reset Preferences")
            }
        }
    }
    // Button to return to main view
    Button(onClick = onMainButtonClick) {
        Text(text = "Return to Main")
    }
}