package com.calki.app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calki.app.ui.CalculatorViewModel
import com.calki.app.ui.screens.CalculatorScreen
import com.calki.app.ui.theme.CalkiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val iconView = splashScreenView.iconView
            val scaleX = ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, 1.14f, 0f)
            val scaleY = ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, 1.14f, 0f)
            val fade = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
            scaleX.interpolator = OvershootInterpolator(1.5f)
            scaleY.interpolator = OvershootInterpolator(1.5f)
            scaleX.duration = 260L
            scaleY.duration = 260L
            fade.duration = 260L
            scaleX.start()
            scaleY.start()
            fade.doOnEnd { splashScreenView.remove() }
            fade.start()
        }

        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            CalkiTheme(themeMode = state.settings.themeMode, accentPreset = state.settings.accentPreset) {
                CalculatorScreen(
                    state = state,
                    onKeyPress = viewModel::onKeyPress,
                    onSwipeDelete = viewModel::deleteLastChar,
                    onLongPressClearAll = viewModel::onLongPressClearAllHistory,
                    onReuseHistory = viewModel::reuseHistoryItem,
                    onDeleteHistoryItem = viewModel::deleteHistoryItem,
                    onClearHistory = viewModel::clearAllHistory,
                    onThemeChange = viewModel::updateTheme,
                    onHapticsChange = viewModel::updateHaptics,
                    onSoundChange = viewModel::updateSound,
                    onDecimalPlacesChange = viewModel::updateDecimalPlaces,
                    onAccentPresetChange = viewModel::updateAccentPreset
                )
            }
        }
    }
}
