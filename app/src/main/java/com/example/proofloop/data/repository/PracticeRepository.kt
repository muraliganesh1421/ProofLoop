package com.example.proofloop.data.repository

import com.example.proofloop.data.local.LocalProofStorage
import com.example.proofloop.data.local.SubjectContentBank
import com.example.proofloop.domain.models.ProofAttemptRecord
import com.example.proofloop.domain.models.ProofQuestion
import com.example.proofloop.domain.models.QuestionState
import com.example.proofloop.domain.models.QuestionType
import com.example.proofloop.domain.models.SubjectType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuestionEvaluationResult(
    val questionId: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val feedbackMessage: String,
    val explanationText: String,
    val timestamp: String = "Just now"
)

class PracticeRepository {
    companion object {
        @Volatile
        private var INSTANCE: PracticeRepository? = null

        fun getInstance(): PracticeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PracticeRepository().also { INSTANCE = it }
            }
        }
    }

    // Storage reference — injected from MainActivity via initStorage()
    private var storage: LocalProofStorage? = null
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun initStorage(localProofStorage: LocalProofStorage) {
        storage = localProofStorage
    }

    private val _questions = MutableStateFlow<List<ProofQuestion>>(SubjectContentBank.questions)
    val questions: StateFlow<List<ProofQuestion>> = _questions.asStateFlow()

    private val _selectedSubject = MutableStateFlow<SubjectType?>(null)
    val selectedSubject: StateFlow<SubjectType?> = _selectedSubject.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _questionState = MutableStateFlow(QuestionState.QUESTION_NOT_ATTEMPTED)
    val questionState: StateFlow<QuestionState> = _questionState.asStateFlow()

    private val _lastEvaluation = MutableStateFlow<QuestionEvaluationResult?>(null)
    val lastEvaluation: StateFlow<QuestionEvaluationResult?> = _lastEvaluation.asStateFlow()

    // Start at 0 — real count comes from DataStore
    private val _completedQuestionsCount = MutableStateFlow(0)
    val completedQuestionsCount: StateFlow<Int> = _completedQuestionsCount.asStateFlow()

    private val _accuracyPercent = MutableStateFlow(0)
    val accuracyPercent: StateFlow<Int> = _accuracyPercent.asStateFlow()

    // Track hint usage for current question
    private val _hintsUsedForCurrentQuestion = MutableStateFlow(0)
    val hintsUsedForCurrentQuestion: StateFlow<Int> = _hintsUsedForCurrentQuestion.asStateFlow()

    private val _aiUsedForCurrentQuestion = MutableStateFlow(false)
    val aiUsedForCurrentQuestion: StateFlow<Boolean> = _aiUsedForCurrentQuestion.asStateFlow()

    // ── Fix #3: Adaptive Question Delivery ────────────────────────────────────
    // Track consecutive incorrect answers of the same error type (topic).
    // When the student gets 2 in a row on the same topic, inject a remedial
    // micro-question on that topic before resuming the normal sequence.
    private var lastIncorrectTopic: String = ""
    private var consecutiveIncorrectCount: Int = 0
    private var pendingRemedialTopic: String = ""  // non-empty = inject next time
    // ──────────────────────────────────────────────────────────────────────────

    fun markHintUsed() {
        _hintsUsedForCurrentQuestion.value += 1
    }

    fun markAiUsed() {
        _aiUsedForCurrentQuestion.value = true
    }

    fun filterBySubject(subject: SubjectType?) {
        _selectedSubject.value = subject
        val filtered = if (subject == null) {
            SubjectContentBank.questions
        } else {
            SubjectContentBank.questions.filter { it.subject == subject }
        }
        _questions.value = if (filtered.isNotEmpty()) filtered else SubjectContentBank.questions
        _currentQuestionIndex.value = 0
        _questionState.value = QuestionState.QUESTION_NOT_ATTEMPTED
        _lastEvaluation.value = null
        // Reset adaptive delivery state for new session
        lastIncorrectTopic = ""
        consecutiveIncorrectCount = 0
        pendingRemedialTopic = ""
    }

    fun selectQuestion(index: Int) {
        if (index in _questions.value.indices) {
            _currentQuestionIndex.value = index
            _questionState.value = QuestionState.QUESTION_NOT_ATTEMPTED
            _lastEvaluation.value = null
            _hintsUsedForCurrentQuestion.value = 0
            _aiUsedForCurrentQuestion.value = false
        }
    }

    fun submitAnswer(userAnswer: String): Result<QuestionEvaluationResult> {
        val cleanUser = userAnswer.trim()

        // HARD RESTRICTION: Reject empty, blank, or whitespace answers
        if (cleanUser.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter your answer before continuing."))
        }

        val currentQ = _questions.value.getOrNull(_currentQuestionIndex.value)
            ?: return Result.failure(IllegalStateException("Question not found"))

        // Prevent double-submit
        if (_questionState.value != QuestionState.QUESTION_NOT_ATTEMPTED) {
            return Result.failure(IllegalStateException("Already submitted."))
        }

        _questionState.value = QuestionState.QUESTION_SUBMITTING

        val cleanCorrect = currentQ.correctAnswer.trim()

        val isCorrect = when (currentQ.type) {
            QuestionType.MULTIPLE_CHOICE -> cleanUser.equals(cleanCorrect, ignoreCase = true)
            QuestionType.NUMERICAL -> {
                val valUser = cleanUser.toDoubleOrNull()
                val valCorrect = cleanCorrect.toDoubleOrNull()
                if (valUser != null && valCorrect != null) {
                    Math.abs(valUser - valCorrect) < 0.5
                } else {
                    cleanUser.equals(cleanCorrect, ignoreCase = true)
                }
            }
            QuestionType.TEXT_REASONING, QuestionType.CODE -> cleanUser.length >= 8
        }

        val feedback = if (isCorrect) {
            "Verified Correct! Your analytical reasoning aligns with expected competency standards."
        } else {
            "Reasoning Gap Identified. Review the step-by-step breakdown below to master this concept."
        }

        val dateDisplay = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()).format(Date())

        val evalResult = QuestionEvaluationResult(
            questionId = currentQ.id,
            userAnswer = cleanUser,
            isCorrect = isCorrect,
            feedbackMessage = feedback,
            explanationText = currentQ.explanationText,
            timestamp = dateDisplay
        )

        _lastEvaluation.value = evalResult
        _questionState.value = if (isCorrect) QuestionState.CORRECT else QuestionState.INCORRECT

        if (isCorrect) {
            _completedQuestionsCount.value = _completedQuestionsCount.value + 1
            // Correct answer resets the consecutive error streak
            lastIncorrectTopic = ""
            consecutiveIncorrectCount = 0
        } else {
            // ── Fix #3: track consecutive same-topic errors ───────────────────
            val errorTopic = currentQ.topic
            if (errorTopic == lastIncorrectTopic) {
                consecutiveIncorrectCount++
            } else {
                lastIncorrectTopic = errorTopic
                consecutiveIncorrectCount = 1
            }
            // Threshold reached → arm remedial injection for nextQuestion()
            if (consecutiveIncorrectCount >= 2 && pendingRemedialTopic.isEmpty()) {
                pendingRemedialTopic = errorTopic
                consecutiveIncorrectCount = 0   // reset so it only fires once per streak
            }
            // ─────────────────────────────────────────────────────────────────
        }

        // ─── PERSIST ATTEMPT TO DATASTORE ─────────────────────────────────────
        val record = ProofAttemptRecord(
            questionId = currentQ.id,
            subject = currentQ.subject.name,
            topic = currentQ.topic,
            skill = currentQ.skill,
            difficulty = currentQ.difficulty.name,
            userAnswer = cleanUser,
            correctAnswer = cleanCorrect,
            isCorrect = isCorrect,
            hintsUsedCount = _hintsUsedForCurrentQuestion.value,
            aiAssistanceUsed = _aiUsedForCurrentQuestion.value,
            explanationViewed = false,
            timestamp = System.currentTimeMillis(),
            dateDisplay = dateDisplay,
            primaryGap = if (!isCorrect) currentQ.topic else ""
        )

        ioScope.launch {
            storage?.saveAttempt(record)
        }
        // ──────────────────────────────────────────────────────────────────────

        return Result.success(evalResult)
    }

    fun markExplanationViewed() {
        // Could update the last record's explanationViewed flag in a future iteration
    }

    fun unlockExplanation() {
        if (_questionState.value == QuestionState.CORRECT ||
            _questionState.value == QuestionState.INCORRECT ||
            _questionState.value == QuestionState.QUESTION_EVALUATED
        ) {
            _questionState.value = QuestionState.EXPLANATION_AVAILABLE
        }
    }

    fun nextQuestion(): Boolean {
        // NEXT QUESTION MUST BE LOCKED UNTIL SUBMITTED & EVALUATED
        if (_questionState.value == QuestionState.QUESTION_NOT_ATTEMPTED ||
            _questionState.value == QuestionState.QUESTION_SUBMITTING
        ) {
            return false
        }

        _questionState.value = QuestionState.QUESTION_COMPLETED

        // ── Fix #3: Inject remedial micro-question if streak threshold was hit ──
        if (pendingRemedialTopic.isNotEmpty()) {
            val topic = pendingRemedialTopic
            pendingRemedialTopic = ""   // consume the flag immediately

            // Find a remedial question: same topic, not the current question
            val currentQId = _questions.value.getOrNull(_currentQuestionIndex.value)?.id ?: ""
            val remedial = SubjectContentBank.questions.firstOrNull { q ->
                q.topic.contains(topic, ignoreCase = true) && q.id != currentQId
            }
            if (remedial != null) {
                // Temporarily insert the remedial question right after current index
                val currentList = _questions.value.toMutableList()
                val insertAt = (_currentQuestionIndex.value + 1).coerceAtMost(currentList.size)
                // Avoid duplicating if it's already present at insertAt position
                if (currentList.getOrNull(insertAt)?.id != remedial.id) {
                    currentList.add(insertAt, remedial)
                    _questions.value = currentList
                }
                selectQuestion(insertAt)
                return true
            }
        }
        // ──────────────────────────────────────────────────────────────────────

        val nextIdx = (_currentQuestionIndex.value + 1) % _questions.value.size
        selectQuestion(nextIdx)
        return true
    }
}
