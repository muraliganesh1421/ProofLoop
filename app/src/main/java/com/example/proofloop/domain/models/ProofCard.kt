package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Attempt(
    val attemptNumber: Int,
    val firstAnswer: String = "",
    val interviewAnswer: String = "",
    val solutionAnswer: String = "",
    val followUpAnswer: String = "",
    val correctiveAnswer: String = "",
    val snapshotPath: String? = null,
    val evidenceNote: String = "Queue stalled near cash counter; card tap failure observed.",
    val evidenceTimestamp: String = "12:42 PM — Peak Lunch Shift",
    val evaluation: SkillEvaluation? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class UserSkill(
    val id: String,
    val name: String,
    val score: Int,
    val previousScore: Int = score,
    val targetScore: Int = 85,
    val category: String = "Core Competency"
)

@Serializable
data class ProofCard(
    val id: String = "proof_rcr_level4",
    val title: String = "ROOT-CAUSE REASONING",
    val domain: String = "Product Thinking & Problem Solving",
    val level: String = "LEVEL 4",
    val overallScore: Int = 78,
    val maxScore: Int = 100,
    val applicationScore: Int = 78,
    val reasoningScore: Int = 78,
    val communicationScore: Int = 84,
    val attemptsCount: Int = 12,
    val evidencePointsCount: Int = 23,
    val defendedDecisionsCount: Int = 8,
    val improvementPercent: Int = 17,
    val baselineScore: Int = 61,
    val verifiedScore: Int = 78,
    val strengths: List<String> = listOf(
        "Problem decomposition",
        "Hypothesis testing",
        "Decision reasoning"
    ),
    val growthArea: String = "Evidence gathering",
    val evidenceList: List<String> = listOf(
        "Framed the cafeteria delay around specific operational stages",
        "Conducted real-time student intercept interview",
        "Isolated POS payment latency from food plating latency",
        "Formulated 2 distinct testable hypotheses with metrics",
        "Defended root-cause fix against superficial cashier additions"
    ),
    val verificationHash: String = "PL-2026-IQOO-9941X",
    val issuedDate: String = "September 2026",
    val verificationMessage: String = "ProofLoop measures demonstrated capability, not just course completion."
)
