package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class SubjectType {
    MATHEMATICS,
    PHYSICS,
    PROGRAMMING,
    DATA_STRUCTURES,
    DBMS,
    COMPUTER_SCIENCE,
    APTITUDE
}

@Serializable
enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

@Serializable
enum class QuestionType {
    MULTIPLE_CHOICE,
    NUMERICAL,
    TEXT_REASONING,
    CODE
}

@Serializable
enum class QuestionState {
    QUESTION_NOT_ATTEMPTED,
    QUESTION_SUBMITTING,
    QUESTION_EVALUATED,
    CORRECT,
    INCORRECT,
    EXPLANATION_AVAILABLE,
    QUESTION_COMPLETED
}

@Serializable
data class ProofQuestion(
    val id: String,
    val subject: SubjectType,
    val topic: String,
    val skill: String,
    val difficulty: DifficultyLevel,
    val questionText: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanationText: String,
    val hintText: String,
    val solutionText: String,
    val tags: List<String> = emptyList(),
    val estimatedTimeMinutes: Int = 3,
    val learningObjective: String = "Master core reasoning"
)

@Serializable
data class ProofAttemptRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val questionId: String,
    val subject: String,
    val topic: String,
    val skill: String = "",
    val difficulty: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val hintsUsedCount: Int = 0,
    val aiAssistanceUsed: Boolean = false,
    val explanationViewed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val dateDisplay: String = "Today",
    val primaryGap: String = "",
    val missionId: String = ""
)
