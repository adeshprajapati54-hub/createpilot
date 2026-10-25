package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        ScriptOutlineEntity::class,
        CalendarEntryEntity::class,
        PaymentTransactionEntity::class,
        SubscriptionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CreatorPilotDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun scriptOutlineDao(): ScriptOutlineDao
    abstract fun calendarEntryDao(): CalendarEntryDao
    abstract fun paymentDao(): PaymentDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorPilotDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CreatorPilotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreatorPilotDatabase::class.java,
                    "creator_pilot_database"
                )
                    .addCallback(CreatorPilotDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class CreatorPilotDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(
                            database.projectDao(),
                            database.scriptOutlineDao(),
                            database.calendarEntryDao(),
                            database.paymentDao(),
                            database.subscriptionDao()
                        )
                    }
                }
            }
        }

        suspend fun populateInitialData(
            projectDao: ProjectDao,
            outlineDao: ScriptOutlineDao,
            calendarDao: CalendarEntryDao,
            paymentDao: PaymentDao? = null,
            subscriptionDao: SubscriptionDao? = null
        ) {
            val starterProjects = listOf(
                ProjectEntity(
                    id = 1,
                    title = "How I Built a 6-Figure Faceless Channel in 90 Days",
                    niche = "Faceless Documentary",
                    format = "Long-form (8-15 min)",
                    status = "Scripting",
                    targetAudience = "Aspiring content creators, side-hustlers, digital entrepreneurs",
                    hook = "99% of faceless channels fail within 30 days because they copy the wrong niches. Here is the exact 4-step framework I used to reach 100k subscribers without showing my face once.",
                    outline = "1. The 3 Dead Niches to Avoid in 2026\n2. The Golden Ratio of Script Retention (60s Hook, Rapid Proof, Micro-payoffs)\n3. Automated B-Roll Workflow using AI generators\n4. YouTube Algorithm Secrets: How CTR & AVD actually talk to each other\n5. Checklist to launch your first video tomorrow",
                    fullScript = "[0:00 - 0:35] HOOK: Look at this channel analytics screenshot. 420,000 views in 90 days. Zero face-cams. Zero expensive gear. But here's what the gurus won't tell you: standard Reddit reposts and AI voice clones are dead. Today, I'm peeling back the entire production pipeline that generated 12,000 in ad revenue.\n\n[0:35 - 2:45] CHAPTER 1: Finding High-RPM Micro Niches. We don't do general history. We do 'High-Stakes Corporate Heists'. Notice the difference?\n\n[2:45 - 6:00] CHAPTER 2: The 5-Second Visual Rule. Every 4.5 seconds, the viewer's brain needs a pattern disruption: kinetic typography, subtle zoom, sound woosh.",
                    tone = "Analytical & Inspiring",
                    sceneNotes = "Scene 1 (0:00-0:15): Dark studio lighting, glowing YouTube studio dashboard graphic. SFX: Low sub-bass drop.\nScene 2 (0:15-0:35): Split screen comparison: Failed bot channel vs High retention channel.\nScene 3 (0:35-1:20): Fast-paced kinetic typography showing RPM stats ($18.50 RPM).",
                    thumbnailPrompt = "Hyper-realistic high-contrast YouTube thumbnail: Glowing faceless silhouette wearing a dark hoodie in front of multi-screen trading monitors displaying green YouTube analytics charts, bold red text overlay 'NEVER SHOW YOUR FACE', 8k octane render",
                    thumbnailResolution = "2K",
                    scheduledDate = "2026-10-28",
                    retentionScore = 92,
                    retentionTips = "Strong opening pattern interrupt. Add sound swoosh at 0:18."
                ),
                ProjectEntity(
                    id = 2,
                    title = "The 10-Minute Daily Deep Work Routine for Creators",
                    niche = "Lifestyle & Productivity",
                    format = "Long-form (8-15 min)",
                    status = "Ready",
                    targetAudience = "Solo entrepreneurs, programmers, creators experiencing burnout",
                    hook = "I tried every productivity hack for 5 years until neuroscientists proved that 90% of our daily focus is ruined before 8:15 AM. Here is the simple 10-minute ritual that changed everything.",
                    outline = "1. The Cortisol Awakening Spike (Why checking notifications destroys creative flow)\n2. The 10-Minute Buffer Protocol\n3. The Analog Notepad Rule\n4. Batching Scripting vs Editing Days\n5. Final Challenge",
                    fullScript = "Before you pick up your phone tomorrow morning, stop. The moment light hits your eyes, your dopamine baseline is established. In this video, I'll walk you through the single 10-minute ritual that doubled my video output while cutting my working hours in half.",
                    tone = "Calm, Authoritative & Empathetic",
                    sceneNotes = "Scene 1: Aesthetic morning coffee pour, cinematic 24fps macro lens. Lo-fi ambient music.\nScene 2: Time-lapse of desk setup transitioning from cluttered to minimalist.",
                    thumbnailPrompt = "Aesthetic split thumbnail: Left side chaotic red notifications phone screen, Right side peaceful glowing minimalist creator desk with coffee and notebook, bold yellow text '10 MINUTE RESET'",
                    thumbnailResolution = "4K",
                    scheduledDate = "2026-10-25",
                    retentionScore = 88,
                    retentionTips = "Hook is very relatable. Keep Chapter 2 concise."
                ),
                ProjectEntity(
                    id = 3,
                    title = "Why Modern AI Coding Assistants Feel Like Magic",
                    niche = "Tech & AI",
                    format = "Shorts (60s)",
                    status = "Editing",
                    targetAudience = "Software engineers, tech lovers, AI enthusiasts",
                    hook = "Did you know software engineers in 2026 write 70% less boilerplate code than just 2 years ago? Here's the secret architecture behind autonomous coding agents.",
                    outline = "1. Evolution from auto-complete to autonomous agents\n2. Multi-turn reasoning benchmarks\n3. What this means for junior developers",
                    fullScript = "Stop writing unit tests by hand. Modern coding agents don't just guess the next word; they simulate terminal environments, compile test suites, and fix runtime errors before you even hit save.",
                    tone = "Energetic & Fast-paced",
                    sceneNotes = "Vertical 9:16 layout. Dynamic IDE screen recording with highlighted code blocks popping in rhythm with beat.",
                    thumbnailPrompt = "Vertical YouTube Shorts cover: Glowing neon robot hand writing sleek Kotlin code on floating glass monitor, vibrant cyber red and electric violet theme",
                    thumbnailResolution = "1K",
                    scheduledDate = "2026-10-24",
                    retentionScore = 95,
                    retentionTips = "Shorts retention needs immediate hook in first 1.5 seconds."
                )
            )
            projectDao.insertAll(starterProjects)

            val starterOutlines = listOf(
                ScriptOutlineEntity(
                    id = 1,
                    projectId = 1,
                    videoTitle = "How I Built a 6-Figure Faceless Channel in 90 Days",
                    hookText = "99% of faceless channels fail within 30 days because they copy the wrong niches.",
                    outlineText = "1. The 3 Dead Niches to Avoid in 2026\n2. Golden Ratio of Script Retention\n3. Automated B-Roll Workflow\n4. YouTube Algorithm Retention Multipliers\n5. 24h Launch Checklist",
                    fullNarration = starterProjects[0].fullScript,
                    tone = "Analytical & Inspiring",
                    wordCount = 145,
                    estimatedDurationMinutes = 1.0,
                    isAiGenerated = true
                ),
                ScriptOutlineEntity(
                    id = 2,
                    projectId = 2,
                    videoTitle = "The 10-Minute Daily Deep Work Routine for Creators",
                    hookText = "I tried every productivity hack for 5 years until neuroscientists proved that 90% of our daily focus is ruined before 8:15 AM.",
                    outlineText = "1. Cortisol Awakening Spike\n2. 10-Minute Buffer Protocol\n3. Analog Notepad Rule\n4. Batching Scripting vs Editing Days\n5. Final Challenge",
                    fullNarration = starterProjects[1].fullScript,
                    tone = "Calm, Authoritative & Empathetic",
                    wordCount = 98,
                    estimatedDurationMinutes = 0.7,
                    isAiGenerated = true
                ),
                ScriptOutlineEntity(
                    id = 3,
                    projectId = 3,
                    videoTitle = "Why Modern AI Coding Assistants Feel Like Magic",
                    hookText = "Did you know software engineers in 2026 write 70% less boilerplate code than just 2 years ago?",
                    outlineText = "1. Evolution to autonomous agents\n2. Multi-turn reasoning\n3. Junior developer impact",
                    fullNarration = starterProjects[2].fullScript,
                    tone = "Energetic & Fast-paced",
                    wordCount = 52,
                    estimatedDurationMinutes = 0.4,
                    isAiGenerated = true
                )
            )
            outlineDao.insertAll(starterOutlines)

            val starterCalendarEntries = listOf(
                CalendarEntryEntity(
                    id = 1,
                    projectId = 3,
                    title = "Why Modern AI Coding Assistants Feel Like Magic",
                    scheduledDate = "2026-10-24",
                    status = "Editing",
                    niche = "Tech & AI",
                    format = "Shorts (60s)",
                    productionNotes = "Add fast subtitle animations and audio riser.",
                    reminderEnabled = true
                ),
                CalendarEntryEntity(
                    id = 2,
                    projectId = 2,
                    title = "The 10-Minute Daily Deep Work Routine for Creators",
                    scheduledDate = "2026-10-25",
                    status = "Ready",
                    niche = "Lifestyle & Productivity",
                    format = "Long-form (8-15 min)",
                    productionNotes = "Final thumbnail 4K export verified. Ready for upload.",
                    reminderEnabled = true
                ),
                CalendarEntryEntity(
                    id = 3,
                    projectId = 1,
                    title = "How I Built a 6-Figure Faceless Channel in 90 Days",
                    scheduledDate = "2026-10-28",
                    status = "Scripting",
                    niche = "Faceless Documentary",
                    format = "Long-form (8-15 min)",
                    productionNotes = "Record Voiceover with Kore or Fenrir TTS.",
                    reminderEnabled = false
                )
            )
            calendarDao.insertAll(starterCalendarEntries)

            subscriptionDao?.upsertSubscription(
                SubscriptionEntity(
                    userId = "creator_pilot_user",
                    planTier = "FREE",
                    status = "ACTIVE",
                    amountUsd = 0.0,
                    billingInterval = "monthly",
                    startDate = "2026-10-24",
                    nextBillingDate = "2026-11-23",
                    autoRenew = false,
                    lastTransactionId = "pay_init_free_welcome",
                    lastOrderId = "order_cp_welcome",
                    aiActionsTotal = 10,
                    aiActionsUsed = 0,
                    scriptsAllowed = 2,
                    scenePromptsAllowed = 10,
                    activeProjectsLimit = 3
                )
            )

            paymentDao?.insertTransaction(
                PaymentTransactionEntity(
                    transactionId = "pay_init_free_welcome",
                    orderId = "order_cp_welcome",
                    planTier = "FREE",
                    amountUsd = 0.0,
                    currency = "USD",
                    billingInterval = "monthly",
                    paymentMethod = "Complimentary Tier",
                    status = "CAPTURED",
                    signatureVerified = true,
                    signature = "simulated_genesis_signature",
                    billingDate = "2026-10-24",
                    nextBillingDate = "2026-11-23",
                    customerEmail = "creator@creatorpilot.ai",
                    receiptNumber = "RCP-2026-0001",
                    gatewayProvider = "Razorpay / Stripe Live Sandbox",
                    notes = "Welcome Genesis Tier for CreatorPilot AI workspace"
                )
            )
        }
    }
}
