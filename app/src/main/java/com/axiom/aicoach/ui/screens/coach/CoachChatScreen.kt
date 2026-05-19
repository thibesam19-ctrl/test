package com.axiom.aicoach.ui.screens.coach

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
)

private val coachResponses = mapOf(
    "weight" to "Based on your 14-day trend, your weight is moving in the right direction — down about 0.4 kg/week. That's a healthy, sustainable rate. Keep it consistent! 💪",
    "workout" to "You've completed 3 workouts this week — great job! I'd recommend not skipping your planned upper body session tomorrow. Consistency over perfection.",
    "eat" to "Looking at your remaining macros for today, I'd suggest a meal with about 40g protein and 50g carbs. Something like grilled chicken with rice or a protein shake with banana would work perfectly.",
    "sore" to "Muscle soreness means your body is adapting — that's a good sign! Make sure to get 7-8 hours of sleep tonight, stay hydrated, and consider a light walk or foam rolling for recovery.",
    "tired" to "Fatigue can signal that your body needs rest. Take a full rest day today. Tomorrow, focus on quality sleep and nutrition. Overtraining is counterproductive.",
    "plan" to "I can adjust your plan! Tell me what you'd like to change — more or fewer days, different split (PPL vs Upper/Lower), or different focus areas and I'll update it for you.",
    "protein" to "For your goal, aim for 1.6–2.2g of protein per kg of bodyweight. At your current weight, that's roughly 128–175g per day. Your current intake is tracking well!",
    "skip" to "Missing one workout isn't the end of the world — but consistency is where results come from. If you're not injured or sick, I'd encourage you to do at least a 20-minute version. Even half is better than none.",
    "default" to "That's a great question! I'm here to help with your fitness journey. Whether it's nutrition, workouts, recovery, or motivation — just ask. I'm powered by Axiom's coaching engine. 🤖\n\n*Note: I'm an AI assistant, not a doctor or licensed trainer. Always consult a professional for medical advice.*",
)

private fun getRuleBasedResponse(message: String): String {
    val lower = message.lowercase()
    return when {
        "weight" in lower || "scale" in lower || "fat" in lower -> coachResponses["weight"]!!
        "workout" in lower || "exercise" in lower || "train" in lower -> coachResponses["workout"]!!
        "eat" in lower || "food" in lower || "meal" in lower || "hungry" in lower -> coachResponses["eat"]!!
        "sore" in lower || "ache" in lower || "pain" in lower -> coachResponses["sore"]!!
        "tired" in lower || "fatigue" in lower || "exhausted" in lower -> coachResponses["tired"]!!
        "plan" in lower || "adjust" in lower || "change" in lower -> coachResponses["plan"]!!
        "protein" in lower || "macro" in lower -> coachResponses["protein"]!!
        "skip" in lower || "miss" in lower || "lazy" in lower -> coachResponses["skip"]!!
        "skinny" in lower || "hate my body" in lower || "not eaten" in lower ->
            "I hear that you're struggling — those feelings are valid. Your worth has nothing to do with your body size. I'd encourage you to speak with a professional if these thoughts are weighing on you. You can reach a counselor at any time. 💙\n\nIn the meantime, let's focus on *feeling strong and healthy*, not a number. What would feel good to work on today?"
        else -> coachResponses["default"]!!
    }
}

