package com.ejemplo.centroreparacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ejemplo.centroreparacion.data.dao.*
import com.ejemplo.centroreparacion.data.entity.*

@Database(
    entities = [Repair::class, ChecklistItem::class, Measurement::class,
        InventoryItem::class, UsedPart::class, RepairPhoto::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repairDao(): RepairDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun usedPartDao(): UsedPartDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "centro_reparacion.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
