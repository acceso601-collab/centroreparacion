package com.ejemplo.centroreparacion.ui.tools

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.ejemplo.centroreparacion.databinding.FragmentToolsBinding
import com.ejemplo.centroreparacion.util.Constants
import com.ejemplo.centroreparacion.util.OhmCalculator
import com.ejemplo.centroreparacion.util.UnitConverter

class ToolsFragment : Fragment() {
    private var _b: FragmentToolsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentToolsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        val modes = listOf("V = I × R", "I = V ÷ R", "R = V ÷ I",
            "P = V × I", "P = I² × R", "P = V² ÷ R")
        b.spinnerMode.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, modes)

        b.btnCalculate.setOnClickListener {
            val a = b.etA.text.toString().toDoubleOrNull()
            val c = b.etB.text.toString().toDoubleOrNull()
            if (a == null || c == null) {
                b.tvResult.text = "⚠ Introduce ambos valores numéricos"
                return@setOnClickListener
            }
            val r = when (b.spinnerMode.selectedItemPosition) {
                0 -> OhmCalculator.calculateVoltage(a, c)
                1 -> OhmCalculator.calculateCurrent(a, c)
                2 -> OhmCalculator.calculateResistance(a, c)
                3 -> OhmCalculator.calculatePowerVI(a, c)
                4 -> OhmCalculator.calculatePowerI2R(a, c)
                else -> OhmCalculator.calculatePowerV2R(a, c)
            }
            b.tvResult.text = if (r.error != null) "❌ ${r.error}"
                else "${r.formula}\n${r.substitution}\n= ${"%.4f".format(r.result)} ${r.unit}"
        }

        // Conversión separada por magnitud
        val mags = listOf("Voltaje", "Corriente", "Resistencia", "Potencia")
        b.spinnerMag.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, mags)
        b.spinnerMag.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, vw: View?, pos: Int, id: Long) {
                val units = when (pos) {
                    0 -> Constants.Units.voltageUnits()
                    1 -> Constants.Units.currentUnits()
                    2 -> Constants.Units.resistanceUnits()
                    else -> Constants.Units.powerUnits()
                }
                b.spinnerFrom.adapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, units)
                b.spinnerTo.adapter = ArrayAdapter(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, units)
                if (units.size > 1) b.spinnerTo.setSelection(1)
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }

        b.btnConvert.setOnClickListener {
            val v = b.etConvertValue.text.toString().toDoubleOrNull()
            if (v == null) { b.tvConvertResult.text = "Introduce un valor"; return@setOnClickListener }
            val from = b.spinnerFrom.selectedItem?.toString() ?: return@setOnClickListener
            val to = b.spinnerTo.selectedItem?.toString() ?: return@setOnClickListener
            val r = UnitConverter.convert(v, from, to)
            b.tvConvertResult.text = if (r == null) "❌ Conversión no permitida"
                else "${"%.6f".format(r)} $to"
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
