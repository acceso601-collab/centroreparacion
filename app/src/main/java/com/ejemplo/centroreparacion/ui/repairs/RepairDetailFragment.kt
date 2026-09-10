package com.ejemplo.centroreparacion.ui.repairs

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.data.entity.Measurement
import com.ejemplo.centroreparacion.data.entity.UsedPart
import com.ejemplo.centroreparacion.databinding.FragmentRepairDetailBinding
import com.ejemplo.centroreparacion.repository.InventoryResult
import com.ejemplo.centroreparacion.util.Constants
import com.ejemplo.centroreparacion.util.MoneyUtils
import com.ejemplo.centroreparacion.util.PdfGenerator
import kotlinx.coroutines.launch

class RepairDetailFragment : Fragment() {

    private var _b: FragmentRepairDetailBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()
    private var repairId: Long = 0

    private lateinit var checklistAdapter: ChecklistAdapter
    private lateinit var measurementAdapter: MeasurementAdapter
    private lateinit var usedPartAdapter: UsedPartAdapter
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRepairDetailBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        repairId = arguments?.getLong("repairId") ?: 0L
        if (repairId == 0L) { requireActivity().onBackPressed(); return }

        setupChecklist()
        setupMeasurements()
        setupUsedParts()
        setupPhotos()
        observeRepair()
        setupStatusSpinner()
        setupButtons()
    }

    private fun observeRepair() {
        vm.repairById(repairId).observe(viewLifecycleOwner) { r ->
            if (r == null) return@observe
            b.tvHeader.text = "${r.brand} ${r.model}"
            b.tvClient.text = "${r.clientName} · ${r.clientPhone}"
            b.tvProblem.text = r.problem
            b.tvImei.text = "IMEI: ${r.imei}"
            b.tvStatus.text = "Estado: ${Constants.RepairStatus.label(r.status)}"
            // Recalcular ganancia con piezas reales
            lifecycleScope.launch {
                val fin = vm.financialsFor(repairId)
                b.tvCost.text = buildString {
                    appendLine("Piezas: ${MoneyUtils.format(fin.piecesCost)}")
                    appendLine("Mano de obra: ${MoneyUtils.format(fin.laborCost)}")
                    appendLine("Otros: ${MoneyUtils.format(fin.otherCosts)}")
                    appendLine("Costo total: ${MoneyUtils.format(fin.totalCost)}")
                    appendLine("Cobrado: ${MoneyUtils.format(fin.chargedPrice)}")
                    append("Ganancia: ${MoneyUtils.format(fin.profit)}")
                }
            }
        }
    }

    private fun setupStatusSpinner() {
        val labels = Constants.RepairStatus.all().map { Constants.RepairStatus.label(it) }
        b.spinnerStatus.adapter = android.widget.ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, labels)
        b.spinnerStatus.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, vw: View?, pos: Int, id: Long) {
                lifecycleScope.launch {
                    val r = vm.getRepair(repairId) ?: return@launch
                    val code = Constants.RepairStatus.all()[pos]
                    if (r.status != code) vm.updateRepair(r.copy(status = code))
                }
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupChecklist() {
        checklistAdapter = ChecklistAdapter { item ->
            vm.updateChecklist(item.copy(status = Constants.Checklist.next(item.status)))
        }
        b.rvChecklist.layoutManager = LinearLayoutManager(requireContext())
        b.rvChecklist.adapter = checklistAdapter
        vm.checklistFor(repairId).observe(viewLifecycleOwner) { checklistAdapter.submitList(it) }
    }

    private fun setupMeasurements() {
        measurementAdapter = MeasurementAdapter { m ->
            AlertDialog.Builder(requireContext())
                .setMessage("¿Eliminar esta medición?")
                .setPositiveButton("Sí") { _, _ -> vm.deleteMeasurement(m) }
                .setNegativeButton("No", null).show()
        }
        b.rvMeasurements.layoutManager = LinearLayoutManager(requireContext())
        b.rvMeasurements.adapter = measurementAdapter
        vm.measurementsFor(repairId).observe(viewLifecycleOwner) { measurementAdapter.submitList(it) }
    }

    private fun setupUsedParts() {
        usedPartAdapter = UsedPartAdapter { part ->
            AlertDialog.Builder(requireContext())
                .setMessage("Eliminar pieza devolverá ${part.quantity} unidad(es) al inventario. ¿Continuar?")
                .setPositiveButton("Eliminar") { _, _ ->
                    lifecycleScope.launch { vm.deleteUsedPartAndRestore(part) }
                }
                .setNegativeButton("Cancelar", null).show()
        }
        b.rvUsedParts.layoutManager = LinearLayoutManager(requireContext())
        b.rvUsedParts.adapter = usedPartAdapter
        vm.usedPartsFor(repairId).observe(viewLifecycleOwner) { usedPartAdapter.submitList(it) }
    }

    private fun setupPhotos() {
        photoAdapter = PhotoAdapter(
            onClick = { photo ->
                val intent = Intent(requireContext(), PhotoViewerActivity::class.java)
                intent.putExtra("path", photo.path)
                startActivity(intent)
            },
            onDelete = { photo ->
                AlertDialog.Builder(requireContext())
                    .setMessage("¿Eliminar esta foto? Se borrará también el archivo.")
                    .setPositiveButton("Eliminar") { _, _ -> vm.deletePhoto(photo) }
                    .setNegativeButton("Cancelar", null).show()
            }
        )
        b.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 3)
        b.rvPhotos.adapter = photoAdapter
        vm.photosFor(repairId).observe(viewLifecycleOwner) { photoAdapter.submitList(it) }
    }

    private fun setupButtons() {
        b.btnAddPhoto.setOnClickListener {
            PhotoPickerHelper(requireActivity()).showPicker { uri ->
                val path = PhotoPickerHelper.copyToAppStorage(requireContext(), uri, repairId)
                path?.let { p ->
                    lifecycleScope.launch { vm.addPhoto(repairId, p) }
                }
            }
        }

        b.btnAddPart.setOnClickListener {
            AddPartDialogFragment.newInstance(repairId).show(parentFragmentManager, "AddPart")
        }

        b.btnAddMeasure.setOnClickListener {
            val value = b.etMeasureValue.text.toString().toDoubleOrNull()
            if (value == null) { b.etMeasureValue.error = "Inválido"; return@setOnClickListener }
            val type = b.etMeasureType.text.toString().ifBlank { "Voltaje" }
            val unit = b.etMeasureUnit.text.toString().ifBlank { "V" }
            val point = b.etMeasurePoint.text.toString().ifBlank { "N/A" }
            val notes = b.etMeasureNotes.text.toString()
            vm.addMeasurement(Measurement(repairId = repairId, type = type, value = value,
                unit = unit, point = point, notes = notes))
            b.etMeasureValue.text.clear(); b.etMeasurePoint.text.clear(); b.etMeasureNotes.text.clear()
        }

        b.btnExportPdf.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val pdf = PdfGenerator(requireContext(), vm.repository).generateRepairPdf(repairId)
                    if (pdf == null) {
                        Toast.makeText(requireContext(), "Error generando PDF", Toast.LENGTH_SHORT).show()
                    } else {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, pdf)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(Intent.createChooser(share, "Compartir informe"))
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        b.btnDeleteRepair.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar reparación")
                .setMessage("Se devolverá al inventario el stock de las piezas utilizadas y se eliminarán fotos, checklist y mediciones. Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    lifecycleScope.launch {
                        vm.deleteRepairWithRestore(repairId)
                        requireActivity().onBackPressed()
                    }
                }
                .setNegativeButton("Cancelar", null).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
