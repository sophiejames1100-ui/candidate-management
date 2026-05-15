package com.ayesha.candidatemanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayesha.candidatemanager.data.CandidateEntity
import com.ayesha.candidatemanager.util.CsvExporter
import com.ayesha.candidatemanager.viewmodel.CandidateViewModel

@Composable
fun CandidateGrid(viewModel: CandidateViewModel) {
    val candidates by viewModel.candidates.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Candidates", style = MaterialTheme.typography.titleLarge)
            
            Button(onClick = { CsvExporter.exportToCsv(context, candidates) }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Export CSV", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export CSV")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Text("Name", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("Job Position", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("Contact", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Notice", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Action", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        }
        Divider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(candidates) { candidate ->
                CandidateRow(candidate, onDelete = { viewModel.deleteCandidate(it) })
                Divider()
            }
        }
    }
}

@Composable
fun CandidateRow(candidate: CandidateEntity, onDelete: (CandidateEntity) -> Unit) {
    val rowColor = if (candidate.noticePeriod.equals("Immediate", ignoreCase = true)) {
        Color(0xFFE8F5E9) // Light green for immediate
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(candidate.candidateName, modifier = Modifier.weight(1.5f))
        Text(candidate.jobPosition, modifier = Modifier.weight(1.5f))
        Text(candidate.contactNumber, modifier = Modifier.weight(1f))
        Text(candidate.noticePeriod, modifier = Modifier.weight(1f))
        
        IconButton(onClick = { onDelete(candidate) }, modifier = Modifier.weight(0.5f)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
        }
    }
}
