package com.ec.almanakuntukanak.controller.peserta

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.PesertaAdapter
import com.ec.almanakuntukanak.model.PesertaModel
import com.ec.almanakuntukanak.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

class PesertaActivity : BaseActivity() {
    private lateinit var from: String
    private lateinit var rcvPeserta: RecyclerView
    private lateinit var btn: Button

    private val db = DBHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peserta)

        val actionBar = supportActionBar
        actionBar!!.title = "Data Peserta"
        actionBar.setDisplayHomeAsUpEnabled(true)

        from = intent.getStringExtra("from").toString()
        rcvPeserta = findViewById(R.id.rcvPeserta)
        btn = findViewById(R.id.btn)

        val broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(arg0: Context, intent: Intent) {
                val action = intent.action
                if (action == "finish ps") {
                    finish()
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("finish ps"))

        btn.setOnClickListener {
            startActivity(Intent(this, PesertaFormActivity::class.java).putExtra("id", 0).putExtra("from", from))
        }

        loadPeserta()
    }

    @SuppressLint("Range")
    private fun loadPeserta() {
        rcvPeserta.layoutManager = LinearLayoutManager(this)
        val items = ArrayList<PesertaModel>()
        val result = db.getEntries()
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    val tpl = result.getString(result.getColumnIndex((DBHelper.entry_tpl)))
                    val item = PesertaModel()
                    item.id = result.getInt(result.getColumnIndex(DBHelper.entry_id))
                    item.nama = result.getString(result.getColumnIndex(DBHelper.entry_name))
                    item.jk = if (result.getInt(result.getColumnIndex(DBHelper.entry_jk)) == 1) "Perempuan" else "Laki-laki"
                    val date = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.entry_tgl)).toString())
                    val tgl = Calendar.getInstance()
                    tgl.set(DateUtils().getDatePart("yyyy", date!!), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date), 0, 0, 0)
                    item.ttl = (if (tpl.isNotEmpty()) "$tpl, " else "") + DateUtils().dpFormatter(tgl.time)
                    items.add(item)
                } while (result.moveToNext())
            }
        }
        rcvPeserta.adapter = PesertaAdapter(this, items, from)
    }
}