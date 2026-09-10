package com.ejemplo.centroreparacion.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ejemplo.centroreparacion.R
import com.ejemplo.centroreparacion.databinding.FragmentDashboardBinding
import com.ejemplo.centroreparacion.repository.RepairRepository
import com.ejemplo.centroreparacion.util.MoneyUtils
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _b: FragmentDashboardBinding? = null
    private val b get() = _b!!
    private lateinit var repo: RepairRepository

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentDashboardBinding.inflate(inflater, c, false)
        return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        repo = RepairRepository(requireContext())

        // Métricas reales desde Room
        repo.repairDao.activeCount().observe(viewLifecycleOwner) { b.tvActive.text = it.toString() }
        repo.repairDao.completedCount().observe(viewLifecycleOwner) { b.tvCompleted.text = it.toString() }
        repo.repairDao.pendingCount().observe(viewLifecycleOwner) { b.tvPending.text = it.toString() }

        // Ingresos y ganancia real
        repo.repairDao.totalRevenue().observe(viewLifecycleOwner) { revenue ->
            val costs = repo.repairDao.totalBaseCosts().value ?: 0.0
            val profit = revenue - costs
            b.tvProfit.text = MoneyUtils.format(profit)
        }
        repo.repairDao.totalBaseCosts().observe(viewLifecycleOwner) { costs ->
            val revenue = repo.repairDao.totalRevenue().value ?: 0.0
            val profit = revenue - costs
            b.tvProfit.text = MoneyUtils.format(profit)
        }

        b.btnNewRepair.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_newRepair)
        }
        b.btnInventory.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_inventory)
        }
        b.btnTools.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_tools)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
