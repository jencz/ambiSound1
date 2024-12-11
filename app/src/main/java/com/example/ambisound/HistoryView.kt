package com.example.ambisound

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ambisound.data.database.model.Audio
import com.example.ambisound.ui.theme.AmbiSoundTheme
import java.util.Date
import java.util.Dictionary
import java.util.Locale

@Composable
fun HistoryView(
    navController: NavController? = null,
    onAudioSelected: ((Int) -> Unit)? = null
) {
    var scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp, 100.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    )
    {
        val (map, setMap) = rememberSaveable<MutableState<Map<Date, List<Audio>>?>> {
            mutableStateOf(
                null
            )
        }

        LaunchedEffect(Unit) {
            val mapDb = repository!!.getAllAudio()

            setMap(mapDb)
        }

        // Elements
        if (map != null) {
            // For each
            map.toSortedMap(reverseOrder()).forEach { entry ->
                DateHeader(entry.key)

                entry.value.forEach { audio ->
                    HistoryAudioBox(audio = audio, onAudioSelected = { id ->
                        navController?.navigate("audio-page/$id")
                    })
                }
            }
        } else {
            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()
            HistoryAudioBox()

            DateHeader()
            HistoryAudioBox()
            HistoryAudioBox()
        }
    }
}

@Composable
fun DateHeader(date: Date? = null) {
    if (date != null) {
        // val dateFormated = SimpleDateFormat("dd/MM/yyyy").format(date.toString())

//        var formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val formatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())

        val formattedDate = formatter.format(date)

        Spacer(modifier = Modifier.size(25.dp))
        Text(
            text = formattedDate,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(5.dp))
    } else {
        Spacer(modifier = Modifier.size(25.dp))
        Text(
            text = "Month Day, Year",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(5.dp))
    }
}

@Composable
fun HistoryAudioBox(audio: Audio? = null, onAudioSelected: ((Long) -> Unit)? = null) {
    if (audio != null) {
        Box(
            modifier = Modifier
                .defaultMinSize(300.dp, 50.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
                .background(Color.White),

            ) {
            Text(
                text = audio.title,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Submit",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                        if (onAudioSelected != null) {
                            onAudioSelected(audio.id) // Navigate to AudioView with Id of audio
                        }
                    } // Navigate to AudioView
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    } else {
        Box(
            modifier = Modifier
                .defaultMinSize(300.dp, 50.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
                .background(Color.White),

            ) {
            Text(
                text = "Evils Larry...",
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Submit",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { } // Navigate to AudioView
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}

data class DateListenedDictionary(
    val list: Dictionary<Date, AudioList>
)

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
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
        { HistoryView() }
    }
}