package com.ec.almanakuntukanak.controller.pemeriksaan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.PemeriksaanAdapter
import com.ec.almanakuntukanak.model.PemeriksaanModel
import com.ec.almanakuntukanak.utils.DateUtils
import com.google.android.material.imageview.ShapeableImageView
import java.util.*
import kotlin.collections.ArrayList

class PemeriksaanActivity : BaseActivity() {
    private lateinit var lnlDetail: LinearLayout
    private lateinit var imgJk: ShapeableImageView
    private lateinit var txtNama: TextView
    private lateinit var txtUmur: TextView
    private lateinit var txtTtl: TextView
    private lateinit var txtNik: TextView
    private lateinit var txtKk: TextView
    private lateinit var txtJkn: TextView
    private lateinit var lnlToggleDetail: LinearLayout
    private lateinit var txtToggleDetail: TextView
    private lateinit var imgToggleDetail: ImageView
    private lateinit var rcvPemeriksaan: RecyclerView
    private var id = 0

    private val db = DBHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pemeriksaan)

        val actionBar = supportActionBar
        actionBar!!.title = "Pemeriksaan"
        actionBar.setDisplayHomeAsUpEnabled(true)

        lnlDetail = findViewById(R.id.lnlDetail)
        imgJk = findViewById(R.id.imgJk)
        txtNama = findViewById(R.id.txtNama)
        txtUmur = findViewById(R.id.txtUmur)
        txtTtl = findViewById(R.id.txtTtl)
        txtNik = findViewById(R.id.txtNik)
        txtKk = findViewById(R.id.txtKk)
        txtJkn = findViewById(R.id.txtJkn)
        lnlToggleDetail = findViewById(R.id.lnlToggleDetail)
        txtToggleDetail = findViewById(R.id.txtToggleDetail)
        imgToggleDetail = findViewById(R.id.imgToggleDetail)
        rcvPemeriksaan = findViewById(R.id.rcvPemeriksaan)

        id = intent.getIntExtra("id", 0)

        lnlToggleDetail.setOnClickListener{
            toggleDetail()
        }

        load()
        loadPemeriksaan()
    }

    @SuppressLint("Range")
    private fun load() {
        val result = db.getEntry(id)
        if (result != null) {
            if (result.moveToFirst()) {
                imgJk.setImageResource(if (result.getInt(result.getColumnIndex(DBHelper.entry_jk)) == 1) R.drawable.ic_gender_male else R.drawable.ic_gender_female)
                txtNama.text = result.getString(result.getColumnIndex(DBHelper.entry_name))
                val date = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.entry_tgl)).toString())
                val tgl = Calendar.getInstance()
                tgl.set(DateUtils().getDatePart("yyyy", date!!), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date))
                val ttl = "TTL: ${result.getString(result.getColumnIndex(DBHelper.entry_tpl))}, ${DateUtils().dpFormatter(tgl.time)}"
                txtTtl.text = ttl
                val nik = "NIK: " + result.getString(result.getColumnIndex(DBHelper.entry_nik))
                txtNik.text = nik
                val kk = "KK: " + result.getString(result.getColumnIndex(DBHelper.entry_kk))
                txtKk.text = kk
                val jkn = "JKN: " + result.getString(result.getColumnIndex(DBHelper.entry_jkn))
                txtJkn.text = jkn
            }
        }
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