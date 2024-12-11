package com.example.ambisound

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ambisound.data.database.AmbiSoundDatabase
import com.example.ambisound.data.database.DatabaseBuilder
import com.example.ambisound.data.repository.AudioRepository
import com.example.ambisound.ui.theme.AmbiSoundTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseBuilder.get(this)
        repository = AudioRepository(database!!)

        enableEdgeToEdge()
        setContent {
            AmbiSoundTheme {
                TheApp()
            }
        }
    }
}

var repository: AudioRepository? = null
var database: AmbiSoundDatabase? = null


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TheApp() {

    // Background Color wraps around app navigation
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
    {
        // establish NavController or something

        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "opening-page")
        {
            composable("opening-page") {
                OpeningPage(navController) {
                    if (it == "HistoryEscapeString") {
                        // If parameters = "HistoryEscapeString", then route to history view
                        navController.navigate("history-page")
                    } else {
                        // Convert to string to pass to audioList function
                        val searchParameters = it // it refers to search parameters
                        navController.navigate("audio-list-page/$searchParameters")
                    }
                }
            }
            composable(
                route = "audio-list-page/{searchParameters}",
                arguments = listOf(navArgument("searchParameters") { type = NavType.StringType })
            ) { backStackEntry -> // Get list of ids from parameters passed from backStack

                val searchParameterString = backStackEntry.arguments?.getString("searchParameters")

                AudioListView(navController = navController, searchParameterString = searchParameterString) {
                    navController.navigate("audio-page/$it")
                }
            }
            composable(
                route = "audio-page/{audioId}",
                arguments = listOf(navArgument("audioId") { type = NavType.LongType })
            ) { backStackEntry ->

                val audioId = backStackEntry.arguments?.getLong("audioId")!!

                AudioView(navController = navController, audioId = audioId)
            }
            composable("history-page") {
                HistoryView(navController) {
                    navController.navigate("audio-page/$it")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PagePreview() {
    AmbiSoundTheme {
        TheApp()
    }
}