package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.PlanTier
import com.example.util.PaymentVerificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Data representation of an active Razorpay / gateway checkout session.
 */
data class PaymentSession(
    val orderId: String,
    val plan: PlanTier,
    val isAnnual: Boolean,
    val amountUsd: Double,
    val currency: String = "USD",
    val keyId: String = PaymentVerificationHelper.DEFAULT_KEY_ID,
    val customerEmail: String = "creator@creatorpilot.ai",
    val customerPhone: String = "+15550192834",
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * State machine representing the end-to-end stages of the checkout flow:
 * Idle -> Initializing -> AwaitingPayment -> Verifying -> Success / Failed / Cancelled
 */
sealed interface PaymentCheckoutUiState {
    data object Idle : PaymentCheckoutUiState

    data class Initializing(
        val plan: PlanTier,
        val isAnnual: Boolean
    ) : PaymentCheckoutUiState

    data class AwaitingPayment(
        val session: PaymentSession
    ) : PaymentCheckoutUiState

    data class Verifying(
        val orderId: String,
        val paymentId: String
    ) : PaymentCheckoutUiState

    data class Success(
        val receipt: PaymentTransactionEntity
    ) : PaymentCheckoutUiState

    data class Failed(
        val orderId: String?,
        val errorMessage: String,
        val canRetry: Boolean = true
    ) : PaymentCheckoutUiState

    data object Cancelled : PaymentCheckoutUiState
}

/**
 * ViewModel responsible for managing checkout flow state, session initialization,
 * cryptographic signature verification, payment status observations, and subscription updates.
 */
class PaymentViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    // --- State Machine ---
    private val _checkoutUiState = MutableStateFlow<PaymentCheckoutUiState>(PaymentCheckoutUiState.Idle)
    val checkoutUiState: StateFlow<PaymentCheckoutUiState> = _checkoutUiState.asStateFlow()

    private val _activeSession = MutableStateFlow<PaymentSession?>(null)
    val activeSession: StateFlow<PaymentSession?> = _activeSession.asStateFlow()

    private val _lastVerifiedReceipt = MutableStateFlow<PaymentTransactionEntity?>(null)
    val lastVerifiedReceipt: StateFlow<PaymentTransactionEntity?> = _lastVerifiedReceipt.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    private val _diagnosticReport = MutableStateFlow<PaymentVerificationHelper.PaymentDiagnosticReport?>(null)
    val diagnosticReport: StateFlow<PaymentVerificationHelper.PaymentDiagnosticReport?> = _diagnosticReport.asStateFlow()

    // Derived flag indicating active work (initializing order or verifying crypto)
    val isProcessing: StateFlow<Boolean> = _checkoutUiState
        .map { state ->
            state is PaymentCheckoutUiState.Initializing || state is PaymentCheckoutUiState.Verifying
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // --- Database-Backed Reactive Flows ---
    val currentSubscription: StateFlow<SubscriptionEntity?> = repository.currentSubscription
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val transactionHistory: StateFlow<List<PaymentTransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    /**
     * Executes the session initialization as a suspend function.
     * Generates order ID, calculates pricing, and transitions to [PaymentCheckoutUiState.AwaitingPayment].
     */
    suspend fun executePaymentSessionInitialization(
        plan: PlanTier,
        isAnnual: Boolean = false,
        customerEmail: String = "creator@creatorpilot.ai",
        customerPhone: String = "+15550192834",
        delayMs: Long = 0L
    ): PaymentSession? {
        if (plan == PlanTier.FREE) {
            executeFreeDowngrade()
            return null
        }

        _checkoutUiState.value = PaymentCheckoutUiState.Initializing(plan, isAnnual)
        if (delayMs > 0) {
            delay(delayMs)
        }

        val orderId = PaymentVerificationHelper.generateOrderId()
        val amountUsd = if (isAnnual) {
            plan.annualMonthlyPrice * 12.0
        } else {
            plan.monthlyPrice.toDouble()
        }

        val session = PaymentSession(
            orderId = orderId,
            plan = plan,
            isAnnual = isAnnual,
            amountUsd = amountUsd,
            currency = "USD",
            keyId = PaymentVerificationHelper.DEFAULT_KEY_ID,
            customerEmail = customerEmail,
            customerPhone = customerPhone,
            description = "${plan.title} (${if (isAnnual) "Annual Billed" else "Monthly"})"
        )

        _activeSession.value = session
        _checkoutUiState.value = PaymentCheckoutUiState.AwaitingPayment(session)
        return session
    }

    /**
     * Initializes a new payment checkout session via viewModelScope.
     */
    fun initializePaymentSession(
        plan: PlanTier,
        isAnnual: Boolean = false,
        customerEmail: String = "creator@creatorpilot.ai",
        customerPhone: String = "+15550192834"
    ) {
        viewModelScope.launch {
            executePaymentSessionInitialization(plan, isAnnual, customerEmail, customerPhone)
        }
    }

    /**
     * Executes cryptographic signature verification and database persistence.
     */
    suspend fun executePaymentVerification(
        orderId: String,
        paymentId: String,
        signature: String,
        paymentMethod: String = "Credit Card (Visa •••• 4242)",
        customerEmail: String = "creator@creatorpilot.ai",
        delayMs: Long = 0L
    ): Result<PaymentTransactionEntity> {
        _checkoutUiState.value = PaymentCheckoutUiState.Verifying(orderId, paymentId)
        if (delayMs > 0) {
            delay(delayMs)
        }

        val session = _activeSession.value
        val plan = session?.plan ?: PlanTier.CREATOR
        val isAnnual = session?.isAnnual ?: false
        val amount = session?.amountUsd ?: (if (isAnnual) plan.annualMonthlyPrice * 12.0 else plan.monthlyPrice.toDouble())
        val interval = if (isAnnual) "annual" else "monthly"

        // 1. Cryptographic HMAC-SHA256 signature verification
        val isSignatureValid = PaymentVerificationHelper.verifyPaymentSignature(
            orderId = orderId,
            paymentId = paymentId,
            receivedSignature = signature
        )

        if (!isSignatureValid) {
            val errorMsg = "Payment signature verification failed. Cryptographic mismatch on order $orderId."
            _checkoutUiState.value = PaymentCheckoutUiState.Failed(
                orderId = orderId,
                errorMessage = errorMsg,
                canRetry = true
            )
            _userFeedbackMessage.value = "Security alert: payment verification failed."
            return Result.failure(SecurityException(errorMsg))
        }

        // 2. Build verified transaction entity
        val transaction = PaymentTransactionEntity(
            transactionId = paymentId,
            orderId = orderId,
            planTier = plan.name,
            amountUsd = amount,
            currency = "USD",
            billingInterval = interval,
            paymentMethod = paymentMethod,
            status = "CAPTURED",
            signatureVerified = true,
            signature = signature,
            billingDate = PaymentVerificationHelper.getCurrentDateFormatted(),
            nextBillingDate = PaymentVerificationHelper.calculateNextBillingDate(isAnnual),
            customerEmail = customerEmail,
            receiptNumber = PaymentVerificationHelper.generateReceiptNumber()
        )

        // 3. Persist and enforce duplicate transaction protection via Repository
        val recordResult = repository.verifyAndRecordPayment(transaction)

        recordResult.fold(
            onSuccess = { verifiedTx ->
                _lastVerifiedReceipt.value = verifiedTx
                _checkoutUiState.value = PaymentCheckoutUiState.Success(verifiedTx)
                _activeSession.value = null
                _userFeedbackMessage.value = "Payment confirmed for ${verifiedTx.planTier} Plan!"
            },
            onFailure = { error ->
                val errorMsg = error.message ?: "Failed to record payment in database."
                _checkoutUiState.value = PaymentCheckoutUiState.Failed(
                    orderId = orderId,
                    errorMessage = errorMsg,
                    canRetry = false
                )
                _userFeedbackMessage.value = "Error: $errorMsg"
            }
        )
        return recordResult
    }

    /**
     * Submits payment authorization for verification via viewModelScope.
     */
    fun verifyAndCapturePayment(
        orderId: String,
        paymentId: String,
        signature: String,
        paymentMethod: String = "Credit Card (Visa •••• 4242)",
        customerEmail: String = "creator@creatorpilot.ai"
    ) {
        viewModelScope.launch {
            executePaymentVerification(orderId, paymentId, signature, paymentMethod, customerEmail)
        }
    }

    /**
     * Simulates a successful gateway payment authorization end-to-end:
     * Generates a valid payment ID and genuine HMAC-SHA256 signature, then submits verification.
     */
    fun simulateSuccessfulPayment(
        paymentMethod: String = "Credit Card (Visa •••• 4242)",
        customerEmail: String? = null
    ) {
        val session = _activeSession.value
        val orderId = session?.orderId ?: PaymentVerificationHelper.generateOrderId()
        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val validSignature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId)
        val email = customerEmail ?: session?.customerEmail ?: "creator@creatorpilot.ai"

        verifyAndCapturePayment(
            orderId = orderId,
            paymentId = paymentId,
            signature = validSignature,
            paymentMethod = paymentMethod,
            customerEmail = email
        )
    }

    /**
     * Simulates a fraudulent or corrupted payment where the signature is tampered.
     * Demonstrates security rejection and transitions state to [PaymentCheckoutUiState.Failed].
     */
    fun simulateTamperedSignaturePayment(
        paymentMethod: String = "Tampered / Spoofed Client"
    ) {
        val session = _activeSession.value
        val orderId = session?.orderId ?: PaymentVerificationHelper.generateOrderId()
        val paymentId = PaymentVerificationHelper.generateTransactionId()
        val tamperedSignature = "tampered_bad_signature_digest_${System.currentTimeMillis()}"

        verifyAndCapturePayment(
            orderId = orderId,
            paymentId = paymentId,
            signature = tamperedSignature,
            paymentMethod = paymentMethod
        )
    }

    /**
     * Simulates duplicate payment attempt by submitting an already processed transaction ID.
     */
    fun simulateDuplicatePaymentRejection() {
        val existingTxn = transactionHistory.value.firstOrNull()
        if (existingTxn == null) {
            _userFeedbackMessage.value = "No existing transactions found to test duplicate rejection."
            return
        }

        viewModelScope.launch {
            val duplicateTx = existingTxn.copy(
                receiptNumber = PaymentVerificationHelper.generateReceiptNumber()
            )
            val result = repository.verifyAndRecordPayment(duplicateTx)
            result.fold(
                onSuccess = {
                    _userFeedbackMessage.value = "Warning: Duplicate transaction was unexpectedly accepted."
                },
                onFailure = { error ->
                    _userFeedbackMessage.value = "Duplicate protection active: ${error.message}"
                }
            )
        }
    }

    /**
     * Cancels an ongoing checkout session.
     */
    fun cancelCheckoutSession() {
        _activeSession.value = null
        _checkoutUiState.value = PaymentCheckoutUiState.Cancelled
        _userFeedbackMessage.value = "Checkout cancelled."
    }

    /**
     * Resets checkout state back to [PaymentCheckoutUiState.Idle].
     */
    fun resetCheckoutState() {
        _activeSession.value = null
        _checkoutUiState.value = PaymentCheckoutUiState.Idle
    }

    /**
     * Dismisses the active receipt dialog.
     */
    fun dismissReceiptModal() {
        _lastVerifiedReceipt.value = null
    }

    /**
     * Cancels auto-renewal of the current subscription.
     */
    fun cancelSubscription() {
        viewModelScope.launch {
            repository.cancelSubscription()
            _userFeedbackMessage.value = "Subscription auto-renewal cancelled."
        }
    }

    /**
     * Reactivates auto-renewal of the current subscription.
     */
    fun reactivateSubscription() {
        viewModelScope.launch {
            repository.reactivateSubscription()
            _userFeedbackMessage.value = "Subscription auto-renewal reactivated."
        }
    }

    /**
     * Toggles subscription auto-renewal.
     */
    fun toggleAutoRenewal(enabled: Boolean) {
        if (enabled) {
            reactivateSubscription()
        } else {
            cancelSubscription()
        }
    }

    /**
     * Runs internal diagnostics for HMAC-SHA256, billing dates, quota maps, and exports.
     */
    fun runDiagnostics() {
        val report = PaymentVerificationHelper.runSelfDiagnostics()
        _diagnosticReport.value = report
        _userFeedbackMessage.value = "Diagnostics run: ${report.passedTests}/${report.totalTests} tests passed (${report.executionTimeMs}ms)"
    }

    /**
     * Clears user feedback toast/message.
     */
    fun clearUserFeedback() {
        _userFeedbackMessage.value = null
    }

    /**
     * Formats receipt as printable text.
     */
    fun formatReceipt(transaction: PaymentTransactionEntity): String {
        return PaymentVerificationHelper.formatReceiptText(transaction)
    }

    /**
     * Exports transactions history as CSV.
     */
    fun exportTransactionsCsv(): String {
        return PaymentVerificationHelper.formatTransactionsCsv(transactionHistory.value)
    }

    /**
     * Handles switching to Free tier.
     */
    suspend fun executeFreeDowngrade(): PaymentTransactionEntity {
        _checkoutUiState.value = PaymentCheckoutUiState.Initializing(PlanTier.FREE, false)
        val freeSub = PaymentVerificationHelper.buildSubscriptionForPlan(
            plan = PlanTier.FREE,
            isAnnual = false,
            transactionId = "free_downgrade_${System.currentTimeMillis()}",
            orderId = PaymentVerificationHelper.generateOrderId()
        )

        val freeTx = PaymentTransactionEntity(
            transactionId = "free_downgrade_${System.currentTimeMillis()}",
            orderId = PaymentVerificationHelper.generateOrderId(),
            planTier = "FREE",
            amountUsd = 0.0,
            billingInterval = "monthly",
            paymentMethod = "Free Tier",
            status = "CAPTURED",
            signatureVerified = true,
            billingDate = PaymentVerificationHelper.getCurrentDateFormatted(),
            nextBillingDate = PaymentVerificationHelper.calculateNextBillingDate(false),
            receiptNumber = PaymentVerificationHelper.generateReceiptNumber()
        )

        repository.verifyAndRecordPayment(freeTx)
        _lastVerifiedReceipt.value = freeTx
        _checkoutUiState.value = PaymentCheckoutUiState.Success(freeTx)
        _userFeedbackMessage.value = "Switched to Free Plan (10 AI actions/mo)"
        return freeTx
    }
}

/**
 * Factory for creating [PaymentViewModel] with [ProjectRepository] dependency injection.
 */
class PaymentViewModelFactory(
    private val repository: ProjectRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
