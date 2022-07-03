package com.ec.almanakuntukanak.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.MainActivity
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity
import com.ec.almanakuntukanak.receiver.AlarmReceiver
import com.ec.almanakuntukanak.tracker.ServiceTracker
import java.text.SimpleDateFormat
import java.util.*

class AlarmUtils(context: Context): ContextWrapper(context) {
    private val flag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    private val db = DBHelper(this, null)

    @SuppressLint("Range")
    fun setAlarm(date: Calendar, text: String, fullText: String) {
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("text", text)
        intent.putExtra("fullText", fullText)
        intent.putExtra("date", DateUtils().dbFormatter.format(date.time))
        intent.putExtra("time", DateUtils().tmFormatter.format(date.time))

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.timeInMillis, pendingIntent)
    }

    @SuppressLint("Range")
    fun snoozeAlarm(date: String, time: String) {
        val result = db.getVisitsByDate(date, time)
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    val id = result.getInt(result.getColumnIndex(DBHelper.visit_id)).toString()
                    val tp = result.getInt(result.getColumnIndex(DBHelper.visit_type))
                    val alrm = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.visit_alarm)).toString())
                    val schd = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.visit_date)).toString())
                    if (tp == 2 || result.getInt(result.getColumnIndex(DBHelper.visit_alarm)).toString() != result.getInt(result.getColumnIndex(DBHelper.visit_date)).toString()) {
                        Log.v("kapan", "$alrm $schd ${result.getString(result.getColumnIndex(DBHelper.visit_notes))}")
                        val clnd = Calendar.getInstance()
                        clnd.set(DateUtils().getDatePart("yyyy", alrm!!), DateUtils().getDatePart("MM", alrm)-1, DateUtils().getDatePart("dd", alrm))
                        clnd.add(Calendar.DATE, 1)
                        db.snoozeVisit(id, DateUtils().dbFormatter.format(clnd.time).toInt())
                    }
                } while (result.moveToNext())
            }
        }
    }
}