package com.smokebreakbuddy.data.local.dao

import androidx.room.*
import com.smokebreakbuddy.data.model.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    
    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    fun getGroupById(groupId: String): Flow<Group?>
    
    @Query("SELECT * FROM groups WHERE isActive = 1")
    fun getAllActiveGroups(): Flow<List<Group>>
    
    @Query("SELECT * FROM groups WHERE isPublic = 1 AND isActive = 1")
    fun getPublicGroups(): Flow<List<Group>>
    
    @Query("SELECT * FROM groups WHERE createdBy = :userId AND isActive = 1")
    fun getGroupsCreatedBy(userId: String): Flow<List<Group>>
    
    @Query("SELECT * FROM groups WHERE memberIds LIKE '%' || :userId || '%' AND isActive = 1")
    fun getGroupsForUser(userId: String): Flow<List<Group>>
    
    @Query("SELECT * FROM groups WHERE adminIds LIKE '%' || :userId || '%' AND isActive = 1")
    fun getGroupsAdminBy(userId: String): Flow<List<Group>>
    
    @Query("SELECT * FROM groups WHERE inviteCode = :inviteCode AND isActive = 1")
    suspend fun getGroupByInviteCode(inviteCode: String): Group?
    
    @Query("SELECT * FROM groups WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1")
    fun searchGroups(searchQuery: String): Flow<List<Group>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<Group>)
    
    @Update
    suspend fun updateGroup(group: Group)
    
    @Query("UPDATE groups SET memberIds = :memberIds WHERE groupId = :groupId")
    suspend fun updateGroupMembers(groupId: String, memberIds: List<String>)
    
    @Query("UPDATE groups SET adminIds = :adminIds WHERE groupId = :groupId")
    suspend fun updateGroupAdmins(groupId: String, adminIds: List<String>)
    
    @Query("UPDATE groups SET isActive = 0 WHERE groupId = :groupId")
    suspend fun deactivateGroup(groupId: String)
    
    @Delete
    suspend fun deleteGroup(group: Group)
    
    @Query("DELETE FROM groups WHERE groupId = :groupId")
    suspend fun deleteGroupById(groupId: String)
    
    @Query("DELETE FROM groups")
    suspend fun deleteAllGroups()
    
    @Query("SELECT COUNT(*) FROM groups WHERE isActive = 1")
    suspend fun getActiveGroupCount(): Int
}
