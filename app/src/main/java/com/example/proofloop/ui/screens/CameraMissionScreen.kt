package com.example.proofloop.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.proofloop.camera.CameraController
import com.example.proofloop.camera.CameraPreviewView
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

@Composable
fun CameraMissionScreen(
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var flashEnabled by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<String?>(null) }
    var showHowItWorksDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            capturedImageUri = uri.toString()
            MissionRepository.updateSnapshot(uri.toString(), "Uploaded from Gallery")
            Toast.makeText(context, "Note image loaded!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
    ) {
        // ── Header ──
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Camera Practice",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Scan your notes, questions or problems",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Camera Viewfinder with Yellow Corner Brackets ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141A24))
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (hasCameraPermission && capturedImageUri == null) {
                CameraPreviewView(
                    modifier = Modifier.fillMaxSize(),
                    onControllerReady = { }
                )
            } else {
                // Simulated handwritten note math paper
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF8B8173)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "2x + 3 = 11",
                            color = Color(0xFF232528),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Cursive
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "x = ?",
                            color = Color(0xFF232528),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Cursive
                        )
                    }
                }
            }

            // Yellow Reticle / Corner Brackets
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Top-Left corner
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopStart)
                        .border(
                            width = 3.dp,
                            color = ProofLoopYellow,
                            shape = RoundedCornerShape(topStart = 8.dp)
                        )
                )
                // Top-Right corner
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .border(
                            width = 3.dp,
                            color = ProofLoopYellow,
                            shape = RoundedCornerShape(topEnd = 8.dp)
                        )
                )
                // Bottom-Left corner
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomStart)
                        .border(
                            width = 3.dp,
                            color = ProofLoopYellow,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                )
                // Bottom-Right corner
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .border(
                            width = 3.dp,
                            color = ProofLoopYellow,
                            shape = RoundedCornerShape(bottomEnd = 8.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Controls Row: Gallery, Yellow Shutter Button, Flash ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { galleryPicker.launch("image/*") }
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(ProofLoopCardBg)
                        .border(1.dp, ProofLoopCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gallery", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }

            // Large Yellow Shutter Button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ProofLoopYellow)
                    .clickable {
                        capturedImageUri = "captured_scan"
                        MissionRepository.updateSnapshot("captured_scan", "Captured 2x + 3 = 11")
                        Toast.makeText(context, "Captured! Analyzing with ProofLoop AI...", Toast.LENGTH_SHORT).show()
                        onContinueClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Flash
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { flashEnabled = !flashEnabled }
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(if (flashEnabled) Color(0xFF2A2000) else ProofLoopCardBg)
                        .border(1.dp, if (flashEnabled) ProofLoopYellow else ProofLoopCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = if (flashEnabled) ProofLoopYellow else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Flash", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Guidance Card with "How it works?" Button ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ProofLoopCardBg)
                .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = ProofLoopYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AI will analyze the image and guide you step by step.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, ProofLoopYellow, RoundedCornerShape(20.dp))
                        .clickable { showHowItWorksDialog = true }
                        .padding(horizontal = 18.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "💡 How it works?",
                        color = ProofLoopYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
