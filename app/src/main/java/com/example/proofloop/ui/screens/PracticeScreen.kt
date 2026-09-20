package com.example.proofloop.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.data.local.LocalProofStorage
import com.example.proofloop.data.local.SubjectContentBank
import com.example.proofloop.data.repository.PracticeRepository
import com.example.proofloop.domain.models.ProofQuestion
import com.example.proofloop.domain.models.QuestionState
import com.example.proofloop.domain.models.QuestionType
import com.example.proofloop.domain.models.SubjectType
import com.example.proofloop.theme.BgCard
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.BgSurface
import com.example.proofloop.theme.BorderSubtle
import com.example.proofloop.theme.CoralAlert
import com.example.proofloop.theme.IqooOrange
import com.example.proofloop.theme.IqooYellow
import com.example.proofloop.theme.MintProof
import com.example.proofloop.theme.ProofLoopCardBg
import com.example.proofloop.theme.ProofLoopCardBorder
import com.example.proofloop.theme.ProofLoopYellow
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary

private data class PracticeModuleItem(
    val title: String,
    val questionCount: Int,
    val difficulty: String,
    val subject: SubjectType,
    val iconColor: Color,
    val iconBg: Color,
    val icon: ImageVector,
    val prerequisiteTopicKeyword: String? = null   // null = no prerequisite
)

