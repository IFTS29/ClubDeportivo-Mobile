package com.example.pruebaprimeraclase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DatoAdapter(
    private val vencimientos: List<MembershipData>,
    private val getClient: (Int) -> ClientData?,
    private val onItemClick: (MembershipData, ClientData?) -> Unit
) : RecyclerView.Adapter<DatoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSocioId: TextView = view.findViewById(R.id.tvSocioId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_vencimiento, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = vencimientos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vencimiento = vencimientos[position]
        val client = getClient(vencimiento.clientId)
        holder.tvSocioId.text = client?.docNumber ?: "Sin documento"
        holder.itemView.setOnClickListener {
            onItemClick(vencimiento, client)
        }
    }
}