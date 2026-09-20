package com.example.proofloop.data.repository

import com.example.proofloop.data.local.SampleData
import com.example.proofloop.domain.models.Attempt
import com.example.proofloop.domain.models.MicroLesson
import com.example.proofloop.domain.models.Mission
import com.example.proofloop.domain.models.ProofCard
import com.example.proofloop.domain.models.SkillEvaluation
import com.example.proofloop.domain.models.UserSkill
import com.example.proofloop.domain.scoring.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MissionRepository {
    private val _allMissions = MutableStateFlow<List<Mission>>(SampleData.allMissions)
    val allMissions: StateFlow<List<Mission>> = _allMissions.asStateFlow()

    private val _currentMission = MutableStateFlow<Mission>(SampleData.defaultMission)
    val currentMission: StateFlow<Mission> = _currentMission.asStateFlow()

    private val _activeAttempt = MutableStateFlow<Attempt>(Attempt(attemptNumber = 1))
    val activeAttempt: StateFlow<Attempt> = _activeAttempt.asStateFlow()

    private val _firstEvaluation = MutableStateFlow<SkillEvaluation?>(null)
    val firstEvaluation: StateFlow<SkillEvaluation?> = _firstEvaluation.asStateFlow()

    private val _secondEvaluation = MutableStateFlow<SkillEvaluation?>(null)
    val secondEvaluation: StateFlow<SkillEvaluation?> = _secondEvaluation.asStateFlow()

    private val _activeMicroLesson = MutableStateFlow<MicroLesson?>(SampleData.microLessonObservationVsAssumption)
    val activeMicroLesson: StateFlow<MicroLesson?> = _activeMicroLesson.asStateFlow()

    private val _adaptiveNextMission = MutableStateFlow<Mission>(SampleData.missionCanteenPricing)
    val adaptiveNextMission: StateFlow<Mission> = _adaptiveNextMission.asStateFlow()

    private val _userSkills = MutableStateFlow<List<UserSkill>>(SampleData.initialSkills)
    val userSkills: StateFlow<List<UserSkill>> = _userSkills.asStateFlow()

    private val _proofCards = MutableStateFlow<List<ProofCard>>(listOf(SampleData.initialProofCard))
    val proofCards: StateFlow<List<ProofCard>> = _proofCards.asStateFlow()

    private val _missionsCompleted = MutableStateFlow(12)
    val missionsCompleted: StateFlow<Int> = _missionsCompleted.asStateFlow()

    private val _skillsImproved = MutableStateFlow(7)
    val skillsImproved: StateFlow<Int> = _skillsImproved.asStateFlow()

    private val _evidenceCollected = MutableStateFlow(23)
    val evidenceCollected: StateFlow<Int> = _evidenceCollected.asStateFlow()

    private val _defendedDecisions = MutableStateFlow(8)
    val defendedDecisions: StateFlow<Int> = _defendedDecisions.asStateFlow()

    private val _averageImprovement = MutableStateFlow(17)
    val averageImprovement: StateFlow<Int> = _averageImprovement.asStateFlow()

    fun selectMission(missionId: String) {
        val found = _allMissions.value.find { it.id == missionId } ?: SampleData.defaultMission
        _currentMission.value = found
        resetMission()
    }

    fun resetMission() {
        _activeAttempt.value = Attempt(attemptNumber = 1)
        _firstEvaluation.value = null
        _secondEvaluation.value = null
    }

    fun updateFirstAnswer(answer: String) {
        _activeAttempt.value = _activeAttempt.value.copy(firstAnswer = answer)
    }

    fun updateSnapshot(path: String, note: String = "Queue stalled near cash counter; card tap failure observed.") {
        _activeAttempt.value = _activeAttempt.value.copy(
            snapshotPath = path,
            evidenceNote = note,
            evidenceTimestamp = "12:42 PM — Peak Lunch Shift"
        )
        _evidenceCollected.value = _evidenceCollected.value + 1
    }

    fun updateInterviewAnswer(answer: String) {
        _activeAttempt.value = _activeAttempt.value.copy(interviewAnswer = answer)
    }

    fun updateSolutionAnswer(answer: String) {
        _activeAttempt.value = _activeAttempt.value.copy(solutionAnswer = answer)
        _defendedDecisions.value = _defendedDecisions.value + 1
    }

    fun updateFollowUpAnswer(answer: String) {
        _activeAttempt.value = _activeAttempt.value.copy(followUpAnswer = answer)
    }

    fun updateCorrectiveAnswer(answer: String) {
        _activeAttempt.value = _activeAttempt.value.copy(correctiveAnswer = answer)
    }

    fun setFirstEvaluation(eval: SkillEvaluation) {
        _firstEvaluation.value = eval
        _activeAttempt.value = _activeAttempt.value.copy(evaluation = eval)

        // Point adaptive next mission to one targeting the gap (e.g. Canteen Pricing for Percentage Reasoning)
        val nextMission = _allMissions.value.find { it.id == eval.adaptiveNextMissionId }
            ?: SampleData.missionCanteenPricing
        _adaptiveNextMission.value = nextMission
    }

    fun setSecondEvaluation(eval: SkillEvaluation) {
        _secondEvaluation.value = eval

        // Update user competencies with explainable gains
        val updated = _userSkills.value.map { skill ->
            when {
                skill.name.contains("Critical", ignoreCase = true) || skill.name.contains("Reasoning", ignoreCase = true) ->
                    skill.copy(score = 78, previousScore = 61)
                skill.name.contains("Evidence", ignoreCase = true) ->
                    skill.copy(score = 75, previousScore = 55)
                skill.name.contains("Communication", ignoreCase = true) ->
                    skill.copy(score = 81, previousScore = 78)
                skill.name.contains("Problem", ignoreCase = true) ->
                    skill.copy(score = 74, previousScore = 70)
                else -> skill
            }
        }
        _userSkills.value = updated
        _missionsCompleted.value = 12
    }
}
