package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val userId: String = "creator_pilot_user",
    val planTier: String = "FREE", // "FREE", "CREATOR", "PRO", "STUDIO"
    val status: String = "ACTIVE", // "ACTIVE", "CANCELLED", "PAST_DUE", "EXPIRED"
    val amountUsd: Double = 0.0,
    val billingInterval: String = "monthly", // "monthly" or "annual"
    val startDate: String = "2026-10-24",
    val nextBillingDate: String = "2026-11-23",
    val autoRenew: Boolean = true,
    val lastTransactionId: String = "",
    val lastOrderId: String = "",
    val aiActionsTotal: Int = 10,
    val aiActionsUsed: Int = 0,
    val scriptsAllowed: Int = 2,
    val scenePromptsAllowed: Int = 10,
    val activeProjectsLimit: Int = 3,
    val teamMembersLimit: Int = 1,
    val hasPriorityQueue: Boolean = false,
    val hasTeamWorkspaces: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
