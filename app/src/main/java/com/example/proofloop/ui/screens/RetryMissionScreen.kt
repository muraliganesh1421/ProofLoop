package com.example.proofloop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.proofloop.data.local.SampleData
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.speech.SpeechManager
import com.example.proofloop.theme.BgCardElevated
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IndigoNeon
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.ProofLoopPrimaryButton
import com.example.proofloop.ui.components.WaveformVisualizer

@Composable
fun RetryMissionScreen(
    onSubmitRetryClick: () -> Unit
) {
    val context = LocalContext.current
    val speechManager = remember { SpeechManager(context) }

    val isListening by speechManager.isListening.collectAsState()
    val spokenText by speechManager.spokenText.collectAsState()
    val rmsDb by speechManager.rmsDb.collectAsState()

    var customAnswer by remember { mutableStateOf("") }
    var isTypeMode by remember { mutableStateOf(false) }

    val currentText = if (spokenText.isNotBlank()) spokenText else customAnswer

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) speechManager.startListening()
    }

    DisposableEffect(Unit) {
        onDispose { speechManager.destroy() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CORRECTIVE MISSION (RETRY)",
                color = MintProof,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgCardElevated)
                    .clickable { isTypeMode = !isTypeMode }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isTypeMode) Icons.Default.Mic else Icons.Default.Edit,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTypeMode) "Voice" else "Type",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Before choosing a solution, identify 2 possible causes and explain how you would test each.",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Close the reasoning gap. Prove you can validate root causes through hypothesis testing before leaping to an unverified fix.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!isTypeMode) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WaveformVisualizer(isRecording = isListening, rmsDb = rmsDb)
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isListening) MintProof else CyanNeon)
                                .clickable {
                                    if (isListening) {
                                        speechManager.stopListening()
                                    } else {
                                        val hasPerm = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (hasPerm) speechManager.startListening()
                                        else micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Record",
                                tint = BgDark,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isListening) "RECORDING CORRECTIVE PROOF..." else "TAP TO PROVE REASONING",
                            color = if (isListening) MintProof else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = customAnswer,
                    onValueChange = { customAnswer = it },
                    placeholder = { Text("List 2 causes and your testing method for each...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintProof,
                        unfocusedBorderColor = Color(0xFF263248),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentText.isNotBlank()) {
                GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color(0xFF10231D)) {
                    Column {
                        Text(
                            text = "CORRECTIVE PROOF RESPONSE",
                            color = MintProof,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Fill Suggestion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161F2E))
                    .clickable {
                        speechManager.setText(SampleData.sampleAnswerCorrective)
                        customAnswer = SampleData.sampleAnswerCorrective
                    }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MintProof,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto-fill 2 tested hypotheses: \"Cause 1: POS latency (Test: Time 20 card transactions). Cause 2: Food prep delay (Test: Measure cook-to-plate handoff).\"",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        ProofLoopPrimaryButton(
            text = "EVALUATE IMPROVEMENT",
            icon = Icons.Default.Bolt,
            onClick = {
                val finalAnswer = if (currentText.isNotBlank()) currentText else SampleData.sampleAnswerCorrective
                MissionRepository.updateCorrectiveAnswer(finalAnswer)
                onSubmitRetryClick()
            }
        )
    }
}
