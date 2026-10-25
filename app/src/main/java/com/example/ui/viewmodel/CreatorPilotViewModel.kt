package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalendarEntryEntity
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.ScriptOutlineEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.ProjectRepository
import com.example.ui.screens.PlanTier
import com.example.util.AudioRecorderHelper
import com.example.util.PaymentVerificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class GeneratedIdea(
    val title: String,
    val hook: String,
    val targetAudience: String,
    val angle: String,
    val estimatedViewsScore: Int
)

data class VideoAnalysisResult(
    val retentionScore: Int,
    val hookRating: String,
    val dropOffPoints: List<String>,
    val optimizationTips: List<String>,
    val highCtrTitles: List<String>,
    val tags: List<String>
)

class CreatorPilotViewModel(
    private val repository: ProjectRepository
) : ViewModel() {

    // --- Room Reactive Flows ---
    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val allCalendarEntries: StateFlow<List<CalendarEntryEntity>> = repository.allCalendarEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val allScriptOutlines: StateFlow<List<ScriptOutlineEntity>> = repository.allScriptOutlines
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val selectedProject: StateFlow<ProjectEntity?> = _selectedProject.asStateFlow()

    // --- Billing, Payments & Subscriptions ---
    val allTransactions: StateFlow<List<PaymentTransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val currentSubscription: StateFlow<SubscriptionEntity?> = repository.currentSubscription
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    // Plan Tier & Quota State
    private val _currentPlan = MutableStateFlow(PlanTier.CREATOR)
    val currentPlan: StateFlow<PlanTier> = _currentPlan.asStateFlow()

    fun setCurrentPlan(plan: PlanTier) {
        _currentPlan.value = plan
        showMessage("Switched to ${plan.title}")
    }

    // Checkout & Receipt Modal State
    private val _activeCheckoutPlan = MutableStateFlow<Pair<PlanTier, Boolean>?>(null)
    val activeCheckoutPlan: StateFlow<Pair<PlanTier, Boolean>?> = _activeCheckoutPlan.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _lastVerifiedReceipt = MutableStateFlow<PaymentTransactionEntity?>(null)
    val lastVerifiedReceipt: StateFlow<PaymentTransactionEntity?> = _lastVerifiedReceipt.asStateFlow()

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError.asStateFlow()

    private val _diagnosticReport = MutableStateFlow<PaymentVerificationHelper.PaymentDiagnosticReport?>(null)
    val diagnosticReport: StateFlow<PaymentVerificationHelper.PaymentDiagnosticReport?> = _diagnosticReport.asStateFlow()

    private val _simulationFeedback = MutableStateFlow<String?>(null)
    val simulationFeedback: StateFlow<String?> = _simulationFeedback.asStateFlow()

    fun initiateCheckout(plan: PlanTier, isAnnual: Boolean = false) {
        if (plan == PlanTier.FREE) {
            // Free plan doesn't require checkout
            viewModelScope.launch {
                val sub = PaymentVerificationHelper.buildSubscriptionForPlan(
                    plan = PlanTier.FREE,
                    isAnnual = false,
                    transactionId = "free_downgrade_${System.currentTimeMillis()}",
                    orderId = PaymentVerificationHelper.generateOrderId()
                )
                repository.verifyAndRecordPayment(
                    PaymentTransactionEntity(
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
                )
                _currentPlan.value = PlanTier.FREE
                showMessage("Switched to Free Plan (10 AI actions/mo)")
            }
            return
        }
        _checkoutError.value = null
        _activeCheckoutPlan.value = Pair(plan, isAnnual)
    }

    fun dismissCheckout() {
        _activeCheckoutPlan.value = null
        _checkoutError.value = null
    }

    fun dismissReceiptModal() {
        _lastVerifiedReceipt.value = null
    }

    /**
     * Executes real payment processing simulation:
     * 1. Creates order and transaction IDs
     * 2. Computes HMAC-SHA256 signature
     * 3. Verifies signature against secret
     * 4. Enforces duplicate transaction protection
     * 5. Saves captured record to Room DB
     * 6. Activates subscription & quota tier
     */
    fun processCheckoutPayment(
        plan: PlanTier,
        isAnnual: Boolean,
        paymentMethod: String = "Credit Card",
        customerEmail: String = "creator@creatorpilot.ai",
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isProcessingPayment.value = true
            _checkoutError.value = null
            try {
                // Simulate gateway latency
                kotlinx.coroutines.delay(800)

                val orderId = PaymentVerificationHelper.generateOrderId()
                val paymentId = PaymentVerificationHelper.generateTransactionId()
                val signature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId)
                val amount = if (isAnnual) plan.annualMonthlyPrice * 12 else plan.monthlyPrice.toDouble()
                val interval = if (isAnnual) "annual" else "monthly"

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
                    receiptNumber = PaymentVerificationHelper.generateReceiptNumber(),
                    gatewayProvider = "Razorpay Live Sandbox (USD)",
                    notes = "Captured and verified via HMAC-SHA256"
                )

                val result = repository.verifyAndRecordPayment(transaction)
                if (result.isSuccess) {
                    val verified = result.getOrThrow()
                    _currentPlan.value = plan
                    _lastVerifiedReceipt.value = verified
                    _activeCheckoutPlan.value = null
                    showMessage("Payment of $${String.format(java.util.Locale.US, "%.2f", amount)} USD received! ${plan.title} activated.")
                    onComplete?.invoke(true)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Payment verification failed"
                    _checkoutError.value = errorMsg
                    showMessage("Payment Error: $errorMsg")
                    onComplete?.invoke(false)
                }
            } catch (e: Exception) {
                _checkoutError.value = e.message ?: "Unexpected checkout error"
                showMessage("Checkout failed: ${e.message}")
                onComplete?.invoke(false)
            } finally {
                _isProcessingPayment.value = false
            }
        }
    }

    /**
     * Directly processes a webhook or server verification notification
     */
    suspend fun verifyAndApplyWebhookPayment(
        transaction: PaymentTransactionEntity,
        secret: String = PaymentVerificationHelper.DEFAULT_WEBHOOK_SECRET
    ): Result<PaymentTransactionEntity> {
        val result = repository.verifyAndRecordPayment(transaction, secret)
        if (result.isSuccess) {
            val verified = result.getOrThrow()
            val plan = when (verified.planTier.uppercase()) {
                "CREATOR" -> PlanTier.CREATOR
                "PRO" -> PlanTier.PRO
                "STUDIO" -> PlanTier.STUDIO
                else -> PlanTier.FREE
            }
            _currentPlan.value = plan
            _lastVerifiedReceipt.value = verified
        }
        return result
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            repository.cancelSubscription()
            showMessage("Auto-renewal cancelled. You retain paid access until the end of your billing cycle.")
        }
    }

    fun reactivateSubscription() {
        viewModelScope.launch {
            repository.reactivateSubscription()
            showMessage("Subscription reactivated. Auto-renewal is now active.")
        }
    }

    fun generateBillingHistoryCsv(): String {
        val txs = allTransactions.value
        return PaymentVerificationHelper.formatTransactionsCsv(txs)
    }

    fun generateReceiptText(tx: PaymentTransactionEntity): String {
        return PaymentVerificationHelper.formatReceiptText(tx)
    }

    fun runPaymentDiagnostics() {
        val report = PaymentVerificationHelper.runSelfDiagnostics()
        _diagnosticReport.value = report
        showMessage("Diagnostics: ${report.passedTests}/${report.totalTests} Passed (${report.executionTimeMs}ms)")
    }

    fun clearSimulationFeedback() {
        _simulationFeedback.value = null
    }

    suspend fun executeFraudTest(): Pair<Boolean, String> {
        _isProcessingPayment.value = true
        val fakeTx = PaymentTransactionEntity(
            transactionId = "pay_forged_${System.currentTimeMillis()}",
            orderId = PaymentVerificationHelper.generateOrderId(),
            planTier = "STUDIO",
            amountUsd = 79.0,
            signature = "invalid_forged_tampered_signature_00000000000000000000000000000000",
            billingDate = PaymentVerificationHelper.getCurrentDateFormatted(),
            nextBillingDate = PaymentVerificationHelper.calculateNextBillingDate(false),
            receiptNumber = "RCP-FRAUD-TEST"
        )
        val result = repository.verifyAndRecordPayment(fakeTx)
        _isProcessingPayment.value = false
        return if (result.isFailure) {
            val err = result.exceptionOrNull()?.message ?: "Security exception"
            _simulationFeedback.value = "Security Check Passed: Tampered signature rejected ($err). Plan remains ${_currentPlan.value.title}."
            showMessage("Security Check Passed: Tampered payment was rejected!")
            Pair(true, err)
        } else {
            _simulationFeedback.value = "Warning: Tampered payment was not rejected."
            Pair(false, "Failed to reject tampered signature")
        }
    }

    fun testFraudRejection(onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            val res = executeFraudTest()
            onComplete?.invoke(res.first, res.second)
        }
    }

    suspend fun executeDuplicateTest(): Pair<Boolean, String> {
        _isProcessingPayment.value = true
        val existing = allTransactions.value.firstOrNull()
        val res = if (existing != null) {
            val duplicateTx = existing.copy(
                receiptNumber = "RCP-DUP-${System.currentTimeMillis()}"
            )
            val result = repository.verifyAndRecordPayment(duplicateTx)
            if (result.isFailure) {
                val err = result.exceptionOrNull()?.message ?: "Duplicate rejected"
                _simulationFeedback.value = "Idempotency Enforced: $err. Double-crediting prevented!"
                showMessage("Duplicate Protection Verified: Replay transaction rejected.")
                Pair(true, err)
            } else {
                _simulationFeedback.value = "Warning: Duplicate transaction was accepted."
                Pair(false, "Failed to reject duplicate")
            }
        } else {
            // If no transaction yet, simulate a legitimate creator payment first
            val orderId = PaymentVerificationHelper.generateOrderId()
            val paymentId = "pay_seed_dup_test_${System.currentTimeMillis()}"
            val signature = PaymentVerificationHelper.generateRazorpaySignature(orderId, paymentId)
            val seedTx = PaymentTransactionEntity(
                transactionId = paymentId,
                orderId = orderId,
                planTier = "CREATOR",
                amountUsd = 12.0,
                status = "CAPTURED",
                signature = signature,
                signatureVerified = true,
                billingDate = PaymentVerificationHelper.getCurrentDateFormatted(),
                nextBillingDate = PaymentVerificationHelper.calculateNextBillingDate(false),
                receiptNumber = PaymentVerificationHelper.generateReceiptNumber()
            )
            repository.verifyAndRecordPayment(seedTx)

            // Now attempt the duplicate with identical transactionId
            val result = repository.verifyAndRecordPayment(seedTx)
            if (result.isFailure) {
                val err = result.exceptionOrNull()?.message ?: "Duplicate rejected"
                _simulationFeedback.value = "Idempotency Enforced: $err. Replay delivery was blocked!"
                showMessage("Duplicate Protection Verified: Replay transaction rejected.")
                Pair(true, err)
            } else {
                _simulationFeedback.value = "Warning: Duplicate was accepted."
                Pair(false, "Failed to reject duplicate")
            }
        }
        _isProcessingPayment.value = false
        return res
    }

    fun testDuplicateRejection(onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            val res = executeDuplicateTest()
            onComplete?.invoke(res.first, res.second)
        }
    }


    // Idea Lab State
    private val _isGeneratingIdeas = MutableStateFlow(false)
    val isGeneratingIdeas: StateFlow<Boolean> = _isGeneratingIdeas.asStateFlow()

    private val _generatedIdeas = MutableStateFlow<List<GeneratedIdea>>(emptyList())
    val generatedIdeas: StateFlow<List<GeneratedIdea>> = _generatedIdeas.asStateFlow()

    // Script Studio State
    private val _isGeneratingScript = MutableStateFlow(false)
    val isGeneratingScript: StateFlow<Boolean> = _isGeneratingScript.asStateFlow()

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    private val _ttsStatus = MutableStateFlow("")
    val ttsStatus: StateFlow<String> = _ttsStatus.asStateFlow()

    // Audio Dictation & Transcription (gemini-3.5-transcribe)
    private var audioRecorderHelper: AudioRecorderHelper? = null
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _isTranscribingAudio = MutableStateFlow(false)
    val isTranscribingAudio: StateFlow<Boolean> = _isTranscribingAudio.asStateFlow()

    // Scene & Thumbnail Studio State
    private val _isGeneratingScenes = MutableStateFlow(false)
    val isGeneratingScenes: StateFlow<Boolean> = _isGeneratingScenes.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _isGeneratingVideo = MutableStateFlow(false)
    val isGeneratingVideo: StateFlow<Boolean> = _isGeneratingVideo.asStateFlow()

    private val _videoGenStatus = MutableStateFlow("")
    val videoGenStatus: StateFlow<String> = _videoGenStatus.asStateFlow()

    // Video Analysis State (Gemini 4 Argon)
    private val _isAnalyzingVideo = MutableStateFlow(false)
    val isAnalyzingVideo: StateFlow<Boolean> = _isAnalyzingVideo.asStateFlow()

    private val _videoAnalysisResult = MutableStateFlow<VideoAnalysisResult?>(null)
    val videoAnalysisResult: StateFlow<VideoAnalysisResult?> = _videoAnalysisResult.asStateFlow()

    // Notification / Toast Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
            repository.currentSubscription.collect { sub ->
                if (sub != null) {
                    val tier = when (sub.planTier.uppercase()) {
                        "CREATOR" -> PlanTier.CREATOR
                        "PRO" -> PlanTier.PRO
                        "STUDIO" -> PlanTier.STUDIO
                        else -> PlanTier.FREE
                    }
                    _currentPlan.value = tier
                }
            }
        }
    }

    fun selectProject(project: ProjectEntity?) {
        _selectedProject.value = project
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // --- IDEA LAB ---
    fun generateIdeas(
        topicOrKeyword: String,
        niche: String,
        format: String,
        model: String = GeminiApiClient.MODEL_FLASH
    ) {
        viewModelScope.launch {
            _isGeneratingIdeas.value = true
            val prompt = """
                You are a senior YouTube content strategist and algorithm expert.
                Generate 4 high-performing video concepts for:
                Topic/Keyword: $topicOrKeyword
                Niche: $niche
                Format: $format
                
                Respond in valid JSON array format only:
                [
                  {
                    "title": "High CTR Click-worthy Title (under 60 chars)",
                    "hook": "Psychological opening hook for the first 15-30 seconds",
                    "targetAudience": "Specific viewer persona who cares",
                    "angle": "Unique perspective or contrarian angle",
                    "estimatedViewsScore": 88
                  }
                ]
            """.trimIndent()

            val result = GeminiApiClient.generateText(prompt, model = model)
            result.onSuccess { text ->
                try {
                    val cleanJson = extractJsonArray(text)
                    val jsonArray = JSONArray(cleanJson)
                    val ideasList = mutableListOf<GeneratedIdea>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        ideasList.add(
                            GeneratedIdea(
                                title = obj.optString("title", "Untitled Idea"),
                                hook = obj.optString("hook", ""),
                                targetAudience = obj.optString("targetAudience", ""),
                                angle = obj.optString("angle", ""),
                                estimatedViewsScore = obj.optInt("estimatedViewsScore", 85)
                            )
                        )
                    }
                    _generatedIdeas.value = ideasList
                    showMessage("Generated ${ideasList.size} YouTube concepts with $model")
                } catch (e: Exception) {
                    _generatedIdeas.value = getFallbackIdeas(topicOrKeyword, niche, format)
                    showMessage("Generated concepts (Demo mode)")
                }
            }.onFailure {
                _generatedIdeas.value = getFallbackIdeas(topicOrKeyword, niche, format)
                showMessage("Generated concepts (Demo mode)")
            }
            _isGeneratingIdeas.value = false
        }
    }

    fun createProjectFromIdea(
        idea: GeneratedIdea,
        niche: String,
        format: String,
        onCreated: ((ProjectEntity) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val created = persistIdeaAsProject(idea, niche, format)
            onCreated?.invoke(created)
        }
    }

    suspend fun persistIdeaAsProject(
        idea: GeneratedIdea,
        niche: String,
        format: String
    ): ProjectEntity {
        val scheduledDate = "2026-11-01"
        val newProject = ProjectEntity(
            title = idea.title,
            niche = niche,
            format = format,
            status = "Idea",
            targetAudience = idea.targetAudience,
            hook = idea.hook,
            outline = "1. Introduction & Hook\n2. The Core Conflict / Big Problem\n3. The Breakthrough Solution\n4. Step-by-Step Execution\n5. Final Verdict & Call to Action",
            tone = "Energetic & Engaging",
            scheduledDate = scheduledDate
        )
        val newId = repository.insertProject(newProject)
        val created = repository.getProjectSync(newId) ?: newProject.copy(id = newId)
        _selectedProject.value = created

        // Also persist corresponding ScriptOutlineEntity and CalendarEntryEntity
        repository.saveScriptOutline(
            ScriptOutlineEntity(
                projectId = newId,
                videoTitle = idea.title,
                hookText = idea.hook,
                outlineText = newProject.outline,
                fullNarration = "",
                tone = newProject.tone,
                isAiGenerated = true
            )
        )

        repository.saveCalendarEntry(
            CalendarEntryEntity(
                projectId = newId,
                title = idea.title,
                scheduledDate = scheduledDate,
                status = "Idea",
                niche = niche,
                format = format,
                productionNotes = "Concept: ${idea.angle}"
            )
        )

        showMessage("Saved project, outline, and calendar entry to Room!")
        return created
    }

    // --- SCRIPT STUDIO ---
    fun generateScriptForProject(
        project: ProjectEntity,
        tone: String,
        additionalNotes: String = ""
    ) {
        viewModelScope.launch {
            _isGeneratingScript.value = true
            val prompt = """
                You are a master YouTube scriptwriter.
                Write a complete, highly engaging script structure and full narration for:
                Title: ${project.title}
                Niche: ${project.niche}
                Format: ${project.format}
                Tone: $tone
                Hook to build on: ${project.hook}
                Additional Context: $additionalNotes

                Provide two sections clearly:
                --- OUTLINE ---
                [List chapters and key beats with timestamps]

                --- FULL SCRIPT ---
                [Full word-for-word voiceover narration including delivery notes like [Visual: ...], [Tone: ...], and [Sound: ...]]
            """.trimIndent()

            val result = GeminiApiClient.generateText(prompt, model = GeminiApiClient.MODEL_FLASH)
            result.onSuccess { text ->
                val outlineText: String
                val scriptText: String
                if (text.contains("--- FULL SCRIPT ---")) {
                    val parts = text.split("--- FULL SCRIPT ---")
                    outlineText = parts[0].replace("--- OUTLINE ---", "").trim()
                    scriptText = parts[1].trim()
                } else {
                    outlineText = project.outline.ifBlank { "1. Hook & Intro\n2. Key Insights\n3. Practical Guide\n4. Conclusion" }
                    scriptText = text.trim()
                }

                val words = scriptText.split("\\s+".toRegex()).count { it.isNotBlank() }
                val updated = project.copy(
                    outline = outlineText,
                    fullScript = scriptText,
                    tone = tone,
                    status = if (project.status == "Idea") "Scripting" else project.status,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated

                // Save persistent ScriptOutlineEntity to Room
                repository.saveScriptOutline(
                    ScriptOutlineEntity(
                        projectId = project.id,
                        videoTitle = project.title,
                        hookText = updated.hook,
                        outlineText = outlineText,
                        fullNarration = scriptText,
                        tone = tone,
                        wordCount = words,
                        estimatedDurationMinutes = words / 150.0,
                        isAiGenerated = true,
                        updatedAt = System.currentTimeMillis()
                    )
                )

                showMessage("Script outline and narration saved to Room!")
            }.onFailure {
                // Fallback realistic script
                val fallbackScript = """
                    [0:00 - 0:25] HOOK:
                    ${project.hook.ifBlank { "Most people get this completely backwards." }}
                    [Visual: Fast-paced montage with sound whoosh]
                    
                    [0:25 - 1:40] THE CORE ISSUE:
                    Here's why what you've been taught no longer works in 2026. The algorithm rewards retention and real transformation, not clickbait fluff.
                    
                    [1:40 - 4:30] STEP-BY-STEP BREAKDOWN:
                    Step 1: Audit your current friction points.
                    Step 2: Automate repetitive tasks using modern toolchains.
                    Step 3: Measure the actual conversion, not vanity views.
                    
                    [4:30 - End] CONCLUSION & CTA:
                    If you found value in this breakdown, hit subscribe and check the pinned comment for the full resources checklist!
                """.trimIndent()

                val words = fallbackScript.split("\\s+".toRegex()).count { it.isNotBlank() }
                val updated = project.copy(
                    fullScript = fallbackScript,
                    tone = tone,
                    status = if (project.status == "Idea") "Scripting" else project.status,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated

                repository.saveScriptOutline(
                    ScriptOutlineEntity(
                        projectId = project.id,
                        videoTitle = project.title,
                        hookText = updated.hook,
                        outlineText = updated.outline,
                        fullNarration = fallbackScript,
                        tone = tone,
                        wordCount = words,
                        estimatedDurationMinutes = words / 150.0,
                        isAiGenerated = false,
                        updatedAt = System.currentTimeMillis()
                    )
                )

                showMessage("Script generated and saved to Room (Demo mode)")
            }
            _isGeneratingScript.value = false
        }
    }

    fun updateProjectDetails(updated: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(updated.copy(updatedAt = System.currentTimeMillis()))
            _selectedProject.value = updated

            val words = updated.fullScript.split("\\s+".toRegex()).count { it.isNotBlank() }
            repository.saveScriptOutline(
                ScriptOutlineEntity(
                    projectId = updated.id,
                    videoTitle = updated.title,
                    hookText = updated.hook,
                    outlineText = updated.outline,
                    fullNarration = updated.fullScript,
                    tone = updated.tone,
                    wordCount = words,
                    estimatedDurationMinutes = words / 150.0,
                    updatedAt = System.currentTimeMillis()
                )
            )

            showMessage("Draft & script saved to Room database")
        }
    }

    fun playNarrationTTS(context: Context, scriptText: String, voice: String = "Kore") {
        viewModelScope.launch {
            _isTtsPlaying.value = true
            _ttsStatus.value = "Generating speech with gemini-3.8-flash-tts..."
            val sampleText = scriptText.take(500).ifBlank { "Welcome to CreatorPilot AI. Let's create your next viral YouTube video." }
            val res = GeminiApiClient.generateAndPlayTTS(context, sampleText, voiceName = voice)
            res.onSuccess { msg ->
                _ttsStatus.value = msg
                showMessage(msg)
            }.onFailure { err ->
                _ttsStatus.value = "TTS: ${err.message ?: "Unable to stream audio"}"
                showMessage("TTS requires an active Gemini API key in Secrets")
            }
            _isTtsPlaying.value = false
        }
    }

    fun stopAudio() {
        GeminiApiClient.stopAudio()
        _isTtsPlaying.value = false
        _ttsStatus.value = "Audio stopped"
    }

    // --- AUDIO TRANSCRIPTION (gemini-3.5-transcribe) ---
    fun startVoiceDictation(context: Context) {
        if (audioRecorderHelper == null) {
            audioRecorderHelper = AudioRecorderHelper(context)
        }
        val started = audioRecorderHelper?.startRecording() ?: false
        if (started) {
            _isRecordingAudio.value = true
            showMessage("Recording voice note... speak your idea or narration")
        } else {
            showMessage("Could not initialize microphone recorder")
        }
    }

    fun stopVoiceDictationAndTranscribe(
        context: Context,
        onTranscribed: (String) -> Unit
    ) {
        val helper = audioRecorderHelper ?: return
        val audioBytes = helper.stopRecording()
        _isRecordingAudio.value = false

        if (audioBytes == null || audioBytes.isEmpty()) {
            showMessage("No voice audio recorded")
            return
        }

        viewModelScope.launch {
            _isTranscribingAudio.value = true
            showMessage("Transcribing voice note with gemini-3.5-transcribe...")
            val result = GeminiApiClient.transcribeAudio(audioBytes, mimeType = "audio/mp4")
            result.onSuccess { transcribedText ->
                onTranscribed(transcribedText)
                showMessage("Audio transcribed with gemini-3.5-transcribe!")
            }.onFailure { err ->
                val sampleTranscription = "Welcome back everyone. In this video, we're breaking down the 3 viral strategies you need to scale your channel in 2026."
                onTranscribed(sampleTranscription)
                showMessage("Audio transcribed (Demo mode)")
            }
            _isTranscribingAudio.value = false
        }
    }

    // --- SCENE & THUMBNAIL STUDIO ---
    fun generateSceneBreakdown(project: ProjectEntity) {
        viewModelScope.launch {
            _isGeneratingScenes.value = true
            val prompt = """
                You are a director and video editor.
                Break down the following YouTube video into 5 compelling visual scenes with exact timing, visual B-roll prompts (ideal for Midjourney / stock / AI generation), camera angles, and sound effects:
                Title: ${project.title}
                Script excerpt: ${project.fullScript.take(600)}
                
                Format each scene as:
                Scene [Number] ([Timestamp]):
                - Visual: [Detailed prompt for B-roll or animation]
                - Camera: [Angle and movement]
                - Audio / SFX: [Music cue, sound design]
            """.trimIndent()

            val result = GeminiApiClient.generateText(prompt, model = GeminiApiClient.MODEL_FLASH)
            result.onSuccess { text ->
                val updated = project.copy(
                    sceneNotes = text,
                    thumbnailPrompt = project.thumbnailPrompt.ifBlank {
                        "Vibrant YouTube thumbnail: Split screen comparison of ${project.title}, bold yellow typography, cinematic studio lighting, 8k render"
                    },
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Scene prompts generated and saved!")
            }.onFailure {
                val fallback = """
                    Scene 1 (0:00 - 0:20):
                    - Visual: Cinematic high-angle shot of creator workspace with glowing monitors displaying green analytics.
                    - Camera: Slow push-in tracking shot.
                    - Audio / SFX: Deep sub-bass riser leading to sudden whoosh cutoff.

                    Scene 2 (0:20 - 1:15):
                    - Visual: High-tech 3D holographic comparison diagram popping up with glowing metrics.
                    - Camera: Dynamic Dutch tilt with subtle parallax shake.
                    - Audio / SFX: Subtle digital data clicks and ambient cyberpunk synth pad.

                    Scene 3 (1:15 - 3:00):
                    - Visual: Fast-paced kinetic typography highlighting 3 critical rules against dark textured studio background.
                    - Camera: Fast punch zoom cuts on every emphasized word.
                    - Audio / SFX: Energetic bass kick and snappy vinyl transition swooshes.
                """.trimIndent()
                val updated = project.copy(
                    sceneNotes = fallback,
                    thumbnailPrompt = "Hyper-realistic YouTube thumbnail for '${project.title}', striking facial expression, glowing neon borders, bold 3D text overlay, 8k resolution",
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Scene prompts generated (Demo mode)")
            }
            _isGeneratingScenes.value = false
        }
    }

    fun generateThumbnailImage(
        project: ProjectEntity,
        prompt: String,
        resolution: String = "2K"
    ) {
        viewModelScope.launch {
            _isGeneratingImage.value = true
            showMessage("Rendering $resolution thumbnail with gemini-3-pro-image-preview...")
            val result = GeminiApiClient.generateImage(prompt, imageSize = resolution, aspectRatio = "16:9")
            result.onSuccess { base64Data ->
                val imageUrl = "data:image/jpeg;base64,$base64Data"
                val updated = project.copy(
                    thumbnailPrompt = prompt,
                    thumbnailResolution = resolution,
                    thumbnailImageUrl = imageUrl,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Thumbnail rendered successfully at $resolution!")
            }.onFailure {
                val updated = project.copy(
                    thumbnailPrompt = prompt,
                    thumbnailResolution = resolution,
                    thumbnailImageUrl = "mock_thumbnail",
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Thumbnail concept saved to Room (Demo mode)")
            }
            _isGeneratingImage.value = false
        }
    }

    fun generateVideoClip(
        project: ProjectEntity,
        prompt: String,
        aspectRatio: String = "16:9"
    ) {
        viewModelScope.launch {
            _isGeneratingVideo.value = true
            _videoGenStatus.value = "Starting gemini-omni-1.1-flash video generation..."
            val result = GeminiApiClient.generateVideo(
                prompt = prompt,
                aspectRatio = aspectRatio,
                duration = "5s",
                onStatusUpdate = { status -> _videoGenStatus.value = status }
            )
            result.onSuccess { videoData ->
                val updated = project.copy(
                    generatedVideoStatus = "Ready",
                    generatedVideoUrl = videoData,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Video synthesis complete!")
            }.onFailure {
                val updated = project.copy(
                    generatedVideoStatus = "Demo Ready (Sample Generated)",
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updated)
                _selectedProject.value = updated
                showMessage("Video simulation generated (Live requires Interactions API)")
            }
            _isGeneratingVideo.value = false
        }
    }

    // --- VIDEO ANALYSIS (Gemini 4 Argon) ---
    fun analyzeVideoContent(project: ProjectEntity) {
        viewModelScope.launch {
            _isAnalyzingVideo.value = true
            val prompt = """
                You are Gemini 4 Argon, the world's most advanced reasoning model specializing in YouTube audience psychology and algorithmic retention.
                Analyze the following video script and concept:
                Title: ${project.title}
                Format: ${project.format}
                Hook: ${project.hook}
                Script: ${project.fullScript.take(1200)}

                Return a JSON object only:
                {
                  "retentionScore": 91,
                  "hookRating": "A (Strong pattern interrupt)",
                  "dropOffPoints": [
                    "0:45 - Concept explanation slows down tempo",
                    "2:10 - Too much technical jargon without visual relief"
                  ],
                  "optimizationTips": [
                    "Add a b-roll cutaway or sound effect at 0:42",
                    "Tease the breakthrough solution right after the hook"
                  ],
                  "highCtrTitles": [
                    "Title Alternative 1 (Curiosity Gap)",
                    "Title Alternative 2 (Fear of Missing Out)",
                    "Title Alternative 3 (Transformation Promise)"
                  ],
                  "tags": ["youtube growth", "content strategy", "creator pilot"]
                }
            """.trimIndent()

            val result = GeminiApiClient.generateText(prompt, model = GeminiApiClient.MODEL_ARGON)
            result.onSuccess { text ->
                try {
                    val cleanJson = extractJsonObject(text)
                    val json = JSONObject(cleanJson)
                    val dropOffArray = json.optJSONArray("dropOffPoints")
                    val tipsArray = json.optJSONArray("optimizationTips")
                    val titlesArray = json.optJSONArray("highCtrTitles")
                    val tagsArray = json.optJSONArray("tags")

                    val analysis = VideoAnalysisResult(
                        retentionScore = json.optInt("retentionScore", 89),
                        hookRating = json.optString("hookRating", "A-"),
                        dropOffPoints = jsonArrayToStringList(dropOffArray),
                        optimizationTips = jsonArrayToStringList(tipsArray),
                        highCtrTitles = jsonArrayToStringList(titlesArray),
                        tags = jsonArrayToStringList(tagsArray)
                    )
                    _videoAnalysisResult.value = analysis

                    val updated = project.copy(
                        retentionScore = analysis.retentionScore,
                        retentionTips = analysis.optimizationTips.joinToString("; "),
                        updatedAt = System.currentTimeMillis()
                    )
                    repository.updateProject(updated)
                    _selectedProject.value = updated
                    showMessage("Analysis complete with Gemini 4 Argon!")
                } catch (e: Exception) {
                    val fallback = getFallbackAnalysis(project)
                    _videoAnalysisResult.value = fallback
                    showMessage("Analyzed video (Demo mode)")
                }
            }.onFailure {
                val fallback = getFallbackAnalysis(project)
                _videoAnalysisResult.value = fallback
                showMessage("Analyzed video (Demo mode)")
            }
            _isAnalyzingVideo.value = false
        }
    }

    // --- CALENDAR & PERSISTENCE ---
    fun updateProjectStatus(project: ProjectEntity, newStatus: String) {
        viewModelScope.launch {
            val updated = project.copy(status = newStatus, updatedAt = System.currentTimeMillis())
            repository.updateProject(updated)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = updated
            }

            // Also synchronize with CalendarEntryEntity
            val existingCalendar = allCalendarEntries.value.firstOrNull { it.projectId == project.id }
            if (existingCalendar != null) {
                repository.updateCalendarEntry(
                    existingCalendar.copy(
                        status = newStatus,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                repository.saveCalendarEntry(
                    CalendarEntryEntity(
                        projectId = project.id,
                        title = project.title,
                        scheduledDate = project.scheduledDate.ifBlank { "2026-10-31" },
                        status = newStatus,
                        niche = project.niche,
                        format = project.format
                    )
                )
            }
            showMessage("Updated status to $newStatus in Room")
        }
    }

    fun updateScheduledDate(project: ProjectEntity, newDate: String) {
        viewModelScope.launch {
            val updated = project.copy(scheduledDate = newDate, updatedAt = System.currentTimeMillis())
            repository.updateProject(updated)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = updated
            }

            // Sync with CalendarEntryEntity
            val existingCalendar = allCalendarEntries.value.firstOrNull { it.projectId == project.id }
            if (existingCalendar != null) {
                repository.updateCalendarEntry(
                    existingCalendar.copy(
                        scheduledDate = newDate,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                repository.saveCalendarEntry(
                    CalendarEntryEntity(
                        projectId = project.id,
                        title = project.title,
                        scheduledDate = newDate,
                        status = project.status,
                        niche = project.niche,
                        format = project.format
                    )
                )
            }
            showMessage("Scheduled for $newDate (Saved to Room)")
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = null
            }
            showMessage("Deleted project: ${project.title}")
        }
    }

    fun generateCalendarCsv(customProjects: List<ProjectEntity>? = null): String {
        val projects = customProjects ?: allProjects.value.ifEmpty {
            listOfNotNull(_selectedProject.value)
        }
        val sb = StringBuilder()
        sb.append("Title,Niche,Format,Status,Scheduled Date,Retention Score\n")
        for (p in projects) {
            val safeTitle = "\"" + p.title.replace("\"", "\"\"") + "\""
            val safeNiche = "\"" + p.niche.replace("\"", "\"\"") + "\""
            sb.append("$safeTitle,$safeNiche,${p.format},${p.status},${p.scheduledDate},${p.retentionScore}\n")
        }
        return sb.toString()
    }

    fun generateProductionNotesExport(project: ProjectEntity): String {
        return """
            ==================================================
            CREATORPILOT AI - VIDEO PRODUCTION BRIEF
            ==================================================
            PROJECT: ${project.title}
            NICHE: ${project.niche} | FORMAT: ${project.format}
            STATUS: ${project.status} | SCHEDULED: ${project.scheduledDate}
            RETENTION SCORE: ${project.retentionScore}/100
            
            --------------------------------------------------
            TARGET AUDIENCE:
            ${project.targetAudience}
            
            --------------------------------------------------
            OPENING HOOK:
            ${project.hook}
            
            --------------------------------------------------
            OUTLINE:
            ${project.outline}
            
            --------------------------------------------------
            FULL SCRIPT / NARRATION:
            ${project.fullScript}
            
            --------------------------------------------------
            SCENE & B-ROLL BREAKDOWN:
            ${project.sceneNotes}
            
            --------------------------------------------------
            THUMBNAIL PROMPT (${project.thumbnailResolution}):
            ${project.thumbnailPrompt}
            
            ==================================================
            Stored in Local Room Database • CreatorPilot AI
        """.trimIndent()
    }

    // Helpers
    private fun extractJsonArray(raw: String): String {
        val start = raw.indexOf('[')
        val end = raw.lastIndexOf(']')
        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start, end + 1)
        }
        return raw
    }

    private fun extractJsonObject(raw: String): String {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start, end + 1)
        }
        return raw
    }

    private fun jsonArrayToStringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.optString(i))
        }
        return list
    }

    private fun getFallbackIdeas(topic: String, niche: String, format: String): List<GeneratedIdea> {
        val subject = topic.ifBlank { "Modern Content Creation" }
        return listOf(
            GeneratedIdea(
                title = "Why Everyone is Wrong About $subject in 2026",
                hook = "Almost every tutorial on $subject tells you to start with the same beginner mistake. Here is the contrarian strategy that actually gets traction.",
                targetAudience = "Curious viewers and aspiring practitioners in $niche",
                angle = "Debunking common myths with verified data",
                estimatedViewsScore = 94
            ),
            GeneratedIdea(
                title = "I Tested $subject for 30 Days (Real Results)",
                hook = "Can you actually master $subject in 30 days? I documented every single hour, expense, and metric so you don't waste your time.",
                targetAudience = "Viewers looking for unfiltered personal case studies",
                angle = "Transparent experiment with high credibility",
                estimatedViewsScore = 91
            ),
            GeneratedIdea(
                title = "The $subject Playbook: Zero to 100k",
                hook = "If I had to build a presence in $niche from scratch today with zero dollars, this is the exact step-by-step roadmap I would execute.",
                targetAudience = "Builders, creators, and professionals in $niche",
                angle = "Comprehensive actionable blueprint",
                estimatedViewsScore = 87
            ),
            GeneratedIdea(
                title = "5 Warning Signs You're Doing $subject Wrong",
                hook = "If your retention drops after the 45-second mark, you're making at least one of these 5 catastrophic mistakes.",
                targetAudience = "Creators and hobbyists experiencing stagnation",
                angle = "Diagnostic and immediate solution",
                estimatedViewsScore = 89
            )
        )
    }

    private fun getFallbackAnalysis(project: ProjectEntity): VideoAnalysisResult {
        return VideoAnalysisResult(
            retentionScore = 91,
            hookRating = "A (High Curiosity & Fast Pacing)",
            dropOffPoints = listOf(
                "0:42 - Slight drop during theoretical explanation",
                "2:15 - Audience may want an on-screen visual demo sooner",
                "4:10 - Tease the ending payoff to prevent premature click-away"
            ),
            optimizationTips = listOf(
                "Insert kinetic typography and visual b-roll at 0:38",
                "Shorten intro background context by 10 seconds",
                "Add an open loop at 1:45: 'And in a moment, I'll reveal why...'"
            ),
            highCtrTitles = listOf(
                "Why 99% Fail at ${project.title.take(30)}",
                "The Brutal Truth About ${project.niche} in 2026",
                "I Regret Not Knowing This Sooner (${project.title.take(25)})"
            ),
            tags = listOf(project.niche.lowercase(), "youtube algorithm", "viral hooks", "creator pilot")
        )
    }
}

class CreatorPilotViewModelFactory(
    private val repository: ProjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatorPilotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatorPilotViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
