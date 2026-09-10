package com.ejemplo.centroreparacion.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.centroreparacion.data.entity.Repair
import com.ejemplo.centroreparacion.databinding.ItemRepairBinding

class RepairAdapter(private val onClick: (Repair) -> Unit) :
    ListAdapter<Repair, RepairAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRepairBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = getItem(position)
        with(holder.b) {
            tvTitle.text = "${r.brand} ${r.model}"
            tvClient.text = r.clientName
            tvProblem.text = r.problem
            tvStatus.text = r.statusEnum.label
            root.setOnClickListener { onClick(r) }
        }
    }

    class VH(val b: ItemRepairBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Repair>() {
            override fun areItemsTheSame(a: Repair, b: Repair) = a.id == b.id
            override fun areContentsTheSame(a: Repair, b: Repair) = a == b
        }
    }
}
