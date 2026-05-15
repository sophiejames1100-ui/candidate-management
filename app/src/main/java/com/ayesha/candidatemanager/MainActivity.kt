package com.ayesha.candidatemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.ayesha.candidatemanager.ui.Dashboard
import com.ayesha.candidatemanager.ui.CandidateForm
import com.ayesha.candidatemanager.ui.CandidateGrid
import com.ayesha.candidatemanager.viewmodel.CandidateViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CandidateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
                    onPrimary = androidx.compose.ui.graphics.Color.White,
                    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: CandidateViewModel) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Column(modifier = Modifier.fillMaxSize()) {
        Dashboard(viewModel)

        if (isLandscape) {
            // Tablet / Landscape Split Screen
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CandidateForm(viewModel)
                }
                Divider(modifier = Modifier.width(1.dp).fillMaxHeight())
                Box(modifier = Modifier.weight(1.5f)) {
                    CandidateGrid(viewModel)
                }
            }
        } else {
            // Phone / Portrait layout - Stacked or Tabbed (Simplified as stacked here for responsiveness)
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CandidateForm(viewModel)
                }
                Divider()
                Box(modifier = Modifier.weight(1f)) {
                    CandidateGrid(viewModel)
                }
            }
        }
    }
}
