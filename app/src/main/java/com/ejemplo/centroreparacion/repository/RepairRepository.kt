package com.ejemplo.centroreparacion.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ejemplo.centroreparacion.data.AppDatabase
import com.ejemplo.centroreparacion.data.entity.*

class RepairRepository(context: Context) {
    private val db = AppDatabase.get(context)
    val repairDao = db.repairDao()
    val checklistDao = db.checklistDao()
    val measurementDao = db.measurementDao()
    val inventoryDao = db.inventoryDao()
    val usedPartDao = db.usedPartDao()
    val photoDao = db.photoDao()

    // Reparaciones
    fun allRepairs(): LiveData<List<Repair>> = repairDao.all()
    fun repairById(id: Long): LiveData<Repair> = repairDao.byId(id)
    suspend fun getRepair(id: Long): Repair? = repairDao.byIdSync(id)
    suspend fun saveRepair(r: Repair): Long = if (r.id == 0L) repairDao.insert(r) else { repairDao.update(r); r.id }
    suspend fun deleteRepair(r: Repair) = repairDao.delete(r)
    fun searchRepairs(q: String) = repairDao.search(q)
    fun repairsByStatus(s: String) = repairDao.byStatus(s)

    // Checklist
    fun checklistFor(id: Long) = checklistDao.byRepair(id)
    suspend fun updateChecklistItem(item: ChecklistItem) = checklistDao.update(item)
    suspend fun initChecklist(repairId: Long) {
        val default = buildDefaultChecklist(repairId)
        checklistDao.insertAll(default)
    }
    private fun buildDefaultChecklist(repairId: Long): List<ChecklistItem> {
        val items = mutableListOf<ChecklistItem>()
        fun add(cat: String, name: String) = items.add(ChecklistItem(repairId = repairId, category = cat, name = name))
        // ANTES
        listOf("Encendido","Pantalla","Touch","Cámaras","Micrófono","Altavoz","Vibración","Botones","Wi-Fi","Bluetooth","Carga","SIM","Huella/Desbloqueo")
            .forEach { add("ANTES DE DESMONTAR", it) }
        // DURANTE
        listOf("Apagar dispositivo","Desconectar batería","Retirar componentes","Instalar repuesto","Reconectar batería","Inspección visual")
            .forEach { add("DURANTE LA REPARACIÓN", it) }
        // DESPUÉS
        listOf("Encendido","Pantalla","Touch","Cámaras","Micrófono","Altavoz","Carga","Wi-Fi","Bluetooth","SIM","Sensores")
            .forEach { add("DESPUÉS DE LA REPARACIÓN", it) }
        return items
    }

    // Medidas
    fun measurementsFor(id: Long) = measurementDao.byRepair(id)
    suspend fun addMeasurement(m: Measurement) = measurementDao.insert(m)
    suspend fun deleteMeasurement(m: Measurement) = measurementDao.delete(m)

    // Inventario
    fun allInventory() = inventoryDao.all()
    fun searchInventory(q: String) = inventoryDao.search(q)
    suspend fun saveInventoryItem(i: InventoryItem): Long = if (i.id == 0L) inventoryDao.insert(i) else { inventoryDao.update(i); i.id }
    suspend fun deleteInventoryItem(i: InventoryItem) = inventoryDao.delete(i)

    // Piezas usadas
    fun usedPartsFor(id: Long) = usedPartDao.byRepair(id)
    suspend fun addUsedPart(p: UsedPart) {
        usedPartDao.insert(p)
        if (p.inventoryId > 0) inventoryDao.decrement(p.inventoryId, p.quantity)
    }
    suspend fun deleteUsedPart(p: UsedPart) = usedPartDao.delete(p)

    // Fotos
    fun photosFor(id: Long) = photoDao.byRepair(id)
    suspend fun addPhoto(p: RepairPhoto) = photoDao.insert(p)
    suspend fun deletePhoto(p: RepairPhoto) = photoDao.delete(p)

    // Datos de demostración
    suspend fun loadSampleData() {
        val repairId = repairDao.insert(Repair(
            brand = "Oukitel", model = "C61 Pro",
            imei = "356938035643809",
            clientName = "Cliente Demo", clientPhone = "555-1234",
            problem = "Pantalla completamente rota",
            physicalState = "Carcasa con golpes leves",
            accessories = "Funda",
            notes = "Datos de demostración. Puede eliminarse.",
            status = RepairStatus.EN_REPARACION.name,
            laborCost = 15.0, chargedPrice = 45.0
        ))
        initChecklist(repairId)
        measurementDao.insert(Measurement(repairId = repairId, type = "Voltaje", value = 3.81, unit = "V", point = "Batería", notes = "Lectura estable"))
        measurementDao.insert(Measurement(repairId = repairId, type = "Voltaje", value = 5.08, unit = "V", point = "Puerto USB", notes = ""))
        val invId = inventoryDao.insert(InventoryItem(
            name = "Pantalla + táctil Oukitel C61 Pro", category = "Pantallas",
            brand = "Oukitel", compatibleModel = "C61 Pro",
            quantity = 3, buyPrice = 18.0, sellPrice = 35.0, sku = "SCR-C61P-001"
        ))
        usedPartDao.insert(UsedPart(repairId = repairId, inventoryId = invId, name = "Pantalla + táctil Oukitel C61 Pro", quantity = 1, unitPrice = 18.0))
    }

    suspend fun clearAll() {
        db.clearAllTables()
    }
}
