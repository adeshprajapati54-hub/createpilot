package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment_transactions",
    indices = [
        Index(value = ["transactionId"], unique = true),
        Index(value = ["orderId"])
    ]
)
data class PaymentTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: String, // e.g., "pay_rzp_109283"
    val orderId: String,       // e.g., "order_cp_99214"
    val planTier: String,      // "CREATOR", "PRO", "STUDIO", "FREE"
    val amountUsd: Double,     // 12.00, 29.00, 79.00
    val currency: String = "USD",
    val billingInterval: String = "monthly", // "monthly", "annual"
    val paymentMethod: String = "Credit Card", // "Credit Card", "UPI", "Net Banking"
    val status: String = "CAPTURED",          // "CAPTURED", "FAILED", "PENDING", "REFUNDED", "CANCELLED"
    val signatureVerified: Boolean = true,
    val signature: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val billingDate: String,    // "2026-10-24"
    val nextBillingDate: String, // "2026-11-23"
    val customerEmail: String = "creator@creatorpilot.ai",
    val receiptNumber: String,  // "RCP-2026-9281"
    val gatewayProvider: String = "Razorpay", // "Razorpay" or "Stripe"
    val notes: String = ""
)
