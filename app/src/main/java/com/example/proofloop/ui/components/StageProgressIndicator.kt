package com.example.proofloop.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CyanNeon
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary

@Composable
fun StageProgressIndicator(
    currentStage: Int,
    modifier: Modifier = Modifier
) {
    val stages = listOf(
        "Understanding context",
        "Analyzing response",
        "Mapping competencies",
        "Preparing feedback"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        stages.forEachIndexed { index, title ->
            val isCompleted = index < currentStage
            val isCurrent = index == currentStage

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MintProof
                                isCurrent -> CyanNeon.copy(alpha = 0.2f)
                                else -> Color(0xFF1F283A)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = BgDark,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (isCurrent) {
                        CircularProgressIndicator(
                            color = CyanNeon,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = title,
                    color = when {
                        isCompleted -> MintProof
                        isCurrent -> TextPrimary
                        else -> TextMuted
                    },
                    fontSize = 15.sp,
                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
