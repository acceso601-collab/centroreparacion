package com.ejemplo.centroreparacion.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ejemplo.centroreparacion.data.entity.InventoryItem

@Dao
interface InventoryDao {
    @Insert suspend fun insert(item: InventoryItem): Long
    @Update suspend fun update(item: InventoryItem)
    @Delete suspend fun delete(item: InventoryItem)
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun all(): LiveData<List<InventoryItem>>
    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun byIdSync(id: Long): InventoryItem?
    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :q || '%' OR brand LIKE '%' || :q || '%' OR sku LIKE '%' || :q || '%' ORDER BY name ASC")
    fun search(q: String): LiveData<List<InventoryItem>>
    @Query("UPDATE inventory_items SET quantity = quantity - :qty WHERE id = :id")
    suspend fun decrement(id: Long, qty: Int)
}
