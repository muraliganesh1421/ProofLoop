package com.example.proofloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.theme.AmberGap
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.ProofLoopPrimaryButton

@Composable
fun SkillDetailScreen(
    skillId: String,
    onBackClick: () -> Unit,
    onPracticeMission: (String) -> Unit
) {
    val skills by MissionRepository.userSkills.collectAsState()
    val skill = skills.find { it.id == skillId } ?: skills.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SKILL PROFILE DETAIL",
                color = IqooOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Score
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = IqooOrange.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF181514)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = skill.name,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${skill.category} Track",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${skill.score}%",
                            color = if (skill.score >= 75) MintProof else IqooYellow,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Target: ${skill.targetScore}%",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Growth progression
            Text(
                text = "VERIFIED GROWTH CURVE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ProgressionStep(attempt = "Baseline Attempt 1", score = "55%", delta = "Initial Gap Identified", color = AmberGap)
                    Spacer(modifier = Modifier.height(8.dp))
                    ProgressionStep(attempt = "Corrective Attempt 2", score = "75%", delta = "+20% Demonstrated Gain", color = MintProof)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recurring Mistakes in this skill
            Text(
                text = "RECURRING MISTAKE AUDIT",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AmberGap.copy(alpha = 0.4f)
            ) {
                Text(
                    text = "• In 2 attempts, price adjustment was calculated per individual unit instead of being multiplied across the 240 attendee volume.",
                    color = Color(0xFFFED7AA),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recommended Next Mission
            Text(
                text = "RECOMMENDED PRACTICE MISSION",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyanNeon.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Canteen Pricing & Margins",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tests break-even calculation under 18% ingredient cost hike.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        ProofLoopPrimaryButton(
            text = "START RECOMMENDED MISSION",
            icon = Icons.Default.PlayArrow,
            onClick = { onPracticeMission("mission_canteen_pricing") }
        )
    }
}

@Composable
private fun ProgressionStep(
    attempt: String,
    score: String,
    delta: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = attempt,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = delta,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = score,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
