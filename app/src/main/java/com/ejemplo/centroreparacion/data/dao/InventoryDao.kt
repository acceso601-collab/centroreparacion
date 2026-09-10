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
    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun byId(id: Long): LiveData<InventoryItem?>

    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :q || '%' OR brand LIKE '%' || :q || '%' OR sku LIKE '%' || :q || '%' ORDER BY name ASC")
    fun search(q: String): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE category = :cat ORDER BY name ASC")
    fun byCategory(cat: String): LiveData<List<InventoryItem>>

    /**
     * Descuenta stock solo si hay suficiente. Devuelve el número de filas afectadas.
     * Si devuelve 0, significa que NO se modificó nada (stock insuficiente).
     */
    @Query("UPDATE inventory_items SET quantity = quantity - :qty WHERE id = :id AND quantity >= :qty")
    suspend fun decrementIfAvailable(id: Long, qty: Int): Int

    @Query("UPDATE inventory_items SET quantity = quantity + :qty WHERE id = :id")
    suspend fun increment(id: Long, qty: Int)

    @Query("SELECT COUNT(*) FROM inventory_items")
    fun count(): LiveData<Int>

    @Query("SELECT SUM(quantity * buyPrice) FROM inventory_items")
    fun totalValue(): LiveData<Double?>
}
