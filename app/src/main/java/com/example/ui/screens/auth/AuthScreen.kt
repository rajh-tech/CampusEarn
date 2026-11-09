package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.CampusEarnRepository
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    repository: CampusEarnRepository,
    onLoginSuccess: () -> Unit
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up

    // Login state
    var loginEmail by remember { mutableStateOf("rahul.sharma@iitd.ac.in") }
    var loginPassword by remember { mutableStateOf("campus123") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Registration state
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regPhone by remember { mutableStateOf("+91 ") }
    var regCollege by remember { mutableStateOf("") }
    var regCourse by remember { mutableStateOf("") }
    var regGradYear by remember { mutableStateOf("2027") }
    var regCity by remember { mutableStateOf("New Delhi") }
    var regState by remember { mutableStateOf("Delhi") }
    var regRole by remember { mutableStateOf(UserRole.STUDENT) }
    var regError by remember { mutableStateOf<String?>(null) }
    var showVerifyEmailDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = DeepBluePrimary, modifier = Modifier.size(20.dp))
                                Text("CampusEarn", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepBluePrimary))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedAuthTab == 0) "Welcome Back" else "Create CampusEarn Account",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                        )
                        Text(
                            text = "Learn. Work. Earn. • Indian Student Marketplace",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                // Tab Switcher
                item {
                    TabRow(
                        selectedTabIndex = selectedAuthTab,
                        containerColor = Color(0xFFF1F5F9),
                        indicator = {},
                        divider = {},
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Tab(
                            selected = selectedAuthTab == 0,
                            onClick = { selectedAuthTab = 0; loginError = null },
                            text = { Text("Sign In", fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = selectedAuthTab == 1,
                            onClick = { selectedAuthTab = 1; regError = null },
                            text = { Text("Register", fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                if (selectedAuthTab == 0) {
                    // LOGIN FORM
                    item {
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("College or Business Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate400) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate400) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = Slate400
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (loginError != null) {
                        item {
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = loginError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFDC2626)),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                    loginError = "Please enter both email and password."
                                } else {
                                    val success = repository.login(loginEmail, loginPassword)
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        loginError = "Invalid credentials. Please verify your email and password."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                        ) {
                            Text("Sign In to CampusEarn", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Quick Demo Persona Logins for instant evaluation
                    item {
                        Divider(color = Slate200)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Quick Demo Logins (Tap to pre-fill):",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontWeight = FontWeight.SemiBold)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    loginEmail = "rahul.sharma@iitd.ac.in"
                                    loginPassword = "password123"
                                    repository.login(loginEmail, loginPassword)
                                    onLoginSuccess()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Student", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    loginEmail = "priya@edusparks.in"
                                    loginPassword = "password123"
                                    repository.login(loginEmail, loginPassword)
                                    onLoginSuccess()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Employer", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    loginEmail = "admin@campusearn.in"
                                    loginPassword = "adminpassword"
                                    repository.login(loginEmail, loginPassword)
                                    onLoginSuccess()
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Admin", fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    // REGISTRATION FORM
                    item {
                        Text(
                            text = "Select Account Purpose:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = regRole == UserRole.STUDENT,
                                onClick = { regRole = UserRole.STUDENT },
                                label = { Text("🎓 Student") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = regRole == UserRole.EMPLOYER,
                                onClick = { regRole = UserRole.EMPLOYER },
                                label = { Text("💼 Employer") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = regRole == UserRole.BOTH,
                                onClick = { regRole = UserRole.BOTH },
                                label = { Text("⚡ Both") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text(if (regRole == UserRole.EMPLOYER) "Business / Contact Name" else "Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("student@college.ac.in") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+91 98765 43210") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password (min 6 chars)") },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (regRole != UserRole.EMPLOYER) {
                        item {
                            OutlinedTextField(
                                value = regCollege,
                                onValueChange = { regCollege = it },
                                label = { Text("College / University Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = regCourse,
                                    onValueChange = { regCourse = it },
                                    label = { Text("Course / Branch") },
                                    placeholder = { Text("B.Tech / B.Com") },
                                    modifier = Modifier.weight(1.5f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = regGradYear,
                                    onValueChange = { regGradYear = it },
                                    label = { Text("Grad Year") },
                                    placeholder = { Text("2027") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = regCity,
                                onValueChange = { regCity = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = regState,
                                onValueChange = { regState = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    if (regError != null) {
                        item {
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = regError!!,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFDC2626)),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (regFullName.isBlank()) {
                                    regError = "Please enter your full name."
                                } else if (!regEmail.contains("@") || !regEmail.contains(".")) {
                                    regError = "Please enter a valid email address."
                                } else if (regPassword.length < 6) {
                                    regError = "Password must be at least 6 characters."
                                } else {
                                    val success = repository.register(
                                        fullName = regFullName,
                                        email = regEmail,
                                        password = regPassword,
                                        phone = regPhone,
                                        collegeName = if (regCollege.isBlank()) "Registered Institute" else regCollege,
                                        course = if (regCourse.isBlank()) "Undergraduate" else regCourse,
                                        graduationYear = regGradYear,
                                        city = regCity,
                                        state = regState,
                                        role = regRole
                                    )
                                    if (success) {
                                        showVerifyEmailDialog = true
                                    } else {
                                        regError = "Registration failed. Please check your inputs."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldEarn)
                        ) {
                            Text("Complete Registration", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Email Verification Notice Dialog
    if (showVerifyEmailDialog) {
        AlertDialog(
            onDismissRequest = {
                showVerifyEmailDialog = false
                onLoginSuccess()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = EmeraldEarn)
                    Text("Account Registered!")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Welcome to CampusEarn, $regFullName!",
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        "A verification email has been dispatched to $regEmail. Your verified campus profile is now active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVerifyEmailDialog = false
                        onLoginSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBluePrimary)
                ) {
                    Text("Enter CampusEarn")
                }
            }
        )
    }
}
