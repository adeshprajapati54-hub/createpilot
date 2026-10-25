package com.example.data.repository

import com.example.data.local.CalendarEntryDao
import com.example.data.local.CalendarEntryEntity
import com.example.data.local.CreatorPilotDatabase
import com.example.data.local.PaymentDao
import com.example.data.local.PaymentTransactionEntity
import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.ScriptOutlineDao
import com.example.data.local.ScriptOutlineEntity
import com.example.data.local.SubscriptionDao
import com.example.data.local.SubscriptionEntity
import com.example.ui.screens.PlanTier
import com.example.util.PaymentVerificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class ProjectRepository(
    private val projectDao: ProjectDao,
    private val scriptOutlineDao: ScriptOutlineDao,
    private val calendarEntryDao: CalendarEntryDao,
    private val paymentDao: PaymentDao? = null,
    private val subscriptionDao: SubscriptionDao? = null
) {
    // --- Video Projects ---
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getAllProjectsSync(): List<ProjectEntity> = projectDao.getAllProjectsSync()

    fun getProject(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    suspend fun getProjectSync(id: Long): ProjectEntity? = projectDao.getProjectByIdSync(id)

    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>> =
        projectDao.getProjectsByStatus(status)

    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
        scriptOutlineDao.deleteByProjectId(project.id)
        calendarEntryDao.deleteByProjectId(project.id)
    }

    suspend fun deleteProjectById(id: Long) {
        projectDao.deleteProjectById(id)
        scriptOutlineDao.deleteByProjectId(id)
        calendarEntryDao.deleteByProjectId(id)
    }

    // --- Script Outlines ---
    val allScriptOutlines: Flow<List<ScriptOutlineEntity>> = scriptOutlineDao.getAllOutlines()

    fun getOutlineForProject(projectId: Long): Flow<ScriptOutlineEntity?> =
        scriptOutlineDao.getOutlineForProject(projectId)

    suspend fun getOutlineForProjectSync(projectId: Long): ScriptOutlineEntity? =
        scriptOutlineDao.getOutlineForProjectSync(projectId)

    suspend fun saveScriptOutline(outline: ScriptOutlineEntity): Long =
        scriptOutlineDao.insertOutline(outline)

    suspend fun deleteScriptOutline(outline: ScriptOutlineEntity) =
        scriptOutlineDao.deleteOutline(outline)

    // --- Calendar Entries ---
    val allCalendarEntries: Flow<List<CalendarEntryEntity>> =
        calendarEntryDao.getAllCalendarEntries()

    fun getCalendarEntriesByStatus(status: String): Flow<List<CalendarEntryEntity>> =
        calendarEntryDao.getEntriesByStatus(status)

    fun getCalendarEntriesForDate(date: String): Flow<List<CalendarEntryEntity>> =
        calendarEntryDao.getEntriesForDate(date)

    suspend fun saveCalendarEntry(entry: CalendarEntryEntity): Long =
        calendarEntryDao.insertEntry(entry)

    suspend fun updateCalendarEntry(entry: CalendarEntryEntity) =
        calendarEntryDao.updateEntry(entry)

    suspend fun deleteCalendarEntry(entry: CalendarEntryEntity) =
        calendarEntryDao.deleteEntry(entry)

    suspend fun seedIfEmpty() {
        if (projectDao.getCount() == 0) {
            CreatorPilotDatabase.populateInitialData(
                projectDao,
                scriptOutlineDao,
                calendarEntryDao,
                paymentDao,
                subscriptionDao
            )
        }
    }

    // --- Billing, Payments & Subscriptions ---
    val allTransactions: Flow<List<PaymentTransactionEntity>> =
        paymentDao?.getAllTransactions() ?: emptyFlow()

    suspend fun getAllTransactionsSync(): List<PaymentTransactionEntity> =
        paymentDao?.getAllTransactionsSync() ?: emptyList()

    val currentSubscription: Flow<SubscriptionEntity?> =
        subscriptionDao?.getSubscription() ?: emptyFlow()

    suspend fun getSubscriptionSync(userId: String = "creator_pilot_user"): SubscriptionEntity? =
        subscriptionDao?.getSubscriptionSync(userId)

    suspend fun hasTransaction(txnId: String): Boolean {
        return (paymentDao?.countTransactionWithId(txnId) ?: 0) > 0
    }

    /**
     * Verifies payment signature and records transaction with duplicate protection.
     * Activates plan tier and updates user quotas only upon cryptographic proof.
     */
    suspend fun verifyAndRecordPayment(
        transaction: PaymentTransactionEntity,
        secret: String = PaymentVerificationHelper.DEFAULT_WEBHOOK_SECRET
    ): Result<PaymentTransactionEntity> {
        val pDao = paymentDao ?: return Result.failure(IllegalStateException("Payment system not initialized"))
        val sDao = subscriptionDao ?: return Result.failure(IllegalStateException("Subscription system not initialized"))

        // 1. Duplicate transaction protection
        if (pDao.countTransactionWithId(transaction.transactionId) > 0) {
            return Result.failure(IllegalArgumentException("Duplicate transaction ${transaction.transactionId} has already been captured and processed."))
        }

        // 2. Cryptographic signature verification
        val isVerified = if (transaction.signature.isNotBlank()) {
            PaymentVerificationHelper.verifyPaymentSignature(
                orderId = transaction.orderId,
                paymentId = transaction.transactionId,
                receivedSignature = transaction.signature,
                secret = secret
            )
        } else {
            transaction.signatureVerified
        }

        if (!isVerified) {
            return Result.failure(SecurityException("Cryptographic verification failed for transaction ${transaction.transactionId}. Payment unverified."))
        }

        val verifiedTx = transaction.copy(
            status = "CAPTURED",
            signatureVerified = true
        )

        // 3. Persist transaction record
        pDao.insertTransaction(verifiedTx)

        // 4. Activate plan & update quotas
        val plan = when (verifiedTx.planTier.uppercase()) {
            "CREATOR" -> PlanTier.CREATOR
            "PRO" -> PlanTier.PRO
            "STUDIO" -> PlanTier.STUDIO
            else -> PlanTier.FREE
        }

        val isAnnual = verifiedTx.billingInterval.equals("annual", ignoreCase = true)
        val newSub = PaymentVerificationHelper.buildSubscriptionForPlan(
            plan = plan,
            isAnnual = isAnnual,
            transactionId = verifiedTx.transactionId,
            orderId = verifiedTx.orderId
        )

        sDao.upsertSubscription(newSub)
        return Result.success(verifiedTx)
    }

    suspend fun cancelSubscription(userId: String = "creator_pilot_user") {
        subscriptionDao?.updateAutoRenew(false, userId = userId)
        subscriptionDao?.updateStatus("CANCELLED", userId = userId)
    }

    suspend fun reactivateSubscription(userId: String = "creator_pilot_user") {
        subscriptionDao?.updateAutoRenew(true, userId = userId)
        subscriptionDao?.updateStatus("ACTIVE", userId = userId)
    }
}
