package com.example.ambisound.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ambisound.data.database.AmbiSoundDatabase
import com.example.ambisound.data.database.model.Audio
import java.time.temporal.ChronoUnit
import java.util.Date

class AudioRepository(val db: AmbiSoundDatabase) {
    val audioDao = db.audioDao()

    suspend fun getAllAudio(): Map<Date, List<Audio>> {
        return audioDao.getAll().groupBy { it.dateListened }
    }

    suspend fun getAudio(id: Long): Audio? {
        return audioDao.get(id)
    }

    suspend fun getAudio(trackId: String): Audio? {
        return audioDao.get(trackId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refresh(audio: Audio) {
        val newAudio = audio.copy(dateListened = Date.from(Date().toInstant().truncatedTo(ChronoUnit.MINUTES)))
        audioDao.update(newAudio)
    }

    suspend fun add(audio: Audio): Long {
        return audioDao.insert(audio)
    }
}