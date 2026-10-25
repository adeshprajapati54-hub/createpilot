package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PaymentTransactionEntity>>

    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsSync(): List<PaymentTransactionEntity>

    @Query("SELECT * FROM payment_transactions WHERE transactionId = :txnId LIMIT 1")
    suspend fun getTransactionById(txnId: String): PaymentTransactionEntity?

    @Query("SELECT * FROM payment_transactions WHERE orderId = :orderId LIMIT 1")
    suspend fun getTransactionByOrderId(orderId: String): PaymentTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: PaymentTransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: PaymentTransactionEntity)

    @Query("DELETE FROM payment_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("SELECT COUNT(*) FROM payment_transactions WHERE transactionId = :txnId")
    suspend fun countTransactionWithId(txnId: String): Int
}
