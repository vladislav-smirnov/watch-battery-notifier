package io.github.airdaydreamers.batterynotifier.watch.utils

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Log

class AudioManagerUtils {
    companion object {
        const val TAG = "AudioManagerUtils"
        fun isAudiOutputAvailable(context: Context): Boolean {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            fun audioOutputAvailable(type: Int): Boolean {
                if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                    return false
                }
                return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                    .any { it.type == type }
            }

            val isAvailable =
                audioOutputAvailable(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
                        || audioOutputAvailable(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP)

            Log.d(TAG, "audio output isAvailable = $isAvailable")
            return isAvailable
        }
    }
}