package com.example.proofloop.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // Phase 1 — wordmark rises and fades in (0–600ms)
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffsetY = remember { Animatable(32f) }

    // Phase 2 — gold line sweeps width (600–1100ms)
    val lineScale = remember { Animatable(0f) }
    val lineAlpha = remember { Animatable(0f) }

    // Phase 3 — tagline fades in + dot pulses (1100–1700ms)
    val taglineAlpha = remember { Animatable(0f) }
    val dotScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Phase 1: wordmark
        launch {
            wordmarkAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            wordmarkOffsetY.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        delay(550)

        // Phase 2: gold line sweep
        launch {
            lineAlpha.animateTo(1f, animationSpec = tween(150, easing = LinearEasing))
        }
        launch {
            lineScale.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        delay(480)

        // Phase 3: tagline + dot
        launch {
            taglineAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            dotScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            )
        }

        delay(900)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // — ProofLoop wordmark —
            Text(
                text = "ProofLoop",
                color = TextPrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier
                    .alpha(wordmarkAlpha.value)
                    .graphicsLayer { translationY = wordmarkOffsetY.value }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // — Gold sweep line —
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(2.dp)
                    .alpha(lineAlpha.value)
                    .scale(scaleX = lineScale.value, scaleY = 1f)
                    .background(ProofLoopYellow)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // — Tagline —
            Text(
                text = "Don't just learn it. Prove it.",
                color = TextMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(taglineAlpha.value)
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // — Pulsing gold dot —
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .scale(dotScale.value)
                    .clip(CircleShape)
                    .background(ProofLoopYellow)
            )
        }
    }
}
