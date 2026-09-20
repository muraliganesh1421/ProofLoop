package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CompetencyScores(
    // 7 Core Evaluated Competencies for Performance & Academic Learning
    val observation: Int = 72,       // Constraint Observation (20%)
    val evidence: Int = 55,          // Percentage Reasoning / Evidence (20%)
    val hypothesis: Int = 60,        // Mathematical Method (15%)
    val testing: Int = 65,           // Calculation Accuracy (15%)
    val reasoning: Int = 72,         // Mathematical Reasoning (15%)
    val communication: Int = 68,     // Explanation & Defense (10%)
    val decisionMaking: Int = 62,    // Solution & Trade-offs (5%)

    // Backward-compatible dimension aliases
    val problemFraming: Int = observation,
    val questionQuality: Int = evidence,
    val rootCauseReasoning: Int = if (evidence < reasoning) evidence else reasoning,
    val decisionQuality: Int = decisionMaking
) {
    /**
     * Transparent, explainable weighted scoring formula:
     * Constraint Observation: 20%
     * Percentage Reasoning:   20%
     * Mathematical Method:    15%
     * Calculation Accuracy:   15%
     * Mathematical Reasoning: 15%
     * Explanation & Defense:  10%
     * Solution & Trade-offs:   5%
     */
    fun calculateWeightedScore(): Int {
        val weighted = (observation * 0.20) +
                (evidence * 0.20) +
                (hypothesis * 0.15) +
                (testing * 0.15) +
                (reasoning * 0.15) +
                (communication * 0.10) +
                (decisionMaking * 0.05)
        return kotlin.math.round(weighted).toInt()
    }

    fun average(): Int = calculateWeightedScore()

    fun getWeakestCompetency(): Pair<String, Int> {
        val list = listOf(
            "Percentage Reasoning" to evidence,
            "Mathematical Method" to hypothesis,
            "Trade-off Decision" to decisionMaking,
            "Calculation Accuracy" to testing,
            "Explanation & Defense" to communication,
            "Constraint Observation" to observation,
            "Mathematical Reasoning" to reasoning
        )
        return list.minByOrNull { it.second } ?: ("Percentage Reasoning" to evidence)
    }
}
