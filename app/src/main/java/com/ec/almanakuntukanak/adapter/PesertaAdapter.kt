package com.ec.almanakuntukanak.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.PesertaModel
import com.ec.almanakuntukanak.controller.peserta.PesertaActivity
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity

class PesertaAdapter(context: PesertaActivity, private var items: ArrayList<PesertaModel>, private var from: String) :
    RecyclerView.Adapter<PesertaAdapter.ViewHolder>() {

    private var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_peserta, parent, false)
        return ViewHolder(v as LinearLayout)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item: PesertaModel = items[position]
        holder.lnlBlankSta.visibility = if (position == 0) View.VISIBLE else View.GONE
        holder.lnlDetail.setOnClickListener {
            context.startActivity(Intent(context, if (from == "imunisasi") ImunisasiActivity::class.java else PemeriksaanActivity::class.java))
        }
        holder.txtNama.text = item.nama
        holder.txtJk.text = item.jk
        holder.txtTtl.text = item.ttl
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lnlBlankSta: LinearLayout = view.findViewById(R.id.lnlBlankSta)
        var lnlDetail: LinearLayout = view.findViewById(R.id.lnlDetail)
        var txtNama: TextView = view.findViewById(R.id.txtNama)
        var txtJk: TextView = view.findViewById(R.id.txtJk)
        var txtTtl: TextView = view.findViewById(R.id.txtTtl)
        var lnlBlankEnd: LinearLayout = view.findViewById(R.id.lnlBlankEnd)
    }
}