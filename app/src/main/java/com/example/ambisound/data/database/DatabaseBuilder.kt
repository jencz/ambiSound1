package com.example.ambisound.data.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    var db: AmbiSoundDatabase? = null

    fun get(context: Context): AmbiSoundDatabase {
        if (db == null)
            db = build(context)

        return db!!
    }

    private fun build(appContext: Context): AmbiSoundDatabase {
        return Room.databaseBuilder(
            appContext,
            AmbiSoundDatabase::class.java,
            "ambisound"
        ).build()
    }
}