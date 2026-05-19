package com.axiom.aicoach.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.components.AxiomSecondaryButton
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

// ── SignInScreen ──────────────────────────────────────────────────────────────

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = Spacing.xl),
        ) {
            // ── Inline back button ────────────────────────────────────────────
            Spacer(Modifier.height(Spacing.xl))
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }

            Spacer(Modifier.height(Spacing.s32))

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            Spacer(Modifier.height(Spacing.s40))

            // ── Fields ────────────────────────────────────────────────────────
            AxiomTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = "Email",
                leadingIcon = { Icon(Icons.Default.Email, null, tint = colors.textMuted) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )
            Spacer(Modifier.height(20.dp))
            AxiomTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = "Password",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = colors.textMuted) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = colors.textMuted,
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        onSignInSuccess()
                    }
                }),
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.error,
                )
            }

            Spacer(Modifier.height(Spacing.md))
            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.primary,
                )
            }

            Spacer(Modifier.height(Spacing.s32))

            // ── Primary action ────────────────────────────────────────────────
            AxiomPrimaryButton(
                text = "Sign In",
                onClick = {
                    isLoading = true
                    onSignInSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank(),
                loading = isLoading,
            )

            Spacer(Modifier.height(Spacing.xl))
            OrDivider()
            Spacer(Modifier.height(Spacing.xl))

            // ── Google button ─────────────────────────────────────────────────
            AxiomSecondaryButton(
                text = "G   Continue with Google",
                onClick = onSignInSuccess,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.s40))

            // ── Bottom nav ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        text = "Sign Up",
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}

// ── SignUpScreen ──────────────────────────────────────────────────────────────

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val passwordStrength = remember(password) { getPasswordStrength(password) }
    val passwordMatch = confirmPassword.isEmpty() || password == confirmPassword
    val isValid = name.isNotBlank() && email.contains("@") && password.length >= 8 && passwordMatch

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = Spacing.xl),
        ) {
            // ── Inline back button ────────────────────────────────────────────
            Spacer(Modifier.height(Spacing.xl))
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }

            Spacer(Modifier.height(Spacing.s32))

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text = "Create your account",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "7-day free trial, no card required",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            Spacer(Modifier.height(Spacing.s40))

            // ── Fields ────────────────────────────────────────────────────────
            AxiomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = { Icon(Icons.Default.Person, null, tint = colors.textMuted) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )
            Spacer(Modifier.height(20.dp))
            AxiomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = { Icon(Icons.Default.Email, null, tint = colors.textMuted) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )
            Spacer(Modifier.height(20.dp))
            AxiomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = colors.textMuted) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = colors.textMuted,
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )
            if (password.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                PasswordStrengthBar(passwordStrength)
            }
            Spacer(Modifier.height(20.dp))
            AxiomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = colors.textMuted) },
                visualTransformation = PasswordVisualTransformation(),
                isError = !passwordMatch,
                supportingText = if (!passwordMatch) "Passwords don't match" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            )

            Spacer(Modifier.height(Spacing.s40))

            AxiomPrimaryButton(
                text = "Create Account",
                onClick = { isLoading = true; onSignUpSuccess() },
                modifier = Modifier.fillMaxWidth(),
                enabled = isValid,
                loading = isLoading,
            )

            Spacer(Modifier.height(Spacing.xl))
            OrDivider()
            Spacer(Modifier.height(Spacing.xl))

            AxiomSecondaryButton(
                text = "G   Continue with Google",
                onClick = onSignUpSuccess,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.s40))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                TextButton(onClick = onNavigateToSignIn) {
                    Text(
                        text = "Sign In",
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xxxl))
        }
    }
}

// ── PasswordResetScreen ───────────────────────────────────────────────────────

@Composable
fun PasswordResetScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.xl))
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }

            if (sent) {
                // ── Success state ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("✉️", style = MaterialTheme.typography.displayLarge, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(Spacing.xxxl))
                    Text(
                        text = "Sent!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.success,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(Spacing.xl))
                    Text(
                        text = "Check your inbox — we sent a reset link to $email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                // ── Form state ────────────────────────────────────────────────
                Spacer(Modifier.height(Spacing.s32))
                Text(
                    text = "Forgot your password?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = "Enter your email and we'll send a reset link.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(Spacing.s40))
                AxiomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = colors.textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                Spacer(Modifier.height(Spacing.s40))
                AxiomPrimaryButton(
                    text = "Send Reset Link",
                    onClick = { sent = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = email.contains("@"),
                )
            }
        }
    }
}

// ── Shared UI helpers ─────────────────────────────────────────────────────────

@Composable
fun AxiomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
) {
    val colors = AxiomTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        enabled = enabled,
        singleLine = singleLine,
        shape = Radius.md,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.border,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.textMuted,
            cursorColor = colors.primary,
            focusedContainerColor = colors.inputBackground,
            unfocusedContainerColor = colors.inputBackground,
        ),
    )
}

@Composable
fun OrDivider() {
    val colors = AxiomTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = colors.border)
        Text("  or  ", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        HorizontalDivider(Modifier.weight(1f), color = colors.border)
    }
}

@Composable
fun PasswordStrengthBar(strength: PasswordStrength) {
    val colors = AxiomTheme.colors
    val (color, label) = when (strength) {
        PasswordStrength.WEAK -> colors.error to "Weak"
        PasswordStrength.FAIR -> colors.warning to "Fair"
        PasswordStrength.GOOD -> colors.info to "Good"
        PasswordStrength.STRONG -> colors.success to "Strong"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(4) { i ->
            val filled = i < strength.ordinal + 1
            Box(
                Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 1.dp)
                    .let {
                        if (filled) it.background(color, Radius.pill)
                        else it.background(colors.borderSubtle, Radius.pill)
                    },
            )
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

enum class PasswordStrength { WEAK, FAIR, GOOD, STRONG }

fun getPasswordStrength(password: String): PasswordStrength {
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return PasswordStrength.values()[score.coerceIn(0, 3)]
}
