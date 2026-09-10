package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.ChecklistItem

@Dao
interface ChecklistDao {
    @Insert suspend fun insertAll(items: List<ChecklistItem>)
    @Insert suspend fun insert(item: ChecklistItem): Long
    @Update suspend fun update(item: ChecklistItem)
    @Query("SELECT * FROM checklist_items WHERE repairId = :repairId ORDER BY id ASC")
    fun byRepair(repairId: Long): LiveData<List<ChecklistItem>>
    @Query("DELETE FROM checklist_items WHERE repairId = :repairId")
    suspend fun deleteByRepair(repairId: Long)
}
