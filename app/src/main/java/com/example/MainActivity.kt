package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CreatorPilotDatabase
import com.example.data.local.PaymentTransactionEntity
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.ProjectRepository
import com.example.ui.components.AppTopBar
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.SubscriptionCheckoutDialog
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IdeaLabScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.SceneStudioScreen
import com.example.ui.screens.ScriptStudioScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.SubscriptionScreenContent
import com.example.ui.screens.VideoAnalysisScreen
import com.example.ui.theme.CreatorPilotTheme
import com.example.ui.theme.CreatorRed
import com.example.ui.viewmodel.CreatorPilotViewModel
import com.example.ui.viewmodel.CreatorPilotViewModelFactory
import com.example.ui.viewmodel.PaymentViewModel
import com.example.ui.viewmodel.PaymentViewModelFactory
import com.example.ui.viewmodel.PaymentCheckoutUiState

enum class AppScreen {
    Ideas,
    Scripts,
    Calendar,
    Home,
    IdeaLab,
    ScriptStudio,
    SceneStudio,
    VideoAnalysis,
    Pricing,
    Subscription
}

class MainActivity : ComponentActivity() {

    private val viewModel: CreatorPilotViewModel by viewModels {
        val database = CreatorPilotDatabase.getDatabase(applicationContext, (this as androidx.lifecycle.LifecycleOwner).lifecycleScopeInternal())
        val repository = ProjectRepository(
            database.projectDao(),
            database.scriptOutlineDao(),
            database.calendarEntryDao(),
            database.paymentDao(),
            database.subscriptionDao()
        )
        CreatorPilotViewModelFactory(repository)
    }

    private val paymentViewModel: PaymentViewModel by viewModels {
        val database = CreatorPilotDatabase.getDatabase(applicationContext, (this as androidx.lifecycle.LifecycleOwner).lifecycleScopeInternal())
        val repository = ProjectRepository(
            database.projectDao(),
            database.scriptOutlineDao(),
            database.calendarEntryDao(),
            database.paymentDao(),
            database.subscriptionDao()
        )
        PaymentViewModelFactory(repository)
    }

    private fun androidx.lifecycle.LifecycleOwner.lifecycleScopeInternal() =
        (this as ComponentActivity).lifecycle.let {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CreatorPilotTheme(darkTheme = true) {
                CreatorPilotApp(viewModel = viewModel, paymentViewModel = paymentViewModel)
            }
        }
    }
}

