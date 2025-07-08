package com.smokebreakbuddy.data.local.dao

import androidx.room.*
import com.smokebreakbuddy.data.model.BreakInvitation
import com.smokebreakbuddy.data.model.BreakInvitationStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BreakInvitationDao {
    
    @Query("SELECT * FROM break_invitations WHERE invitationId = :invitationId")
    fun getInvitationById(invitationId: String): Flow<BreakInvitation?>
    
    @Query("SELECT * FROM break_invitations WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getInvitationsForGroup(groupId: String): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE initiatorUserId = :userId ORDER BY createdAt DESC")
    fun getInvitationsCreatedBy(userId: String): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE status = :status ORDER BY createdAt DESC")
    fun getInvitationsByStatus(status: BreakInvitationStatus): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE status = 'PENDING' AND expiresAt > :currentTime ORDER BY createdAt DESC")
    fun getActiveInvitations(currentTime: Date): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE groupId = :groupId AND status = 'PENDING' AND expiresAt > :currentTime ORDER BY createdAt DESC")
    fun getActiveInvitationsForGroup(groupId: String, currentTime: Date): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getInvitationsInDateRange(startDate: Date, endDate: Date): Flow<List<BreakInvitation>>
    
    @Query("SELECT * FROM break_invitations WHERE initiatorUserId = :userId AND createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getUserInvitationsInDateRange(userId: String, startDate: Date, endDate: Date): Flow<List<BreakInvitation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: BreakInvitation)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitations(invitations: List<BreakInvitation>)
    
    @Update
    suspend fun updateInvitation(invitation: BreakInvitation)
    
    @Query("UPDATE break_invitations SET status = :status WHERE invitationId = :invitationId")
    suspend fun updateInvitationStatus(invitationId: String, status: BreakInvitationStatus)
    
    @Query("UPDATE break_invitations SET status = 'EXPIRED' WHERE expiresAt <= :currentTime AND status = 'PENDING'")
    suspend fun expireOldInvitations(currentTime: Date)
    
    @Delete
    suspend fun deleteInvitation(invitation: BreakInvitation)
    
    @Query("DELETE FROM break_invitations WHERE invitationId = :invitationId")
    suspend fun deleteInvitationById(invitationId: String)
    
    @Query("DELETE FROM break_invitations WHERE createdAt < :cutoffDate")
    suspend fun deleteOldInvitations(cutoffDate: Date)
    
    @Query("DELETE FROM break_invitations")
    suspend fun deleteAllInvitations()
    
    @Query("SELECT COUNT(*) FROM break_invitations WHERE initiatorUserId = :userId AND DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodayInvitationCountForUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM break_invitations WHERE groupId = :groupId AND DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodayInvitationCountForGroup(groupId: String): Int
}
