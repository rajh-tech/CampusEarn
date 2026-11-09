package com.example.data.model

enum class UserRole(val displayName: String) {
    STUDENT("Student"),
    EMPLOYER("Employer"),
    BOTH("Student & Employer"),
    ADMIN("Admin Moderator")
}

enum class ActiveMode {
    STUDENT,
    EMPLOYER,
    ADMIN
}

enum class JobCategory(val id: String, val title: String, val iconName: String) {
    GRAPHIC_DESIGNING("cat_gd", "Graphic Designing", "Palette"),
    CONTENT_WRITING("cat_cw", "Content Writing", "Edit"),
    DATA_ENTRY("cat_de", "Data Entry", "Table"),
    WEB_DEVELOPMENT("cat_wd", "Web Development", "Code"),
    VIDEO_EDITING("cat_ve", "Video Editing", "Video"),
    SOCIAL_MEDIA("cat_sm", "Social Media Management", "Share2"),
    ONLINE_TUTORING("cat_ot", "Online Tutoring", "BookOpen"),
    CAMPUS_AMBASSADOR("cat_ca", "Campus Ambassador", "Award"),
    EVENT_MANAGEMENT("cat_em", "Event Management", "Calendar"),
    OFFLINE_JOBS("cat_oj", "Part-time Offline Jobs", "MapPin")
}

enum class WorkType(val label: String) {
    ONLINE("Online / Remote"),
    OFFLINE("Offline / On-Campus")
}

enum class PaymentType(val label: String) {
    FIXED("Fixed Pay"),
    HOURLY("Hourly Pay")
}

enum class JobStatus {
    ACTIVE,
    CLOSED,
    COMPLETED
}

enum class ApplicationStatus(val label: String) {
    PENDING("Pending"),
    SHORTLISTED("Shortlisted"),
    ACCEPTED("Accepted / Hired"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn")
}

enum class WorkSubmissionStatus(val label: String) {
    PENDING_REVIEW("Under Review"),
    CHANGES_REQUESTED("Revision Requested"),
    APPROVED("Approved & Completed")
}

enum class PaymentStatus(val label: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    PAID("Paid / Credited"),
    FAILED("Failed"),
    REFUNDED("Refunded")
}

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val collegeName: String,
    val course: String,
    val city: String,
    val state: String,
    val graduationYear: String,
    val bio: String,
    val avatarUrl: String = "",
    val roles: Set<UserRole> = setOf(UserRole.STUDENT),
    val isVerified: Boolean = true,
    val skills: List<String> = emptyList(),
    val portfolioUrl: String = "",
    val companyName: String = "",
    val businessDescription: String = "",
    val completedJobsCount: Int = 0,
    val averageRating: Double = 4.8,
    val createdAt: String = "2026-08-15"
) {
    fun calculateCompletionPercentage(): Int {
        var score = 0
        if (fullName.isNotBlank()) score += 10
        if (email.isNotBlank()) score += 10
        if (phone.isNotBlank()) score += 10
        if (collegeName.isNotBlank()) score += 15
        if (course.isNotBlank()) score += 10
        if (graduationYear.isNotBlank()) score += 10
        if (city.isNotBlank() && state.isNotBlank()) score += 10
        if (bio.isNotBlank()) score += 15
        if (skills.isNotEmpty()) score += 10
        if (portfolioUrl.isNotBlank()) score += 10
        return score.coerceAtMost(100)
    }
}

data class SupportedLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val script: String
)

data class JobPost(
    val id: String,
    val employerId: String,
    val employerName: String,
    val employerAvatar: String = "",
    val isEmployerVerified: Boolean = true,
    val employerRating: Double = 4.9,
    val title: String,
    val category: JobCategory,
    val description: String,
    val responsibilities: List<String> = emptyList(),
    val requiredSkills: List<String>,
    val workType: WorkType,
    val location: String, // e.g. "Pan-India (Remote)" or "Delhi NCR, Connaught Place"
    val paymentAmount: Double, // in INR ₹
    val paymentType: PaymentType = PaymentType.FIXED,
    val duration: String, // e.g. "2 Weeks", "1 Month", "3 Days"
    val openings: Int = 1,
    val deadline: String = "Nov 15, 2026",
    val status: JobStatus = JobStatus.ACTIVE,
    val applicantsCount: Int = 0,
    val postedDate: String = "2 hours ago",
    val isFeatured: Boolean = false
)

data class JobApplication(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val studentId: String,
    val studentName: String,
    val studentCollege: String,
    val studentCourse: String,
    val studentPhone: String = "",
    val studentGraduationYear: String = "",
    val studentRating: Double = 4.8,
    val studentAvatar: String = "",
    val introduction: String,
    val skills: List<String>,
    val portfolioUrl: String = "",
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val appliedDate: String = "Oct 28, 2026"
)

data class WorkSubmission(
    val id: String,
    val applicationId: String,
    val jobId: String,
    val jobTitle: String,
    val studentId: String,
    val studentName: String,
    val submissionUrl: String,
    val notes: String,
    val status: WorkSubmissionStatus = WorkSubmissionStatus.PENDING_REVIEW,
    val submittedAt: String = "Just now",
    val feedback: String = ""
)

data class PaymentRecord(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val payerId: String,
    val payerName: String,
    val receiverId: String,
    val receiverName: String,
    val amount: Double, // ₹
    val paymentMethod: String, // "UPI (GPay / PhonePe)", "IMPS Bank Transfer"
    val transactionReference: String,
    val status: PaymentStatus,
    val createdAt: String
)

data class ReviewItem(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val reviewerId: String,
    val reviewerName: String,
    val revieweeId: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val createdAt: String
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

data class ConversationItem(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val studentId: String,
    val studentName: String,
    val studentCollege: String,
    val employerId: String,
    val employerName: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0
)

data class NotificationItem(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: String,
    val category: String = "General"
)

data class ReportItem(
    val id: String,
    val reporterId: String,
    val reporterName: String,
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val reason: String,
    val description: String,
    val status: String = "PENDING_MODERATION",
    val createdAt: String
)
