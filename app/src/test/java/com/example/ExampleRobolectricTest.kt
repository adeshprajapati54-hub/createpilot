package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CalendarEntryEntity
import com.example.data.local.CreatorPilotDatabase
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.ScriptOutlineEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.PlanTier
import com.example.ui.viewmodel.CreatorPilotViewModel
import com.example.ui.viewmodel.GeneratedIdea
import com.example.util.PaymentVerificationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: CreatorPilotDatabase
    private lateinit var repository: ProjectRepository
    private lateinit var viewModel: CreatorPilotViewModel

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CreatorPilotDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ProjectRepository(
            db.projectDao(),
            db.scriptOutlineDao(),
            db.calendarEntryDao(),
            db.paymentDao(),
            db.subscriptionDao()
        )
        viewModel = CreatorPilotViewModel(repository)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context verifies CreatorPilot AI app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CreatorPilot AI", appName)
    }

    @Test
    fun `project entity default fields verification`() {
        val project = ProjectEntity(
            title = "Test Video",
            niche = "Tech & AI",
            format = "Long-form (8-15 min)"
        )
        assertEquals("Test Video", project.title)
        assertEquals("Idea", project.status)
        assertEquals("2K", project.thumbnailResolution)
        assertTrue(project.retentionScore > 0)
    }

    @Test
    fun `script outline entity persists in Room database`() = runBlocking {
        val outline = ScriptOutlineEntity(
            projectId = 42,
            videoTitle = "AI Revolution in 2026",
            hookText = "Stop scrolling. This changes everything.",
            outlineText = "1. Intro\n2. Analysis\n3. Solution",
            fullNarration = "Today we examine how autonomous agents are reshaping creative workflows.",
            tone = "Energetic & Engaging",
            wordCount = 120,
            estimatedDurationMinutes = 0.8
        )
        val id = db.scriptOutlineDao().insertOutline(outline)
        assertTrue(id > 0)

        val retrieved = db.scriptOutlineDao().getOutlineForProjectSync(42)
        assertNotNull(retrieved)
        assertEquals("AI Revolution in 2026", retrieved?.videoTitle)
        assertEquals("Stop scrolling. This changes everything.", retrieved?.hookText)
    }

    @Test
    fun `calendar entry entity persists in Room database`() = runBlocking {
        val entry = CalendarEntryEntity(
            projectId = 101,
            title = "How to Double YouTube Retention",
            scheduledDate = "2026-11-15",
            status = "Ready",
            niche = "Education & Science",
            format = "Long-form (8-15 min)",
            productionNotes = "Add motion graphics at chapter 2",
            reminderEnabled = true
        )
        val id = db.calendarEntryDao().insertEntry(entry)
        assertTrue(id > 0)

        val retrieved = db.calendarEntryDao().getEntryForProjectSync(101)
        assertNotNull(retrieved)
        assertEquals("2026-11-15", retrieved?.scheduledDate)
        assertEquals("Ready", retrieved?.status)
    }

    @Test
    fun `pricing tiers match specification`() {
        assertEquals(0, PlanTier.FREE.monthlyPrice)
        assertEquals(12, PlanTier.CREATOR.monthlyPrice)
        assertEquals(29, PlanTier.PRO.monthlyPrice)
        assertEquals(79, PlanTier.STUDIO.monthlyPrice)
        assertEquals("10 AI actions/mo", PlanTier.FREE.aiActions)
        assertEquals("150 AI actions/mo", PlanTier.CREATOR.aiActions)
        assertEquals("500 AI actions/mo", PlanTier.PRO.aiActions)
        assertEquals("1,500 shared AI actions/mo", PlanTier.STUDIO.aiActions)
    }

    @Test
    fun `gemini model constants and api service endpoints are correctly configured`() {
        assertEquals("gemini-3.8-flash", GeminiApiClient.MODEL_FLASH)
        assertEquals("gemini-3.1-flash-lite-preview", GeminiApiClient.MODEL_FLASH_LITE)
        assertEquals("gemini-4-argon", GeminiApiClient.MODEL_ARGON)
        assertEquals("gemini-3-pro-image-preview", GeminiApiClient.MODEL_IMAGE_PRO)
        assertEquals("gemini-3.8-flash-tts", GeminiApiClient.MODEL_TTS)
        assertEquals("gemini-omni-1.1-flash", GeminiApiClient.MODEL_OMNI_VIDEO)
        assertEquals("gemini-3.5-transcribe", GeminiApiClient.MODEL_TRANSCRIBE)
    }

    @Test
    fun `full repository database seeding and cascade cleanup CUJ`() = runBlocking {
        // Test seeding if empty
        repository.seedIfEmpty()
        val projects = repository.allProjects.first()
        assertTrue("Seed data should populate initial drafts", projects.isNotEmpty())

        val outlines = repository.allScriptOutlines.first()
        assertTrue("Seed data should populate initial script outlines", outlines.isNotEmpty())

        val calendarEntries = repository.allCalendarEntries.first()
        assertTrue("Seed data should populate initial calendar entries", calendarEntries.isNotEmpty())

        // Insert new custom project
        val customProj = ProjectEntity(
            id = 999,
            title = "Autonomous Coding Agents in Android",
            niche = "Tech & AI",
            format = "Long-form (8-15 min)",
            status = "Scripting",
            scheduledDate = "2026-11-20"
        )
        repository.insertProject(customProj)

        repository.saveScriptOutline(
            ScriptOutlineEntity(
                projectId = 999,
                videoTitle = customProj.title,
                hookText = "Can an AI agent write a complete production Android app?",
                outlineText = "1. Setup\n2. Execution\n3. Verification",
                fullNarration = "In this video we run live benchmarks against real build chains."
            )
        )

        repository.saveCalendarEntry(
            CalendarEntryEntity(
                projectId = 999,
                title = customProj.title,
                scheduledDate = "2026-11-20",
                status = "Scripting"
            )
        )

        // Verify retrieval
        val retrievedProj = repository.getProjectSync(999)
        assertNotNull(retrievedProj)
        assertEquals("Autonomous Coding Agents in Android", retrievedProj?.title)

        val retrievedOutline = repository.getOutlineForProjectSync(999)
        assertNotNull(retrievedOutline)
        assertEquals("Can an AI agent write a complete production Android app?", retrievedOutline?.hookText)

        // Delete project and verify cascade deletion in repository
        repository.deleteProjectById(999)
        assertNull(repository.getProjectSync(999))
        assertNull(repository.getOutlineForProjectSync(999))
    }

    @Test
    fun `viewmodel workflow and exports generation CUJ`() = runBlocking {
        // Test Idea to Project Creation
        val idea = GeneratedIdea(
            title = "10 YouTube Algorithm Secrets in 2026",
            hook = "You've been optimizing the wrong metrics.",
            targetAudience = "Content creators",
            angle = "AVD vs CTR correlation",
            estimatedViewsScore = 96
        )

        val created = viewModel.persistIdeaAsProject(idea, niche = "Tech & AI", format = "Long-form (8-15 min)")
        assertNotNull(created)
        assertEquals("10 YouTube Algorithm Secrets in 2026", created.title)

        // Test Production Brief Export
        val brief = viewModel.generateProductionNotesExport(created)
        assertTrue("Brief must contain project title", brief.contains(idea.title))
        assertTrue("Brief must contain target audience", brief.contains("Content creators"))
        assertTrue("Brief must contain hook", brief.contains(idea.hook))

        // Test Calendar CSV Export
        val csv = viewModel.generateCalendarCsv(listOf(created))
        assertTrue("CSV header check", csv.startsWith("Title,Niche,Format,Status,Scheduled Date,Retention Score"))
        assertTrue("CSV content check", csv.contains("10 YouTube Algorithm Secrets in 2026"))

        // Test Plan Selection
        viewModel.setCurrentPlan(PlanTier.PRO)
        assertEquals(PlanTier.PRO, viewModel.currentPlan.value)

        viewModel.setCurrentPlan(PlanTier.STUDIO)
        assertEquals(PlanTier.STUDIO, viewModel.currentPlan.value)
    }

    @Test
    fun `bottom navigation bar destinations contain Ideas Scripts and Calendar`() {
        val destinations = listOf(AppScreen.Ideas, AppScreen.Scripts, AppScreen.Calendar)
        assertEquals(3, destinations.size)
        assertEquals("Ideas", AppScreen.Ideas.name)
        assertEquals("Scripts", AppScreen.Scripts.name)
        assertEquals("Calendar", AppScreen.Calendar.name)
    }

    @Test
    fun `hmac-sha256 signature calculation and verification validates authentic webhook payloads`() {
        val orderId = "order_cp_88312"
        val paymentId = "pay_rzp_live_test_7719"
        val secret = "test_webhook_secret_key_123"

        val signature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId, secret)
        assertNotNull(signature)
        assertTrue(signature.isNotBlank())
        assertEquals(64, signature.length) // SHA256 hex is 64 characters

        val isValid = PaymentVerificationHelper.verifyPaymentSignature(orderId, paymentId, signature, secret)
        assertTrue("Authentic signature must be verified", isValid)

        val isTamperedValid = PaymentVerificationHelper.verifyPaymentSignature(
            orderId = "order_cp_TAMPERED",
            paymentId = paymentId,
            receivedSignature = signature,
            secret = secret
        )
        assertFalse("Tampered orderId must fail verification", isTamperedValid)

        val isTamperedSigValid = PaymentVerificationHelper.verifyPaymentSignature(
            orderId = orderId,
            paymentId = paymentId,
            receivedSignature = "deadbeef12345678",
            secret = secret
        )
        assertFalse("Tampered signature must fail verification", isTamperedSigValid)
    }

    @Test
    fun `payment transaction entity persists in Room database`() = runBlocking {
        val tx = PaymentTransactionEntity(
            transactionId = "pay_rzp_creator_test_01",
            orderId = "order_cp_1001",
            planTier = "CREATOR",
            amountUsd = 12.00,
            currency = "USD",
            billingInterval = "monthly",
            paymentMethod = "Credit Card",
            status = "CAPTURED",
            signatureVerified = true,
            signature = "sig_abc123",
            billingDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            customerEmail = "creator@studio.io",
            receiptNumber = "RCP-2026-1001"
        )

        val rowId = db.paymentDao().insertTransaction(tx)
        assertTrue(rowId > 0)

        val retrieved = db.paymentDao().getTransactionById("pay_rzp_creator_test_01")
        assertNotNull(retrieved)
        assertEquals("CREATOR", retrieved?.planTier)
        assertEquals(12.00, retrieved?.amountUsd ?: 0.0, 0.001)
        assertEquals("CAPTURED", retrieved?.status)
        assertEquals("RCP-2026-1001", retrieved?.receiptNumber)
        assertTrue(retrieved?.signatureVerified == true)
    }

    @Test
    fun `subscription entity persists and updates renewal status in Room`() = runBlocking {
        val sub = SubscriptionEntity(
            userId = "creator_pilot_user",
            planTier = "PRO",
            status = "ACTIVE",
            amountUsd = 29.00,
            billingInterval = "monthly",
            startDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            autoRenew = true,
            lastTransactionId = "pay_pro_101",
            lastOrderId = "order_pro_101",
            aiActionsTotal = 500,
            scriptsAllowed = 100,
            scenePromptsAllowed = 500,
            activeProjectsLimit = 50,
            hasPriorityQueue = true
        )

        db.subscriptionDao().upsertSubscription(sub)
        val retrieved = db.subscriptionDao().getSubscriptionSync("creator_pilot_user")
        assertNotNull(retrieved)
        assertEquals("PRO", retrieved?.planTier)
        assertEquals(500, retrieved?.aiActionsTotal)
        assertTrue(retrieved?.hasPriorityQueue == true)
        assertTrue(retrieved?.autoRenew == true)

        // Cancel auto-renew
        db.subscriptionDao().updateAutoRenew(false, userId = "creator_pilot_user")
        val updated = db.subscriptionDao().getSubscriptionSync("creator_pilot_user")
        assertFalse(updated?.autoRenew == true)
    }

    @Test
    fun `repository verifyAndRecordPayment processes valid payment and activates plan tier`() = runBlocking {
        val orderId = "order_cp_9901"
        val paymentId = "pay_rzp_studio_test"
        val secret = PaymentVerificationHelper.DEFAULT_WEBHOOK_SECRET
        val signature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId, secret)

        val tx = PaymentTransactionEntity(
            transactionId = paymentId,
            orderId = orderId,
            planTier = "STUDIO",
            amountUsd = 79.00,
            currency = "USD",
            billingInterval = "monthly",
            paymentMethod = "Credit Card",
            status = "PENDING",
            signatureVerified = false,
            signature = signature,
            billingDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            receiptNumber = "RCP-2026-7777"
        )

        val result = repository.verifyAndRecordPayment(tx, secret)
        assertTrue("Payment verification must succeed", result.isSuccess)

        val verified = result.getOrNull()
        assertNotNull(verified)
        assertEquals("CAPTURED", verified?.status)
        assertTrue(verified?.signatureVerified == true)

        // Verify active subscription updated to Studio
        val activeSub = repository.getSubscriptionSync()
        assertNotNull(activeSub)
        assertEquals("STUDIO", activeSub?.planTier)
        assertEquals(1500, activeSub?.aiActionsTotal)
        assertEquals(5, activeSub?.teamMembersLimit)
        assertTrue(activeSub?.hasTeamWorkspaces == true)
    }

    @Test
    fun `repository duplicate protection rejects repeated transaction delivery`() = runBlocking {
        val orderId = "order_cp_dup_test"
        val paymentId = "pay_rzp_duplicate_001"
        val secret = PaymentVerificationHelper.DEFAULT_WEBHOOK_SECRET
        val signature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId, secret)

        val tx = PaymentTransactionEntity(
            transactionId = paymentId,
            orderId = orderId,
            planTier = "CREATOR",
            amountUsd = 12.00,
            signature = signature,
            billingDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            receiptNumber = "RCP-DUP-01"
        )

        val firstAttempt = repository.verifyAndRecordPayment(tx, secret)
        assertTrue("First attempt should succeed", firstAttempt.isSuccess)

        // Attempting to process the same transaction again (simulating duplicate webhook / replay)
        val duplicateAttempt = repository.verifyAndRecordPayment(tx, secret)
        assertTrue("Duplicate attempt must fail", duplicateAttempt.isFailure)
        val errorMsg = duplicateAttempt.exceptionOrNull()?.message
        assertTrue(errorMsg?.contains("Duplicate transaction") == true)
    }

    @Test
    fun `tampered payment signature is rejected with SecurityException`() = runBlocking {
        val tx = PaymentTransactionEntity(
            transactionId = "pay_rzp_forged",
            orderId = "order_cp_forged",
            planTier = "PRO",
            amountUsd = 29.00,
            signature = "tampered_fake_signature_hex_value_000000000000000000000000000000",
            billingDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            receiptNumber = "RCP-FORGED"
        )

        val result = repository.verifyAndRecordPayment(tx)
        assertTrue("Fraudulent or unverified payment must be rejected", result.isFailure)
        assertTrue(result.exceptionOrNull() is SecurityException)
    }

    @Test
    fun `receipt formatting and CSV export include transaction details and headers`() {
        val tx = PaymentTransactionEntity(
            transactionId = "pay_rzp_format_test",
            orderId = "order_cp_format_test",
            planTier = "CREATOR",
            amountUsd = 12.00,
            currency = "USD",
            billingInterval = "monthly",
            paymentMethod = "Credit Card",
            status = "CAPTURED",
            signatureVerified = true,
            billingDate = "2026-10-24",
            nextBillingDate = "2026-11-23",
            customerEmail = "creator@creatorpilot.ai",
            receiptNumber = "RCP-2026-8888"
        )

        val receiptText = PaymentVerificationHelper.formatReceiptText(tx)
        assertTrue(receiptText.contains("CREATORPILOT AI RECEIPT"))
        assertTrue(receiptText.contains("RCP-2026-8888"))
        assertTrue(receiptText.contains("pay_rzp_format_test"))
        assertTrue(receiptText.contains("$12.00 USD"))
        assertTrue(receiptText.contains("HMAC-SHA256 VERIFIED"))
        assertTrue(receiptText.contains("Next Billing Date: 2026-11-23"))

        val csv = PaymentVerificationHelper.formatTransactionsCsv(listOf(tx))
        assertTrue(csv.startsWith("Receipt Number,Transaction ID,Order ID,Plan,Amount (USD)"))
        assertTrue(csv.contains("RCP-2026-8888"))
        assertTrue(csv.contains("12.0"))
    }

    @Test
    fun `payment verification helper runSelfDiagnostics executes and passes 4 out of 4 tests`() {
        val report = PaymentVerificationHelper.runSelfDiagnostics()
        assertEquals(4, report.totalTests)
        assertEquals(4, report.passedTests)
        assertTrue(report.items.all { it.passed })
        assertTrue("Execution time must be recorded", report.executionTimeMs >= 0)

        val cryptoItem = report.items.firstOrNull { it.name.contains("HMAC-SHA256") }
        assertNotNull(cryptoItem)
        assertTrue(cryptoItem?.passed == true)
        assertTrue(cryptoItem?.detail?.contains("64-char") == true)

        val quotaItem = report.items.firstOrNull { it.name.contains("Quota") }
        assertNotNull(quotaItem)
        assertTrue(quotaItem?.passed == true)
    }

    @Test
    fun `viewmodel testFraudRejection catches security tampering and preserves plan`() = runBlocking {
        val initialPlan = viewModel.currentPlan.value
        val (successResult, errorMsg) = viewModel.executeFraudTest()

        assertTrue("Fraud rejection must be recognized as security success", successResult)
        assertTrue("Error message should mention signature or verification", errorMsg.contains("signature", ignoreCase = true) || errorMsg.contains("verification", ignoreCase = true))
        assertEquals("Current plan must remain unchanged after fraudulent attempt", initialPlan, viewModel.currentPlan.value)

        val feedback = viewModel.simulationFeedback.value
        assertNotNull(feedback)
        assertTrue(feedback?.contains("Security Check Passed") == true)
    }

    @Test
    fun `viewmodel testDuplicateRejection enforces idempotency and protects against replay`() = runBlocking {
        val (passResult, errorMsg) = viewModel.executeDuplicateTest()

        assertTrue("Duplicate rejection must succeed", passResult)
        assertTrue("Error message should mention duplicate", errorMsg.contains("Duplicate", ignoreCase = true))

        val feedback = viewModel.simulationFeedback.value
        assertNotNull(feedback)
        assertTrue(feedback?.contains("Idempotency Enforced") == true)
    }
}



