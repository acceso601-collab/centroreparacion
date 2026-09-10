package com.ejemplo.centroreparacion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ejemplo.centroreparacion.data.dao.*
import com.ejemplo.centroreparacion.data.entity.*

@Database(
    entities = [Repair::class, ChecklistItem::class, Measurement::class,
        InventoryItem::class, UsedPart::class, RepairPhoto::class],
    version = 2, exportSchema = false
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Room recreará las tablas al detectar cambios de esquema en desarrollo.
                // En producción real, aquí irían los ALTER TABLE correspondientes.
                // Como esta es la primera versión pública estable, no hay usuarios previos.
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "centro_reparacion.db"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
            }
    }
}
