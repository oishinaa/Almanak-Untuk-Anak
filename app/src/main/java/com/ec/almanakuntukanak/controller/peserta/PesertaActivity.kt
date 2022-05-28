package com.ec.almanakuntukanak.controller.peserta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.PesertaAdapter
import com.ec.almanakuntukanak.model.PesertaModel

class PesertaActivity : BaseActivity() {
    private lateinit var from: String
    private lateinit var rcvPeserta: RecyclerView
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peserta)

        val actionBar = supportActionBar
        actionBar!!.title = "Data Peserta"
        actionBar.setDisplayHomeAsUpEnabled(true)

        from = intent.getStringExtra("from").toString()
        rcvPeserta = findViewById(R.id.rcvPeserta)
        btn = findViewById(R.id.btn)

        btn.setOnClickListener {
            startActivity(Intent(this, PesertaFormActivity::class.java))
        }

        loadPeserta()
    }

    private fun loadPeserta() {
        rcvPeserta.layoutManager = LinearLayoutManager(this)
        val items = ArrayList<PesertaModel>()
        for (i in 1 until 8) {
            val item = PesertaModel()
            item.nama = "Sari"
            item.jk = "Perempuan"
            item.ttl = "Malang, 25 Januari 2022"
            items.add(item)
        }
        rcvPeserta.adapter = PesertaAdapter(this, items, from)
    }
}