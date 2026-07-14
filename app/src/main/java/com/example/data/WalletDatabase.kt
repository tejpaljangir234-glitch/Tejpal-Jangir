package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardHolder: String,
    val cardNumber: String,
    val expiry: String,
    val balance: Double,
    val cardType: String, // "Visa", "Mastercard", "Amex", "Other"
    val colorHex: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val timestamp: Long,
    val category: String, // "Food", "Shopping", "Utilities", "Salary", "Transfer", "Entertainment"
    val isIncome: Boolean,
    val cardId: Int
)

@Dao
interface WalletDao {
    @Query("SELECT * FROM cards ORDER BY id ASC")
    fun getAllCardsFlow(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTransactionsForCardFlow(cardId: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()
}

@Database(entities = [CardEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
}
