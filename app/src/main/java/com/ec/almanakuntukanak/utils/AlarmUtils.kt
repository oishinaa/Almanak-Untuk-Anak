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
import com.ec.almanakuntukanak.receiver.AlarmReceiver
import com.ec.almanakuntukanak.tracker.ServiceTracker
import java.text.SimpleDateFormat
import java.util.*

class AlarmUtils(context: Context): ContextWrapper(context) {
    private val flag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    private val db = DBHelper(this, null)

    @SuppressLint("Range")
    fun setAlarm(date: Calendar, type: String, text: String) {
        if (DateUtils().dbFormatter.format(date.time) < DateUtils().dbFormatter.format(Date()) || (
                DateUtils().dbFormatter.format(date.time) == DateUtils().dbFormatter.format(Date()) &&
                DateUtils().tmFormatter.format(date.time) < DateUtils().dbFormatter.format(Date())
            )
        ) date.add(Calendar.DATE, 1)

        /*val result = db.getAlarm()
        if (result != null) {
            if (!result.moveToFirst()) {
                db.addAlarm(DateUtils().dbFormatter.format(date.time).toInt(), DateUtils().tmFormatter.format(date.time))
                ServiceTracker().actionOnService(this, "start")
            } else {
                val currAlarmDate = result.getInt(result.getColumnIndex(DBHelper.alarm_date)).toString()
                val currAlarmTime = result.getString(result.getColumnIndex(DBHelper.alarm_time))

                if (currAlarmDate != DateUtils().dbFormatter.format(date.time) || currAlarmTime != DateUtils().tmFormatter.format(date.time)) {
                    db.updAlarm(DateUtils().dbFormatter.format(date.time).toInt(), DateUtils().tmFormatter.format(date.time))
                    ServiceTracker().actionOnService(this, "start")
                }
            }
        }*/

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("type", type)
        intent.putExtra("text", text)

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.timeInMillis, pendingIntent)
    }

    @SuppressLint("Range")
    fun snoozeAlarm(type: String, text: String) {
        /*val result = db.getAlarm()
        if (result != null) {
            if (result.moveToFirst()) {
                val currAlarmDate = result.getInt(result.getColumnIndex(DBHelper.alarm_date)).toString()
                val currAlarmTime = result.getString(result.getColumnIndex(DBHelper.alarm_time))

                if (currAlarmDate != "0") {
                    Log.v("log", currAlarmTime + " " + currAlarmDate)
                    val tempDate = DateUtils().dbFormatter.parse(currAlarmDate)
                    val tempTime = DateUtils().tmFormatter.parse(currAlarmTime)

                    val currAlarmClnd = Calendar.getInstance()
                    currAlarmClnd.set(DateUtils().getDatePart("yyyy", tempDate!!), DateUtils().getDatePart("MM", tempDate)-1, DateUtils().getDatePart("dd", tempDate),
                            DateUtils().getDatePart("HH", tempTime!!), DateUtils().getDatePart("mm", tempTime))
                    currAlarmClnd.add(Calendar.DATE, 1)
                    db.updAlarm(DateUtils().dbFormatter.format(currAlarmClnd.time).toInt(), DateUtils().tmFormatter.format(currAlarmClnd.time))

                    val intent = Intent(this, AlarmReceiver::class.java)
                    intent.putExtra("type", type)
                    intent.putExtra("text", text)

                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, currAlarmClnd.timeInMillis, pendingIntent)
                }
            }
        }*/
    }

    @SuppressLint("Range")
    fun unsetAlarm() {
        /*val result = db.getAlarm()
        if (result != null) {
            if (result.moveToFirst()) {
                val currAlarmDate = result.getInt(result.getColumnIndex(DBHelper.alarm_date)).toString()
                if (currAlarmDate != "0") {
                    db.updAlarm(0, "")
                    ServiceTracker().actionOnService(this, "start")
                }
            }
        }*/

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)
        alarmManager.cancel(pendingIntent)
    }
}