package com.ejemplo.centroreparacion.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.centroreparacion.data.entity.InventoryItem
import com.ejemplo.centroreparacion.databinding.ItemInventoryBinding

class InventoryAdapter(private val onClick: (InventoryItem) -> Unit) :
    ListAdapter<InventoryItem, InventoryAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val i = getItem(position)
        with(holder.b) {
            tvName.text = i.name
            tvCategory.text = i.category
            tvQty.text = "Cant: ${i.quantity}"
            tvPrice.text = "$%.2f".format(i.sellPrice)
            root.setOnClickListener { onClick(i) }
        }
    }

    class VH(val b: ItemInventoryBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<InventoryItem>() {
            override fun areItemsTheSame(a: InventoryItem, b: InventoryItem) = a.id == b.id
            override fun areContentsTheSame(a: InventoryItem, b: InventoryItem) = a == b
        }
    }
}
