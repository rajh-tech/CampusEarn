package com.example

import com.example.data.model.ActiveMode
import com.example.data.model.ApplicationStatus
import com.example.data.model.UserProfile
import com.example.data.repository.CampusEarnRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testProfileCompletionPercentage() {
        val user = UserProfile(
            id = "test_user",
            fullName = "Ananya Sen",
            email = "ananya@du.ac.in",
            phone = "9876543210",
            collegeName = "St. Stephen's College",
            course = "BA Economics",
            city = "Delhi",
            state = "Delhi",
            graduationYear = "2026",
            bio = "Creative writer and researcher with extensive campus editorial experience.",
            skills = listOf("Writing", "Research"),
            portfolioUrl = "https://ananya.work"
        )
        val score = user.calculateCompletionPercentage()
        assertTrue("Completion percentage should be 100 for complete profile with phone and grad year", score == 100)
    }

    @Test
    fun testProfilePhoneAndGraduationYearPersistence() {
        val repository = CampusEarnRepository.getInstance()
        val current = repository.currentUser.value
        repository.updateProfile(current.copy(phone = "+91 91234 56789", graduationYear = "2028"))
        val updated = repository.currentUser.value
        assertEquals("+91 91234 56789", updated.phone)
        assertEquals("2028", updated.graduationYear)
    }

    @Test
    fun testRepositoryJobApplicationAndWithdrawFlow() {
        val repository = CampusEarnRepository.getInstance()
        val testJob = repository.postJob(
            title = "App Test Gig ${System.currentTimeMillis()}",
            category = com.example.data.model.JobCategory.WEB_DEVELOPMENT,
            description = "Test gig description for application and withdrawal flow verification",
            responsibilities = listOf("Design", "Code"),
            requiredSkills = listOf("Kotlin", "Jetpack Compose"),
            workType = com.example.data.model.WorkType.ONLINE,
            location = "Remote",
            paymentAmount = 2000.0,
            paymentType = com.example.data.model.PaymentType.FIXED,
            duration = "3 Days",
            openings = 1,
            deadline = "Nov 20, 2026"
        )

        // 1. Submit application
        val applied = repository.applyToJob(
            jobId = testJob.id,
            introduction = "Passionate student creator ready to deliver pixel-perfect designs.",
            skills = listOf("Figma", "Canva"),
            portfolioUrl = "https://behance.net/test"
        )
        assertTrue("Initial application to new job must succeed", applied)
        val myApp = repository.applications.value.firstOrNull { it.jobId == testJob.id && it.studentId == repository.currentUser.value.id }
        assertNotNull("Application should exist", myApp)

        // 2. Duplicate active application attempt must be rejected
        val duplicateAttempt = repository.applyToJob(
            jobId = testJob.id,
            introduction = "Trying duplicate",
            skills = emptyList(),
            portfolioUrl = ""
        )
        assertFalse("Duplicate active application must be rejected", duplicateAttempt)

        // 3. Withdraw application
        repository.withdrawApplication(myApp!!.id)
        val withdrawnApp = repository.applications.value.firstOrNull { it.id == myApp.id }
        assertNotNull(withdrawnApp)
        assertEquals("Application status must be WITHDRAWN", ApplicationStatus.WITHDRAWN, withdrawnApp!!.status)

        // 4. Re-applying after withdrawal should be permitted
        val reapplySuccess = repository.applyToJob(
            jobId = testJob.id,
            introduction = "Re-applying with updated portfolio link.",
            skills = listOf("Figma", "Tailwind"),
            portfolioUrl = "https://github.com/test-reapply"
        )
        assertTrue("Re-applying after withdrawal must succeed", reapplySuccess)
        val reactivatedApp = repository.applications.value.firstOrNull { it.id == myApp.id }
        assertNotNull(reactivatedApp)
        assertEquals("Re-activated application must be PENDING", ApplicationStatus.PENDING, reactivatedApp!!.status)
    }

    @Test
    fun testLanguageSelection() {
        val repository = CampusEarnRepository.getInstance()
        assertEquals("English (India)", repository.selectedLanguage.value)

        repository.setLanguage("Hindi")
        assertEquals("Hindi", repository.selectedLanguage.value)

        repository.setLanguage("Hinglish")
        assertEquals("Hinglish", repository.selectedLanguage.value)

        repository.setLanguage("English (India)")
    }

    @Test
    fun testRoleSwitching() {
        val repository = CampusEarnRepository.getInstance()
        repository.switchActiveMode(ActiveMode.EMPLOYER)
        assertEquals(ActiveMode.EMPLOYER, repository.activeMode.value)

        repository.switchActiveMode(ActiveMode.ADMIN)
        assertEquals(ActiveMode.ADMIN, repository.activeMode.value)

        repository.switchActiveMode(ActiveMode.STUDENT)
        assertEquals(ActiveMode.STUDENT, repository.activeMode.value)
    }

    @Test
    fun testAuthenticationLifecycle() {
        val repository = CampusEarnRepository.getInstance()

        // 1. Initial pre-registered session
        assertTrue("Session must be logged in initially", repository.isLoggedIn.value)

        // 2. Logout
        repository.logout()
        assertFalse("Session must be logged out", repository.isLoggedIn.value)

        // 3. Login with registered student
        val loginSuccess = repository.login("rahul.sharma@iitd.ac.in", "campus123")
        assertTrue("Login must succeed for valid email", loginSuccess)
        assertTrue("Session must be logged in after sign in", repository.isLoggedIn.value)
        assertEquals("Rahul Sharma", repository.currentUser.value.fullName)

        // 4. Register new user
        val registered = repository.register(
            fullName = "Neha Kapoor",
            email = "neha@srcc.du.ac.in",
            password = "securepass123",
            phone = "+91 99887 76655",
            collegeName = "SRCC Delhi",
            course = "B.Com Honours",
            graduationYear = "2026",
            city = "Delhi",
            state = "Delhi",
            role = com.example.data.model.UserRole.STUDENT
        )
        assertTrue("Registration must succeed for valid inputs", registered)
        assertEquals("Neha Kapoor", repository.currentUser.value.fullName)
    }

    @Test
    fun testJobSafetyAntiScamValidation() {
        val repository = CampusEarnRepository.getInstance()

        // 1. Scam containing registration fee request
        val scamViolation1 = repository.validateJobSafety(
            title = "Data Entry Work",
            description = "Pay a registration fee of ₹300 to activate work portal."
        )
        assertNotNull("Must detect registration fee scam", scamViolation1)
        assertTrue(scamViolation1!!.contains("registration fee", ignoreCase = true))

        // 2. Scam containing security deposit
        val scamViolation2 = repository.validateJobSafety(
            title = "Typist Assistant",
            description = "Security deposit required before work assignment."
        )
        assertNotNull("Must detect deposit scam", scamViolation2)

        // 3. Academic cheating violation
        val cheatingViolation = repository.validateJobSafety(
            title = "Online Assignment Help",
            description = "Write my exam and online test for me."
        )
        assertNotNull("Must block academic cheating requests", cheatingViolation)

        // 4. Clean job listing must pass safety validation
        val cleanJob = repository.validateJobSafety(
            title = "Graphic Designer for Social Media Posters",
            description = "Design 10 creative square flyers for our annual college festival in Canva or Figma."
        )
        assertNull("Legitimate job listing must pass validation without error", cleanJob)
    }

    @Test
    fun testApplicantDecisionsAndDirectMessaging() {
        val repository = CampusEarnRepository.getInstance()
        val job = repository.postJob(
            title = "Messaging Test Gig ${System.currentTimeMillis()}",
            category = com.example.data.model.JobCategory.CONTENT_WRITING,
            description = "Test gig for applicant decision testing",
            responsibilities = listOf("Writing"),
            requiredSkills = listOf("Content"),
            workType = com.example.data.model.WorkType.ONLINE,
            location = "Remote",
            paymentAmount = 1800.0,
            paymentType = com.example.data.model.PaymentType.FIXED,
            duration = "2 Days",
            openings = 1,
            deadline = "Dec 1, 2026"
        )

        // 1. Submit application
        repository.applyToJob(
            jobId = job.id,
            introduction = "Experienced campus lead with past event management experience.",
            skills = listOf("Communication", "Leadership"),
            portfolioUrl = "https://linkedin.com/in/test"
        )
        val app = repository.applications.value.firstOrNull { it.jobId == job.id && it.studentId == repository.currentUser.value.id }
        assertNotNull(app)

        // 2. Employer reviews: shortlist & reject
        repository.updateApplicationStatus(app!!.id, ApplicationStatus.SHORTLISTED)
        assertEquals(ApplicationStatus.SHORTLISTED, repository.applications.value.first { it.id == app.id }.status)

        repository.updateApplicationStatus(app.id, ApplicationStatus.REJECTED)
        assertEquals(ApplicationStatus.REJECTED, repository.applications.value.first { it.id == app.id }.status)

        // 3. Direct messaging
        val convId = repository.startConversationForJob(job.id)
        assertTrue("Conversation ID must not be blank", convId.isNotBlank())

        repository.sendMessage(convId, "Hello! I am ready to start the project deliverables.")
        val msgs = repository.messages.value[convId]
        assertNotNull(msgs)
        assertTrue("Messages list must contain the sent message", msgs!!.any { it.message.contains("ready to start") })

        // 4. Block user
        repository.blockUser("malicious_user_99")
        assertTrue("User must be in blocked list", repository.isUserBlocked("malicious_user_99"))
        assertFalse("Other user must not be blocked", repository.isUserBlocked("innocent_user_1"))
    }
}
