package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "used_parts",
    foreignKeys = [
        ForeignKey(
            entity = Repair::class,
            parentColumns = ["id"],
            childColumns = ["repairId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = InventoryItem::class,
            parentColumns = ["id"],
            childColumns = ["inventoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("repairId"), Index("inventoryId")]
)
data class UsedPart(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val inventoryId: Long?,
    val name: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double get() = unitPrice * quantity
}
