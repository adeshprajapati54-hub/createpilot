package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.SubscriptionEntity
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioIndigo
import com.example.ui.theme.StudioViolet
import com.example.util.PaymentVerificationHelper
import java.util.Locale

enum class PlanTier(
    val title: String,
    val monthlyPrice: Int,
    val annualMonthlyPrice: Double,
    val aiActions: String,
    val badge: String? = null
) {
    FREE("Free Plan", 0, 0.0, "10 AI actions/mo", null),
    CREATOR("Creator Plan", 12, 9.60, "150 AI actions/mo", "POPULAR"),
    PRO("Pro Plan", 29, 23.20, "500 AI actions/mo", "POWER CREATOR"),
    STUDIO("Studio Plan", 79, 63.20, "1,500 shared AI actions/mo", "TEAMS & AGENCIES")
}

@Composable
fun PricingScreen(
    currentPlan: PlanTier = PlanTier.FREE,
    currentSubscription: SubscriptionEntity? = null,
    transactions: List<PaymentTransactionEntity> = emptyList(),
    onSelectPlan: (PlanTier, Boolean) -> Unit = { _, _ -> },
    onCancelSubscription: () -> Unit = {},
    onReactivateSubscription: () -> Unit = {},
    onViewReceipt: (PaymentTransactionEntity) -> Unit = {},
    onBack: () -> Unit,
    isProcessingPayment: Boolean = false,
    diagnosticReport: PaymentVerificationHelper.PaymentDiagnosticReport? = null,
    simulationFeedback: String? = null,
    onRunDiagnostics: () -> Unit = {},
    onTestFraudCheck: () -> Unit = {},
    onTestDuplicateCheck: () -> Unit = {},
    onClearSimulationFeedback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var isAnnualBilling by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("pricing_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "CreatorPilot Billing & Plans",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Verified subscriptions & quotas built for video creators",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Tab Selector: Plans vs Billing History vs Test Lab
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CreatorRed,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).testTag("pricing_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Plans", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_pricing_plans")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Billing (${transactions.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_billing_receipts")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Test Lab", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_payment_test_lab")
                )
            }
        }

        if (selectedTab == 0) {
            // Active Subscription Card Banner
            item {
                ActiveSubscriptionCard(
                    currentPlan = currentPlan,
                    subscription = currentSubscription,
                    onCancel = onCancelSubscription,
                    onReactivate = onReactivateSubscription
                )
            }

            // Billing Interval Switcher (Monthly vs Annual -20%)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().testTag("billing_interval_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isAnnualBilling) CreatorRed else Color.Transparent)
                                .clickable { isAnnualBilling = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Monthly Billing",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isAnnualBilling) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAnnualBilling) CreatorRed else Color.Transparent)
                                .clickable { isAnnualBilling = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Annual (Save 20%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAnnualBilling) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StudioAmber)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "-20%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tier 1: Free Plan ($0/month)
            item {
                PlanCard(
                    tier = PlanTier.FREE,
                    isAnnual = isAnnualBilling,
                    isCurrent = currentPlan == PlanTier.FREE,
                    accentColor = MaterialTheme.colorScheme.outline,
                    icon = Icons.Default.Bolt,
                    features = listOf(
                        "10 AI actions/month",
                        "10 video ideas per month",
                        "2 full script outlines & narrations",
                        "10 scene timing & B-roll prompts",
                        "5 thumbnail concepts",
                        "3 active video projects in workspace",
                        "Basic content calendar",
                        "TXT and CSV exports"
                    ),
                    onSelect = { onSelectPlan(PlanTier.FREE, false) }
                )
            }

            // Tier 2: Creator Plan ($12/month)
            item {
                PlanCard(
                    tier = PlanTier.CREATOR,
                    isAnnual = isAnnualBilling,
                    isCurrent = currentPlan == PlanTier.CREATOR,
                    accentColor = CreatorRed,
                    icon = Icons.Default.Star,
                    features = listOf(
                        "150 AI actions/month",
                        "Unlimited manual drafts",
                        "Up to 30 AI-assisted scripts",
                        "Scene prompts & timing cues",
                        "Thumbnail briefs",
                        "Up to 3 channel profiles",
                        "Content calendar with status pipeline",
                        "TXT, CSV, and Markdown exports",
                        "Reusable channel voice & tone presets"
                    ),
                    onSelect = { onSelectPlan(PlanTier.CREATOR, isAnnualBilling) }
                )
            }

            // Tier 3: Pro Plan ($29/month)
            item {
                PlanCard(
                    tier = PlanTier.PRO,
                    isAnnual = isAnnualBilling,
                    isCurrent = currentPlan == PlanTier.PRO,
                    accentColor = StudioViolet,
                    icon = Icons.Default.WorkspacePremium,
                    features = listOf(
                        "500 AI actions/month",
                        "Everything in Creator Plan",
                        "Up to 100 AI-assisted scripts",
                        "Advanced scripts & scene breakdowns",
                        "Batch ideas and high-CTR titles",
                        "Shorts repurposing (9:16 vertical adaptation)",
                        "Priority queue processing",
                        "Increased channel & project capacity"
                    ),
                    onSelect = { onSelectPlan(PlanTier.PRO, isAnnualBilling) }
                )
            }

            // Tier 4: Studio Plan ($79/month)
            item {
                PlanCard(
                    tier = PlanTier.STUDIO,
                    isAnnual = isAnnualBilling,
                    isCurrent = currentPlan == PlanTier.STUDIO,
                    accentColor = StudioCyan,
                    icon = Icons.Default.Groups,
                    features = listOf(
                        "1,500 shared AI actions/month",
                        "Everything in Pro Plan",
                        "Up to 5 team members / collaborators",
                        "Shared master content calendar",
                        "Client & multi-channel dedicated workspaces",
                        "Shared templates & brand voice presets",
                        "Team collaboration & review approval workflow"
                    ),
                    onSelect = { onSelectPlan(PlanTier.STUDIO, isAnnualBilling) }
                )
            }

            // Gateway & Verification Note
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = StudioEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Trusted Payment Verification Engine",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "CreatorPilot uses cryptographic HMAC-SHA256 signature verification. Paid features and quotas unlock only after trusted gateway confirmation with duplicate delivery protection.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (selectedTab == 1) {
            // BILLING & RECEIPTS TAB
            item {
                BillingOverviewHeader(
                    currentPlan = currentPlan,
                    subscription = currentSubscription,
                    totalTransactions = transactions.size,
                    onExportCsv = {
                        val csv = PaymentVerificationHelper.formatTransactionsCsv(transactions)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("CreatorPilot Billing CSV", csv))
                        Toast.makeText(context, "Billing history copied as CSV!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Transactions Yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Choose a plan to activate Creator, Pro, or Studio features.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        onViewReceipt = { onViewReceipt(tx) }
                    )
                }
            }
        } else {
            // SANDBOX & TEST LAB TAB
            item {
                PaymentSandboxContent(
                    currentPlan = currentPlan,
                    currentSubscription = currentSubscription,
                    totalTransactions = transactions.size,
                    isProcessing = isProcessingPayment,
                    diagnosticReport = diagnosticReport,
                    simulationFeedback = simulationFeedback,
                    onRunDiagnostics = onRunDiagnostics,
                    onSimulatePlan = onSelectPlan,
                    onTestFraudCheck = onTestFraudCheck,
                    onTestDuplicateCheck = onTestDuplicateCheck,
                    onClearFeedback = onClearSimulationFeedback
                )
            }
        }
    }
}

