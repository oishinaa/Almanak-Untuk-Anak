package com.ec.almanakuntukanak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ec.almanakuntukanak.utils.NotificationUtils
import com.ec.almanakuntukanak.tracker.AudioTracker
import com.ec.almanakuntukanak.utils.AlarmUtils
import java.util.*
import kotlin.concurrent.timerTask

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AudioTracker.getMediaPlayerInstance().startAudio(context)

        val type = intent.getStringExtra("type")
        val text = intent.getStringExtra("text")
        val notification = NotificationUtils(context).getAlarmNotifBuilder(type!!, text!!).build()
        NotificationUtils(context).getManager().notify(2, notification)
        AlarmUtils(context).snoozeAlarm(type, text)

        Timer().schedule(timerTask { AudioTracker.getMediaPlayerInstance().stopAudio() }, 60 * 1000)
    }
}