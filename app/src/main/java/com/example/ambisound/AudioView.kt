package com.example.ambisound

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.ambisound.data.database.model.Audio
import com.example.ambisound.ui.theme.AmbiSoundTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

var updateAudioPlayerJob: Job? = null

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioView(
    navController: NavController? = null,
    audioId: Long
) {
    val (audio, setAudio) = remember<MutableState<Audio?>> { mutableStateOf(null) }
    val (mediaPlayer, setMediaPlayer) = remember<MutableState<MediaPlayer?>> {
        mutableStateOf(null)
    }
    val (isLoading, setIsLoading) = remember { mutableStateOf(true) }
    val (isPlaying, setIsPlaying) = remember { mutableStateOf(false) }
    val (playTime, setPlayTime) = remember { mutableStateOf("0:00") }
    val (playProgress, setPlayProgress) = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val audioDb = repository!!.getAudio(audioId)
        repository!!.refresh(audioDb!!)

        setAudio(audioDb)
        val mp = MediaPlayer()
        mp.setDataSource(audioDb.previewUrl)
        mp.prepare()
        setMediaPlayer(mp)
        setIsLoading(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        if (isLoading)
        {
            Image(
                painter = painterResource(R.drawable.landscape_placeholder),
                contentDescription = "AudioImageDescription",
                modifier = Modifier
                    .size(225.dp)
                    .clip(RoundedCornerShape(30.dp))
            )
        }
        else
        {
            AsyncImage(
                model = audio!!.imageSrc,
                contentDescription = "Audio Image",
                modifier = Modifier
                    .size(225.dp)
                    .clip(RoundedCornerShape(30.dp))
            )
        }

        Spacer(modifier = Modifier.size(15.dp))

        Text(
            text = if (isLoading) "AudioName" else audio!!.title,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.size(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            // Rewind
            Button(
                onClick = {
                    mediaPlayer?.seekTo(max(mediaPlayer.currentPosition - 5000, 0))
                    startPlayer(mediaPlayer!!, setIsPlaying, setPlayTime, setPlayProgress)
                },
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
            {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(70.dp)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            // Play/Pause
            Button(
                onClick = if (!isLoading)
                    if (isPlaying) ({
                        // Pause Audio
                        stopPlayer(mediaPlayer!!, setIsPlaying)
                    }) else ({
                        // Play Audio
                        startPlayer(mediaPlayer!!, setIsPlaying, setPlayTime, setPlayProgress)
                    })
                else ({}),
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentPadding = PaddingValues(0.dp)
            )
            {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            // Forward
            Button(
                onClick = {
                    mediaPlayer?.seekTo(
                        min(
                            mediaPlayer.currentPosition + 5000,
                            mediaPlayer.duration
                        )
                    )
                    startPlayer(mediaPlayer!!, setIsPlaying, setPlayTime, setPlayProgress)
                },
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
            {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }
        Spacer(modifier = Modifier.size(30.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            Text(text = playTime, color = Color.White) // Current TimeStamp 0:00

            Spacer(modifier = Modifier.size(15.dp))

            Slider(
                value = playProgress,
                onValueChange = { newPosition: Float ->
                    stopPlayer(mediaPlayer!!, setIsPlaying)
                    mediaPlayer.seekTo((newPosition * mediaPlayer.duration).toInt())
                    setPlayProgress(newPosition)
                },
                onValueChangeFinished = {
                    startPlayer(mediaPlayer!!, setIsPlaying, setPlayTime, setPlayProgress)
                },
                valueRange = (0f..1f), // Audio Duration
                steps = 100,
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Spacer(modifier = Modifier.size(15.dp))

            var lengthText: String

            if (audio == null) {
                lengthText = "x:xx"
            } else {
                val minutes = audio.lengthInSeconds / 60
                val seconds = audio.lengthInSeconds % 60
                lengthText = "${minutes}:${seconds}"
            }

            Text(text = lengthText, color = Color.White) // Total duration x:xx
        }
    }

}

fun startPlayer(
    mediaPlayer: MediaPlayer,
    setIsPlaying: (Boolean) -> Unit,
    setPlayTime: (String) -> Unit,
    setPlayProgress: (Float) -> Unit
) {
    mediaPlayer.start()
    setIsPlaying(true)
    startUpdatingAudioPlayer(
        mediaPlayer = mediaPlayer,
        updatePlayTime = setPlayTime,
        updatePlayProgress = setPlayProgress
    )
}

fun stopPlayer(mediaPlayer: MediaPlayer, setIsPlaying: (Boolean) -> Unit) {
    setIsPlaying(false)
    mediaPlayer.pause()
    stopUpdatingAudioPlayer()
}

fun startUpdatingAudioPlayer(
    mediaPlayer: MediaPlayer,
    updatePlayTime: (String) -> Unit,
    updatePlayProgress: (Float) -> Unit
) {
    updateAudioPlayerJob = MainScope().launch {
        while (true) {
            val minutes = (mediaPlayer.currentPosition / 1000 / 60).toString().padStart(2, '0')
            val seconds = (mediaPlayer.currentPosition / 1000 % 60).toString().padStart(2, '0')
            val progress =
                mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()

            updatePlayTime("${minutes}:${seconds}")
            updatePlayProgress(progress)

            delay(1000)
        }
    }
}

fun stopUpdatingAudioPlayer() {
    updateAudioPlayerJob?.cancel()
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AudioPreview() {
    AmbiSoundTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF040B1F),
                            Color(0xFF235AA1)
                        )
                    )
                )
        )
        { AudioView(audioId = 1) }
    }
}