package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CardEntity
import com.example.data.TransactionEntity
import com.example.data.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    // Database flows
    val cards: StateFlow<List<CardEntity>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allTransactions = repository.allTransactions
    val allTransactions: StateFlow<List<TransactionEntity>> = _allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected filter card ID (null means "All Cards")
    private val _selectedCardId = MutableStateFlow<Int?>(null)
    val selectedCardId: StateFlow<Int?> = _selectedCardId.asStateFlow()

    // Search and filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("All") // "All", "Income", "Expense"
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    // Composed state flow for filtered transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        _allTransactions,
        _selectedCardId,
        _searchQuery,
        _selectedCategoryFilter,
        _selectedTypeFilter
    ) { txList, selectedCardId, query, category, typeFilter ->
        txList.filter { tx ->
            val matchesCard = selectedCardId == null || tx.cardId == selectedCardId
            val matchesQuery = query.isEmpty() || tx.title.contains(query, ignoreCase = true) || tx.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || tx.category.equals(category, ignoreCase = true)
            val matchesType = when (typeFilter) {
                "Income" -> tx.isIncome
                "Expense" -> !tx.isIncome
                else -> true
            }
            matchesCard && matchesQuery && matchesCategory && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Dialog & Sheet states
    private val _showSendMoneyDialog = MutableStateFlow(false)
    val showSendMoneyDialog = _showSendMoneyDialog.asStateFlow()

    private val _showAddCardDialog = MutableStateFlow(false)
    val showAddCardDialog = _showAddCardDialog.asStateFlow()

    private val _showAddTransactionDialog = MutableStateFlow(false)
    val showAddTransactionDialog = _showAddTransactionDialog.asStateFlow()

    private val _selectedTransactionDetails = MutableStateFlow<TransactionEntity?>(null)
    val selectedTransactionDetails = _selectedTransactionDetails.asStateFlow()

    private val _showPinPrompt = MutableStateFlow<PendingTransaction?>(null)
    val showPinPrompt = _showPinPrompt.asStateFlow()

    // Success state for money sent animations
    private val _showSuccessSplash = MutableStateFlow<String?>(null) // recipient name if active
    val showSuccessSplash = _showSuccessSplash.asStateFlow()

    data class PendingTransaction(
        val recipient: String,
        val amount: Double,
        val category: String,
        val cardId: Int
    )

    fun selectCard(cardId: Int?) {
        _selectedCardId.value = cardId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun updateTypeFilter(type: String) {
        _selectedTypeFilter.value = type
    }

    fun setShowSendMoneyDialog(show: Boolean) {
        _showSendMoneyDialog.value = show
    }

    fun setShowAddCardDialog(show: Boolean) {
        _showAddCardDialog.value = show
    }

    fun setShowAddTransactionDialog(show: Boolean) {
        _showAddTransactionDialog.value = show
    }

    fun selectTransaction(transaction: TransactionEntity?) {
        _selectedTransactionDetails.value = transaction
    }

    fun initiateSendMoney(recipient: String, amount: Double, category: String, cardId: Int) {
        _showPinPrompt.value = PendingTransaction(recipient, amount, category, cardId)
        _showSendMoneyDialog.value = false
    }

    fun cancelPinPrompt() {
        _showPinPrompt.value = null
    }

    fun confirmPinAndSend(pin: String) {
        val pending = _showPinPrompt.value ?: return
        if (pin == "1234") { // A simple mock security pin for simulation purposes
            viewModelScope.launch {
                val newTx = TransactionEntity(
                    title = "To ${pending.recipient}",
                    amount = pending.amount,
                    timestamp = System.currentTimeMillis(),
                    category = pending.category,
                    isIncome = false,
                    cardId = pending.cardId
                )
                repository.insertTransaction(newTx)
                _showPinPrompt.value = null
                _showSuccessSplash.value = pending.recipient
            }
        } else {
            // In a real app we would raise an error, but let's make it robust:
            // Throwing or displaying an error can be handled in UI. We will handle invalid PIN in UI.
        }
    }

    fun dismissSuccessSplash() {
        _showSuccessSplash.value = null
    }

    fun addNewCard(cardHolder: String, cardNumber: String, expiry: String, balance: Double, cardType: String, colorHex: String) {
        viewModelScope.launch {
            val formattedNumber = if (cardNumber.length >= 16) {
                "${cardNumber.substring(0, 4)} •••• •••• ${cardNumber.substring(12)}"
            } else {
                cardNumber
            }
            val newCard = CardEntity(
                cardHolder = cardHolder,
                cardNumber = formattedNumber,
                expiry = expiry,
                balance = balance,
                cardType = cardType,
                colorHex = colorHex
            )
            repository.insertCard(newCard)
            _showAddCardDialog.value = false
        }
    }

    fun addNewTransaction(title: String, amount: Double, timestamp: Long, category: String, isIncome: Boolean, cardId: Int) {
        viewModelScope.launch {
            val newTx = TransactionEntity(
                title = title,
                amount = amount,
                timestamp = timestamp,
                category = category,
                isIncome = isIncome,
                cardId = cardId
            )
            repository.insertTransaction(newTx)
            _showAddTransactionDialog.value = false
        }
    }

    // Factory for creating ViewModel with repository
    class Factory(private val repository: WalletRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
                return WalletViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
