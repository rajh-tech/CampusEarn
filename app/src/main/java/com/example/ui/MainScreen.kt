package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ActiveMode
import com.example.data.model.JobCategory
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.CampusEarnTopBar
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.employer.EmployerDashboardScreen
import com.example.ui.screens.jobs.JobDetailsScreen
import com.example.ui.screens.jobs.JobMarketplaceScreen
import com.example.ui.screens.messages.MessagingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.student.StudentDashboardScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.screens.work.WorkManagementScreen
import com.example.ui.theme.*

enum class ScreenTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    EXPLORE("Explore", Icons.Filled.Search, Icons.Outlined.Search),
    WORK("My Work", Icons.Filled.Work, Icons.Outlined.WorkOutline),
    MESSAGES("Messages", Icons.Filled.Chat, Icons.Outlined.ChatBubbleOutline),
    WALLET("Wallet", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
}

@Composable
fun MainScreen(
    repository: CampusEarnRepository = CampusEarnRepository.getInstance()
) {
    val isLoggedIn by repository.isLoggedIn.collectAsState()
    val activeMode by repository.activeMode.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val conversations by repository.conversations.collectAsState()

    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    var selectedJobIdForDetails by remember { mutableStateOf<String?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<JobCategory?>(null) }
    var selectedConversationId by remember { mutableStateOf<String?>(null) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AuthScreen(
            repository = repository,
            onLoginSuccess = {
                currentTab = ScreenTab.HOME
                selectedJobIdForDetails = null
            }
        )
        return
    }

    val unreadNotifs = notifications.count { !it.isRead }
    val unreadMessages = conversations.sumOf { it.unreadCount }

    // Global back handling
    BackHandler(enabled = selectedJobIdForDetails != null || currentTab != ScreenTab.HOME) {
        if (selectedJobIdForDetails != null) {
            selectedJobIdForDetails = null
        } else if (currentTab != ScreenTab.HOME) {
            currentTab = ScreenTab.HOME
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Tablet & Desktop Responsive Layout with NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                if (selectedJobIdForDetails == null) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Box(modifier = Modifier.padding(vertical = 16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "CampusEarn",
                                    tint = DeepBluePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    ) {
                        ScreenTab.values().forEach { tab ->
                            val isSelected = currentTab == tab
                            val badgeCount = when (tab) {
                                ScreenTab.MESSAGES -> unreadMessages
                                else -> 0
                            }

                            NavigationRailItem(
                                selected = isSelected,
                                onClick = {
                                    currentTab = tab
                                    selectedJobIdForDetails = null
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (badgeCount > 0) {
                                                Badge(containerColor = PurpleAccent) {
                                                    Text(badgeCount.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                },
                                label = { Text(tab.title) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = DeepBluePrimary,
                                    indicatorColor = Color(0xFFDBEAFE)
                                )
                            )
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        if (selectedJobIdForDetails == null) {
                            CampusEarnTopBar(
                                activeMode = activeMode,
                                unreadNotificationsCount = unreadNotifs,
                                onModeChange = { newMode -> repository.switchActiveMode(newMode) },
                                onNotificationsClick = {
                                    showNotificationsDialog = true
                                    repository.markNotificationsAsRead()
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(modifier = Modifier.fillMaxSize().widthIn(max = 1000.dp)) {
                            MainTabContent(
                                currentTab = currentTab,
                                activeMode = activeMode,
                                selectedJobIdForDetails = selectedJobIdForDetails,
                                selectedCategoryFilter = selectedCategoryFilter,
                                selectedConversationId = selectedConversationId,
                                repository = repository,
                                onSelectJobDetails = { selectedJobIdForDetails = it },
                                onBackFromDetails = { selectedJobIdForDetails = null },
                                onNavigateToCategory = {
                                    selectedCategoryFilter = it
                                    currentTab = ScreenTab.EXPLORE
                                },
                                onNavigateToTab = { currentTab = it },
                                onOpenChat = { convId ->
                                    selectedConversationId = convId
                                    currentTab = ScreenTab.MESSAGES
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Compact Layout with Bottom NavigationBar
            Scaffold(
                topBar = {
                    if (selectedJobIdForDetails == null) {
                        CampusEarnTopBar(
                            activeMode = activeMode,
                            unreadNotificationsCount = unreadNotifs,
                            onModeChange = { newMode -> repository.switchActiveMode(newMode) },
                            onNotificationsClick = {
                                showNotificationsDialog = true
                                repository.markNotificationsAsRead()
                            }
                        )
                    }
                },
                bottomBar = {
                    if (selectedJobIdForDetails == null) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            windowInsets = NavigationBarDefaults.windowInsets
                        ) {
                            ScreenTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                val badgeCount = when (tab) {
                                    ScreenTab.MESSAGES -> unreadMessages
                                    else -> 0
                                }

                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        currentTab = tab
                                        selectedJobIdForDetails = null
                                    },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                if (badgeCount > 0) {
                                                    Badge(containerColor = PurpleAccent) {
                                                        Text(badgeCount.toString())
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                                contentDescription = tab.title
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = when (activeMode) {
                                            ActiveMode.STUDENT -> DeepBluePrimary
                                            ActiveMode.EMPLOYER -> PurpleAccent
                                            ActiveMode.ADMIN -> AmberPending
                                        },
                                        selectedTextColor = when (activeMode) {
                                            ActiveMode.STUDENT -> DeepBluePrimary
                                            ActiveMode.EMPLOYER -> PurpleAccent
                                            ActiveMode.ADMIN -> AmberPending
                                        },
                                        indicatorColor = when (activeMode) {
                                            ActiveMode.STUDENT -> Color(0xFFDBEAFE)
                                            ActiveMode.EMPLOYER -> PurpleLight
                                            ActiveMode.ADMIN -> Color(0xFFFEF3C7)
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MainTabContent(
                        currentTab = currentTab,
                        activeMode = activeMode,
                        selectedJobIdForDetails = selectedJobIdForDetails,
                        selectedCategoryFilter = selectedCategoryFilter,
                        selectedConversationId = selectedConversationId,
                        repository = repository,
                        onSelectJobDetails = { selectedJobIdForDetails = it },
                        onBackFromDetails = { selectedJobIdForDetails = null },
                        onNavigateToCategory = {
                            selectedCategoryFilter = it
                            currentTab = ScreenTab.EXPLORE
                        },
                        onNavigateToTab = { currentTab = it },
                        onOpenChat = { convId ->
                            selectedConversationId = convId
                            currentTab = ScreenTab.MESSAGES
                        }
                    )
                }
            }
        }
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = DeepBluePrimary)
                    Text("Notifications", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                if (notifications.isEmpty()) {
                    Text("No notifications yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                        )
                                        Text(
                                            text = notif.timestamp,
                                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun MainTabContent(
    currentTab: ScreenTab,
    activeMode: ActiveMode,
    selectedJobIdForDetails: String?,
    selectedCategoryFilter: JobCategory?,
    selectedConversationId: String?,
    repository: CampusEarnRepository,
    onSelectJobDetails: (String) -> Unit,
    onBackFromDetails: () -> Unit,
    onNavigateToCategory: (JobCategory) -> Unit,
    onNavigateToTab: (ScreenTab) -> Unit,
    onOpenChat: (String) -> Unit
) {
    if (selectedJobIdForDetails != null) {
        JobDetailsScreen(
            jobId = selectedJobIdForDetails,
            repository = repository,
            onBack = onBackFromDetails,
            onNavigateToChat = onOpenChat
        )
    } else {
        AnimatedContent(
            targetState = Pair(currentTab, activeMode),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { (tab, mode) ->
            when (tab) {
                ScreenTab.HOME -> {
                    when (mode) {
                        ActiveMode.STUDENT -> {
                            StudentDashboardScreen(
                                repository = repository,
                                onNavigateToJobDetails = onSelectJobDetails,
                                onNavigateToCategory = onNavigateToCategory,
                                onNavigateToApplications = { onNavigateToTab(ScreenTab.WORK) },
                                onNavigateToWallet = { onNavigateToTab(ScreenTab.WALLET) },
                                onNavigateToProfile = { onNavigateToTab(ScreenTab.PROFILE) }
                            )
                        }
                        ActiveMode.EMPLOYER -> {
                            EmployerDashboardScreen(
                                repository = repository,
                                onNavigateToChat = onOpenChat
                            )
                        }
                        ActiveMode.ADMIN -> {
                            AdminDashboardScreen(repository = repository)
                        }
                    }
                }

                ScreenTab.EXPLORE -> {
                    JobMarketplaceScreen(
                        repository = repository,
                        selectedCategoryFilter = selectedCategoryFilter,
                        onNavigateToJobDetails = onSelectJobDetails
                    )
                }

                ScreenTab.WORK -> {
                    WorkManagementScreen(
                        repository = repository,
                        onNavigateToChat = onOpenChat
                    )
                }

                ScreenTab.MESSAGES -> {
                    MessagingScreen(
                        repository = repository,
                        selectedConversationId = selectedConversationId,
                        onBackToList = { }
                    )
                }

                ScreenTab.WALLET -> {
                    WalletScreen(repository = repository)
                }

                ScreenTab.PROFILE -> {
                    ProfileScreen(
                        repository = repository,
                        onModeChange = { newMode -> repository.switchActiveMode(newMode) }
                    )
                }
            }
        }
    }
}
