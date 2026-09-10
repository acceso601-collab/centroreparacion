package com.ejemplo.centroreparacion.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repairs",
    foreignKeys = [],
    indices = [Index("status"), Index("clientName"), Index("imei")]
)
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
    val status: String = "RECIBIDO",
    val laborCost: Double = 0.0,
    val otherCosts: Double = 0.0,
    val chargedPrice: Double = 0.0
)
