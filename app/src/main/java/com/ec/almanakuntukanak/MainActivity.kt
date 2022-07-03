package com.ec.almanakuntukanak

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ec.almanakuntukanak.controller.panduan.PanduanActivity
import com.ec.almanakuntukanak.controller.peserta.PesertaActivity
import com.ec.almanakuntukanak.tracker.AudioTracker
import com.ec.almanakuntukanak.tracker.ServiceTracker
import com.ec.almanakuntukanak.utils.NotificationUtils
import com.google.android.material.button.MaterialButton

class MainActivity: AppCompatActivity() {
    private lateinit var btnImunisasi: MaterialButton
    private lateinit var btnPemeriksaan: MaterialButton
    private lateinit var btnPanduan: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Menu Utama"

        btnImunisasi = findViewById(R.id.btnImunisasi)
        btnPemeriksaan = findViewById(R.id.btnPemeriksaan)
        btnPanduan = findViewById(R.id.btnPanduan)

        btnImunisasi.setOnClickListener {
            startActivity(Intent(this, PesertaActivity::class.java).putExtra("from", "imunisasi"))
        }
        btnPemeriksaan.setOnClickListener {
            startActivity(Intent(this, PesertaActivity::class.java).putExtra("from", "pemeriksaan"))
        }
        btnPanduan.setOnClickListener {
            startActivity(Intent(this, PanduanActivity::class.java))
        }

        AudioTracker.getMediaPlayerInstance().stopAudio()
        NotificationUtils(this).getManager().cancel(2)
        ServiceTracker().actionOnService(this, "start")
    }
}