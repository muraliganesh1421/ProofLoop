package com.example.proofloop.data.repository

import com.example.proofloop.domain.models.MistakeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MistakeEntry(
    val item: MistakeItem,
    val subject: String,
    val isResolved: Boolean = false,
    val lastAttemptDate: String = "Today",
    val microLessonId: String = "ml_percentage_impact"
)

class MistakeRepository {
    companion object {
        @Volatile
        private var INSTANCE: MistakeRepository? = null

        fun getInstance(): MistakeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MistakeRepository().also { INSTANCE = it }
            }
        }

        val initialMistakes = listOf(
            MistakeEntry(
                item = MistakeItem(
                    id = "m_01",
                    mistakeTitle = "Compound Percentage Over-Look",
                    whatHappened = "Applied +12% food inflation as a flat Rs 120 increase instead of scaling across 240 attendees.",
                    competencyAffected = "Percentage Reasoning",
                    whyItMatters = "In multi-unit budgeting, per-head increases compound across volume, causing massive budget overruns.",
                    recommendedCorrection = "Multiply rate increase by single meal cost (Rs 85 x 1.12 = Rs 95.20), then scale by 240 attendees.",
                    occurrenceCount = 2
                ),
                subject = "Mathematics",
                isResolved = false,
                lastAttemptDate = "Yesterday"
            ),
            MistakeEntry(
                item = MistakeItem(
                    id = "m_02",
                    mistakeTitle = "Jumping to Premature Solution",
                    whatHappened = "Proposed adding a second cashier before verifying if the card terminal network was operational.",
                    competencyAffected = "Root-Cause Reasoning",
                    whyItMatters = "Adding capacity at an operational step does not solve throughput when downstream processing is locked.",
                    recommendedCorrection = "Formulate 2 testable hypotheses regarding the bottleneck before selecting a solution.",
                    occurrenceCount = 1
                ),
                subject = "Critical Thinking",
                isResolved = false,
                lastAttemptDate = "3 days ago"
            ),
            MistakeEntry(
                item = MistakeItem(
                    id = "m_03",
                    mistakeTitle = "Neglecting Buffer Allowance in Tile Cuts",
                    whatHappened = "Calculated tile count strictly on room floor area without adding the 8% perimeter wastage buffer.",
                    competencyAffected = "Calculation Accuracy",
                    whyItMatters = "Perimeter cuts and obstacle corners reduce usable tile area, leaving project incomplete.",
                    recommendedCorrection = "Always apply: Total Tiles = (Net Floor Area x 1.08) / Tile Unit Area.",
                    occurrenceCount = 1
                ),
                subject = "Geometry",
                isResolved = true,
                lastAttemptDate = "Last week"
            )
        )
    }

    private val _mistakes = MutableStateFlow<List<MistakeEntry>>(initialMistakes)
    val mistakes: StateFlow<List<MistakeEntry>> = _mistakes.asStateFlow()

    fun resolveMistake(mistakeId: String) {
        val updated = _mistakes.value.map {
            if (it.item.id == mistakeId) it.copy(isResolved = true) else it
        }
        _mistakes.value = updated
    }

    fun addMistake(mistake: MistakeEntry) {
        _mistakes.value = listOf(mistake) + _mistakes.value
    }
}
