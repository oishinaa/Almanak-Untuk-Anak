package com.ec.almanakuntukanak.controller.imunisasi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.ImunisasiAdapter
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.tracker.ServiceTracker
import com.ec.almanakuntukanak.utils.DateUtils
import com.google.android.material.imageview.ShapeableImageView
import java.util.*
import kotlin.collections.ArrayList

class ImunisasiActivity : BaseActivity() {
    private lateinit var lnlDetail: LinearLayout
    private lateinit var imgJk: ShapeableImageView
    private lateinit var txtNama: TextView
    private lateinit var txtUmur: TextView
    private lateinit var txtTtl: TextView
    private lateinit var txtNik: TextView
    private lateinit var txtKk: TextView
    private lateinit var txtJkn: TextView
    private lateinit var btnSudah: Button
    private lateinit var btnJadwal: Button
    private lateinit var lnlToggleDetail: LinearLayout
    private lateinit var txtToggleDetail: TextView
    private lateinit var imgToggleDetail: ImageView
    private lateinit var spnImunisasi: Spinner
    private lateinit var txtJadwal: TextView
    private lateinit var txtSudah: TextView
    private lateinit var rcvImunisasi: RecyclerView
    private lateinit var btn: Button
    private lateinit var lnl: LinearLayout
    private lateinit var edtPassword: EditText
    private lateinit var imgShow: ImageView
    private lateinit var imgHide: ImageView
    private lateinit var lnlDate: LinearLayout
    private lateinit var txtDate: TextView
    private lateinit var date: Calendar
    private var _year = 0
    private var _month = 0
    private var _day = 0
    private var id = 0
    private var name = ""

