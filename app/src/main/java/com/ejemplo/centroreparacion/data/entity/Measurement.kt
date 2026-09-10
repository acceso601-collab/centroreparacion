package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurements",
    foreignKeys = [ForeignKey(
        entity = Repair::class,
        parentColumns = ["id"],
        childColumns = ["repairId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("repairId")]
)
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
