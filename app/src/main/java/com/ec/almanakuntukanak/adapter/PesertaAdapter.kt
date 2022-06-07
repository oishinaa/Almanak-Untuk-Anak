package com.ec.almanakuntukanak.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ec.almanakuntukanak.DBHelper
import com.ec.almanakuntukanak.R
import com.ec.almanakuntukanak.model.PesertaModel
import com.ec.almanakuntukanak.controller.peserta.PesertaActivity
import com.ec.almanakuntukanak.controller.imunisasi.ImunisasiActivity
import com.ec.almanakuntukanak.controller.pemeriksaan.PemeriksaanActivity
import com.ec.almanakuntukanak.controller.peserta.PesertaFormActivity

class PesertaAdapter(context: PesertaActivity, private var items: ArrayList<PesertaModel>, private var from: String) :
    RecyclerView.Adapter<PesertaAdapter.ViewHolder>() {

    private var context: Context = context
    private val db = DBHelper(context, null)

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
            context.startActivity(Intent(context, if (from == "imunisasi") ImunisasiActivity::class.java else PemeriksaanActivity::class.java).putExtra("id", item.id))
        }
        holder.txtNama.text = item.nama
        holder.txtJk.text = item.jk
        holder.txtTtl.text = item.ttl
        holder.btnEdit.setOnClickListener { context.startActivity(Intent(context, PesertaFormActivity::class.java).putExtra("id", item.id).putExtra("from", from)) }
        holder.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Apakah anda yakin untuk menghapus data ini?")
            builder.setPositiveButton("Oke") { _, _ -> db.delEntry(item.id); restartActivity() }
            builder.setNegativeButton("Batal") { _, _ -> }
            builder.show()
        }
        holder.lnlBlankEnd.visibility = if (position+1 == items.size) View.VISIBLE else View.GONE
    }

    private fun restartActivity() {
        context.sendBroadcast(Intent("finish p"))
        val intent = Intent(context, PesertaActivity::class.java)
        intent.putExtra("from", from)
        context.startActivity(intent)
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
        var btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        var btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        var lnlBlankEnd: LinearLayout = view.findViewById(R.id.lnlBlankEnd)
    }
}