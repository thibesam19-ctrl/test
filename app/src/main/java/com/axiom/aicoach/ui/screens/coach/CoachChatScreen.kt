package com.axiom.aicoach.ui.screens.coach

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val id: String, val role: String, val content: String, val timestamp: Long = System.currentTimeMillis())

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
        // Safety guardrail
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
                ChatMessage("intro", "assistant", "Hey! I'm Coach Axiom — your personal AI fitness coach. I'm here to help you with workouts, nutrition, recovery, and motivation.\n\nWhat's on your mind today? 💪"),
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

    Scaffold(
        topBar = {
            CoachTopBar(onBack)
        },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                item { Spacer(Modifier.height(Spacing.md)) }
                items(messages) { message ->
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

            // Suggested prompts (shown only when few messages)
            if (messages.size <= 2) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = Spacing.xl)
                        .height(160.dp),
                ) {
                    items(suggestedPrompts) { prompt ->
                        SuggestionChip(
                            onClick = {
                                val userMsg = ChatMessage(System.currentTimeMillis().toString(), "user", prompt)
                                messages = messages + userMsg
                                isTyping = true
                                scope.launch {
                                    delay(1200)
                                    val response = getRuleBasedResponse(prompt)
                                    isTyping = false
                                    messages = messages + ChatMessage((System.currentTimeMillis() + 1).toString(), "assistant", response)
                                }
                            },
                            label = { Text(prompt, style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                        )
                    }
                }
            }

            // Input bar
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
                    placeholder = { Text("Ask your coach anything...") },
                    modifier = Modifier.weight(1f),
                    shape = Radius.xl,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedContainerColor = colors.inputBackground,
                        unfocusedContainerColor = colors.inputBackground,
                    ),
                )
                Spacer(Modifier.width(Spacing.lg))
                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isEmpty()) return@IconButton
                        inputText = ""
                        val userMsg = ChatMessage(System.currentTimeMillis().toString(), "user", text)
                        messages = messages + userMsg
                        isTyping = true
                        scope.launch {
                            delay((800L..2000L).random())
                            val response = getRuleBasedResponse(text)
                            isTyping = false
                            messages = messages + ChatMessage((System.currentTimeMillis() + 1).toString(), "assistant", response)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) colors.primary else colors.borderSubtle),
                    enabled = inputText.isNotBlank(),
                ) {
                    Icon(Icons.Default.Send, "Send", tint = if (inputText.isNotBlank()) colors.textOnPrimary else colors.textMuted)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoachTopBar(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.primaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🤖", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.width(Spacing.lg))
                Column {
                    Text("Coach Axiom", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    Text("AI-powered · Rule-based", style = MaterialTheme.typography.labelSmall, color = colors.success)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(androidx.compose.material.icons.Icons.Default.ArrowBack, null, tint = colors.textPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
    )
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val colors = AxiomTheme.colors
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!isUser) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colors.primaryLight)
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center,
            ) {
                Text("🤖", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.width(8.dp))
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
}

@Composable
private fun TypingIndicator() {
    val colors = AxiomTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Text("🤖", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                .background(colors.card)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text("Thinking…", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
        }
    }
}
