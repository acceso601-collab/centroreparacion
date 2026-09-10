package com.ejemplo.centroreparacion.ui.repairs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.data.entity.Measurement
import com.ejemplo.centroreparacion.data.entity.Repair
import com.ejemplo.centroreparacion.data.entity.RepairStatus
import com.ejemplo.centroreparacion.databinding.FragmentRepairDetailBinding

class RepairDetailFragment : Fragment() {
    private var _b: FragmentRepairDetailBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()
    private var repairId: Long = 0
    private var current: Repair? = null
    private lateinit var checkAdapter: ChecklistAdapter
    private lateinit var measureAdapter: MeasurementAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRepairDetailBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        repairId = arguments?.getLong("repairId") ?: 0
        if (repairId == 0L) { requireActivity().onBackPressed(); return }

        vm.repairById(repairId).observe(viewLifecycleOwner) { r ->
            if (r == null) return@observe
            current = r
            b.tvHeader.text = "${r.brand} ${r.model}"
            b.tvClient.text = "Cliente: ${r.clientName} · ${r.clientPhone}"
            b.tvProblem.text = r.problem
            b.tvStatus.text = "Estado: ${r.statusEnum.label}"
            b.tvCost.text = "Total: $%.2f · Ganancia: $%.2f".format(
                r.chargedPrice,
                r.chargedPrice - (r.laborCost + r.otherCosts)
            )
        }

        // Checklist
        checkAdapter = ChecklistAdapter { vm.updateChecklist(it) }
        b.rvChecklist.layoutManager = LinearLayoutManager(requireContext())
        b.rvChecklist.adapter = checkAdapter
        vm.checklistFor(repairId).observe(viewLifecycleOwner) { checkAdapter.submitList(it) }

        // Mediciones
        measureAdapter = MeasurementAdapter { m ->
            android.app.AlertDialog.Builder(requireContext())
                .setMessage("¿Eliminar esta medición?")
                .setPositiveButton("Sí") { _, _ -> vm.deleteMeasurement(m) }
                .setNegativeButton("No", null).show()
        }
        b.rvMeasurements.layoutManager = LinearLayoutManager(requireContext())
        b.rvMeasurements.adapter = measureAdapter
        vm.measurementsFor(repairId).observe(viewLifecycleOwner) { measureAdapter.submitList(it) }

        // Cambiar estado
        b.spinnerStatus.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, view: View?, pos: Int, id: Long) {
                current?.let { r ->
                    if (r.statusEnum != RepairStatus.values()[pos]) {
                        vm.saveRepair(r.copy(status = RepairStatus.values()[pos].name))
                    }
                }
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
        val statusLabels = RepairStatus.values().map { it.label }
        b.spinnerStatus.adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusLabels)

        // Añadir medición rápida
        b.btnAddMeasure.setOnClickListener {
            val m = Measurement(
                repairId = repairId,
                type = b.etMeasureType.text.toString().ifEmpty { "Voltaje" },
                value = b.etMeasureValue.text.toString().toDoubleOrNull() ?: return@setOnClickListener,
                unit = b.etMeasureUnit.text.toString().ifEmpty { "V" },
                point = b.etMeasurePoint.text.toString().ifEmpty { "N/A" }
            )
            vm.addMeasurement(m)
            b.etMeasureValue.text.clear()
            b.etMeasurePoint.text.clear()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
