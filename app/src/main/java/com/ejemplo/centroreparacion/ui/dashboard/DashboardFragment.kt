package com.ejemplo.centroreparacion.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ejemplo.centroreparacion.databinding.FragmentDashboardBinding
import com.ejemplo.centroreparacion.repository.RepairRepository
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!
    private lateinit var repo: RepairRepository

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDashboardBinding.inflate(inflater, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        repo = RepairRepository(requireContext())

        repo.repairDao.activeCount().observe(viewLifecycleOwner) { b.tvActive.text = it.toString() }
        repo.repairDao.completedCount().observe(viewLifecycleOwner) { b.tvCompleted.text = it.toString() }
        repo.repairDao.pendingCount().observe(viewLifecycleOwner) { b.tvPending.text = it.toString() }
        repo.repairDao.totalProfit().observe(viewLifecycleOwner) { b.tvProfit.text = "$%.2f".format(it ?: 0.0) }

        b.btnNewRepair.setOnClickListener {
            findNavController().navigate(com.ejemplo.centroreparacion.R.id.action_dashboard_to_newRepair)
        }
        b.btnInventory.setOnClickListener {
            findNavController().navigate(com.ejemplo.centroreparacion.R.id.action_dashboard_to_inventory)
        }
        b.btnTools.setOnClickListener {
            findNavController().navigate(com.ejemplo.centroreparacion.R.id.toolsFragment)
        }
        b.btnLoadSample.setOnClickListener {
            lifecycleScope.launch {
                repo.loadSampleData()
                Toast.makeText(requireContext(), "Datos de prueba cargados", Toast.LENGTH_SHORT).show()
            }
        }
        b.btnClearAll.setOnClickListener {
            lifecycleScope.launch {
                repo.clearAll()
                Toast.makeText(requireContext(), "Datos eliminados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
