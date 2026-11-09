package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CampusEarnRepository private constructor() {

    companion object {
        @Volatile
        private var instance: CampusEarnRepository? = null

        fun getInstance(): CampusEarnRepository {
            return instance ?: synchronized(this) {
                instance ?: CampusEarnRepository().also { instance = it }
            }
        }
    }

    // Pre-registered users for instant evaluation & authentication
    private val defaultStudent = UserProfile(
        id = "user_student_1",
        fullName = "Rahul Sharma",
        email = "rahul.sharma@iitd.ac.in",
        phone = "+91 98765 43210",
        collegeName = "IIT Delhi (Indian Institute of Technology)",
        course = "B.Tech Computer Science & Engineering",
        city = "New Delhi",
        state = "Delhi",
        graduationYear = "2027",
        bio = "Third-year engineering student passionate about UI design, mobile apps, and technical writing. Looking for part-time gigs to fund my research projects.",
        avatarUrl = "",
        roles = setOf(UserRole.STUDENT, UserRole.EMPLOYER),
        isVerified = true,
        skills = listOf("Jetpack Compose", "React", "Figma", "Content Writing", "Video Editing"),
        portfolioUrl = "https://github.com/rahul-codes",
        completedJobsCount = 4,
        averageRating = 4.9
    )

    private val defaultEmployer = UserProfile(
        id = "emp_1",
        fullName = "Priya Verma",
        companyName = "EduSparks EdTech Pvt Ltd",
        email = "priya@edusparks.in",
        phone = "+91 98111 22334",
        collegeName = "EduSparks EdTech Founder",
        course = "IIM Bangalore Alum",
        city = "Bengaluru",
        state = "Karnataka",
        graduationYear = "2021",
        bio = "Hiring creative college students for educational app design, marketing, and technical writing.",
        businessDescription = "Leading Indian student learning platform offering curated prep courses and campus ambassador opportunities.",
        roles = setOf(UserRole.EMPLOYER),
        isVerified = true,
        skills = listOf("Product Management", "Marketing", "Graphic Design Hiring"),
        completedJobsCount = 18,
        averageRating = 5.0
    )

    private val defaultAdmin = UserProfile(
        id = "admin_super_1",
        fullName = "CampusEarn Trust & Safety Admin",
        companyName = "CampusEarn Operations",
        email = "admin@campusearn.in",
        phone = "+91 80000 11223",
        collegeName = "CampusEarn HQ",
        course = "Operations & Compliance",
        city = "Bengaluru",
        state = "Karnataka",
        graduationYear = "2020",
        bio = "Platform moderator verifying student safety, employer authenticity, and zero-fee compliance.",
        roles = setOf(UserRole.ADMIN),
        isVerified = true
    )

    private val _allUsers = MutableStateFlow<List<UserProfile>>(listOf(defaultStudent, defaultEmployer, defaultAdmin))
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    // Authentication session state
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Current logged-in user profile
    private val _currentUser = MutableStateFlow(defaultStudent)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Current active mode
    private val _activeMode = MutableStateFlow(ActiveMode.STUDENT)
    val activeMode: StateFlow<ActiveMode> = _activeMode.asStateFlow()

    // Blocked users set
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    // Active Language Preference
    private val _selectedLanguage = MutableStateFlow("English (India)")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    // Jobs list
    private val _jobs = MutableStateFlow<List<JobPost>>(emptyList())
    val jobs: StateFlow<List<JobPost>> = _jobs.asStateFlow()

    // Applications list
    private val _applications = MutableStateFlow<List<JobApplication>>(emptyList())
    val applications: StateFlow<List<JobApplication>> = _applications.asStateFlow()

    // Work submissions
    private val _workSubmissions = MutableStateFlow<List<WorkSubmission>>(emptyList())
    val workSubmissions: StateFlow<List<WorkSubmission>> = _workSubmissions.asStateFlow()

    // Payments ledger
    private val _payments = MutableStateFlow<List<PaymentRecord>>(emptyList())
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    // Conversations
    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations.asStateFlow()

    // Messages grouped by conversation ID
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Moderation reports
    private val _reports = MutableStateFlow<List<ReportItem>>(emptyList())
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val initialJobs = listOf(
            JobPost(
                id = "job_1",
                employerId = "emp_1",
                employerName = "EduSparks EdTech Pvt Ltd",
                isEmployerVerified = true,
                employerRating = 4.9,
                title = "UI/UX & Graphic Designer for College App Launch",
                category = JobCategory.GRAPHIC_DESIGNING,
                description = "We are launching a college prep application and require 12 sleek social media posters, 3 brochure layouts, and app store banner graphics. Style guidelines and Canva/Figma assets will be provided.",
                responsibilities = listOf(
                    "Design high-conversion Instagram square & story banners",
                    "Create 3 PDF flyer mockups for campus distribution",
                    "Adhere strictly to brand typography and vibrant color palette"
                ),
                requiredSkills = listOf("Figma", "Canva", "Photoshop", "Typography"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 4500.0,
                paymentType = PaymentType.FIXED,
                duration = "10 Days",
                openings = 2,
                deadline = "Nov 12, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 6,
                postedDate = "3 hours ago",
                isFeatured = true
            ),
            JobPost(
                id = "job_2",
                employerId = "emp_2",
                employerName = "CodeKite Startup Studio",
                isEmployerVerified = true,
                employerRating = 5.0,
                title = "Responsive Web Landing Page in React / Tailwind",
                category = JobCategory.WEB_DEVELOPMENT,
                description = "Need a front-end developer to code a 4-section landing page from Figma design. Clean code, mobile responsiveness, and GitHub deployment are musts.",
                responsibilities = listOf(
                    "Translate Figma wireframes into responsive React.js / Tailwind code",
                    "Ensure snappy mobile performance and edge-to-edge aesthetics",
                    "Deliver clean repo via GitHub with README documentation"
                ),
                requiredSkills = listOf("React.js", "Tailwind CSS", "JavaScript", "Git"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 7500.0,
                paymentType = PaymentType.FIXED,
                duration = "2 Weeks",
                openings = 1,
                deadline = "Nov 20, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 11,
                postedDate = "1 day ago",
                isFeatured = true
            ),
            JobPost(
                id = "job_3",
                employerId = "emp_1",
                employerName = "EduSparks EdTech Pvt Ltd",
                isEmployerVerified = true,
                employerRating = 4.9,
                title = "Technical Content Writer - AI & Coding Guides",
                category = JobCategory.CONTENT_WRITING,
                description = "Write 5 beginner-friendly, high quality blog posts explaining Python basics and campus tech projects. Plagiarism-free and properly formatted in Markdown.",
                responsibilities = listOf(
                    "Write 5 articles of 1,200 words each on student tech trends",
                    "Incorporate SEO keywords and clean code snippet examples",
                    "Zero AI-copying (100% original, verified with Turnitin)"
                ),
                requiredSkills = listOf("Content Writing", "SEO", "Python Basics", "Research"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 3500.0,
                paymentType = PaymentType.FIXED,
                duration = "1 Week",
                openings = 3,
                deadline = "Nov 14, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 8,
                postedDate = "2 days ago"
            ),
            JobPost(
                id = "job_4",
                employerId = "emp_3",
                employerName = "FestCraft India Events",
                isEmployerVerified = true,
                employerRating = 4.7,
                title = "Delhi Youth Fest - Hospitality & Stage Crew",
                category = JobCategory.EVENT_MANAGEMENT,
                description = "Exciting offline weekend gig for college students! Coordinate guest check-ins, VIP badges, and stage sound checks at Siri Fort Auditorium during the national youth symposium.",
                responsibilities = listOf(
                    "Welcome attendees, verify passes via QR scanner app",
                    "Coordinate green-room schedule for speakers & artists",
                    "Maintain active safety standards and attendee queues"
                ),
                requiredSkills = listOf("Communication", "Coordination", "Punctuality", "Teamwork"),
                workType = WorkType.OFFLINE,
                location = "Delhi NCR (Siri Fort Auditorium, South Delhi)",
                paymentAmount = 3000.0,
                paymentType = PaymentType.FIXED,
                duration = "2 Days (Sat-Sun)",
                openings = 8,
                deadline = "Nov 8, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 15,
                postedDate = "5 hours ago",
                isFeatured = true
            ),
            JobPost(
                id = "job_5",
                employerId = "emp_4",
                employerName = "UrbanBlend Apparel & Lifestyle",
                isEmployerVerified = true,
                employerRating = 4.8,
                title = "Instagram Reels & Short-Form Video Editor",
                category = JobCategory.VIDEO_EDITING,
                description = "Looking for a creative student with great sense of pacing, trending audio, and text animations to edit 15 aesthetic Reels from raw footage.",
                responsibilities = listOf(
                    "Cut and color-grade raw mobile footage into 20-30s Reels",
                    "Add dynamic captions, transitions, sound effects",
                    "Deliver final MP4s formatted for 9:16 mobile feeds"
                ),
                requiredSkills = listOf("Premiere Pro / CapCut", "Sound Design", "Color Grading", "Trendy Pacing"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 5000.0,
                paymentType = PaymentType.FIXED,
                duration = "12 Days",
                openings = 2,
                deadline = "Nov 18, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 9,
                postedDate = "Yesterday"
            ),
            JobPost(
                id = "job_6",
                employerId = "emp_5",
                employerName = "TopRanker Online Classes",
                isEmployerVerified = true,
                employerRating = 4.9,
                title = "Class 10th-12th Mathematics Doubt Solver",
                category = JobCategory.ONLINE_TUTORING,
                description = "Solve high school math questions on our student discussion portal. Write neat step-by-step solutions with diagrams where required.",
                responsibilities = listOf(
                    "Solve 25-30 calculus and algebra questions per week",
                    "Provide neat handwritten or typed LaTeX answers",
                    "Clarify follow-up student questions in simple language"
                ),
                requiredSkills = listOf("Calculus", "Algebra", "Clear Penmanship", "LaTeX / Docs"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 6000.0,
                paymentType = PaymentType.FIXED,
                duration = "1 Month",
                openings = 4,
                deadline = "Nov 25, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 14,
                postedDate = "3 days ago"
            ),
            JobPost(
                id = "job_7",
                employerId = "emp_6",
                employerName = "Blitz Marketing Network",
                isEmployerVerified = true,
                employerRating = 4.6,
                title = "Campus Ambassador - North & South Delhi Campuses",
                category = JobCategory.CAMPUS_AMBASSADOR,
                description = "Represent CampusEarn and youth discounts partner brands across college societies, clubs, and WhatsApp groups. High performance bonuses included.",
                responsibilities = listOf(
                    "Organize mini info sessions during college lunch hours",
                    "Share campaign banners in verified student community groups",
                    "Collect genuine student feedback and feature requests"
                ),
                requiredSkills = listOf("Leadership", "Public Speaking", "Social Networking", "Organizing"),
                workType = WorkType.ONLINE,
                location = "Delhi NCR Campuses",
                paymentAmount = 4000.0,
                paymentType = PaymentType.FIXED,
                duration = "3 Weeks",
                openings = 5,
                deadline = "Nov 15, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 18,
                postedDate = "4 days ago"
            ),
            JobPost(
                id = "job_8",
                employerId = "emp_7",
                employerName = "RetailX Smart Tech Store",
                isEmployerVerified = true,
                employerRating = 4.8,
                title = "Weekend Tech Demonstrator - Koramangala Mall",
                category = JobCategory.OFFLINE_JOBS,
                description = "Demonstrate smart wearable gadgets and wireless audio gear to mall visitors. Safe public environment, verified employer, snacks and refreshments covered.",
                responsibilities = listOf(
                    "Guide walk-in customers through audio comparisons",
                    "Assist store staff with demo station inventory",
                    "Maintain pleasant and polite customer interactions"
                ),
                requiredSkills = listOf("Customer Service", "Gadget Enthusiasm", "Spoken English & Hindi"),
                workType = WorkType.OFFLINE,
                location = "Bengaluru (Nexus Koramangala Mall)",
                paymentAmount = 3200.0,
                paymentType = PaymentType.FIXED,
                duration = "2 Weekends (4 Days)",
                openings = 3,
                deadline = "Nov 10, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 7,
                postedDate = "1 day ago"
            ),
            JobPost(
                id = "job_9",
                employerId = "emp_2",
                employerName = "CodeKite Startup Studio",
                isEmployerVerified = true,
                employerRating = 5.0,
                title = "Verified Excel / Google Sheets Data Cleaning",
                category = JobCategory.DATA_ENTRY,
                description = "Clean up and standardize 1,500 rows of college club directory data (emails, URLs, phone formatting, deduplication).",
                responsibilities = listOf(
                    "Standardize names, phone formats, and verify email syntax",
                    "Remove duplicates and organize tags neatly",
                    "Deliver final .xlsx or Google Sheet with validation formulas"
                ),
                requiredSkills = listOf("MS Excel", "Google Sheets", "Attention to Detail"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 2000.0,
                paymentType = PaymentType.FIXED,
                duration = "4 Days",
                openings = 2,
                deadline = "Nov 12, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 12,
                postedDate = "4 hours ago"
            ),
            JobPost(
                id = "job_10",
                employerId = "emp_4",
                employerName = "UrbanBlend Apparel & Lifestyle",
                isEmployerVerified = true,
                employerRating = 4.8,
                title = "College Meme & Gen-Z Community Manager",
                category = JobCategory.SOCIAL_MEDIA,
                description = "Manage our student community channel, curate relatable college exam memes, and host weekly trivia polls to boost engagement.",
                responsibilities = listOf(
                    "Create 4 witty memes per week reflecting campus life",
                    "Reply to comments and maintain friendly brand tone",
                    "Track impressions and weekly community growth metrics"
                ),
                requiredSkills = listOf("Meme Culture", "Canva / Phonto", "Social Media Copy", "Creativity"),
                workType = WorkType.ONLINE,
                location = "Pan-India (Remote)",
                paymentAmount = 4000.0,
                paymentType = PaymentType.FIXED,
                duration = "3 Weeks",
                openings = 1,
                deadline = "Nov 19, 2026",
                status = JobStatus.ACTIVE,
                applicantsCount = 20,
                postedDate = "2 days ago"
            )
        )
        _jobs.value = initialJobs

        // Initial student applications
        val initialApplications = listOf(
            JobApplication(
                id = "app_1",
                jobId = "job_2",
                jobTitle = "Responsive Web Landing Page in React / Tailwind",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                studentCollege = "IIT Delhi",
                studentCourse = "B.Tech CSE",
                studentRating = 4.9,
                introduction = "Hi Priya! I have built 8+ responsive web apps using React.js and Tailwind CSS with 100/100 Lighthouse performance scores. Excited to turn your Figma mockups into pixel-perfect code.",
                skills = listOf("React.js", "Tailwind CSS", "TypeScript", "Git"),
                portfolioUrl = "https://github.com/rahul-codes",
                status = ApplicationStatus.ACCEPTED,
                appliedDate = "Oct 25, 2026"
            ),
            JobApplication(
                id = "app_2",
                jobId = "job_1",
                jobTitle = "UI/UX & Graphic Designer for College App Launch",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                studentCollege = "IIT Delhi",
                studentCourse = "B.Tech CSE",
                studentRating = 4.9,
                introduction = "Hey there! I regularly design posters for college cultural fests (Rendezvous) using Figma and Canva. Check out my design drive for past banners.",
                skills = listOf("Figma", "Canva", "Poster Design"),
                portfolioUrl = "https://behance.net/rahul_designs",
                status = ApplicationStatus.SHORTLISTED,
                appliedDate = "Oct 28, 2026"
            ),
            JobApplication(
                id = "app_3",
                jobId = "job_4",
                jobTitle = "Delhi Youth Fest - Hospitality & Stage Crew",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                studentCollege = "IIT Delhi",
                studentCourse = "B.Tech CSE",
                studentRating = 4.9,
                introduction = "I live in South Delhi and can easily commute to Siri Fort Auditorium. Experienced in managing crowd registrations and VIP escorts during college events.",
                skills = listOf("Coordination", "Punctuality", "Communication"),
                portfolioUrl = "",
                status = ApplicationStatus.PENDING,
                appliedDate = "Yesterday"
            ),
            // Sample other applicant for employer view
            JobApplication(
                id = "app_4",
                jobId = "job_1",
                jobTitle = "UI/UX & Graphic Designer for College App Launch",
                studentId = "user_student_2",
                studentName = "Sneha Patel",
                studentCollege = "Delhi University (Miranda House)",
                studentCourse = "BA English & Media Studies",
                studentRating = 4.8,
                introduction = "Hello team! As the creative director of Miranda House Media Society, I craft eye-catching illustrations and brochures.",
                skills = listOf("Photoshop", "Illustrator", "Canva"),
                portfolioUrl = "https://instagram.com/sneha_creates",
                status = ApplicationStatus.PENDING,
                appliedDate = "Oct 29, 2026"
            )
        )
        _applications.value = initialApplications

        // Work Submissions
        val initialSubmissions = listOf(
            WorkSubmission(
                id = "sub_1",
                applicationId = "app_1",
                jobId = "job_2",
                jobTitle = "Responsive Web Landing Page in React / Tailwind",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                submissionUrl = "https://github.com/rahul-codes/campusearn-landing-demo",
                notes = "Completed the 4 sections according to Figma design. Fully responsive across 360px mobile to 4K ultra-wide screens. Deployed on Vercel preview.",
                status = WorkSubmissionStatus.APPROVED,
                submittedAt = "Oct 29, 2026",
                feedback = "Outstanding work Rahul! Clean code architecture and blazing fast page load. Payment released to your wallet."
            )
        )
        _workSubmissions.value = initialSubmissions

        // Payments ledger
        val initialPayments = listOf(
            PaymentRecord(
                id = "pay_101",
                jobId = "job_2",
                jobTitle = "Responsive Web Landing Page in React / Tailwind",
                payerId = "emp_2",
                payerName = "CodeKite Startup Studio",
                receiverId = "user_student_1",
                receiverName = "Rahul Sharma",
                amount = 7500.0,
                paymentMethod = "UPI (rahul@okhdfcbank)",
                transactionReference = "TXN_UPI_20261029_894123",
                status = PaymentStatus.PAID,
                createdAt = "Oct 29, 2026"
            ),
            PaymentRecord(
                id = "pay_102",
                jobId = "job_archived_1",
                jobTitle = "Python Web Scraping & Data Extraction",
                payerId = "emp_1",
                payerName = "EduSparks EdTech Pvt Ltd",
                receiverId = "user_student_1",
                receiverName = "Rahul Sharma",
                amount = 2500.0,
                paymentMethod = "UPI (rahul@okhdfcbank)",
                transactionReference = "TXN_UPI_20261014_118492",
                status = PaymentStatus.PAID,
                createdAt = "Oct 14, 2026"
            ),
            PaymentRecord(
                id = "pay_103",
                jobId = "job_1",
                jobTitle = "UI/UX & Graphic Designer for College App Launch",
                payerId = "emp_1",
                payerName = "EduSparks EdTech Pvt Ltd",
                receiverId = "user_student_1",
                receiverName = "Rahul Sharma",
                amount = 4500.0,
                paymentMethod = "Escrow Ledger Held",
                transactionReference = "ESC_20261028_499211",
                status = PaymentStatus.PENDING,
                createdAt = "Oct 28, 2026"
            )
        )
        _payments.value = initialPayments

        // Conversations
        val initialConversations = listOf(
            ConversationItem(
                id = "conv_1",
                jobId = "job_2",
                jobTitle = "Responsive Web Landing Page in React / Tailwind",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                studentCollege = "IIT Delhi",
                employerId = "emp_2",
                employerName = "CodeKite Startup Studio (Vikram)",
                lastMessage = "Thanks for the swift delivery, Rahul! The mobile layout is super slick.",
                lastMessageTime = "10:45 AM",
                unreadCount = 0
            ),
            ConversationItem(
                id = "conv_2",
                jobId = "job_1",
                jobTitle = "UI/UX & Graphic Designer for College App Launch",
                studentId = "user_student_1",
                studentName = "Rahul Sharma",
                studentCollege = "IIT Delhi",
                employerId = "emp_1",
                employerName = "EduSparks EdTech (Priya)",
                lastMessage = "Hi Rahul, your past poster work looks great. Are you available for a 10-min briefing call?",
                lastMessageTime = "Yesterday",
                unreadCount = 1
            )
        )
        _conversations.value = initialConversations

        val initialMessagesMap = mapOf(
            "conv_1" to listOf(
                ChatMessage(
                    id = "msg_1",
                    conversationId = "conv_1",
                    senderId = "emp_2",
                    senderName = "Vikram (CodeKite)",
                    message = "Welcome aboard Rahul! Here is the Figma link for the landing page project.",
                    timestamp = "Oct 26, 11:30 AM",
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_2",
                    conversationId = "conv_1",
                    senderId = "user_student_1",
                    senderName = "Rahul Sharma",
                    message = "Thanks Vikram! I reviewed the design tokens. Will implement Tailwind CSS v3 with responsive breakpoint grids.",
                    timestamp = "Oct 26, 11:35 AM",
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_3",
                    conversationId = "conv_1",
                    senderId = "user_student_1",
                    senderName = "Rahul Sharma",
                    message = "I have uploaded the preview build link and submitted the work under My Work tab.",
                    timestamp = "Oct 29, 09:15 AM",
                    isRead = true
                ),
                ChatMessage(
                    id = "msg_4",
                    conversationId = "conv_1",
                    senderId = "emp_2",
                    senderName = "Vikram (CodeKite)",
                    message = "Thanks for the swift delivery, Rahul! The mobile layout is super slick. Approved and released payment!",
                    timestamp = "Oct 29, 10:45 AM",
                    isRead = true
                )
            ),
            "conv_2" to listOf(
                ChatMessage(
                    id = "msg_201",
                    conversationId = "conv_2",
                    senderId = "emp_1",
                    senderName = "Priya (EduSparks)",
                    message = "Hi Rahul, your past poster work looks great. Are you available for a 10-min briefing call?",
                    timestamp = "Yesterday, 04:20 PM",
                    isRead = false
                )
            )
        )
        _messages.value = initialMessagesMap

        // Notifications
        val initialNotifications = listOf(
            NotificationItem(
                id = "notif_1",
                userId = "user_student_1",
                title = "Payment Credited! ₹7,500",
                message = "CodeKite Startup approved your landing page submission. ₹7,500 has been credited to your CampusEarn ledger.",
                isRead = false,
                timestamp = "Oct 29, 10:46 AM",
                category = "Payment"
            ),
            NotificationItem(
                id = "notif_2",
                userId = "user_student_1",
                title = "Application Shortlisted!",
                message = "EduSparks EdTech shortlisted your profile for 'UI/UX & Graphic Designer'. Check your messages for instructions.",
                isRead = false,
                timestamp = "Yesterday, 04:21 PM",
                category = "Application"
            ),
            NotificationItem(
                id = "notif_3",
                userId = "user_student_1",
                title = "Safety Reminder for Offline Gigs",
                message = "Applying for offline events? Always meet at verified public venues and never pay any registration fees.",
                isRead = true,
                timestamp = "3 days ago",
                category = "Safety"
            )
        )
        _notifications.value = initialNotifications

        // Moderation reports
        val initialReports = listOf(
            ReportItem(
                id = "rep_1",
                reporterId = "user_student_1",
                reporterName = "Rahul Sharma",
                jobId = "job_flagged_1",
                jobTitle = "Data Typist - Deposit ₹500 for Security",
                reason = "Upfront Security Fee Scam",
                description = "Employer is demanding ₹500 via GooglePay before assigning data work. This violates CampusEarn zero-fee policy.",
                status = "PENDING_MODERATION",
                createdAt = "Oct 30, 2026"
            )
        )
        _reports.value = initialReports
    }

    // Role switching
    fun switchActiveMode(mode: ActiveMode) {
        _activeMode.value = mode
    }

    fun updateProfile(updated: UserProfile) {
        _currentUser.value = updated
    }

    // Job Actions
    fun postJob(
        title: String,
        category: JobCategory,
        description: String,
        responsibilities: List<String>,
        requiredSkills: List<String>,
        workType: WorkType,
        location: String,
        paymentAmount: Double,
        paymentType: PaymentType,
        duration: String,
        openings: Int,
        deadline: String
    ): JobPost {
        val user = _currentUser.value
        val newJob = JobPost(
            id = "job_${UUID.randomUUID().toString().take(8)}",
            employerId = user.id,
            employerName = if (user.fullName.isNotBlank()) user.fullName else "Campus Employer",
            isEmployerVerified = true,
            employerRating = 5.0,
            title = title,
            category = category,
            description = description,
            responsibilities = responsibilities,
            requiredSkills = requiredSkills,
            workType = workType,
            location = location,
            paymentAmount = paymentAmount,
            paymentType = paymentType,
            duration = duration,
            openings = openings,
            deadline = deadline,
            status = JobStatus.ACTIVE,
            applicantsCount = 0,
            postedDate = "Just now",
            isFeatured = false
        )
        _jobs.update { listOf(newJob) + it }
        return newJob
    }

    fun closeJob(jobId: String) {
        _jobs.update { list ->
            list.map { if (it.id == jobId) it.copy(status = JobStatus.CLOSED) else it }
        }
    }

    fun deleteJob(jobId: String) {
        _jobs.update { list -> list.filterNot { it.id == jobId } }
    }

    // Application Actions
    fun applyToJob(
        jobId: String,
        introduction: String,
        skills: List<String>,
        portfolioUrl: String
    ): Boolean {
        val student = _currentUser.value
        // Prevent duplicate active applications
        val existing = _applications.value.firstOrNull { it.jobId == jobId && it.studentId == student.id }
        if (existing != null && existing.status != ApplicationStatus.WITHDRAWN) {
            return false
        }

        val targetJob = _jobs.value.firstOrNull { it.id == jobId } ?: return false

        if (existing != null && existing.status == ApplicationStatus.WITHDRAWN) {
            // Re-apply by updating existing record
            _applications.update { list ->
                list.map {
                    if (it.id == existing.id) {
                        it.copy(
                            status = ApplicationStatus.PENDING,
                            introduction = introduction,
                            skills = skills,
                            portfolioUrl = portfolioUrl,
                            appliedDate = "Today (Re-applied)"
                        )
                    } else it
                }
            }
            _jobs.update { list ->
                list.map { if (it.id == jobId) it.copy(applicantsCount = it.applicantsCount + 1) else it }
            }
            val notif = NotificationItem(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = student.id,
                title = "Application Re-submitted",
                message = "Your application for '${targetJob.title}' has been re-submitted.",
                isRead = false,
                timestamp = "Just now",
                category = "Application"
            )
            _notifications.update { listOf(notif) + it }
            return true
        }

        val newApp = JobApplication(
            id = "app_${UUID.randomUUID().toString().take(8)}",
            jobId = jobId,
            jobTitle = targetJob.title,
            studentId = student.id,
            studentName = student.fullName,
            studentCollege = student.collegeName,
            studentCourse = student.course,
            studentPhone = student.phone,
            studentGraduationYear = student.graduationYear,
            studentRating = student.averageRating,
            studentAvatar = student.avatarUrl,
            introduction = introduction,
            skills = skills,
            portfolioUrl = portfolioUrl,
            status = ApplicationStatus.PENDING,
            appliedDate = "Today"
        )

        _applications.update { listOf(newApp) + it }
        // Update job applicant count
        _jobs.update { list ->
            list.map { if (it.id == jobId) it.copy(applicantsCount = it.applicantsCount + 1) else it }
        }

        // Add notification
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = student.id,
            title = "Application Submitted",
            message = "Your application for '${targetJob.title}' was sent to the employer.",
            isRead = false,
            timestamp = "Just now",
            category = "Application"
        )
        _notifications.update { listOf(notif) + it }

        return true
    }

    fun updateApplicationStatus(applicationId: String, newStatus: ApplicationStatus) {
        _applications.update { list ->
            list.map { if (it.id == applicationId) it.copy(status = newStatus) else it }
        }
    }

    fun withdrawApplication(applicationId: String) {
        val app = _applications.value.firstOrNull { it.id == applicationId } ?: return
        _applications.update { list ->
            list.map { if (it.id == applicationId) it.copy(status = ApplicationStatus.WITHDRAWN) else it }
        }
        _jobs.update { list ->
            list.map { if (it.id == app.jobId) it.copy(applicantsCount = (it.applicantsCount - 1).coerceAtLeast(0)) else it }
        }
        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = app.studentId,
            title = "Application Withdrawn",
            message = "Your application for '${app.jobTitle}' has been successfully withdrawn.",
            isRead = false,
            timestamp = "Just now",
            category = "Application"
        )
        _notifications.update { listOf(notif) + it }
    }

    // Work Submission
    fun submitWork(
        applicationId: String,
        jobId: String,
        jobTitle: String,
        submissionUrl: String,
        notes: String
    ): WorkSubmission {
        val student = _currentUser.value
        val submission = WorkSubmission(
            id = "sub_${UUID.randomUUID().toString().take(8)}",
            applicationId = applicationId,
            jobId = jobId,
            jobTitle = jobTitle,
            studentId = student.id,
            studentName = student.fullName,
            submissionUrl = submissionUrl,
            notes = notes,
            status = WorkSubmissionStatus.PENDING_REVIEW,
            submittedAt = "Just now"
        )
        _workSubmissions.update { listOf(submission) + it }
        return submission
    }

    fun reviewWork(submissionId: String, approved: Boolean, feedback: String) {
        _workSubmissions.update { list ->
            list.map { sub ->
                if (sub.id == submissionId) {
                    val status = if (approved) WorkSubmissionStatus.APPROVED else WorkSubmissionStatus.CHANGES_REQUESTED
                    sub.copy(status = status, feedback = feedback)
                } else sub
            }
        }

        if (approved) {
            val sub = _workSubmissions.value.firstOrNull { it.id == submissionId }
            if (sub != null) {
                val targetJob = _jobs.value.firstOrNull { it.id == sub.jobId }
                val amount = targetJob?.paymentAmount ?: 3000.0
                // Add payment record
                val payment = PaymentRecord(
                    id = "pay_${UUID.randomUUID().toString().take(8)}",
                    jobId = sub.jobId,
                    jobTitle = sub.jobTitle,
                    payerId = "emp_active",
                    payerName = targetJob?.employerName ?: "Employer",
                    receiverId = sub.studentId,
                    receiverName = sub.studentName,
                    amount = amount,
                    paymentMethod = "UPI Ledger Release",
                    transactionReference = "TXN_UPI_${System.currentTimeMillis().toString().takeLast(8)}",
                    status = PaymentStatus.PAID,
                    createdAt = "Just now"
                )
                _payments.update { listOf(payment) + it }

                // Notify student
                val notif = NotificationItem(
                    id = "notif_${UUID.randomUUID().toString().take(8)}",
                    userId = sub.studentId,
                    title = "Work Approved! ₹${amount.toInt()} Credited",
                    message = "Your work for '${sub.jobTitle}' has been approved! Funds added to your wallet.",
                    isRead = false,
                    timestamp = "Just now",
                    category = "Payment"
                )
                _notifications.update { listOf(notif) + it }
            }
        }
    }

    // Wallet Payout Request
    fun requestPayout(amount: Double, upiOrBank: String, isUpi: Boolean): Boolean {
        val totalEarned = _payments.value
            .filter { it.receiverId == _currentUser.value.id && it.status == PaymentStatus.PAID }
            .sumOf { it.amount }

        val alreadyWithdrawn = _payments.value
            .filter { it.status == PaymentStatus.PROCESSING }
            .sumOf { it.amount }

        val available = (totalEarned - alreadyWithdrawn).coerceAtLeast(0.0)
        if (amount > available || amount < 100.0) return false

        val newRecord = PaymentRecord(
            id = "payout_${UUID.randomUUID().toString().take(8)}",
            jobId = "payout_req",
            jobTitle = "Wallet Payout Request",
            payerId = "campusearn_escrow",
            payerName = "CampusEarn Settlement",
            receiverId = _currentUser.value.id,
            receiverName = _currentUser.value.fullName,
            amount = amount,
            paymentMethod = if (isUpi) "UPI: $upiOrBank" else "Bank A/C: $upiOrBank",
            transactionReference = "REQ_OUT_${System.currentTimeMillis().toString().takeLast(8)}",
            status = PaymentStatus.PROCESSING,
            createdAt = "Just now"
        )
        _payments.update { listOf(newRecord) + it }

        val notif = NotificationItem(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = _currentUser.value.id,
            title = "Payout Request Submitted ₹${amount.toInt()}",
            message = "Your payout request for ₹${amount.toInt()} to $upiOrBank is processing. Verified transfer within 24 hours.",
            isRead = false,
            timestamp = "Just now",
            category = "Wallet"
        )
        _notifications.update { listOf(notif) + it }
        return true
    }

    // Messaging - Real Student & Employer Direct Messaging
    fun sendMessage(conversationId: String, text: String) {
        val user = _currentUser.value
        val newMsg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = user.id,
            senderName = user.fullName,
            message = text,
            timestamp = "Just now",
            isRead = true
        )

        _messages.update { currentMap ->
            val existingList = currentMap[conversationId] ?: emptyList()
            currentMap + (conversationId to (existingList + newMsg))
        }

        // Update conversation last message
        _conversations.update { list ->
            list.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = text,
                        lastMessageTime = "Just now",
                        unreadCount = 0
                    )
                } else conv
            }
        }

        // Send a real notification to the other party
        val conv = _conversations.value.firstOrNull { it.id == conversationId }
        if (conv != null) {
            val recipientId = if (user.id == conv.studentId) conv.employerId else conv.studentId
            val notif = NotificationItem(
                id = "notif_${UUID.randomUUID().toString().take(8)}",
                userId = recipientId,
                title = "New Message from ${user.fullName}",
                message = if (text.length > 60) text.take(57) + "..." else text,
                isRead = false,
                timestamp = "Just now",
                category = "Message"
            )
            _notifications.update { listOf(notif) + it }
        }
    }

    fun markConversationAsRead(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(unreadCount = 0) else it }
        }
        _messages.update { currentMap ->
            val list = currentMap[conversationId] ?: emptyList()
            currentMap + (conversationId to list.map { it.copy(isRead = true) })
        }
    }

    fun startConversationForJob(jobId: String): String {
        val user = _currentUser.value
        val existing = _conversations.value.firstOrNull { it.jobId == jobId && it.studentId == user.id }
        if (existing != null) return existing.id

        val job = _jobs.value.firstOrNull { it.id == jobId } ?: return ""
        val newConvId = "conv_${UUID.randomUUID().toString().take(8)}"
        val newConv = ConversationItem(
            id = newConvId,
            jobId = jobId,
            jobTitle = job.title,
            studentId = user.id,
            studentName = user.fullName,
            studentCollege = user.collegeName,
            employerId = job.employerId,
            employerName = job.employerName,
            lastMessage = "Started conversation regarding ${job.title}",
            lastMessageTime = "Just now",
            unreadCount = 0
        )
        _conversations.update { listOf(newConv) + it }
        _messages.update { it + (newConvId to emptyList()) }
        return newConvId
    }

    // Safety validation for gig listings
    fun validateJobSafety(title: String, description: String): String? {
        val prohibitedPatterns = listOf(
            "registration fee" to "Upfront registration fees are strictly prohibited under the CampusEarn Zero-Fee Guarantee.",
            "deposit" to "Security deposits or upfront payments from students violate platform safety policy.",
            "pay first" to "Employers may never ask students to pay money before starting work.",
            "processing fee" to "Processing fees are prohibited on CampusEarn.",
            "cheating" to "Academic dishonesty, including exam cheating, is strictly barred.",
            "write my exam" to "Academic integrity violation: completing examinations for credit is prohibited.",
            "proxy exam" to "Proxy attendance or test-taking is strictly prohibited.",
            "whatsapp payment" to "All escrow settlements must occur via on-platform UPI/Bank verified transfers.",
            "crypto investment" to "Investment or multi-level schemes are not permitted."
        )

        val combined = "$title $description".lowercase()
        for ((pattern, message) in prohibitedPatterns) {
            if (combined.contains(pattern)) {
                return message
            }
        }
        return null
    }

    // User Blocking
    fun blockUser(userId: String) {
        _blockedUserIds.update { it + userId }
    }

    fun unblockUser(userId: String) {
        _blockedUserIds.update { it - userId }
    }

    fun isUserBlocked(userId: String): Boolean {
        return _blockedUserIds.value.contains(userId)
    }

    // Authentication & Account Lifecycle
    fun login(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.length < 4) return false
        val found = _allUsers.value.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
        if (found != null) {
            _currentUser.value = found
            _activeMode.value = when {
                found.roles.contains(UserRole.ADMIN) -> ActiveMode.ADMIN
                found.roles.contains(UserRole.EMPLOYER) -> ActiveMode.EMPLOYER
                else -> ActiveMode.STUDENT
            }
            _isLoggedIn.value = true
            return true
        }

        // Generic authentic sign-in fallback
        val isEmp = email.contains("emp", ignoreCase = true) || email.contains("edusparks", ignoreCase = true)
        val isAdmin = email.contains("admin", ignoreCase = true)
        val profile = UserProfile(
            id = "user_${UUID.randomUUID().toString().take(8)}",
            fullName = email.substringBefore("@").replace(".", " ").capitalize(),
            email = email.trim(),
            phone = "+91 98000 00000",
            collegeName = if (isEmp) "Campus Corporate Partner" else "Delhi University",
            course = if (isEmp) "Executive" else "B.Com Honours",
            city = "New Delhi",
            state = "Delhi",
            graduationYear = "2026",
            bio = if (isEmp) "Hiring college students on CampusEarn." else "College student looking for verified gigs.",
            roles = when {
                isAdmin -> setOf(UserRole.ADMIN)
                isEmp -> setOf(UserRole.EMPLOYER)
                else -> setOf(UserRole.STUDENT, UserRole.EMPLOYER)
            },
            isVerified = true
        )
        _allUsers.update { it + profile }
        _currentUser.value = profile
        _activeMode.value = when {
            isAdmin -> ActiveMode.ADMIN
            isEmp -> ActiveMode.EMPLOYER
            else -> ActiveMode.STUDENT
        }
        _isLoggedIn.value = true
        return true
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        collegeName: String,
        course: String,
        graduationYear: String,
        city: String,
        state: String,
        role: UserRole
    ): Boolean {
        if (fullName.isBlank() || email.isBlank() || password.length < 6) return false
        val newId = "user_${UUID.randomUUID().toString().take(8)}"
        val newUser = UserProfile(
            id = newId,
            fullName = fullName.trim(),
            companyName = if (role == UserRole.EMPLOYER) fullName.trim() else "",
            email = email.trim().lowercase(),
            phone = phone.trim(),
            collegeName = collegeName.trim(),
            course = course.trim(),
            city = city.trim(),
            state = state.trim(),
            graduationYear = graduationYear.trim(),
            bio = if (role == UserRole.EMPLOYER) "Verified business hiring campus talent." else "College student ready to work and earn pocket money.",
            businessDescription = if (role == UserRole.EMPLOYER) "Registered partner organization on CampusEarn." else "",
            roles = when (role) {
                UserRole.STUDENT -> setOf(UserRole.STUDENT)
                UserRole.EMPLOYER -> setOf(UserRole.EMPLOYER)
                UserRole.BOTH -> setOf(UserRole.STUDENT, UserRole.EMPLOYER)
                UserRole.ADMIN -> setOf(UserRole.ADMIN)
            },
            isVerified = true
        )
        _allUsers.update { it + newUser }
        _currentUser.value = newUser
        _activeMode.value = if (role == UserRole.EMPLOYER) ActiveMode.EMPLOYER else ActiveMode.STUDENT
        _isLoggedIn.value = true
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Moderation & Safety
    fun reportJobOrUser(
        reportedUserId: String,
        reportedUserName: String,
        jobId: String,
        jobTitle: String,
        reason: String,
        description: String
    ) {
        val user = _currentUser.value
        val report = ReportItem(
            id = "rep_${UUID.randomUUID().toString().take(8)}",
            reporterId = user.id,
            reporterName = user.fullName,
            reportedUserId = reportedUserId,
            reportedUserName = reportedUserName,
            jobId = jobId,
            jobTitle = jobTitle,
            reason = reason,
            description = description,
            status = "PENDING_MODERATION",
            createdAt = "Just now"
        )
        _reports.update { listOf(report) + it }
    }

    fun moderateReport(reportId: String, action: String) {
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = action) else it }
        }
    }

    fun resolveReportAndRemoveJob(reportId: String, jobId: String) {
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = "RESOLVED_REMOVED") else it }
        }
        if (jobId.isNotBlank()) {
            _jobs.update { list -> list.filterNot { it.id == jobId } }
        }
    }

    fun dismissReport(reportId: String) {
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(status = "DISMISSED") else it }
        }
    }

    fun markNotificationsAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }
}
