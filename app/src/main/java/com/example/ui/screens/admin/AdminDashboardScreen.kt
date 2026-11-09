package com.example.ui.screens.admin

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
import com.example.data.model.JobStatus
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    repository: CampusEarnRepository
) {
    val jobs by repository.jobs.collectAsState()
    val applications by repository.applications.collectAsState()
    val payments by repository.payments.collectAsState()
    val reports by repository.reports.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Overview & Stats, 1: Safety & Reports, 2: All Jobs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Admin Top Bar
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Admin Moderation Panel",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "CampusEarn Trust & Safety Operations",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                    Surface(
                        color = AmberPending,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "SUPER ADMIN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedAdminTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    Tab(
                        selected = selectedAdminTab == 0,
                        onClick = { selectedAdminTab = 0 },
                        text = { Text("Platform KPIs", color = Color.White) }
                    )
                    Tab(
                        selected = selectedAdminTab == 1,
                        onClick = { selectedAdminTab = 1 },
                        text = { Text("Reports (${reports.size})", color = Color.White) }
                    )
                    Tab(
                        selected = selectedAdminTab == 2,
                        onClick = { selectedAdminTab = 2 },
                        text = { Text("Jobs (${jobs.size})", color = Color.White) }
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedAdminTab == 0) {
                // Platform Metrics Grid
                item {
                    Text("India-Wide Student Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Registered Students", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                Text("14,820", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary))
                                Text("Across 240+ Colleges", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Verified Employers", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                Text("1,240", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PurpleAccent))
                                Text("Startups & Agencies", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Total ₹ Disbursed", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                Text("₹52,80,000", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldEarn))
                                Text("Zero Payment Fraud", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Active Live Gigs", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                Text("${jobs.size}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900))
                                Text("Pan-India & Offline", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            }
                        }
                    }
                }

                // Fraud Prevention Rules Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = AmberPending)
                                Text(
                                    text = "Automated Scam Protection Active",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                )
                            }
                            Text(
                                text = "Keyword scanning automatically intercepts suspicious requests for security deposits, registration fees, or off-platform WhatsApp contact.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F))
                            )
                        }
                    }
                }
            } else if (selectedAdminTab == 1) {
                // Safety & Reports Queue
                if (reports.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = Icons.Default.CheckCircle,
                            title = "Moderation Queue Clean",
                            description = "No pending fraud reports or user complaints."
                        )
                    }
                } else {
                    items(reports) { rep ->
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
                                    Surface(color = RoseLight, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = rep.reason,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RoseReject),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(rep.createdAt, style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                }

                                Text(
                                    text = "Reported Listing: ${rep.jobTitle}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                                )
                                Text(
                                    text = "Details: ${rep.description}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                                )
                                Text(
                                    text = "Reported by: ${rep.reporterName} • Status: ${rep.status}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            repository.moderateReport(rep.id, "REMOVED_AND_BANNED")
                                            if (rep.jobId.isNotBlank()) repository.deleteJob(rep.jobId)
                                            if (rep.reportedUserId.isNotBlank()) repository.blockUser(rep.reportedUserId)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoseReject),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Remove & Ban")
                                    }

                                    OutlinedButton(
                                        onClick = { repository.moderateReport(rep.id, "DISMISSED") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Dismiss")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // All Jobs Moderation
                items(jobs) { job ->
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
                                Text(job.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900), modifier = Modifier.weight(1f))
                                Text("₹${job.paymentAmount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldEarn))
                            }
                            Text("Employer: ${job.employerName} • ${job.category.title} • ${job.workType.label}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { repository.deleteJob(job.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = RoseReject, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete Listing", color = RoseReject)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
