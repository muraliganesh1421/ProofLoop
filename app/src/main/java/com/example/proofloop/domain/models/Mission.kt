package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Mission(
    val id: String,
    val title: String,
    val domain: String,
    val subject: String = "MATHEMATICS",
    val subtitle: String,
    val description: String,
    val contextScenario: String,
    val estimatedMinutes: Int = 5,
    val difficulty: String = "Intermediate",
    val primaryCompetency: String = "Mathematical Reasoning",
    val targetGap: String = "Percentage Reasoning",
    val rules: List<String> = listOf(
        "Observe the key constraints",
        "Calculate total expenditure",
        "Test dynamic what-if condition",
        "Defend your final decision"
    ),
    val firstQuestion: String = "What key variables and constraints do you identify?",
    val interviewPrompt: String = "Interview an organizer or stakeholder. What did they report?",
    val defendPrompt: String = "Give one solution and explain why it addresses the root constraint.",
    val followUpQuestion: String = "If prices increase by 12%, does your proposal still hold?",
    val correctiveChallenge: String = "Calculate the exact percentage impact and explain your trade-off before deciding.",
    val sampleEvidenceNote: String = "Food vendor quote confirmed: Rs 85/student for 240 students.",
    val isMathMission: Boolean = true
)
