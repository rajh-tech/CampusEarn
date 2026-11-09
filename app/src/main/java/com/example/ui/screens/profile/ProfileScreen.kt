package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.ui.components.SafetyNoticeBanner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: CampusEarnRepository,
    onModeChange: (ActiveMode) -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val activeMode by repository.activeMode.collectAsState()
    val selectedLanguage by repository.selectedLanguage.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEditEmployerDialog by remember { mutableStateOf(false) }
    var showSafetyCharterDialog by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    val supportedLanguages = listOf(
        SupportedLanguage("en", "English (India)", "English", "Latin"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "Devanagari"),
        SupportedLanguage("hing", "Hinglish", "Hinglish (Campus)", "Colloquial"),
        SupportedLanguage("bn", "Bengali", "বাংলা", "Bengali"),
        SupportedLanguage("mr", "Marathi", "मराठी", "Devanagari"),
        SupportedLanguage("ta", "Tamil", "தமிழ்", "Tamil"),
        SupportedLanguage("te", "Telugu", "తెలుగు", "Telugu"),
        SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ", "Kannada")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Profile Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Profile & Account",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )
                Text(
                    text = "Manage your verified student and employer credentials",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card with Avatar & Verification
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(DeepBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.fullName.take(1),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser.fullName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                    )
                                    if (currentUser.isVerified) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified Student",
                                            tint = DeepBlueSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = currentUser.collegeName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                                    maxLines = 1
                                )
                                Text(
                                    text = "${currentUser.course} • Batch of ${currentUser.graduationYear}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        // Contact info pills (Phone & City)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = DeepBluePrimary, modifier = Modifier.size(13.dp))
                                    Text(
                                        text = currentUser.phone,
                                        style = MaterialTheme.typography.labelSmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "★ ${currentUser.averageRating} Rating",
                                    style = MaterialTheme.typography.labelSmall.copy(color = AmberPending, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(color = EmeraldLight, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "${currentUser.completedJobsCount} Gigs Completed",
                                    style = MaterialTheme.typography.labelSmall.copy(color = EmeraldEarn, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = currentUser.bio,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate700, lineHeight = 18.sp)
                        )

                        // Skills Tags
                        if (currentUser.skills.isNotEmpty()) {
                            Text("Verified Skills & Tech Stack", style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontWeight = FontWeight.Bold))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currentUser.skills.forEach { skill ->
                                    Surface(
                                        color = Color(0xFFEDE9FE),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.labelSmall.copy(color = PurpleAccent, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Portfolio Link
                        if (currentUser.portfolioUrl.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = DeepBluePrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = currentUser.portfolioUrl,
                                    style = MaterialTheme.typography.labelSmall.copy(color = DeepBlueSecondary, fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        if (activeMode == ActiveMode.EMPLOYER) {
                            OutlinedButton(
                                onClick = { showEditEmployerDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(16.dp), tint = PurpleAccent)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Employer Profile (Business, Description, City)", color = PurpleAccent)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Student Credentials (Phone, Batch, Skills)")
                            }
                        }
                    }
                }
            }

            // Quick Role Switcher Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Account Role Switcher",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = "One CampusEarn account can act as both Student and Employer. Switch your working view anytime:",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onModeChange(ActiveMode.STUDENT) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeMode == ActiveMode.STUDENT) DeepBluePrimary else Slate100,
                                    contentColor = if (activeMode == ActiveMode.STUDENT) Color.White else Slate700
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Student")
                            }

                            Button(
                                onClick = { onModeChange(ActiveMode.EMPLOYER) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeMode == ActiveMode.EMPLOYER) PurpleAccent else Slate100,
                                    contentColor = if (activeMode == ActiveMode.EMPLOYER) Color.White else Slate700
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Employer")
                            }

                            Button(
                                onClick = { onModeChange(ActiveMode.ADMIN) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeMode == ActiveMode.ADMIN) AmberPending else Slate100,
                                    contentColor = if (activeMode == ActiveMode.ADMIN) Color.White else Slate700
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Admin")
                            }
                        }
                    }
                }
            }

            // Safety & Trust Center (Clickable)
            item {
                Card(
                    onClick = { showSafetyCharterDialog = true },
                    modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.VerifiedUser, contentDescription = "Safety", tint = DeepBluePrimary, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CampusEarn Zero-Fee Guarantee",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary)
                            )
                            Text(
                                text = "Legitimate gigs never require registration fees. Tap to view our student protection charter.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DeepBluePrimary)
                    }
                }
            }

            // Language Selection Card (Clickable)
            item {
                Card(
                    onClick = { showLanguageSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = "Language", tint = PurpleAccent, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Language Preference", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text("Choose English, Hindi, or Regional languages", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                            }
                        }

                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = selectedLanguage,
                                    style = MaterialTheme.typography.labelSmall.copy(color = DeepBluePrimary, fontWeight = FontWeight.Bold)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = DeepBluePrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Quick Demo Persona Switcher (For easy evaluator walkthrough)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Quick Demo Personas",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700)
                        )
                        Text(
                            text = "Switch between realistic profiles to test student applications, employer hiring, and admin moderation:",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    repository.updateProfile(
                                        UserProfile(
                                            id = "user_student_1",
                                            fullName = "Rahul Sharma",
                                            email = "rahul.sharma@iitd.ac.in",
                                            phone = "+91 98765 43210",
                                            collegeName = "IIT Delhi",
                                            course = "B.Tech CSE",
                                            city = "New Delhi",
                                            state = "Delhi",
                                            graduationYear = "2027",
                                            bio = "Engineering student passionate about UI design and tech gigs.",
                                            skills = listOf("Jetpack Compose", "React", "Figma", "Writing"),
                                            portfolioUrl = "https://github.com/rahul-codes",
                                            completedJobsCount = 4,
                                            averageRating = 4.9
                                        )
                                    )
                                    onModeChange(ActiveMode.STUDENT)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Rahul (Student)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    repository.updateProfile(
                                        UserProfile(
                                            id = "emp_1",
                                            fullName = "Priya Verma",
                                            email = "priya@edusparks.in",
                                            phone = "+91 98111 22334",
                                            collegeName = "EduSparks EdTech Founder",
                                            course = "IIM Bangalore Alum",
                                            city = "Bengaluru",
                                            state = "Karnataka",
                                            graduationYear = "2021",
                                            bio = "Hiring creative college students for app marketing and technical writing.",
                                            roles = setOf(UserRole.EMPLOYER),
                                            skills = listOf("Product Management", "Marketing"),
                                            completedJobsCount = 18,
                                            averageRating = 5.0
                                        )
                                    )
                                    onModeChange(ActiveMode.EMPLOYER)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Priya (Employer)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Secure Logout Button
            item {
                Card(
                    onClick = { showLogoutConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEE2E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Log Out Account", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C)))
                                Text("Safely sign out from this device session", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFDC2626))
                    }
                }
            }
        }
    }

    // Language Selection Modal BottomSheet
    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
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
                            text = "Select Application Language",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = "Choose your preferred language for navigation and gig discovery",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                    IconButton(onClick = { showLanguageSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(supportedLanguages) { lang ->
                        val isSelected = selectedLanguage.startsWith(lang.name.take(4)) || selectedLanguage == lang.name
                        Card(
                            onClick = {
                                repository.setLanguage(lang.name)
                                showLanguageSheet = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) DeepBluePrimary else Slate200
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = lang.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) DeepBluePrimary else Slate900
                                        )
                                    )
                                    Text(
                                        text = "${lang.nativeName} • ${lang.script}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                    )
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = DeepBluePrimary)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Edit Profile Modal Dialog with Phone and Graduation Year
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(currentUser.fullName) }
        var phoneInput by remember { mutableStateOf(currentUser.phone) }
        var collegeInput by remember { mutableStateOf(currentUser.collegeName) }
        var courseInput by remember { mutableStateOf(currentUser.course) }
        var gradYearInput by remember { mutableStateOf(currentUser.graduationYear) }
        var cityInput by remember { mutableStateOf(currentUser.city) }
        var bioInput by remember { mutableStateOf(currentUser.bio) }
        var skillsInput by remember { mutableStateOf(currentUser.skills.joinToString(", ")) }
        var portfolioInput by remember { mutableStateOf(currentUser.portfolioUrl) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Student Credentials") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+91 98765 43210") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate400) }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = collegeInput,
                            onValueChange = { collegeInput = it },
                            label = { Text("College / University") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = courseInput,
                                onValueChange = { courseInput = it },
                                label = { Text("Degree & Course") },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = gradYearInput,
                                onValueChange = { gradYearInput = it },
                                label = { Text("Grad Year") },
                                placeholder = { Text("2027") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            label = { Text("City & State") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = skillsInput,
                            onValueChange = { skillsInput = it },
                            label = { Text("Skills (comma-separated)") },
                            placeholder = { Text("React, Figma, Writing, Video Editing") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = bioInput,
                            onValueChange = { bioInput = it },
                            label = { Text("Bio") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = portfolioInput,
                            onValueChange = { portfolioInput = it },
                            label = { Text("Portfolio / GitHub URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val skillsList = skillsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        repository.updateProfile(
                            currentUser.copy(
                                fullName = nameInput,
                                phone = phoneInput,
                                collegeName = collegeInput,
                                course = courseInput,
                                graduationYear = gradYearInput,
                                city = cityInput,
                                bio = bioInput,
                                skills = skillsList,
                                portfolioUrl = portfolioInput
                            )
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
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
                    Text("Student Safety Charter")
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
                    Text("Close")
                }
            }
        )
    }

    // Edit Employer Profile Dialog
    if (showEditEmployerDialog) {
        var companyInput by remember { mutableStateOf(currentUser.companyName.ifBlank { currentUser.fullName }) }
        var contactNameInput by remember { mutableStateOf(currentUser.fullName) }
        var phoneInput by remember { mutableStateOf(currentUser.phone) }
        var cityInput by remember { mutableStateOf(currentUser.city) }
        var stateInput by remember { mutableStateOf(currentUser.state) }
        var descInput by remember { mutableStateOf(currentUser.businessDescription.ifBlank { currentUser.bio }) }

        AlertDialog(
            onDismissRequest = { showEditEmployerDialog = false },
            title = { Text("Edit Employer Profile") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = companyInput,
                            onValueChange = { companyInput = it },
                            label = { Text("Business / Company Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = contactNameInput,
                            onValueChange = { contactNameInput = it },
                            label = { Text("Lead Recruiter / Contact Person") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Official Contact Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = stateInput,
                                onValueChange = { stateInput = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Company Description / Hiring Focus") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateProfile(
                            currentUser.copy(
                                fullName = contactNameInput,
                                companyName = companyInput,
                                phone = phoneInput,
                                city = cityInput,
                                state = stateInput,
                                businessDescription = descInput,
                                bio = descInput
                            )
                        )
                        showEditEmployerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                ) {
                    Text("Save Organization Info")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditEmployerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626)) },
            title = { Text("Sign Out of CampusEarn?") },
            text = {
                Text(
                    "Are you sure you want to log out? Your applications, gigs, and wallet records will be safely preserved when you sign in again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        repository.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Stay Logged In")
                }
            }
        )
    }
}
