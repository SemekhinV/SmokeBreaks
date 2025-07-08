package com.smokebreakbuddy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smokebreakbuddy.ui.navigation.SmokeBreakBuddyNavigation
import com.smokebreakbuddy.ui.theme.SmokeBreakBuddyTheme
import com.smokebreakbuddy.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Install splash screen
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                authViewModel.isLoading.value
            }
        }
        
        setContent {
            SmokeBreakBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                    val isLoading by authViewModel.isLoading.collectAsState()
                    
                    if (!isLoading) {
                        SmokeBreakBuddyNavigation(isLoggedIn = isLoggedIn)
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Update user online status
        authViewModel.updateOnlineStatus(true)
    }
    
    override fun onPause() {
        super.onPause()
        // Update user online status when app goes to background
        authViewModel.updateOnlineStatus(false)
    }
}
