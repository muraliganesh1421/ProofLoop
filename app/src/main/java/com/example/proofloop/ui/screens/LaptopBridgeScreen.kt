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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooOrangeGlow
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.ProofLoopPrimaryButton

@Composable
fun LaptopBridgeScreen(
    onBackClick: () -> Unit
) {
    var isConnected by remember { mutableStateOf(false) }
    var activeStage by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PHONE + LAPTOP WORKSPACE BRIDGE",
                color = IqooOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Multi-Device Learning Flow",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "The phone is your active sensor & AI coach; the laptop is your expansive problem workspace.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dual Device Hardware Link Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (isConnected) MintProof.copy(alpha = 0.5f) else IqooOrange.copy(alpha = 0.4f),
                backgroundColor = Color(0xFF161514)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Phone Device
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF241C16)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "Phone",
                                    tint = IqooOrange,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "iQOO PHONE",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Camera & Voice AI",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }

                        // Link Status
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Sync,
                                contentDescription = null,
                                tint = if (isConnected) MintProof else IqooOrange,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isConnected) "SYNC ACTIVE" else "BRIDGE READY",
                                color = if (isConnected) MintProof else IqooOrange,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Laptop Device
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E222A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Laptop,
                                    contentDescription = "Laptop",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PC WORKSPACE",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Deep Simulation",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isConnected) Color(0x3300E676) else Color(0xFF1A1D24))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isConnected)
                                "Session paired: localhost:8000/bridge/session-iqoo-9941"
                            else
                                "Bridge pairing code: PL-IQOO-FESTIVAL (Office Kit Ready)",
                            color = if (isConnected) MintProof else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4-STAGE INTERACTIVE WORKFLOW
            Text(
                text = "INTERACTIVE 4-STAGE LEARNING WORKFLOW",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            WorkflowStageCard(
                step = 1,
                title = "Phone: Capture Evidence in the World",
                subtitle = "Use camera to scan receipts, classroom dimensions, or price boards.",
                icon = Icons.Default.CameraAlt,
                isActive = activeStage == 1,
                onSelect = { activeStage = 1 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            WorkflowStageCard(
                step = 2,
                title = "Laptop: Expand Problem in Workspace",
                subtitle = "Spreadsheets, interactive line charts, and mathematical multi-variable models.",
                icon = Icons.Default.Laptop,
                isActive = activeStage == 2,
                onSelect = { activeStage = 2 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            WorkflowStageCard(
                step = 3,
                title = "Phone: Defend Solution by Voice",
                subtitle = "Explain your formulas, trade-offs, and percentage adjustments into the microphone.",
                icon = Icons.Default.Mic,
                isActive = activeStage == 3,
                onSelect = { activeStage = 3 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            WorkflowStageCard(
                step = 4,
                title = "AI: Evaluate Competency & Adapt",
                subtitle = "Detect percentage gap, generate micro-lesson, and deliver adapted next challenge.",
                icon = Icons.Default.Verified,
                isActive = activeStage == 4,
                onSelect = { activeStage = 4 }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        ProofLoopPrimaryButton(
            text = if (isConnected) "DISCONNECT WORKSPACE" else "SIMULATE LAPTOP BRIDGE",
            icon = Icons.Default.Devices,
            onClick = { isConnected = !isConnected }
        )
    }
}

@Composable
private fun WorkflowStageCard(
    step: Int,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0xFF1E1B18) else Color(0xFF131720))
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) IqooOrange else Color(0xFF222B3A),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) IqooOrangeGlow else Color(0xFF1B2230)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) IqooOrange else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "STEP 0$step • $title",
                    color = if (isActive) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
