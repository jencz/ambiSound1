package com.example.ambisound

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ambisound.ui.theme.AmbiSoundTheme

@Composable
fun OpeningPage(
    navController: NavController? = null,
    onSwitchPage: ((String) -> Unit)? = null, // List of Ids to pass back to nav when switching over to ListView
) {
    var searchParameters = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.opening_explore_new_ambient_sounds),
            color = Color.White
        )

        OutlinedTextField(
            value = searchParameters.value,
            onValueChange = { newParameter: String -> searchParameters.value = newParameter },
            label = {
                Text(
                    text = stringResource(R.string.opening_search_label)
                )
            },
            trailingIcon = {
                // Example: Clear Icon, which clears the text when clicked
                if (searchParameters.value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Submit",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                if (onSwitchPage != null) {
                                    onSwitchPage(searchParameters.value) // Navigate to listView
                                }
                            }
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.LightGray,
                focusedLabelColor = Color.White
            )
        )
        Spacer(modifier = Modifier.size(40.dp))
        Button(
            onClick = {
                // Get Dictionary of history objects

                if (onSwitchPage != null) {
                    onSwitchPage("HistoryEscapeString") // Navigate to History page
                }
            }
        ) {
            Text(
                text = stringResource(R.string.opening_history_button_label),
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OpeningPagePreview() {
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
        { OpeningPage() }
    }
}