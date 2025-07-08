package com.smokebreakbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smokebreakbuddy.ui.screens.main.*
import com.smokebreakbuddy.ui.viewmodel.AuthViewModel
import com.smokebreakbuddy.ui.viewmodel.BreakInvitationViewModel

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Groups : BottomNavItem("groups", Icons.Default.Group, "Groups")
    object Invitations : BottomNavItem("invitations", Icons.Default.NotificationsActive, "Breaks")
    object Analytics : BottomNavItem("analytics", Icons.Default.Analytics, "Analytics")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    breakInvitationViewModel: BreakInvitationViewModel = hiltViewModel()
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Groups,
        BottomNavItem.Invitations,
        BottomNavItem.Analytics,
        BottomNavItem.Profile
    )
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val activeInvitations by breakInvitationViewModel.activeInvitations.collectAsState()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Badge(
                                containerColor = if (item == BottomNavItem.Invitations && activeInvitations.isNotEmpty()) 
                                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface
                            ) {
                                Icon(item.icon, contentDescription = item.title)
                                if (item == BottomNavItem.Invitations && activeInvitations.isNotEmpty()) {
                                    Text(
                                        text = activeInvitations.size.toString(),
                                        color = MaterialTheme.colorScheme.onError,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> GroupsScreen()
                2 -> InvitationsScreen()
                3 -> AnalyticsScreen()
                4 -> ProfileScreen()
            }
        }
    }
}
