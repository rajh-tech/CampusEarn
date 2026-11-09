package com.example.ui.screens.jobs

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.CategoryIcon
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String,
    repository: CampusEarnRepository,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val allJobs by repository.jobs.collectAsState()
    val applications by repository.applications.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    val job = allJobs.firstOrNull { it.id == jobId }
    val existingApplication = applications.firstOrNull { it.jobId == jobId && it.studentId == currentUser.id }
    val isApplicationActive = existingApplication != null && existingApplication.status != ApplicationStatus.WITHDRAWN

    var showApplySheet by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showWithdrawConfirmDialog by remember { mutableStateOf(false) }
    var showSafetyCharterDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    var reportDesc by remember { mutableStateOf("") }

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Job listing not found or has been removed.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gig Overview", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Share Job Button
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out this college student gig on CampusEarn: ${job.title} at ${job.employerName} (Pay: ₹${job.paymentAmount.toInt()} ${if (job.paymentType == PaymentType.HOURLY) "/hr" else ""})! Learn. Work. Earn."
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Student Gig"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Gig", tint = Slate700)
                    }

                    // Report Flag Button
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "Report Gig", tint = Slate500)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Payment Offer",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                        )
                        Text(
                            text = "₹${job.paymentAmount.toInt()} ${if (job.paymentType == PaymentType.HOURLY) "/hr" else "Total"}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldEarn
                            )
                        )
                    }

                    if (isApplicationActive && existingApplication != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Status:", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                StatusBadge(status = existingApplication.status.label)
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                // If hired, show message button
                                if (existingApplication.status == ApplicationStatus.ACCEPTED) {
                                    Button(
                                        onClick = {
                                            val convId = repository.startConversationForJob(job.id)
                                            if (convId.isNotBlank()) onNavigateToChat(convId)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chat with Employer")
                                    }
                                }

                                // If pending or shortlisted, show Withdraw Application button
                                if (existingApplication.status == ApplicationStatus.PENDING || existingApplication.status == ApplicationStatus.SHORTLISTED) {
                                    OutlinedButton(
                                        onClick = { showWithdrawConfirmDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseReject),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(RoseReject))
                                    ) {
                                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Withdraw Application", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    } else {
                        // User has either not applied or previously withdrawn
                        val isReapply = existingApplication?.status == ApplicationStatus.WITHDRAWN
                        Button(
                            onClick = { showApplySheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isReapply) "Re-apply for Gig" else "Apply for Gig",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CategoryIcon(category = job.category, modifier = Modifier.size(16.dp), tint = DeepBluePrimary)
                                    Text(
                                        text = job.category.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepBluePrimary
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Posted ${job.postedDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                            )
                        }

                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900
                            )
                        )

                        // Employer row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = job.employerName.take(1),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBluePrimary
                                    )
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = job.employerName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Slate800
                                        )
                                    )
                                    if (job.isEmployerVerified) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Employer",
                                            tint = DeepBlueSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "★ ${job.employerRating} Employer Rating • ${job.applicantsCount} active applicants",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberPending,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Highlights Grid (Work type, Duration, Location, Openings, Deadline)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Gig Specifications",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Work Format", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(
                                    text = job.workType.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate800)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Duration", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(
                                    text = job.duration,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate800)
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Location", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(
                                    text = job.location,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate800)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Deadline", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(
                                    text = job.deadline,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate800)
                                )
                            }
                        }
                    }
                }
            }

            // Offline Gig Safety Tip Banner
            if (job.workType == WorkType.OFFLINE) {
                item {
                    Card(
                        onClick = { showSafetyCharterDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AmberPending)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Offline Campus Safety Notice (Tap to read)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                )
                                Text(
                                    text = "For offline work, always meet at official campus auditoriums, fest venues, or malls. CampusEarn strictly prohibits upfront deposits.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F))
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AmberPending)
                        }
                    }
                }
            }

            // Full Job Description
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Job Description",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = job.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate700, lineHeight = 22.sp)
                        )

                        if (job.responsibilities.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Key Deliverables & Responsibilities",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                            )
                            job.responsibilities.forEach { resp ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("•", style = MaterialTheme.typography.bodyMedium.copy(color = DeepBluePrimary, fontWeight = FontWeight.Bold))
                                    Text(resp, style = MaterialTheme.typography.bodyMedium.copy(color = Slate700))
                                }
                            }
                        }
                    }
                }
            }

            // Required Skills Tags
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Required Skills",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            job.requiredSkills.forEach { skill ->
                                Surface(
                                    color = Color(0xFFEDE9FE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PurpleAccent,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Apply BottomSheet Modal
    if (showApplySheet) {
        ModalBottomSheet(
            onDismissRequest = { showApplySheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            var introPitch by remember { mutableStateOf("") }
            var portfolioInput by remember { mutableStateOf(currentUser.portfolioUrl) }
            var selectedSkills by remember { mutableStateOf(job.requiredSkills.take(3).toSet()) }
            var isSubmitting by remember { mutableStateOf(false) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Submit Gig Application",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                            )
                            Text(
                                text = "Applying to: ${job.title}",
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.SemiBold)
                            )
                        }
                        IconButton(onClick = { showApplySheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // Verified Student Identity Card
                item {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Verified Student Profile:", style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontWeight = FontWeight.Bold))
                            Text("${currentUser.fullName} • ${currentUser.collegeName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                            Text("Degree: ${currentUser.course} (Graduating ${currentUser.graduationYear}) • Contact: ${currentUser.phone}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                        }
                    }
                }

                // Relevant Skills Selector
                item {
                    Text("Highlight Relevant Skills for this Gig:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        job.requiredSkills.forEach { skill ->
                            val isSelected = selectedSkills.contains(skill)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill
                                },
                                label = { Text(skill) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }
                }

                // Intro Pitch
                item {
                    OutlinedTextField(
                        value = introPitch,
                        onValueChange = { introPitch = it },
                        label = { Text("Proposal Pitch / Relevant Experience") },
                        placeholder = { Text("Briefly describe why you are the best fit for this gig...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Portfolio Link
                item {
                    OutlinedTextField(
                        value = portfolioInput,
                        onValueChange = { portfolioInput = it },
                        label = { Text("Portfolio / GitHub / Google Drive link") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = Slate400) }
                    )
                }

                // Transparent Guarantee Note
                item {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CampusEarn Zero-Fee Guarantee: You will receive ₹${job.paymentAmount.toInt()} in full upon work approval. No cut or registration deposit.",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepBluePrimary),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            isSubmitting = true
                            val intro = if (introPitch.isBlank())
                                "Hello! I am a student at ${currentUser.collegeName} with skills in ${selectedSkills.joinToString(", ")}. Eager to deliver top quality work on schedule."
                            else introPitch

                            repository.applyToJob(
                                jobId = job.id,
                                introduction = intro,
                                skills = selectedSkills.toList(),
                                portfolioUrl = portfolioInput
                            )
                            isSubmitting = false
                            showApplySheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Confirm & Submit Proposal", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Withdraw Application Confirmation Dialog
    if (showWithdrawConfirmDialog && existingApplication != null) {
        AlertDialog(
            onDismissRequest = { showWithdrawConfirmDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RoseReject)
                    Text("Withdraw Application?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to withdraw your application for '${job.title}'? The employer will be notified, and you can re-apply anytime if positions remain open.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Slate700)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.withdrawApplication(existingApplication.id)
                        showWithdrawConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseReject)
                ) {
                    Text("Confirm Withdraw")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawConfirmDialog = false }) {
                    Text("Keep Application")
                }
            }
        )
    }

    // Report Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Job Listing") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Help keep CampusEarn safe for fellow students. Why are you reporting this gig?", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Reason (e.g. Asking upfront fee, Fraud, Unsafe)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reportDesc,
                        onValueChange = { reportDesc = it },
                        label = { Text("Details & Evidence") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.reportJobOrUser(
                            reportedUserId = job.employerId,
                            reportedUserName = job.employerName,
                            jobId = job.id,
                            jobTitle = job.title,
                            reason = if (reportReason.isNotBlank()) reportReason else "Suspicious Listing",
                            description = reportDesc
                        )
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseReject)
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

    // Safety Charter Dialog
    if (showSafetyCharterDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyCharterDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DeepBluePrimary)
                    Text("CampusEarn Safety Guidelines")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Zero Upfront Fee Policy:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("No employer may ever ask you to pay for materials, registration, or software access. Report any such requests immediately.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                    Text("2. Safe Public Venues:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("For offline event assistance and store demonstration gigs, always meet at official campus halls or registered venues.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                    Text("3. Escrow Payment Security:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("All payouts are disbursed in full directly to your verified Indian UPI ID or Bank account upon milestone completion.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSafetyCharterDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Understood")
                }
            }
        )
    }
}
