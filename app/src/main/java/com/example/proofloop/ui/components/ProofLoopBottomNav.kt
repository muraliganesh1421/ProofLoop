package com.example.proofloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextSecondary

enum class NavTab {
    HOME,
    SKILLS,
    MISSIONS,
    PRACTICE,
    PROGRESS
}

@Composable
fun ProofLoopBottomNav(
    currentTab: NavTab,
    onSelectTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BgDark)
            .border(
                width = 1.dp,
                color = Color(0xFF1E222A),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentTab == NavTab.HOME,
                onClick = { onSelectTab(NavTab.HOME) }
            )
            BottomNavItem(
                icon = Icons.Default.Insights,
                label = "Skills",
                isSelected = currentTab == NavTab.SKILLS,
                onClick = { onSelectTab(NavTab.SKILLS) }
            )
            BottomNavItem(
                icon = Icons.Default.Assignment,
                label = "Missions",
                isSelected = currentTab == NavTab.MISSIONS,
                onClick = { onSelectTab(NavTab.MISSIONS) }
            )
            BottomNavItem(
                icon = Icons.Default.Psychology,
                label = "Practice",
                isSelected = currentTab == NavTab.PRACTICE,
                onClick = { onSelectTab(NavTab.PRACTICE) }
            )
            BottomNavItem(
                icon = Icons.Default.BarChart,
                label = "Progress",
                isSelected = currentTab == NavTab.PROGRESS,
                onClick = { onSelectTab(NavTab.PROGRESS) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) com.example.proofloop.theme.ProofLoopYellow else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) com.example.proofloop.theme.ProofLoopYellow else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
