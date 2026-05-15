package com.ayesha.candidatemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val candidateName: String,
    val jobPosition: String,
    val currentCompany: String,
    val contactNumber: String,
    val emailId: String,
    val currentCtc: String,
    val ctcType: String,
    val expectedCtc: String,
    val noticePeriod: String,
    val availableToJoinFrom: String,
    val remarks: String
)
