package com.smokebreakbuddy.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smokebreakbuddy.data.model.BreakInvitation
import com.smokebreakbuddy.ui.theme.BreakGreen
import com.smokebreakbuddy.ui.theme.DeclineRed
import com.smokebreakbuddy.ui.theme.OnlineGreen
import com.smokebreakbuddy.ui.viewmodel.AuthViewModel
import com.smokebreakbuddy.ui.viewmodel.BreakInvitationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    breakInvitationViewModel: BreakInvitationViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val activeInvitations by breakInvitationViewModel.activeInvitations.collectAsState()
    val todayInvitationCount by breakInvitationViewModel.todayInvitationCount.collectAsState()
    
    var showCreateInvitationDialog by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = currentUser?.displayName ?: "User",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = "Online",
                                tint = OnlineGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnlineGreen
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Break invitations today: $todayInvitationCount",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Quick Actions
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Create Break Invitation
                Card(
                    modifier = Modifier.weight(1f),
                    onClick = { showCreateInvitationDialog = true }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create Invitation",
                            modifier = Modifier.size(32.dp),
                            tint = BreakGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start Break",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // View Groups
                Card(
                    modifier = Modifier.weight(1f),
                    onClick = { /* Navigate to groups */ }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "View Groups",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "My Groups",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Active Invitations
        item {
            Text(
                text = "Active Invitations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (activeInvitations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SentimentSatisfied,
                            contentDescription = "No invitations",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No active break invitations",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Create one to get started!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(activeInvitations) { invitation ->
                InvitationCard(
                    invitation = invitation,
                    onAccept = { breakInvitationViewModel.acceptInvitation(invitation.invitationId) },
                    onDecline = { breakInvitationViewModel.declineInvitation(invitation.invitationId) },
                    onMaybe = { breakInvitationViewModel.maybeInvitation(invitation.invitationId) }
                )
            }
        }
    }
    
    // Create Invitation Dialog
    if (showCreateInvitationDialog) {
        CreateInvitationDialog(
            onDismiss = { showCreateInvitationDialog = false },
            onCreateInvitation = { message, location, duration ->
                // You would need to get the selected group ID from somewhere
                breakInvitationViewModel.createBreakInvitation(
                    groupId = "default_group", // This should come from group selection
                    message = message,
                    location = location,
                    plannedDuration = duration
                )
                showCreateInvitationDialog = false
            }
        )
    }
}

@Composable
fun InvitationCard(
    invitation: BreakInvitation,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onMaybe: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invitation.initiatorName.ifEmpty { "Someone" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "invites you for a break",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (invitation.message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${invitation.message}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                    
                    if (invitation.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = invitation.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Text(
                    text = invitation.createdAt?.let { timeFormat.format(it) } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DeclineRed
                    )
                ) {
                    Text("Decline")
                }
                
                OutlinedButton(
                    onClick = onMaybe,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Maybe")
                }
                
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreakGreen
                    )
                ) {
                    Text("Accept")
                }
            }
        }
    }
}

@Composable
fun CreateInvitationDialog(
    onDismiss: () -> Unit,
    onCreateInvitation: (String, String, Int) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("10") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Break Invitation") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message (optional)") },
                    placeholder = { Text("Want to grab some fresh air?") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (optional)") },
                    placeholder = { Text("Smoking area") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateInvitation(
                        message.trim(),
                        location.trim(),
                        duration.toIntOrNull() ?: 10
                    )
                }
            ) {
                Text("Send Invitation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
