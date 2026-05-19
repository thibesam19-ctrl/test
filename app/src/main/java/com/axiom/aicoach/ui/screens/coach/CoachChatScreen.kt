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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
)

@Composable
fun CoachChatScreen(
    onBack: () -> Unit,
    viewModel: CoachViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

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

            items(uiState.messages, key = { it.id }) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                ) {
                    MessageBubbleUi(message)
                }
            }

            if (uiState.isTyping) {
                item { TypingIndicator() }
            }

            item { Spacer(Modifier.height(Spacing.md)) }
        }

        // ── Suggested prompts ─────────────────────────────────────────────────
        if (uiState.messages.size <= 2) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                items(uiState.suggestedPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(Radius.xl)
                            .border(1.dp, colors.border, Radius.xl)
                            .clickable { viewModel.sendSuggestedPrompt(prompt) }
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
                value = uiState.inputText,
                onValueChange = { viewModel.updateInput(it) },
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
                        if (uiState.inputText.isNotBlank()) colors.primary else colors.borderSubtle
                    )
                    .clickable(enabled = uiState.inputText.isNotBlank()) {
                        viewModel.sendMessage()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (uiState.inputText.isNotBlank()) colors.textOnPrimary else colors.textMuted,
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
private fun MessageBubbleUi(message: ChatMessageUi) {
    val colors = AxiomTheme.colors
    val isUser = message.isUser
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
                    text = message.text,
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
