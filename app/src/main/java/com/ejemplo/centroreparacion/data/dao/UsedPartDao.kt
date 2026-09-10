package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.UsedPart

@Dao
interface UsedPartDao {
    @Insert suspend fun insert(p: UsedPart): Long
    @Delete suspend fun delete(p: UsedPart)
    @Query("SELECT * FROM used_parts WHERE repairId = :repairId ORDER BY id ASC")
    fun byRepair(repairId: Long): LiveData<List<UsedPart>>
    @Query("SELECT * FROM used_parts WHERE repairId = :repairId")
    suspend fun byRepairSync(repairId: Long): List<UsedPart>
    @Query("SELECT IFNULL(SUM(quantity * unitPrice), 0) FROM used_parts WHERE repairId = :repairId")
    fun piecesCostFor(repairId: Long): LiveData<Double>
    @Query("SELECT IFNULL(SUM(quantity * unitPrice), 0) FROM used_parts WHERE repairId = :repairId")
    suspend fun piecesCostSync(repairId: Long): Double
    @Query("SELECT * FROM used_parts WHERE inventoryId = :invId")
    suspend fun byInventory(invId: Long): List<UsedPart>
}
