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
fun TermsOfServiceScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors

    Scaffold(
        topBar = { AxiomTopBar("Terms of Service", onBack = onBack) },
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
                text = "Please read these Terms of Service carefully before using Axiom AI Coach. By accessing or using the app, you agree to be bound by these terms.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )

            Spacer(Modifier.height(Spacing.s32))

            ToSSection(
                title = "1. Acceptance",
                body = """By downloading, installing, or using Axiom AI Coach ("the App"), you agree to these Terms of Service ("Terms") and our Privacy Policy, which is incorporated herein by reference. If you do not agree to these Terms, do not use the App.

These Terms constitute a legally binding agreement between you and Axiom AI Coach, Inc. ("Axiom", "we", "us", or "our"). We reserve the right to update these Terms at any time. Continued use of the App after changes are posted constitutes your acceptance of the revised Terms.""",
            )

            ToSSection(
                title = "2. Description of Service",
                body = """Axiom AI Coach is a fitness and wellness application that provides:

• AI-powered workout planning and coaching recommendations.
• Exercise form analysis using your device camera.
• Nutrition tracking, meal logging, and dietary guidance.
• Progress tracking including body measurements, weight history, and progress photos.
• Personalised coaching chat powered by artificial intelligence.

The App is intended for general fitness and wellness purposes for healthy adults. It is not a medical device, medical service, or substitute for professional medical advice.""",
            )

            ToSSection(
                title = "3. User Accounts",
                body = """To use the App, you must create an account. You agree to:

• Provide accurate, current, and complete information during registration.
• Maintain the security of your password and accept responsibility for all activity under your account.
• Notify us immediately at support@axiomaicoach.com if you suspect unauthorised access to your account.
• Not share your account credentials with any other person.

You must be at least 16 years of age to create an account. By creating an account, you represent that you meet this age requirement. We reserve the right to terminate accounts that violate these Terms.""",
            )

            ToSSection(
                title = "4. Subscription Terms",
                body = """Axiom AI Coach offers both free and premium subscription tiers.

Free tier: Access to core features including basic workout logging and nutrition tracking.

Premium subscription:
• Billed monthly or annually via the Google Play billing system.
• Subscription automatically renews unless cancelled at least 24 hours before the end of the current billing period.
• You may cancel your subscription at any time in your Google Play account settings.
• No refunds are provided for partial subscription periods, except as required by applicable law.
• We reserve the right to change subscription pricing with 30 days' prior notice to existing subscribers.

All payments are processed by Google Play. We do not store your payment details.""",
            )

            ToSSection(
                title = "5. AI Features Disclaimer",
                body = """IMPORTANT — PLEASE READ CAREFULLY

The AI coaching, workout recommendations, nutrition guidance, and form analysis features within Axiom AI Coach are provided for general informational and fitness purposes only.

• AI recommendations are NOT medical advice. They do not constitute diagnosis, treatment, or prevention of any medical condition.
• Always consult a qualified healthcare professional, physician, or certified personal trainer before beginning any new exercise programme or making significant changes to your diet, especially if you have any pre-existing medical conditions, injuries, or health concerns.
• The form analysis feature provides general feedback on exercise technique and should not replace in-person coaching by a qualified professional.
• Fitness and nutritional needs vary significantly between individuals. AI-generated recommendations may not be appropriate for everyone.
• Axiom AI Coach is not responsible for any injury, adverse health outcome, or other harm arising from following AI-generated recommendations.

By using the AI features, you acknowledge these limitations and assume full responsibility for your fitness activities.""",
            )

            ToSSection(
                title = "6. Prohibited Uses",
                body = """You agree not to use the App to:

• Violate any applicable local, state, national, or international law or regulation.
• Attempt to reverse-engineer, decompile, or disassemble any part of the App.
• Transmit any content that is unlawful, harmful, threatening, abusive, or otherwise objectionable.
• Attempt to gain unauthorised access to any part of the App or its related systems.
• Use automated scripts, bots, or other automated means to access the App.
• Interfere with or disrupt the integrity or performance of the App.
• Use the App for any commercial purpose without our prior written consent.
• Impersonate any person or entity, or misrepresent your affiliation with any person or entity.""",
            )

            ToSSection(
                title = "7. Limitation of Liability",
                body = """TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW:

• THE APP IS PROVIDED "AS IS" AND "AS AVAILABLE" WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED.
• WE DISCLAIM ALL WARRANTIES, INCLUDING IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
• WE SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES ARISING FROM YOUR USE OF, OR INABILITY TO USE, THE APP.
• OUR TOTAL CUMULATIVE LIABILITY TO YOU FOR ANY CLAIMS ARISING FROM OR RELATED TO THESE TERMS OR YOUR USE OF THE APP SHALL NOT EXCEED THE GREATER OF (A) THE AMOUNT YOU PAID US IN THE 12 MONTHS PRECEDING THE CLAIM, OR (B) USD $100.

Some jurisdictions do not allow certain liability exclusions, so some of the above may not apply to you.""",
            )

            ToSSection(
                title = "8. Termination",
                body = """We may suspend or terminate your account and access to the App at our sole discretion, without notice, for conduct that we believe:

• Violates these Terms.
• Is harmful to other users, third parties, or the App.
• Is required by law or a regulatory authority.

You may terminate your account at any time by contacting us at support@axiomaicoach.com or through the account deletion feature in Settings. Upon termination, your right to use the App ceases immediately. Sections that by their nature should survive termination (including limitations of liability and disclaimers) will survive.""",
            )

            ToSSection(
                title = "9. Governing Law",
                body = """These Terms shall be governed by and construed in accordance with the laws of the State of California, United States, without regard to its conflict of law provisions.

Any disputes arising from these Terms or your use of the App shall be resolved through binding arbitration in accordance with the American Arbitration Association rules, except that either party may seek injunctive relief in a court of competent jurisdiction for intellectual property matters or to prevent irreparable harm.

If you have any questions about these Terms, please contact us at legal@axiomaicoach.com.""",
            )

            Spacer(Modifier.height(Spacing.s80))
        }
    }
}

@Composable
private fun ToSSection(title: String, body: String) {
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
