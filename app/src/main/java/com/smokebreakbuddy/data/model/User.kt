package com.smokebreakbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @DocumentId
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val department: String = "",
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val fcmToken: String = "",
    val preferences: UserPreferences = UserPreferences(),
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

data class UserPreferences(
    val enableNotifications: Boolean = true,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true,
    val workingHours: WorkingHours = WorkingHours(),
    val maxBreaksPerDay: Int = 5
)

data class WorkingHours(
    val startTime: String = "09:00", // HH:mm format
    val endTime: String = "17:00",   // HH:mm format
    val workingDays: List<Int> = listOf(1, 2, 3, 4, 5) // Monday to Friday
)
