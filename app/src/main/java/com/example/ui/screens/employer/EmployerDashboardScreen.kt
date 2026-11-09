package com.example.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.CategoryIcon
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerDashboardScreen(
    repository: CampusEarnRepository,
    onNavigateToChat: (String) -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val allJobs by repository.jobs.collectAsState()
    val applications by repository.applications.collectAsState()
    val workSubmissions by repository.workSubmissions.collectAsState()

    // Filter jobs owned by employer or editable
    val myPostedJobs = allJobs // In prototype, employer can manage posted jobs or newly created jobs
    val activeJobsCount = myPostedJobs.count { it.status == JobStatus.ACTIVE }
    val totalApplicants = applications.size
    val hiredCount = applications.count { it.status == ApplicationStatus.ACCEPTED }

    var showPostJobSheet by remember { mutableStateOf(false) }
    var selectedJobForApplicants by remember { mutableStateOf<JobPost?>(null) }
    var safetyError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Employer Welcome & Action Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepBluePrimary)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Employer Portal",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF93C5FD), fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Hire Top College Talent",
                                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Surface(
                                color = PurpleAccent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Verified Partner",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Post online tasks or offline campus gigs. Verified Indian college students apply with portfolios.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0))
                        )

                        Button(
                            onClick = { showPostJobSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DeepBluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Post a New Student Gig",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = DeepBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Active Listings", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text("$activeJobsCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Applicants", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text("$totalApplicants", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PurpleAccent))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Hired Students", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                            Text("$hiredCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldEarn))
                        }
                    }
                }
            }

            // Section: My Posted Jobs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Manage Job Postings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                    )
                    Text(
                        text = "${myPostedJobs.size} listings",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                    )
                }
            }

            items(myPostedJobs) { job ->
                val jobApps = applications.filter { it.jobId == job.id }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (job.status == JobStatus.ACTIVE) Color(0xFFD1FAE5) else Slate100,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = job.status.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (job.status == JobStatus.ACTIVE) EmeraldEarn else Slate500,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "₹${job.paymentAmount.toInt()} Budget",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldEarn
                                )
                            )
                        }

                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )

                        Text(
                            text = "${job.category.title} • ${job.workType.label} • ${job.duration}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )

                        Divider(color = Slate100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { selectedJobForApplicants = job }
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp), tint = DeepBluePrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "View Applicants (${jobApps.size})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary)
                                )
                            }

                            if (job.status == JobStatus.ACTIVE) {
                                TextButton(
                                    onClick = { repository.closeJob(job.id) }
                                ) {
                                    Text(
                                        text = "Close Listing",
                                        style = MaterialTheme.typography.labelMedium.copy(color = RoseReject)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet: Post a New Job Form
    if (showPostJobSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPostJobSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            var title by remember { mutableStateOf("") }
            var category by remember { mutableStateOf(JobCategory.GRAPHIC_DESIGNING) }
            var description by remember { mutableStateOf("") }
            var skillsInput by remember { mutableStateOf("Figma, Canva") }
            var workType by remember { mutableStateOf(WorkType.ONLINE) }
            var location by remember { mutableStateOf("Pan-India (Remote)") }
            var payAmount by remember { mutableStateOf("3000") }
            var payType by remember { mutableStateOf(PaymentType.FIXED) }
            var duration by remember { mutableStateOf("1 Week") }
            var openings by remember { mutableStateOf("1") }
            var deadline by remember { mutableStateOf("Nov 25, 2026") }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Create Student Gig Posting",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                    )
                    Text(
                        text = "Students will apply directly with college info and verified credentials.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Job Title") },
                        placeholder = { Text("e.g. Campus Ambassador / Python Tutor / UI Designer") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("Category (10 Predefined)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                    var expandedCat by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat }
                    ) {
                        OutlinedTextField(
                            value = category.title,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            JobCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.title) },
                                    onClick = {
                                        category = cat
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Job Description & Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = workType == WorkType.ONLINE,
                            onClick = {
                                workType = WorkType.ONLINE
                                location = "Pan-India (Remote)"
                            },
                            label = { Text("🌐 Online Remote") }
                        )
                        FilterChip(
                            selected = workType == WorkType.OFFLINE,
                            onClick = {
                                workType = WorkType.OFFLINE
                                location = "Delhi NCR (Campus/Mall)"
                            },
                            label = { Text("📍 Offline Campus") }
                        )
                    }
                }

                if (workType == WorkType.OFFLINE) {
                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("City & Venue / Campus") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = payAmount,
                            onValueChange = { payAmount = it },
                            label = { Text("Payment in ₹ (INR)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = skillsInput,
                        onValueChange = { skillsInput = it },
                        label = { Text("Required Skills (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Button(
                        onClick = {
                            val violation = repository.validateJobSafety(title, description)
                            if (violation != null) {
                                safetyError = violation
                                return@Button
                            }

                            val amount = payAmount.toDoubleOrNull() ?: 2500.0
                            val skillsList = skillsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            repository.postJob(
                                title = if (title.isBlank()) "College Project Assistant" else title,
                                category = category,
                                description = if (description.isBlank()) "Looking for dedicated college students to assist with task completion and deliverables." else description,
                                responsibilities = listOf(
                                    "Complete task according to project specs",
                                    "Submit progress milestone updates",
                                    "Coordinate with team via CampusEarn chat"
                                ),
                                requiredSkills = if (skillsList.isEmpty()) listOf("Communication", "Organization") else skillsList,
                                workType = workType,
                                location = location,
                                paymentAmount = amount,
                                paymentType = payType,
                                duration = duration,
                                openings = openings.toIntOrNull() ?: 1,
                                deadline = deadline
                            )
                            showPostJobSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Publish Student Gig", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Modal BottomSheet: View Applicants for Selected Job
    if (selectedJobForApplicants != null) {
        val targetJob = selectedJobForApplicants!!
        val targetApplicants = applications.filter { it.jobId == targetJob.id }

        ModalBottomSheet(
            onDismissRequest = { selectedJobForApplicants = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Applicants for Gig",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = targetJob.title,
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    IconButton(onClick = { selectedJobForApplicants = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                if (targetApplicants.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.PersonSearch,
                        title = "No Applicants Yet",
                        description = "Your gig is active. College students will apply soon!"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(targetApplicants) { app ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = app.studentName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                            )
                                            Text(
                                                text = "${app.studentCollege} • ${app.studentCourse}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                                            )
                                        }
                                        StatusBadge(status = app.status.label)
                                    }

                                    Text(
                                        text = "\"${app.introduction}\"",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                                    )

                                    if (app.portfolioUrl.isNotBlank()) {
                                        Text(
                                            text = "Portfolio: ${app.portfolioUrl}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = DeepBlueSecondary, fontWeight = FontWeight.Medium)
                                        )
                                    }

                                    // Action buttons for applicant
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (app.status != ApplicationStatus.ACCEPTED) {
                                            Button(
                                                onClick = {
                                                    repository.updateApplicationStatus(app.id, ApplicationStatus.ACCEPTED)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Accept & Hire")
                                            }
                                        }

                                        if (app.status == ApplicationStatus.PENDING) {
                                            OutlinedButton(
                                                onClick = {
                                                    repository.updateApplicationStatus(app.id, ApplicationStatus.SHORTLISTED)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Shortlist")
                                            }
                                        }

                                        if (app.status == ApplicationStatus.PENDING || app.status == ApplicationStatus.SHORTLISTED) {
                                            OutlinedButton(
                                                onClick = {
                                                    repository.updateApplicationStatus(app.id, ApplicationStatus.REJECTED)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject", color = Color(0xFFDC2626))
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                val convId = repository.startConversationForJob(targetJob.id)
                                                if (convId.isNotBlank()) {
                                                    selectedJobForApplicants = null
                                                    onNavigateToChat(convId)
                                                }
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
        }
    }

    // Anti-Scam Safety Warning Dialog
    if (safetyError != null) {
        AlertDialog(
            onDismissRequest = { safetyError = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = AmberPending) },
            title = { Text("Job Listing Safety Warning") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CampusEarn Zero-Fee & Academic Integrity Policy",
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = safetyError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB91C1C)
                    )
                    Text(
                        text = "Please revise your gig description to comply with student protection rules before publishing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { safetyError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("I Understand, Revise Listing")
                }
            }
        )
    }
}
