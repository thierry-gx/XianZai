package com.thierryguichardaz.xianzai.ui.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme

// --- Data Classes (Copied from your original MainActivity) ---
data class AnswerOption(
    val text: String,
    val points: Int
)

data class Question(
    val id: Int,
    val text: String,
    val options: List<AnswerOption>
)

val meqQuestions = listOf(
    Question(
        id = 1,
        text = "1. Approximately what time would you get up if you were entirely free to plan your day?",
        options = listOf(
            AnswerOption("a) 5:00 AM–6:30 AM (05:00–06:30 h)", 5),
            AnswerOption("b) 6:30 AM–7:45 AM (06:30–07:45 h)", 4),
            AnswerOption("c) 7:45 AM–9:45 AM (07:45–09:45 h)", 3),
            AnswerOption("d) 9:45 AM–11:00 AM (09:45–11:00 h)", 2),
            AnswerOption("e) 11:00 AM–12 noon (11:00–12:00 h)", 1)
        )
    ),
    Question(
        id = 2,
        text = "2. Approximately what time would you go to bed if you were entirely free to plan your evening?",
        options = listOf(
            AnswerOption("a) 8:00 PM–9:00 PM (20:00–21:00 h)", 5),
            AnswerOption("b) 9:00 PM–10:15 PM (21:00–22:15 h)", 4),
            AnswerOption("c) 10:15 PM–12:30 AM (22:15–00:30 h)", 3),
            AnswerOption("d) 12:30 AM–1:45 AM (00:30–01:45 h)", 2),
            AnswerOption("e) 1:45 AM–3:00 AM (01:45–03:00 h)", 1)
        )
    ),
    Question(
        id = 3,
        text = "3. If you usually have to get up at a specific time in the morning, how much do you depend on an alarm clock?",
        options = listOf(
            AnswerOption("a) Not at all", 4),
            AnswerOption("b) Slightly", 3),
            AnswerOption("c) Somewhat", 2),
            AnswerOption("d) Very much", 1)
        )
    ),
    Question(
        id = 4,
        text = "4. How easy do you find it to get up in the morning (when you are not awakened unexpectedly)?",
        options = listOf(
            AnswerOption("a) Very difficult", 1),
            AnswerOption("b) Somewhat difficult", 2),
            AnswerOption("c) Fairly easy", 3),
            AnswerOption("d) Very easy", 4)
        )
    ),
    Question(
        id = 5,
        text = "5. How alert do you feel during the first half hour after you wake up in the morning?",
        options = listOf(
            AnswerOption("a) Not at all alert", 1),
            AnswerOption("b) Slightly alert", 2),
            AnswerOption("c) Fairly alert", 3),
            AnswerOption("d) Very alert", 4)
        )
    ),
    Question(
        id = 6,
        text = "6. How hungry do you feel during the first half hour after you wake up?",
        options = listOf(
            AnswerOption("a) Not at all hungry", 1),
            AnswerOption("b) Slightly hungry", 2),
            AnswerOption("c) Fairly hungry", 3),
            AnswerOption("d) Very hungry", 4)
        )
    ),
    Question(
        id = 7,
        text = "7. During the first half hour after you wake up in the morning, how do you feel?",
        options = listOf(
            AnswerOption("a) Very tired", 1),
            AnswerOption("b) Fairly tired", 2),
            AnswerOption("c) Fairly refreshed", 3),
            AnswerOption("d) Very refreshed", 4)
        )
    ),
    Question(
        id = 8,
        text = "8. If you had no commitments the next day, what time would you go to bed compared to your usual bedtime?",
        options = listOf(
            AnswerOption("a) Seldom or never later", 4),
            AnswerOption("b) Less that 1 hour later", 3),
            AnswerOption("c) 1-2 hours later", 2),
            AnswerOption("d) More than 2 hours later", 1)
        )
    ),
    Question(
        id = 9,
        text = "9. You have decided to do physical exercise. A friend suggests that you do this for one hour twice a week, and the best time for him is between 7-8 AM (07-08 h). Bearing in mind nothing but your own internal “clock,” how do you think you would perform?",
        options = listOf(
            AnswerOption("a) Would be in good form", 4),
            AnswerOption("b) Would be in reasonable form", 3),
            AnswerOption("c) Would find it difficult", 2),
            AnswerOption("d) Would find it very difficult", 1)
        )
    ),
    Question(
        id = 10,
        text = "10. At approximately what time in the evening do you feel tired, and, as a result, in need of sleep?",
        options = listOf(
            AnswerOption("a) 8:00 PM–9:00 PM (20:00–21:00 h)", 5),
            AnswerOption("b) 9:00 PM–10:15 PM (21:00–22:15 h)", 4),
            AnswerOption("c) 10:15 PM–12:45 AM (22:15–00:45 h)", 3),
            AnswerOption("d) 12:45 AM–2:00 AM (00:45–02:00 h)", 2),
            AnswerOption("e) 2:00 AM–3:00 AM (02:00–03:00 h)", 1)
        )
    ),
    Question(
        id = 11,
        text = "11. You want to be at your peak performance for a test that you know is going to be mentally exhausting and will last two hours. You are entirely free to plan your day. Considering only your \"internal clock,\" which one of the four testing times would you choose?",
        options = listOf(
            AnswerOption("a) 8 AM–10 AM (08–10 h)", 6),
            AnswerOption("b) 11 AM–1 PM (11–13 h)", 4),
            AnswerOption("c) 3 PM–5 PM (15–17 h)", 2),
            AnswerOption("d) 7 PM–9 PM (19–21 h)", 0)
        )
    ),
    Question(
        id = 12,
        text = "12. If you got into bed at 11 PM (23 h), how tired would you be?",
        options = listOf(
            AnswerOption("a) Not at all tired", 0),
            AnswerOption("b) A little tired", 2),
            AnswerOption("c) Fairly tired", 3),
            AnswerOption("d) Very tired", 5)
        )
    ),
    Question(
        id = 13,
        text = "13. For some reason you have gone to bed several hours later than usual, but there is no need to get up at any particular time the next morning. Which one of the following are you most likely to do?",
        options = listOf(
            AnswerOption("a) Will wake up at usual time, but will not fall back asleep", 4),
            AnswerOption("b) Will wake up at usual time and will doze thereafter", 3),
            AnswerOption("c) Will wake up at usual time, but will fall asleep again", 2),
            AnswerOption("d) Will not wake up until later than usual", 1)
        )
    ),
    Question(
        id = 14,
        text = "14. One night you have to remain awake between 4-6 AM (04-06 h) in order to carry out a night watch. You have no time commitments the next day. Which one of the alternatives would suit you best?",
        options = listOf(
            AnswerOption("a) Would not go to bed until the watch is over", 1),
            AnswerOption("b) Would take a nap before and sleep after", 2),
            AnswerOption("c) Would take a good sleep before and nap after", 3),
            AnswerOption("d) Would sleep only before the watch", 4)
        )
    ),
    Question(
        id = 15,
        text = "15. You have two hours of hard physical work. You are entirely free to plan your day. Considering only your internal “clock,” which of the following times would you choose?",
        options = listOf(
            AnswerOption("a) 8 AM–10 AM (08–10 h)", 4),
            AnswerOption("b) 11 AM-1 PM (11–13 h)", 3),
            AnswerOption("c) 3 PM–5 PM (15–17 h)", 2),
            AnswerOption("d) 7 PM–9 PM (19–21 h)", 1)
        )
    ),
    Question(
        id = 16,
        text = "16. You have decided to do physical exercise. A friend suggests that you do this for one hour twice a week. The best time for her is between 10-11 PM (22-23 h). Bearing in mind only your internal “clock,” how well do you think you would perform?",
        options = listOf(
            AnswerOption("a) Would be in good form", 1),
            AnswerOption("b) Would be in reasonable form", 2),
            AnswerOption("c) Would find it difficult", 3),
            AnswerOption("d) Would find it very difficult", 4)
        )
    ),
    Question(
        id = 17,
        text = "17. Suppose you can choose your own work hours. Assume that you work a five-hour day (including breaks), your job is interesting, and you are paid based on your performance. At approximately what time would you choose to begin?",
        options = listOf(
            AnswerOption("a) 5 hours starting between 4–8 AM (04-08 h)", 5),
            AnswerOption("b) 5 hours starting between 8–9 AM (08–09 h)", 4),
            AnswerOption("c) 5 hours starting between 9 AM–2 PM (09–14 h)", 3),
            AnswerOption("d) 5 hours starting between 2–5 PM (14–17 h)", 2),
            AnswerOption("e) 5 hours starting between 5 PM–4 AM (17–04 h)", 1)
        )
    ),
    Question(
        id = 18,
        text = "18. At approximately what time of day do you usually feel your best?",
        options = listOf(
            AnswerOption("a) 5–8 AM (05–08 h)", 5),
            AnswerOption("b) 8–10 AM (08–10 h)", 4),
            AnswerOption("c) 10 AM–5 PM (10–17 h)", 3),
            AnswerOption("d) 5–10 PM (17–22 h)", 2),
            AnswerOption("e) 10 PM–5 AM (22–05 h)", 1)
        )
    ),
    Question(
        id = 19,
        text = "19. One hears about \"morning types\" and \"evening types.\" Which one of these types do you consider yourself to be?",
        options = listOf(
            AnswerOption("a) Definitely a morning type", 6),
            AnswerOption("b) Rather more a morning type than an evening type", 4),
            AnswerOption("c) Rather more an evening type than a morning type", 2),
            AnswerOption("d) Definitely an evening type", 1)
        )
    )
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(onQuizComplete: (chronotype: String) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }
    var totalScore by remember { mutableStateOf<Int?>(null) }
    var chronotypeResult by remember { mutableStateOf<String?>(null) }

    val currentQuestion = meqQuestions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("XianZai - Chronotype Quiz") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (totalScore == null) {
                    Text(
                        "Question ${currentQuestionIndex + 1} of ${meqQuestions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator( // UPDATED
                        progress = { (currentQuestionIndex + 1) / meqQuestions.size.toFloat() },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                } else {
                    Spacer(modifier = Modifier.height(60.dp))
                }

                AnimatedContent(
                    targetState = currentQuestionIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            // UPDATED
                            slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300))
                                .togetherWith(slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)))
                        } else {
                            // UPDATED
                            slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300))
                                .togetherWith(slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)))
                        }.using(SizeTransform(clip = false))
                    },
                    modifier = Modifier.weight(1f)
                ) { targetIndex ->
                    if (totalScore != null && chronotypeResult != null) {
                        ResultCard(score = totalScore!!, chronotype = chronotypeResult!!)
                    } else {
                        val questionToShow = meqQuestions[targetIndex]
                        QuestionCardEnhanced(
                            question = questionToShow,
                            selectedOptionIndex = selectedAnswers[questionToShow.id],
                            onOptionSelected = { selectedIndex ->
                                selectedAnswers[questionToShow.id] = selectedIndex
                                if (targetIndex == meqQuestions.size - 1) {
                                    totalScore = null
                                    chronotypeResult = null
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (totalScore == null || chronotypeResult == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (currentQuestionIndex > 0) Arrangement.SpaceBetween else Arrangement.End
                    ) {
                        if (currentQuestionIndex > 0) {
                            Button(
                                onClick = {
                                    if (currentQuestionIndex > 0) {
                                        currentQuestionIndex--
                                        if (totalScore != null) {
                                            totalScore = null
                                            chronotypeResult = null
                                        }
                                    }
                                },
                                enabled = currentQuestionIndex > 0
                            ) {
                                Text("Previous")
                            }
                        }

                        val isLastQuestion = currentQuestionIndex == meqQuestions.size - 1
                        val canProceed = selectedAnswers[currentQuestion.id] != null
                        val allQuestionsAnswered = selectedAnswers.size == meqQuestions.size

                        Button(
                            onClick = {
                                if (isLastQuestion && allQuestionsAnswered) {
                                    var score = 0
                                    meqQuestions.forEach { q ->
                                        selectedAnswers[q.id]?.let { answerIndex ->
                                            score += q.options[answerIndex].points
                                        }
                                    }
                                    totalScore = score
                                    chronotypeResult = determineChronotype(score)
                                } else if (!isLastQuestion && canProceed) {
                                    currentQuestionIndex++
                                }
                            },
                            enabled = if (isLastQuestion) allQuestionsAnswered else canProceed
                        ) {
                            Text(if (isLastQuestion) "Calculate" else "Next")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            currentQuestionIndex = 0
                            selectedAnswers.clear()
                            totalScore = null
                            chronotypeResult = null
                        }) {
                            Text("Retake Quiz")
                        }
                        Button(onClick = {
                            chronotypeResult?.let { onQuizComplete(it) }
                        }) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ResultCard(score: Int, chronotype: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz Complete!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Total Score: $score",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Your Chronotype:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                chronotype,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            HorizontalDivider( // UPDATED
                modifier = Modifier.padding(vertical = 24.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Text(
                text = getChronotypeComment(score),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun QuestionCardEnhanced(
    question: Question,
    selectedOptionIndex: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { onOptionSelected(index) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${('a' + index)})",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = option.text.replaceFirst(Regex("^[a-e]\\) "), ""),
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }
            }
        }
    }
}

fun determineChronotype(score: Int): String {
    return when (score) {
        in 16..30 -> "Definite Evening"
        in 31..41 -> "Moderate Evening"
        in 42..58 -> "Intermediate"
        in 59..69 -> "Moderate Morning"
        in 70..86 -> "Definite Morning"
        else -> "Invalid score"
    }
}

fun getChronotypeComment(score: Int): String {
    return when (score) {
        in 16..30 -> "You are most productive late in the day or even at night. If possible, schedule demanding tasks for the afternoon or evening. Try to maintain a consistent sleep routine, and consider limiting evening screen time to improve morning alertness."
        in 31..41 -> "You tend to feel more focused and energized in the late afternoon. To boost productivity, avoid scheduling important meetings or tasks early in the morning. Gradually adjusting your bedtime and using morning light exposure may help shift your rhythm."
        in 42..58 -> "You don’t show a strong preference for mornings or evenings, which gives you flexibility. Take note of your personal energy peaks throughout the day and try to align important work with those times. Maintain consistent sleep and wake times for best performance."
        in 59..69 -> "You function well in the morning and likely feel sharp soon after waking up. Leverage this by scheduling mentally demanding tasks early in the day. Be cautious not to overcommit in the evening when your energy naturally dips."
        in 70..86 -> "You are most alert and productive in the early morning. Aim to complete key tasks before midday. Prioritize winding down in the evening to protect your early bedtime, and avoid stimulants or heavy meals late in the day."
        else -> "No specific advice available for this score."
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun QuizScreenPreview() {
    XianZaiTheme {
        QuizScreen(onQuizComplete = { chronotype -> println("Quiz complete: $chronotype") })
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ResultCardPreview() {
    XianZaiTheme {
        ResultCard(score = 50, chronotype = "Intermediate")
    }
}