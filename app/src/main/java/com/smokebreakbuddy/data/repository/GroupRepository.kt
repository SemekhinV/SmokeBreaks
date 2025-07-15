package com.smokebreakbuddy.data.repository

import androidx.work.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.smokebreakbuddy.data.local.dao.GroupDao
import com.smokebreakbuddy.data.model.Group
import com.smokebreakbuddy.data.model.GroupWithMembers
import com.smokebreakbuddy.data.model.Member
import com.smokebreakbuddy.data.model.User
import com.smokebreakbuddy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val groupDao: GroupDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val memberRepository: MemberRepository,
    private val messaging: FirebaseMessaging
) {
    private val groupsCollection = firestore.collection("groups")

    fun getCurrentGroupCount(): Flow<Group?> = groupDao.getGroupById(getCurrentUserId())
    private fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""

    fun getUserGroupsWithMembers(userId: String): Flow<List<GroupWithMembers>> {
        // Сначала пытаемся получить из локальной БД (Room)
        // Затем можно синхронизировать с Firestore
        return groupDao.getGroupsWithMembersForUser(userId)
            .onStart { fetchAndStoreUserGroups(userId) }
    }

    suspend fun getGroupsForUserFromFirestore(userId: String): List<Pair<Group, List<Member>>> {
        val groupsWithMembersList = mutableListOf<Pair<Group, List<Member>>>()

        // 1. Получаем ID групп, в которых состоит пользователь
        val memberEntries = firestore.collection("members")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Member::class.java)

        val groupIds = memberEntries.map { it.groupId }.distinct()

        if (groupIds.isEmpty()) {
            return emptyList()
        }

        // 2. Получаем детали этих групп
        // Firestore имеет ограничение на 10 элементов в 'in' запросе (или 30 с OR),
        // поэтому может потребоваться разбить на части, если групп много.
        val groupsChunks = groupIds.chunked(10) // Разбиваем на чанки по 10
        for (chunk in groupsChunks) {
            val groupsSnapshot = firestore.collection("groups")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            for (groupDocument in groupsSnapshot.documents) {
                val group = groupDocument.toObject(Group::class.java)
                if (group != null) {
                    // 3. Для каждой группы получаем всех ее участников
                    val membersSnapshot = firestore.collection("members")
                        .whereEqualTo("groupId", group.groupId)
                        .get()
                        .await()
                    val members = membersSnapshot.toObjects(Member::class.java)
                    groupsWithMembersList.add(Pair(group, members))
                }
            }
        }
        return groupsWithMembersList
    }

    private suspend fun fetchAndStoreUserGroups(userId: String) {
        try {
            val userGroupIdsSnapshot = firestore.collection("members")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val groupIds = userGroupIdsSnapshot.documents.mapNotNull { it.getString("groupId") }

            if (groupIds.isNotEmpty()) {
                val groupsSnapshot = firestore.collection("groups")
                    .whereIn(FieldPath.documentId(), groupIds)
                    .get()
                    .await()

                val groups = groupsSnapshot.toObjects(Group::class.java)

                for (group in groups) {
                    val membersSnapshot = firestore.collection("members")
                        .whereEqualTo("groupId", group.groupId)
                        .get()
                        .await()
                    val members = membersSnapshot.toObjects(Member::class.java)
                    groupDao.upsertGroupWithMembers(group, members) // Сохраняем в Room
                }
            }
        } catch (e: Exception) {
            // Обработка ошибок
        }
    }

    suspend fun createGroup(group: Group): Resource<Group> {
        return try {
            groupDao.insertGroup(group)
            Resource.Success(group)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create group")
        }
    }

    suspend fun updateUser(group: Group): Resource<Group> {
        return try {
            groupDao.updateGroup(group)
            Resource.Success(group)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update group")
        }
    }

    suspend fun deleteGroup(group: Group): Resource<Group> {
        return try {
            groupDao.deleteGroup(group)
            Resource.Success(group)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete group")
        }
    }
}