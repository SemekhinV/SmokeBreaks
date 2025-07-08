package com.smokebreakbuddy.data.local.dao

import androidx.room.*
import com.smokebreakbuddy.data.model.BreakSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BreakSessionDao {
    
    @Query("SELECT * FROM break_sessions WHERE sessionId = :sessionId")
    fun getSessionById(sessionId: String): Flow<BreakSession?>
    
    @Query("SELECT * FROM break_sessions WHERE invitationId = :invitationId")
    fun getSessionByInvitationId(invitationId: String): Flow<BreakSession?>
    
    @Query("SELECT * FROM break_sessions WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getSessionsForGroup(groupId: String): Flow<List<BreakSession>>
    
    @Query("SELECT * FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' ORDER BY createdAt DESC")
    fun getSessionsForUser(userId: String): Flow<List<BreakSession>>
    
    @Query("SELECT * FROM break_sessions WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getSessionsInDateRange(startDate: Date, endDate: Date): Flow<List<BreakSession>>
    
    @Query("SELECT * FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getUserSessionsInDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<BreakSession>>
    
    @Query("SELECT AVG(actualDuration) FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND createdAt >= :startDate")
    suspend fun getAverageSessionDurationForUser(userId: String, startDate: Date): Double?
    
    @Query("SELECT AVG(rating) FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND rating > 0")
    suspend fun getAverageRatingForUser(userId: String): Double?
    
    @Query("SELECT COUNT(*) FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodaySessionCountForUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM break_sessions WHERE groupId = :groupId AND DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodaySessionCountForGroup(groupId: String): Int
    
    @Query("SELECT SUM(actualDuration) FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND createdAt >= :startDate")
    suspend fun getTotalBreakTimeForUser(userId: String, startDate: Date): Int?
    
    @Query("SELECT COUNT(*) FROM break_sessions WHERE participantIds LIKE '%' || :userId || '%' AND createdAt >= :startDate AND createdAt <= :endDate")
    suspend fun getSessionCountForUserInPeriod(userId: String, startDate: Date, endDate: Date): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BreakSession)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<BreakSession>)
    
    @Update
    suspend fun updateSession(session: BreakSession)
    
    @Query("UPDATE break_sessions SET actualDuration = :duration, endTime = :endTime WHERE sessionId = :sessionId")
    suspend fun updateSessionDuration(sessionId: String, duration: Int, endTime: Date)
    
    @Query("UPDATE break_sessions SET rating = :rating, feedback = :feedback WHERE sessionId = :sessionId")
    suspend fun updateSessionRating(sessionId: String, rating: Int, feedback: String)
    
    @Delete
    suspend fun deleteSession(session: BreakSession)
    
    @Query("DELETE FROM break_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: String)
    
    @Query("DELETE FROM break_sessions WHERE createdAt < :cutoffDate")
    suspend fun deleteOldSessions(cutoffDate: Date)
    
    @Query("DELETE FROM break_sessions")
    suspend fun deleteAllSessions()
}
