package com.ayesha.candidatemanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayesha.candidatemanager.data.CandidateEntity
import com.ayesha.candidatemanager.ui.components.HandwritingCanvasDialog
import com.ayesha.candidatemanager.viewmodel.CandidateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateForm(viewModel: CandidateViewModel) {
    var date by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var candidateName by remember { mutableStateOf("") }
    var jobPosition by remember { mutableStateOf("") }
    var currentCompany by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var emailId by remember { mutableStateOf("") }
    var currentCtc by remember { mutableStateOf("") }
    var ctcType by remember { mutableStateOf("Fixed") }
    var expectedCtc by remember { mutableStateOf("") }
    var noticePeriod by remember { mutableStateOf("") }
    var availableToJoinFrom by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val jobPositions by viewModel.jobPositions.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val noticePeriods by viewModel.noticePeriods.collectAsState()
    val remarksHistory by viewModel.remarks.collectAsState()

    var showHandwritingCanvasFor by remember { mutableStateOf<String?>(null) }

    if (showHandwritingCanvasFor != null) {
        HandwritingCanvasDialog(
            onDismiss = { showHandwritingCanvasFor = null },
            onTextRecognized = { text ->
                when (showHandwritingCanvasFor) {
                    "Name" -> candidateName = text
                    "Job" -> jobPosition = text
                    "Company" -> currentCompany = text
                    "Contact" -> contactNumber = text
                    "Email" -> emailId = text
                    "CurrentCTC" -> currentCtc = text
                    "ExpectedCTC" -> expectedCtc = text
                    "Notice" -> noticePeriod = text
                    "Remarks" -> remarks = text
                }
                showHandwritingCanvasFor = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Candidate Entry Form", style = MaterialTheme.typography.titleLarge) }

        item {
            CustomTextField("Date", date, { date = it }, null)
        }
        item {
            CustomTextField("Candidate Name", candidateName, { candidateName = it }, { showHandwritingCanvasFor = "Name" })
        }
        item {
            AutoCompleteTextField("Job Position", jobPosition, { jobPosition = it }, jobPositions, { showHandwritingCanvasFor = "Job" })
        }
        item {
            AutoCompleteTextField("Current Company", currentCompany, { currentCompany = it }, companies, { showHandwritingCanvasFor = "Company" })
        }
        item {
            CustomTextField("Contact Number", contactNumber, { contactNumber = it }, { showHandwritingCanvasFor = "Contact" })
        }
        item {
            CustomTextField("Email ID", emailId, { emailId = it }, { showHandwritingCanvasFor = "Email" })
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomTextField("Current CTC", currentCtc, { currentCtc = it }, { showHandwritingCanvasFor = "CurrentCTC" }, Modifier.weight(1f))
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = ctcType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("CTC Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Fixed", "Variable", "Fixed + Variable").forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    ctcType = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            CustomTextField("Expected CTC (Lakhs)", expectedCtc, { expectedCtc = it }, { showHandwritingCanvasFor = "ExpectedCTC" })
        }
        item {
            AutoCompleteTextField("Notice Period", noticePeriod, { noticePeriod = it }, noticePeriods, { showHandwritingCanvasFor = "Notice" })
        }
        item {
            CustomTextField("Available To Join From", availableToJoinFrom, { availableToJoinFrom = it }, null)
        }
        item {
            AutoCompleteTextField("Remarks", remarks, { remarks = it }, remarksHistory, { showHandwritingCanvasFor = "Remarks" })
        }
        item {
            Button(
                onClick = {
                    val candidate = CandidateEntity(
                        date = date,
                        candidateName = candidateName,
                        jobPosition = jobPosition,
                        currentCompany = currentCompany,
                        contactNumber = contactNumber,
                        emailId = emailId,
                        currentCtc = currentCtc,
                        ctcType = ctcType,
                        expectedCtc = expectedCtc,
                        noticePeriod = noticePeriod,
                        availableToJoinFrom = availableToJoinFrom,
                        remarks = remarks
                    )
                    viewModel.addCandidate(candidate)
                    
                    // Reset fields
                    candidateName = ""
                    jobPosition = ""
                    currentCompany = ""
                    contactNumber = ""
                    emailId = ""
                    currentCtc = ""
                    expectedCtc = ""
                    noticePeriod = ""
                    availableToJoinFrom = ""
                    remarks = ""
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Save Candidate")
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onStylusClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            if (onStylusClick != null) {
                IconButton(onClick = onStylusClick) {
                    Icon(imageVector = Icons.Default.Create, contentDescription = "Write with Stylus")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onStylusClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = {
                IconButton(onClick = onStylusClick) {
                    Icon(imageVector = Icons.Default.Create, contentDescription = "Write with Stylus")
                }
            }
        )
        val filteredOptions = suggestions.filter { it.contains(value, ignoreCase = true) }
        if (filteredOptions.isNotEmpty() && expanded) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                filteredOptions.take(5).forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
