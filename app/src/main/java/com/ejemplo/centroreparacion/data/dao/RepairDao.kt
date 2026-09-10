package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.Repair

@Dao
interface RepairDao {
    @Insert suspend fun insert(repair: Repair): Long
    @Update suspend fun update(repair: Repair)
    @Delete suspend fun delete(repair: Repair)

    @Query("SELECT * FROM repairs ORDER BY entryDate DESC")
    fun all(): LiveData<List<Repair>>

    @Query("SELECT * FROM repairs ORDER BY entryDate DESC LIMIT :limit")
    fun recent(limit: Int): LiveData<List<Repair>>

    @Query("SELECT * FROM repairs WHERE id = :id")
    fun byId(id: Long): LiveData<Repair?>

    @Query("SELECT * FROM repairs WHERE id = :id")
    suspend fun byIdSync(id: Long): Repair?

    @Query("SELECT COUNT(*) FROM repairs WHERE status NOT IN ('ENTREGADO','CANCELADO')")
    fun activeCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM repairs WHERE status = 'ENTREGADO'")
    fun completedCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM repairs WHERE status NOT IN ('ENTREGADO','CANCELADO')")
    fun pendingCount(): LiveData<Int>

    @Query("SELECT IFNULL(SUM(chargedPrice), 0) FROM repairs WHERE status = 'ENTREGADO'")
    fun totalRevenue(): LiveData<Double>

    @Query("SELECT IFNULL(SUM(laborCost + otherCosts), 0) FROM repairs WHERE status = 'ENTREGADO'")
    fun totalBaseCosts(): LiveData<Double>

    @Query("SELECT * FROM repairs WHERE " +
        "clientName LIKE '%' || :q || '%' OR " +
        "clientPhone LIKE '%' || :q || '%' OR " +
        "brand LIKE '%' || :q || '%' OR " +
        "model LIKE '%' || :q || '%' OR " +
        "imei LIKE '%' || :q || '%' " +
        "ORDER BY entryDate DESC")
    fun search(q: String): LiveData<List<Repair>>

    @Query("SELECT * FROM repairs WHERE status = :status ORDER BY entryDate DESC")
    fun byStatus(status: String): LiveData<List<Repair>>

    @Query("SELECT * FROM repairs WHERE entryDate >= :from AND entryDate <= :to ORDER BY entryDate DESC")
    fun byDateRange(from: Long, to: Long): LiveData<List<Repair>>
}
