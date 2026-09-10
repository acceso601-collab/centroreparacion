package com.ejemplo.centroreparacion.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.centroreparacion.data.entity.InventoryItem
import com.ejemplo.centroreparacion.repository.RepairRepository
import kotlinx.coroutines.launch

class InventoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RepairRepository(app)
    fun all() = repo.allInventory()
    fun search(q: String) = repo.searchInventory(q)
    fun save(item: InventoryItem) = viewModelScope.launch { repo.saveInventoryItem(item) }
    fun delete(item: InventoryItem) = viewModelScope.launch { repo.deleteInventoryItem(item) }
}
