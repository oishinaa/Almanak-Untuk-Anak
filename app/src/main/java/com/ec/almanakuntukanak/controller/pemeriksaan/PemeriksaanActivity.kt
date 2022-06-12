package com.ec.almanakuntukanak.controller.pemeriksaan

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

        val broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(arg0: Context, intent: Intent) {
                val action = intent.action
                if (action == "finish p") {
                    finish()
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("finish p"))

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
                imgJk.setImageResource(if (result.getInt(result.getColumnIndex(DBHelper.entry_jk)) == 1) R.drawable.ic_gender_female else R.drawable.ic_gender_male)
                txtNama.text = result.getString(result.getColumnIndex(DBHelper.entry_name))
                val date = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.entry_tgl)).toString())
                val year = DateUtils().getDatePart("yyyy", Date()) - DateUtils().getDatePart("yyyy", date!!)
                val day = DateUtils().getDatePart("dd", Date()) - DateUtils().getDatePart("dd", date)
                val month = if (year < 0) 0 else 12 * year + (DateUtils().getDatePart("MM", Date()) - DateUtils().getDatePart("MM", date)) + if (day < 0) -1 else 0
                val umur = "" + (if (month < 0) 0 else month/12) + " Tahun " + (if (month < 0) 0 else month%12) + " Bulan"
                txtUmur.text = umur
                val tgl = Calendar.getInstance()
                tgl.set(DateUtils().getDatePart("yyyy", date), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date))
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

    @SuppressLint("Range")
    private fun loadPemeriksaan() {
        rcvPemeriksaan.layoutManager = LinearLayoutManager(this)
        var i = 0
        var isSet = false
        val listPemeriksaan = ArrayList<PemeriksaanModel>()
        val arrPeriode = arrayOf("29 Hari", "3 Bulan", "6 Bulan", "9 Bulan", "12 Bulan", "18 Bulan", "2 Tahun", "3 Tahun", "4 Tahun", "5 Tahun")
        val result = db.getVisits(2, id)
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    val pemeriksaanModel = PemeriksaanModel()
                    pemeriksaanModel.id = result.getInt(result.getColumnIndex(DBHelper.visit_id))
                    pemeriksaanModel.entry_id = result.getInt(result.getColumnIndex(DBHelper.visit_entry_id))
                    pemeriksaanModel.date = result.getInt(result.getColumnIndex(DBHelper.visit_date))
                    pemeriksaanModel.time = result.getString(result.getColumnIndex(DBHelper.visit_time))
                    val dt = DateUtils().dbFormatter.parse(pemeriksaanModel.date.toString())
                    val tm = DateUtils().tmFormatter.parse(pemeriksaanModel.time)
                    pemeriksaanModel.status = result.getInt(result.getColumnIndex(DBHelper.visit_status))
                    pemeriksaanModel.periode = arrPeriode[i]
                    pemeriksaanModel.alarm = "Alarm akan berbunyi pada " + DateUtils().dpFormatter(dt!!) + " Jam " + DateUtils().tmFormatter.format(tm!!)
                    listPemeriksaan.add(pemeriksaanModel)
                    if (!isSet && DateUtils().dbFormatter.format(Date()).toInt() < result.getInt(result.getColumnIndex(DBHelper.visit_date)) && pemeriksaanModel.status != 1) {
                        listPemeriksaan[if (i == 0) 0 else i-(if (listPemeriksaan[i-1].status == 1) 0 else 1)].status = 2
                        isSet = true
                    }
                    i++
                } while (result.moveToNext())
            }
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