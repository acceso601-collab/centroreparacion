package com.ejemplo.centroreparacion.ui.inventory

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.data.entity.InventoryItem
import com.ejemplo.centroreparacion.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment() {
    private var _b: FragmentInventoryBinding? = null
    private val b get() = _b!!
    private val vm: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentInventoryBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        adapter = InventoryAdapter { showEditDialog(it) }
        b.rv.layoutManager = LinearLayoutManager(requireContext())
        b.rv.adapter = adapter
        vm.all().observe(viewLifecycleOwner) { adapter.submitList(it) }

        b.btnSearch.setOnClickListener {
            val q = b.etSearch.text.toString().trim()
            if (q.isEmpty()) vm.all().observe(viewLifecycleOwner) { adapter.submitList(it) }
            else vm.search(q).observe(viewLifecycleOwner) { adapter.submitList(it) }
        }
        b.fab.setOnClickListener { showEditDialog(null) }
    }

    private fun showEditDialog(existing: InventoryItem?) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL; setPadding(40, 20, 40, 20)
        }
        fun field(hint: String, value: String = ""): EditText =
            EditText(requireContext()).apply { this.hint = hint; setText(value); layout.addView(this) }

        val etName = field("Nombre", existing?.name ?: "")
        val etCategory = field("Categoría", existing?.category ?: "")
        val etBrand = field("Marca", existing?.brand ?: "")
        val etCompatible = field("Modelo compatible", existing?.compatibleModel ?: "")
        val etQty = field("Cantidad", existing?.quantity?.toString() ?: "0").apply { inputType = InputType.TYPE_CLASS_NUMBER }
        val etBuy = field("Precio compra", existing?.buyPrice?.toString() ?: "0")
        val etSell = field("Precio venta", existing?.sellPrice?.toString() ?: "0")
        val etLocation = field("Ubicación", existing?.location ?: "")
        val etSku = field("SKU", existing?.sku ?: "")
        val etNotes = field("Notas", existing?.notes ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Nueva pieza" else "Editar pieza")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) { Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val qty = etQty.text.toString().toIntOrNull() ?: 0
                if (qty < 0) { Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val item = existing?.copy(
                    name = name, category = etCategory.text.toString(),
                    brand = etBrand.text.toString(), compatibleModel = etCompatible.text.toString(),
                    quantity = qty, buyPrice = etBuy.text.toString().toDoubleOrNull() ?: 0.0,
                    sellPrice = etSell.text.toString().toDoubleOrNull() ?: 0.0,
                    location = etLocation.text.toString(), sku = etSku.text.toString(),
                    notes = etNotes.text.toString()
                ) ?: InventoryItem(
                    name = name, category = etCategory.text.toString(),
                    brand = etBrand.text.toString(), compatibleModel = etCompatible.text.toString(),
                    quantity = qty, buyPrice = etBuy.text.toString().toDoubleOrNull() ?: 0.0,
                    sellPrice = etSell.text.toString().toDoubleOrNull() ?: 0.0,
                    location = etLocation.text.toString(), sku = etSku.text.toString(),
                    notes = etNotes.text.toString()
                )
                vm.save(item)
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton(if (existing == null) "Cerrar" else "Eliminar") { _, _ ->
                if (existing != null) vm.delete(existing)
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
