package com.example.proofloop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.local.LocalProofStorage
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

private data class MissionCardModel(
    val id: String,
    val title: String,
    val description: String,
    val statusText: String,
    val currentProgress: Int,
    val totalProgress: Int,
    val xp: Int,
    val iconColor: Color,
    val iconBg: Color,
    val icon: ImageVector
)

@Composable
fun MissionsScreen(
    onSelectMissionDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val storage = remember { LocalProofStorage.getInstance(context) }
    val allAttempts by storage.attemptsFlow.collectAsState(initial = emptyList())
    val allMissions by MissionRepository.allMissions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Active") }

    val filterTabs = listOf("All", "Active", "Completed", "Upcoming")

    val missionsList = listOf(
        MissionCardModel(
            id = "mission_math_dynamic_pricing",
            title = "Algebra Master",
            description = "Solve 10 linear equation problems",
            statusText = "In Progress",
            currentProgress = 5,
            totalProgress = 10,
            xp = 50,
            iconColor = Color(0xFF42A5F5),
            iconBg = Color(0xFF102338),
            icon = Icons.Default.Calculate
        ),
        MissionCardModel(
            id = "mission_geometry_explorer",
            title = "Geometry Explorer",
            description = "Complete 8 geometry challenges",
            statusText = "In Progress",
            currentProgress = 3,
            totalProgress = 8,
            xp = 40,
            iconColor = Color(0xFF26A69A),
            iconBg = Color(0xFF0F2B26),
            icon = Icons.Default.Functions
        ),
        MissionCardModel(
            id = "mission_daily_streak",
            title = "Daily Practice Streak",
            description = "Practice for 7 consecutive days",
            statusText = "In Progress",
            currentProgress = 4,
            totalProgress = 7,
            xp = 30,
            iconColor = Color(0xFFFFB300),
            iconBg = Color(0xFF2A2000),
            icon = Icons.Default.Shield
        ),
        MissionCardModel(
            id = "mission_circuit_power",
            title = "Circuit Optimization",
            description = "Complete 6 circuit analysis questions",
            statusText = "Upcoming",
            currentProgress = 0,
            totalProgress = 6,
            xp = 35,
            iconColor = Color(0xFFAB47BC),
            iconBg = Color(0xFF28132F),
            icon = Icons.Default.Assignment
        )
    )

    val filtered = missionsList.filter { m ->
        val matchesTab = when (selectedTab) {
            "Active" -> m.statusText == "In Progress"
            "Completed" -> m.currentProgress >= m.totalProgress
            "Upcoming" -> m.statusText == "Upcoming"
            else -> true
        }
        val q = searchQuery.trim().lowercase()
        val matchesQ = q.isEmpty() || m.title.lowercase().contains(q) || m.description.lowercase().contains(q)
        matchesTab && matchesQ
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // ── Header ──
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Missions",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Complete challenges. Earn rewards.",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Search Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ProofLoopCardBg)
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search missions...", color = TextMuted, fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Filter Tabs ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterTabs.forEach { tab ->
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
                        .padding(horizontal = 16.dp, vertical = 7.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // ── Missions List ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { mission ->
                MissionCardItem(
                    mission = mission,
                    onContinue = {
                        val matchingMission = allMissions.find { it.id == mission.id } ?: allMissions.firstOrNull()
                        matchingMission?.let { MissionRepository.selectMission(it.id) }
                        onSelectMissionDetail(matchingMission?.id ?: mission.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun MissionCardItem(
    mission: MissionCardModel,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ProofLoopCardBg)
            .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row with icon, title, and In Progress badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(mission.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = mission.icon,
                        contentDescription = null,
                        tint = mission.iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mission.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = mission.description,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // In Progress Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0E2232))
                        .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = mission.statusText,
                        color = CyanNeon,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress text
            Text(
                text = "${mission.currentProgress}/${mission.totalProgress}",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 54.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row: XP on left, Yellow Continue button on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = ProofLoopYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ ${mission.xp} XP",
                        color = ProofLoopYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ProofLoopYellow)
                        .clickable { onContinue() }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Continue",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
