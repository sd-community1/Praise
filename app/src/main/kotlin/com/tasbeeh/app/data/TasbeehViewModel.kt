package com.tasbeeh.app.data

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tasbeeh.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasbeehViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)

    private val _state = MutableStateFlow(TasbeehState())
    val state: StateFlow<TasbeehState> = _state.asStateFlow()

    private val _sessions = MutableStateFlow<List<SessionRecord>>(emptyList())
    val sessions: StateFlow<List<SessionRecord>> = _sessions.asStateFlow()

    private val _showTargetReached = MutableStateFlow(false)
    val showTargetReached: StateFlow<Boolean> = _showTargetReached.asStateFlow()

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0
    private var completeSoundId: Int = 0
    private var soundsLoaded = false

    init {
        initSoundPool(application)
        viewModelScope.launch {
            prefsManager.tasbeehStateFlow.collect { saved ->
                _state.value = saved
            }
        }
        viewModelScope.launch {
            prefsManager.sessionsFlow.collect { list ->
                _sessions.value = list
            }
        }
    }

    private fun initSoundPool(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) soundsLoaded = true
        }
        clickSoundId = soundPool?.load(context, R.raw.click, 1) ?: 0
        completeSoundId = soundPool?.load(context, R.raw.complete, 1) ?: 0
    }

    fun increment() {
        val current = _state.value
        val newCount = current.currentCount + 1
        val target = if (current.isCustomTarget) current.customTargetValue else current.target

        performHaptic(isComplete = newCount >= target)
        if (current.soundEnabled && soundsLoaded) {
            if (newCount >= target) {
                soundPool?.play(completeSoundId, 1f, 1f, 1, 0, 1f)
            } else {
                soundPool?.play(clickSoundId, 0.7f, 0.7f, 1, 0, 1f)
            }
        }

        _state.value = current.copy(currentCount = newCount)
        viewModelScope.launch { prefsManager.saveCount(newCount) }

        if (newCount >= target) {
            _showTargetReached.value = true
            val dhikrText = if (current.selectedDhikr.id == 0) current.customDhikr
            else current.selectedDhikr.arabic
            viewModelScope.launch {
                prefsManager.addSession(
                    SessionRecord(
                        dhikrArabic = dhikrText,
                        count = newCount,
                        target = target,
                        completed = true
                    )
                )
            }
        }
    }

    fun undoLast() {
        val current = _state.value
        if (current.currentCount > 0) {
            val newCount = current.currentCount - 1
            _state.value = current.copy(currentCount = newCount)
            viewModelScope.launch { prefsManager.saveCount(newCount) }
        }
    }

    fun reset() {
        val current = _state.value
        if (current.currentCount > 0) {
            val dhikrText = if (current.selectedDhikr.id == 0) current.customDhikr
            else current.selectedDhikr.arabic
            val target = if (current.isCustomTarget) current.customTargetValue else current.target
            viewModelScope.launch {
                prefsManager.addSession(
                    SessionRecord(
                        dhikrArabic = dhikrText,
                        count = current.currentCount,
                        target = target,
                        completed = current.currentCount >= target
                    )
                )
                prefsManager.saveCount(0)
            }
        }
        _state.value = current.copy(currentCount = 0)
    }

    fun dismissTargetReached() {
        _showTargetReached.value = false
    }

    fun selectDhikr(dhikr: DhikrItem) {
        _state.value = _state.value.copy(selectedDhikr = dhikr)
        viewModelScope.launch { prefsManager.saveDhikr(dhikr.id) }
    }

    fun setCustomDhikr(text: String) {
        _state.value = _state.value.copy(customDhikr = text)
        viewModelScope.launch { prefsManager.saveCustomDhikr(text) }
    }

    fun setTarget(target: Int) {
        _state.value = _state.value.copy(target = target, isCustomTarget = false)
        viewModelScope.launch { prefsManager.saveTarget(target, false, _state.value.customTargetValue) }
    }

    fun setCustomTarget(value: Int) {
        if (value > 0) {
            _state.value = _state.value.copy(customTargetValue = value, isCustomTarget = true, target = value)
            viewModelScope.launch { prefsManager.saveTarget(value, true, value) }
        }
    }

    fun setVibration(enabled: Boolean) {
        _state.value = _state.value.copy(vibrationEnabled = enabled)
        viewModelScope.launch { prefsManager.saveVibration(enabled) }
    }

    fun setSound(enabled: Boolean) {
        _state.value = _state.value.copy(soundEnabled = enabled)
        viewModelScope.launch { prefsManager.saveSound(enabled) }
    }

    fun setTheme(isDark: Boolean) {
        _state.value = _state.value.copy(isDarkTheme = isDark)
        viewModelScope.launch { prefsManager.saveTheme(isDark) }
    }

    fun clearHistory() {
        viewModelScope.launch { prefsManager.clearSessions() }
    }

    private fun performHaptic(isComplete: Boolean) {
        if (!_state.value.vibrationEnabled) return
        vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = if (isComplete) {
                VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 80), -1)
            } else {
                VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            if (isComplete) vibrator.vibrate(longArrayOf(0, 80, 60, 80), -1)
            else vibrator.vibrate(30)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundPool?.release()
        soundPool = null
    }
}
