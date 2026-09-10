package com.ejemplo.centroreparacion.ui.repairs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.centroreparacion.data.entity.*
import com.ejemplo.centroreparacion.repository.InventoryResult
import com.ejemplo.centroreparacion.repository.RepairRepository
import com.ejemplo.centroreparacion.util.ProfitCalculator
import kotlinx.coroutines.launch

class RepairViewModel(app: Application) : AndroidViewModel(app) {
    val repository = RepairRepository(app)

    fun allRepairs() = repository.allRepairs()
    fun recentRepairs() = repository.recentRepairs()
    fun repairById(id: Long) = repository.repairById(id)
    fun search(q: String) = repository.searchRepairs(q)
    fun checklistFor(id: Long) = repository.checklistFor(id)
    fun measurementsFor(id: Long) = repository.measurementsFor(id)
    fun usedPartsFor(id: Long) = repository.usedPartsFor(id)
    fun photosFor(id: Long) = repository.photosFor(id)
    fun piecesCostFor(id: Long) = repository.piecesCostFor(id)

    suspend fun getRepair(id: Long) = repository.getRepair(id)
    suspend fun financialsFor(id: Long): ProfitCalculator.Result = repository.financialsFor(id)

    fun createRepair(r: Repair, onCreated: (Long) -> Unit) = viewModelScope.launch {
        val id = repository.createRepair(r)
        onCreated(id)
    }
    fun updateRepair(r: Repair) = viewModelScope.launch { repository.updateRepair(r) }

    fun deleteRepairWithRestore(id: Long, onDone: () -> Unit = {}) = viewModelScope.launch {
        repository.deleteRepairWithRestore(id)
        onDone()
    }

    fun updateChecklist(item: ChecklistItem) = viewModelScope.launch { repository.updateChecklistItem(item) }
    fun addChecklistItem(item: ChecklistItem) = viewModelScope.launch { repository.addChecklistItem(item) }
    fun deleteChecklistItem(item: ChecklistItem) = viewModelScope.launch { repository.deleteChecklistItem(item) }

    fun addMeasurement(m: Measurement) = viewModelScope.launch { repository.addMeasurement(m) }
    fun deleteMeasurement(m: Measurement) = viewModelScope.launch { repository.deleteMeasurement(m) }

    fun addUsedPart(repairId: Long, inventoryId: Long, qty: Int, onResult: (InventoryResult) -> Unit) =
        viewModelScope.launch {
            val r = repository.addUsedPartTransactional(repairId, inventoryId, qty)
            onResult(r)
        }
    fun deleteUsedPartAndRestore(p: UsedPart) = viewModelScope.launch {
        repository.deleteUsedPartAndRestore(p)
    }

    fun addPhoto(repairId: Long, path: String) = viewModelScope.launch {
        repository.addPhoto(RepairPhoto(repairId = repairId, path = path))
    }
    fun deletePhoto(p: RepairPhoto) = viewModelScope.launch { repository.deletePhotoAndFile(p) }
}
