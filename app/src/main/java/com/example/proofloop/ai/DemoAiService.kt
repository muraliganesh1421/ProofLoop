package com.example.proofloop.ai

import com.example.proofloop.data.local.SampleData
import com.example.proofloop.domain.models.CompetencyScores
import com.example.proofloop.domain.models.Mission
import com.example.proofloop.domain.models.SkillEvaluation
import com.example.proofloop.domain.scoring.ScoringEngine
import kotlinx.coroutines.delay

class DemoAiService : AiService {

    override suspend fun evaluateFirstAttempt(
        mission: Mission,
        investigationAnswer: String,
        interviewAnswer: String,
        solutionAnswer: String,
        followUpAnswer: String
    ): Result<SkillEvaluation> {
        // Realistic processing delay (1.2s) to simulate on-device/cloud AI reasoning
        delay(1200)

        val scores = ScoringEngine.initialAttemptScores
        val overall = scores.calculateWeightedScore() // 64 -> 61 display

        val eval = SkillEvaluation(
            skill = if (mission.isMathMission) "Mathematical Reasoning" else "Root-Cause Reasoning",
            overallScore = 61,
            scores = scores,
            strengths = listOf(
                "Identified the basic budget constraints (Decoration, Transport, Food)",
                "Acknowledged that total expenditure exceeded the Rs 12,000 limit"
            ),
            weaknesses = listOf(
                "Overlooked the volume multiplier of price adjustments on 240 students",
                "Cut line items arbitrarily without calculating percentage trade-offs",
                "Did not formulate a robust reserve for food price surges"
            ),
            evidence = listOf(
                "Fixed costs totaled Rs 4,700, leaving only Rs 7,300 for food",
                "Food alone was calculated at Rs 20,400 (Rs 85 x 240)",
                "Solution assumed vendor discount without validating contractual minimums"
            ),
            primaryGap = "Percentage Reasoning",
            feedback = "You structured the problem clearly, but missed how small percentage fluctuations compound across large unit volumes. Your primary competency gap is Percentage Reasoning.",
            nextChallenge = "Calculate the exact impact of a 12% price increase on 240 students and defend 2 balanced trade-offs.",
            whyYouImproved = emptyList(),
            microLesson = SampleData.microLessonObservationVsAssumption,
            adaptiveNextMissionId = "mission_canteen_pricing"
        )
        return Result.success(eval)
    }

    override suspend fun evaluateCorrectiveAttempt(
        mission: Mission,
        previousEvaluation: SkillEvaluation,
        correctiveAnswer: String
    ): Result<SkillEvaluation> {
        // Believable processing delay (1.2s)
        delay(1200)

        val scores = ScoringEngine.correctiveAttemptScores
        val overall = scores.calculateWeightedScore() // 78

        val eval = SkillEvaluation(
            skill = if (mission.isMathMission) "Mathematical Reasoning" else "Root-Cause Reasoning",
            overallScore = 78,
            scores = scores,
            strengths = listOf(
                "Calculated exact 12% food shock: Rs 20,400 -> Rs 22,848 (+Rs 2,448)",
                "Demonstrated rigorous mathematical reasoning with balanced line-item offsets",
                "Defended realistic trade-offs maintaining zero budget deficit"
            ),
            weaknesses = listOf(
                "Could build a 3% contingency buffer for unforeseen beverage logistics"
            ),
            evidence = listOf(
                "Calculated per-head buffet conversion to Rs 45/student",
                "Offset decoration savings (-Rs 2,200) directly into food buffer",
                "Eliminated printed passes, saving Rs 400 in event administrative costs"
            ),
            primaryGap = "Percentage Reasoning (Closed: 55 -> 75)",
            feedback = "Outstanding improvement! You applied precise percentage calculations to the entire student volume and defended a mathematically sound, deficit-free budget.",
            nextChallenge = "Verified Capability Achieved: Mathematical Reasoning (Level 4).",
            whyYouImproved = ScoringEngine.whyYouImprovedReasons,
            microLesson = null,
            adaptiveNextMissionId = "mission_canteen_pricing"
        )
        return Result.success(eval)
    }
}
