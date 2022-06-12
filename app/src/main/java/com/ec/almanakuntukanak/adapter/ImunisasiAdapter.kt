package com.ec.almanakuntukanak.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.ImunisasiModel
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity

class ImunisasiAdapter(context: ImunisasiActivity, private var items: ArrayList<ImunisasiModel>) :
    RecyclerView.Adapter<ImunisasiAdapter.ViewHolder>() {

    private var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_imunisasi, parent, false)
        return ViewHolder(v as LinearLayout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: ImunisasiModel = items[position]
        holder.lnlBlankSta.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.txtNama.text = item.nama
        holder.txtDate.text = item.alarm
        holder.imgDelete.visibility = if (item.alarm.isNotEmpty()) View.VISIBLE else View.GONE
        holder.imgDelete.setOnClickListener {  }
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
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