package com.ec.almanakuntukanak.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.MainActivity
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.receiver.SnoozeReceiver
import java.util.*
import kotlin.collections.ArrayList

class NotificationUtils(base: Context): ContextWrapper(base) {
    private val channelId = "App Alert Notification ID"
    private val channelName = "App Alert Notification"
    private val db = DBHelper(this, null)
    private val flag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    // Create channel for Android version 26+
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }

    // Get Manager
    fun getManager(): NotificationManager {
        if (manager == null) manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    @SuppressLint("Range")
    fun getInfoNotifBuilder(): Notification {
        var alarm = ""
        var i = 0
        var isSet = false
        var message = ""
        var info = ""
        var fullInfo = ""
        val staFrom = Calendar.getInstance()
        val upUntil = Calendar.getInstance()
        val arrUpImunisasi = ArrayList<String>()
        val arrImunisasi = ArrayList<String>()
        val arrPemeriksaan = ArrayList<String>()
        val listKunjungan = ArrayList<KunjunganModel>()
        val result = db.getVisits()
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    val kunjunganModel = KunjunganModel()
                    kunjunganModel.date = result.getInt(result.getColumnIndex(DBHelper.visit_alarm))
                    kunjunganModel.time = result.getString(result.getColumnIndex(DBHelper.visit_time))
                    kunjunganModel.status = result.getInt(result.getColumnIndex(DBHelper.visit_status))
                    kunjunganModel.entry_id = result.getInt(result.getColumnIndex(DBHelper.visit_entry_id))
                    val entry = db.getEntry(kunjunganModel.entry_id)
                    kunjunganModel.info = if (entry != null) if (entry.moveToFirst()) entry.getString(entry.getColumnIndex(DBHelper.entry_name)) else "" else ""
                    kunjunganModel.alarm = result.getString(result.getColumnIndex(DBHelper.visit_notes))
                    kunjunganModel.id = result.getInt(result.getColumnIndex(DBHelper.visit_id))
                    kunjunganModel.type = result.getInt(result.getColumnIndex(DBHelper.visit_type))
                    if (kunjunganModel.date != 0) {
                        listKunjungan.add(kunjunganModel)
                        val tempDate = DateUtils().dbFormatter.parse(kunjunganModel.date.toString())
                        val tempTime = DateUtils().tmFormatter.parse(kunjunganModel.time)
                        val clnd = Calendar.getInstance()
                        clnd.set(DateUtils().getDatePart("yyyy", tempDate!!), DateUtils().getDatePart("MM", tempDate)-1, DateUtils().getDatePart("dd", tempDate),
                            DateUtils().getDatePart("HH", tempTime!!), DateUtils().getDatePart("mm", tempTime), 0)
                        if (Date().time < clnd.timeInMillis && kunjunganModel.status == 0 && (!isSet || clnd.timeInMillis < upUntil.timeInMillis)) {
                            val tempActDate = DateUtils().dbFormatter.parse(result.getString(result.getColumnIndex(DBHelper.visit_date)))
                            val actClnd = Calendar.getInstance()
                            actClnd.set(DateUtils().getDatePart("yyyy", tempActDate!!), DateUtils().getDatePart("MM", tempActDate)-1, DateUtils().getDatePart("dd", tempActDate),
                                DateUtils().getDatePart("HH", tempTime), DateUtils().getDatePart("mm", tempTime), 0)
                            Log.v("notif", "${clnd.time} ${actClnd.time} ${kunjunganModel.alarm}")
                            if (!isSet) {
                                alarm = "Alarm berikutnya pada ${DateUtils().dtFormatter(clnd.time)}."
                                message = "Alarm berikutnya pada ${DateUtils().dtFormatter(clnd.time)}."

                                staFrom.set(clnd.get(Calendar.YEAR), clnd.get(Calendar.MONTH), clnd.get(Calendar.DATE), clnd.get(Calendar.HOUR_OF_DAY), clnd.get(Calendar.MINUTE), 0)
                                upUntil.set(clnd.get(Calendar.YEAR), clnd.get(Calendar.MONTH), clnd.get(Calendar.DATE), 0, 0, 0)
                                upUntil.add(Calendar.DATE, 7)
                            }
                            if (clnd.timeInMillis/1000 == staFrom.timeInMillis/1000 && kunjunganModel.type == 1) {
                                if (actClnd.timeInMillis/1000 == clnd.timeInMillis/1000) {
                                    info += (if (info.isEmpty()) "Hari ini ada " else ", ") + "imunisasi ${kunjunganModel.alarm} untuk ${kunjunganModel.info}"
                                    fullInfo = info
                                } else {
                                    arrUpImunisasi.add("Jadwal imunisasi ${kunjunganModel.alarm} untuk ${kunjunganModel.info} pada ${DateUtils().dtFormatter(actClnd.time)}.")
                                }
                            }
                            // message
                            if (kunjunganModel.type == 2) {
                                if (Date().time >= actClnd.timeInMillis) arrPemeriksaan.add(kunjunganModel.info)
                            } else {
                                arrImunisasi.add("\nJadwal imunisasi ${kunjunganModel.alarm} untuk ${kunjunganModel.info} pada ${DateUtils().dtFormatter(actClnd.time)}.")
                            }
                            isSet = true
                        }
                        i++
                    }
                } while (result.moveToNext())
                for(a in arrUpImunisasi) {
                    if (info.isEmpty()) {
                        info = a
                        fullInfo = a
                    }
                    else fullInfo += "\n$a"
                }
                for(a in arrImunisasi) message += a
                if (arrPemeriksaan.size > 0) {
                    var msgPeriksa = "Segera lakukan pemeriksaan stimulasi dini & intervensi deteksi tumbuh kembang untuk "
                    for ((j, a) in arrPemeriksaan.withIndex()) msgPeriksa += "${if(arrPemeriksaan.size > 1 && j == arrPemeriksaan.size-1) " dan " else if (j != 0) ", " else ""}$a"
                    msgPeriksa += " di bidan desa atau Kader."
                    message += "\n$msgPeriksa"
                    if (info.isEmpty()) {
                        info = msgPeriksa
                        fullInfo = msgPeriksa
                    }
                    else fullInfo += "\n$msgPeriksa"
                }
                AlarmUtils(this).setAlarm(staFrom, "$info!", fullInfo)
            }
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT }.let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        }

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Info")
            .setContentText(if (alarm == "") "Alarm belum terpasang, silahkan isi data anda." else alarm)
            .setStyle(if (message != "") bigText else null)
            .setSmallIcon(R.drawable.logo_tp_grey)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.logo_bg))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun getAlarmNotifBuilder(text: String, message: String): NotificationCompat.Builder {
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)

        val pendingIntent = PendingIntent.getBroadcast(this, (1..2147483647).random(), Intent(this, SnoozeReceiver::class.java).putExtra("open", true), flag)
        val snoozeIntent = PendingIntent.getBroadcast(this, (1..2147483647).random(), Intent(this, SnoozeReceiver::class.java).putExtra("open", false), flag)
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Alarm")
            .setContentText(text)
            .setStyle(if (message != "") bigText else null)
            .setSmallIcon(R.drawable.logo_tp_grey)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.logo_bg))
            .addAction(R.drawable.logo_tp_grey, "Buka aplikasi", pendingIntent)
            .addAction(R.drawable.logo_tp_grey, "Matikan Alarm", snoozeIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
    }
}