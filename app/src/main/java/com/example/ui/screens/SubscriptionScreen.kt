package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SubscriptionEntity
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.SubscriptionCheckoutDialog
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioIndigo
import com.example.ui.theme.StudioViolet
import com.example.ui.viewmodel.PaymentCheckoutUiState
import com.example.ui.viewmodel.PaymentViewModel
import java.util.Locale

/**
 * SubscriptionScreen composable displaying Creator, Pro, and Studio subscription plans.
 * Integrates directly with [PaymentViewModel] to initialize checkout sessions and observe real-time status.
 */
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun SubscriptionScreen(
    paymentViewModel: PaymentViewModel,
    onBack: (() -> Unit)? = null,
    showDialogs: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val checkoutUiState by paymentViewModel.checkoutUiState.collectAsStateWithLifecycle()
    val activeSession by paymentViewModel.activeSession.collectAsStateWithLifecycle()
    val currentSubscription by paymentViewModel.currentSubscription.collectAsStateWithLifecycle()
    val lastVerifiedReceipt by paymentViewModel.lastVerifiedReceipt.collectAsStateWithLifecycle()
    val isProcessing by paymentViewModel.isProcessing.collectAsStateWithLifecycle()

    SubscriptionScreenContent(
        currentSubscription = currentSubscription,
        checkoutUiState = checkoutUiState,
        isProcessing = isProcessing,
        onSelectPlan = { tier, isAnnual ->
            paymentViewModel.initializePaymentSession(tier, isAnnual)
        },
        onCancelSubscription = { paymentViewModel.cancelSubscription() },
        onReactivateSubscription = { paymentViewModel.reactivateSubscription() },
        onBack = onBack,
        lazyListState = lazyListState,
        modifier = modifier
    )

    if (showDialogs) {
        // Dedicated Checkout Dialog when session is active in standalone mode
        activeSession?.let { session ->
            val isVerifying = checkoutUiState is PaymentCheckoutUiState.Verifying
            val failedError = (checkoutUiState as? PaymentCheckoutUiState.Failed)?.errorMessage

            SubscriptionCheckoutDialog(
                plan = session.plan,
                isAnnual = session.isAnnual,
                isProcessing = isVerifying || isProcessing,
                errorMessage = failedError,
                onDismiss = { paymentViewModel.cancelCheckoutSession() },
                onConfirmPayment = { method, email ->
                    paymentViewModel.simulateSuccessfulPayment(method, email)
                }
            )
        }

        // Success Receipt Dialog in standalone mode
        lastVerifiedReceipt?.let { tx ->
            PaymentReceiptDialog(
                transaction = tx,
                onDismiss = { paymentViewModel.dismissReceiptModal() }
            )
        }
    }
}

/**
 * Stateless content layer for SubscriptionScreen, supporting previews, unit tests, and custom architectures.
 */
@Composable
fun SubscriptionScreenContent(
    currentSubscription: SubscriptionEntity?,
    checkoutUiState: PaymentCheckoutUiState,
    isProcessing: Boolean,
    onSelectPlan: (PlanTier, Boolean) -> Unit,
    onCancelSubscription: () -> Unit = {},
    onReactivateSubscription: () -> Unit = {},
    onBack: (() -> Unit)? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    var isAnnualBilling by remember { mutableStateOf(false) }

    val activePlanName = currentSubscription?.planTier?.uppercase(Locale.ROOT) ?: "FREE"

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("subscription_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar / Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("subscription_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Choose Your Plan",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Scale your video production with AI-assisted scripting & generation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Security Shield Badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Encrypted",
                            tint = StudioEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "256-Bit SSL",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Active Subscription Banner (if on a paid plan)
        if (currentSubscription != null && activePlanName != "FREE") {
            item {
                ActiveSubscriptionCard(
                    subscription = currentSubscription,
                    onCancel = onCancelSubscription,
                    onReactivate = onReactivateSubscription
                )
            }
        }

        // Monthly / Annual Billing Toggle Pill
        item {
            BillingCycleSelector(
                isAnnual = isAnnualBilling,
                onToggle = { isAnnualBilling = it }
            )
        }

        // Plan 1: Creator Plan
        item {
            SubscriptionPlanCard(
                plan = PlanTier.CREATOR,
                isAnnual = isAnnualBilling,
                isCurrentPlan = activePlanName == "CREATOR",
                accentColor = StudioEmerald,
                icon = Icons.Default.Bolt,
                features = listOf(
                    "150 AI actions per month",
                    "30 full video scripts & viral hooks",
                    "15 active video projects",
                    "Natural voiceover generation (TTS)",
                    "2K thumbnail prompt generator",
                    "Publishing calendar & schedule tracker",
                    "Standard generation queue"
                ),
                isProcessing = isProcessing,
                onSelect = { onSelectPlan(PlanTier.CREATOR, isAnnualBilling) }
            )
        }

        // Plan 2: Pro Plan (Highlighted Most Popular)
        item {
            SubscriptionPlanCard(
                plan = PlanTier.PRO,
                isAnnual = isAnnualBilling,
                isCurrentPlan = activePlanName == "PRO",
                isFeatured = true,
                accentColor = CreatorRed,
                icon = Icons.Default.Star,
                features = listOf(
                    "500 AI actions per month",
                    "100 long-form scripts & multi-scene outlines",
                    "50 active video projects",
                    "Priority generation queue (zero wait time)",
                    "Full retention curve drop-off analysis",
                    "High-CTR title optimizer & tags",
                    "Custom voice speed & emotional tones",
                    "Commercial monetization license"
                ),
                isProcessing = isProcessing,
                onSelect = { onSelectPlan(PlanTier.PRO, isAnnualBilling) }
            )
        }

        // Plan 3: Studio Plan (Teams & High Volume)
        item {
            SubscriptionPlanCard(
                plan = PlanTier.STUDIO,
                isAnnual = isAnnualBilling,
                isCurrentPlan = activePlanName == "STUDIO",
                accentColor = StudioViolet,
                icon = Icons.Default.Groups,
                features = listOf(
                    "1,500 shared AI actions per month",
                    "500 full video scripts & outlines",
                    "200 active video projects",
                    "5 team member seats & collaborative workspaces",
                    "Dedicated cloud compute cluster",
                    "Bulk CSV calendar & script exports",
                    "White-glove onboarding & custom AI prompts",
                    "24/7 Priority support hotline"
                ),
                isProcessing = isProcessing,
                onSelect = { onSelectPlan(PlanTier.STUDIO, isAnnualBilling) }
            )
        }

        // Trust & Guarantee Badges Footer
        item {
            TrustBadgesFooter()
        }
    }
}

