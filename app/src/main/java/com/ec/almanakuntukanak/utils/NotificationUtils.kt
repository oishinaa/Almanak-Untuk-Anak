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
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.receiver.SnoozeReceiver
import java.util.*

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
        var upcoming = -1
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
                    val notes = result.getString(result.getColumnIndex(DBHelper.visit_notes))
                    val entry = db.getEntry(kunjunganModel.entry_id)
                    val name = if (entry != null) if (entry.moveToFirst()) entry.getString(entry.getColumnIndex(DBHelper.entry_name)) else "hm" else ""
                    kunjunganModel.type = result.getInt(result.getColumnIndex(DBHelper.visit_type))
                    kunjunganModel.info = "Hari ini ada "
                    kunjunganModel.info += (if (kunjunganModel.type == 1) "imunisasi $notes" else "pemeriksaan SDI-DTK") + " untuk $name!"
                    kunjunganModel.id = result.getInt(result.getColumnIndex(DBHelper.visit_id))
                    if (kunjunganModel.date != 0) {
                        val tempDate = DateUtils().dbFormatter.parse(kunjunganModel.date.toString())
                        val tempTime = DateUtils().tmFormatter.parse(kunjunganModel.time)
                        val clnd = Calendar.getInstance()
                        clnd.set(DateUtils().getDatePart("yyyy", tempDate!!), DateUtils().getDatePart("MM", tempDate)-1, DateUtils().getDatePart("dd", tempDate),
                            DateUtils().getDatePart("HH", tempTime!!), DateUtils().getDatePart("mm", tempTime))


                        listKunjungan.add(kunjunganModel)

                        if (!isSet && Date().time < clnd.timeInMillis && kunjunganModel.status != 1) {
                            Log.v("notif", "${clnd.time} ${Date()}")
                            upcoming = i
                            isSet = true
                        }
                    }
                    i++
                } while (result.moveToNext())
                if (upcoming != -1) {
                    val tempDate = DateUtils().dbFormatter.parse(listKunjungan[upcoming].date.toString())
                    val tempTime = DateUtils().tmFormatter.parse(listKunjungan[upcoming].time)

                    val clnd = Calendar.getInstance()
                    clnd.set(DateUtils().getDatePart("yyyy", tempDate!!), DateUtils().getDatePart("MM", tempDate)-1, DateUtils().getDatePart("dd", tempDate),
                        DateUtils().getDatePart("HH", tempTime!!), DateUtils().getDatePart("mm", tempTime))
                    clnd.add(Calendar.DATE, -5)
                    alarm = DateUtils().dtFormatter(clnd.time)
                    AlarmUtils(this).setAlarm(clnd, listKunjungan[upcoming].type.toString(), listKunjungan[upcoming].info, listKunjungan[upcoming].id.toString())
                }
            }
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT }.let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        }

        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Info")
            .setContentText(if (alarm == "") "Alarm belum terpasang, silahkan isi data anda." else "Alarm berikutnya pada $alarm.")
            .setSmallIcon(R.drawable.logo_tp_grey)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.logo_bg))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun getAlarmNotifBuilder(type: String, text: String, id: String): NotificationCompat.Builder {
        val intent: Intent
        val icon: Int
        if (type == "1") {
            intent = Intent(this, ImunisasiActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            icon = R.drawable.ic_syringe
        } else if (type == "2") {
            intent = Intent(this, PemeriksaanActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            icon = R.drawable.ic_baby_bed
        } else {
            intent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            icon = R.drawable.logo_tp_grey
        }

        val actionIntent = PendingIntent.getBroadcast(this, (1..2147483647).random(), Intent(this, SnoozeReceiver::class.java).putExtra("id", id).putExtra("act", "open"), flag)
        val snoozeIntent = PendingIntent.getBroadcast(this, (1..2147483647).random(), Intent(this, SnoozeReceiver::class.java).putExtra("id", id).putExtra("act", "none"), flag)
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Alarm")
            .setContentText(text)
            .setSmallIcon(R.drawable.logo_tp_grey)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.logo_bg))
            .addAction(icon, "Buka aplikasi", actionIntent)
            .addAction(icon, "Ingatkan besok", snoozeIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
    }
}