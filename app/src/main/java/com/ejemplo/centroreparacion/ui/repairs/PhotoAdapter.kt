package com.ejemplo.centroreparacion.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ejemplo.centroreparacion.data.entity.RepairPhoto
import com.ejemplo.centroreparacion.databinding.ItemPhotoBinding
import java.io.File

class PhotoAdapter(
    private val onClick: (RepairPhoto) -> Unit,
    private val onDelete: (RepairPhoto) -> Unit
) : ListAdapter<RepairPhoto, PhotoAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        val f = File(p.path)
        holder.b.img.load(if (f.exists()) f else null)
        holder.b.root.setOnClickListener { onClick(p) }
        holder.b.root.setOnLongClickListener { onDelete(p); true }
    }

    class VH(val b: ItemPhotoBinding) : RecyclerView.ViewHolder(b.root)

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RepairPhoto>() {
            override fun areItemsTheSame(a: RepairPhoto, b: RepairPhoto) = a.id == b.id
            override fun areContentsTheSame(a: RepairPhoto, b: RepairPhoto) = a == b
        }
    }
}
