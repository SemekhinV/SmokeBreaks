package com.smokebreakbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smokebreakbuddy.data.model.BreakInvitation
import com.smokebreakbuddy.data.model.ResponseType
import com.smokebreakbuddy.data.repository.BreakInvitationRepository
import com.smokebreakbuddy.data.repository.UserRepository
import com.smokebreakbuddy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BreakInvitationViewModel @Inject constructor(
    private val breakInvitationRepository: BreakInvitationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _activeInvitations = MutableStateFlow<List<BreakInvitation>>(emptyList())
    val activeInvitations: StateFlow<List<BreakInvitation>> = _activeInvitations.asStateFlow()

    private val _userInvitations = MutableStateFlow<List<BreakInvitation>>(emptyList())
    val userInvitations: StateFlow<List<BreakInvitation>> = _userInvitations.asStateFlow()

    private val _todayInvitationCount = MutableStateFlow(0)
    val todayInvitationCount: StateFlow<Int> = _todayInvitationCount.asStateFlow()

    private val _createInvitationSuccess = MutableStateFlow(false)
    val createInvitationSuccess: StateFlow<Boolean> = _createInvitationSuccess.asStateFlow()

    init {
        loadActiveInvitations()
        loadUserInvitations()
        loadTodayInvitationCount()
    }

    fun createBreakInvitation(
        groupId: String,
        message: String,
        location: String,
        plannedDuration: Int = 10
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _createInvitationSuccess.value = false

            when (val result = breakInvitationRepository.createBreakInvitation(
                groupId = groupId,
                message = message,
                location = location,
                plannedDuration = plannedDuration
            )) {
                is Resource.Success -> {
                    _createInvitationSuccess.value = true
                    loadActiveInvitations()
                    loadUserInvitations()
                    loadTodayInvitationCount()
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Handle loading if needed
                }
            }
            _isLoading.value = false
        }
    }

    fun respondToInvitation(
        invitationId: String,
        response: ResponseType,
        reason: String = "",
        customMessage: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = breakInvitationRepository.respondToInvitation(
                invitationId = invitationId,
                response = response,
                reason = reason,
                customMessage = customMessage
            )) {
                is Resource.Success -> {
                    loadActiveInvitations()
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Handle loading if needed
                }
            }
            _isLoading.value = false
        }
    }

    fun cancelInvitation(invitationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = breakInvitationRepository.cancelInvitation(invitationId)) {
                is Resource.Success -> {
                    loadActiveInvitations()
                    loadUserInvitations()
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {
                    // Handle loading if needed
                }
            }
            _isLoading.value = false
        }
    }

    private fun loadActiveInvitations() {
        viewModelScope.launch {
            breakInvitationRepository.getActiveInvitations().collect { invitations ->
                _activeInvitations.value = invitations
            }
        }
    }

    private fun loadUserInvitations() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            breakInvitationRepository.getUserInvitations(userId).collect { invitations ->
                _userInvitations.value = invitations
            }
        }
    }

    private fun loadTodayInvitationCount() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            val count = breakInvitationRepository.getTodayInvitationCount(userId)
            _todayInvitationCount.value = count
        }
    }

    fun getInvitationsForGroup(groupId: String) {
        viewModelScope.launch {
            breakInvitationRepository.getInvitationsForGroup(groupId).collect { invitations ->
                // You might want to store this in a separate state flow for group-specific invitations
            }
        }
    }

    fun getActiveInvitationsForGroup(groupId: String) {
        viewModelScope.launch {
            breakInvitationRepository.getActiveInvitationsForGroup(groupId).collect { invitations ->
                // Store group-specific active invitations
            }
        }
    }

    fun getInvitationsInDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            breakInvitationRepository.getInvitationsInDateRange(startDate, endDate).collect { invitations ->
                // Handle date range invitations for analytics
            }
        }
    }

    fun getUserInvitationsInDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            breakInvitationRepository.getUserInvitationsInDateRange(userId, startDate, endDate)
                .collect { invitations ->
                    // Handle user's date range invitations for analytics
                }
        }
    }

    fun expireOldInvitations() {
        viewModelScope.launch {
            breakInvitationRepository.expireOldInvitations()
            loadActiveInvitations()
        }
    }

    fun refreshInvitations() {
        loadActiveInvitations()
        loadUserInvitations()
        loadTodayInvitationCount()
    }

    fun clearError() {
        _error.value = null
    }

    fun clearCreateSuccess() {
        _createInvitationSuccess.value = false
    }

    // Quick action functions
    fun acceptInvitation(invitationId: String) {
        respondToInvitation(invitationId, ResponseType.ACCEPTED)
    }

    fun declineInvitation(invitationId: String, reason: String = "Busy") {
        respondToInvitation(invitationId, ResponseType.DECLINED, reason)
    }

    fun maybeInvitation(invitationId: String, message: String = "") {
        respondToInvitation(invitationId, ResponseType.MAYBE, customMessage = message)
    }

    // Analytics helper functions
    fun getWeeklyInvitationStats() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = calendar.time
            
            getUserInvitationsInDateRange(startDate, endDate)
        }
    }

    fun getMonthlyInvitationStats() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endDate = calendar.time
            calendar.add(Calendar.MONTH, -1)
            val startDate = calendar.time
            
            getUserInvitationsInDateRange(startDate, endDate)
        }
    }
}
