package com.example.proofloop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.proofloop.data.auth.SessionManager
import com.example.proofloop.data.repository.MissionRepository
import com.example.proofloop.ui.components.NavTab
import com.example.proofloop.ui.components.ProofLoopBottomNav
import com.example.proofloop.ui.screens.AiAnalysisScreen
import com.example.proofloop.ui.screens.AiAssistantScreen
import com.example.proofloop.ui.screens.CameraMissionScreen
import com.example.proofloop.ui.screens.DefendSolutionScreen
import com.example.proofloop.ui.screens.FollowUpChallengeScreen
import com.example.proofloop.ui.screens.GlobalSearchScreen
import com.example.proofloop.ui.screens.HistoryDetailScreen
import com.example.proofloop.ui.screens.HistoryScreen
import com.example.proofloop.ui.screens.HomeScreen
import com.example.proofloop.ui.screens.ImprovementScreen
import com.example.proofloop.ui.screens.InterviewScreen
import com.example.proofloop.ui.screens.LaptopBridgeScreen
import com.example.proofloop.ui.screens.LoginScreen
import com.example.proofloop.ui.screens.MicroLessonScreen
import com.example.proofloop.ui.screens.MissionBriefScreen
import com.example.proofloop.ui.screens.MissionDetailScreen
import com.example.proofloop.ui.screens.MissionsScreen
import com.example.proofloop.ui.screens.MistakesScreen
import com.example.proofloop.ui.screens.PracticeScreen
import com.example.proofloop.ui.screens.ProfileScreen
import com.example.proofloop.ui.screens.ProgressAnalyticsScreen
import com.example.proofloop.ui.screens.ProofCardScreen
import com.example.proofloop.ui.screens.QuestionVoiceScreen
import com.example.proofloop.ui.screens.RetryMissionScreen
import com.example.proofloop.ui.screens.SettingsScreen
import com.example.proofloop.ui.screens.SkillDashboardScreen
import com.example.proofloop.ui.screens.SkillDetailScreen
import com.example.proofloop.ui.screens.SkillReviewScreen
import com.example.proofloop.ui.screens.SkillsScreen
import com.example.proofloop.ui.screens.SplashScreen

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    val backStack = rememberNavBackStack(SplashKey)
    val currentKey = backStack.lastOrNull()

    val showBottomBar = currentKey in listOf(HomeKey, SkillsKey, MissionsKey, PracticeKey, ProgressAnalyticsKey)

    val currentTab = when (currentKey) {
        HomeKey -> NavTab.HOME
        SkillsKey -> NavTab.SKILLS
        MissionsKey -> NavTab.MISSIONS
        PracticeKey -> NavTab.PRACTICE
        ProgressAnalyticsKey -> NavTab.PROGRESS
        else -> NavTab.HOME
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ProofLoopBottomNav(
                    currentTab = currentTab,
                    onSelectTab = { tab ->
                        val targetKey = when (tab) {
                            NavTab.HOME -> HomeKey
                            NavTab.SKILLS -> SkillsKey
                            NavTab.MISSIONS -> MissionsKey
                            NavTab.PRACTICE -> PracticeKey
                            NavTab.PROGRESS -> ProgressAnalyticsKey
                        }
                        if (currentKey != targetKey) {
                            backStack.clear()
                            backStack.add(targetKey)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                },
                entryProvider = entryProvider {
                    entry<SplashKey> {
                        SplashScreen(
                            onNavigateToHome = {
                                backStack.clear()
                                // Skip login if session is already active
                                if (isLoggedIn) {
                                    backStack.add(HomeKey)
                                } else {
                                    backStack.add(LoginKey)
                                }
                            }
                        )
                    }

                    entry<LoginKey> {
                        LoginScreen(
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            }
                        )
                    }

                    entry<HomeKey> {
                        HomeScreen(
                            onStartMissionClick = { backStack.add(MissionDetailKey(com.example.proofloop.data.repository.MissionRepository.currentMission.value.id)) },
                            onDashboardClick = { backStack.add(SkillsKey) },
                            onLaptopBridgeClick = { backStack.add(LaptopBridgeKey) },
                            onSearchClick = { backStack.add(GlobalSearchKey) },
                            onProfileClick = { backStack.add(ProfileKey) },
                            onPracticeClick = { backStack.add(PracticeKey) },
                            onMistakesClick = { backStack.add(MistakesKey) },
                            onAiAssistantClick = { backStack.add(AiAssistantKey) },
                            onCameraClick = { backStack.add(CameraMissionKey) },
                            onMissionsClick = { backStack.add(MissionsKey) }
                        )
                    }

                    entry<PracticeKey> {
                        PracticeScreen(
                            onOpenAiAssistant = { topic -> backStack.add(AiAssistantKey) }
                        )
                    }

                    entry<AiAssistantKey> {
                        AiAssistantScreen(
                            onBackClick = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<GlobalSearchKey> {
                        GlobalSearchScreen(
                            onBackClick = { backStack.removeLastOrNull() },
                            onSelectResult = { category, id ->
                                when (category.lowercase()) {
                                    "skills" -> backStack.add(SkillDetailKey(id))
                                    "missions" -> backStack.add(MissionDetailKey(id))
                                    "practice" -> backStack.add(PracticeKey)
                                    "history" -> backStack.add(HistoryDetailKey(id))
                                }
                            }
                        )
                    }

                    entry<MistakesKey> {
                        MistakesScreen(
                            onBackClick = { backStack.removeLastOrNull() },
                            onReviewMicroLesson = { backStack.add(MicroLessonKey) },
                            onReattemptChallenge = { backStack.add(RetryMissionKey) }
                        )
                    }

                    entry<ProgressAnalyticsKey> {
                        ProgressAnalyticsScreen(
                            onBackClick = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<ProfileKey> {
                        ProfileScreen(
                            onBackClick = { backStack.removeLastOrNull() },
                            onOpenSettings = { backStack.add(SettingsKey) },
                            onLogoutClick = {
                                backStack.clear()
                                backStack.add(LoginKey)
                            }
                        )
                    }

                    entry<SettingsKey> {
                        SettingsScreen(
                            onBackClick = { backStack.removeLastOrNull() },
                            onLogoutClick = {
                                backStack.clear()
                                backStack.add(LoginKey)
                            }
                        )
                    }


                    entry<MissionsKey> {
                        MissionsScreen(
                            onSelectMissionDetail = { missionId ->
                                backStack.add(MissionDetailKey(missionId))
                            }
                        )
                    }

                    entry<MissionDetailKey> { key ->
                        MissionDetailScreen(
                            missionId = key.missionId,
                            onStartExecution = {
                                backStack.add(CameraMissionKey)
                            },
                            onBackClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }

                    entry<SkillsKey> {
                        SkillsScreen(
                            onSelectSkillDetail = { skillId ->
                                backStack.add(SkillDetailKey(skillId))
                            }
                        )
                    }

                    entry<SkillDetailKey> { key ->
                        SkillDetailScreen(
                            skillId = key.skillId,
                            onBackClick = { backStack.removeLastOrNull() },
                            onPracticeMission = { missionId ->
                                com.example.proofloop.data.repository.MissionRepository.selectMission(missionId)
                                backStack.add(MissionDetailKey(missionId))
                            }
                        )
                    }

                    entry<HistoryKey> {
                        HistoryScreen(
                            onSelectHistoryDetail = { historyId ->
                                backStack.add(HistoryDetailKey(historyId))
                            }
                        )
                    }

                    entry<HistoryDetailKey> { key ->
                        HistoryDetailScreen(
                            historyId = key.historyId,
                            onBackClick = { backStack.removeLastOrNull() },
                            onPracticeNextMission = { missionId ->
                                com.example.proofloop.data.repository.MissionRepository.selectMission(missionId)
                                backStack.add(MissionDetailKey(missionId))
                            }
                        )
                    }

                    entry<ProofKey> {
                        ProofCardScreen(
                            onDashboardClick = {
                                backStack.clear()
                                backStack.add(SkillsKey)
                            },
                            onNextMissionClick = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            }
                        )
                    }

                    // Focused Mission Execution Steps
                    entry<MissionBriefKey> {
                        MissionBriefScreen(
                            onStartMissionClick = { backStack.add(CameraMissionKey) },
                            onBackClick = { backStack.removeLastOrNull() }
                        )
                    }

                    entry<CameraMissionKey> {
                        CameraMissionScreen(
                            onContinueClick = { backStack.add(QuestionVoiceKey) }
                        )
                    }

                    entry<QuestionVoiceKey> {
                        QuestionVoiceScreen(
                            onContinueClick = { backStack.add(InterviewKey) }
                        )
                    }

                    entry<InterviewKey> {
                        InterviewScreen(
                            onContinueClick = { backStack.add(DefendSolutionKey) }
                        )
                    }

                    entry<DefendSolutionKey> {
                        DefendSolutionScreen(
                            onContinueClick = { backStack.add(FollowUpChallengeKey) }
                        )
                    }

                    entry<FollowUpChallengeKey> {
                        FollowUpChallengeScreen(
                            onAnalyzeClick = { backStack.add(AiAnalysisKey(isRetry = false)) }
                        )
                    }

                    entry<AiAnalysisKey> { key ->
                        AiAnalysisScreen(
                            isRetry = key.isRetry,
                            onCompleted = {
                                if (!key.isRetry) {
                                    // ── Fix #4: Mission Flow Branching ──────────────────────────
                                    // High performers (≥85 overallScore) skip Corrective Retry and
                                    // jump straight to the Improvement/ProofCard path.
                                    val score = MissionRepository.firstEvaluation.value?.overallScore ?: 0
                                    if (score >= 85) {
                                        backStack.add(ImprovementKey)
                                    } else {
                                        backStack.add(SkillReviewKey)
                                    }
                                    // ────────────────────────────────────────────────────────────
                                } else {
                                    backStack.add(ImprovementKey)
                                }
                            }
                        )
                    }

                    entry<SkillReviewKey> {
                        SkillReviewScreen(
                            onTryAgainClick = { backStack.add(MicroLessonKey) }
                        )
                    }

                    entry<MicroLessonKey> {
                        MicroLessonScreen(
                            onContinueToRetryClick = { backStack.add(RetryMissionKey) }
                        )
                    }

                    entry<RetryMissionKey> {
                        RetryMissionScreen(
                            onSubmitRetryClick = { backStack.add(AiAnalysisKey(isRetry = true)) }
                        )
                    }

                    entry<ImprovementKey> {
                        ImprovementScreen(
                            onViewProofClick = { backStack.add(ProofKey) }
                        )
                    }

                    entry<ProofCardKey> {
                        ProofCardScreen(
                            onDashboardClick = {
                                backStack.clear()
                                backStack.add(SkillsKey)
                            },
                            onNextMissionClick = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            }
                        )
                    }

                    entry<SkillDashboardKey> {
                        SkillDashboardScreen(
                            onHomeClick = {
                                backStack.clear()
                                backStack.add(HomeKey)
                            },
                            onLaptopBridgeClick = { backStack.add(LaptopBridgeKey) },
                            onViewProofClick = { backStack.add(ProofKey) }
                        )
                    }

                    entry<LaptopBridgeKey> {
                        LaptopBridgeScreen(
                            onBackClick = { backStack.removeLastOrNull() }
                        )
                    }
                }
            )
        }
    }
}
