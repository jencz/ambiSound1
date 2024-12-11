package com.example.ambisound

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.ambisound.data.database.model.Audio
import com.example.ambisound.ui.theme.AmbiSoundTheme
import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.time.temporal.ChronoUnit
import java.util.Base64
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioListView(
    navController: NavController? = null,
    searchParameterString: String? = null,
    onAudioSelected: ((Int) -> Unit)? = null
) {
    var audioList by remember { mutableStateOf(listOf<Audio>()) }
    var loading by remember { mutableStateOf(false) }

    if (searchParameterString != null) {
        LaunchedEffect(searchParameterString) {
            loading = true
            try {
                val aiResponse = callHuggingFaceAPI(searchParameterString)
                val accessToken = getSpotifyAccessToken()
                if (accessToken != null) {
                    audioList = callSpotifyAPI(accessToken, aiResponse)
                }
            } catch (e: Exception) {
                throw e
            } finally {
                loading = false
            }
        }
    }


    var scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 100.dp, 0.dp, 0.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else if (audioList.isNotEmpty()) {
            audioList.forEach { audio ->
                AudioBox(audio = audio, onAudioSelected = { id ->
                    navController?.navigate("audio-page/$id")
                })
            }
        } else {
            Text(text = "No results found.", color = Color.White)
        }
    }
}