@Composable
fun CreatorPilotApp(
    viewModel: CreatorPilotViewModel,
    paymentViewModel: PaymentViewModel? = null
) {
    val context = LocalContext.current
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.Ideas) }

    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()
    val generatedIdeas by viewModel.generatedIdeas.collectAsStateWithLifecycle()
    val isGeneratingIdeas by viewModel.isGeneratingIdeas.collectAsStateWithLifecycle()
    val isGeneratingScript by viewModel.isGeneratingScript.collectAsStateWithLifecycle()
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()
    val ttsStatus by viewModel.ttsStatus.collectAsStateWithLifecycle()
    val isGeneratingScenes by viewModel.isGeneratingScenes.collectAsStateWithLifecycle()
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsStateWithLifecycle()
    val isGeneratingVideo by viewModel.isGeneratingVideo.collectAsStateWithLifecycle()
    val videoGenStatus by viewModel.videoGenStatus.collectAsStateWithLifecycle()
    val isAnalyzingVideo by viewModel.isAnalyzingVideo.collectAsStateWithLifecycle()
    val videoAnalysisResult by viewModel.videoAnalysisResult.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val isTranscribingAudio by viewModel.isTranscribingAudio.collectAsStateWithLifecycle()
    val currentPlan by viewModel.currentPlan.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    // PaymentViewModel state & flows
    val paymentCheckoutState by (paymentViewModel?.checkoutUiState ?: kotlinx.coroutines.flow.MutableStateFlow(PaymentCheckoutUiState.Idle)).collectAsStateWithLifecycle()
    val paymentActiveSession by (paymentViewModel?.activeSession ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()
    val paymentLastReceipt by (paymentViewModel?.lastVerifiedReceipt ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()
    val paymentFeedbackMsg by (paymentViewModel?.userFeedbackMessage ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()
    val paymentDiagReport by (paymentViewModel?.diagnosticReport ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()
    val paymentIsProcessing by (paymentViewModel?.isProcessing ?: kotlinx.coroutines.flow.MutableStateFlow(false)).collectAsStateWithLifecycle()

    val allTransactions by (paymentViewModel?.transactionHistory ?: viewModel.allTransactions).collectAsStateWithLifecycle()
    val currentSubscription by (paymentViewModel?.currentSubscription ?: viewModel.currentSubscription).collectAsStateWithLifecycle()
    val activeCheckoutPlan by viewModel.activeCheckoutPlan.collectAsStateWithLifecycle()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsStateWithLifecycle()
    val lastVerifiedReceipt by viewModel.lastVerifiedReceipt.collectAsStateWithLifecycle()
    val checkoutError by viewModel.checkoutError.collectAsStateWithLifecycle()
    val diagnosticReport by viewModel.diagnosticReport.collectAsStateWithLifecycle()
    val simulationFeedback by viewModel.simulationFeedback.collectAsStateWithLifecycle()
    var inspectedReceipt by remember { mutableStateOf<PaymentTransactionEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Toast or Snackbar notifications
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    LaunchedEffect(paymentFeedbackMsg) {
        paymentFeedbackMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            paymentViewModel?.clearUserFeedback()
        }
    }

    // Handle Android system back gesture to return to Ideas tab
    if (currentScreen != AppScreen.Ideas) {
        BackHandler {
            currentScreen = AppScreen.Ideas
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen != AppScreen.Pricing && currentScreen != AppScreen.Subscription) {
                AppTopBar(
                    activeProjectTitle = selectedProject?.title,
                    hasApiKey = GeminiApiClient.hasApiKey(),
                    planLabel = "${currentPlan.name} $${currentPlan.monthlyPrice}",
                    onPricingClick = { currentScreen = AppScreen.Subscription }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.Ideas || currentScreen == AppScreen.IdeaLab || currentScreen == AppScreen.Home,
                    onClick = { currentScreen = AppScreen.Ideas },
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = "Ideas") },
                    label = { Text("Ideas", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_ideas"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CreatorRed,
                        selectedTextColor = CreatorRed,
                        indicatorColor = CreatorRed.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.Scripts || currentScreen == AppScreen.ScriptStudio,
                    onClick = { currentScreen = AppScreen.Scripts },
                    icon = { Icon(Icons.Default.Description, contentDescription = "Scripts") },
                    label = { Text("Scripts", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_scripts"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CreatorRed,
                        selectedTextColor = CreatorRed,
                        indicatorColor = CreatorRed.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.Calendar,
                    onClick = { currentScreen = AppScreen.Calendar },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                    label = { Text("Calendar", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_calendar"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CreatorRed,
                        selectedTextColor = CreatorRed,
                        indicatorColor = CreatorRed.copy(alpha = 0.15f)
                    )
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                AppScreen.Ideas, AppScreen.IdeaLab -> {
                    var ideasSubTab by rememberSaveable { mutableStateOf(0) }
                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            selectedTabIndex = ideasSubTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = CreatorRed,
                            modifier = Modifier.testTag("ideas_tab_row")
                        ) {
                            Tab(
                                selected = ideasSubTab == 0,
                                onClick = { ideasSubTab = 0 },
                                text = { Text("Idea Generator", fontWeight = FontWeight.SemiBold) },
                                icon = { Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("tab_idea_generator")
                            )
                            Tab(
                                selected = ideasSubTab == 1,
                                onClick = { ideasSubTab = 1 },
                                text = { Text("Video Drafts (${projects.size})", fontWeight = FontWeight.SemiBold) },
                                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                modifier = Modifier.testTag("tab_saved_drafts")
                            )
                        }

                        if (ideasSubTab == 0) {
                            IdeaLabScreen(
                                generatedIdeas = generatedIdeas,
                                isGenerating = isGeneratingIdeas,
                                onGenerateIdeas = { topic, niche, format, model ->
                                    viewModel.generateIdeas(topic, niche, format, model)
                                },
                                onCreateProject = { idea, niche, format ->
                                    viewModel.createProjectFromIdea(idea, niche, format)
                                },
                                onSendToScriptStudio = { idea, niche, format ->
                                    viewModel.createProjectFromIdea(idea, niche, format)
                                    currentScreen = AppScreen.Scripts
                                }
                            )
                        } else {
                            HomeScreen(
                                projects = projects,
                                selectedProject = selectedProject,
                                onSelectProject = { p -> viewModel.selectProject(p) },
                                onDeleteProject = { p -> viewModel.deleteProject(p) },
                                onNavigateToIdeaLab = { ideasSubTab = 0 },
                                onNavigateToScriptStudio = { currentScreen = AppScreen.Scripts },
                                onNavigateToSceneStudio = { currentScreen = AppScreen.SceneStudio },
                                onNavigateToCalendar = { currentScreen = AppScreen.Calendar },
                                onNavigateToArgonAnalyzer = { currentScreen = AppScreen.VideoAnalysis },
                                onNavigateToPricing = { currentScreen = AppScreen.Pricing },
                                onCreateNewProject = { title, niche, format ->
                                    viewModel.createProjectFromIdea(
                                        com.example.ui.viewmodel.GeneratedIdea(
                                            title = title,
                                            hook = "",
                                            targetAudience = "Target viewers in $niche",
                                            angle = "Core thesis",
                                            estimatedViewsScore = 88
                                        ),
                                        niche = niche,
                                        format = format
                                    )
                                }
                            )
                        }
                    }
                }

                AppScreen.Scripts, AppScreen.ScriptStudio -> {
                    ScriptStudioScreen(
                        projects = projects,
                        selectedProject = selectedProject,
                        isGeneratingScript = isGeneratingScript,
                        isTtsPlaying = isTtsPlaying,
                        ttsStatus = ttsStatus,
                        isRecordingAudio = isRecordingAudio,
                        isTranscribingAudio = isTranscribingAudio,
                        onSelectProject = { p -> viewModel.selectProject(p) },
                        onGenerateScript = { p, tone, notes ->
                            viewModel.generateScriptForProject(p, tone, notes)
                        },
                        onSaveProject = { p -> viewModel.updateProjectDetails(p) },
                        onPlayTTS = { text, voice ->
                            viewModel.playNarrationTTS(context, text, voice)
                        },
                        onStopAudio = { viewModel.stopAudio() },
                        onStartVoiceDictation = { viewModel.startVoiceDictation(context) },
                        onStopVoiceDictation = { callback -> viewModel.stopVoiceDictationAndTranscribe(context, callback) },
                        onProceedToScenes = { currentScreen = AppScreen.SceneStudio }
                    )
                }
                AppScreen.Home -> {
                    HomeScreen(
                        projects = projects,
                        selectedProject = selectedProject,
                        onSelectProject = { p -> viewModel.selectProject(p) },
                        onDeleteProject = { p -> viewModel.deleteProject(p) },
                        onNavigateToIdeaLab = { currentScreen = AppScreen.IdeaLab },
                        onNavigateToScriptStudio = { currentScreen = AppScreen.ScriptStudio },
                        onNavigateToSceneStudio = { currentScreen = AppScreen.SceneStudio },
                        onNavigateToCalendar = { currentScreen = AppScreen.Calendar },
                        onNavigateToArgonAnalyzer = { currentScreen = AppScreen.VideoAnalysis },
                        onNavigateToPricing = { currentScreen = AppScreen.Pricing },
                        onCreateNewProject = { title, niche, format ->
                            viewModel.createProjectFromIdea(
                                com.example.ui.viewmodel.GeneratedIdea(
                                    title = title,
                                    hook = "",
                                    targetAudience = "Target viewers in $niche",
                                    angle = "Core thesis",
                                    estimatedViewsScore = 88
                                ),
                                niche = niche,
                                format = format
                            )
                        }
                    )
                }

                AppScreen.SceneStudio -> {
                    SceneStudioScreen(
                        projects = projects,
                        selectedProject = selectedProject,
                        isGeneratingScenes = isGeneratingScenes,
                        isGeneratingImage = isGeneratingImage,
                        isGeneratingVideo = isGeneratingVideo,
                        videoGenStatus = videoGenStatus,
                        onGenerateScenes = { p -> viewModel.generateSceneBreakdown(p) },
                        onGenerateThumbnail = { p, prompt, res ->
                            viewModel.generateThumbnailImage(p, prompt, res)
                        },
                        onGenerateVideo = { p, prompt, aspect ->
                            viewModel.generateVideoClip(p, prompt, aspect)
                        },
                        onUpdateProject = { p -> viewModel.updateProjectDetails(p) },
                        onNavigateToCalendar = { currentScreen = AppScreen.Calendar }
                    )
                }

                AppScreen.Calendar -> {
                    CalendarScreen(
                        projects = projects,
                        onUpdateStatus = { p, st -> viewModel.updateProjectStatus(p, st) },
                        onUpdateScheduledDate = { p, date -> viewModel.updateScheduledDate(p, date) },
                        onGenerateCsv = { viewModel.generateCalendarCsv() },
                        onGenerateProductionExport = { p -> viewModel.generateProductionNotesExport(p) }
                    )
                }

                AppScreen.VideoAnalysis -> {
                    VideoAnalysisScreen(
                        projects = projects,
                        selectedProject = selectedProject,
                        isAnalyzing = isAnalyzingVideo,
                        analysisResult = videoAnalysisResult,
                        onAnalyzeProject = { p -> viewModel.analyzeVideoContent(p) },
                        onSelectProject = { p -> viewModel.selectProject(p) }
                    )
                }

                AppScreen.Pricing -> {
                    PricingScreen(
                        currentPlan = currentPlan,
                        currentSubscription = currentSubscription,
                        transactions = allTransactions,
                        onSelectPlan = { tier, isAnnual ->
                            if (paymentViewModel != null) {
                                paymentViewModel.initializePaymentSession(tier, isAnnual)
                            } else {
                                viewModel.initiateCheckout(tier, isAnnual)
                            }
                        },
                        onCancelSubscription = {
                            paymentViewModel?.cancelSubscription() ?: viewModel.cancelSubscription()
                        },
                        onReactivateSubscription = {
                            paymentViewModel?.reactivateSubscription() ?: viewModel.reactivateSubscription()
                        },
                        onViewReceipt = { tx -> inspectedReceipt = tx },
                        onBack = { currentScreen = AppScreen.Ideas },
                        isProcessingPayment = paymentIsProcessing || isProcessingPayment,
                        diagnosticReport = paymentDiagReport ?: diagnosticReport,
                        simulationFeedback = paymentFeedbackMsg ?: simulationFeedback,
                        onRunDiagnostics = {
                            paymentViewModel?.runDiagnostics() ?: viewModel.runPaymentDiagnostics()
                        },
                        onTestFraudCheck = {
                            paymentViewModel?.simulateTamperedSignaturePayment() ?: viewModel.testFraudRejection()
                        },
                        onTestDuplicateCheck = {
                            paymentViewModel?.simulateDuplicatePaymentRejection() ?: viewModel.testDuplicateRejection()
                        },
                        onClearSimulationFeedback = {
                            paymentViewModel?.clearUserFeedback() ?: viewModel.clearSimulationFeedback()
                        }
                    )
                }

                AppScreen.Subscription -> {
                    if (paymentViewModel != null) {
                        SubscriptionScreen(
                            paymentViewModel = paymentViewModel,
                            onBack = { currentScreen = AppScreen.Ideas },
                            showDialogs = false
                        )
                    } else {
                        SubscriptionScreenContent(
                            currentSubscription = currentSubscription,
                            checkoutUiState = paymentCheckoutState,
                            isProcessing = isProcessingPayment,
                            onSelectPlan = { tier, isAnnual -> viewModel.initiateCheckout(tier, isAnnual) },
                            onCancelSubscription = { viewModel.cancelSubscription() },
                            onReactivateSubscription = { viewModel.reactivateSubscription() },
                            onBack = { currentScreen = AppScreen.Ideas }
                        )
                    }
                }
            }

            // Checkout Dialog Modal (driven by PaymentViewModel activeSession or fallback to CreatorPilotViewModel)
            if (paymentActiveSession != null) {
                val session = paymentActiveSession!!
                val isVerifying = paymentCheckoutState is PaymentCheckoutUiState.Verifying
                val failedError = (paymentCheckoutState as? PaymentCheckoutUiState.Failed)?.errorMessage
                SubscriptionCheckoutDialog(
                    plan = session.plan,
                    isAnnual = session.isAnnual,
                    isProcessing = isVerifying || paymentIsProcessing,
                    errorMessage = failedError,
                    onDismiss = { paymentViewModel?.cancelCheckoutSession() },
                    onConfirmPayment = { method, email ->
                        paymentViewModel?.simulateSuccessfulPayment(method, email)
                    }
                )
            } else {
                activeCheckoutPlan?.let { (plan, isAnnual) ->
                    SubscriptionCheckoutDialog(
                        plan = plan,
                        isAnnual = isAnnual,
                        isProcessing = isProcessingPayment,
                        errorMessage = checkoutError,
                        onDismiss = { viewModel.dismissCheckout() },
                        onConfirmPayment = { method, email ->
                            viewModel.processCheckoutPayment(plan, isAnnual, method, email)
                        }
                    )
                }
            }

            // Fresh Payment Receipt Modal
            val activeReceipt = paymentLastReceipt ?: lastVerifiedReceipt
            activeReceipt?.let { tx ->
                PaymentReceiptDialog(
                    transaction = tx,
                    onDismiss = {
                        paymentViewModel?.dismissReceiptModal()
                        viewModel.dismissReceiptModal()
                    }
                )
            }

            // Historical Inspected Receipt Modal
            inspectedReceipt?.let { tx ->
                PaymentReceiptDialog(
                    transaction = tx,
                    onDismiss = { inspectedReceipt = null }
                )
            }
        }
    }
}
