package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusEarnTopBar(
    activeMode: ActiveMode,
    unreadNotificationsCount: Int,
    onModeChange: (ActiveMode) -> Unit,
    onNotificationsClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DeepBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "CampusEarn Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Campus",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBluePrimary,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text = "Earn",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PurpleAccent,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                    Text(
                        text = "Learn. Work. Earn. (₹)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Actions: Role Switcher Chip & Notification Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Role Toggle Pill
                FilterChip(
                    selected = true,
                    onClick = {
                        val nextMode = when (activeMode) {
                            ActiveMode.STUDENT -> ActiveMode.EMPLOYER
                            ActiveMode.EMPLOYER -> ActiveMode.ADMIN
                            ActiveMode.ADMIN -> ActiveMode.STUDENT
                        }
                        onModeChange(nextMode)
                    },
                    label = {
                        Text(
                            text = when (activeMode) {
                                ActiveMode.STUDENT -> "Student"
                                ActiveMode.EMPLOYER -> "Employer"
                                ActiveMode.ADMIN -> "Admin"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (activeMode) {
                                ActiveMode.STUDENT -> Icons.Default.Person
                                ActiveMode.EMPLOYER -> Icons.Default.Business
                                ActiveMode.ADMIN -> Icons.Default.Shield
                            },
                            contentDescription = "Role Mode",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (activeMode) {
                            ActiveMode.STUDENT -> Color(0xFFDBEAFE)
                            ActiveMode.EMPLOYER -> PurpleLight
                            ActiveMode.ADMIN -> Color(0xFFFEF3C7)
                        },
                        selectedLabelColor = when (activeMode) {
                            ActiveMode.STUDENT -> DeepBluePrimary
                            ActiveMode.EMPLOYER -> PurpleAccent
                            ActiveMode.ADMIN -> Color(0xFFB45309)
                        }
                    )
                )

                // Notification Bell
                IconButton(onClick = onNotificationsClick) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(containerColor = RoseReject) {
                                    Text(unreadNotificationsCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Slate700
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyNoticeBanner(
    modifier: Modifier = Modifier,
    onLearnMoreClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onLearnMoreClick?.invoke() },
        enabled = onLearnMoreClick != null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Safety",
                    tint = DeepBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CampusEarn Zero-Fee Guarantee",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepBluePrimary
                    )
                )
                Text(
                    text = "Legitimate gigs never require registration fees or security deposits. Verified payment ledger in INR ₹.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                )
            }
            if (onLearnMoreClick != null) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Learn More", tint = DeepBluePrimary)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "ACCEPTED", "APPROVED", "PAID" -> EmeraldLight to EmeraldEarn
        "SHORTLISTED", "PROCESSING" -> Color(0xFFEDE9FE) to PurpleAccent
        "PENDING", "PENDING_REVIEW" -> AmberLight to AmberPending
        "REJECTED", "FAILED", "WITHDRAWN" -> RoseLight to RoseReject
        else -> Slate100 to Slate600
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = status.replace("_", " "),
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CategoryIcon(category: JobCategory, modifier: Modifier = Modifier, tint: Color = DeepBluePrimary) {
    val icon = when (category) {
        JobCategory.GRAPHIC_DESIGNING -> Icons.Default.Palette
        JobCategory.CONTENT_WRITING -> Icons.Default.EditNote
        JobCategory.DATA_ENTRY -> Icons.Default.TableChart
        JobCategory.WEB_DEVELOPMENT -> Icons.Default.Code
        JobCategory.VIDEO_EDITING -> Icons.Default.Videocam
        JobCategory.SOCIAL_MEDIA -> Icons.Default.Share
        JobCategory.ONLINE_TUTORING -> Icons.Default.School
        JobCategory.CAMPUS_AMBASSADOR -> Icons.Default.MilitaryTech
        JobCategory.EVENT_MANAGEMENT -> Icons.Default.Celebration
        JobCategory.OFFLINE_JOBS -> Icons.Default.LocationOn
    }
    Icon(imageVector = icon, contentDescription = category.title, tint = tint, modifier = modifier)
}

@Composable
fun JobItemCard(
    job: JobPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CategoryIcon(category = job.category, modifier = Modifier.size(14.dp), tint = DeepBluePrimary)
                        Text(
                            text = job.category.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepBluePrimary
                            )
                        )
                    }
                }

                // Payment Tag in INR ₹
                Surface(
                    color = EmeraldLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "₹${job.paymentAmount.toInt()} ${if (job.paymentType == PaymentType.HOURLY) "/hr" else ""}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldEarn
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Job Title
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    lineHeight = 20.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Employer info & verification
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = job.employerName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                )
                if (job.isEmployerVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Employer",
                        tint = DeepBlueSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text(
                    text = "• ★ ${job.employerRating}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AmberPending,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Metadata Chips (Work Type, Location, Duration)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Online/Offline Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (job.workType == WorkType.ONLINE) Icons.Default.LaptopMac else Icons.Default.Place,
                        contentDescription = null,
                        tint = if (job.workType == WorkType.ONLINE) PurpleAccent else AmberPending,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (job.workType == WorkType.ONLINE) "Online" else job.location.take(18),
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                        maxLines = 1
                    )
                }

                // Duration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = job.duration,
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Applicants count
                Text(
                    text = "${job.applicantsCount} applied",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate500,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate800)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate500),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(actionButtonText)
            }
        }
    }
}
