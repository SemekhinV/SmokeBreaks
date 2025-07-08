package com.smokebreakbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey
    @DocumentId
    val groupId: String = "",
    val name: String = "",
    val description: String = "",
    val isPublic: Boolean = false,
    val createdBy: String = "",
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val inviteCode: String = "",
    val maxMembers: Int = 20,
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

data class GroupMember(
    val userId: String = "",
    val groupId: String = "",
    val role: GroupRole = GroupRole.MEMBER,
    val joinedAt: Date? = null,
    val isActive: Boolean = true
)

enum class GroupRole {
    ADMIN,
    MEMBER
}

data class GroupInvitation(
    val invitationId: String = "",
    val groupId: String = "",
    val invitedByUserId: String = "",
    val invitedUserId: String = "",
    val invitedEmail: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val message: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val respondedAt: Date? = null
)

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED
}
