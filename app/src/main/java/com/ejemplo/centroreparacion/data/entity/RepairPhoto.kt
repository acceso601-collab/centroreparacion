package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repair_photos",
    foreignKeys = [ForeignKey(
        entity = Repair::class,
        parentColumns = ["id"],
        childColumns = ["repairId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("repairId")]
)
data class RepairPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repairId: Long,
    val path: String,
    val timestamp: Long = System.currentTimeMillis()
)
