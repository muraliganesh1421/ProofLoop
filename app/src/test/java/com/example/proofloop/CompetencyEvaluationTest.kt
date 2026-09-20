package com.example.proofloop

import com.example.proofloop.ai.EvaluationJsonParser
import com.example.proofloop.domain.models.CompetencyScores
import com.example.proofloop.domain.models.SkillEvaluation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CompetencyEvaluationTest {

    @Test
    fun testCompetencyScoresWeakestCalculation() {
        val scores = CompetencyScores(
            observation = 82,
            evidence = 55, // Percentage Reasoning (lowest score)
            hypothesis = 72,
            testing = 77,
            reasoning = 84,
            communication = 81,
            decisionMaking = 69
        )

        val weakest = scores.getWeakestCompetency()
        assertEquals("Percentage Reasoning", weakest.first)
        assertEquals(55, weakest.second)
    }

    @Test
    fun testJsonParserWithMarkdownWrapping() {
        val rawJsonWithMarkdown = """
            ```json
            {
              "skill": "Mathematical Reasoning",
              "overallScore": 77,
              "scores": {
                "problemFraming": 82,
                "questionQuality": 55,
                "rootCauseReasoning": 72,
                "communication": 81,
                "decisionQuality": 69
              },
              "strengths": ["Clear breakdown of the festival budget constraints"],
              "weaknesses": ["Missed compounding volume effect of price shock"],
              "evidence": ["Did not multiply unit price hike across 240 attendees"],
              "primaryGap": "Percentage Reasoning",
              "feedback": "Your baseline arithmetic was accurate, but percentage shocks require volume compounding.",
              "nextChallenge": "Before choosing a solution, calculate the 12% price hike on 240 students."
            }
            ```
        """.trimIndent()

        val fallback = SkillEvaluation(
            skill = "Fallback Skill",
            overallScore = 60,
            scores = CompetencyScores(),
            strengths = emptyList(),
            weaknesses = emptyList(),
            evidence = emptyList(),
            primaryGap = "Unknown",
            feedback = "",
            nextChallenge = ""
        )

        val parsed = EvaluationJsonParser.parse(rawJsonWithMarkdown, fallback)
        assertNotNull(parsed)
        assertEquals("Mathematical Reasoning", parsed.skill)
        assertEquals(77, parsed.overallScore)
        assertEquals("Percentage Reasoning", parsed.primaryGap)
        assertEquals(82, parsed.scores.problemFraming)
        assertEquals(55, parsed.scores.questionQuality)
    }
}
