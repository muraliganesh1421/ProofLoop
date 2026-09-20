package com.example.proofloop

import com.example.proofloop.ai.DemoAiService
import com.example.proofloop.data.local.SampleData
import com.example.proofloop.domain.scoring.ScoringEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoAiServiceTest {

    private val demoAiService = DemoAiService()

    @Test
    fun testFirstAttemptEvaluationYieldsWeakestPercentageReasoningGap() = runBlocking {
        val result = demoAiService.evaluateFirstAttempt(
            mission = SampleData.defaultMission,
            investigationAnswer = SampleData.sampleAnswerInvestigate,
            interviewAnswer = SampleData.sampleAnswerInterview,
            solutionAnswer = SampleData.sampleAnswerSolution,
            followUpAnswer = SampleData.sampleAnswerFollowUp
        )

        assertTrue(result.isSuccess)
        val eval = result.getOrThrow()

        assertEquals("Mathematical Reasoning", eval.skill)
        assertEquals(61, eval.overallScore)
        assertEquals("Percentage Reasoning", eval.primaryGap)
        assertEquals(55, eval.scores.evidence) // Percentage Reasoning dimension
    }

    @Test
    fun testCorrectiveAttemptEvaluationShows17PercentImprovement() = runBlocking {
        val firstResult = demoAiService.evaluateFirstAttempt(
            mission = SampleData.defaultMission,
            investigationAnswer = SampleData.sampleAnswerInvestigate,
            interviewAnswer = SampleData.sampleAnswerInterview,
            solutionAnswer = SampleData.sampleAnswerSolution,
            followUpAnswer = SampleData.sampleAnswerFollowUp
        )
        val firstEval = firstResult.getOrThrow()

        val correctiveResult = demoAiService.evaluateCorrectiveAttempt(
            mission = SampleData.defaultMission,
            previousEvaluation = firstEval,
            correctiveAnswer = SampleData.sampleAnswerCorrective
        )

        assertTrue(correctiveResult.isSuccess)
        val secondEval = correctiveResult.getOrThrow()

        assertEquals(78, secondEval.overallScore)
        val gain = secondEval.overallScore - firstEval.overallScore
        assertEquals(17, gain) // 61 -> 78 (+17%)

        // Verify percentage gap closed from 55 to 75
        assertEquals(55, firstEval.scores.evidence)
        assertEquals(75, secondEval.scores.evidence)
    }

    @Test
    fun testScoringEngineCalculationConsistency() {
        val before = ScoringEngine.initialAttemptScores
        val after = ScoringEngine.correctiveAttemptScores
        val deltas = ScoringEngine.getScoreComparison(before, after)

        assertEquals(7, deltas.size)
        val primaryGap = deltas.find { it.isPrimaryGap }
        assertTrue(primaryGap != null)
        assertEquals("Percentage Reasoning", primaryGap?.competencyName)
        assertEquals(20, primaryGap?.delta) // 55 -> 75 (+20)
    }
}
