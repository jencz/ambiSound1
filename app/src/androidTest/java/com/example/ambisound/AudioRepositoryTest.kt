package com.example.ambisound

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ambisound.data.database.AmbiSoundDatabase
import com.example.ambisound.data.database.TempDatabaseBuilder
import com.example.ambisound.data.database.model.Audio
import com.example.ambisound.data.repository.AudioRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AudioRepositoryTest {
    private lateinit var db: AmbiSoundDatabase
    private lateinit var repository: AudioRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = TempDatabaseBuilder.get(context)
        repository = AudioRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
        TempDatabaseBuilder.reset()
    }

    @Test
    fun addAudio() = runTest {
        val audio = Audio(
            trackId = "24601",
            title = "title3",
            artist = "artist4",
            imageSrc = "test_image_src",
            previewUrl = "test_preview_url",
            lengthInSeconds = 1,
            dateListened = Date()
        )

        repository.add(audio)

        val audioByDate = db.audioDao().getAll()
        assert(audioByDate.size == 1)

        val insertedAudio = audioByDate.first()

        assert(insertedAudio.artist == audio.artist)
    }

    @Test
    fun refreshAudio() = runTest {
        val audio = Audio(
            id = 1,
            trackId = "24601",
            title = "title3",
            artist = "artist4",
            imageSrc = "test_image_src",
            previewUrl = "test_preview_url",
            lengthInSeconds = 1,
            dateListened = Date()
        )

        repository.audioDao.insert(audio)
        val insertedAudio = repository.audioDao.get(1)
        assert(insertedAudio != null)
        repository.refresh(insertedAudio!!)

        val audioByDate = db.audioDao().getAll()
        assert(audioByDate.size == 1)

        val newAudio = audioByDate.first()

        assert(newAudio.dateListened > audio.dateListened)
    }

    @Test
    fun getAllAudio_single() = runTest {
        val audio = Audio(
            trackId = "24601",
            title = "title3",
            artist = "artist4",
            imageSrc = "test_image_src",
            previewUrl = "test_preview_url",
            lengthInSeconds = 1,
            dateListened = Date()
        )

        repository.audioDao.insert(audio)

        val audioByDate = repository.getAllAudio()
        val dateListened = audioByDate.keys.first()
        val audioInserted = audioByDate.values.first().first()

        assert(audioByDate.size == 1)
        assert(dateListened == audio.dateListened)
        assert(audioInserted.id != 0L)
        assert(audioInserted.artist == "artist4")
    }

    @Test
    fun getAudio_single() = runTest {
        val audio = Audio(
            trackId = "24601",
            title = "title3",
            artist = "artist4",
            imageSrc = "test_image_src",
            previewUrl = "test_preview_url",
            lengthInSeconds = 1,
            dateListened = Date()
        )

        repository.audioDao.insert(audio)

        val audioFetched = repository.getAudio("24601")

        assert(audioFetched != null)
        assert(audioFetched!!.artist == "artist4")
    }

    @Test
    fun getAudio_single_notFound() = runTest {
        val audioFetched = repository.getAudio("non-existent track id")

        assert(audioFetched == null)
    }

    @Test
    fun getAllAudio_multiple() = runTest {
        val audioList = listOf(
            Audio(
                trackId = "2333",
                title = "titre",
                artist = "artiste",
                imageSrc = "some_url",
                previewUrl = "some_another_url",
                lengthInSeconds = 1000,
                dateListened = Date(2000, 1, 1)
            ),
            Audio(
                trackId = "24601",
                title = "title3",
                artist = "artist4",
                imageSrc = "test_image_src",
                previewUrl = "test_preview_url",
                lengthInSeconds = 1,
                dateListened = Date(2000, 1, 2)
            ),
            Audio(
                trackId = "123",
                title = "t",
                artist = "a",
                imageSrc = "i",
                previewUrl = "p",
                lengthInSeconds = 10,
                dateListened = Date(2000, 1, 2)
            )
        )

        for (audio in audioList) {
            repository.audioDao.insert(audio)
        }

        val audioByDate = repository.getAllAudio()

        assert(audioByDate.size == 2)
        assert(audioByDate.keys.elementAt(0) == Date(2000, 1, 1))
        assert(audioByDate.keys.elementAt(1) == Date(2000, 1, 2))
        assert(audioByDate.values.elementAt(0).size == 1)
        assert(audioByDate.values.elementAt(1).size == 2)

        assert(audioByDate.values.elementAt(0)[0].title == "titre")
        assert(audioByDate.values.elementAt(1)[0].title == "title3")
        assert(audioByDate.values.elementAt(1)[1].title == "t")
    }
}