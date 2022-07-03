package com.ec.almanakuntukanak.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ec.almanakuntukanak.utils.NotificationUtils
import com.ec.almanakuntukanak.tracker.AudioTracker
import com.ec.almanakuntukanak.tracker.ServiceTracker

class SnoozeReceiver: BroadcastReceiver() {
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        AudioTracker.getMediaPlayerInstance().stopAudio()
        NotificationUtils(context).getManager().cancel(2)
        ServiceTracker().actionOnService(context, "start")
    }
}