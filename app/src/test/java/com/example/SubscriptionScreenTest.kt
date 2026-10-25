package com.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.local.SubscriptionEntity
import com.example.ui.screens.ActiveSubscriptionCard
import com.example.ui.screens.BillingCycleSelector
import com.example.ui.screens.PlanTier
import com.example.ui.screens.SubscriptionPlanCard
import com.example.ui.screens.SubscriptionScreenContent
import com.example.ui.theme.CreatorPilotTheme
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioViolet
import com.example.ui.viewmodel.PaymentCheckoutUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SubscriptionScreenTest {

    @Rule
    @JvmField
    val composeTestRule = createComposeRule()

    @Test
    fun `Creator Plan card displays title, pricing, features, and triggers onSelect`() {
        var clicked = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                SubscriptionPlanCard(
                    plan = PlanTier.CREATOR,
                    isAnnual = false,
                    isCurrentPlan = false,
                    accentColor = StudioEmerald,
                    icon = Icons.Default.Bolt,
                    features = listOf(
                        "150 AI actions per month",
                        "30 full video scripts & viral hooks"
                    ),
                    isProcessing = false,
                    onSelect = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Creator Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("150 AI actions/mo").assertIsDisplayed()
        composeTestRule.onNodeWithText("POPULAR").assertIsDisplayed()
        composeTestRule.onNodeWithText("$12").assertIsDisplayed()
        composeTestRule.onNodeWithText("150 AI actions per month").assertIsDisplayed()

        composeTestRule.onNodeWithTag("subscribe_creator_button").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `Pro Plan card displays featured styling, annual discount, and triggers onSelect`() {
        var clicked = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                SubscriptionPlanCard(
                    plan = PlanTier.PRO,
                    isAnnual = true,
                    isCurrentPlan = false,
                    isFeatured = true,
                    accentColor = CreatorRed,
                    icon = Icons.Default.Star,
                    features = listOf(
                        "500 AI actions per month",
                        "Priority generation queue (zero wait time)"
                    ),
                    isProcessing = false,
                    onSelect = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Pro Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("POWER CREATOR").assertIsDisplayed()
        composeTestRule.onNodeWithText("$23.20").assertIsDisplayed()
        composeTestRule.onNodeWithText("$29/mo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Priority generation queue (zero wait time)").assertIsDisplayed()

        composeTestRule.onNodeWithTag("subscribe_pro_button").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `Studio Plan card displays title, team features, and triggers onSelect`() {
        var clicked = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                SubscriptionPlanCard(
                    plan = PlanTier.STUDIO,
                    isAnnual = false,
                    isCurrentPlan = false,
                    accentColor = StudioViolet,
                    icon = Icons.Default.Groups,
                    features = listOf(
                        "1,500 shared AI actions per month",
                        "5 team member seats & collaborative workspaces"
                    ),
                    isProcessing = false,
                    onSelect = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Studio Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("TEAMS & AGENCIES").assertIsDisplayed()
        composeTestRule.onNodeWithText("$79").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 team member seats & collaborative workspaces").assertIsDisplayed()

        composeTestRule.onNodeWithTag("subscribe_studio_button").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `Active plan displays disabled Active Plan button state`() {
        composeTestRule.setContent {
            CreatorPilotTheme {
                SubscriptionPlanCard(
                    plan = PlanTier.CREATOR,
                    isAnnual = false,
                    isCurrentPlan = true,
                    accentColor = StudioEmerald,
                    icon = Icons.Default.Bolt,
                    features = listOf("150 AI actions per month"),
                    isProcessing = false,
                    onSelect = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Active Plan").assertIsDisplayed()
    }

    @Test
    fun `BillingCycleSelector toggles annual billing and displays 20 percent savings`() {
        var isAnnualState = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                BillingCycleSelector(
                    isAnnual = isAnnualState,
                    onToggle = { isAnnualState = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Billing Cadence").assertIsDisplayed()
        composeTestRule.onNodeWithText("SAVE 20%").assertIsDisplayed()

        composeTestRule.onNodeWithTag("billing_interval_switch").performClick()
        assertTrue(isAnnualState)
    }

    @Test
    fun `ActiveSubscriptionCard displays current subscription details and triggers renewal controls`() {
        val existingSub = SubscriptionEntity(
            userId = "creator_pilot_user",
            planTier = "PRO",
            status = "ACTIVE",
            amountUsd = 29.00,
            billingInterval = "monthly",
            startDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            autoRenew = true,
            lastTransactionId = "pay_test_active",
            lastOrderId = "order_test_active",
            aiActionsTotal = 500,
            aiActionsUsed = 120
        )

        var cancelInvoked = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                ActiveSubscriptionCard(
                    subscription = existingSub,
                    onCancel = { cancelInvoked = true },
                    onReactivate = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("active_subscription_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Current: PRO Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("ACTIVE").assertIsDisplayed()
        composeTestRule.onNodeWithText("Turn off auto-renew").performClick()
        assertTrue(cancelInvoked)
    }

    @Test
    fun `SubscriptionScreenContent top bar and security header render`() {
        var backClicked = false

        composeTestRule.setContent {
            CreatorPilotTheme {
                SubscriptionScreenContent(
                    currentSubscription = null,
                    checkoutUiState = PaymentCheckoutUiState.Idle,
                    isProcessing = false,
                    onSelectPlan = { _, _ -> },
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Choose Your Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("256-Bit SSL").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subscription_back_button").performClick()
        assertTrue(backClicked)
    }
}
