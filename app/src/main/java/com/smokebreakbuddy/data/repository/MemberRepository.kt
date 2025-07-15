package com.smokebreakbuddy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.smokebreakbuddy.data.local.dao.MemberDao
import com.smokebreakbuddy.data.model.Member
import com.smokebreakbuddy.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberDao: MemberDao,
    private val firestore: FirebaseFirestore
) {

    suspend fun createMembership(member: Member): Resource<Member> {
        return try {
            memberDao.insertMembership(member)
            Resource.Success(member)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create membership")
        }
    }

    suspend fun updateMembership(member: Member): Resource<Member> {
        return try {
            memberDao.updateMembership(member)
            Resource.Success(member)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update membership")
        }
    }

    suspend fun deleteMembership(member: Member): Resource<Member> {
        return try {
            memberDao.deleteMembership(member)
            Resource.Success(member)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete membership")
        }
    }
}