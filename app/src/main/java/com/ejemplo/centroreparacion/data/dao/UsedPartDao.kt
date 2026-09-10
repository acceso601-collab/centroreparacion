package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.UsedPart

@Dao
interface UsedPartDao {
    @Insert suspend fun insert(p: UsedPart): Long
    @Delete suspend fun delete(p: UsedPart)
    @Query("SELECT * FROM used_parts WHERE repairId = :repairId")
    fun byRepair(repairId: Long): LiveData<List<UsedPart>>
}
