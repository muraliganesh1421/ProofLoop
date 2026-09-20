package com.example.proofloop.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.ai.AiServiceProvider
import com.example.proofloop.theme.BgCard
import com.example.proofloop.theme.BgCardElevated
import com.example.proofloop.theme.BorderSubtle
import com.example.proofloop.theme.CyanGlow
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

@Composable
fun DemoModeBadge(
    modifier: Modifier = Modifier
) {
    val isDemoMode by AiServiceProvider.isDemoMode.collectAsState()
    val apiKey by AiServiceProvider.customApiKey.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDemoMode) CyanGlow else Color(0x2600E676))
            .clickable { showDialog = true }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isDemoMode) CyanNeon else MintProof)
            )
            Text(
                text = if (isDemoMode) "DEMO MODE" else "LIVE AI",
                color = if (isDemoMode) CyanNeon else MintProof,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Configure",
                tint = if (isDemoMode) CyanNeon else MintProof,
                modifier = Modifier.size(12.dp)
            )
        }
    }

    if (showDialog) {
        var tempKey by remember { mutableStateOf(apiKey) }
        var tempDemo by remember { mutableStateOf(isDemoMode) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "AI Engine Configuration",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "ProofLoop uses a hybrid engine: reliable deterministic Demo Mode for instant presentations and Gemini Cloud AI for live evaluation.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enable Demo Mode (Offline)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = tempDemo,
                            onCheckedChange = { tempDemo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyanNeon,
                                checkedTrackColor = CyanGlow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Custom Gemini API Key (Optional)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        placeholder = { Text("AIzaSy...", color = TextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        AiServiceProvider.setDemoMode(tempDemo)
                        if (tempKey.isNotBlank()) {
                            AiServiceProvider.setApiKey(tempKey)
                        }
                        showDialog = false
                    }
                ) {
                    Text("Save", color = CyanNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = BgCardElevated,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
