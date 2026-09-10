package com.ejemplo.centroreparacion.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.centroreparacion.data.entity.Measurement
import com.ejemplo.centroreparacion.databinding.ItemMeasurementBinding
import java.text.SimpleDateFormat
import java.util.*

class MeasurementAdapter(private val onDelete: (Measurement) -> Unit) :
    ListAdapter<Measurement, MeasurementAdapter.VH>(DIFF) {

    private val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemMeasurementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = getItem(position)
        with(holder.b) {
            tvType.text = "${m.type}: ${m.value} ${m.unit}"
            tvPoint.text = "${m.point} · ${sdf.format(Date(m.timestamp))}"
            root.setOnLongClickListener { onDelete(m); true }
        }
    }

    class VH(val b: ItemMeasurementBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Measurement>() {
            override fun areItemsTheSame(a: Measurement, b: Measurement) = a.id == b.id
            override fun areContentsTheSame(a: Measurement, b: Measurement) = a == b
        }
    }
}
