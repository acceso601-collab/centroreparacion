package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.RepairPhoto

@Dao
interface PhotoDao {
    @Insert suspend fun insert(p: RepairPhoto): Long
    @Delete suspend fun delete(p: RepairPhoto)
    @Query("SELECT * FROM repair_photos WHERE repairId = :repairId ORDER BY timestamp DESC")
    fun byRepair(repairId: Long): LiveData<List<RepairPhoto>>
}
