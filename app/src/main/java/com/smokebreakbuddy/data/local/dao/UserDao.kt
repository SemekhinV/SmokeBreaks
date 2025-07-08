package com.smokebreakbuddy.data.local.dao

import androidx.room.*
import com.smokebreakbuddy.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User?>
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE isOnline = 1")
    fun getOnlineUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE department = :department")
    fun getUsersByDepartment(department: String): Flow<List<User>>
    
    @Query("SELECT DISTINCT department FROM users WHERE department != ''")
    fun getAllDepartments(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET isOnline = :isOnline WHERE userId = :userId")
    suspend fun updateUserOnlineStatus(userId: String, isOnline: Boolean)
    
    @Query("UPDATE users SET fcmToken = :token WHERE userId = :userId")
    suspend fun updateFcmToken(userId: String, token: String)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
