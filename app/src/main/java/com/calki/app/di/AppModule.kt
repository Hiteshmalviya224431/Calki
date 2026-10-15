package com.calki.app.di

import android.content.Context
import androidx.room.Room
import com.calki.app.data.local.CalkiDatabase
import com.calki.app.data.local.HistoryDao
import com.calki.app.domain.evaluator.ExpressionEvaluator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CalkiDatabase {
        return Room.databaseBuilder(context, CalkiDatabase::class.java, "calki_db")
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideHistoryDao(db: CalkiDatabase): HistoryDao = db.historyDao()

    @Provides
    @Singleton
    fun provideExpressionEvaluator(): ExpressionEvaluator = ExpressionEvaluator()
}
