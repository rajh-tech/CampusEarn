package com.example.ui.screens.messages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationItem
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    repository: CampusEarnRepository,
    selectedConversationId: String? = null,
    onBackToList: () -> Unit = {}
) {
    val conversations by repository.conversations.collectAsState()
    val allMessages by repository.messages.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var activeConvId by remember { mutableStateOf(selectedConversationId) }

    LaunchedEffect(selectedConversationId) {
        if (selectedConversationId != null) {
            activeConvId = selectedConversationId
        }
    }

    LaunchedEffect(activeConvId) {
        if (activeConvId != null) {
            repository.markConversationAsRead(activeConvId!!)
        }
    }

    if (activeConvId != null) {
        val conv = conversations.firstOrNull { it.id == activeConvId }
        val msgList = allMessages[activeConvId] ?: emptyList()
        var messageInput by remember { mutableStateOf("") }
        var showChatMenu by remember { mutableStateOf(false) }
        var showReportDialog by remember { mutableStateOf(false) }
        var showBlockConfirmDialog by remember { mutableStateOf(false) }

        BackHandler {
            activeConvId = null
            onBackToList()
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val otherPartyName = if (currentUser.id == conv?.studentId) conv?.employerName ?: "Employer" else conv?.studentName ?: "Student"
                                    Text(
                                        text = otherPartyName.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PurpleAccent)
                                    )
                                }
                                Column {
                                    val otherPartyName = if (currentUser.id == conv?.studentId) conv?.employerName ?: "Employer" else conv?.studentName ?: "Student"
                                    Text(
                                        text = otherPartyName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Direct Messaging • Verified Session",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn, fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                activeConvId = null
                                onBackToList()
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showChatMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showChatMenu,
                                onDismissRequest = { showChatMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Report User / Scam") },
                                    onClick = {
                                        showChatMenu = false
                                        showReportDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = AmberPending) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Block User") },
                                    onClick = {
                                        showChatMenu = false
                                        showBlockConfirmDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626)) }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )

                    // Pinned Gig Context Banner
                    Surface(
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Work, contentDescription = null, tint = DeepBluePrimary, modifier = Modifier.size(15.dp))
                                Text(
                                    text = conv?.jobTitle ?: "Gig Discussion",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary),
                                    maxLines = 1
                                )
                            }
                            Surface(color = EmeraldLight, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "Escrow Active",
                                    style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Quick Suggestions Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickReplies = listOf(
                                "👋 Hello, eager to discuss the deliverables!",
                                "📅 Can we confirm the deadline?",
                                "🔗 Sharing my project drive link",
                                "✅ Deliverable updated and submitted"
                            )
                            quickReplies.forEach { reply ->
                                SuggestionChip(
                                    onClick = {
                                        messageInput = reply
                                    },
                                    label = { Text(reply, style = MaterialTheme.typography.labelSmall) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Color(0xFFF1F5F9),
                                        labelColor = Slate700
                                    )
                                )
                            }
                        }

                        // Message Input Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                placeholder = { Text("Type message regarding this gig...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                singleLine = false,
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepBluePrimary,
                                    unfocusedBorderColor = Slate200,
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                )
                            )

                            IconButton(
                                onClick = {
                                    if (messageInput.isNotBlank()) {
                                        repository.sendMessage(activeConvId!!, messageInput.trim())
                                        messageInput = ""
                                    }
                                },
                                enabled = messageInput.isNotBlank(),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = DeepBluePrimary,
                                    contentColor = Color.White,
                                    disabledContainerColor = Slate200,
                                    disabledContentColor = Slate400
                                )
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            val listState = rememberLazyListState()
            LaunchedEffect(msgList.size) {
                if (msgList.isNotEmpty()) {
                    listState.animateScrollToItem(msgList.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BackgroundLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🔒 CampusEarn Direct Chat. Keep discussions on-platform to protect your payout.",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate500),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                items(msgList) { msg ->
                    val isMe = msg.senderId == currentUser.id
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            color = if (isMe) DeepBluePrimary else Color.White,
                            tonalElevation = 1.dp,
                            border = if (!isMe) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200)) else null,
                            modifier = Modifier.widthIn(max = 290.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (!isMe) {
                                    Text(
                                        text = msg.senderName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PurpleAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = msg.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isMe) Color.White else Slate800,
                                        lineHeight = 20.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.align(Alignment.End),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = msg.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isMe) Color(0xFFBFDBFE) else Slate400,
                                            fontSize = 10.sp
                                        )
                                    )
                                    if (isMe && msg.isRead) {
                                        Text(
                                            text = "• Seen",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF93C5FD),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showReportDialog) {
            var reportReason by remember { mutableStateOf("Suspicious Upfront Fee Request") }
            var reportDetails by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                icon = { Icon(Icons.Default.Flag, contentDescription = null, tint = AmberPending) },
                title = { Text("Report Chat / User") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Reason for report:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        listOf("Suspicious Upfront Fee Request", "Academic Dishonesty / Cheating", "Abusive or Inappropriate Behavior", "Offline Safety Concern").forEach { reason ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { reportReason = reason }
                            ) {
                                RadioButton(selected = reportReason == reason, onClick = { reportReason = reason })
                                Text(reason, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        OutlinedTextField(
                            value = reportDetails,
                            onValueChange = { reportDetails = it },
                            placeholder = { Text("Describe the issue for admin moderation...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val reportedUser = if (currentUser.id == conv?.studentId) conv?.employerName ?: "Employer" else conv?.studentName ?: "Student"
                            val reportedId = if (currentUser.id == conv?.studentId) conv?.employerId ?: "" else conv?.studentId ?: ""
                            repository.reportJobOrUser(
                                reportedUserId = reportedId,
                                reportedUserName = reportedUser,
                                jobId = conv?.jobId ?: "",
                                jobTitle = conv?.jobTitle ?: "",
                                reason = reportReason,
                                description = reportDetails.ifBlank { "Flagged from direct messaging: $reportReason" }
                            )
                            showReportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPending)
                    ) {
                        Text("Submit Report")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showBlockConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showBlockConfirmDialog = false },
                icon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626)) },
                title = { Text("Block this User?") },
                text = {
                    val otherParty = if (currentUser.id == conv?.studentId) conv?.employerName ?: "Employer" else conv?.studentName ?: "Student"
                    Text(
                        "You will no longer receive direct messages or job inquiries from $otherParty.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val targetId = if (currentUser.id == conv?.studentId) conv?.employerId ?: "" else conv?.studentId ?: ""
                            repository.blockUser(targetId)
                            showBlockConfirmDialog = false
                            activeConvId = null
                            onBackToList()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Block User")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBlockConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    } else {
        // Conversation List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Messages & Discussions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                    )
                    Text(
                        text = "Connect directly with verified gig employers and student creators",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }

            if (conversations.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "No Conversations Yet",
                    description = "When you apply to gigs or hire students, chats will appear here."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(conversations) { conv ->
                        Card(
                            onClick = { activeConvId = conv.id },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = conv.employerName.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PurpleAccent)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = conv.employerName,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                        )
                                        Text(
                                            text = conv.lastMessageTime,
                                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                        )
                                    }

                                    Text(
                                        text = "Re: ${conv.jobTitle}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.SemiBold),
                                        maxLines = 1
                                    )

                                    Text(
                                        text = conv.lastMessage,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                                        maxLines = 1
                                    )
                                }

                                if (conv.unreadCount > 0) {
                                    Badge(containerColor = PurpleAccent) {
                                        Text(conv.unreadCount.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
