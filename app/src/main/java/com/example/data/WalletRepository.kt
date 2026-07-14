package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WalletRepository(private val walletDao: WalletDao) {

    val allCards: Flow<List<CardEntity>> = walletDao.getAllCardsFlow()
    val allTransactions: Flow<List<TransactionEntity>> = walletDao.getAllTransactionsFlow()

    fun getTransactionsForCard(cardId: Int): Flow<List<TransactionEntity>> {
        return walletDao.getTransactionsForCardFlow(cardId)
    }

    suspend fun getCardById(cardId: Int): CardEntity? {
        return walletDao.getCardById(cardId)
    }

    suspend fun insertCard(card: CardEntity): Long {
        return walletDao.insertCard(card)
    }

    suspend fun updateCard(card: CardEntity) {
        walletDao.updateCard(card)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        // Also update card balance based on whether transaction is income or expense
        val card = walletDao.getCardById(transaction.cardId)
        if (card != null) {
            val newBalance = if (transaction.isIncome) {
                card.balance + transaction.amount
            } else {
                card.balance - transaction.amount
            }
            walletDao.updateCard(card.copy(balance = newBalance))
        }
        return walletDao.insertTransaction(transaction)
    }

    suspend fun populateInitialDataIfEmpty() {
        val existingCards = walletDao.getAllCardsFlow().first()
        if (existingCards.isEmpty()) {
            // Insert primary premium card
            val cardId1 = walletDao.insertCard(
                CardEntity(
                    cardHolder = "Alex Mercer",
                    cardNumber = "4532 •••• •••• 8824",
                    expiry = "12/29",
                    balance = 5420.50,
                    cardType = "Visa",
                    colorHex = "#1E293B" // Deep Slate/Slate 800
                )
            ).toInt()

            // Insert secondary gold card
            val cardId2 = walletDao.insertCard(
                CardEntity(
                    cardHolder = "Alex Mercer",
                    cardNumber = "5412 •••• •••• 9912",
                    expiry = "08/28",
                    balance = 1200.00,
                    cardType = "Mastercard",
                    colorHex = "#B45309" // Dark Amber/Gold 700
                )
            ).toInt()

            // Insert debit card
            val cardId3 = walletDao.insertCard(
                CardEntity(
                    cardHolder = "Alex Mercer",
                    cardNumber = "4000 •••• •••• 1045",
                    expiry = "05/27",
                    balance = 350.15,
                    cardType = "Visa",
                    colorHex = "#047857" // Deep Emerald/Emerald 700
                )
            ).toInt()

            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            // Populate Card 1 Transactions
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Monthly Salary (Google Inc)",
                    amount = 3500.00,
                    timestamp = now - 1 * dayMs,
                    category = "Salary",
                    isIncome = true,
                    cardId = cardId1
                )
            )
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Whole Foods Market",
                    amount = 124.50,
                    timestamp = now - 2 * dayMs,
                    category = "Food",
                    isIncome = false,
                    cardId = cardId1
                )
            )
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Netflix Premium Subscription",
                    amount = 15.99,
                    timestamp = now - 3 * dayMs,
                    category = "Entertainment",
                    isIncome = false,
                    cardId = cardId1
                )
            )

            // Populate Card 2 Transactions
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Bella Italia Dinner",
                    amount = 85.00,
                    timestamp = now - (0.5 * dayMs).toLong(),
                    category = "Food",
                    isIncome = false,
                    cardId = cardId2
                )
            )
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Electric Utility Bill",
                    amount = 45.20,
                    timestamp = now - 4 * dayMs,
                    category = "Utilities",
                    isIncome = false,
                    cardId = cardId2
                )
            )

            // Populate Card 3 Transactions
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Keychron Mechanical Keyboard",
                    amount = 119.00,
                    timestamp = now - 5 * dayMs,
                    category = "Shopping",
                    isIncome = false,
                    cardId = cardId3
                )
            )
            walletDao.insertTransaction(
                TransactionEntity(
                    title = "Received from Mom",
                    amount = 50.00,
                    timestamp = now - 6 * dayMs,
                    category = "Transfer",
                    isIncome = true,
                    cardId = cardId3
                )
            )
        }
    }
}
