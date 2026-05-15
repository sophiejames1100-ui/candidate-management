package com.ayesha.candidatemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestions")
data class SuggestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // e.g., "JOB_POSITION", "COMPANY", "NOTICE_PERIOD", "REMARK"
    val text: String,
    val frequency: Int = 1,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
