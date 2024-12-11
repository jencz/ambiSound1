package com.example.ambisound.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ambisound.data.database.dao.AudioDao
import com.example.ambisound.data.database.model.Audio

@Database(entities = [Audio::class], version = 1)
@TypeConverters(Converters::class)
abstract class AmbiSoundDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
}