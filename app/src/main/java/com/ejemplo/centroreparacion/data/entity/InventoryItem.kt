package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_items",
    indices = [Index("name"), Index("sku"), Index("category")]
)
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
) {
    val lowStock: Boolean get() = quantity <= 2
}
