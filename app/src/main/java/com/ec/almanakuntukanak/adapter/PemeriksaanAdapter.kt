package com.ec.almanakuntukanak.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.PemeriksaanModel
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity

class PemeriksaanAdapter(context: PemeriksaanActivity, private var items: ArrayList<PemeriksaanModel>) :
    RecyclerView.Adapter<PemeriksaanAdapter.ViewHolder>() {

    private var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_pemeriksaan, parent, false)
        return ViewHolder(v as LinearLayout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: PemeriksaanModel = items[position]
        holder.lnlBlankSta.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.txtPeriode.text = item.periode
        holder.btnSudah.visibility = if (item.status < 2) View.INVISIBLE else View.VISIBLE
        holder.btnTunda.visibility = if (item.status < 2) View.INVISIBLE else View.VISIBLE
        holder.imgCheck.visibility = if (item.status != 1) View.GONE else View.VISIBLE
        holder.txtAlarm.visibility = if (item.status < 2) View.GONE else View.VISIBLE
        holder.txtAlarm.text = "Alarm akan berbunyi pada " + item.alarm
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
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