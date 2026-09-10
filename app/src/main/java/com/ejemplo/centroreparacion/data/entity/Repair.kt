package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RepairStatus(val label: String) {
    RECIBIDO("Recibido"),
    DIAGNOSTICANDO("Diagnosticando"),
    ESPERANDO_PIEZAS("Esperando piezas"),
    EN_REPARACION("En reparación"),
    EN_PRUEBAS("En pruebas"),
    REPARADO("Reparado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}

@Entity(tableName = "repairs")
data class Repair(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String = "",
    val model: String = "",
    val imei: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val problem: String = "",
    val physicalState: String = "",
    val accessories: String = "",
    val pin: String = "",
    val entryDate: Long = System.currentTimeMillis(),
    val estimatedDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: String = RepairStatus.RECIBIDO.name,
    val laborCost: Double = 0.0,
    val otherCosts: Double = 0.0,
    val chargedPrice: Double = 0.0
) {
    val statusEnum: RepairStatus get() = try { RepairStatus.valueOf(status) } catch (e: Exception) { RepairStatus.RECIBIDO }
}

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val category: String,
    val name: String,
    val status: String = "NOT_TESTED" // OK, FAILED, NOT_TESTED
)

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val type: String,
    val value: Double,
    val unit: String,
    val point: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val brand: String = "",
    val compatibleModel: String = "",
    val quantity: Int = 0,
    val buyPrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val location: String = "",
    val sku: String = "",
    val notes: String = ""
)

@Entity(tableName = "used_parts")
data class UsedPart(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val inventoryId: Long = 0,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
)

@Entity(tableName = "repair_photos")
data class RepairPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val path: String,
    val timestamp: Long = System.currentTimeMillis()
)
