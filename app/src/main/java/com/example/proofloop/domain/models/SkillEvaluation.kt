package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SkillEvaluation(
    val skill: String = "Product Thinking",
    val overallScore: Int = 0,
    val scores: CompetencyScores = CompetencyScores(),
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val evidence: List<String> = emptyList(),
    val primaryGap: String = "Evidence Gathering",
    val feedback: String = "You moved toward a solution before validating multiple possible causes with on-the-ground evidence.",
    val nextChallenge: String = "Before choosing a solution, identify 2 possible causes and explain how you would test each.",
    val whyYouImproved: List<String> = listOf(
        "You gathered photo and interview evidence before deciding.",
        "You considered multiple distinct operational causes.",
        "You formulated measurable, testable hypotheses."
    ),
    val microLesson: MicroLesson? = null,
    val adaptiveNextMissionId: String = "mission_campus_lost_found"
)
