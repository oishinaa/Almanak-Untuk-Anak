package com.ec.almanakuntukanak.controller.pemeriksaan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.adapter.PemeriksaanAdapter
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.tracker.ServiceTracker
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
    private lateinit var btn: Button
    private lateinit var edtPassword: EditText
    private lateinit var imgShow: ImageView
    private lateinit var imgHide: ImageView
    private lateinit var tgl: Calendar
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
        btn = findViewById(R.id.btn)

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
            builder.setPositiveButton("Oke") { _,_ -> turnOffAlarm(edtPassword.text.toString()) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }

        load()
        loadPemeriksaan()
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
                tgl = Calendar.getInstance()
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
    private fun loadPemeriksaan() {
        rcvPemeriksaan.layoutManager = LinearLayoutManager(this)
        var i = 0
        var isSet = false
        val clBefore = Calendar.getInstance()
        val listPemeriksaan = ArrayList<KunjunganModel>()
        val arrPeriode = arrayOf("29 Hari", "3 Bulan", "6 Bulan", "9 Bulan", "12 Bulan", "18 Bulan", "2 Tahun", "3 Tahun", "4 Tahun", "5 Tahun", "6 Tahun")
        val result = db.getVisits(2, id)
        if (result != null) {
            if (result.moveToFirst()) {
                do {
                    val kunjunganModel = KunjunganModel()
                    kunjunganModel.id = result.getInt(result.getColumnIndex(DBHelper.visit_id))
                    kunjunganModel.entry_id = result.getInt(result.getColumnIndex(DBHelper.visit_entry_id))
                    kunjunganModel.date = result.getInt(result.getColumnIndex(DBHelper.visit_alarm))
                    kunjunganModel.time = result.getString(result.getColumnIndex(DBHelper.visit_time))
                    val dt = DateUtils().dbFormatter.parse(result.getInt(result.getColumnIndex(DBHelper.visit_date)).toString())
                    val al = DateUtils().dbFormatter.parse(kunjunganModel.date.toString())
                    val tm = DateUtils().tmFormatter.parse(kunjunganModel.time)
                    val cl = Calendar.getInstance()
                    cl.set(DateUtils().getDatePart("yyyy", dt!!), DateUtils().getDatePart("MM", dt)-1, DateUtils().getDatePart("dd", dt), DateUtils().getDatePart("HH", tm!!), DateUtils().getDatePart("mm", tm), 0)
                    kunjunganModel.status = result.getInt(result.getColumnIndex(DBHelper.visit_status))
                    kunjunganModel.info = arrPeriode[i]
                    kunjunganModel.alarm = "Alarm akan berbunyi pada " + DateUtils().dpFormatter(al!!) + " Jam " + DateUtils().tmFormatter.format(tm)
                    listPemeriksaan.add(kunjunganModel)
                    if (kunjunganModel.status < 10) {
                        if (!isSet && (Date().time < cl.timeInMillis || i == 9) && kunjunganModel.status != 1) {
                            val current = if (i != 0 && i != 9 && listPemeriksaan[i-1].status != 1) i-1 else i
                            cl.set(DateUtils().getDatePart("yyyy", al), DateUtils().getDatePart("MM", al)-1, DateUtils().getDatePart("dd", al), DateUtils().getDatePart("HH", tm), DateUtils().getDatePart("mm", tm), 0)
                            val comparator = if (i != 0 && i != 9 && listPemeriksaan[i-1].status != 1) clBefore else cl
                            listPemeriksaan[current].status = 2
                            if (i != 0 && comparator.timeInMillis < Date().time) {
                                val pemeriksaan = listPemeriksaan[current]
                                val clAfter = Calendar.getInstance()
                                clAfter.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE), DateUtils().getDatePart("HH", tm), DateUtils().getDatePart("mm", tm), 0)
                                if (clAfter.timeInMillis < Date().time) {
                                    clAfter.add(Calendar.DATE, 1)
                                }
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Apakah anda sudah melakukan pemeriksaan SDI-DTK di umur " + arrPeriode[current].lowercase() + " sampai " + arrPeriode[current+1].lowercase() + "?")
                                builder.setPositiveButton("Sudah") { _,_ ->
                                    db.updVisitAsDone(pemeriksaan.id)
                                    restartActivity()
                                }
                                builder.setNegativeButton("Belum") { _,_ ->
                                    db.updVisit(pemeriksaan.id, pemeriksaan.entry_id, 2, DateUtils().dbFormatter.format(clAfter.time).toInt(), DateUtils().tmFormatter.format(clAfter.time), "", 0)
                                    restartActivity()
                                }
                                builder.setCancelable(false)
                                builder.show()
                            }
                            isSet = true
                        }
                    } else if (i == 0) {
                        btn.text ="Nyalakan Alarm"
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
                            builder.setPositiveButton("Oke") { _,_ -> turnOnAlarm(edtPassword.text.toString()) }
                            builder.setNegativeButton("Batal") { _,_ -> }
                            builder.show()
                        }
                    }
                    clBefore.set(DateUtils().getDatePart("yyyy", al), DateUtils().getDatePart("MM", al)-1, DateUtils().getDatePart("dd", al), DateUtils().getDatePart("HH", tm), DateUtils().getDatePart("mm", tm), 0)
                    i++
                } while (result.moveToNext())
            }
        }
        rcvPemeriksaan.adapter = PemeriksaanAdapter(this, listPemeriksaan, tgl)
    }

    private fun turnOffAlarm(text: String) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Matikan Alarm?")
            builder.setPositiveButton("Oke") { _,_ -> db.turnOffVisit(id); restartActivity() }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun turnOnAlarm(text: String) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Nyalakan Alarm?")
            builder.setPositiveButton("Oke") { _,_ -> db.turnOnVisit(id); restartActivity() }
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
        startActivity(Intent(this, PemeriksaanActivity::class.java).putExtra("id", id))
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