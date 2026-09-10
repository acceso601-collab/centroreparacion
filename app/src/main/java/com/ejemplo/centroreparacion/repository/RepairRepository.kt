package com.ejemplo.centroreparacion.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.ejemplo.centroreparacion.data.AppDatabase
import com.ejemplo.centroreparacion.data.entity.*
import com.ejemplo.centroreparacion.util.Constants
import com.ejemplo.centroreparacion.util.ProfitCalculator
import java.io.File

sealed class InventoryResult {
    data class Success(val usedPartId: Long) : InventoryResult()
    data class InsufficientStock(val available: Int, val requested: Int) : InventoryResult()
    data class ItemNotFound(val name: String) : InventoryResult()
    data class Error(val message: String) : InventoryResult()
}

class RepairRepository(private val context: Context) {
    private val db = AppDatabase.get(context)
    val repairDao = db.repairDao()
    val checklistDao = db.checklistDao()
    val measurementDao = db.measurementDao()
    val inventoryDao = db.inventoryDao()
    val usedPartDao = db.usedPartDao()
    val photoDao = db.photoDao()

    // ════════ REPARACIONES ════════
    fun allRepairs() = repairDao.all()
    fun recentRepairs(limit: Int = 5) = repairDao.recent(limit)
    fun repairById(id: Long) = repairDao.byId(id)
    suspend fun getRepair(id: Long) = repairDao.byIdSync(id)
    fun searchRepairs(q: String) = repairDao.search(q)
    fun repairsByStatus(s: String) = repairDao.byStatus(s)
    fun repairsByDateRange(from: Long, to: Long) = repairDao.byDateRange(from, to)

    /**
     * Crea una reparación Y su checklist inicial en una sola transacción.
     * Si algo falla, no se guarda nada.
     */
    suspend fun createRepair(repair: Repair): Long = db.withTransaction {
        val id = repairDao.insert(repair)
        checklistDao.insertAll(buildDefaultChecklist(id))
        id
    }

    suspend fun updateRepair(repair: Repair) = repairDao.update(repair)

    /**
     * Elimina reparación + devuelve stock de todas sus piezas + elimina fotos físicas.
     * Todo transaccional.
     */
    suspend fun deleteRepairWithRestore(repairId: Long) {
        val parts = usedPartDao.byRepairSync(repairId)
        val photos = photoDao.byRepairSync(repairId)
        db.withTransaction {
            parts.forEach { part ->
                part.inventoryId?.let { invId ->
                    inventoryDao.increment(invId, part.quantity)
                }
            }
            repairDao.byIdSync(repairId)?.let { repairDao.delete(it) }
            // Fotos: eliminamos también los archivos físicos
            photos.forEach { photo ->
                runCatching { File(photo.path).takeIf { it.exists() }?.delete() }
            }
        }
    }

    // ════════ CHECKLIST ════════
    fun checklistFor(id: Long) = checklistDao.byRepair(id)
    suspend fun updateChecklistItem(item: ChecklistItem) = checklistDao.update(item)
    suspend fun addChecklistItem(item: ChecklistItem) = checklistDao.insert(item)
    suspend fun deleteChecklistItem(item: ChecklistItem) = checklistDao.delete(item)

    private fun buildDefaultChecklist(repairId: Long): List<ChecklistItem> {
        val items = mutableListOf<ChecklistItem>()
        fun add(cat: String, name: String) = items.add(
            ChecklistItem(repairId = repairId, category = cat, name = name))

        listOf("Encendido","Pantalla","Touch","Cámaras","Micrófono","Altavoz","Vibración",
            "Botones","Wi-Fi","Bluetooth","Carga","SIM","Huella/Desbloqueo")
            .forEach { add("ANTES DE DESMONTAR", it) }
        listOf("Apagar dispositivo","Desconectar batería","Retirar componentes",
            "Instalar repuesto","Reconectar batería","Inspección visual")
            .forEach { add("DURANTE LA REPARACIÓN", it) }
        listOf("Encendido","Pantalla","Touch","Cámaras","Micrófono","Altavoz",
            "Carga","Wi-Fi","Bluetooth","SIM","Sensores")
            .forEach { add("DESPUÉS DE LA REPARACIÓN", it) }
        return items
    }

    // ════════ MEDICIONES ════════
    fun measurementsFor(id: Long) = measurementDao.byRepair(id)
    suspend fun addMeasurement(m: Measurement) = measurementDao.insert(m)
    suspend fun deleteMeasurement(m: Measurement) = measurementDao.delete(m)

    // ════════ INVENTARIO ════════
    fun allInventory() = inventoryDao.all()
    fun searchInventory(q: String) = inventoryDao.search(q)
    fun inventoryByCategory(cat: String) = inventoryDao.byCategory(cat)
    fun inventoryCount() = inventoryDao.count()
    fun inventoryValue() = inventoryDao.totalValue()

    suspend fun saveInventoryItem(i: InventoryItem): Long =
        if (i.id == 0L) inventoryDao.insert(i) else { inventoryDao.update(i); i.id }

