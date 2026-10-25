package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CreatorPilotDatabase
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.PlanTier
import com.example.ui.viewmodel.PaymentCheckoutUiState
import com.example.ui.viewmodel.PaymentViewModel
import com.example.util.PaymentVerificationHelper
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
class PaymentViewModelTest {

    private lateinit var db: CreatorPilotDatabase
    private lateinit var repository: ProjectRepository
    private lateinit var paymentViewModel: PaymentViewModel

    @Before
    fun setup() {
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

        paymentViewModel = PaymentViewModel(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `initial state is Idle with no active session or receipts`() {
        assertEquals(PaymentCheckoutUiState.Idle, paymentViewModel.checkoutUiState.value)
        assertNull(paymentViewModel.activeSession.value)
        assertNull(paymentViewModel.lastVerifiedReceipt.value)
        assertFalse(paymentViewModel.isProcessing.value)
    }

    @Test
    fun `initializePaymentSession for Pro plan transitions to AwaitingPayment`() = runBlocking {
        val session = paymentViewModel.executePaymentSessionInitialization(
            plan = PlanTier.PRO,
            isAnnual = false,
            customerEmail = "test@creatorpilot.ai"
        )

        assertNotNull(session)
        val state = paymentViewModel.checkoutUiState.value
        assertTrue("Expected AwaitingPayment state but was $state", state is PaymentCheckoutUiState.AwaitingPayment)
        val activeSession = (state as PaymentCheckoutUiState.AwaitingPayment).session
        assertNotNull(activeSession)
        assertEquals(PlanTier.PRO, activeSession.plan)
        assertEquals(29.0, activeSession.amountUsd, 0.01)
        assertEquals("USD", activeSession.currency)
        assertEquals("test@creatorpilot.ai", activeSession.customerEmail)
        assertTrue(activeSession.orderId.startsWith("order_cp_"))
        assertEquals(activeSession, paymentViewModel.activeSession.value)
    }

    @Test
    fun `initializePaymentSession for annual Creator plan calculates annual total`() = runBlocking {
        val session = paymentViewModel.executePaymentSessionInitialization(
            plan = PlanTier.CREATOR,
            isAnnual = true,
            customerEmail = "creator@creatorpilot.ai"
        )

        assertNotNull(session)
        val state = paymentViewModel.checkoutUiState.value as PaymentCheckoutUiState.AwaitingPayment
        assertEquals(PlanTier.CREATOR, state.session.plan)
        assertTrue(state.session.isAnnual)
        // Creator annual is $9.60/mo * 12 = $115.20
        assertEquals(115.20, state.session.amountUsd, 0.01)
    }

    @Test
    fun `initializePaymentSession for Free tier directly activates subscription and yields Success`() = runBlocking {
        paymentViewModel.executeFreeDowngrade()

        val state = paymentViewModel.checkoutUiState.value
        assertTrue("Expected Success state for Free plan downgrade, got $state", state is PaymentCheckoutUiState.Success)
        val receipt = (state as PaymentCheckoutUiState.Success).receipt
        assertEquals("FREE", receipt.planTier)
        assertEquals(0.0, receipt.amountUsd, 0.01)
        assertTrue(receipt.signatureVerified)

        // Verify in database
        val sub = repository.getSubscriptionSync()
        assertNotNull(sub)
        assertEquals("FREE", sub?.planTier)
        assertEquals(10, sub?.aiActionsTotal)
    }

    @Test
    fun `verifyAndCapturePayment with valid HMAC-SHA256 signature updates status to CAPTURED and yields Success`() = runBlocking {
        val session = paymentViewModel.executePaymentSessionInitialization(plan = PlanTier.PRO, isAnnual = false)
        assertNotNull(session)

        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val validSig = PaymentVerificationHelper.generateRazorpaySignature(session!!.orderId, paymentId)

        val result = paymentViewModel.executePaymentVerification(
            orderId = session.orderId,
            paymentId = paymentId,
            signature = validSig,
            paymentMethod = "Credit Card",
            customerEmail = "pro@creatorpilot.ai"
        )

        assertTrue("Verification must succeed", result.isSuccess)
        val state = paymentViewModel.checkoutUiState.value
        assertTrue("Expected Success state but was $state", state is PaymentCheckoutUiState.Success)
        val receipt = (state as PaymentCheckoutUiState.Success).receipt
        assertEquals("CAPTURED", receipt.status)
        assertTrue(receipt.signatureVerified)
        assertEquals(session.orderId, receipt.orderId)
        assertEquals(paymentId, receipt.transactionId)
        assertNotNull(paymentViewModel.lastVerifiedReceipt.value)

        // Verify database persistence
        val savedSub = repository.getSubscriptionSync()
        assertNotNull(savedSub)
        assertEquals("PRO", savedSub?.planTier)
        assertEquals(500, savedSub?.aiActionsTotal)
        assertTrue(savedSub?.hasPriorityQueue == true)
    }

    @Test
    fun `verifyAndCapturePayment with tampered signature transitions to Failed state with security error`() = runBlocking {
        val session = paymentViewModel.executePaymentSessionInitialization(plan = PlanTier.STUDIO, isAnnual = false)
        assertNotNull(session)

        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val tamperedSig = "fake_tampered_signature_payload"

        val result = paymentViewModel.executePaymentVerification(
            orderId = session!!.orderId,
            paymentId = paymentId,
            signature = tamperedSig
        )

        assertTrue("Verification must fail for tampered signature", result.isFailure)
        val state = paymentViewModel.checkoutUiState.value
        assertTrue("Expected Failed state for tampered signature, got $state", state is PaymentCheckoutUiState.Failed)
        val failed = state as PaymentCheckoutUiState.Failed
        assertTrue(failed.errorMessage.contains("signature verification failed"))
        assertTrue(failed.canRetry)

        // Ensure database was NOT updated to Studio
        val savedSub = repository.getSubscriptionSync()
        assertTrue(savedSub == null || savedSub.planTier != "STUDIO")
    }

    @Test
    fun `cancelCheckoutSession cancels active session and transitions to Cancelled`() = runBlocking {
        paymentViewModel.executePaymentSessionInitialization(plan = PlanTier.CREATOR)
        assertNotNull(paymentViewModel.activeSession.value)

        paymentViewModel.cancelCheckoutSession()

        assertEquals(PaymentCheckoutUiState.Cancelled, paymentViewModel.checkoutUiState.value)
        assertNull(paymentViewModel.activeSession.value)
        assertEquals("Checkout cancelled.", paymentViewModel.userFeedbackMessage.value)
    }

    @Test
    fun `cancelSubscription and reactivateSubscription toggle autoRenew status in database`() = runBlocking {
        // First activate a subscription
        val session = paymentViewModel.executePaymentSessionInitialization(plan = PlanTier.CREATOR)
        assertNotNull(session)
        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val sig = PaymentVerificationHelper.generateRazorpaySignature(session!!.orderId, paymentId)
        paymentViewModel.executePaymentVerification(session.orderId, paymentId, sig)

        var sub = repository.getSubscriptionSync()
        assertTrue(sub?.autoRenew == true)
        assertEquals("ACTIVE", sub?.status)

        // Cancel renewal
        repository.cancelSubscription()

        sub = repository.getSubscriptionSync()
        assertFalse(sub?.autoRenew == true)
        assertEquals("CANCELLED", sub?.status)

        // Reactivate renewal
        repository.reactivateSubscription()

        sub = repository.getSubscriptionSync()
        assertTrue(sub?.autoRenew == true)
        assertEquals("ACTIVE", sub?.status)
    }

    @Test
    fun `runDiagnostics executes all payment diagnostic checks`() {
        paymentViewModel.runDiagnostics()
        val report = paymentViewModel.diagnosticReport.value
        assertNotNull(report)
        assertEquals(4, report?.totalTests)
        assertEquals(4, report?.passedTests)
        assertTrue(report?.items?.all { it.passed } == true)
    }

    @Test
    fun `exportTransactionsCsv formats CSV headers and content`() = runBlocking {
        val session = paymentViewModel.executePaymentSessionInitialization(plan = PlanTier.CREATOR)
        assertNotNull(session)
        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val sig = PaymentVerificationHelper.generateRazorpaySignature(session!!.orderId, paymentId)
        paymentViewModel.executePaymentVerification(session.orderId, paymentId, sig)

        val txns = repository.getAllTransactionsSync()
        val csv = PaymentVerificationHelper.formatTransactionsCsv(txns)
        assertTrue(csv.contains("Receipt Number,Transaction ID,Order ID,Plan"))
        assertTrue(csv.contains("CREATOR"))
    }
}
