package com.ejemplo.centroreparacion.ui.repairs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.centroreparacion.data.entity.ChecklistItem
import com.ejemplo.centroreparacion.databinding.ItemChecklistBinding

class ChecklistAdapter(private val onChange: (ChecklistItem) -> Unit) :
    ListAdapter<ChecklistItem, ChecklistAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemChecklistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.b) {
            tvName.text = "${item.category}: ${item.name}"
            when (item.status) {
                "OK" -> { tvState.text = "✓"; tvState.setTextColor(Color.parseColor("#4CAF50")) }
                "FAILED" -> { tvState.text = "✗"; tvState.setTextColor(Color.parseColor("#F44336")) }
                else -> { tvState.text = "—"; tvState.setTextColor(Color.parseColor("#757575")) }
            }
            root.setOnClickListener {
                val next = when (item.status) {
                    "NOT_TESTED" -> "OK"; "OK" -> "FAILED"; else -> "NOT_TESTED"
                }
                onChange(item.copy(status = next))
            }
        }
    }

    class VH(val b: ItemChecklistBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ChecklistItem>() {
            override fun areItemsTheSame(a: ChecklistItem, b: ChecklistItem) = a.id == b.id
            override fun areContentsTheSame(a: ChecklistItem, b: ChecklistItem) = a == b
        }
    }
}
