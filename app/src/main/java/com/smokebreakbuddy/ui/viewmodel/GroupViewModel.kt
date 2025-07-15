package com.smokebreakbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.smokebreakbuddy.data.model.Group
import com.smokebreakbuddy.data.model.GroupWithMembers
import com.smokebreakbuddy.data.repository.GroupRepository
import com.smokebreakbuddy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false) // Изначально false, true при операции
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _groupError = MutableStateFlow<String?>(null)
    val groupError: StateFlow<String?> = _groupError.asStateFlow()

    private val _userGroups = MutableStateFlow<List<GroupWithMembers>>(mutableListOf())
    val userGroups: StateFlow<List<GroupWithMembers>> = _userGroups.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            _groupError.value = null
            val userId = auth.currentUser?.uid ?: ""
            if (userId.isEmpty()) {
                _groupError.value = "User not logged in."
                _isLoading.value = false
                return@launch
            }
            groupRepository.getUserGroupsWithMembers(userId)
                .onStart {
                    _isLoading.value = true
                    _groupError.value = null
                }
                .catch { exception ->
                    _groupError.value = exception.message ?: "Failed to load groups."
                    _userGroups.value = mutableListOf() // Очищаем список при ошибке
                    _isLoading.value = false
                }
                .collect { groups ->
                    _userGroups.value = groups
                    _isLoading.value = false
                }
        }
    }

    fun createGroup(
        name: String,
        description: String,
        isPublic: Boolean,
        maxMembers: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _groupError.value = null
            try {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _groupError.value = "User not authenticated."
                    _isLoading.value = false
                    return@launch
                }
                val newGroup = Group(
                    name = name,
                    description = description,
                    isPublic = isPublic,
                    createdBy = currentUserId,
                    maxMembers = maxMembers,
                )
                when (val result = groupRepository.createGroup(newGroup)) {
                    is Resource.Success -> {}
                    is Resource.Error -> {
                        _groupError.value = result.message ?: "Failed to create group."
                    }
                    is Resource.Loading -> {}
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _groupError.value = e.message ?: "Failed to create group."
                _isLoading.value = false
            }
        }
    }

    fun updateGroup(
        name: String,
        description: String,
        isPublic: Boolean,
        maxMembers: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _groupError.value = null
            try {
                val currentUserId = auth.currentUser?.uid
                if (currentUserId == null) {
                    _groupError.value = "User not authenticated."
                    _isLoading.value = false
                    return@launch
                }
                val newGroup = Group(
                    name = name,
                    description = description,
                    isPublic = isPublic,
                    createdBy = "$currentUserId",
                    maxMembers = maxMembers
                )

                when (val result = groupRepository.createGroup(newGroup)) {
                    is Resource.Success -> {}
                    is Resource.Error -> {
                        _groupError.value = result.message ?: "Failed to create group."
                    }
                    is Resource.Loading -> {}
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _groupError.value = e.message ?: "Failed to create group."
                _isLoading.value = false
            }
        }
    }
}