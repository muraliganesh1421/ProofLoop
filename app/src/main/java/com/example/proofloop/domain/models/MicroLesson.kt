package com.example.proofloop.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class MicroLesson(
    val id: String = "ml_obs_vs_assump",
    val title: String = "Observation vs. Assumption",
    val targetGap: String = "Evidence Gathering",
    val readDurationSeconds: Int = 60,
    val coreInsight: String = "Assumptions feel like facts until tested against physical evidence.",
    val takeaways: List<String> = listOf(
        "Never propose a fix until you have logged at least 2 distinct observable data points.",
        "Distinguish between what you see (queue length) and why it happens (payment latency).",
        "A single interview insight is a clue, not a confirmed bottleneck."
    ),
    val exercisePrompt: String = "Before selecting a solution, identify 2 possible causes and formulate a test for each."
)
