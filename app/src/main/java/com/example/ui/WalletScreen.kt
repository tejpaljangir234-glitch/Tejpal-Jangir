package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CardEntity
import com.example.data.TransactionEntity
import com.example.viewmodel.WalletViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

// Utility colors matching our Slate theme
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate400 = Color(0xFF94A3B8)
val Slate100 = Color(0xFFF1F5F9)

val IndigoGlow = Color(0xFF6366F1)
val BlueGlow = Color(0xFF3B82F6)
val EmeraldGlow = Color(0xFF10B981)
val RoseGlow = Color(0xFFF43F5E)

// Category Colors for our Donut Chart
val CategoryColors = mapOf(
    "Food" to Color(0xFFF59E0B),        // Amber
    "Shopping" to Color(0xFFEC4899),    // Pink
    "Utilities" to Color(0xFF06B6D4),   // Cyan
    "Salary" to Color(0xFF10B981),      // Emerald
    "Transfer" to Color(0xFF8B5CF6),    // Purple
    "Entertainment" to Color(0xFFF43F5E) // Rose
)

@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.cards.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val selectedCardId by viewModel.selectedCardId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val typeFilter by viewModel.selectedTypeFilter.collectAsState()

    // Dialog & overlay states
    val showSendMoney by viewModel.showSendMoneyDialog.collectAsState()
    val showAddCard by viewModel.showAddCardDialog.collectAsState()
    val selectedTxDetails by viewModel.selectedTransactionDetails.collectAsState()
    val showPinPrompt by viewModel.showPinPrompt.collectAsState()
    val showSuccessSplash by viewModel.showSuccessSplash.collectAsState()
    val showAddTransaction by viewModel.showAddTransactionDialog.collectAsState()

    // Calculate total balance across all cards
    val totalBalance = remember(cards) { cards.sumOf { it.balance } }

    val totalIncome = remember(allTransactions) {
        allTransactions.filter { it.isIncome }.sumOf { it.amount }
    }
    val totalExpenses = remember(allTransactions) {
        allTransactions.filter { !it.isIncome }.sumOf { it.amount }
    }
    val dynamicBalance = remember(totalIncome, totalExpenses) {
        totalIncome - totalExpenses
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    val writer = java.io.BufferedWriter(java.io.OutputStreamWriter(outputStream))
                    writer.write("ID,Title,Amount,Type,Category,Date\n")
                    transactions.forEach { tx ->
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(tx.timestamp))
                        val cleanTitle = tx.title.replace("\"", "\"\"")
                        val typeStr = if (tx.isIncome) "Income" else "Expense"
                        writer.write("${tx.id},\"$cleanTitle\",${tx.amount},$typeStr,${tx.category},\"$formattedDate\"\n")
                    }
                    writer.flush()
                }
                android.widget.Toast.makeText(context, "Exported successfully!", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. App Header & Hero Balance Card
            item {
                HeaderSection(
                    totalBalance = totalBalance,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    dynamicBalance = dynamicBalance,
                    onSendClick = { viewModel.setShowSendMoneyDialog(true) },
                    onAddCardClick = { viewModel.setShowAddCardDialog(true) }
                )
            }

            // 2. Cards Carousel (Horizontal)
            item {
                CardsCarouselSection(
                    cards = cards,
                    selectedCardId = selectedCardId,
                    onCardSelect = { viewModel.selectCard(it) },
                    onAddCardClick = { viewModel.setShowAddCardDialog(true) }
                )
            }

            // 3. Category Insights (Visual Donut Chart)
            item {
                InsightsSection(transactions = transactions)
            }

            // 4. Transactions List Header (Filters)
            item {
                TransactionsHeaderSection(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.updateSearchQuery(it) },
                    selectedCategory = categoryFilter,
                    onCategoryChange = { viewModel.updateCategoryFilter(it) },
                    selectedType = typeFilter,
                    onTypeChange = { viewModel.updateTypeFilter(it) },
                    onResetFilter = { viewModel.selectCard(null) },
                    isFilteringByCard = selectedCardId != null,
                    onExportCSV = {
                        exportLauncher.launch("transactions.csv")
                    }
                )
            }

            // 5. Transactions Items
            if (transactions.isEmpty()) {
                item {
                    EmptyTransactionsState()
                }
            } else {
                items(
                    items = transactions,
                    key = { it.id }
                ) { tx ->
                    val card = cards.find { it.id == tx.cardId }
                    TransactionItemRow(
                        transaction = tx,
                        cardType = card?.cardType ?: "Card",
                        cardDigits = card?.cardNumber?.takeLast(4) ?: "••••",
                        onClick = { viewModel.selectTransaction(tx) }
                    )
                }
            }

            // Bottom space for scrolling breathability
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- OVERLAYS & DIALOGS ---

        // A. Send Money Dialog
        if (showSendMoney) {
            SendMoneyDialog(
                cards = cards,
                onDismiss = { viewModel.setShowSendMoneyDialog(false) },
                onSendInitiated = { recipient, amount, category, cardId ->
                    viewModel.initiateSendMoney(recipient, amount, category, cardId)
                }
            )
        }

        // B. Add Card Dialog
        if (showAddCard) {
            AddCardDialog(
                onDismiss = { viewModel.setShowAddCardDialog(false) },
                onCardAdded = { holder, number, expiry, balance, type, color ->
                    viewModel.addNewCard(holder, number, expiry, balance, type, color)
                }
            )
        }

        // C. Transaction Details Dialog
        selectedTxDetails?.let { tx ->
            val card = cards.find { it.id == tx.cardId }
            TransactionDetailsDialog(
                transaction = tx,
                card = card,
                onDismiss = { viewModel.selectTransaction(null) }
            )
        }

        // D. PIN Verification Prompt
        showPinPrompt?.let { pending ->
            PinPromptDialog(
                pending = pending,
                onDismiss = { viewModel.cancelPinPrompt() },
                onPinVerified = { pin -> viewModel.confirmPinAndSend(pin) }
            )
        }

        // E. Success Overlay Splash Screen
        showSuccessSplash?.let { recipient ->
            SuccessSplashOverlay(
                recipient = recipient,
                onDismiss = { viewModel.dismissSuccessSplash() }
            )
        }

        // F. Add Transaction Dialog Form
        if (showAddTransaction) {
            AddTransactionDialog(
                cards = cards,
                onDismiss = { viewModel.setShowAddTransactionDialog(false) },
                onTransactionAdded = { title, amount, timestamp, category, isIncome, cardId ->
                    viewModel.addNewTransaction(title, amount, timestamp, category, isIncome, cardId)
                }
            )
        }

        // Glowing FAB Button with Indigo/Blue gradient to trigger AddTransactionDialog Form
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .navigationBarsPadding()
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(IndigoGlow, BlueGlow)))
                .clickable { viewModel.setShowAddTransactionDialog(true) }
                .testTag("add_transaction_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Transaction",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

// ==================== UI COMPONENTS ====================

@Composable
fun HeaderSection(
    totalBalance: Double,
    totalIncome: Double,
    totalExpenses: Double,
    dynamicBalance: Double,
    onSendClick: () -> Unit,
    onAddCardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome Back,",
                    color = Slate400,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Alex Mercer",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // User Profile Avatar placeholder
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(IndigoGlow, BlueGlow)))
                    .clickable { /* Profile settings could be here */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AM",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Total Balance Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Slate800.copy(alpha = 0.9f), Slate800),
                        center = Offset(0.5f, 0f),
                        radius = 800f
                    )
                )
                .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "TOTAL WALLET BALANCE",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$",
                        color = IndigoGlow,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
                    )
                    Text(
                        text = String.format(Locale.US, "%,.2f", totalBalance),
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Beautiful interactive row of actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSendClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("send_money_action"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Send Money",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = onAddCardClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("add_card_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Card",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Card",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaction Summary Card (Requested summary card dynamically calculating statistics)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Slate800)
                .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(16.dp)
                .testTag("transaction_summary_card")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "TRANSACTION LEDGER SUMMARY",
                    color = Slate400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Income
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income Icon",
                                tint = EmeraldGlow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Income",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "+$%,.2f", totalIncome),
                            color = EmeraldGlow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.testTag("summary_total_income")
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Slate700.copy(alpha = 0.5f))
                    )

                    // Middle Column: Expenses
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Expense Icon",
                                tint = RoseGlow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Expenses",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "-$%,.2f", totalExpenses),
                            color = RoseGlow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.testTag("summary_total_expenses")
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Slate700.copy(alpha = 0.5f))
                    )

                    // Right Column: Ledger Balance
                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ledger Balance",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val balanceColor = if (dynamicBalance >= 0) EmeraldGlow else RoseGlow
                        val prefix = if (dynamicBalance >= 0) "$" else "-$"
                        Text(
                            text = String.format(Locale.US, "%s%,.2f", prefix, kotlin.math.abs(dynamicBalance)),
                            color = balanceColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.testTag("summary_ledger_balance")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardsCarouselSection(
    cards: List<CardEntity>,
    selectedCardId: Int?,
    onCardSelect: (Int?) -> Unit,
    onAddCardClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Cards",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (selectedCardId != null) {
                TextButton(
                    onClick = { onCardSelect(null) },
                    colors = ButtonDefaults.textButtonColors(contentColor = IndigoGlow)
                ) {
                    Text("Show All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "${cards.size} Available",
                    color = Slate400,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
        ) {
            items(cards, key = { it.id }) { card ->
                val isSelected = selectedCardId == card.id
                val cardColor = remember(card.colorHex) {
                    try {
                        Color(android.graphics.Color.parseColor(card.colorHex))
                    } catch (e: Exception) {
                        IndigoGlow
                    }
                }

                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(cardColor, cardColor.copy(alpha = 0.7f))
                            )
                        )
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            if (isSelected) onCardSelect(null) else onCardSelect(card.id)
                        }
                        .padding(20.dp)
                        .testTag("card_item_${card.id}")
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Card Type & Brand Logo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Card Chip",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = card.cardType.uppercase(),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // Masked Card Number
                        Text(
                            text = card.cardNumber,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        // Balance, Holder, Expiry Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "CARD BALANCE",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", card.balance),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "EXPIRY",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = card.expiry,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Add Card Placeholder inside Carousel
            item {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Slate800)
                        .border(
                            width = 1.dp,
                            color = Slate700.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onAddCardClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Slate700),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Card Icon",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "New Card",
                            color = Slate400,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightsSection(transactions: List<TransactionEntity>) {
    // Only aggregate expense transactions
    val expenses = remember(transactions) { transactions.filter { !it.isIncome } }
    val categoryTotals = remember(expenses) {
        expenses.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    }
    val totalExpense = remember(categoryTotals) { categoryTotals.values.sum() }

    if (expenses.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Expense Insights",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donut Chart on Canvas
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        categoryTotals.forEach { (cat, amount) ->
                            val sweepAngle = (amount / totalExpense * 360f).toFloat()
                            val color = CategoryColors[cat] ?: Slate400
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 24f, cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total",
                            color = Slate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format(Locale.US, "$%,.0f", totalExpense),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legends
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sorting by heaviest expense first
                    categoryTotals.entries.sortedByDescending { it.value }.take(4).forEach { (category, amount) ->
                        val color = CategoryColors[category] ?: Slate400
                        val percentage = (amount / totalExpense * 100).toInt()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category,
                                    color = Slate100,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "$percentage%",
                                color = Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionsHeaderSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    selectedType: String,
    onTypeChange: (String) -> Unit,
    onResetFilter: () -> Unit,
    isFilteringByCard: Boolean,
    onExportCSV: () -> Unit
) {
    val categories = listOf("All", "Food", "Shopping", "Utilities", "Salary", "Transfer", "Entertainment")
    val types = listOf("All", "Income", "Expense")

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transactions History",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFilteringByCard) {
                    TextButton(
                        onClick = onResetFilter,
                        colors = ButtonDefaults.textButtonColors(contentColor = RoseGlow)
                    ) {
                        Text("Clear Filter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = onExportCSV,
                    colors = ButtonDefaults.textButtonColors(contentColor = IndigoGlow),
                    modifier = Modifier.testTag("export_csv_button")
                ) {
                    Text("Export CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("transaction_search_input"),
            placeholder = { Text("Search transactions...", color = Slate400) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Slate400
                )
            },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Slate400
                        )
                    }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Slate800,
                unfocusedContainerColor = Slate800,
                focusedBorderColor = Slate700,
                unfocusedBorderColor = Slate800,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Transaction Type Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Slate800)
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            types.forEach { type ->
                val isSelected = selectedType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) IndigoGlow else Color.Transparent)
                        .clickable { onTypeChange(type) }
                        .padding(vertical = 8.dp)
                        .testTag("type_filter_$type"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type,
                        color = if (isSelected) Color.White else Slate400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Dropdown Filter
        ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded,
            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = if (selectedCategory == "All") "All Categories" else selectedCategory,
                onValueChange = {},
                label = { Text("Filter by Category", color = Slate400, fontSize = 12.sp) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("category_filter_dropdown"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Slate800,
                    unfocusedContainerColor = Slate800,
                    focusedBorderColor = IndigoGlow,
                    unfocusedBorderColor = Slate800,
                    focusedLabelColor = IndigoGlow,
                    unfocusedLabelColor = Slate400
                )
            )
            ExposedDropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false },
                modifier = Modifier.background(Slate800)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val catColor = CategoryColors[cat] ?: Slate400
                                if (cat != "All") {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(catColor)
                                    )
                                }
                                Text(
                                    text = cat,
                                    color = if (isSelected) IndigoGlow else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onCategoryChange(cat)
                            categoryDropdownExpanded = false
                        },
                        modifier = Modifier.testTag("category_dropdown_item_$cat")
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyTransactionsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Empty filter",
            tint = Slate400.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Transactions Found",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Try adjusting your filters or search query",
            color = Slate400,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    cardType: String,
    cardDigits: String,
    onClick: () -> Unit
) {
    val categoryIcon = when (transaction.category) {
        "Food" -> Icons.Default.Restaurant
        "Shopping" -> Icons.Default.ShoppingBag
        "Utilities" -> Icons.Default.Lightbulb
        "Salary" -> Icons.Default.Work
        "Transfer" -> Icons.Default.SwapHoriz
        "Entertainment" -> Icons.Default.Movie
        else -> Icons.Default.CreditCard
    }

    val categoryColor = CategoryColors[transaction.category] ?: Slate400

    val formattedDate = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)
        sdf.format(Date(transaction.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("transaction_item_${transaction.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Category Icon Bubble
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = transaction.category,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = transaction.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$cardType •••• $cardDigits  |  $formattedDate",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = (if (transaction.isIncome) "+" else "-") + String.format(Locale.US, "$%,.2f", transaction.amount),
            color = if (transaction.isIncome) EmeraldGlow else Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== OVERLAYS & MODALS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyDialog(
    cards: List<CardEntity>,
    onDismiss: () -> Unit,
    onSendInitiated: (String, Double, String, Int) -> Unit
) {
    if (cards.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Add a card first before initiating transfers", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow)) {
                        Text("Okay")
                    }
                }
            }
        }
        return
    }

    var recipient by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Transfer") }
    var selectedCard by remember { mutableStateOf(cards.first()) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedCard by remember { mutableStateOf(false) }

    val categories = listOf("Transfer", "Food", "Shopping", "Utilities", "Entertainment")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 500.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Slate800,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send Money",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // 1. Recipient input
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Recipient Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("recipient_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                // 2. Amount Input
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("send_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                // 3. Card Dropdown Picker
                ExposedDropdownMenuBox(
                    expanded = expandedCard,
                    onExpandedChange = { expandedCard = !expandedCard }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = "${selectedCard.cardType} (*${selectedCard.cardNumber.takeLast(4)}) - $${selectedCard.balance}",
                        onValueChange = {},
                        label = { Text("Pay From") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCard) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("pay_from_card_picker"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCard,
                        onDismissRequest = { expandedCard = false }
                    ) {
                        cards.forEach { card ->
                            DropdownMenuItem(
                                text = { Text("${card.cardType} (*${card.cardNumber.takeLast(4)}) - $${card.balance}") },
                                onClick = {
                                    selectedCard = card
                                    expandedCard = false
                                }
                            )
                        }
                    }
                }

                // 4. Category Dropdown Picker
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("send_category_picker"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action buttons
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull() ?: 0.0
                        if (recipient.isNotEmpty() && amt > 0.0 && amt <= selectedCard.balance) {
                            onSendInitiated(recipient, amt, category, selectedCard.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_money_submit"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow),
                    shape = RoundedCornerShape(12.dp),
                    enabled = recipient.isNotEmpty() && (amountStr.toDoubleOrNull() ?: 0.0) > 0.0 && (amountStr.toDoubleOrNull() ?: 0.0) <= selectedCard.balance
                ) {
                    Text("Proceed to PIN Screen", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onCardAdded: (String, String, String, Double, String, String) -> Unit
) {
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    var cardType by remember { mutableStateOf("Visa") }
    var cardColorHex by remember { mutableStateOf("#1E293B") } // Slate Default
    var expandedType by remember { mutableStateOf(false) }

    val cardTypes = listOf("Visa", "Mastercard", "Amex")
    val colors = listOf(
        "#1E293B" to "Slate",
        "#1E1B4B" to "Indigo",
        "#022C22" to "Teal",
        "#701A75" to "Fuchsia",
        "#7C2D12" to "Orange",
        "#0F172A" to "Dark"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 500.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Slate800,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add New Card",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // 1. Holder Name
                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { cardHolder = it },
                    label = { Text("Cardholder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("card_holder_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                // 2. Card Number (mask formatting is done on save)
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it },
                    label = { Text("16-Digit Card Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("card_number_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 3. Expiry Date (MM/YY)
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { if (it.length <= 5) expiry = it },
                        label = { Text("Expiry (MM/YY)") },
                        singleLine = true,
                        placeholder = { Text("12/29") },
                        modifier = Modifier.weight(1f).testTag("card_expiry_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )

                    // 4. Initial balance
                    OutlinedTextField(
                        value = initialBalance,
                        onValueChange = { initialBalance = it },
                        label = { Text("Initial Balance ($)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f).testTag("card_balance_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )
                }

                // 5. Card Brand Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = cardType,
                        onValueChange = {},
                        label = { Text("Card Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("card_type_picker"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        cardTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    cardType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // 6. Style Palette Selection
                Column {
                    Text("Select Card Theme", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.forEach { (hex, name) ->
                            val color = remember { Color(android.graphics.Color.parseColor(hex)) }
                            val isSelected = cardColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { cardColorHex = hex }
                                    .testTag("color_picker_$name")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action
                Button(
                    onClick = {
                        val bal = initialBalance.toDoubleOrNull() ?: 0.0
                        if (cardHolder.isNotEmpty() && cardNumber.length == 16 && expiry.contains("/")) {
                            onCardAdded(cardHolder, cardNumber, expiry, bal, cardType, cardColorHex)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("card_submit"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow),
                    shape = RoundedCornerShape(12.dp),
                    enabled = cardHolder.isNotEmpty() && cardNumber.length == 16 && expiry.isNotEmpty() && initialBalance.isNotEmpty()
                ) {
                    Text("Issue Virtual Card", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransactionDetailsDialog(
    transaction: TransactionEntity,
    card: CardEntity?,
    onDismiss: () -> Unit
) {
    val formattedDate = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy • hh:mm a", Locale.US)
        sdf.format(Date(transaction.timestamp))
    }

    // Custom simulated security hash to represent a blockchain/bank reference
    val refHash = remember(transaction.id) {
        "TXN" + (transaction.timestamp / 1000).toString(16).uppercase() + "M" + transaction.id.toString().padStart(3, '0')
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = Slate800,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Receipt Details",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Header icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.isIncome) EmeraldGlow.copy(alpha = 0.15f) else RoseGlow.copy(
                                alpha = 0.15f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = "Tx Flow",
                        tint = if (transaction.isIncome) EmeraldGlow else RoseGlow,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Amount
                Text(
                    text = (if (transaction.isIncome) "+" else "-") + String.format(Locale.US, "$%,.2f", transaction.amount),
                    color = if (transaction.isIncome) EmeraldGlow else Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                // Title / Recipient
                Text(
                    text = transaction.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Detail Lines Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate900)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(label = "Status", value = "Completed", valueColor = EmeraldGlow)
                    DetailRow(label = "Category", value = transaction.category)
                    DetailRow(label = "Payment Instrument", value = card?.let { "${it.cardType} (*${it.cardNumber.takeLast(4)})" } ?: "My Wallet")
                    DetailRow(label = "Date & Time", value = formattedDate)
                    DetailRow(label = "Transaction ID", value = refHash)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Slate400, fontSize = 12.sp)
        Text(text = value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PinPromptDialog(
    pending: WalletViewModel.PendingTransaction,
    onDismiss: () -> Unit,
    onPinVerified: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            if (pin == "1234") {
                onPinVerified(pin)
            } else {
                pinError = true
                pin = ""
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Slate800,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Security Verification",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Confirm sending $${pending.amount} to ${pending.recipient} by entering your secure PIN.",
                    color = Slate400,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                // PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < pin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) IndigoGlow else Slate700)
                                .border(
                                    width = 1.dp,
                                    color = if (pinError) RoseGlow else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                if (pinError) {
                    Text(
                        text = "Invalid PIN! Hint: Default simulation PIN is '1234'",
                        color = RoseGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Hint: Default simulation PIN is '1234'",
                        color = Slate400.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Numeric keypad simulator
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "⌫")
                    )

                    keys.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowKeys.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.8f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Slate900)
                                        .clickable {
                                            pinError = false
                                            when (key) {
                                                "C" -> pin = ""
                                                "⌫" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                                else -> if (pin.length < 4) pin += key
                                            }
                                        }
                                        .testTag("pin_key_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        color = if (key == "C" || key == "⌫") RoseGlow else Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessSplashOverlay(
    recipient: String,
    onDismiss: () -> Unit
) {
    // Automatically close splash screen after 2.5 seconds
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900.copy(alpha = 0.95f))
            .clickable { onDismiss() } // Allow dismissing by tapping anywhere
            .testTag("success_splash_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success Icon",
                tint = EmeraldGlow,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Transaction Complete!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Money sent successfully to $recipient.",
                color = Slate100,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap anywhere to continue",
                color = Slate400,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    cards: List<CardEntity>,
    onDismiss: () -> Unit,
    onTransactionAdded: (title: String, amount: Double, timestamp: Long, category: String, isIncome: Boolean, cardId: Int) -> Unit
) {
    if (cards.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Add a card first before adding transactions", color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow)) {
                        Text("Okay")
                    }
                }
            }
        }
        return
    }

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedCardId by remember { mutableStateOf(cards.first().id) }
    
    // Date states
    val todayStr = remember { SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date()) }
    val yesterdayStr = remember { 
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -1)
        SimpleDateFormat("MM/dd/yyyy", Locale.US).format(cal.time)
    }
    var selectedDateOption by remember { mutableStateOf("Today") } // "Today", "Yesterday", "Custom"
    var customDateStr by remember { mutableStateOf(todayStr) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 500.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Slate800,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Transaction",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // 1. Expense/Income Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isIncome) RoseGlow else Color.Transparent)
                            .clickable { isIncome = false }
                            .padding(vertical = 10.dp)
                            .testTag("form_type_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            color = if (!isIncome) Color.White else Slate400,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isIncome) EmeraldGlow else Color.Transparent)
                            .clickable { isIncome = true }
                            .padding(vertical = 10.dp)
                            .testTag("form_type_income"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            color = if (isIncome) Color.White else Slate400,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. Amount Input
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("form_amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                // 3. Description / Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Cinema Tickets, Weekly Groceries") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("form_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IndigoGlow,
                        unfocusedBorderColor = Slate700
                    )
                )

                // 4. Card Selection Carousel
                Text(
                    text = "Link to Card",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cards) { card ->
                        val isSelected = selectedCardId == card.id
                        val cardColor = remember(card.colorHex) {
                            try {
                                Color(android.graphics.Color.parseColor(card.colorHex))
                            } catch (e: Exception) {
                                IndigoGlow
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) cardColor else Slate900)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color.White else Slate700.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCardId = card.id }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("form_card_item_${card.id}")
                        ) {
                            Column {
                                Text(
                                    text = card.cardType.uppercase(),
                                    color = if (isSelected) Color.White else Slate400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "*${card.cardNumber.takeLast(4)}",
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 5. Category Horizontal Selection Chips
                Text(
                    text = "Category",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                val categories = listOf("Food", "Shopping", "Utilities", "Salary", "Transfer", "Entertainment")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        val catColor = CategoryColors[cat] ?: Slate400
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) catColor.copy(alpha = 0.2f) else Slate900)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) catColor else Slate700.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("form_category_chip_$cat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(catColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else Slate400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 6. Date Options Row Selector (Today, Yesterday, Custom)
                Text(
                    text = "Transaction Date",
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Today", "Yesterday", "Custom").forEach { opt ->
                        val isSelected = selectedDateOption == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) IndigoGlow else Slate900)
                                .clickable { selectedDateOption = opt }
                                .padding(vertical = 10.dp)
                                .testTag("form_date_opt_$opt"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) Color.White else Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Conditional custom date input field
                if (selectedDateOption == "Custom") {
                    OutlinedTextField(
                        value = customDateStr,
                        onValueChange = { customDateStr = it },
                        label = { Text("Date (MM/DD/YYYY)") },
                        placeholder = { Text("e.g. 07/13/2026") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("form_custom_date_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = IndigoGlow,
                            unfocusedBorderColor = Slate700
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                val finalAmount = amountStr.toDoubleOrNull() ?: 0.0
                val isValid = title.isNotEmpty() && finalAmount > 0.0
                val selectedCard = cards.find { it.id == selectedCardId }
                val isBalanceSufficient = isIncome || selectedCard == null || finalAmount <= selectedCard.balance

                Button(
                    onClick = {
                        val timestamp = when (selectedDateOption) {
                            "Today" -> System.currentTimeMillis()
                            "Yesterday" -> {
                                val cal = java.util.Calendar.getInstance()
                                cal.add(java.util.Calendar.DATE, -1)
                                cal.timeInMillis
                            }
                            else -> {
                                try {
                                    SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(customDateStr)?.time ?: System.currentTimeMillis()
                                } catch (e: Exception) {
                                    System.currentTimeMillis()
                                }
                            }
                        }
                        onTransactionAdded(title, finalAmount, timestamp, selectedCategory, isIncome, selectedCardId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("form_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isValid && isBalanceSufficient
                ) {
                    Text(
                        text = if (isBalanceSufficient) "Add Transaction" else "Insufficient Card Balance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
