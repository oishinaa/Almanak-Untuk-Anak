package com.ec.almanakuntukanak.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
        val id = intent.getStringExtra("id")
        val act = intent.getStringExtra("act")
        val db = DBHelper(context, null)
        val result = db.getVisit(id!!)
        if (result != null) {
            if (result.moveToFirst()) {
                val date = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.visit_alarm)).toString())
                val clnd = Calendar.getInstance()
                val type = result.getString(result.getColumnIndex(DBHelper.visit_type))
                clnd.set(DateUtils().getDatePart("yyyy", date!!), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date))
                clnd.add(Calendar.DATE, 1)
                db.snoozeVisit(id, DateUtils().dbFormatter.format(clnd.time).toInt())
                if (act == "open") {
                    val intent: Intent
                    if (type == "1") {
                        intent = Intent(context, ImunisasiActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    } else if (type == "2") {
                        intent = Intent(context, PemeriksaanActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    } else {
                        intent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent.putExtra("id", result.getInt(result.getColumnIndex(DBHelper.visit_entry_id))))
                } else {
                    ServiceTracker().actionOnService(context, "start")
                }
            }
        }
    }
}