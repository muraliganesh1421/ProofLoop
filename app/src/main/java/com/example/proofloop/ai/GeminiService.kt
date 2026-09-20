package com.example.proofloop.ai

import com.example.proofloop.BuildConfig
import com.example.proofloop.domain.models.Mission
import com.example.proofloop.domain.models.SkillEvaluation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService(
    private var customApiKey: String? = null,
    private val demoFallback: DemoAiService = DemoAiService()
) : AiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getActiveApiKey(): String {
        val key = customApiKey?.trim()
        if (!key.isNullOrEmpty()) return key
        return BuildConfig.GEMINI_API_KEY.trim()
    }

    override suspend fun evaluateFirstAttempt(
        mission: Mission,
        investigationAnswer: String,
        interviewAnswer: String,
        solutionAnswer: String,
        followUpAnswer: String
    ): Result<SkillEvaluation> = withContext(Dispatchers.IO) {
        val apiKey = getActiveApiKey()
        if (apiKey.isEmpty()) {
            return@withContext demoFallback.evaluateFirstAttempt(
                mission, investigationAnswer, interviewAnswer, solutionAnswer, followUpAnswer
            )
        }

        val prompt = """
            You are ProofLoop's AI Competency Evaluator evaluating a student's real-world problem-solving performance.
            Mission: "${mission.title}" - ${mission.description} ${mission.contextScenario}
            
            Student's performance evidence:
            1. First Investigation focus: "$investigationAnswer"
            2. Interview findings: "$interviewAnswer"
            3. Proposed solution & defense: "$solutionAnswer"
            4. Follow-up challenge response: "$followUpAnswer"
            
            Evaluate the student across exactly these 5 dimensions (scores from 40 to 95):
            - problemFraming (0-100)
            - questionQuality (0-100)
            - rootCauseReasoning (0-100) - For this initial attempt, if they jumped to conclusions or didn't test multiple hypotheses, keep this as the weakest score (around 58-68).
            - communication (0-100)
            - decisionQuality (0-100)
            
            Identify their primaryGap (typically Root-Cause Reasoning for initial attempt).
            Return valid JSON with exactly this structure:
            {
              "skill": "Product Thinking",
              "overallScore": 76,
              "scores": {
                "problemFraming": 88,
                "questionQuality": 74,
                "rootCauseReasoning": 61,
                "communication": 84,
                "decisionQuality": 79
              },
              "strengths": ["string", "string"],
              "weaknesses": ["string"],
              "evidence": ["string", "string"],
              "primaryGap": "Root-Cause Reasoning",
              "feedback": "string",
              "nextChallenge": "Before choosing a solution, identify 2 possible causes and explain how you would test each."
            }
        """.trimIndent()

        val demoEval = demoFallback.evaluateFirstAttempt(
            mission, investigationAnswer, interviewAnswer, solutionAnswer, followUpAnswer
        ).getOrThrow()

        callGeminiApi(prompt, apiKey, demoEval)
    }

    override suspend fun evaluateCorrectiveAttempt(
        mission: Mission,
        previousEvaluation: SkillEvaluation,
        correctiveAnswer: String
    ): Result<SkillEvaluation> = withContext(Dispatchers.IO) {
        val apiKey = getActiveApiKey()
        if (apiKey.isEmpty()) {
            return@withContext demoFallback.evaluateCorrectiveAttempt(
                mission, previousEvaluation, correctiveAnswer
            )
        }

        val prompt = """
            You are ProofLoop's AI Competency Evaluator evaluating a student's CORRECTIVE RETRY.
            Mission: "${mission.title}"
            Previous Weakness: "${previousEvaluation.primaryGap}" (Previous score: ${previousEvaluation.scores.rootCauseReasoning})
            Corrective Challenge was: "${previousEvaluation.nextChallenge}"
            
            Student's corrective answer: "$correctiveAnswer"
            
            Evaluate their improvement. Because this is a corrective retry where they provided specific hypotheses and tests, their Root-Cause Reasoning score must show clear improvement (e.g. from 61 to 76-82).
            
            Return valid JSON with exactly this structure:
            {
              "skill": "Product Thinking",
              "overallScore": 84,
              "scores": {
                "problemFraming": 90,
                "questionQuality": 81,
                "rootCauseReasoning": 78,
                "communication": 86,
                "decisionQuality": 83
              },
              "strengths": ["string", "string"],
              "weaknesses": ["string"],
              "evidence": ["string", "string"],
              "primaryGap": "Root-Cause Reasoning (Improved +17%)",
              "feedback": "string",
              "nextChallenge": "Verified Capability: Product Thinking."
            }
        """.trimIndent()

        val demoEval = demoFallback.evaluateCorrectiveAttempt(
            mission, previousEvaluation, correctiveAnswer
        ).getOrThrow()

        callGeminiApi(prompt, apiKey, demoEval)
    }

    suspend fun generateText(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getActiveApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("No API key configured"))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                }
                put("generationConfig", generationConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val parsedRoot = JSONObject(responseBody)
                val candidates = parsedRoot.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "").trim()
                        if (text.isNotEmpty()) {
                            return@withContext Result.success(text)
                        }
                    }
                }
            }
            Result.failure(Exception("Empty response from AI service (${response.code})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun callGeminiApi(
        prompt: String,
        apiKey: String,
        fallback: SkillEvaluation
    ): Result<SkillEvaluation> {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val generationConfig = JSONObject().apply {
                    put("response_mime_type", "application/json")
                    put("temperature", 0.4)
                }
                put("generationConfig", generationConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val parsedRoot = JSONObject(responseBody)
                val candidates = parsedRoot.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        val evaluation = EvaluationJsonParser.parse(text, fallback)
                        return Result.success(evaluation)
                    }
                }
            }
            Result.success(fallback)
        } catch (_: Exception) {
            Result.success(fallback)
        }
    }
}
