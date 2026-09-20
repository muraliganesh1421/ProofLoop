package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class EvidenceType {
    CAMERA,
    UPLOAD,
    MANUAL,
    PROVIDED
}

@Serializable
data class EvidenceItem(
    val id: String,
    val missionId: String,
    val type: EvidenceType,
    val contentUriOrText: String,
    val description: String,
    val timestamp: String,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class MistakeItem(
    val id: String,
    val mistakeTitle: String,
    val whatHappened: String,
    val competencyAffected: String,
    val whyItMatters: String,
    val recommendedCorrection: String,
    val occurrenceCount: Int = 1
)

@Serializable
data class HistoryRecord(
    val id: String,
    val missionId: String,
    val missionTitle: String,
    val subject: String,
    val attemptNumber: Int,
    val score: Int,
    val previousScore: Int? = null,
    val competencyScores: CompetencyScores,
    val evidenceList: List<EvidenceItem>,
    val explanation: String,
    val primaryGap: String,
    val mistakes: List<MistakeItem>,
    val strengths: List<String>,
    val dateDisplay: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class UserSession(
    val userId: String = "student_demo",
    val name: String = "Murali",
    val email: String = "murali.student@iqoo.edu",
    val isDemoUser: Boolean = true,
    val currentStreakDays: Int = 4,
    val totalMissionsCompleted: Int = 12,
    val totalSkillsImproved: Int = 7
)
