package com.ayesha.candidatemanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {
    @Query("SELECT * FROM candidates ORDER BY id DESC")
    fun getAllCandidates(): Flow<List<CandidateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateEntity)

    @Update
    suspend fun updateCandidate(candidate: CandidateEntity)

    @Delete
    suspend fun deleteCandidate(candidate: CandidateEntity)
    
    @Query("DELETE FROM candidates")
    suspend fun deleteAllCandidates()

    // Suggestions DAO methods combined here for simplicity
    @Query("SELECT * FROM suggestions WHERE type = :type ORDER BY frequency DESC, lastUsedTimestamp DESC")
    fun getSuggestionsByType(type: String): Flow<List<SuggestionEntity>>

    @Query("SELECT * FROM suggestions WHERE type = :type AND text = :text LIMIT 1")
    suspend fun getSuggestionByText(type: String, text: String): SuggestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: SuggestionEntity)

    @Update
    suspend fun updateSuggestion(suggestion: SuggestionEntity)
}
