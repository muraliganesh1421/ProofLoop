package com.example.proofloop.data.repository

import com.example.proofloop.ai.AiServiceProvider
import com.example.proofloop.ai.GeminiService
import com.example.proofloop.domain.models.QuestionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

data class AiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: String = "Just now"
)

enum class MessageSender {
    USER,
    ASSISTANT
}

class AIRepository(
    private val aiServiceProvider: AiServiceProvider = AiServiceProvider,
    private val practiceRepository: PracticeRepository = PracticeRepository.getInstance()
) {
    companion object {
        @Volatile
        private var INSTANCE: AIRepository? = null

        fun getInstance(): AIRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AIRepository().also { INSTANCE = it }
            }
        }
    }

    val isDemoMode: StateFlow<Boolean> = aiServiceProvider.isDemoMode
    val customApiKey: StateFlow<String> = aiServiceProvider.customApiKey

    fun setApiKey(key: String) {
        aiServiceProvider.setApiKey(key)
    }

    fun setDemoMode(enabled: Boolean) {
        aiServiceProvider.setDemoMode(enabled)
    }

    suspend fun getHint(
        questionText: String,
        subject: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val state = practiceRepository.questionState.value

        // STRICT GATING: Before submission, provide only Socratic strategy hints
        if (state == QuestionState.QUESTION_NOT_ATTEMPTED || state == QuestionState.QUESTION_SUBMITTING) {
            return@withContext Result.success(
                "💡 Socratic Strategy: Identify the known variables and constraints given in the problem statement. What is being asked? Formulate your initial attempt and submit it to unlock deeper analysis!"
            )
        }

        // Post submission: Use Gemini if available
        val currentQ = practiceRepository.questions.value.getOrNull(practiceRepository.currentQuestionIndex.value)
        val prompt = """
            You are ProofLoop's intelligent learning tutor.
            Subject: $subject
            Question: "$questionText"
            Provide a helpful, targeted pedagogical hint pointing out how to solve this step-by-step.
            Keep the response concise (2-3 sentences max).
        """.trimIndent()

        val geminiResult = callGeminiIfAvailable(prompt)
        if (geminiResult.isSuccess) {
            return@withContext geminiResult
        }

        val fallback = currentQ?.hintText?.takeIf { it.isNotBlank() }
            ?: "Break down the problem into individual components and check unit consistency."
        Result.success("💡 Hint: $fallback")
    }

    suspend fun explainMistake(
        questionText: String,
        userAnswer: String,
        correctAnswer: String,
        explanationContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = """
            You are ProofLoop's AI learning diagnostic tutor.
            Question: "$questionText"
            Student's submitted answer: "$userAnswer"
            Correct answer: "$correctAnswer"
            Concept notes: "$explanationContext"
            
            Provide an empathetic, encouraging mistake analysis explaining why the student's answer was incorrect, what misconception likely occurred, and how to arrive at the correct answer.
            Keep it structured in 2-3 clear sentences.
        """.trimIndent()

        val geminiResult = callGeminiIfAvailable(prompt)
        if (geminiResult.isSuccess) {
            return@withContext geminiResult
        }

        Result.success(
            "Analysis: Your submitted answer was '$userAnswer' while the expected answer is '$correctAnswer'. $explanationContext"
        )
    }

    suspend fun chat(
        userMessage: String,
        topicContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanMsg = userMessage.trim()
        if (cleanMsg.isEmpty()) return@withContext Result.failure(IllegalArgumentException("Message cannot be empty"))

        val state = practiceRepository.questionState.value
        val currentQ = practiceRepository.questions.value.getOrNull(practiceRepository.currentQuestionIndex.value)
        val lastEval = practiceRepository.lastEvaluation.value

        // ABSOLUTE GATING HARD-CHECK: Direct attempts to extract solution before submission
        val isTryingToCheat = cleanMsg.contains("answer", ignoreCase = true) ||
                cleanMsg.contains("solution", ignoreCase = true) ||
                cleanMsg.contains("solve it for me", ignoreCase = true) ||
                cleanMsg.contains("give me the answer", ignoreCase = true) ||
                cleanMsg.contains("what is the answer", ignoreCase = true) ||
                cleanMsg.contains("what is the value", ignoreCase = true) ||
                cleanMsg.contains("which option", ignoreCase = true)

        if (state == QuestionState.QUESTION_NOT_ATTEMPTED && isTryingToCheat) {
            return@withContext Result.success(
                "🔒 ProofLoop Strict Answer-Gating Active:\n\n" +
                "I cannot reveal the answer or complete solution before you submit your initial attempt.\n\n" +
                "• Read the problem statement carefully\n" +
                "• Identify your knowns and unknowns\n" +
                "• Enter your best reasoned solution and press 'Submit Attempt'\n\n" +
                "Once submitted, I will analyze your reasoning, highlight mistakes, and break down the complete derivation."
            )
        }

        // Build context-aware prompt for Gemini
        val gatingInstruction = if (state == QuestionState.QUESTION_NOT_ATTEMPTED) {
            """
            PEDAGOGICAL CONSTRAINT (STRICT):
            The student has NOT yet submitted an attempt for the current question.
            Under NO circumstances should you state the final numerical answer, final option, or write the complete final solution.
            Instead, teach Socratically: explain foundational definitions, ask guiding questions, explain problem-solving strategy, or clarify prerequisites.
            """.trimIndent()
        } else {
            """
            STATUS: The student has submitted their attempt (Status: ${if (lastEval?.isCorrect == true) "CORRECT" else "INCORRECT"}).
            You are free to provide full explanations, analyze errors, verify steps, and show step-by-step mathematical/code derivation.
            """.trimIndent()
        }

        val currentQContext = if (currentQ != null) {
            "Current Question: ${currentQ.questionText}\nSubject: ${currentQ.subject.name}\nTopic: ${currentQ.topic}"
        } else {
            "Topic: $topicContext"
        }

        val prompt = """
            You are ProofLoop's intelligent AI tutor and learning companion.
            $currentQContext
            $gatingInstruction
            
            Student says: "$cleanMsg"
            
            Provide a warm, supportive, insightful, and pedagogical response.
        """.trimIndent()

        val geminiResult = callGeminiIfAvailable(prompt)
        if (geminiResult.isSuccess) {
            return@withContext geminiResult
        }

        // Offline / Demo fallback response with pedagogical depth
        val fallbackReply = when {
            state == QuestionState.QUESTION_NOT_ATTEMPTED ->
                "We're currently exploring $topicContext. To build mastery, consider what variables you can isolate first. What is your hypothesis before submitting?"
            cleanMsg.contains("percentage", ignoreCase = true) || cleanMsg.contains("math", ignoreCase = true) ->
                "When calculating rate increases across a group, apply the multiplier (e.g. 1 + rate) to the individual unit price first, then scale by total headcount."
            cleanMsg.contains("queue", ignoreCase = true) || cleanMsg.contains("dsa", ignoreCase = true) ->
                "In FIFO systems, bottlenecks scale linearly with arrival rates if service rate is bounded. Look at average latency per request."
            cleanMsg.contains("sql", ignoreCase = true) || cleanMsg.contains("join", ignoreCase = true) ->
                "Outer joins preserve all tuples from one side even when the predicate doesn't match, populating missing attributes with NULL."
            else ->
                "I am your ProofLoop AI companion in $topicContext. Let's break down the concepts together! What specific step would you like to explore?"
        }

        Result.success(fallbackReply)
    }

    private suspend fun callGeminiIfAvailable(prompt: String): Result<String> {
        val key = customApiKey.value
        if (key.isNotBlank()) {
            return GeminiService(customApiKey = key).generateText(prompt)
        }
        return GeminiService().generateText(prompt)
    }
}
