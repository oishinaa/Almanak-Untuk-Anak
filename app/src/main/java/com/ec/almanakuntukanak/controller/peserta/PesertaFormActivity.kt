package com.ec.almanakuntukanak.controller.peserta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.utils.DateUtils
import java.util.*

class PesertaFormActivity : BaseActivity() {
    private lateinit var edtName: EditText
    private lateinit var rdbPerempuan: RadioButton
    private lateinit var rdbLakilaki: RadioButton
    private lateinit var edtTpl: EditText
    private lateinit var lnlTgl: LinearLayout
    private lateinit var txtTgl: TextView
    private lateinit var edtNIK: EditText
    private lateinit var edtKK: EditText
    private lateinit var edtJKN: EditText
    private lateinit var btn: Button
    private lateinit var tgl: Calendar
    private lateinit var from: String
    private var id: Int = 0

    private val db = DBHelper(this, null)

    private val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        tgl.set(year, month, day, 0, 0, 0)
        txtTgl.text = DateUtils().dpFormatter(tgl.time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peserta_form)

        val actionBar = supportActionBar
        actionBar!!.title = "${if (intent.getIntExtra("id", 0) == 0) "Tambah" else "Ubah"} Data Peserta"
        actionBar.setDisplayHomeAsUpEnabled(true)

        edtName = findViewById(R.id.edtName)
        rdbPerempuan = findViewById(R.id.rdbPerempuan)
        rdbLakilaki = findViewById(R.id.rdbLakilaki)
        edtTpl = findViewById(R.id.edtTpl)
        lnlTgl = findViewById(R.id.lnlTgl)
        txtTgl = findViewById(R.id.txtTgl)
        edtNIK = findViewById(R.id.edtNIK)
        edtKK = findViewById(R.id.edtKK)
        edtJKN = findViewById(R.id.edtJKN)
        btn = findViewById(R.id.btn)

        id = intent.getIntExtra("id", 0)
        from = intent.getStringExtra("from").toString()
        tgl = Calendar.getInstance()
        txtTgl.text = DateUtils().dpFormatter(tgl.time)

        if (id != 0) load()
        lnlTgl.setOnClickListener {
            if (id == 0) showDatePickerDialog(onDateSetListener, tgl)
            else {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Tidak dapat mengubah tanggal lahir.")
                builder.setPositiveButton("Oke") { _,_ -> }
                builder.show()
            }
        }
        btn.setOnClickListener { if (id == 0) add() else edit() }
    }

    @SuppressLint("Range")
    fun load() {
        val result = db.getEntry(id)
        if (result != null) {
            if (result.moveToFirst()) {
                edtName.setText(result.getString(result.getColumnIndex(DBHelper.entry_name)))
                if (result.getInt(result.getColumnIndex(DBHelper.entry_jk)) == 1) rdbPerempuan.isChecked = true
                else rdbLakilaki.isChecked = true
                edtTpl.setText(result.getString(result.getColumnIndex(DBHelper.entry_tpl)))
                val date = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.entry_tgl)).toString())
                tgl.set(DateUtils().getDatePart("yyyy", date!!), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date), 0, 0, 0)
                txtTgl.text = DateUtils().dpFormatter(tgl.time)
                edtNIK.setText(result.getString(result.getColumnIndex(DBHelper.entry_nik)))
                edtKK.setText(result.getString(result.getColumnIndex(DBHelper.entry_kk)))
                edtJKN.setText(result.getString(result.getColumnIndex(DBHelper.entry_jkn)))
            }
        }
    }

    private fun add() {
        val jk = if (rdbPerempuan.isChecked) 1 else 2
        db.addEntry(edtName.text.toString(), jk, edtTpl.text.toString(), DateUtils().dbFormatter.format(tgl.time).toInt(),
            edtNIK.text.toString(), edtKK.text.toString(), edtJKN.text.toString())
        insertVisit()
        finish()
        restartActivity()
    }

    private fun edit() {
        val jk = if (rdbPerempuan.isChecked) 1 else 2
        db.updEntry(id, edtName.text.toString(), jk, edtTpl.text.toString(), DateUtils().dbFormatter.format(tgl.time).toInt(),
            edtNIK.text.toString(), edtKK.text.toString(), edtJKN.text.toString())
        finish()
        restartActivity()
    }

    @SuppressLint("Range")
    private fun insertVisit() {
        if (id == 0) {
            val result = db.getLastEntry()
            if (result.moveToFirst()) {
                id = result.getInt(result.getColumnIndex(DBHelper.entry_id))
            }
        }

        if (id != 0) {
            db.delAllVisits(2, id)
            val dateArr = arrayOf(Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(),
                Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance())
            val datePartArr = arrayOf(Calendar.DATE, Calendar.MONTH, Calendar.MONTH, Calendar.MONTH, Calendar.MONTH,
                Calendar.MONTH, Calendar.YEAR, Calendar.YEAR, Calendar.YEAR, Calendar.YEAR)
            val amountArr = arrayOf(29, 3, 6, 9, 12, 18, 2, 3, 4, 5)
            for((i, dt) in dateArr.withIndex()) {
                dt.set(tgl.get(Calendar.YEAR), tgl.get(Calendar.MONTH), tgl.get(Calendar.DATE), 7, 0, 0)
                dt.add(datePartArr[i], amountArr[i])
            }

            for(dt in dateArr) {
                db.addVisit(id, 2, DateUtils().dbFormatter.format(dt.time).toInt(), DateUtils().dbFormatter.format(dt.time).toInt(), "07:00", "", 0)
            }
        }
    }

    private fun restartActivity() {
        sendBroadcast(Intent("finish ps"))
        val intent = Intent(this, PesertaActivity::class.java)
        intent.putExtra("from", from)
        startActivity(intent)
    }
}