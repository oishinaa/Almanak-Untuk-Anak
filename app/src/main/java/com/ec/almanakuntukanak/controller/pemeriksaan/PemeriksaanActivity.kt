package com.ec.almanakuntukanak.controller.pemeriksaan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.PemeriksaanAdapter
import com.ec.almanakuntukanak.model.PemeriksaanModel

class PemeriksaanActivity : BaseActivity() {
    private lateinit var lnlDetail: LinearLayout
    private lateinit var lnlToggleDetail: LinearLayout
    private lateinit var txtToggleDetail: TextView
    private lateinit var imgToggleDetail: ImageView
    private lateinit var rcvPemeriksaan: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemeriksaan)

        val actionBar = supportActionBar
        actionBar!!.title = "Pemeriksaan"
        actionBar.setDisplayHomeAsUpEnabled(true)

        lnlDetail = findViewById(R.id.lnlDetail)
        lnlToggleDetail = findViewById(R.id.lnlToggleDetail)
        txtToggleDetail = findViewById(R.id.txtToggleDetail)
        imgToggleDetail = findViewById(R.id.imgToggleDetail)
        rcvPemeriksaan = findViewById(R.id.rcvPemeriksaan)

        lnlToggleDetail.setOnClickListener{
            toggleDetail()
        }

        loadPemeriksaan()
    }

    private fun loadPemeriksaan() {
        rcvPemeriksaan.layoutManager = LinearLayoutManager(this)
        val listPemeriksaan = ArrayList<PemeriksaanModel>()
        for (i in 1 until 11) {
            val pemeriksaanModel = PemeriksaanModel()
            pemeriksaanModel.status = if (i < 3) 1 else if (i == 3) 2 else 0
            pemeriksaanModel.periode = "29 Hari - 3 Bulan"
            pemeriksaanModel.alarm = "1 Feb 2023 Jam 07:00"
            listPemeriksaan.add(pemeriksaanModel)
        }
        rcvPemeriksaan.adapter = PemeriksaanAdapter(this, listPemeriksaan)
    }

    private fun toggleDetail() {
        if (lnlDetail.visibility == View.GONE) {
            lnlDetail.visibility = View.VISIBLE
            txtToggleDetail.text = "Sembunyikan Rincian"
            imgToggleDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_up))
        } else {
            lnlDetail.visibility = View.GONE
            txtToggleDetail.text = "Tampilkan Rincian"
            imgToggleDetail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_down))
        }
    }
}