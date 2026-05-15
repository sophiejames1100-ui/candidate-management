package com.ayesha.candidatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayesha.candidatemanager.data.AppDatabase
import com.ayesha.candidatemanager.data.CandidateEntity
import com.ayesha.candidatemanager.data.SuggestionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CandidateViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).candidateDao()

    val candidates: Flow<List<CandidateEntity>> = dao.getAllCandidates()

    // Suggestions state
    private val _jobPositions = MutableStateFlow<List<String>>(emptyList())
    val jobPositions: StateFlow<List<String>> = _jobPositions.asStateFlow()

    private val _companies = MutableStateFlow<List<String>>(emptyList())
    val companies: StateFlow<List<String>> = _companies.asStateFlow()

    private val _noticePeriods = MutableStateFlow<List<String>>(
        listOf("Immediate", "15 Days", "1 Month", "2 Months", "3 Months", "4 Months")
    )
    val noticePeriods: StateFlow<List<String>> = _noticePeriods.asStateFlow()

    private val _remarks = MutableStateFlow<List<String>>(emptyList())
    val remarks: StateFlow<List<String>> = _remarks.asStateFlow()

    init {
        loadSuggestions()
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            dao.getSuggestionsByType("JOB_POSITION").collect { list ->
                _jobPositions.value = list.map { it.text }
            }
        }
        viewModelScope.launch {
            dao.getSuggestionsByType("COMPANY").collect { list ->
                _companies.value = list.map { it.text }
            }
        }
        viewModelScope.launch {
            dao.getSuggestionsByType("NOTICE_PERIOD").collect { list ->
                val defaults = listOf("Immediate", "15 Days", "1 Month", "2 Months", "3 Months", "4 Months")
                _noticePeriods.value = (defaults + list.map { it.text }).distinct()
            }
        }
        viewModelScope.launch {
            dao.getSuggestionsByType("REMARK").collect { list ->
                _remarks.value = list.map { it.text }
            }
        }
    }

    fun addCandidate(candidate: CandidateEntity) {
        viewModelScope.launch {
            dao.insertCandidate(candidate)
            saveSuggestion("JOB_POSITION", candidate.jobPosition)
            saveSuggestion("COMPANY", candidate.currentCompany)
            saveSuggestion("NOTICE_PERIOD", candidate.noticePeriod)
            saveSuggestion("REMARK", candidate.remarks)
        }
    }

    fun deleteCandidate(candidate: CandidateEntity) {
        viewModelScope.launch {
            dao.deleteCandidate(candidate)
        }
    }

    private suspend fun saveSuggestion(type: String, text: String) {
        if (text.isBlank()) return
        val existing = dao.getSuggestionByText(type, text)
        if (existing != null) {
            dao.updateSuggestion(existing.copy(frequency = existing.frequency + 1, lastUsedTimestamp = System.currentTimeMillis()))
        } else {
            dao.insertSuggestion(SuggestionEntity(type = type, text = text))
        }
    }
}
