package com.ejemplo.centroreparacion.ui.repairs

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.R
import com.ejemplo.centroreparacion.data.entity.RepairStatus
import com.ejemplo.centroreparacion.databinding.FragmentRepairsListBinding

class RepairsListFragment : Fragment() {
    private var _b: FragmentRepairsListBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()
    private lateinit var adapter: RepairAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentRepairsListBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        adapter = RepairAdapter { repair ->
            findNavController().navigate(R.id.action_list_to_detail, bundleOf("repairId" to repair.id))
        }
        b.rv.layoutManager = LinearLayoutManager(requireContext())
        b.rv.adapter = adapter

        val statuses = listOf("Todos") + RepairStatus.values().map { it.label }
b.spinnerStatus.adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)
vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }

        b.btnSearch.setOnClickListener {
            val q = b.etSearch.text.toString().trim()
            if (q.isEmpty()) val statuses = listOf("Todos") + RepairStatus.values().map { it.label }
b.spinnerStatus.adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)
vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }
            else vm.search(q).observe(viewLifecycleOwner) { adapter.submitList(it) }
        }

        b.spinnerStatus.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 0) val statuses = listOf("Todos") + RepairStatus.values().map { it.label }
b.spinnerStatus.adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses)
vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }
                else {
                    val status = RepairStatus.values()[pos - 1]
                    vm.allRepairs().observe(viewLifecycleOwner) { list ->
                        adapter.submitList(list.filter { it.statusEnum == status })
                    }
                }
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }

        b.fab.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_new)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
