package com.ayesha.candidatemanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayesha.candidatemanager.viewmodel.CandidateViewModel

@Composable
fun Dashboard(viewModel: CandidateViewModel) {
    val candidates by viewModel.candidates.collectAsState(initial = emptyList())

    val totalCandidates = candidates.size
    val immediateJoiners = candidates.count { it.noticePeriod.equals("Immediate", ignoreCase = true) }
    // Very basic average expected CTC calculation (assuming string is numeric)
    val avgCtc = if (candidates.isNotEmpty()) {
        candidates.mapNotNull { it.expectedCtc.toDoubleOrNull() }.average().takeIf { !it.isNaN() }?.let {
            String.format("%.2f", it)
        } ?: "0"
    } else "0"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DashboardCard("Total Candidates", totalCandidates.toString(), Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        DashboardCard("Immediate Joiners", immediateJoiners.toString(), Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        DashboardCard("Avg Expected CTC", "$avgCtc L", Modifier.weight(1f))
    }
}

@Composable
fun DashboardCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
