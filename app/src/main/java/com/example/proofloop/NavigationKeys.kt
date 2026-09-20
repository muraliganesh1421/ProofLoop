package com.example.proofloop

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Auth & Entry
@Serializable data object SplashKey : NavKey
@Serializable data object LoginKey : NavKey

// Main Bottom Bar Tabs & Primary Hubs
@Serializable data object HomeKey : NavKey
@Serializable data object MissionsKey : NavKey
@Serializable data object SkillsKey : NavKey
@Serializable data object PracticeKey : NavKey
@Serializable data object HistoryKey : NavKey
@Serializable data object ProofKey : NavKey

// Feature Hubs
@Serializable data object AiAssistantKey : NavKey
@Serializable data object GlobalSearchKey : NavKey
@Serializable data object MistakesKey : NavKey
@Serializable data object ProgressAnalyticsKey : NavKey
@Serializable data object ProfileKey : NavKey
@Serializable data object SettingsKey : NavKey

// Detail Pages
@Serializable data class MissionDetailKey(val missionId: String) : NavKey
@Serializable data class SkillDetailKey(val skillId: String) : NavKey
@Serializable data class HistoryDetailKey(val historyId: String) : NavKey

// Mission Execution Flow (Focused, Preserved)
@Serializable data object MissionBriefKey : NavKey
@Serializable data object CameraMissionKey : NavKey
@Serializable data object QuestionVoiceKey : NavKey
@Serializable data object InterviewKey : NavKey
@Serializable data object DefendSolutionKey : NavKey
@Serializable data class AiAnalysisKey(val isRetry: Boolean = false) : NavKey
@Serializable data object FollowUpChallengeKey : NavKey
@Serializable data object SkillReviewKey : NavKey
@Serializable data object MicroLessonKey : NavKey
@Serializable data object RetryMissionKey : NavKey
@Serializable data object ImprovementKey : NavKey
@Serializable data object ProofCardKey : NavKey
@Serializable data object SkillDashboardKey : NavKey
@Serializable data object LaptopBridgeKey : NavKey