@Composable
fun PaymentSandboxContent(
    currentPlan: PlanTier,
    currentSubscription: SubscriptionEntity?,
    totalTransactions: Int,
    isProcessing: Boolean,
    diagnosticReport: PaymentVerificationHelper.PaymentDiagnosticReport?,
    simulationFeedback: String?,
    onRunDiagnostics: () -> Unit,
    onSimulatePlan: (PlanTier, Boolean) -> Unit,
    onTestFraudCheck: () -> Unit,
    onTestDuplicateCheck: () -> Unit,
    onClearFeedback: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Banner Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("sandbox_banner_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StudioViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = StudioViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Payment Test Lab & Sandbox",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Razorpay & Stripe Simulation Engine",
                                fontSize = 11.sp,
                                color = StudioEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "SANDBOX READY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = StudioEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Verify cryptographic HMAC-SHA256 signature verification, test fraudulent tampering rejection, enforce duplicate delivery protection, and run live on-device diagnostic assertions.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        // Feedback / Result banner
        AnimatedVisibility(visible = simulationFeedback != null) {
            simulationFeedback?.let { msg ->
                val isSuccess = msg.contains("Passed") || msg.contains("Enforced")
                val borderColor = if (isSuccess) StudioEmerald else StudioAmber
                val bgColor = if (isSuccess) StudioEmerald.copy(alpha = 0.12f) else StudioAmber.copy(alpha = 0.12f)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .testTag("simulation_feedback_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = borderColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = onClearFeedback,
                            modifier = Modifier.size(28.dp).testTag("btn_dismiss_simulation_feedback")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Live Cryptographic Diagnostics Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("diagnostic_tests_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Cryptographic Self-Diagnostics",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (diagnosticReport != null)
                                    "Passed ${diagnosticReport.passedTests}/${diagnosticReport.totalTests} tests (${diagnosticReport.executionTimeMs}ms)"
                                else
                                    "4 automated on-device tests available",
                                fontSize = 11.sp,
                                color = if (diagnosticReport != null) StudioEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (diagnosticReport != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Button(
                        onClick = onRunDiagnostics,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_run_diagnostics")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (diagnosticReport != null) "Re-test" else "Run Tests",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (diagnosticReport != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    diagnosticReport.items.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (item.passed) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (item.passed) StudioEmerald else CreatorRed,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (item.passed) "PASS" else "FAIL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (item.passed) StudioEmerald else CreatorRed
                                    )
                                }
                                Text(
                                    text = item.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = item.detail,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Simulation Scenarios Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("simulation_scenarios_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Interactive Payment Scenarios",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Trigger checkout simulation, security fraud rejections, and idempotency tests:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scenario 1: Pro Plan Simulation ($29)
                Button(
                    onClick = { onSimulatePlan(PlanTier.PRO, false) },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioViolet),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sim_btn_pro_monthly")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Pro Plan Checkout ($29/mo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 2: Creator Plan Simulation ($12)
                Button(
                    onClick = { onSimulatePlan(PlanTier.CREATOR, false) },
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sim_btn_creator_monthly")
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Creator Plan Checkout ($12/mo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 3: Studio Plan Simulation ($79)
                Button(
                    onClick = { onSimulatePlan(PlanTier.STUDIO, false) },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sim_btn_studio_monthly")
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulate Studio Plan Checkout ($79/mo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Security Test 1: Fraudulent Tampered Signature Rejection
                OutlinedButton(
                    onClick = onTestFraudCheck,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sim_btn_test_fraud")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Security, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Test Tampered Signature Rejection (Fraud Check)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Security Test 2: Duplicate Delivery Rejection
                OutlinedButton(
                    onClick = onTestDuplicateCheck,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sim_btn_test_duplicate")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = StudioEmerald, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Test Duplicate Replay Protection (Idempotency)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reset to Free Tier
                OutlinedButton(
                    onClick = { onSimulatePlan(PlanTier.FREE, false) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("sim_btn_reset_free")
                ) {
                    Text(
                        text = "Reset to Free Tier ($0/mo · 10 AI Actions)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Gateway Specifications & Security Overview
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("gateway_specs_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gateway & Security Specification",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val specs = listOf(
                    "Gateway Provider" to "Razorpay Subscriptions (International) & Stripe",
                    "Cryptographic Algorithm" to "HmacSHA256 (RFC-2104 hex digest)",
                    "Signature Payload" to "order_id + \"|\" + payment_id",
                    "Active Currency" to "USD ($) - International Cards & UPI",
                    "Webhook Endpoint" to "/api/billing/webhook (Supabase/Room sync)",
                    "Duplicate Handling" to "Unique index on transactionId in Room DB",
                    "Database Status" to "Room DB v3 (PaymentDao & SubscriptionDao)"
                )

                specs.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            value,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ActiveSubscriptionCard(
    currentPlan: PlanTier,
    subscription: SubscriptionEntity?,
    onCancel: () -> Unit,
    onReactivate: () -> Unit
) {
    val planColor = when (currentPlan) {
        PlanTier.CREATOR -> CreatorRed
        PlanTier.PRO -> StudioViolet
        PlanTier.STUDIO -> StudioCyan
        else -> MaterialTheme.colorScheme.outline
    }

    val isCancelled = subscription?.status == "CANCELLED" || subscription?.autoRenew == false
    val nextDate = subscription?.nextBillingDate ?: PaymentVerificationHelper.calculateNextBillingDate(false)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, planColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("active_subscription_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(planColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = planColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentPlan.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isCancelled) StudioAmber else StudioEmerald)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isCancelled) "CANCELS AT END" else "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                        Text(
                            text = currentPlan.aiActions,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "$${currentPlan.monthlyPrice}/mo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = planColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCancelled) "Access ends: $nextDate" else "Next Renewal: $nextDate",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentPlan != PlanTier.FREE) {
                    if (isCancelled) {
                        OutlinedButton(
                            onClick = onReactivate,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_reactivate_sub")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reactivate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onCancel,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_cancel_sub")
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(12.dp), tint = CreatorRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel Renewal", fontSize = 11.sp, color = CreatorRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillingOverviewHeader(
    currentPlan: PlanTier,
    subscription: SubscriptionEntity?,
    totalTransactions: Int,
    onExportCsv: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("billing_overview_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Billing & Invoicing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Gateway: Razorpay & Stripe Live Sandbox",
                        fontSize = 11.sp,
                        color = StudioEmerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onExportCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_export_billing_csv")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Plan Tier", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(currentPlan.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Total Receipts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$totalTransactions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Renewal Date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(subscription?.nextBillingDate ?: "N/A", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Currency", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("USD ($)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StudioEmerald)
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: PaymentTransactionEntity,
    onViewReceipt: () -> Unit
) {
    val planColor = when (transaction.planTier.uppercase()) {
        "CREATOR" -> CreatorRed
        "PRO" -> StudioViolet
        "STUDIO" -> StudioCyan
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_card_${transaction.receiptNumber.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(planColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = planColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${transaction.planTier} Plan (${transaction.billingInterval})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${transaction.receiptNumber} · ${transaction.billingDate}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(StudioEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "VERIFIED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = StudioEmerald
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = transaction.paymentMethod,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.US, "%.2f", transaction.amountUsd)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onViewReceipt,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("btn_view_receipt_${transaction.receiptNumber.lowercase()}")
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Receipt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    tier: PlanTier,
    isAnnual: Boolean,
    isCurrent: Boolean,
    accentColor: Color,
    icon: ImageVector,
    features: List<String>,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayPrice = if (isAnnual) {
        if (tier.monthlyPrice == 0) "$0" else "$${String.format(Locale.US, "%.2f", tier.annualMonthlyPrice)}"
    } else {
        "$${tier.monthlyPrice}"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(
                if (isCurrent) 2.dp else 1.dp,
                if (isCurrent) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .testTag("plan_card_${tier.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tier.title,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tier.title.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = tier.aiActions,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (tier.badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tier.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = displayPrice,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAnnual) " / month (billed annually)" else " / month",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feature list
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(StudioEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = StudioEmerald,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            if (isCurrent) {
                OutlinedButton(
                    onClick = onSelect,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = "Active Plan",
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            } else {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("select_plan_${tier.name.lowercase()}")
                ) {
                    Text(
                        text = if (tier.monthlyPrice == 0) "Switch to Free ($0)" else "Select ${tier.title} ($$displayPrice)",
                        fontWeight = FontWeight.Bold,
                        color = if (accentColor == StudioCyan) Color.Black else Color.White
                    )
                }
            }
        }
    }
}
