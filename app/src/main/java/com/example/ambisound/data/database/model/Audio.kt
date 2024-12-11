package com.example.ambisound.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "audio")
data class Audio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "track_id") val trackId: String,
    @ColumnInfo val title: String,
    @ColumnInfo val artist: String,
    @ColumnInfo(name = "image_src") val imageSrc: String,
    @ColumnInfo(name = "preview_url") val previewUrl: String,
    @ColumnInfo(name = "length_in_seconds") val lengthInSeconds: Int,
    @ColumnInfo(name = "date_listened") val dateListened: Date = Date(0)
)