@Composable
fun PracticeScreen(
    onOpenAiAssistant: (String) -> Unit = {},
    repository: PracticeRepository = remember { PracticeRepository.getInstance() }
) {
    val context = LocalContext.current
    val questions by repository.questions.collectAsState()
    val currentIndex by repository.currentQuestionIndex.collectAsState()
    val selectedSubject by repository.selectedSubject.collectAsState()
    val questionState by repository.questionState.collectAsState()
    val lastEvaluation by repository.lastEvaluation.collectAsState()

    // Prerequisite mastery data — mirrors ProgressAnalyticsScreen pattern
    val storage = remember { LocalProofStorage.getInstance(context) }
    val allAttempts by storage.attemptsFlow.collectAsState(initial = emptyList())


    var isSolvingMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("Mathematics") }

    val filterTabs = listOf("All", "Mathematics", "Physics", "Chemistry", "Computer Science")

    val practiceModules = listOf(
        PracticeModuleItem("Linear Equations", 10, "Medium", SubjectType.MATHEMATICS, Color(0xFF42A5F5), Color(0xFF102338), Icons.Default.Calculate),
        PracticeModuleItem("Quadratic Equations", 10, "Hard", SubjectType.MATHEMATICS, Color(0xFFAB47BC), Color(0xFF28132F), Icons.Default.Calculate, prerequisiteTopicKeyword = "linear"),
        PracticeModuleItem("Geometry Basics", 10, "Easy", SubjectType.MATHEMATICS, Color(0xFF26A69A), Color(0xFF0F2B26), Icons.Default.Functions),
        PracticeModuleItem("Trigonometry Ratios", 10, "Medium", SubjectType.MATHEMATICS, Color(0xFF5C6BC0), Color(0xFF171B38), Icons.Default.Calculate, prerequisiteTopicKeyword = "geometry"),
        PracticeModuleItem("Probability", 10, "Medium", SubjectType.MATHEMATICS, Color(0xFF29B6F6), Color(0xFF0E2538), Icons.Default.Functions),
        PracticeModuleItem("Kinematics Motion", 8, "Medium", SubjectType.PHYSICS, Color(0xFFFF7043), Color(0xFF331B13), Icons.Default.Psychology),
        PracticeModuleItem("Programming Loops", 10, "Easy", SubjectType.PROGRAMMING, Color(0xFF7E57C2), Color(0xFF1F1532), Icons.Default.Calculate)
    )


    if (!isSolvingMode) {
        // ── SCREEN 4: Practice Modules Catalog ──
        val filteredModules = practiceModules.filter { m ->
            val matchesTab = when (selectedFilterTab) {
                "Mathematics" -> m.subject == SubjectType.MATHEMATICS
                "Physics" -> m.subject == SubjectType.PHYSICS
                "Computer Science" -> m.subject == SubjectType.COMPUTER_SCIENCE || m.subject == SubjectType.PROGRAMMING
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchesQ = q.isEmpty() || m.title.lowercase().contains(q)
            matchesTab && matchesQ
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Practice",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Test your knowledge. Grow your skills.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ProofLoopCardBg)
                    .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search questions...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = tab == selectedFilterTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) ProofLoopYellow else ProofLoopCardBg)
                            .border(
                                1.dp,
                                if (isSelected) ProofLoopYellow else ProofLoopCardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilterTab = tab }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Module Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredModules) { module ->
                    // Compute prerequisite mastery (reuse same accuracy pattern as ProgressAnalyticsScreen)
                    val isLocked: Boolean
                    val prereqLabel: String
                    val prereq = module.prerequisiteTopicKeyword
                    if (prereq == null) {
                        isLocked = false
                        prereqLabel = ""
                    } else {
                        val prereqAttempts = allAttempts.filter {
                            it.topic.contains(prereq, ignoreCase = true) ||
                            it.skill.contains(prereq, ignoreCase = true)
                        }
                        val prereqCorrect = prereqAttempts.count { it.isCorrect }
                        val prereqAccuracy = if (prereqAttempts.isNotEmpty())
                            prereqCorrect * 100 / prereqAttempts.size else 0
                        isLocked = prereqAccuracy < 70
                        prereqLabel = module.title.replaceFirstChar { it.uppercase() }
                    }
                    PracticeModuleCard(
                        module = module,
                        isLocked = isLocked,
                        prereqLabel = prereqLabel,
                        onStart = {
                            repository.filterBySubject(module.subject)
                            isSolvingMode = true
                        }
                    )
                }
            }
        }
    } else {
        // ── DISTRACTION-FREE SOLVING VIEW WITH STRICT ANSWER GATING ──
        val currentQuestion: ProofQuestion = questions.getOrNull(currentIndex) ?: SubjectContentBank.questions[0]
        var userTextAnswer by remember(currentIndex) { mutableStateOf("") }
        var selectedOption by remember(currentIndex) { mutableStateOf("") }
        var showHint by remember(currentIndex) { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            // Top Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isSolvingMode = false }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modules", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ProofLoopCardBg)
                        .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Q ${currentIndex + 1} / ${questions.size}",
                        fontSize = 12.sp,
                        color = ProofLoopYellow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ProofLoopCardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentQuestion.subject.name.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProofLoopYellow
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2A2000))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = currentQuestion.difficulty.name,
                                fontSize = 10.sp,
                                color = ProofLoopYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ProofLoopYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Skill: ${currentQuestion.skill} • Topic: ${currentQuestion.topic}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Answer Input Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ProofLoopCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BgSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR SOLUTION ATTEMPT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    when (currentQuestion.type) {
                        QuestionType.MULTIPLE_CHOICE -> {
                            currentQuestion.options.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (questionState == QuestionState.QUESTION_NOT_ATTEMPTED) {
                                                selectedOption = option
                                            }
                                        }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedOption == option,
                                        onClick = {
                                            if (questionState == QuestionState.QUESTION_NOT_ATTEMPTED) {
                                                selectedOption = option
                                            }
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = ProofLoopYellow)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = option, color = TextPrimary, fontSize = 14.sp)
                                }
                            }
                        }
                        else -> {
                            OutlinedTextField(
                                value = userTextAnswer,
                                onValueChange = { userTextAnswer = it },
                                placeholder = { Text("Write your exact answer or derivation...", color = TextMuted, fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = questionState == QuestionState.QUESTION_NOT_ATTEMPTED,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BgCard,
                                    unfocusedContainerColor = BgCard,
                                    focusedBorderColor = ProofLoopYellow,
                                    unfocusedBorderColor = ProofLoopCardBorder
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hints & Socratic Assistant
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        showHint = true
                        repository.markHintUsed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ProofLoopYellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Need a Hint?", fontSize = 12.sp, color = TextPrimary)
                }

                Button(
                    onClick = {
                        repository.markAiUsed()
                        onOpenAiAssistant(currentQuestion.topic)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF221638)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF8C52FF), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Tutor", fontSize = 12.sp, color = Color.White)
                }
            }

            // Display active hint
            if (showHint) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A12)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ProofLoopYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currentQuestion.hintText, color = ProofLoopYellow, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Evaluation Result
            AnimatedVisibility(visible = questionState != QuestionState.QUESTION_NOT_ATTEMPTED) {
                val eval = lastEvaluation
                if (eval != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (eval.isCorrect) Color(0xFF0D2518) else Color(0xFF2A1012)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (eval.isCorrect) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (eval.isCorrect) MintProof else CoralAlert,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (eval.isCorrect) "VERIFIED CORRECT (+25 XP)" else "REASONING GAP DETECTED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (eval.isCorrect) MintProof else CoralAlert
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = eval.feedbackMessage, color = TextPrimary, fontSize = 13.sp)
                            if (!eval.isCorrect) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Correct Answer: ${currentQuestion.correctAnswer}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            if (questionState == QuestionState.QUESTION_NOT_ATTEMPTED) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ProofLoopYellow)
                        .clickable {
                            val finalAns = if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE) selectedOption else userTextAnswer.trim()
                            if (finalAns.isNotBlank()) {
                                repository.submitAnswer(finalAns)
                            } else {
                                Toast.makeText(context, "Please enter or select an answer first", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("VERIFY MY PROOF", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ProofLoopYellow)
                        .clickable {
                            if (currentIndex < questions.size - 1) {
                                repository.nextQuestion()
                            } else {
                                isSolvingMode = false
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (currentIndex < questions.size - 1) "NEXT QUESTION" else "COMPLETE PRACTICE MODULE",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun PracticeModuleCard(
    module: PracticeModuleItem,
    isLocked: Boolean = false,
    prereqLabel: String = "",
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ProofLoopCardBg)
            .border(
                1.dp,
                if (isLocked) ProofLoopCardBorder.copy(alpha = 0.4f) else ProofLoopCardBorder,
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLocked) module.iconBg.copy(alpha = 0.4f) else module.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = null,
                    tint = if (isLocked) module.iconColor.copy(alpha = 0.35f) else module.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = module.title,
                    color = if (isLocked) TextMuted else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (isLocked) {
                    Text(
                        text = "Complete prerequisite at 70%+ to unlock",
                        color = CoralAlert.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "${module.questionCount} questions • ${module.difficulty}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isLocked) {
                // Lock badge — not tappable
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2A1A1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = CoralAlert.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                // Yellow Pill "Start" Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ProofLoopYellow)
                        .clickable { onStart() }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
