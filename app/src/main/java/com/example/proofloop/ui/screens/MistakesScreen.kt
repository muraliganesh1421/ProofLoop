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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.repository.MistakeRepository
import com.example.proofloop.theme.AmberGap
import com.example.proofloop.theme.BgCard
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.BgSurface
import com.example.proofloop.theme.BorderSubtle
import com.example.proofloop.theme.CoralAlert
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

@Composable
fun MistakesScreen(
    onBackClick: () -> Unit = {},
    onReviewMicroLesson: (String) -> Unit = {},
    onReattemptChallenge: (String) -> Unit = {},
    repository: MistakeRepository = remember { MistakeRepository.getInstance() }
) {
    val mistakes by repository.mistakes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text("MISTAKE TRACKER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoralAlert, letterSpacing = 1.sp)
                Text("Learn → Reattempt → Prove", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Banner Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x2AFF4D4F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = CoralAlert, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Competency Gaps Logged", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Reattempting logged mistakes proves mastery and restores your verified capability score.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mistakes) { entry ->
                val m = entry.item
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (entry.isResolved) MintProof else BorderSubtle, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = BgSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(entry.subject.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (entry.isResolved) Color(0x1A00E676) else Color(0x1AFF9E0B))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (entry.isResolved) "RESOLVED" else "GAP DETECTED",
                                    fontSize = 10.sp,
                                    color = if (entry.isResolved) MintProof else AmberGap,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(m.mistakeTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("What happened: ${m.whatHappened}", fontSize = 12.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgCard)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("WHY IT MATTERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IqooYellow)
                                Text(m.whyItMatters, fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { onReviewMicroLesson(entry.microLessonId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E283A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = IqooYellow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Micro-Lesson", color = TextPrimary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onReattemptChallenge(m.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = IqooOrange),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reattempt Now", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
