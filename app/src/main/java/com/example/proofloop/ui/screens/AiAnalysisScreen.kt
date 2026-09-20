package com.example.proofloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.ai.AiServiceProvider
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.theme.BgCardElevated
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanGlow
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.StageProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun AiAnalysisScreen(
    isRetry: Boolean,
    onCompleted: () -> Unit
) {
    var stage by remember { mutableIntStateOf(0) }
    val mission by MissionRepository.currentMission.collectAsState()
    val attempt by MissionRepository.activeAttempt.collectAsState()
    val firstEval by MissionRepository.firstEvaluation.collectAsState()

    LaunchedEffect(Unit) {
        // Run sequential progress stages
        delay(400)
        stage = 1 // Analyzing response
        delay(450)
        stage = 2 // Mapping competencies
        delay(450)
        stage = 3 // Preparing feedback

        val service = AiServiceProvider.getService()
        if (!isRetry) {
            val result = service.evaluateFirstAttempt(
                mission = mission,
                investigationAnswer = attempt.firstAnswer,
                interviewAnswer = attempt.interviewAnswer,
                solutionAnswer = attempt.solutionAnswer,
                followUpAnswer = attempt.followUpAnswer
            )
            result.onSuccess { eval ->
                MissionRepository.setFirstEvaluation(eval)
            }.onFailure {
                // Fallback guaranteed by DemoAiService
            }
        } else {
            val prev = firstEval ?: com.example.proofloop.data.local.SampleData.initialProofCard.let {
                com.example.proofloop.domain.models.SkillEvaluation()
            }
            val result = service.evaluateCorrectiveAttempt(
                mission = mission,
                previousEvaluation = prev,
                correctiveAnswer = attempt.correctiveAnswer
            )
            result.onSuccess { eval ->
                MissionRepository.setSecondEvaluation(eval)
            }
        }

        stage = 4
        delay(300)
        onCompleted()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(28.dp)
        ) {
            // Pulse Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CyanGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (!isRetry) "Analyzing your reasoning..." else "Evaluating your corrective proof...",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Comparing captured evidence against 5 core competencies",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(modifier = Modifier.padding(horizontal = 4.dp)) {
                StageProgressIndicator(currentStage = stage)
            }
        }
    }
}
