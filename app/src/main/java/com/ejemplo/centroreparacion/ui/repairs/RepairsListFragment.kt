package com.ejemplo.centroreparacion.ui.repairs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ejemplo.centroreparacion.R
import com.ejemplo.centroreparacion.databinding.FragmentRepairsListBinding
import com.ejemplo.centroreparacion.util.Constants

class RepairsListFragment : Fragment() {

    private var _b: FragmentRepairsListBinding? = null
    private val b get() = _b!!
    private val vm: RepairViewModel by viewModels()
    private lateinit var adapter: RepairAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentRepairsListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RepairAdapter { repair ->
            findNavController().navigate(R.id.action_list_to_detail, bundleOf("repairId" to repair.id))
        }
        b.rv.layoutManager = LinearLayoutManager(requireContext())
        b.rv.adapter = adapter

        val statuses = listOf("Todos") + Constants.RepairStatus.all().map { Constants.RepairStatus.label(it) }
        b.spinnerStatus.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            statuses
        )

        vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }

        b.btnSearch.setOnClickListener {
            val q = b.etSearch.text.toString().trim()
            if (q.isEmpty()) {
                vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }
            } else {
                vm.search(q).observe(viewLifecycleOwner) { adapter.submitList(it) }
            }
        }

        b.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    vm.allRepairs().observe(viewLifecycleOwner) { adapter.submitList(it) }
                } else {
                    val statusCode = Constants.RepairStatus.all()[position - 1]
                    vm.allRepairs().observe(viewLifecycleOwner) { list ->
                        adapter.submitList(list.filter { it.status == statusCode })
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        b.fab.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_new)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
