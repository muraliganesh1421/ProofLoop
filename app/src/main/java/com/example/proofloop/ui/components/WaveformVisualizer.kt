package com.example.proofloop.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.IndigoNeon
import kotlin.random.Random

@Composable
fun WaveformVisualizer(
    isRecording: Boolean,
    rmsDb: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 18
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_anim"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val normalizedRms = (rmsDb / 12f).coerceIn(0.1f, 1f)

        for (i in 0 until barCount) {
            val factor = if (isRecording) {
                val distanceRatio = 1f - kotlin.math.abs((i - barCount / 2f) / (barCount / 2f))
                (normalizedRms * distanceRatio * 0.9f + (pulse * 0.3f)).coerceIn(0.15f, 1f)
            } else {
                0.15f
            }

            val barHeight = (48 * factor).dp

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isRecording) {
                            if (i % 2 == 0) CyanNeon else IndigoNeon
                        } else {
                            Color(0xFF263248)
                        }
                    )
            )
        }
    }
}
