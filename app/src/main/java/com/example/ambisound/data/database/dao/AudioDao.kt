package com.example.ambisound.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Update
import com.example.ambisound.data.database.model.Audio
import java.util.Date

@Dao
interface AudioDao {
    @Query("SELECT * FROM audio " +
            "WHERE id = :id")
    suspend fun get(id: Long): Audio?

    @Query("SELECT * FROM audio " +
            "WHERE track_id = :trackId")
    suspend fun get(trackId: String): Audio?

    @Query("SELECT * FROM audio " +
            "ORDER BY date_listened")
    suspend fun getAll(): List<Audio>

    @Insert
    suspend fun insert(audio: Audio): Long

    @Update
    suspend fun update(audio: Audio)
}