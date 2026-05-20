package com.axiom.aicoach.ui.screens.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Spacing

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors

    Scaffold(
        topBar = { AxiomTopBar("Privacy Policy", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        ) {
            Text(
                text = "Last updated: January 2025",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )

            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "Your privacy is important to us. This Privacy Policy explains how Axiom AI Coach collects, uses, and protects your personal information.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            Spacer(Modifier.height(Spacing.s32))

            PolicySection(
                title = "Data We Collect",
                body = """We collect information you provide directly to us, including:

• Account information: name, email address, and password when you create an account.
• Profile data: age, height, weight, fitness goals, and activity level you enter during onboarding.
• Health & fitness data: workout logs, exercise performance, nutrition entries, body measurements, and progress photos you record in the app.
• Camera data: images and video captured during form analysis sessions. See the "AI & Camera Data" section for details.
• Device information: device type, operating system, and app version for troubleshooting and compatibility purposes.
• Usage data: features you interact with, session duration, and crash logs to improve app performance.""",
            )

            PolicySection(
                title = "How We Use Your Data",
                body = """We use the information we collect to:

• Provide, operate, and improve the Axiom AI Coach service.
• Personalise your AI coaching experience, including workout recommendations and nutrition guidance.
• Analyse aggregate trends to improve our algorithms and content — your individual data is never shared with third parties for this purpose.
• Send you notifications and reminders you have opted into, such as workout reminders and weekly progress summaries.
• Respond to your support requests and troubleshoot issues.
• Comply with legal obligations.

We will never sell your personal data to third parties. We will never use your data to serve you advertisements.""",
            )

            PolicySection(
                title = "AI & Camera Data",
                body = """Axiom uses on-device AI to analyse your workout form via the device camera. We have designed this feature with your privacy as the top priority:

• Camera frames used for form analysis are processed entirely on your device. Raw video or images are never uploaded to our servers.
• Only derived metrics (e.g. joint angle scores, rep counts) are stored, not the underlying images or video.
• You may disable camera access at any time in your device Settings. The app remains fully functional for all non-camera features.
• Progress photos you manually save to your gallery are stored encrypted in your account and are never used to train AI models without your explicit, separate consent.

Health data generated in the app (workout logs, body measurements, nutrition entries) is stored on your device and, if you enable cloud sync, in our encrypted cloud infrastructure. This data is never used to train AI models outside of your personal coaching context.""",
            )

            PolicySection(
                title = "Third-Party Services",
                body = """To operate the service, we work with a limited set of trusted third-party providers:

• Cloud infrastructure: encrypted data storage and backend services.
• Authentication: secure sign-in via email/password or third-party OAuth providers (e.g. Google).
• Analytics: anonymised, aggregate crash reporting and performance monitoring to improve app stability. No personally identifiable information is included.
• Payment processing: subscription billing is handled by the Google Play billing system. We do not store payment card details.

We require all third-party service providers to maintain appropriate security measures and to use your data only to provide services to us.""",
            )

            PolicySection(
                title = "Data Retention",
                body = """We retain your data for as long as your account is active or as needed to provide you with the service.

• If you delete your account, we will delete or anonymise your personal data within 30 days, except where we are required to retain it for legal or regulatory purposes.
• Workout and nutrition logs, body measurements, and progress photos are deleted upon account deletion.
• Anonymised, aggregate analytics data that cannot be linked back to you may be retained indefinitely to improve the service.

You may export all of your data at any time via Settings → Privacy & Data → Export My Data.""",
            )

            PolicySection(
                title = "Your Rights",
                body = """Depending on your location, you may have the following rights regarding your personal data:

• Access: request a copy of the personal data we hold about you.
• Correction: request that we correct inaccurate or incomplete data.
• Deletion: request that we delete your personal data (see Data Retention above).
• Portability: request your data in a machine-readable format.
• Objection: object to the processing of your data in certain circumstances.
• Restriction: request that we restrict processing of your data.

To exercise any of these rights, please contact us at privacy@axiomaicoach.com. We will respond to all requests within 30 days.""",
            )

            PolicySection(
                title = "Contact Us",
                body = """If you have any questions, concerns, or requests regarding this Privacy Policy or our data practices, please contact us:

Email: privacy@axiomaicoach.com
Address: Axiom AI Coach, Inc.

We take all privacy inquiries seriously and will respond promptly. If you are not satisfied with our response, you have the right to lodge a complaint with the relevant data protection authority in your jurisdiction.""",
            )

            Spacer(Modifier.height(Spacing.s80))
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    val colors = AxiomTheme.colors

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
    )
    Spacer(Modifier.height(Spacing.md))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textSecondary,
    )
    Spacer(Modifier.height(Spacing.s32))
}
