package com.ec.almanakuntukanak.adapter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity
import com.ec.almanakuntukanak.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

class PemeriksaanAdapter(context: PemeriksaanActivity, private var items: ArrayList<KunjunganModel>, private var tgl: Calendar) :
    RecyclerView.Adapter<PemeriksaanAdapter.ViewHolder>() {

    private lateinit var edtPassword: EditText
    private lateinit var imgShow: ImageView
    private lateinit var imgHide: ImageView
    private lateinit var lnlDate: LinearLayout
    private lateinit var txtDate: TextView
    private lateinit var date: Calendar
    private var dateArr = arrayOf(Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(),
        Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance())
    private var _year = 0
    private var _month = 0
    private var _day = 0
    private var context: Context = context

    private val db = DBHelper(context, null)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val datePartArr = arrayOf(Calendar.DATE, Calendar.MONTH, Calendar.MONTH, Calendar.MONTH, Calendar.MONTH,
            Calendar.MONTH, Calendar.YEAR, Calendar.YEAR, Calendar.YEAR, Calendar.YEAR)
        val amountArr = arrayOf(29, 3, 6, 9, 12, 18, 2, 3, 4, 5)
        for((i, dt) in dateArr.withIndex()) {
            dt.set(tgl.get(Calendar.YEAR), tgl.get(Calendar.MONTH), tgl.get(Calendar.DATE), 0, 0, 0)
            dt.add(datePartArr[i], amountArr[i])
        }

        val v: View = LayoutInflater.from(context).inflate(R.layout.item_pemeriksaan, parent, false)
        return ViewHolder(v as LinearLayout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: KunjunganModel = items[position]
        holder.lnlBlankSta.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.txtPeriode.text = item.info
        holder.imgCheck.visibility = if (item.status != 1) View.GONE else View.VISIBLE
        holder.btnSudah.visibility = if (item.status != 2) View.INVISIBLE else View.VISIBLE
        holder.btnTunda.visibility = if (item.status != 2) View.INVISIBLE else View.VISIBLE
        holder.txtAlarm.visibility = if (item.status != 2) View.GONE else View.VISIBLE
        holder.txtAlarm.text = item.alarm
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
        holder.btnSudah.setOnClickListener {
            val dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_isi_password, null)
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

            val builder = AlertDialog.Builder(context)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> markAsDone(edtPassword.text.toString(), item.id, item.entry_id) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
        holder.btnTunda.setOnClickListener {
            val dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_isi_password, null)
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

            val builder = AlertDialog.Builder(context)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> submitPassword(edtPassword.text.toString(), item.id, item.entry_id, item.date, item.time, position) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
    }

    private fun markAsDone(text: String, id: Int, entry_id: Int) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Tandai Sudah Berkunjung?")
            builder.setPositiveButton("Oke") { _,_ -> db.updVisitAsDone(id); restartActivity(entry_id) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun submitPassword(text: String, id: Int, entry_id: Int, visit_alarm: Int, visit_time: String, position: Int) {
        if (text == "nganjukbangkit") {
            val tempDate = DateUtils().dbFormatter.parse(visit_alarm.toString())
            val tempTime = DateUtils().tmFormatter.parse(visit_time)
            date = Calendar.getInstance()
            date.set(DateUtils().getDatePart("yyyy", tempDate!!), DateUtils().getDatePart("MM", tempDate)-1, DateUtils().getDatePart("dd", tempDate), DateUtils().getDatePart("HH", tempTime!!), DateUtils().getDatePart("mm", tempTime), 0)

            val dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_buat_alarm, null)
            lnlDate = dialogLayout.findViewById(R.id.lnlDate)
            txtDate = dialogLayout.findViewById(R.id.txtDate)
            lnlDate.setOnClickListener { showDatePickerDialog(onDateSetListener, dateArr[position].timeInMillis) }
            txtDate.text = DateUtils().dtFormatter(date.time)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Ubah Pengingat Kunjungan")
            builder.setView(dialogLayout)
            builder.setPositiveButton("Oke") { _,_ -> db.updVisit(id, entry_id, 2, DateUtils().dbFormatter.format(date.time).toInt(), DateUtils().tmFormatter.format(date.time), "", 0); restartActivity(entry_id) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Password salah!")
            builder.setPositiveButton("Oke") { _,_ -> }
            builder.show()
        }
    }

    private fun restartActivity(id: Int) {
        context.sendBroadcast(Intent("finish p"))
        context.startActivity(Intent(context, PemeriksaanActivity::class.java).putExtra("id", id))
    }

    private fun showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener, min: Long) {
        val datePickerDialog = DatePickerDialog(context, onDateSetListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = min
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        val timePickerDialog = TimePickerDialog(context, onTimeSetListener, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lnlBlankSta: LinearLayout = view.findViewById(R.id.lnlBlankSta)
        var txtPeriode: TextView = view.findViewById(R.id.txtPeriode)
        var btnSudah: Button = view.findViewById(R.id.btnSudah)
        var btnTunda: Button = view.findViewById(R.id.btnTunda)
        var imgCheck: ImageView = view.findViewById(R.id.imgCheck)
        var txtAlarm: TextView = view.findViewById(R.id.txtAlarm)
        var lnlBlankEnd: LinearLayout = view.findViewById(R.id.lnlBlankEnd)
    }
}