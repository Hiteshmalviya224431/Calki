package com.calki.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CalculationEntity::class], version = 1, exportSchema = false)
abstract class CalkiDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
