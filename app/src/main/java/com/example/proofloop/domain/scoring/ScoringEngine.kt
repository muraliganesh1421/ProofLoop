package com.example.proofloop.domain.scoring

import com.example.proofloop.domain.models.CompetencyScores

object ScoringEngine {

    val initialAttemptScores = CompetencyScores(
        observation = 72,      // Observation & Constraint Gathering
        evidence = 55,         // Percentage Reasoning (Identified Primary Gap)
        hypothesis = 60,       // Mathematical Method
        testing = 65,          // Calculation Accuracy
        reasoning = 72,        // Mathematical Reasoning
        communication = 68,    // Defense & Explanation
        decisionMaking = 62    // Trade-off Decision
    )

    val correctiveAttemptScores = CompetencyScores(
        observation = 82,
        evidence = 75,         // Percentage Reasoning closed: 55 -> 75
        hypothesis = 76,
        testing = 86,          // Calculation Accuracy jumps to 86
        reasoning = 84,        // Mathematical Reasoning jumps to 84
        communication = 81,    // Defense jumps to 81
        decisionMaking = 71    // Trade-off Decision jumps to 71
    )

    val whyYouImprovedReasons = listOf(
        "You calculated the exact 12% price shock on the total 240 student volume.",
        "You formulated measurable line-item trade-offs instead of arbitrary budget cuts.",
        "You defended your decision with clear mathematical constraints."
    )

    fun calculateWeightedScore(scores: CompetencyScores): Int {
        return scores.calculateWeightedScore()
    }

    fun calculateImprovement(before: CompetencyScores, after: CompetencyScores): Int {
        return calculateWeightedScore(after) - calculateWeightedScore(before)
    }

    fun getScoreComparison(before: CompetencyScores, after: CompetencyScores): List<ScoreDelta> {
        return listOf(
            ScoreDelta("Constraint Observation", before.observation, after.observation, 0.20),
            ScoreDelta("Percentage Reasoning", before.evidence, after.evidence, 0.20, isPrimaryGap = true),
            ScoreDelta("Mathematical Method", before.hypothesis, after.hypothesis, 0.15),
            ScoreDelta("Calculation Accuracy", before.testing, after.testing, 0.15),
            ScoreDelta("Mathematical Reasoning", before.reasoning, after.reasoning, 0.15),
            ScoreDelta("Explanation & Defense", before.communication, after.communication, 0.10),
            ScoreDelta("Trade-off Decision", before.decisionMaking, after.decisionMaking, 0.05)
        )
    }
}

data class ScoreDelta(
    val competencyName: String,
    val beforeScore: Int,
    val afterScore: Int,
    val weight: Double,
    val isPrimaryGap: Boolean = false
) {
    val delta: Int get() = afterScore - beforeScore
}
