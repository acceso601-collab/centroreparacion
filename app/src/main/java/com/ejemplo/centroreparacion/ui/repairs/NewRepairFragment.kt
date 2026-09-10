package com.ejemplo.centroreparacion.ui.repairs

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ejemplo.centroreparacion.R
import com.ejemplo.centroreparacion.data.entity.Repair
import com.ejemplo.centroreparacion.databinding.FragmentNewRepairBinding
import com.ejemplo.centroreparacion.util.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewRepairFragment : Fragment() {

    private var _b: FragmentNewRepairBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()

    private var entryDate: Long = System.currentTimeMillis()
    private var estimatedDate: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentNewRepairBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateDateLabels()

        b.btnEntryDate.setOnClickListener { showDatePicker(true) }
        b.btnEstimatedDate.setOnClickListener { showDatePicker(false) }

        b.btnSave.setOnClickListener {
            val brand = b.etBrand.text.toString().trim()
            val model = b.etModel.text.toString().trim()
            val client = b.etClient.text.toString().trim()
            val problem = b.etProblem.text.toString().trim()

            // Validaciones de campos obligatorios
            var ok = true
            if (brand.isEmpty()) { b.etBrand.error = "Requerido"; ok = false }
            if (model.isEmpty()) { b.etModel.error = "Requerido"; ok = false }
            if (client.isEmpty()) { b.etClient.error = "Requerido"; ok = false }
            if (problem.isEmpty()) { b.etProblem.error = "Requerido"; ok = false }
            if (!ok) return@setOnClickListener

            // Validaciones numéricas
            val labor = b.etLabor.text.toString().toDoubleOrNull() ?: 0.0
            val other = b.etOther.text.toString().toDoubleOrNull() ?: 0.0
            val charged = b.etCharged.text.toString().toDoubleOrNull() ?: 0.0

            if (labor < 0 || other < 0 || charged < 0) {
                Toast.makeText(requireContext(),
                    "Los costos no pueden ser negativos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val repair = Repair(
                brand = brand,
                model = model,
                imei = b.etImei.text.toString().trim(),
                clientName = client,
                clientPhone = b.etPhone.text.toString().trim(),
                problem = problem,
                physicalState = b.etPhysical.text.toString().trim(),
                accessories = b.etAccessories.text.toString().trim(),
                pin = b.etPin.text.toString().trim(),
                notes = b.etNotes.text.toString().trim(),
                entryDate = entryDate,
                estimatedDate = estimatedDate,
                status = Constants.RepairStatus.RECIBIDO,
                laborCost = labor,
                otherCosts = other,
                chargedPrice = charged
            )

            // Deshabilitar el botón para evitar doble toque
            b.btnSave.isEnabled = false

            // Usa la API correcta del ViewModel (createRepair, no saveRepair)
            vm.createRepair(repair) { newId ->
                Toast.makeText(requireContext(), "Reparación creada", Toast.LENGTH_SHORT).show()
                // Navega con la acción propia de este fragmento (no la de la lista)
                findNavController().navigate(
                    R.id.action_new_to_detail,
                    bundleOf("repairId" to newId)
                )
            }
        }

        b.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    private fun showDatePicker(isEntry: Boolean) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = if (isEntry) entryDate else estimatedDate
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val c = Calendar.getInstance()
                c.set(year, month, day, 0, 0, 0)
                c.set(Calendar.MILLISECOND, 0)
                if (isEntry) entryDate = c.timeInMillis else estimatedDate = c.timeInMillis
                updateDateLabels()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateLabels() {
        b.btnEntryDate.text = "Entrada: ${sdf.format(Date(entryDate))}"
        b.btnEstimatedDate.text = "Entrega estimada: ${sdf.format(Date(estimatedDate))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
