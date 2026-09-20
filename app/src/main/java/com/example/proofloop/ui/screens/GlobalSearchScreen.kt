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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.local.SampleData
import com.example.proofloop.theme.BgCard
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.BgSurface
import com.example.proofloop.theme.BorderSubtle
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

data class SearchResultItem(
    val id: String,
    val title: String,
    val category: String, // Skills, Missions, Practice, History
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit = {},
    onSelectResult: (String, String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Skills", "Missions", "Practice", "History")

    // Build Search Index from Sample Data
    val allIndex = remember {
        val list = mutableListOf<SearchResultItem>()

        SampleData.initialSkills.forEach { skill ->
            list.add(
                SearchResultItem(
                    id = skill.id,
                    title = skill.name,
                    category = "Skills",
                    subtitle = "Category: ${skill.category} • Current Score: ${skill.score}%",
                    icon = Icons.Default.Insights
                )
            )
        }

        SampleData.allMissions.forEach { mission ->
            list.add(
                SearchResultItem(
                    id = mission.id,
                    title = mission.title,
                    category = "Missions",
                    subtitle = "${mission.subject} • ${mission.difficulty} • Gap: ${mission.targetGap}",
                    icon = Icons.Default.Assignment
                )
            )
        }

        list.add(
            SearchResultItem(
                id = "pq_01",
                title = "Percentage Shock & Cumulative Totals",
                category = "Practice",
                subtitle = "Mathematics • 12% Inflation Shock Calculation",
                icon = Icons.Default.Psychology
            )
        )

        list.add(
            SearchResultItem(
                id = "h_01",
                title = "Campus Festival Budget Verification",
                category = "History",
                subtitle = "Attempt #1 • Score 78/100 • Sep 2026",
                icon = Icons.Default.History
            )
        )

        list
    }

    val filteredResults = remember(searchQuery, selectedCategory) {
        val queryClean = searchQuery.trim().lowercase()
        allIndex.filter { item ->
            val matchesCategory = (selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true))
            val matchesQuery = queryClean.isEmpty() ||
                    item.title.lowercase().contains(queryClean) ||
                    item.subtitle.lowercase().contains(queryClean) ||
                    item.category.lowercase().contains(queryClean)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(14.dp)
    ) {
        // Search Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search topics, skills, missions, practice...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IqooOrange) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard,
                    focusedBorderColor = IqooOrange,
                    unfocusedBorderColor = BorderSubtle
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSel = (selectedCategory == cat)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSel) IqooOrange else BgSurface)
                        .border(1.dp, if (isSel) IqooOrange else BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        color = if (isSel) Color.White else TextSecondary,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Results Section
        Text(
            text = "SEARCH RESULTS (${filteredResults.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No matching results found", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Try searching for 'Math', 'Budget', 'Queue', or 'Percentage'", color = TextMuted, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { onSelectResult(item.category, item.id) },
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E283C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = null, tint = IqooYellow, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(item.category.uppercase(), color = IqooOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.subtitle, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
