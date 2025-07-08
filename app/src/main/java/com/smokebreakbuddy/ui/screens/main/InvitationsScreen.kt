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
import com.smokebreakbuddy.data.model.BreakInvitationStatus
import com.smokebreakbuddy.data.model.ResponseType
import com.smokebreakbuddy.ui.theme.*
import com.smokebreakbuddy.ui.viewmodel.BreakInvitationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationsScreen(
    breakInvitationViewModel: BreakInvitationViewModel = hiltViewModel()
) {
    val activeInvitations by breakInvitationViewModel.activeInvitations.collectAsState()
    val userInvitations by breakInvitationViewModel.userInvitations.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Sent", "History")
    
    // Mock data for demonstration
    val sentInvitations = remember {
        listOf(
            BreakInvitation(
                invitationId = "sent1",
                initiatorName = "You",
                message = "Coffee break anyone?",
                location = "Break room",
                status = BreakInvitationStatus.ACTIVE,
                createdAt = Date()
            ),
            BreakInvitation(
                invitationId = "sent2",
                initiatorName = "You",
                message = "Quick smoke break",
                location = "Smoking area",
                status = BreakInvitationStatus.COMPLETED,
                createdAt = Calendar.getInstance().apply { add(Calendar.HOUR, -2) }.time
            )
        )
    }
    
    val historyInvitations = remember {
        listOf(
            BreakInvitation(
                invitationId = "hist1",
                initiatorName = "John Doe",
                message = "Let's grab some fresh air",
                location = "Terrace",
                status = BreakInvitationStatus.COMPLETED,
                createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
            ),
            BreakInvitation(
                invitationId = "hist2",
                initiatorName = "Sarah Smith",
                message = "Coffee and chat?",
                location = "Cafeteria",
                status = BreakInvitationStatus.CANCELLED,
                createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }.time
            ),
            BreakInvitation(
                invitationId = "hist3",
                initiatorName = "Mike Johnson",
                message = "",
                location = "Smoking area",
                status = BreakInvitationStatus.EXPIRED,
                createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.time
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Break Invitations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { breakInvitationViewModel.refreshInvitations() }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Row
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title)
                            when (index) {
                                0 -> if (activeInvitations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge {
                                        Text(
                                            text = activeInvitations.size.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                1 -> if (sentInvitations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge {
                                        Text(
                                            text = sentInvitations.size.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        when (selectedTab) {
            0 -> ActiveInvitationsTab(
                invitations = activeInvitations,
                onAccept = { id -> breakInvitationViewModel.acceptInvitation(id) },
                onDecline = { id -> breakInvitationViewModel.declineInvitation(id) },
                onMaybe = { id -> breakInvitationViewModel.maybeInvitation(id) }
            )
            1 -> SentInvitationsTab(
                invitations = sentInvitations,
                onCancel = { id -> breakInvitationViewModel.cancelInvitation(id) }
            )
            2 -> HistoryInvitationsTab(invitations = historyInvitations)
        }
    }
}

@Composable
fun ActiveInvitationsTab(
    invitations: List<BreakInvitation>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onMaybe: (String) -> Unit
) {
    if (invitations.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.NotificationsNone,
            title = "No active invitations",
            description = "You're all caught up! Active break invitations will appear here."
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invitations) { invitation ->
                ActiveInvitationCard(
                    invitation = invitation,
                    onAccept = { onAccept(invitation.invitationId) },
                    onDecline = { onDecline(invitation.invitationId) },
                    onMaybe = { onMaybe(invitation.invitationId) }
                )
            }
        }
    }
}

@Composable
fun SentInvitationsTab(
    invitations: List<BreakInvitation>,
    onCancel: (String) -> Unit
) {
    if (invitations.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.Send,
            title = "No sent invitations",
            description = "Invitations you send to groups will appear here."
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invitations) { invitation ->
                SentInvitationCard(
                    invitation = invitation,
                    onCancel = { onCancel(invitation.invitationId) }
                )
            }
        }
    }
}

@Composable
fun HistoryInvitationsTab(
    invitations: List<BreakInvitation>
) {
    if (invitations.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.History,
            title = "No invitation history",
            description = "Your past invitations and responses will appear here."
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invitations) { invitation ->
                HistoryInvitationCard(invitation = invitation)
            }
        }
    }
}

@Composable
fun ActiveInvitationCard(
    invitation: BreakInvitation,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onMaybe: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = "Active",
                            tint = BreakGreen,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = invitation.initiatorName.ifEmpty { "Someone" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
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
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = invitation.createdAt?.let { timeFormat.format(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${invitation.plannedDuration} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaybeOrange
                    )
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
fun SentInvitationCard(
    invitation: BreakInvitation,
    onCancel: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Sent",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You sent an invitation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
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
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = invitation.createdAt?.let { timeFormat.format(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    StatusChip(status = invitation.status)
                }
            }
            
            // Mock responses for demonstration
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Responses: 2 accepted, 1 maybe, 0 declined",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (invitation.status == BreakInvitationStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Invitation")
                }
            }
        }
    }
}

@Composable
fun HistoryInvitationCard(
    invitation: BreakInvitation
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        )
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
                        text = if (invitation.initiatorName == "You") "You sent an invitation" 
                               else "${invitation.initiatorName} invited you",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (invitation.message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${invitation.message}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                    
                    if (invitation.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = invitation.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = invitation.createdAt?.let { dateFormat.format(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = invitation.createdAt?.let { timeFormat.format(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    StatusChip(status = invitation.status)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: BreakInvitationStatus) {
    val (color, text) = when (status) {
        BreakInvitationStatus.PENDING -> BreakGreen to "Active"
        BreakInvitationStatus.ACTIVE -> BreakGreen to "In Progress"
        BreakInvitationStatus.COMPLETED -> OnlineGreen to "Completed"
        BreakInvitationStatus.CANCELLED -> DeclineRed to "Cancelled"
        BreakInvitationStatus.EXPIRED -> SmokeGray to "Expired"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
