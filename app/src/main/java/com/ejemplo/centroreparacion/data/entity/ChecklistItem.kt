package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklist_items",
    foreignKeys = [ForeignKey(
        entity = Repair::class,
        parentColumns = ["id"],
        childColumns = ["repairId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("repairId")]
)
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val category: String,
    val name: String,
    val status: String = "NOT_TESTED"
)
