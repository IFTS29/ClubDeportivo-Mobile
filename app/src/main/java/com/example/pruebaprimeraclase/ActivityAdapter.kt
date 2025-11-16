package com.example.pruebaprimeraclase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ActivityAdapter(
    private val activities: List<ActivityData>,

    // CAMBIO 1: Añadimos la lista de IDs ya inscriptos
    private val alreadyRegisteredIds: Set<Int>,

    private val listener: OnActivitySelectionListener
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    interface OnActivitySelectionListener {
        fun onSelectionChanged(selectedTotal: Double, selectedIds: List<Int>)
    }

    private val selectedActivityIds = mutableSetOf<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbSelect: CheckBox = view.findViewById(R.id.cbSelectActivity)
        val tvActivityInfo: TextView = view.findViewById(R.id.tvActivityInfo)
        val tvCost: TextView = view.findViewById(R.id.tvActivityCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = activities.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]

        val costText = String.format(Locale.US, "$ %.2f", activity.cost)
        holder.tvCost.text = costText

        // CAMBIO 2: Lógica para deshabilitar filas
        if (alreadyRegisteredIds.contains(activity.activityId)) {
            // --- YA ESTÁ INSCRIPTO HOY ---
            holder.tvActivityInfo.text = "${activity.activityName} - ${activity.activityTime} hs (Ya inscripto)"
            holder.cbSelect.isChecked = true
            holder.cbSelect.isEnabled = false
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.6f // Lo "grizamos"
            holder.cbSelect.setOnCheckedChangeListener(null)
            holder.itemView.setOnClickListener(null)

        } else {
            // --- ESTÁ DISPONIBLE ---
            holder.tvActivityInfo.text = "${activity.activityName} - ${activity.activityTime} hs"
            holder.cbSelect.isEnabled = true
            holder.itemView.isEnabled = true
            holder.itemView.alpha = 1.0f

            // Lógica normal de selección
            holder.cbSelect.setOnCheckedChangeListener(null)
            holder.cbSelect.isChecked = selectedActivityIds.contains(activity.activityId)

            holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedActivityIds.add(activity.activityId)
                } else {
                    selectedActivityIds.remove(activity.activityId)
                }
                notifyActivityOfSelectionChange()
            }

            holder.itemView.setOnClickListener {
                holder.cbSelect.isChecked = !holder.cbSelect.isChecked
            }
        }
    }

    // Esta función calcula el total SOLO de los items NUEVOS seleccionados
    private fun notifyActivityOfSelectionChange() {
        var currentTotal = 0.0

        for (id in selectedActivityIds) {
            // Suma solo si no estaba ya inscripto (aunque ya lo filtramos antes)
            if (!alreadyRegisteredIds.contains(id)) {
                currentTotal += activities.find { it.activityId == id }?.cost ?: 0.0
            }
        }

        listener.onSelectionChanged(currentTotal, selectedActivityIds.toList())
    }
}