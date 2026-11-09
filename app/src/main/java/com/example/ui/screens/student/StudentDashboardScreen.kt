package com.example.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun StudentDashboardScreen(
    repository: CampusEarnRepository,
    onNavigateToJobDetails: (String) -> Unit,
    onNavigateToCategory: (JobCategory) -> Unit,
    onNavigateToApplications: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val allJobs by repository.jobs.collectAsState()
    val applications by repository.applications.collectAsState()
    val payments by repository.payments.collectAsState()

    val myApplications = applications.filter { it.studentId == currentUser.id }
    val appliedCount = myApplications.size
    var showSafetyCharterDialog by remember { mutableStateOf(false) }
    val acceptedCount = myApplications.count { it.status == ApplicationStatus.ACCEPTED }
    val completedCount = currentUser.completedJobsCount

    val totalEarned = payments
        .filter { it.receiverId == currentUser.id && it.status == PaymentStatus.PAID }
        .sumOf { it.amount }

    val completionPercentage = currentUser.calculateCompletionPercentage()
    val recommendedJobs = allJobs.filter { it.status == JobStatus.ACTIVE }.take(4)
    val latestJobs = allJobs.filter { it.status == JobStatus.ACTIVE }.takeLast(4).reversed()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Visual Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "Indian college students collaborating on CampusEarn",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xCC0F172A),
                                    Color(0xF00F172A)
                                )
                            )
                        )
                )

                // Welcome text over hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Namaste, ${currentUser.fullName.split(" ").firstOrNull() ?: "Student"}! 🎓",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${currentUser.collegeName} • ${currentUser.course}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Profile completion gauge card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable { onNavigateToProfile() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { completionPercentage / 100f },
                            modifier = Modifier.size(52.dp),
                            color = PurpleAccent,
                            trackColor = Color(0xFFDDD6FE),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "$completionPercentage%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PurpleAccent
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Student Profile Completion",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                        Text(
                            text = if (completionPercentage < 100)
                                "Complete your portfolio to get 3x more gig offers"
                            else
                                "Your profile is verified and stands out to employers!",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Edit Profile",
                        tint = Slate400
                    )
                }
            }
        }

        // Quick Stats Metric Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wallet Earnings Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToWallet() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFA7F3D0)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Earnings",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = EmeraldEarn,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = EmeraldEarn,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₹${totalEarned.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF065F46)
                            )
                        )
                        Text(
                            text = "$completedCount gigs done",
                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn)
                        )
                    }
                }

                // Applications Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToApplications() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Applications",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = DeepBluePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = DeepBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$appliedCount Applied",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBluePrimary
                            )
                        )
                        Text(
                            text = "$acceptedCount accepted",
                            style = MaterialTheme.typography.labelSmall.copy(color = DeepBlueSecondary)
                        )
                    }
                }
            }
        }

        // Safety Notice
        item {
            SafetyNoticeBanner(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onLearnMoreClick = { showSafetyCharterDialog = true }
            )
        }

        // Popular Job Categories Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Categories",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )
                Text(
                    text = "10 Gig Fields",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }
        }

        // Categories Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(JobCategory.values()) { category ->
                    Card(
                        onClick = { onNavigateToCategory(category) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200)),
                        modifier = Modifier.width(135.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                CategoryIcon(category = category, modifier = Modifier.size(20.dp), tint = DeepBluePrimary)
                            }
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800,
                                    lineHeight = 16.sp
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Recommended Jobs Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = AmberPending,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Recommended for You",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    )
                }
            }
        }

        // Recommended Jobs List
        items(recommendedJobs) { job ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                JobItemCard(job = job, onClick = { onNavigateToJobDetails(job.id) })
            }
        }

        // Latest Campus Gigs Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest Campus Gigs",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                )
            }
        }

        // Latest Jobs List
        items(latestJobs) { job ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                JobItemCard(job = job, onClick = { onNavigateToJobDetails(job.id) })
            }
        }
    }

    if (showSafetyCharterDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyCharterDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DeepBluePrimary)
                    Text("CampusEarn Student Safety Charter")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Zero Upfront Fee Guarantee:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("Students never pay any registration, interview, or training deposit. Any employer requesting payments will be banned immediately.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                    Text("2. Verified Indian Campuses & Venues:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("For offline event assistance, registrations, or tech demonstrations, always meet inside verified college venues or prominent public malls.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                    Text("3. Direct Escrow Protection:", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("Employers deposit gig budgets into CampusEarn escrow before student hiring. Approved work funds release straight to your UPI or Bank A/C.", style = MaterialTheme.typography.bodySmall, color = Slate700)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSafetyCharterDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Got It")
                }
            }
        )
    }
}
