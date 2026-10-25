package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId LIMIT 1")
    fun getSubscription(userId: String = "creator_pilot_user"): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM user_subscriptions WHERE userId = :userId LIMIT 1")
    suspend fun getSubscriptionSync(userId: String = "creator_pilot_user"): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubscription(subscription: SubscriptionEntity)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("UPDATE user_subscriptions SET autoRenew = :autoRenew, updatedAt = :timestamp WHERE userId = :userId")
    suspend fun updateAutoRenew(autoRenew: Boolean, timestamp: Long = System.currentTimeMillis(), userId: String = "creator_pilot_user")

    @Query("UPDATE user_subscriptions SET status = :status, updatedAt = :timestamp WHERE userId = :userId")
    suspend fun updateStatus(status: String, timestamp: Long = System.currentTimeMillis(), userId: String = "creator_pilot_user")
}
