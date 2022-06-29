package com.ec.almanakuntukanak.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.KunjunganModel
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity

class ImunisasiAdapter(context: ImunisasiActivity, private var items: ArrayList<KunjunganModel>) :
    RecyclerView.Adapter<ImunisasiAdapter.ViewHolder>() {

    private lateinit var edtPassword: EditText
    private lateinit var imgShow: ImageView
    private lateinit var imgHide: ImageView
    private var context: Context = context

    private val db = DBHelper(context, null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_imunisasi, parent, false)
        return ViewHolder(v as LinearLayout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: KunjunganModel = items[position]
        holder.lnlBlankSta.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.txtNama.text = item.info
        holder.txtDate.text = item.alarm
        holder.imgDelete.visibility = if (item.alarm.isNotEmpty()) View.VISIBLE else View.GONE
        holder.imgDelete.setOnClickListener {
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
            builder.setPositiveButton("Oke") { _,_ -> delete(edtPassword.text.toString(), item.id, item.entry_id) }
            builder.setNegativeButton("Batal") { _,_ -> }
            builder.show()
        }
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
    }

    private fun delete(text: String, id: Int, entry_id: Int) {
        if (text == "nganjukbangkit") {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Hapus Jadwal Imunisasi Ini?")
            builder.setPositiveButton("Oke") { _,_ -> db.delVisit(id); restartActivity(entry_id) }
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
        context.sendBroadcast(Intent("finish i"))
        context.startActivity(Intent(context, ImunisasiActivity::class.java).putExtra("id", id))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lnlBlankSta: LinearLayout = view.findViewById(R.id.lnlBlankSta)
        var txtNama: TextView = view.findViewById(R.id.txtNama)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var imgDelete: ImageView = view.findViewById(R.id.imgDelete)
        var lnlBlankEnd: LinearLayout = view.findViewById(R.id.lnlBlankEnd)
    }
}