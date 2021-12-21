package io.github.airdaydreamers.batterynotifier.watch.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.BatteryManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BootCompletedJobService : JobService() {
    companion object {
        const val TAG = "BootCompletedJobService"
    }

    private lateinit var parameters: JobParameters
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    var ringtone: Ringtone? = null //Will be replaced latter
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: action = ${intent?.action}")
            intent?.also {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
                val batteryPercent = level * 100 / scale.toFloat()

                Log.v(TAG, "Battery Percent = $batteryPercent")

                if (batteryPercent == 100.0f) {

                    //region  will be changed to audiManager. This is temp solution for testing
                    val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
                    ringtone?.isLooping = false
                    ringtone?.play()

                    //endregion

                    unregisterReceiver(this) //need to sync information about this receiver and don't unregister twice
                }
            }

        }
    }

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(TAG, "onReceive: action = ${p1?.action}")

            when (p1?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    Log.v(TAG, "onReceive: register to battery changed")
                    registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Log.v(TAG, "unregister")
                    try {
                        ringtone?.stop()
                        unregisterReceiver(batteryReceiver)
                    } catch (iae: IllegalArgumentException) {
                        Log.w(TAG, "Receiver was not registered")
                    }
                }
            }
        }

    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob")

        if (params == null) {
            return false
        }

        parameters = params

        scope.launch { //doesn't need. will be removed.
            IntentFilter(Intent.ACTION_POWER_CONNECTED)
                .apply { this.addAction(Intent.ACTION_POWER_DISCONNECTED) }.let {
                    applicationContext.registerReceiver(powerReceiver, it)
                }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.v(TAG, "onStopJob")
        try {
            applicationContext.unregisterReceiver(powerReceiver)
        } catch (iae: IllegalArgumentException) {
            Log.w(TAG, "Receiver was not registered")
        }
        return true
    }

}