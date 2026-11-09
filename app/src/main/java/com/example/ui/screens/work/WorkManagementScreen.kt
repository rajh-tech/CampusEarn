package com.example.ui.screens.work

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkManagementScreen(
    repository: CampusEarnRepository,
    onNavigateToChat: (String) -> Unit
) {
    val activeMode by repository.activeMode.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val allJobs by repository.jobs.collectAsState()
    val applications by repository.applications.collectAsState()
    val workSubmissions by repository.workSubmissions.collectAsState()

    // Student applications
    val myAllApps = applications.filter { it.studentId == currentUser.id }
    val acceptedApps = myAllApps.filter { it.status == ApplicationStatus.ACCEPTED }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSubmitDialogForApp by remember { mutableStateOf<JobApplication?>(null) }
    var showReviewDialogForSub by remember { mutableStateOf<WorkSubmission?>(null) }
    var appToWithdraw by remember { mutableStateOf<JobApplication?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Tab Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (activeMode == ActiveMode.STUDENT) "My Applications & Work" else "Student Deliverables Review",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )
                Text(
                    text = if (activeMode == ActiveMode.STUDENT)
                        "Track submitted applications, manage accepted college gigs, and deliver work"
                    else
                        "Review student deliverables, request revisions, and approve payments",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (activeMode == ActiveMode.STUDENT) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = DeepBluePrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Applications (${myAllApps.size})", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Hired Gigs (${acceptedApps.size})", style = MaterialTheme.typography.labelSmall) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Delivered (${workSubmissions.size})", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                } else {
                    TabRow(
                        selectedTabIndex = selectedTab.coerceAtMost(1),
                        containerColor = Color.Transparent,
                        contentColor = PurpleAccent
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("All Submissions (${workSubmissions.size})") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Needs Review (${workSubmissions.count { it.status == WorkSubmissionStatus.PENDING_REVIEW }})") }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (activeMode == ActiveMode.STUDENT) {
                when (selectedTab) {
                    0 -> {
                        // My Applications Tab
                        if (myAllApps.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.AssignmentLate,
                                    title = "No Applications Sent",
                                    description = "Browse the Marketplace to apply to student gigs!"
                                )
                            }
                        } else {
                            items(myAllApps) { app ->
                                val matchingJob = allJobs.firstOrNull { it.id == app.jobId }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = app.jobTitle,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900),
                                                modifier = Modifier.weight(1f)
                                            )
                                            StatusBadge(status = app.status.label)
                                        }

                                        if (matchingJob != null) {
                                            Text(
                                                text = "Employer: ${matchingJob.employerName} • Budget: ₹${matchingJob.paymentAmount.toInt()}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                                            )
                                        }

                                        Text(
                                            text = "Proposal: \"${app.introduction.take(120)}...\"",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                        )

                                        Divider(color = Slate100)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Applied: ${app.appliedDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                if (app.status == ApplicationStatus.PENDING || app.status == ApplicationStatus.SHORTLISTED) {
                                                    OutlinedButton(
                                                        onClick = { appToWithdraw = app },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseReject),
                                                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(RoseReject))
                                                    ) {
                                                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Withdraw", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                if (app.status == ApplicationStatus.ACCEPTED) {
                                                    Button(
                                                        onClick = { selectedTab = 1 },
                                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("View Gig Work", fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Hired / Active Gigs Tab
                        if (acceptedApps.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.Task,
                                    title = "No Hired Gigs Yet",
                                    description = "Apply to high-paying gigs in the Marketplace to get hired!"
                                )
                            }
                        } else {
                            items(acceptedApps) { app ->
                                val matchingJob = allJobs.firstOrNull { it.id == app.jobId }
                                val existingSubmission = workSubmissions.firstOrNull { it.applicationId == app.id }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(color = EmeraldLight, shape = RoundedCornerShape(8.dp)) {
                                                Text(
                                                    text = "HIRED / ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldEarn),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Text(
                                                text = "₹${matchingJob?.paymentAmount?.toInt() ?: 3500}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldEarn)
                                            )
                                        }

                                        Text(
                                            text = app.jobTitle,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                        )

                                        if (matchingJob != null) {
                                            Text(
                                                text = "Employer: ${matchingJob.employerName} • Deadline: ${matchingJob.deadline}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                                            )
                                        }

                                        Divider(color = Slate100)

                                        if (existingSubmission != null) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Submission Status:", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                                                StatusBadge(status = existingSubmission.status.label)
                                            }
                                            if (existingSubmission.feedback.isNotBlank()) {
                                                Surface(
                                                    color = Color(0xFFEFF6FF),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = "Employer Note: ${existingSubmission.feedback}",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = DeepBluePrimary),
                                                        modifier = Modifier.padding(10.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = { showSubmitDialogForApp = app },
                                                colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(if (existingSubmission != null) "Re-submit Work" else "Submit Deliverable")
                                            }

                                            IconButton(
                                                onClick = {
                                                    val convId = repository.startConversationForJob(app.jobId)
                                                    if (convId.isNotBlank()) onNavigateToChat(convId)
                                                }
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = DeepBluePrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Delivered Tab
                        if (workSubmissions.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.Task,
                                    title = "No Work Submitted Yet",
                                    description = "Deliverables submitted for review will appear here."
                                )
                            }
                        } else {
                            items(workSubmissions) { sub ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(sub.jobTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                                            StatusBadge(status = sub.status.label)
                                        }
                                        Text("Delivered: ${sub.submittedAt}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                        Text("Link: ${sub.submissionUrl}", style = MaterialTheme.typography.bodySmall.copy(color = DeepBlueSecondary))
                                        Text("Notes: ${sub.notes}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                                        if (sub.feedback.isNotBlank()) {
                                            Text("Feedback: ${sub.feedback}", style = MaterialTheme.typography.bodySmall.copy(color = EmeraldEarn, fontWeight = FontWeight.Bold))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Employer view: review student submissions
                items(workSubmissions) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(sub.jobTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                                    Text("Student: ${sub.studentName}", style = MaterialTheme.typography.bodySmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.SemiBold))
                                }
                                StatusBadge(status = sub.status.label)
                            }

                            Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Deliverable URL / Repo:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                    Text(sub.submissionUrl, style = MaterialTheme.typography.bodySmall.copy(color = DeepBlueSecondary, fontWeight = FontWeight.Bold))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Student Notes:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                    Text(sub.notes, style = MaterialTheme.typography.bodySmall.copy(color = Slate700))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showReviewDialogForSub = sub },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Review & Approve")
                                }

                                IconButton(
                                    onClick = {
                                        val convId = repository.startConversationForJob(sub.jobId)
                                        if (convId.isNotBlank()) onNavigateToChat(convId)
                                    }
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = DeepBluePrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Submit Work Dialog
    if (showSubmitDialogForApp != null) {
        val targetApp = showSubmitDialogForApp!!
        var submissionUrl by remember { mutableStateOf("") }
        var submissionNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSubmitDialogForApp = null },
            title = { Text("Submit Deliverable") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Provide a link to your Google Drive, Figma, GitHub repo, or work file:", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = submissionUrl,
                        onValueChange = { submissionUrl = it },
                        label = { Text("Project / Deliverable URL") },
                        placeholder = { Text("https://drive.google.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = submissionNotes,
                        onValueChange = { submissionNotes = it },
                        label = { Text("Submission Notes & Summary") },
                        placeholder = { Text("Summary of completed deliverables, credentials, or instructions...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val url = if (submissionUrl.isBlank()) "https://campusearn.app/deliverables/demo-preview" else submissionUrl
                        val notes = if (submissionNotes.isBlank()) "Completed deliverables submitted for review." else submissionNotes
                        repository.submitWork(
                            applicationId = targetApp.id,
                            jobId = targetApp.jobId,
                            jobTitle = targetApp.jobTitle,
                            submissionUrl = url,
                            notes = notes
                        )
                        showSubmitDialogForApp = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Submit for Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialogForApp = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Review Work Dialog for Employer
    if (showReviewDialogForSub != null) {
        val sub = showReviewDialogForSub!!
        var feedbackText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReviewDialogForSub = null },
            title = { Text("Review Student Work") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Reviewing submission by ${sub.studentName} for '${sub.jobTitle}'.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Feedback / Rating Comment") },
                        placeholder = { Text("Great job on timely delivery / Please revise banner layout...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.reviewWork(
                            submissionId = sub.id,
                            approved = true,
                            feedback = if (feedbackText.isBlank()) "Work approved! Payment credited to student ledger." else feedbackText
                        )
                        showReviewDialogForSub = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn)
                ) {
                    Text("Approve & Pay")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        repository.reviewWork(
                            submissionId = sub.id,
                            approved = false,
                            feedback = if (feedbackText.isBlank()) "Please make minor revisions according to requirements." else feedbackText
                        )
                        showReviewDialogForSub = null
                    }
                ) {
                    Text("Request Revision")
                }
            }
        )
    }

    // Application Withdrawal Confirmation Dialog from My Work screen
    if (appToWithdraw != null) {
        val target = appToWithdraw!!
        AlertDialog(
            onDismissRequest = { appToWithdraw = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = RoseReject)
                    Text("Withdraw Application?")
                }
            },
            text = {
                Text("Are you sure you want to withdraw your application for '${target.jobTitle}'? You can re-apply anytime from the Marketplace.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.withdrawApplication(target.id)
                        appToWithdraw = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseReject)
                ) {
                    Text("Confirm Withdraw")
                }
            },
            dismissButton = {
                TextButton(onClick = { appToWithdraw = null }) {
                    Text("Keep Application")
                }
            }
        )
    }
}