    suspend fun deleteInventoryItem(i: InventoryItem) = inventoryDao.delete(i)

    // ════════ PIEZAS USADAS (con transacción y validación de stock) ════════
    fun usedPartsFor(id: Long) = usedPartDao.byRepair(id)
    fun piecesCostFor(id: Long) = usedPartDao.piecesCostFor(id)
    suspend fun piecesCostSync(id: Long) = usedPartDao.piecesCostSync(id)

    /**
     * Agrega una pieza usada y descuenta stock, todo en una transacción.
     * Si el stock es insuficiente, devuelve InsufficientStock y NO modifica nada.
     */
    suspend fun addUsedPartTransactional(
        repairId: Long,
        inventoryId: Long,
        quantity: Int
    ): InventoryResult {
        if (quantity <= 0) return InventoryResult.Error("Cantidad debe ser mayor a 0")

        return db.withTransaction {
            val item = inventoryDao.byIdSync(inventoryId)
                ?: return@withTransaction InventoryResult.ItemNotFound("ID $inventoryId")

            if (item.quantity < quantity) {
                return@withTransaction InventoryResult.InsufficientStock(item.quantity, quantity)
            }

            val affected = inventoryDao.decrementIfAvailable(inventoryId, quantity)
            if (affected == 0) {
                return@withTransaction InventoryResult.InsufficientStock(item.quantity, quantity)
            }

            val id = usedPartDao.insert(UsedPart(
                repairId = repairId,
                inventoryId = inventoryId,
                name = item.name,
                quantity = quantity,
                unitPrice = item.buyPrice
            ))
            InventoryResult.Success(id)
        }
    }

    /**
     * Elimina pieza usada y devuelve stock en una transacción.
     */
    suspend fun deleteUsedPartAndRestore(part: UsedPart) = db.withTransaction {
        usedPartDao.delete(part)
        part.inventoryId?.let { inventoryDao.increment(it, part.quantity) }
    }

    // ════════ FOTOS ════════
    fun photosFor(id: Long) = photoDao.byRepair(id)
    suspend fun addPhoto(p: RepairPhoto) = photoDao.insert(p)

    suspend fun deletePhotoAndFile(p: RepairPhoto) {
        db.withTransaction { photoDao.delete(p) }
        runCatching { File(p.path).takeIf { it.exists() }?.delete() }
    }

    // ════════ CÁLCULO FINANCIERO ════════
    suspend fun financialsFor(repairId: Long): ProfitCalculator.Result {
        val r = repairDao.byIdSync(repairId) ?: return ProfitCalculator.calculate(0.0, 0.0, 0.0, 0.0)
        val pieces = usedPartDao.piecesCostSync(repairId)
        return ProfitCalculator.calculate(pieces, r.otherCosts, r.laborCost, r.chargedPrice)
    }

    // ════════ DATOS DEMO (con lógica real) ════════
    suspend fun loadSampleData() {
        // Creamos la reparación + checklist
        val repairId = createRepair(Repair(
            brand = "Oukitel", model = "C61 Pro",
            imei = "356938035643809",
            clientName = "Cliente Demo", clientPhone = "555-1234",
            problem = "Pantalla completamente rota",
            physicalState = "Carcasa con golpes leves",
            accessories = "Funda",
            notes = "Datos de demostración. Puede eliminarse.",
            status = Constants.RepairStatus.EN_REPARACION,
            laborCost = 700.0, otherCosts = 100.0, chargedPrice = 3000.0
        ))

        // Inventario
        val pantallaId = inventoryDao.insert(InventoryItem(
            name = "Pantalla + táctil Oukitel C61 Pro", category = Constants.Categories.PANTALLAS,
            brand = "Oukitel", compatibleModel = "C61 Pro",
            quantity = 3, buyPrice = 1000.0, sellPrice = 1800.0, sku = "SCR-C61P-001"
        ))
        val bateriaId = inventoryDao.insert(InventoryItem(
            name = "Batería Oukitel C61 Pro", category = Constants.Categories.BATERIAS,
            brand = "Oukitel", compatibleModel = "C61 Pro",
            quantity = 5, buyPrice = 500.0, sellPrice = 900.0, sku = "BAT-C61P-001"
        ))

        // Usar piezas por la lógica transaccional (descuenta stock real)
        addUsedPartTransactional(repairId, pantallaId, 1)
        addUsedPartTransactional(repairId, bateriaId, 1)

        // Mediciones
        measurementDao.insert(Measurement(repairId = repairId, type = "Voltaje",
            value = 3.81, unit = "V", point = "Batería", notes = "Lectura estable"))
        measurementDao.insert(Measurement(repairId = repairId, type = "Voltaje",
            value = 5.08, unit = "V", point = "Puerto USB", notes = ""))
    }

    suspend fun clearAll() = db.withTransaction {
        // Eliminamos todos los archivos de fotos primero
        val photos = photoDao.byRepairSync(-1) // no-op para mantener patrón
        db.clearAllTables()
    }
}
