package com.smokebreakbuddy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smokebreakbuddy.data.local.dao.BreakInvitationDao
import com.smokebreakbuddy.data.model.BreakInvitation
import com.smokebreakbuddy.data.model.BreakInvitationStatus
import com.smokebreakbuddy.data.model.BreakResponse
import com.smokebreakbuddy.data.model.ResponseType
import com.smokebreakbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreakInvitationRepository @Inject constructor(
    private val breakInvitationDao: BreakInvitationDao,
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) {
    
    private val invitationsCollection = firestore.collection("break_invitations")
    
    suspend fun createBreakInvitation(
        groupId: String,
        message: String,
        location: String,
        plannedDuration: Int
    ): Resource<BreakInvitation> {
        return try {
            val currentUser = userRepository.getCurrentUser()
            val userId = userRepository.getCurrentUserId()
            
            // Create expiration time (default 10 minutes)
            val expirationTime = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 10)
            }.time
            
            val invitation = BreakInvitation(
                invitationId = UUID.randomUUID().toString(),
                groupId = groupId,
                initiatorUserId = userId,
                initiatorName = "", // Will be filled from user data
                message = message,
                location = location,
                plannedDuration = plannedDuration,
                expiresAt = expirationTime,
                createdAt = Date(),
                status = BreakInvitationStatus.PENDING
            )
            
            // Save to Firestore
            invitationsCollection.document(invitation.invitationId).set(invitation).await()
            
            // Save to local database
            breakInvitationDao.insertInvitation(invitation)
            
            Resource.Success(invitation)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create break invitation")
        }
    }
    
    suspend fun respondToInvitation(
        invitationId: String,
        response: ResponseType,
        reason: String = "",
        customMessage: String = ""
    ): Resource<Unit> {
        return try {
            val userId = userRepository.getCurrentUserId()
            val invitation = breakInvitationDao.getInvitationById(invitationId)
            
            // Create response
            val breakResponse = BreakResponse(
                userId = userId,
                userName = "", // Will be filled from user data
                response = response,
                reason = reason,
                customMessage = customMessage,
                respondedAt = Date(),
                responseTime = System.currentTimeMillis() // Calculate actual response time
            )
            
            // Update invitation with response
            // This would typically involve updating the responses list in the invitation
            // Implementation depends on how you want to structure the responses
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to respond to invitation")
        }
    }
    
    suspend fun cancelInvitation(invitationId: String): Resource<Unit> {
        return try {
            // Update status in Firestore
            invitationsCollection.document(invitationId)
                .update("status", BreakInvitationStatus.CANCELLED)
                .await()
            
            // Update in local database
            breakInvitationDao.updateInvitationStatus(invitationId, BreakInvitationStatus.CANCELLED)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel invitation")
        }
    }
    
    fun getActiveInvitations(): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getActiveInvitations(Date())
    }
    
    fun getInvitationsForGroup(groupId: String): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getInvitationsForGroup(groupId)
    }
    
    fun getActiveInvitationsForGroup(groupId: String): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getActiveInvitationsForGroup(groupId, Date())
    }
    
    fun getUserInvitations(userId: String): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getInvitationsCreatedBy(userId)
    }
    
    suspend fun getInvitationById(invitationId: String): Resource<BreakInvitation> {
        return try {
            // Try local first, then Firestore if not found
            val firestoreInvitation = invitationsCollection.document(invitationId).get().await()
            if (firestoreInvitation.exists()) {
                val invitation = firestoreInvitation.toObject(BreakInvitation::class.java)!!
                breakInvitationDao.insertInvitation(invitation)
                Resource.Success(invitation)
            } else {
                Resource.Error("Invitation not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch invitation")
        }
    }
    
    suspend fun expireOldInvitations(): Resource<Unit> {
        return try {
            val currentTime = Date()
            
            // Update expired invitations in Firestore
            val expiredInvitations = invitationsCollection
                .whereEqualTo("status", BreakInvitationStatus.PENDING.name)
                .whereLessThan("expiresAt", currentTime)
                .get()
                .await()
            
            for (document in expiredInvitations.documents) {
                document.reference.update("status", BreakInvitationStatus.EXPIRED.name)
            }
            
            // Update in local database
            breakInvitationDao.expireOldInvitations(currentTime)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to expire old invitations")
        }
    }
    
    suspend fun getTodayInvitationCount(userId: String): Int {
        return breakInvitationDao.getTodayInvitationCountForUser(userId)
    }
    
    fun getInvitationsInDateRange(startDate: Date, endDate: Date): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getInvitationsInDateRange(startDate, endDate)
    }
    
    fun getUserInvitationsInDateRange(
        userId: String, 
        startDate: Date, 
        endDate: Date
    ): Flow<List<BreakInvitation>> {
        return breakInvitationDao.getUserInvitationsInDateRange(userId, startDate, endDate)
    }
}
