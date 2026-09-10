package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.Measurement

@Dao
interface MeasurementDao {
    @Insert suspend fun insert(m: Measurement): Long
    @Delete suspend fun delete(m: Measurement)
    @Query("SELECT * FROM measurements WHERE repairId = :repairId ORDER BY timestamp DESC")
    fun byRepair(repairId: Long): LiveData<List<Measurement>>
}
