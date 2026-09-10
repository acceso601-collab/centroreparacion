package com.ejemplo.centroreparacion.ui.repairs

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.data.entity.InventoryItem
import com.ejemplo.centroreparacion.databinding.FragmentAddPartBinding
import com.ejemplo.centroreparacion.repository.InventoryResult
import com.ejemplo.centroreparacion.util.MoneyUtils
import kotlinx.coroutines.launch

class AddPartDialogFragment : DialogFragment() {

    private var _b: FragmentAddPartBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels({ requireParentFragment() })
    private var repairId: Long = 0
    private var selected: InventoryItem? = null

    companion object {
        fun newInstance(repairId: Long) = AddPartDialogFragment().apply {
            arguments = Bundle().apply { putLong("repairId", repairId) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        repairId = arguments?.getLong("repairId") ?: 0
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentAddPartBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val adapter = InventorySelectAdapter { item ->
            selected = item
            b.etQty.setText("1")
            b.tvSelected.text = "${item.name} · Stock: ${item.quantity} · ${MoneyUtils.format(item.buyPrice)}"
        }
        b.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        b.rvInventory.adapter = adapter

        vm.repository.allInventory().observe(viewLifecycleOwner) { adapter.submitList(it) }

        b.btnConfirm.setOnClickListener {
            val item = selected
            if (item == null) {
                Toast.makeText(requireContext(), "Selecciona una pieza", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val qty = b.etQty.text.toString().toIntOrNull() ?: 0
            if (qty <= 0) {
                b.etQty.error = "Cantidad inválida"
                return@setOnClickListener
            }
            b.btnConfirm.isEnabled = false // evitar doble toque

            vm.addUsedPart(repairId, item.id, qty) { result ->
                when (result) {
                    is InventoryResult.Success -> {
                        Toast.makeText(requireContext(),
                            "Pieza agregada", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    is InventoryResult.InsufficientStock -> {
                        Toast.makeText(requireContext(),
                            "No hay suficiente stock. Disponible: ${result.available}",
                            Toast.LENGTH_LONG).show()
                        b.btnConfirm.isEnabled = true
                    }
                    is InventoryResult.ItemNotFound ->
                        Toast.makeText(requireContext(), "Pieza no encontrada", Toast.LENGTH_SHORT).show()
                    is InventoryResult.Error ->
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        b.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}

/** Adaptador interno para seleccionar inventario. */
class InventorySelectAdapter(private val onClick: (InventoryItem) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<InventoryItem,
        InventorySelectAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(com.ejemplo.centroreparacion.R.layout.item_inventory_select, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.name.text = item.name
        holder.detail.text = "Stock: ${item.quantity} · ${MoneyUtils.format(item.buyPrice)}"
        holder.itemView.isEnabled = item.quantity > 0
        holder.itemView.alpha = if (item.quantity > 0) 1f else 0.4f
        holder.itemView.setOnClickListener { if (item.quantity > 0) onClick(item) }
    }

    class VH(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val name: android.widget.TextView = v.findViewById(com.ejemplo.centroreparacion.R.id.tvName)
        val detail: android.widget.TextView = v.findViewById(com.ejemplo.centroreparacion.R.id.tvDetail)
    }

    companion object {
        val DIFF = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<InventoryItem>() {
            override fun areItemsTheSame(a: InventoryItem, b: InventoryItem) = a.id == b.id
            override fun areContentsTheSame(a: InventoryItem, b: InventoryItem) = a == b
        }
    }
}
