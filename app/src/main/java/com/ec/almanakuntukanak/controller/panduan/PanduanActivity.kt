package com.ec.almanakuntukanak.controller.panduan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R

class PanduanActivity : BaseActivity() {
    private lateinit var btnNolBulan: Button
    private lateinit var btnTujuhBulan: Button
    private lateinit var btnSembilanBulan: Button
    private lateinit var btnSebelasBulan: Button
    private lateinit var btnSatuTahun: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panduan)

        val actionBar = supportActionBar
        actionBar!!.title = "Pemberian Makan"
        actionBar.setDisplayHomeAsUpEnabled(true)

        btnNolBulan = findViewById(R.id.btnNolBulan)
        btnTujuhBulan = findViewById(R.id.btnTujuhBulan)
        btnSembilanBulan = findViewById(R.id.btnSembilanBulan)
        btnSebelasBulan = findViewById(R.id.btnSebelasBulan)
        btnSatuTahun = findViewById(R.id.btnSatuTahun)

        btnNolBulan.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java).putExtra("fileName", "panduan1"))
        }
        btnTujuhBulan.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java).putExtra("fileName", "panduan2"))
        }
        btnSembilanBulan.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java).putExtra("fileName", "panduan3"))
        }
        btnSebelasBulan.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java).putExtra("fileName", "panduan4"))
        }
        btnSatuTahun.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java).putExtra("fileName", "panduan5"))
        }
    }
}