/**
 * Plan card component with distinctive styling, badges, price breakdowns, and feature checks.
 */
@Composable
internal fun SubscriptionPlanCard(
    plan: PlanTier,
    isAnnual: Boolean,
    isCurrentPlan: Boolean,
    isFeatured: Boolean = false,
    accentColor: Color,
    icon: ImageVector,
    features: List<String>,
    isProcessing: Boolean,
    onSelect: () -> Unit
) {
    val cardBorder = if (isFeatured) {
        BorderStroke(2.dp, Brush.linearGradient(listOf(accentColor, StudioIndigo)))
    } else if (isCurrentPlan) {
        BorderStroke(1.5.dp, StudioEmerald)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.name.lowercase(Locale.ROOT)}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFeatured) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = cardBorder
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row: Icon, Title, and Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = plan.aiActions,
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (plan.badge != null) {
                    Surface(
                        color = accentColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plan.badge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing Presentation
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                val displayPrice = if (isAnnual) {
                    String.format(Locale.US, "$%.2f", plan.annualMonthlyPrice)
                } else {
                    "$${plan.monthlyPrice}"
                }

                Text(
                    text = displayPrice,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " / month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )

                if (isAnnual) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$${plan.monthlyPrice}/mo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textDecoration = TextDecoration.LineThrough,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            if (isAnnual) {
                val annualBilled = String.format(Locale.US, "$%.2f", plan.annualMonthlyPrice * 12.0)
                Text(
                    text = "Billed annually ($annualBilled / year) — saves 20%",
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioEmerald,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Feature Checklist
            Text(
                text = "What's included:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            val buttonTag = "subscribe_${plan.name.lowercase(Locale.ROOT)}_button"

            if (isCurrentPlan) {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag(buttonTag),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, StudioEmerald)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = StudioEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Plan",
                        fontWeight = FontWeight.Bold,
                        color = StudioEmerald
                    )
                }
            } else {
                Button(
                    onClick = onSelect,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag(buttonTag),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFeatured) CreatorRed else accentColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        val buttonText = when (plan) {
                            PlanTier.CREATOR -> "Choose Creator"
                            PlanTier.PRO -> "Upgrade to Pro"
                            PlanTier.STUDIO -> "Get Studio Access"
                            else -> "Select Plan"
                        }
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Monthly vs Annual Billing Toggle with highlighted savings badge.
 */
@Composable
internal fun BillingCycleSelector(
    isAnnual: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Billing Cadence",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAnnual) "Annual Billing (Billed once/yr)" else "Monthly Billing (Cancel anytime)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = StudioEmerald.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "SAVE 20%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioEmerald,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = isAnnual,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("billing_interval_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = StudioEmerald
                    )
                )
            }
        }
    }
}

/**
 * Card highlighting the user's currently active subscription and renewal controls.
 */
@Composable
internal fun ActiveSubscriptionCard(
    subscription: SubscriptionEntity,
    onCancel: () -> Unit,
    onReactivate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_subscription_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = StudioIndigo.copy(alpha = 0.12f)
        ),
        border = BorderStroke(1.dp, StudioIndigo.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = StudioIndigo,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Current: ${subscription.planTier} Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = if (subscription.status == "ACTIVE") StudioEmerald.copy(alpha = 0.2f) else StudioAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = subscription.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (subscription.status == "ACTIVE") StudioEmerald else StudioAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Next Billing: ${subscription.nextBillingDate} (${subscription.billingInterval.replaceFirstChar { it.uppercase() }})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Quota Allocation: ${subscription.aiActionsUsed} / ${subscription.aiActionsTotal} AI Actions used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (subscription.autoRenew) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.testTag("cancel_renewal_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Turn off auto-renew", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Button(
                    onClick = onReactivate,
                    modifier = Modifier.testTag("reactivate_renewal_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald)
                ) {
                    Text("Reactivate auto-renew", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

/**
 * Trust & security footer highlighting encryption and gateway protections.
 */
@Composable
private fun TrustBadgesFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Razorpay & Stripe Verified • Instant Cryptographic Activation",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Cancel or modify your plan at any time. Quotas update immediately.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
