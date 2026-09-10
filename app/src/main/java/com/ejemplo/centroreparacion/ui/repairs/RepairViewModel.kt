package com.ejemplo.centroreparacion.ui.repairs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ejemplo.centroreparacion.data.entity.*
import com.ejemplo.centroreparacion.repository.RepairRepository
import kotlinx.coroutines.launch

class RepairViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RepairRepository(app)

    fun allRepairs() = repo.allRepairs()
    fun repairById(id: Long) = repo.repairById(id)
    fun search(q: String) = repo.searchRepairs(q)
    fun checklistFor(id: Long) = repo.checklistFor(id)
    fun measurementsFor(id: Long) = repo.measurementsFor(id)
    fun usedPartsFor(id: Long) = repo.usedPartsFor(id)
    fun photosFor(id: Long) = repo.photosFor(id)

    suspend fun getRepair(id: Long) = repo.getRepair(id)

    fun saveRepair(r: Repair, onCreate: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repo.saveRepair(r)
        if (r.id == 0L) { repo.initChecklist(id); onCreate(id) }
    }
    fun deleteRepair(r: Repair) = viewModelScope.launch { repo.deleteRepair(r) }
    fun updateChecklist(item: ChecklistItem) = viewModelScope.launch { repo.updateChecklistItem(item) }
    fun addMeasurement(m: Measurement) = viewModelScope.launch { repo.addMeasurement(m) }
    fun deleteMeasurement(m: Measurement) = viewModelScope.launch { repo.deleteMeasurement(m) }
    fun addUsedPart(p: UsedPart) = viewModelScope.launch { repo.addUsedPart(p) }
    fun deleteUsedPart(p: UsedPart) = viewModelScope.launch { repo.deleteUsedPart(p) }
    fun addPhoto(p: RepairPhoto) = viewModelScope.launch { repo.addPhoto(p) }
    fun deletePhoto(p: RepairPhoto) = viewModelScope.launch { repo.deletePhoto(p) }
}
