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
import androidx.navigation.NavController
import com.smokebreakbuddy.data.model.Group
import com.smokebreakbuddy.data.model.GroupWithMembers
import com.smokebreakbuddy.ui.theme.BreakGreen
import com.smokebreakbuddy.ui.viewmodel.AuthViewModel
import com.smokebreakbuddy.ui.viewmodel.GroupViewModel
import com.smokebreakbuddy.utils.IntOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    navController: NavController,
    groupViewModel: GroupViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by groupViewModel.isLoading.collectAsState()
    val groupError by groupViewModel.groupError.collectAsState()
    val userGroup by groupViewModel.userGroups.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showJoinGroupDialog by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Groups",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showJoinGroupDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join")
                    }
                    
                    Button(
                        onClick = { showCreateGroupDialog = true }
                    ) {
                        Icon(
                            Icons.Default.GroupAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create")
                    }
                }
            }
        }
        
        // My Groups Section
        item {
            Text(
                text = "My Groups (${userGroup.size}()})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (userGroup.isEmpty()) {
            item {
                EmptyGroupsCard(
                    title = "No groups yet",
                    description = "Create or join a group to start coordinating breaks with your colleagues",
                    actionText = "Create Group",
                    onAction = { showCreateGroupDialog = true }
                )
            }
        } else {
            items(userGroup) { data ->
                GroupCard(
                    info = data,
                    isOwner = data.group.createdBy == currentUser?.userId,
                    onEdit = { /* TODO: Edit group */ },
                    onLeave = { /* TODO: Leave group */ },
                    onViewMembers = { /* TODO: View members */ }
                )
            }
        }
        
        // Public Groups Section
        item {
            Text(
                text = "Discover Public Groups",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
//        items(publicGroups) { group ->
//            PublicGroupCard(
//                group = group,
//                onJoin = { /* TODO: Join group */ }
//            )
//        }
    }
    
    // Create Group Dialog
    if (showCreateGroupDialog) {
        CreateGroupDialog(
            onDismiss = { showCreateGroupDialog = false },
            onCreateGroup = { name, description, isPublic, maxMembers ->
                // TODO: Create group
                showCreateGroupDialog = false
                groupViewModel.createGroup(name, description, isPublic, maxMembers )
            }
        )
    }
    
    // Join Group Dialog
    if (showJoinGroupDialog) {
        JoinGroupDialog(
            onDismiss = { showJoinGroupDialog = false },
            onJoinGroup = { inviteCode ->
                // TODO: Join group by invite code
                showJoinGroupDialog = false
            }
        )
    }
}

@Composable
fun GroupCard(
    info: GroupWithMembers,
    isOwner: Boolean,
    onEdit: () -> Unit,
    onLeave: () -> Unit,
    onViewMembers: () -> Unit
) {
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
                        Text(
                            text = info.group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isOwner) {
                            Surface(
                                color = BreakGreen,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "OWNER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    if (info.group.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = info.group.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${info.members.size} members",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Icon(
                            if (info.group.isPublic) Icons.Default.Public else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (info.group.isPublic) "Public" else "Private",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { /* TODO: Show group menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Group options")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewMembers,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Members")
                }
                
                if (isOwner) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Manage")
                    }
                } else {
                    OutlinedButton(
                        onClick = onLeave,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Leave")
                    }
                }
            }
        }
    }
}

@Composable
fun PublicGroupCard(
    info: GroupWithMembers,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = info.group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (info.group.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = info.group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${info.members.size} members",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreakGreen
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Join")
                }
            }
        }
    }
}

@Composable
fun EmptyGroupsCard(
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit
) {
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
                Icons.Default.GroupAdd,
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
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreateGroup: (String, String, Boolean, Int) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }
    var maxMembers by remember { mutableIntStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Group") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    placeholder = { Text("My Awesome Group") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Describe your group...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                IntOutlinedTextField(
                    label = "Max Members",
                    initialValue = maxMembers,
                    onValueChange = { maxMembers = it ?: 20 },
                    minValue = 2,
                    maxValue = 350
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isPublic,
                        onCheckedChange = { isPublic = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPublic) "Public group" else "Private group",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = if (isPublic) "Anyone can discover and join this group" 
                           else "People need an invite to join this group",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateGroup(groupName.trim(), description.trim(), isPublic, maxMembers)
                },
                enabled = groupName.isNotBlank()
            ) {
                Text("Create Group")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun JoinGroupDialog(
    onDismiss: () -> Unit,
    onJoinGroup: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Group") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter the invite code provided by the group admin.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it },
                    label = { Text("Invite Code") },
                    placeholder = { Text("ABC123") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onJoinGroup(inviteCode.trim())
                },
                enabled = inviteCode.isNotBlank()
            ) {
                Text("Join Group")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