@Composable
fun CoachChatScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "intro",
                    role = "assistant",
                    content = "Hey! I'm Coach Axiom — your personal AI fitness coach. I'm here to help you with workouts, nutrition, recovery, and motivation.\n\nWhat's on your mind today? 💪",
                )
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestedPrompts = listOf(
        "Why am I not losing weight?",
        "What should I eat today?",
        "I'm feeling sore after yesterday",
        "Adjust my workout plan",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding(),
    ) {
        // ── Custom top bar ────────────────────────────────────────────────────
        CoachTopBar(onBack = onBack)

        // ── Message list ──────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item { Spacer(Modifier.height(Spacing.md)) }

            items(messages, key = { it.id }) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                ) {
                    MessageBubble(message)
                }
            }

            if (isTyping) {
                item { TypingIndicator() }
            }

            item { Spacer(Modifier.height(Spacing.md)) }
        }

        // ── Suggested prompts ─────────────────────────────────────────────────
        if (messages.size <= 2) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                items(suggestedPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(Radius.xl)
                            .border(1.dp, colors.border, Radius.xl)
                            .clickable {
                                val userMsg = ChatMessage(
                                    id = System.currentTimeMillis().toString(),
                                    role = "user",
                                    content = prompt,
                                )
                                messages = messages + userMsg
                                isTyping = true
                                scope.launch {
                                    delay(1200)
                                    val response = getRuleBasedResponse(prompt)
                                    isTyping = false
                                    messages = messages + ChatMessage(
                                        id = (System.currentTimeMillis() + 1).toString(),
                                        role = "assistant",
                                        content = response,
                                    )
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary,
                        )
                    }
                }
            }
        }

        // ── Input bar ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.Bottom,
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask your coach anything…", color = colors.textMuted) },
                modifier = Modifier.weight(1f),
                shape = Radius.xl,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = colors.borderSubtle,
                    unfocusedContainerColor = colors.borderSubtle,
                ),
            )
            Spacer(Modifier.width(Spacing.lg))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank()) colors.primary else colors.borderSubtle
                    )
                    .clickable(enabled = inputText.isNotBlank()) {
                        val text = inputText.trim()
                        if (text.isEmpty()) return@clickable
                        inputText = ""
                        val userMsg = ChatMessage(
                            id = System.currentTimeMillis().toString(),
                            role = "user",
                            content = text,
                        )
                        messages = messages + userMsg
                        isTyping = true
                        scope.launch {
                            delay((800L..2000L).random())
                            val response = getRuleBasedResponse(text)
                            isTyping = false
                            messages = messages + ChatMessage(
                                id = (System.currentTimeMillis() + 1).toString(),
                                role = "assistant",
                                content = response,
                            )
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) colors.textOnPrimary else colors.textMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // ── Safety notice ─────────────────────────────────────────────────────
        Text(
            text = "Axiom AI is not a medical professional. Always consult a doctor.",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl, vertical = Spacing.sm)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun CoachTopBar(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = Spacing.sm, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = colors.textPrimary,
            )
        }
        Spacer(Modifier.width(Spacing.sm))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🤖", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(Spacing.lg))
        Column {
            Text(
                text = "Coach Axiom",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Text(
                text = "AI-powered · Always here",
                style = MaterialTheme.typography.labelSmall,
                color = colors.success,
            )
        }
    }
    HorizontalDivider(color = colors.borderSubtle)
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val colors = AxiomTheme.colors
    val isUser = message.role == "user"
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = remember(message.timestamp) {
        timeFormatter.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
    ) {
        Row(
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colors.primaryLight)
                        .align(Alignment.Bottom),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🤖", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.width(Spacing.md))
            }

            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isUser) 16.dp else 4.dp,
                            topEnd = if (isUser) 4.dp else 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp,
                        )
                    )
                    .background(if (isUser) colors.primary else colors.card)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) colors.textOnPrimary else colors.textPrimary,
                )
            }
        }

        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = timeString,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            modifier = Modifier.padding(horizontal = if (isUser) 0.dp else 36.dp),
        )
    }
}

@Composable
private fun TypingIndicator() {
    val colors = AxiomTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0
                1.0f at 200
                0.3f at 500
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot1",
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 150
                1.0f at 350
                0.3f at 650
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot2",
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 300
                1.0f at 500
                0.3f at 800
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "dot3",
    )

    Row(verticalAlignment = Alignment.Bottom) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Text("🤖", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(Spacing.md))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(colors.card)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(colors.textMuted)
                        .alpha(dot1Alpha),
                )
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(colors.textMuted)
                        .alpha(dot2Alpha),
                )
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(colors.textMuted)
                        .alpha(dot3Alpha),
                )
            }
        }
    }
}
