@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.lukittu_lemmikki

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Settings(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    selectedModel: String,
    onModelChange: (String) -> Unit,
    isSkinwalkerMode: Boolean,
    onSkinwalkerModeChange: (Boolean) -> Unit,
    onMainButtonClick: () -> Unit,
    onSettingsButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    BackButton(darkTheme, onClick = onMainButtonClick)
                })
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Dark theme switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Dark Theme",
                            color = MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { isChecked ->
                                onDarkThemeChange(isChecked)
                                preferencesManager.saveDarkTheme(isChecked)
                                if (isChecked) {
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                    Toast.makeText(context, "Dark theme enabled", Toast.LENGTH_SHORT).show()
                                } else {
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                    Toast.makeText(context, "Dark theme disabled", Toast.LENGTH_SHORT).show()
                                }
                                onSettingsButtonClick()
                            }
                        )
                    }

                    // Skinwalker mode switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Skinwalker Mode",
                            color = MaterialTheme.colorScheme.onSurface)
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
                        preferencesManager.saveSteps(-1)
                        preferencesManager.saveTotalStepsAtLevelStart(0)
                        preferencesManager.saveLevel(1)
                        Toast.makeText(context, "Preferences reset", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(
                            text = "Reset Preferences",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    )
}

@Composable
fun BackButton(darkTheme: Boolean, onClick: () -> Unit) {
    val vectorDrawable: Painter = if (darkTheme) {
        painterResource(id = R.drawable.back_arrow_superior)
    } else {
        painterResource(id = R.drawable.back_arrow)
    }

    Image(
        painter = vectorDrawable,
        contentDescription = "Back",
        modifier = Modifier
            .clickable(onClick = onClick)
            .size(48.dp)
    )
}
