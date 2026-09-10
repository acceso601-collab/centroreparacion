package com.ejemplo.centroreparacion.ui.repairs

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ejemplo.centroreparacion.R
import com.ejemplo.centroreparacion.data.entity.Repair
import com.ejemplo.centroreparacion.databinding.FragmentNewRepairBinding
import java.util.Calendar

class NewRepairFragment : Fragment() {
    private var _b: FragmentNewRepairBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentNewRepairBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        b.btnSave.setOnClickListener {
            val brand = b.etBrand.text.toString().trim()
            val model = b.etModel.text.toString().trim()
            val client = b.etClient.text.toString().trim()
            val problem = b.etProblem.text.toString().trim()

            // Validaciones
            if (brand.isEmpty()) { b.etBrand.error = "Requerido"; return@setOnClickListener }
            if (model.isEmpty()) { b.etModel.error = "Requerido"; return@setOnClickListener }
            if (client.isEmpty()) { b.etClient.error = "Requerido"; return@setOnClickListener }
            if (problem.isEmpty()) { b.etProblem.error = "Requerido"; return@setOnClickListener }

            val labor = b.etLabor.text.toString().toDoubleOrNull() ?: 0.0
            val other = b.etOther.text.toString().toDoubleOrNull() ?: 0.0
            val charged = b.etCharged.text.toString().toDoubleOrNull() ?: 0.0

            if (labor < 0 || other < 0 || charged < 0) {
                Toast.makeText(requireContext(), "Los costos no pueden ser negativos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val repair = Repair(
                brand = brand, model = model,
                imei = b.etImei.text.toString().trim(),
                clientName = client,
                clientPhone = b.etPhone.text.toString().trim(),
                problem = problem,
                physicalState = b.etPhysical.text.toString().trim(),
                accessories = b.etAccessories.text.toString().trim(),
                pin = b.etPin.text.toString().trim(),
                notes = b.etNotes.text.toString().trim(),
                laborCost = labor, otherCosts = other, chargedPrice = charged
            )
            vm.saveRepair(repair) { newId ->
                Toast.makeText(requireContext(), "Reparación creada", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_list_to_detail, Bundle().apply { putLong("repairId", newId) })
            }
        }
        b.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
