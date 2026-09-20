package com.example.proofloop.ai

import com.example.proofloop.domain.models.Mission
import com.example.proofloop.domain.models.SkillEvaluation

interface AiService {
    suspend fun evaluateFirstAttempt(
        mission: Mission,
        investigationAnswer: String,
        interviewAnswer: String,
        solutionAnswer: String,
        followUpAnswer: String
    ): Result<SkillEvaluation>

    suspend fun evaluateCorrectiveAttempt(
        mission: Mission,
        previousEvaluation: SkillEvaluation,
        correctiveAnswer: String
    ): Result<SkillEvaluation>
}
