package com.example.proofloop.ai

import com.example.proofloop.domain.models.CompetencyScores
import com.example.proofloop.domain.models.SkillEvaluation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object EvaluationJsonParser {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun parse(rawText: String, fallback: SkillEvaluation): SkillEvaluation {
        return try {
            val cleaned = cleanJsonString(rawText)
            val jsonElement = json.parseToJsonElement(cleaned).jsonObject

            val skill = jsonElement["skill"]?.jsonPrimitive?.contentOrNull ?: fallback.skill
            val overallScore = jsonElement["overallScore"]?.jsonPrimitive?.intOrNull ?: fallback.overallScore

            val scoresObj = jsonElement["scores"]?.jsonObject
            val scores = if (scoresObj != null) {
                CompetencyScores(
                    problemFraming = scoresObj["problemFraming"]?.jsonPrimitive?.intOrNull ?: fallback.scores.problemFraming,
                    questionQuality = scoresObj["questionQuality"]?.jsonPrimitive?.intOrNull ?: fallback.scores.questionQuality,
                    rootCauseReasoning = scoresObj["rootCauseReasoning"]?.jsonPrimitive?.intOrNull ?: fallback.scores.rootCauseReasoning,
                    communication = scoresObj["communication"]?.jsonPrimitive?.intOrNull ?: fallback.scores.communication,
                    decisionQuality = scoresObj["decisionQuality"]?.jsonPrimitive?.intOrNull ?: fallback.scores.decisionQuality
                )
            } else {
                fallback.scores
            }

            val strengths = jsonElement["strengths"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
                ?: fallback.strengths
            val weaknesses = jsonElement["weaknesses"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
                ?: fallback.weaknesses
            val evidence = jsonElement["evidence"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
                ?: fallback.evidence

            val primaryGap = jsonElement["primaryGap"]?.jsonPrimitive?.contentOrNull ?: fallback.primaryGap
            val feedback = jsonElement["feedback"]?.jsonPrimitive?.contentOrNull ?: fallback.feedback
            val nextChallenge = jsonElement["nextChallenge"]?.jsonPrimitive?.contentOrNull ?: fallback.nextChallenge

            SkillEvaluation(
                skill = skill,
                overallScore = if (overallScore > 0) overallScore else scores.average(),
                scores = scores,
                strengths = strengths.ifEmpty { fallback.strengths },
                weaknesses = weaknesses.ifEmpty { fallback.weaknesses },
                evidence = evidence.ifEmpty { fallback.evidence },
                primaryGap = primaryGap,
                feedback = feedback,
                nextChallenge = nextChallenge
            )
        } catch (_: Exception) {
            fallback
        }
    }

    private fun cleanJsonString(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```json")) {
            text = text.removePrefix("```json")
        } else if (text.startsWith("```")) {
            text = text.removePrefix("```")
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```")
        }
        text = text.trim()
        val startIndex = text.indexOf('{')
        val endIndex = text.lastIndexOf('}')
        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            text.substring(startIndex, endIndex + 1)
        } else {
            text
        }
    }
}
