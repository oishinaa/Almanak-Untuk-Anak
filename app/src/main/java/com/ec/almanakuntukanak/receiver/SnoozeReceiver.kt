package com.ec.almanakuntukanak.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.MainActivity
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity
import com.ec.almanakuntukanak.utils.NotificationUtils
import com.ec.almanakuntukanak.tracker.AudioTracker
import com.ec.almanakuntukanak.tracker.ServiceTracker
import com.ec.almanakuntukanak.utils.DateUtils
import java.util.*

class SnoozeReceiver: BroadcastReceiver() {
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        AudioTracker.getMediaPlayerInstance().stopAudio()
        NotificationUtils(context).getManager().cancel(2)
        ServiceTracker().actionOnService(context, "start")

        val open = intent.getBooleanExtra("id", false)
        if (open) {
            context.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}