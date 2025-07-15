package com.smokebreakbuddy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smokebreakbuddy.data.model.Group
import com.smokebreakbuddy.data.model.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members WHERE userId = :userId")
    fun getUserGroups(userId: String): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(member: Member)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(members: List<Member>)

    @Update
    suspend fun updateMembership(member: Member)

//    @Query("UPDATE members SET memberIds = :memberIds WHERE groupId = :groupId")
//    suspend fun updateMembership(groupId: String, memberIds: List<String>)

//    @Query("UPDATE groups SET isActive = 0 WHERE groupId = :groupId")
//    suspend fun deactivateGroup(groupId: String)

    @Delete
    suspend fun deleteMembership(member: Member)

    @Query("DELETE FROM members WHERE userId = :userId")
    suspend fun deleteMembershipById(userId: String)

    @Query("DELETE FROM members")
    suspend fun deleteAllGroups()
}