@Composable
fun AudioBox(audio: Audio? = null, onAudioSelected: ((Long) -> Unit)? = null) {
    if (audio != null) {
        Box(
            modifier = Modifier
                .defaultMinSize(300.dp, 75.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
                .background(Color.White),

            ) {

            /*
            Image(
                painter = painterResource(R.drawable.landscape_placeholder), // Audio.Image?
                contentDescription = "AudioImage", // Audio.ImageDescription?
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            */
            AsyncImage(
                model = audio.imageSrc,
                contentDescription = "Audio Image",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Text(
                text = audio.title,
                modifier = Modifier.align(Alignment.Center)
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
                    }
            )
            Spacer(modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.size(10.dp))
        }
    } else {
        Box(
            modifier = Modifier
                .defaultMinSize(300.dp, 75.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
                .background(Color.White),

            ) {


            Image(
                painter = painterResource(R.drawable.landscape_placeholder), // Audio.Image?
                contentDescription = "AudioImage", // Audio.ImageDescription?
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Text(
                text = "Audio Name",
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Submit",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterEnd)
                    .clickable {
                    } // Navigate to AudioView
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

data class AudioList(
    val list: List<Audio>
)


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ListPreview() {
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
        { AudioListView() }
    }
}

suspend fun callHuggingFaceAPI(userMood: String): String {
    val maxNewTokens = 20
    val apiKey = "HUGGING_FACE_API_KEY"
    val modelId = "microsoft/Phi-3-mini-4k-instruct"

    val prompt =
        "Respond with exactly one music query (2-5 words) that reflects the mood: '$userMood'. The query should be simple and directly relevant for searching music. Do not include any other extra text. Only provide the query itself."

    val client = OkHttpClient()
    val requestBodyString = """
        {
            "inputs": "$prompt",
            "parameters":
            {
                "max_new_tokens": $maxNewTokens,
                "return_full_text": false
            }
        }
    """.trimIndent()

    val request = Request.Builder()
        .url("https://api-inference.huggingface.co/models/$modelId")
        .post(requestBodyString.toRequestBody("application/json".toMediaType()))
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .header("x-use-cache", "false")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw Exception("API call failed")
            val jsonArray = JSONArray(response.body!!.string())
            val rawGeneratedText = jsonArray.getJSONObject(0).getString("generated_text")
            rawGeneratedText.replace(Regex("\\n|Query|\"|:|#|\\*|Instruction|Solution|\\d"), "")
                .trim()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun callSpotifyAPI(accessToken: String, aiResponse: String): List<Audio> {
    val audios = mutableListOf<Audio>()
    val ambientGenre = "$aiResponse ambient"

    if (accessToken == null) {
        throw Exception("Error: Access token is null")
    }

    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/search?q=$ambientGenre&type=track&limit=10")
        .header("Authorization", "Bearer $accessToken")
        .header("Content-Type", "application/json")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Call Unsuccessful: ${response.code}")
            }

            val responseBody = response.body?.string() ?: throw Exception("Invalid Response Body")
            val json = JSONObject(responseBody)
            val tracks = json.getJSONObject("tracks").getJSONArray("items")

            for (i in 0 until tracks.length()) {
                val track = tracks.getJSONObject(i)
                val trackId = track.getString("id")
                val previewUrl = fetchPreviewUrl(trackId)

                if (previewUrl != null) {
                    val trackName = track.getString("name")
                    val artistName =
                        track.getJSONArray("artists").getJSONObject(0).getString("name")
                    val artwork =
                        track.getJSONObject("album").getJSONArray("images").getJSONObject(0)
                            .getString("url")
                    val duration = track.getString("duration_ms").toInt() / 1000

                    var audioDb = repository!!.getAudio(trackId)

                    // if track is included in database
                    if (audioDb == null) {
                        val audio =
                            Audio(
                                trackId = trackId,
                                title = trackName,
                                artist = artistName,
                                imageSrc = artwork,
                                previewUrl = previewUrl,
                                lengthInSeconds = duration,
                                dateListened = Date.from(
                                    Date().toInstant().truncatedTo(ChronoUnit.MINUTES)
                                )
                            )
                        val audioId = repository!!.add(audio)
                        audioDb = repository!!.getAudio(audioId)
                    }

                    audios.add(audioDb!!)
                }
            }

            audios
        } catch (e: Exception) {
            throw e
        }
    }
}

@SuppressLint("NewApi")
suspend fun getSpotifyAccessToken(): String? {
    val clientId = "SPOTIFY_CLIENT_ID"
    val clientSecret = "SPOTIFY_SECRET"
    val client = OkHttpClient()

    val credentials = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    val requestBodyString = "grant_type=client_credentials&scope=user-modify-playback-state"

    val request = Request.Builder()
        .url("https://accounts.spotify.com/api/token")
        .post(requestBodyString.toRequestBody("application/x-www-form-urlencoded".toMediaType()))
        .header("Authorization", "Basic $credentials")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Call Unsuccessful")
            }

            val responseBody = response.body?.string() ?: throw Exception("Invalid Body")
            val json = JSONObject(responseBody)
            json.getString("access_token")
        } catch (e: Exception) {
            throw e
        }
    }
}

// The preview_url endpoint for spotify was deprecated so we had to find a workaround to get a track's preview url. Shoutout to Diego Perez for this solution: https://stackoverflow.com/a/79238027
fun fetchPreviewUrl(trackId: String): String? {
    val embedUrl = "https://open.spotify.com/embed/track/$trackId"
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(embedUrl)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Failed to fetch embed page: ${response.code}")
            return null
        }

        val html = response.body?.string() ?: return null
        val document = Jsoup.parse(html)
        val scriptElements = document.getElementsByTag("script")

        for (element in scriptElements) {
            val scriptContent = element.html()
            if (scriptContent.isNotEmpty()) {
                /*val jsonObject = JSONObject(scriptContent)
                val audioPreview1 = findNodeValue1(jsonObject, "audioPreview")*/
                return findNodeValueWithJsonPath(scriptContent, "audioPreview")
            }
        }
    }
    return null
}

private fun findNodeValueWithJsonPath(jsonString: String, targetNode: String): String? {
    return try {
        // Construct the JsonPath query
        val query = "$..$targetNode.url"
        println("Using JsonPath Query: $query") // Debug query

        // Perform the query
        // Handle cases where the result is a list (e.g., multiple matches)
        when (val result = JsonPath.read<Any>(jsonString, query)) {
            is List<*> -> {
                if (result.isNotEmpty()) result[0].toString() else null // Return the first match
            }

            is String -> result // Directly return the string if it's not a list
            else -> null
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    }
}