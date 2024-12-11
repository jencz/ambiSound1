package com.example.ambisound.data.database

import android.content.Context
import androidx.room.Room

object TempDatabaseBuilder {
    private var db: AmbiSoundDatabase? = null

    fun reset() {
        db = null
    }

    fun get(context: Context): AmbiSoundDatabase {
        if (db == null)
            db = build(context)

        return db!!
    }

    private fun build(appContext: Context): AmbiSoundDatabase {
        return Room.inMemoryDatabaseBuilder(
            appContext,
            AmbiSoundDatabase::class.java
        ).build()
    }
}