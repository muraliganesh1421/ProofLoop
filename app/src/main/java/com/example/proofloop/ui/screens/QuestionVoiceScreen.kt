package com.example.proofloop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.scale
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
import com.example.proofloop.theme.CyanGlow
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IndigoNeon
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import com.example.proofloop.ui.components.GlassCard
import com.example.proofloop.ui.components.ProofLoopPrimaryButton
import com.example.proofloop.ui.components.ProofLoopSecondaryButton
import com.example.proofloop.ui.components.WaveformVisualizer

@Composable
fun QuestionVoiceScreen(
    onContinueClick: () -> Unit
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
        if (granted) {
            speechManager.startListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechManager.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Step Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEP 1 OF 4",
                color = CyanNeon,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // Mode toggle
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
                    text = if (isTypeMode) "Switch to Voice" else "Type Answer",
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
                text = "What would you investigate first?",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Speak or describe the specific point in the cafeteria operation you would examine first.",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Voice Recording Box or Type Field
            if (!isTypeMode) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isListening) CyanNeon else Color(0xFF263248)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WaveformVisualizer(
                            isRecording = isListening,
                            rmsDb = rmsDb
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mic Button with animated pulse
                        val scale by animateFloatAsState(
                            targetValue = if (isListening) 1.1f else 1f,
                            label = "mic_scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(if (isListening) CyanNeon else IndigoNeon)
                                .clickable {
                                    if (isListening) {
                                        speechManager.stopListening()
                                    } else {
                                        val hasAudioPerm = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED

                                        if (hasAudioPerm) {
                                            speechManager.startListening()
                                        } else {
                                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
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
                            text = if (isListening) "LISTENING... (TAP TO FINISH)" else "TAP TO SPEAK",
                            color = if (isListening) CyanNeon else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = customAnswer,
                    onValueChange = { customAnswer = it },
                    placeholder = { Text("Describe your investigation focus...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Color(0xFF263248),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transcribed / Selected Answer Display
            if (currentText.isNotBlank()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF131B2A)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CAPTURED OBSERVATION",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        speechManager.setText("")
                                        customAnswer = ""
                                    }
                            )
                        }
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

            // 1-Tap Hackathon Quick Fill Demo Suggestion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF161F2E))
                    .clickable {
                        speechManager.setText(SampleData.sampleAnswerInvestigate)
                        customAnswer = SampleData.sampleAnswerInvestigate
                    }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto-fill realistic answer: \"I will observe where the line stops moving—at tray collection, food assembly, or POS terminal.\"",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProofLoopSecondaryButton(
                text = "RETRY",
                icon = Icons.Default.Refresh,
                onClick = {
                    speechManager.setText("")
                    customAnswer = ""
                },
                modifier = Modifier.weight(1f)
            )

            val context = LocalContext.current
            ProofLoopPrimaryButton(
                text = "NEXT STEP",
                icon = Icons.Default.ArrowForward,
                onClick = {
                    if (currentText.isBlank()) {
                        Toast.makeText(context, "Enter your answer before continuing.", Toast.LENGTH_SHORT).show()
                    } else {
                        MissionRepository.updateFirstAnswer(currentText)
                        onContinueClick()
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
