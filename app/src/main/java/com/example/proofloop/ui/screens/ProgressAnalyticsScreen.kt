package com.example.proofloop.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.local.LocalProofStorage
import com.example.proofloop.domain.models.SubjectType
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

private data class SkillPerfItem(
    val name: String,
    val percentage: Int,
    val barColor: Color,
    val iconColor: Color,
    val iconBg: Color,
    val icon: ImageVector
)

@Composable
fun ProgressAnalyticsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val storage = remember { LocalProofStorage.getInstance(context) }
    val allAttempts by storage.attemptsFlow.collectAsState(initial = emptyList())

    val totalAttempts = allAttempts.size
    val correctCount = allAttempts.count { it.isCorrect }
    val liveScore = if (totalAttempts > 0) (correctCount * 100 / totalAttempts) else 68

    var selectedTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Skills", "Performance")

    val skillPerformances = listOf(
        SkillPerfItem("Algebra", 80, ProofLoopYellow, Color(0xFFFF5252), Color(0xFF2D1416), Icons.Default.Calculate),
        SkillPerfItem("Geometry", 65, Color(0xFF29B6F6), Color(0xFF29B6F6), Color(0xFF0F2636), Icons.Default.Functions),
        SkillPerfItem("Calculus", 50, Color(0xFFAB47BC), Color(0xFFAB47BC), Color(0xFF28132F), Icons.Default.Functions),
        SkillPerfItem("Trigonometry", 45, Color(0xFFEC407A), Color(0xFFFF7043), Color(0xFF331B13), Icons.Default.Calculate)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Progress",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Track your growth. Stay motivated.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Tab Row (Pill Tabs) ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ProofLoopYellow else ProofLoopCardBg)
                        .border(
                            1.dp,
                            if (isSelected) ProofLoopYellow else ProofLoopCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 18.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Overall Progress Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ProofLoopCardBg)
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Circular Progress Ring
                    ProgressCircleRing(progress = liveScore, size = 80)

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Text(
                            text = "Overall Progress",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "↑ 12% this week",
                                color = MintProof,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom 3 metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "12", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Skills Learned", color = TextMuted, fontSize = 11.sp)
                    }
                    Column {
                        Text(text = "8", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Missions Completed", color = TextMuted, fontSize = 11.sp)
                    }
                    Column {
                        Text(text = "24", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Day Streak", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // ── Skill Performance Section ──
        Text(
            text = "Skill Performance",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            skillPerformances.forEach { skill ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(skill.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = skill.icon,
                            contentDescription = null,
                            tint = skill.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = skill.name,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${skill.percentage}%",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { skill.percentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp)),
                            color = skill.barColor,
                            trackColor = Color(0xFF1E2838)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCircleRing(progress: Int, size: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(800),
        label = "overallRing"
    )

    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 7.dp.toPx()
            val radius = (this.size.minDimension - strokeW) / 2
            val center = androidx.compose.ui.geometry.Offset(this.size.width / 2, this.size.height / 2)

            drawArc(
                color = Color(0xFF1E2838),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            drawArc(
                color = ProofLoopYellow,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$progress%",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
