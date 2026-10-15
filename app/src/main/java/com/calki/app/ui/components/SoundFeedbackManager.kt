package com.calki.app.ui.components

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View

object FeedbackHelper {
    fun performFeedback(view: View, hapticsEnabled: Boolean, soundEnabled: Boolean, isLongPress: Boolean = false) {
        if (hapticsEnabled) {
            val constant = if (isLongPress) HapticFeedbackConstants.LONG_PRESS else HapticFeedbackConstants.KEYBOARD_TAP
            view.performHapticFeedback(constant)
        }
        if (soundEnabled) {
            val audioManager = view.context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.45f)
        }
    }
}
