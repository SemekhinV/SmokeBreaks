package com.smokebreakbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.smokebreakbuddy.data.model.User
import com.smokebreakbuddy.data.repository.UserRepository
import com.smokebreakbuddy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isLoading.value = true
            val isUserLoggedIn = userRepository.isUserLoggedIn()
            _isLoggedIn.value = isUserLoggedIn
            
            if (isUserLoggedIn) {
                loadCurrentUser()
            }
            _isLoading.value = false
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _isLoggedIn.value = true
                            loadCurrentUser()
                            updateOnlineStatus(true)
                        } else {
                            _authError.value = task.exception?.message ?: "Sign in failed"
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Sign in failed"
                _isLoading.value = false
            }
        }
    }

    fun signUpWithEmail(
        email: String, 
        password: String, 
        displayName: String, 
        department: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Create user profile
                            val user = User(
                                email = email,
                                displayName = displayName,
                                department = department,
                                isOnline = true
                            )
                            createUserProfile(user)
                        } else {
                            _authError.value = task.exception?.message ?: "Sign up failed"
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Sign up failed"
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                    email = firebaseUser.email ?: "",
                                    displayName = firebaseUser.displayName ?: "",
                                    avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                                    isOnline = true
                                )
                                createUserProfile(user)
                            }
                        } else {
                            _authError.value = task.exception?.message ?: "Google sign in failed"
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Google sign in failed"
                _isLoading.value = false
            }
        }
    }

    private fun createUserProfile(user: User) {
        viewModelScope.launch {
            when (val result = userRepository.createUser(user)) {
                is Resource.Success -> {
                    _isLoggedIn.value = true
                    _currentUser.value = result.data
                    updateOnlineStatus(true)
                }
                is Resource.Error -> {
                    _authError.value = result.message
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = userRepository.signOut()) {
                is Resource.Success -> {
                    _isLoggedIn.value = false
                    _currentUser.value = null
                }
                is Resource.Error -> {
                    _authError.value = result.message
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
            _isLoading.value = false
        }
    }

    fun updateOnlineStatus(isOnline: Boolean) {
        viewModelScope.launch {
            if (userRepository.isUserLoggedIn()) {
                userRepository.updateUserOnlineStatus(isOnline)
            }
        }
    }

    fun clearError() {
        _authError.value = null
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            try {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _authError.value = task.exception?.message ?: "Password reset failed"
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Password reset failed"
                _isLoading.value = false
            }
        }
    }
}
