package com.smokebreakbuddy.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
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
    val inviteCode: String = "",
    val maxMembers: Int = 20,
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
//    val invitation: List<GroupInvitation> = listOf()
)

@Entity(
    tableName = "members",
    primaryKeys = ["userId", "groupId"],
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Member(
    val userId: String = "",
    val groupId: String = "",
    val role: GroupRole = GroupRole.MEMBER,
    @ServerTimestamp val joinedAt: Date = Date(),
    val isActive: Boolean = true
)

data class GroupWithMembers(
    @Embedded
    val group: Group,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "groupId"
    )
    val members: List<Member> // Теперь здесь список сущностей Member
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
