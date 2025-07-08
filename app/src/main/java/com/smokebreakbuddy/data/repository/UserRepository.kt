package com.smokebreakbuddy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smokebreakbuddy.data.local.dao.UserDao
import com.smokebreakbuddy.data.model.User
import com.smokebreakbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) {
    
    private val usersCollection = firestore.collection("users")
    
    fun getCurrentUser(): Flow<User?> = userDao.getUserById(getCurrentUserId())
    
    fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    suspend fun createUser(user: User): Resource<User> {
        return try {
            // Get FCM token
            val fcmToken = messaging.token.await()
            val userWithToken = user.copy(
                userId = getCurrentUserId(),
                fcmToken = fcmToken
            )
            
            // Save to Firestore
            usersCollection.document(userWithToken.userId).set(userWithToken).await()
            
            // Save to local database
            userDao.insertUser(userWithToken)
            
            Resource.Success(userWithToken)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create user")
        }
    }
    
    suspend fun updateUser(user: User): Resource<User> {
        return try {
            // Update in Firestore
            usersCollection.document(user.userId).set(user).await()
            
            // Update in local database
            userDao.updateUser(user)
            
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user")
        }
    }
    
    suspend fun getUserById(userId: String): Resource<User> {
        return try {
            // Try to get from local database first
            val localUser = userDao.getUserById(userId)
            
            // Also fetch from Firestore to ensure data is up-to-date
            val firestoreUser = usersCollection.document(userId).get().await()
            if (firestoreUser.exists()) {
                val user = firestoreUser.toObject(User::class.java)!!
                userDao.insertUser(user)
                Resource.Success(user)
            } else {
                Resource.Error("User not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user")
        }
    }
    
    suspend fun updateUserOnlineStatus(isOnline: Boolean): Resource<Unit> {
        return try {
            val userId = getCurrentUserId()
            
            // Update in Firestore
            usersCollection.document(userId)
                .update("isOnline", isOnline)
                .await()
            
            // Update in local database
            userDao.updateUserOnlineStatus(userId, isOnline)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update online status")
        }
    }
    
    suspend fun updateFcmToken(token: String): Resource<Unit> {
        return try {
            val userId = getCurrentUserId()
            
            // Update in Firestore
            usersCollection.document(userId)
                .update("fcmToken", token)
                .await()
            
            // Update in local database
            userDao.updateFcmToken(userId, token)
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update FCM token")
        }
    }
    
    fun getOnlineUsers(): Flow<List<User>> = userDao.getOnlineUsers()
    
    fun getUsersByDepartment(department: String): Flow<List<User>> = 
        userDao.getUsersByDepartment(department)
    
    fun getAllDepartments(): Flow<List<String>> = userDao.getAllDepartments()
    
    suspend fun signOut(): Resource<Unit> {
        return try {
            // Update online status to false
            updateUserOnlineStatus(false)
            
            // Sign out from Firebase
            auth.signOut()
            
            // Clear local data if needed
            // userDao.deleteAllUsers()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to sign out")
        }
    }
    
    suspend fun deleteAccount(): Resource<Unit> {
        return try {
            val userId = getCurrentUserId()
            
            // Delete from Firestore
            usersCollection.document(userId).delete().await()
            
            // Delete from local database
            userDao.deleteUserById(userId)
            
            // Delete Firebase account
            auth.currentUser?.delete()?.await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete account")
        }
    }
}
