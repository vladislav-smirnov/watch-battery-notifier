package io.github.airdaydreamers.batterynotifier.watch.receivers

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.airdaydreamers.batterynotifier.watch.services.BootCompletedJobService

/*I use BootCompletedReceiver because it's only one way
to schedule a job which will wait charging.
Actually Google on this page https://developer.android.com/training/monitoring-device-state/battery-monitoring
we should use

<receiver android:name=".PowerConnectionReceiver">
  <intent-filter>
    <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
    <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
  </intent-filter>
</receiver>

But it will not work because Android 8 and above has Background Execution Limits, Android provides
Implicit Broadcast Exceptions.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootCompletedReceiver"
        const val JOB_ID = 166 //temp value. need to use job id from the list
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: action = ${intent.action}")

        JobInfo.Builder(JOB_ID, ComponentName(context, BootCompletedJobService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build().also {
                val scheduler =
                    context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

                scheduler.schedule(it).also { result ->
                    if (result == JobScheduler.RESULT_SUCCESS) {
                        Log.v(TAG, "Job was scheduled successfully")
                    } else {
                        Log.v(TAG, "Job was not scheduled")
                    }
                }
            }
    }
}