package com.example.proofloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
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
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooOrangeGlow
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.CompetencyScoreBar
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.ProofLoopPrimaryButton
import com.example.proofloop.ui.components.ProofLoopSecondaryButton

@Composable
fun SkillDashboardScreen(
    onHomeClick: () -> Unit,
    onLaptopBridgeClick: () -> Unit,
    onViewProofClick: () -> Unit
) {
    val skills by MissionRepository.userSkills.collectAsState()
    val missionsCompleted by MissionRepository.missionsCompleted.collectAsState()
    val skillsImproved by MissionRepository.skillsImproved.collectAsState()
    val evidenceCollected by MissionRepository.evidenceCollected.collectAsState()
    val defendedDecisions by MissionRepository.defendedDecisions.collectAsState()
    val avgImprovement by MissionRepository.averageImprovement.collectAsState()
    val adaptiveNextMission by MissionRepository.adaptiveNextMission.collectAsState()

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
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PROOF & SKILL TELEMETRY",
                color = IqooOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Demonstrated Capability",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Auditable performance records captured across real-world problem missions.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4 Telemetry Stat Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatTile(
                    title = "MISSIONS",
                    value = "$missionsCompleted",
                    subtitle = "VERIFIED",
                    modifier = Modifier.weight(1f),
                    color = IqooOrange
                )
                StatTile(
                    title = "SKILLS",
                    value = "$skillsImproved",
                    subtitle = "IMPROVED",
                    modifier = Modifier.weight(1f),
                    color = IqooYellow
                )
                StatTile(
                    title = "EVIDENCE",
                    value = "$evidenceCollected",
                    subtitle = "POINTS",
                    modifier = Modifier.weight(1f),
                    color = MintProof
                )
                StatTile(
                    title = "DECISIONS",
                    value = "$defendedDecisions",
                    subtitle = "DEFENDED",
                    modifier = Modifier.weight(1f),
                    color = CyanNeon
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Competency Levels
            Text(
                text = "VERIFIED COMPETENCY LEVELS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    skills.forEach { skill ->
                        CompetencyScoreBar(
                            name = skill.name,
                            score = skill.score,
                            isGap = skill.name.contains("Percentage", ignoreCase = true)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Adaptive Next Mission Card
            Text(
                text = "ADAPTIVE NEXT CHALLENGE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = IqooOrange.copy(alpha = 0.5f),
                backgroundColor = Color(0xFF191614)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AUTOMATICALLY ADAPTED TO YOUR GAP",
                            color = IqooOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${adaptiveNextMission.estimatedMinutes} MIN",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = adaptiveNextMission.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = adaptiveNextMission.subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Verified Credentials / Proof Card Quick Access
            Text(
                text = "OFFICIAL PROOF CREDENTIAL",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewProofClick() },
                borderColor = MintProof.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F2E23)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = MintProof,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MATHEMATICAL REASONING",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Verified: 78% (+17% improvement)",
                                color = MintProof,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "View",
                        tint = MintProof,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProofLoopSecondaryButton(
                text = "LAPTOP BRIDGE",
                icon = Icons.Default.Devices,
                onClick = onLaptopBridgeClick,
                modifier = Modifier.weight(1f)
            )

            ProofLoopPrimaryButton(
                text = "NEW MISSION",
                icon = Icons.Default.Bolt,
                onClick = onHomeClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: Color = CyanNeon
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
