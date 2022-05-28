package com.ec.almanakuntukanak.controller.imunisasi

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.ImunisasiAdapter
import com.ec.almanakuntukanak.model.ImunisasiModel

class ImunisasiActivity : BaseActivity() {
    private lateinit var lnlDetail: LinearLayout
    private lateinit var lnlToggleDetail: LinearLayout
    private lateinit var txtToggleDetail: TextView
    private lateinit var imgToggleDetail: ImageView
    private lateinit var spnImunisasi: Spinner
    private lateinit var txtJadwal: TextView
    private lateinit var txtSudah: TextView
    private lateinit var rcvImunisasi: RecyclerView
    private lateinit var btn: Button
    private lateinit var lnl: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imunisasi)

        val actionBar = supportActionBar
        actionBar!!.title = "Imunisasi"
        actionBar.setDisplayHomeAsUpEnabled(true)

        lnlDetail = findViewById(R.id.lnlDetail)
        lnlToggleDetail = findViewById(R.id.lnlToggleDetail)
        txtToggleDetail = findViewById(R.id.txtToggleDetail)
        imgToggleDetail = findViewById(R.id.imgToggleDetail)
        spnImunisasi = findViewById(R.id.spnImunisasi)
        rcvImunisasi = findViewById(R.id.rcvImunisasi)
        txtJadwal = findViewById(R.id.txtJadwal)
        txtSudah = findViewById(R.id.txtSudah)
        btn = findViewById(R.id.btn)
        lnl = findViewById(R.id.lnl)

        lnlToggleDetail.setOnClickListener{
            toggleDetail()
        }

        val types = arrayOf("Hep-B 0", "BCG", "Polio 1", "DPT,HB,Hib1", "PVC/Pneumokokus 1")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spnImunisasi.adapter = adapter

        spnImunisasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@ImunisasiActivity, types[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        txtJadwal.setOnClickListener {
            loadImunisasi("jadwal")
        }
        txtSudah.setOnClickListener {
            loadImunisasi("sudah")
        }

        loadImunisasi("jadwal")
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

    private fun loadImunisasi(type: String) {
        if (type == "jadwal") {
            txtJadwal.setTextColor(ContextCompat.getColor(this, R.color.ic_white))
            txtJadwal.background = ContextCompat.getDrawable(this, R.drawable.bg_corner_t_pink)
            txtSudah.setTextColor(ContextCompat.getColor(this, R.color.dark))
            txtSudah.background = ContextCompat.getDrawable(this, R.drawable.bg_corner_t_grey)
            btn.visibility = View.VISIBLE
            lnl.visibility = View.GONE
        } else {
            txtSudah.setTextColor(ContextCompat.getColor(this, R.color.ic_white))
            txtSudah.background = ContextCompat.getDrawable(this, R.drawable.bg_corner_t_pink)
            txtJadwal.setTextColor(ContextCompat.getColor(this, R.color.dark))
            txtJadwal.background = ContextCompat.getDrawable(this, R.drawable.bg_corner_t_grey)
            btn.visibility = View.GONE
            lnl.visibility = View.VISIBLE
        }
        rcvImunisasi.layoutManager = LinearLayoutManager(this)
        val listImunisasi = ArrayList<ImunisasiModel>()
        for (i in 1 until 11) {
            val imunisasiModel = ImunisasiModel()
            imunisasiModel.nama = "PVC/Pneumokokus 1"
            imunisasiModel.date = if (type == "jadwal") "1 Feb 2023 Jam 07:00" else ""
            listImunisasi.add(imunisasiModel)
        }
        rcvImunisasi.adapter = ImunisasiAdapter(this, listImunisasi)
    }
}