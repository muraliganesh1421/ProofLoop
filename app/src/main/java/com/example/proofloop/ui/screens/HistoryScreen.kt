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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
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
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CoralAlert
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

private data class HistoryItemModel(
    val id: String,
    val title: String,
    val dateText: String,
    val scoreText: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val type: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color
)

@Composable
fun HistoryScreen(
    onSelectHistoryDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val storage = remember { LocalProofStorage.getInstance(context) }
    val allAttempts by storage.attemptsFlow.collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Practice", "Missions", "Camera")

    val staticHistoryList = listOf(
        HistoryItemModel("h_1", "Linear Equations", "12 Sep 2026", "8/10 • 80%", "Good", MintProof, Color(0xFF0F2B1E), "Practice", Icons.Default.Calculate, Color(0xFF42A5F5), Color(0xFF102338)),
        HistoryItemModel("h_2", "Geometry Basics", "11 Sep 2026", "6/10 • 60%", "Needs Practice", ProofLoopYellow, Color(0xFF2A2200), "Practice", Icons.Default.Functions, Color(0xFF26A69A), Color(0xFF0F2B26)),
        HistoryItemModel("h_3", "Mole Concept", "10 Sep 2026", "4/10 • 40%", "Mistake", CoralAlert, Color(0xFF2D1416), "Practice", Icons.Default.Science, Color(0xFFEC407A), Color(0xFF2D111D)),
        HistoryItemModel("h_4", "Trigonometry Ratios", "8 Sep 2026", "9/10 • 90%", "Good", MintProof, Color(0xFF0F2B1E), "Missions", Icons.Default.Calculate, Color(0xFF5C6BC0), Color(0xFF171B38)),
        HistoryItemModel("h_5", "Quadratic Equations", "6 Sep 2026", "5/10 • 50%", "Needs Practice", ProofLoopYellow, Color(0xFF2A2200), "Camera", Icons.Default.CameraAlt, Color(0xFFAB47BC), Color(0xFF28132F))
    )

    val displayList = if (allAttempts.isNotEmpty()) {
        allAttempts.mapIndexed { idx, attempt ->
            val status = when {
                attempt.isCorrect -> "Good"
                attempt.hintsUsedCount > 1 -> "Needs Practice"
                else -> "Mistake"
            }
            val statusColor = when (status) {
                "Good" -> MintProof
                "Needs Practice" -> ProofLoopYellow
                else -> CoralAlert
            }
            val statusBg = when (status) {
                "Good" -> Color(0xFF0F2B1E)
                "Needs Practice" -> Color(0xFF2A2200)
                else -> Color(0xFF2D1416)
            }
            HistoryItemModel(
                id = attempt.id,
                title = attempt.topic.ifBlank { "Problem ${idx + 1}" },
                dateText = "Recent",
                scoreText = if (attempt.isCorrect) "Verified" else "Gap Flagged",
                status = status,
                statusColor = statusColor,
                statusBg = statusBg,
                type = "Practice",
                icon = Icons.Default.Calculate,
                iconColor = Color(0xFF42A5F5),
                iconBg = Color(0xFF102338)
            )
        }
    } else {
        staticHistoryList
    }

    val filtered = displayList.filter { item ->
        when (selectedFilter) {
            "Practice" -> item.type == "Practice"
            "Missions" -> item.type == "Missions"
            "Camera" -> item.type == "Camera"
            else -> true
        }
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
                text = "History",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Your learning journey",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Filter Pills ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { tab ->
                val isSelected = tab == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ProofLoopYellow else ProofLoopCardBg)
                        .border(
                            1.dp,
                            if (isSelected) ProofLoopYellow else ProofLoopCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedFilter = tab }
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

        // ── History Cards List ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { item ->
                HistoryCardRow(item = item, onClick = { onSelectHistoryDetail(item.id) })
            }
        }
    }
}

@Composable
private fun HistoryCardRow(
    item: HistoryItemModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ProofLoopCardBg)
            .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.dateText} • ${item.scoreText}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Status Badge (Good, Needs Practice, Mistake)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.statusBg)
                    .border(1.dp, item.statusColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.status,
                    color = item.statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