    private val db = DBHelper(this, null)

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        date.set(_year, _month, _day, hour, minute, 0)
        txtDate.text = DateUtils().dtFormatter(date.time)
    }

    private val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        _year = year
        _month = month
        _day = day
        showTimePickerDialog(onTimeSetListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imunisasi)

        val actionBar = supportActionBar
        actionBar!!.title = "Imunisasi"
        actionBar.setDisplayHomeAsUpEnabled(true)

        lnlDetail = findViewById(R.id.lnlDetail)
        imgJk = findViewById(R.id.imgJk)
        txtNama = findViewById(R.id.txtNama)
        txtUmur = findViewById(R.id.txtUmur)
        txtTtl = findViewById(R.id.txtTtl)
        txtNik = findViewById(R.id.txtNik)
        txtKk = findViewById(R.id.txtKk)
        txtJkn = findViewById(R.id.txtJkn)
        btnSudah = findViewById(R.id.btnSudah)
        btnJadwal = findViewById(R.id.btnJadwal)
        lnlToggleDetail = findViewById(R.id.lnlToggleDetail)
        txtToggleDetail = findViewById(R.id.txtToggleDetail)
        imgToggleDetail = findViewById(R.id.imgToggleDetail)
        spnImunisasi = findViewById(R.id.spnImunisasi)
        rcvImunisasi = findViewById(R.id.rcvImunisasi)
        txtJadwal = findViewById(R.id.txtJadwal)
        txtSudah = findViewById(R.id.txtSudah)
        btn = findViewById(R.id.btn)
        lnl = findViewById(R.id.lnl)

        val broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(arg0: Context, intent: Intent) {
                val action = intent.action
                if (action == "finish i") {
                    finish()
                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("finish i"))

        id = intent.getIntExtra("id", 0)

        lnlToggleDetail.setOnClickListener{
            toggleDetail()
        }

        date = Calendar.getInstance()
        val types = arrayOf("Hep-B 0", "BCG", "Polio 1", "DPT,HB,Hib1", "PCV/Pneumokokus 1", "Polio 2", "Rotavirus 1", "DPT, Hb, Hib2", "Polio 3",
            "DPT, Hb, Hib3", "PCV/Pneumokokus 2", "Polio 4", "Rotavirus 2", "IPV/Polio Suntik", "MR/campak 1", "DPT, Hb, Hib4", "MR/campak 2")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spnImunisasi.adapter = adapter

        spnImunisasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                name = types[position]
                // Toast.makeText(this@ImunisasiActivity, types[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        btnSudah.setOnClickListener {
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_isi_password, null)
            edtPassword = dialogLayout.findViewById(R.id.edtPassword)
            imgShow = dialogLayout.findViewById(R.id.imgShow)
            imgHide = dialogLayout.findViewById(R.id.imgHide)
            edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            imgShow.setOnClickListener {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgHide.visibility = View.VISIBLE
                imgShow.visibility = View.GONE
            }
            imgHide.setOnClickListener {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                imgShow.visibility = View.VISIBLE
                imgHide.visibility = View.GONE
            }

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> markAsDone(edtPassword.text.toString(), name) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
        btnJadwal.setOnClickListener {
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_isi_password, null)
            edtPassword = dialogLayout.findViewById(R.id.edtPassword)
            imgShow = dialogLayout.findViewById(R.id.imgShow)
            imgHide = dialogLayout.findViewById(R.id.imgHide)
            edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            imgShow.setOnClickListener {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgHide.visibility = View.VISIBLE
                imgShow.visibility = View.GONE
            }
            imgHide.setOnClickListener {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                imgShow.visibility = View.VISIBLE
                imgHide.visibility = View.GONE
            }

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> submitPassword(edtPassword.text.toString()) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
        btn.setOnClickListener {
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_isi_password, null)
            edtPassword = dialogLayout.findViewById(R.id.edtPassword)
            imgShow = dialogLayout.findViewById(R.id.imgShow)
            imgHide = dialogLayout.findViewById(R.id.imgHide)
            edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            imgShow.setOnClickListener {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgHide.visibility = View.VISIBLE
                imgShow.visibility = View.GONE
            }
            imgHide.setOnClickListener {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                imgShow.visibility = View.VISIBLE
                imgHide.visibility = View.GONE
            }

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> deleteAll(edtPassword.text.toString()) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
        txtJadwal.setOnClickListener {
            loadImunisasi("jadwal")
        }
        txtSudah.setOnClickListener {
            loadImunisasi("sudah")
        }

        load()
        loadImunisasi("jadwal")
        ServiceTracker().actionOnService(this, "start")
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
                tgl.set(DateUtils().getDatePart("yyyy", date), DateUtils().getDatePart("MM", date)-1, DateUtils().getDatePart("dd", date), 0, 0, 0)
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
        var i = 0
        val listImunisasi = ArrayList<KunjunganModel>()
        val result = db.getVisits(1, id)
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    if ((type == "jadwal" && result.getInt(result.getColumnIndex(DBHelper.visit_status)) == 0) ||
                        (type == "sudah" && result.getInt(result.getColumnIndex(DBHelper.visit_status)) == 1)) {
                        val kunjunganModel = KunjunganModel()
                        kunjunganModel.id = result.getInt(result.getColumnIndex(DBHelper.visit_id))
                        kunjunganModel.entry_id = result.getInt(result.getColumnIndex(DBHelper.visit_entry_id))
                        kunjunganModel.date = result.getInt(result.getColumnIndex(DBHelper.visit_date))
                        kunjunganModel.time = result.getString(result.getColumnIndex(DBHelper.visit_time))
                        kunjunganModel.status = result.getInt(result.getColumnIndex(DBHelper.visit_status))
                        kunjunganModel.info = result.getString(result.getColumnIndex(DBHelper.visit_notes))
                        if (type == "jadwal") {
                            val dt = DateUtils().dbFormatter.parse(kunjunganModel.date.toString())
                            val al = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.visit_alarm)).toString())
                            val tm = DateUtils().tmFormatter.parse(kunjunganModel.time)
                            kunjunganModel.alarm = DateUtils().dpFormatter(dt!!) + " Jam " + DateUtils().tmFormatter.format(tm!!)
                            val cl = Calendar.getInstance()
                            cl.set(DateUtils().getDatePart("yyyy", dt), DateUtils().getDatePart("MM", dt)-1, DateUtils().getDatePart("dd", dt), DateUtils().getDatePart("HH", tm), DateUtils().getDatePart("mm", tm), 0)
                            if (cl.timeInMillis > Date().time && dt != al) {
                                Log.v("perlu update", cl.time.toString() + " " + dt + " $al")
                                val clAfter = Calendar.getInstance()
                                clAfter.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), cl.get(Calendar.HOUR_OF_DAY), cl.get(Calendar.MINUTE), 0)
                                if (clAfter.timeInMillis < Date().time) {
                                    if (clAfter.timeInMillis < Date().time) clAfter.add(Calendar.DATE, 1)
                                }
                                db.updVisit(kunjunganModel.id, kunjunganModel.entry_id, 1, DateUtils().dbFormatter.format(clAfter.time).toInt(), DateUtils().tmFormatter.format(clAfter.time), kunjunganModel.info, 0)
                            }
                        }
                        listImunisasi.add(kunjunganModel)
                        i++
                    }
                } while (result.moveToNext())
            }
        }
        rcvImunisasi.adapter = ImunisasiAdapter(this, listImunisasi)
    }

    private fun deleteAll(text: String) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Hapus Semua Jadwal?")
            builder.setPositiveButton("Oke") { _,_ -> db.delAllVisits(1, id); restartActivity() }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun markAsDone(text: String, visitName: String) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Tandai Sudah Berkunjung?")
            builder.setPositiveButton("Oke") { _,_ ->
                val result = db.getVisitByName(id, name)
                if (result != null) {
                    if (result.moveToFirst()) {
                        db.updVisitAsDoneByName(id, visitName)
                    } else {
                        db.addVisit(id, 1, 0, 0, "", name, 1)
                    }
                }
                restartActivity()
            }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun submitPassword(text: String) {
        if (text == "nganjukbangkit") {
            val dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_buat_alarm, null)
            lnlDate = dialogLayout.findViewById(R.id.lnlDate)
            txtDate = dialogLayout.findViewById(R.id.txtDate)
            lnlDate.setOnClickListener { showDatePickerDialog(onDateSetListener, date) }

            txtDate.text = DateUtils().dtFormatter(date.time)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Jadwal Pengingat Kunjungan")
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ ->
                val result = db.getVisitByName(id, name)
                if (result != null) {
                    val alarm = Calendar.getInstance()
                    alarm.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), 0)
                    if (date.timeInMillis > Date().time) {
                        alarm.add(Calendar.DATE, -5)
                        if (DateUtils().dbFormatter.format(alarm.time).toInt() < DateUtils().dbFormatter.format(Date().time).toInt()) {
                            alarm.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), 0)
                        }
                        if (alarm.timeInMillis < Date().time) {
                            alarm.add(Calendar.DATE, 1)
                        }
                    }
                    if (result.moveToFirst()) {
                        db.updVisitByName(id, DateUtils().dbFormatter.format(alarm.time).toInt(), DateUtils().dbFormatter.format(date.time).toInt(), DateUtils().tmFormatter.format(date.time), name)
                    } else {
                        db.addVisit(id, 1, DateUtils().dbFormatter.format(alarm.time).toInt(), DateUtils().dbFormatter.format(date.time).toInt(), DateUtils().tmFormatter.format(date.time), name, 0)
                    }
                }
                restartActivity()
            }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun restartActivity() {
        finish()
        startActivity(Intent(this, ImunisasiActivity::class.java).putExtra("id", id))
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