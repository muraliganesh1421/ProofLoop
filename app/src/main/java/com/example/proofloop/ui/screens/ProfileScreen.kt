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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.proofloop.data.auth.AuthRepository
import com.example.proofloop.data.auth.SessionManager
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    sessionManager: SessionManager? = null
) {
    val context = LocalContext.current
    val manager = remember { sessionManager ?: SessionManager.getInstance(context) }
    val session by manager.sessionFlow.collectAsState(initial = null)
    val authRepo = remember { AuthRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    val userName = session?.name?.ifBlank { "Murali Ganesh" } ?: "Murali Ganesh"
    val userEmail = session?.email?.ifBlank { "murali.student@iqoo.edu" } ?: "murali.student@iqoo.edu"

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Profile", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── User Profile Card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ProofLoopCardBg)
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2000))
                        .border(2.dp, ProofLoopYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = ProofLoopYellow,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userEmail,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A2000))
                            .border(1.dp, ProofLoopYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Student",
                            color = ProofLoopYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Edit Pencil Icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2838))
                        .clickable { /* Edit profile */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Menu List Items (Matching Screen 9) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ProofLoopCardBg)
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(18.dp))
        ) {
            ProfileMenuItem(icon = Icons.Default.Person, label = "Personal Information", onClick = {})
            ProfileMenuItem(icon = Icons.Default.TrackChanges, label = "Learning Goals", onClick = {})

            // Notifications with Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text("Notifications", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = ProofLoopYellow,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = Color(0xFF202838)
                    )
                )
            }

            // Appearance with "Dark Mode"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DarkMode, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Text("Appearance", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text("Dark Mode", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            }

            ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Help, label = "Help & Support", onClick = { showHelpDialog = true })
            ProfileMenuItem(icon = Icons.Default.Info, label = "About ProofLoop", onClick = { showAboutDialog = true })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bottom CTA: Log Out Button ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, ProofLoopYellow, RoundedCornerShape(20.dp))
                .clickable {
                    scope.launch {
                        authRepo.logout()
                        onLogoutClick()
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = ProofLoopYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    color = ProofLoopYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("How ProofLoop Works", color = ProofLoopYellow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("� Complete missions to earn Proof Cards\n� Unlock new skills by finishing prerequisites\n� Answer adaptive questions to prove mastery\n� Retry missions to improve your score\n\nFor help or feedback:\n?? support@proofloop.app\n?? discord.gg/proofloop", color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = { TextButton(onClick = { showHelpDialog = false }) { Text("Got it", color = ProofLoopYellow) } },
            containerColor = ProofLoopCardBg,
            tonalElevation = 0.dp
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About ProofLoop", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("ProofLoop v1.0", color = ProofLoopYellow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("ProofLoop is a mastery-based learning platform that replaces passive study with active proof.\n\nInstead of just watching or reading, you solve real problems, defend your reasoning, and earn Proof Cards � verifiable evidence of genuine understanding.\n\nBuilt with Jetpack Compose � Kotlin � Gemini AI\nDesigned for hackathon judges and real learners alike.\n\n� 2025 ProofLoop Team", color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("Close", color = ProofLoopYellow) } },
            containerColor = ProofLoopCardBg,
            tonalElevation = 0.dp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}
