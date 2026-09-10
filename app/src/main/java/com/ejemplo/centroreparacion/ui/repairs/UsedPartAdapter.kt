package com.ejemplo.centroreparacion.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ejemplo.centroreparacion.data.entity.UsedPart
import com.ejemplo.centroreparacion.databinding.ItemUsedPartBinding
import com.ejemplo.centroreparacion.util.MoneyUtils

class UsedPartAdapter(private val onDelete: (UsedPart) -> Unit) :
    ListAdapter<UsedPart, UsedPartAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemUsedPartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        with(holder.b) {
            tvName.text = p.name
            tvDetail.text = "${p.quantity} × ${MoneyUtils.format(p.unitPrice)}"
            tvSubtotal.text = MoneyUtils.format(p.subtotal)
            btnDelete.setOnClickListener { onDelete(p) }
        }
    }

    class VH(val b: ItemUsedPartBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<UsedPart>() {
            override fun areItemsTheSame(a: UsedPart, b: UsedPart) = a.id == b.id
            override fun areContentsTheSame(a: UsedPart, b: UsedPart) = a == b
        }
    }
}
