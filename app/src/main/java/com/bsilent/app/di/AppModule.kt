package com.bsilent.app.di

import android.content.Context
import androidx.room.Room
import com.bsilent.app.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_db"
    ).build()

    @Singleton
    @Provides
    fun providePlacesDao(db: AppDatabase) = db.placesDao

    @Singleton
    @Provides
    fun provideScheduleDao(db: AppDatabase) = db.scheduleDao

    @Singleton
    @Provides
    fun provideFilesDir(@ApplicationContext context: Context): File = context.filesDir

}