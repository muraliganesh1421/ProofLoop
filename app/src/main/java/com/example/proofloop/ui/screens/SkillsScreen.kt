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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.proofloop.domain.models.SubjectType
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.BorderSubtle
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

data class SkillItemData(
    val id: String,
    val title: String,
    val subject: SubjectType,
    val completedLessons: Int,
    val totalLessons: Int,
    val masteryPct: Int,
    val iconColor: Color,
    val iconBg: Color,
    val icon: ImageVector
)

@Composable
fun SkillsScreen(
    onSelectSkillDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val storage = remember { LocalProofStorage.getInstance(context) }
    val allAttempts by storage.attemptsFlow.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf("All") }

    val filterTabs = listOf("All", "Mathematics", "Physics", "Chemistry", "Computer Science", "DSA", "DBMS")

    val skillsList = listOf(
        SkillItemData("skill_algebra", "Algebra", SubjectType.MATHEMATICS, 12, 20, 60, Color(0xFFFF5252), Color(0xFF2D1416), Icons.Default.Calculate),
        SkillItemData("skill_geometry", "Geometry", SubjectType.MATHEMATICS, 8, 15, 53, Color(0xFF29B6F6), Color(0xFF0F2636), Icons.Default.Functions),
        SkillItemData("skill_calculus", "Calculus", SubjectType.MATHEMATICS, 5, 12, 42, Color(0xFFAB47BC), Color(0xFF28132F), Icons.Default.Functions),
        SkillItemData("skill_trig", "Trigonometry", SubjectType.MATHEMATICS, 4, 10, 40, Color(0xFFFF7043), Color(0xFF331B13), Icons.Default.Calculate),
        SkillItemData("skill_statistics", "Statistics", SubjectType.MATHEMATICS, 2, 8, 25, Color(0xFF42A5F5), Color(0xFF102338), Icons.Default.Calculate),
        SkillItemData("skill_physics", "Kinematics", SubjectType.PHYSICS, 6, 12, 50, Color(0xFF26A69A), Color(0xFF0F2B26), Icons.Default.Psychology),
        SkillItemData("skill_prog", "Programming Fundamentals", SubjectType.PROGRAMMING, 7, 14, 65, Color(0xFF7E57C2), Color(0xFF1F1532), Icons.Default.Code),
        SkillItemData("skill_dsa", "Data Structures", SubjectType.DATA_STRUCTURES, 4, 10, 55, Color(0xFFEC407A), Color(0xFF2D111D), Icons.Default.DataObject),
        SkillItemData("skill_dbms", "Relational Databases", SubjectType.DBMS, 5, 8, 70, Color(0xFF26C6DA), Color(0xFF0D282D), Icons.Default.Storage)
    )

    val filtered = skillsList.filter { skill ->
        val matchesCategory = when (selectedSubjectFilter) {
            "Mathematics" -> skill.subject == SubjectType.MATHEMATICS
            "Physics" -> skill.subject == SubjectType.PHYSICS
            "Computer Science" -> skill.subject == SubjectType.COMPUTER_SCIENCE || skill.subject == SubjectType.PROGRAMMING
            "DSA" -> skill.subject == SubjectType.DATA_STRUCTURES
            "DBMS" -> skill.subject == SubjectType.DBMS
            else -> true
        }
        val q = searchQuery.trim().lowercase()
        val matchesQuery = q.isEmpty() || skill.title.lowercase().contains(q)
        matchesCategory && matchesQuery
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
                text = "Skills",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Build your foundation. Master step by step.",
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
                placeholder = { Text("Search skills...", color = TextMuted, fontSize = 13.sp) },
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

        // ── Filter Pills (Horizontal Scroll) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterTabs.forEach { tab ->
                val isSelected = tab == selectedSubjectFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ProofLoopYellow else ProofLoopCardBg)
                        .border(
                            1.dp,
                            if (isSelected) ProofLoopYellow else ProofLoopCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedSubjectFilter = tab }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
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

        // ── Skills List ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { skill ->
                SkillCardItem(skill = skill, onClick = { onSelectSkillDetail(skill.id) })
            }
        }
    }
}

@Composable
private fun SkillCardItem(
    skill: SkillItemData,
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
                    .background(skill.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = skill.icon,
                    contentDescription = skill.title,
                    tint = skill.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title, subtitle, progress bar
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${skill.completedLessons}/${skill.totalLessons} lessons • ${skill.masteryPct}% mastery",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { skill.masteryPct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = skill.iconColor,
                    trackColor = Color(0xFF1E2838)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
