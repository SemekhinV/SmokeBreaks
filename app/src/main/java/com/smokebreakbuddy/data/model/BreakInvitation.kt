package com.smokebreakbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "break_invitations")
data class BreakInvitation(
    @PrimaryKey
    @DocumentId
    val invitationId: String = "",
    val groupId: String = "",
    val initiatorUserId: String = "",
    val initiatorName: String = "",
    val message: String = "",
    val location: String = "",
    val plannedDuration: Int = 10, // in minutes
    val responses: List<BreakResponse> = emptyList(),
    val status: BreakInvitationStatus = BreakInvitationStatus.PENDING,
    val expiresAt: Date? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

data class BreakResponse(
    val userId: String = "",
    val userName: String = "",
    val response: ResponseType = ResponseType.PENDING,
    val reason: String = "",
    val customMessage: String = "",
    @ServerTimestamp
    val respondedAt: Date? = null,
    val responseTime: Long = 0L // Time taken to respond in milliseconds
)

enum class ResponseType {
    PENDING,
    ACCEPTED,
    DECLINED,
    MAYBE
}

enum class BreakInvitationStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    EXPIRED
}

enum class DeclineReason {
    BUSY,
    WILL_GO_LATER,
    NOT_INTERESTED,
    IN_MEETING,
    CUSTOM
}

// For analytics and logging
@Entity(tableName = "break_sessions")
data class BreakSession(
    @PrimaryKey
    val sessionId: String = "",
    val invitationId: String = "",
    val groupId: String = "",
    val participantIds: List<String> = emptyList(),
    val actualDuration: Int = 0, // in minutes
    val startTime: Date? = null,
    val endTime: Date? = null,
    val location: String = "",
    val rating: Int = 0, // 1-5 rating
    val feedback: String = "",
    @ServerTimestamp
    val createdAt: Date? = null
)
