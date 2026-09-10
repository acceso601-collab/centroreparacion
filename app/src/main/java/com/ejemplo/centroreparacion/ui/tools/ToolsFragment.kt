package com.ejemplo.centroreparacion.ui.tools

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ejemplo.centroreparacion.databinding.FragmentToolsBinding

class ToolsFragment : Fragment() {
    private var _b: FragmentToolsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentToolsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        val modes = listOf("V = I × R", "I = V ÷ R", "R = V ÷ I", "P = V × I", "P = I² × R", "P = V² ÷ R")
        b.spinnerMode.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, modes)

        b.btnCalculate.setOnClickListener {
            val a = b.etA.text.toString().toDoubleOrNull()
            val c = b.etB.text.toString().toDoubleOrNull()
            val mode = b.spinnerMode.selectedItemPosition

            if (a == null || c == null) {
                Toast.makeText(requireContext(), "Introduce ambos valores", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            if (a < 0 || c < 0) {
                Toast.makeText(requireContext(), "Los valores no pueden ser negativos", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }

            val (result, formula, unit) = try {
                when (mode) {
                    0 -> Triple(a * c, "V = I × R = $a × $c", "V")           // V
                    1 -> {
                        if (c == 0.0) throw ArithmeticException("División entre cero")
                        Triple(a / c, "I = V ÷ R = $a ÷ $c", "A")
                    }
                    2 -> {
                        if (c == 0.0) throw ArithmeticException("División entre cero")
                        Triple(a / c, "R = V ÷ I = $a ÷ $c", "Ω")
                    }
                    3 -> Triple(a * c, "P = V × I = $a × $c", "W")
                    4 -> Triple(a * a * c, "P = I² × R = $a² × $c", "W")
                    5 -> {
                        if (c == 0.0) throw ArithmeticException("División entre cero")
                        Triple((a * a) / c, "P = V² ÷ R = $a² ÷ $c", "W")
                    }
                    else -> Triple(0.0, "", "")
                }
            } catch (e: ArithmeticException) {
                b.tvResult.text = "❌ Error: ${e.message}"
                return@setOnClickListener
            }

            b.tvResult.text = """
                Fórmula: $formula
                Resultado: ${"%.4f".format(result)} $unit
            """.trimIndent()
        }

        // Conversión rápida de unidades
        b.btnConvert.setOnClickListener {
            val v = b.etConvertValue.text.toString().toDoubleOrNull()
            if (v == null) { b.tvConvertResult.text = "Introduce un valor"; return@setOnClickListener }
            val from = b.spinnerFrom.selectedItem.toString()
            val to = b.spinnerTo.selectedItem.toString()
            val result = convert(v, from, to)
            if (result == null) b.tvConvertResult.text = "Conversión no soportada"
            else b.tvConvertResult.text = "${"%.6f".format(result)} $to"
        }
        val units = listOf("V", "mV", "A", "mA", "Ω", "kΩ", "MΩ", "W", "kW")
        b.spinnerFrom.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, units)
        b.spinnerTo.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, units)
        b.spinnerTo.setSelection(1)
    }

    private fun convert(v: Double, from: String, to: String): Double? {
        val base = when (from) {
            "V" -> v; "mV" -> v / 1000
            "A" -> v; "mA" -> v / 1000
            "Ω" -> v; "kΩ" -> v * 1000; "MΩ" -> v * 1_000_000
            "W" -> v; "kW" -> v * 1000
            else -> return null
        }
        return when (to) {
            "V" -> base; "mV" -> base * 1000
            "A" -> base; "mA" -> base * 1000
            "Ω" -> base; "kΩ" -> base / 1000; "MΩ" -> base / 1_000_000
            "W" -> base; "kW" -> base / 1000
            else -> null
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
