package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.WalletDatabase
import com.example.data.WalletRepository
import com.example.ui.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
  private lateinit var database: WalletDatabase
  private lateinit var repository: WalletRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Room Database safely on the application context
    database = Room.databaseBuilder(
        applicationContext,
        WalletDatabase::class.java,
        "pay_wallet_db"
    ).build()
    repository = WalletRepository(database.walletDao())

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        WalletScreen(
            viewModel = viewModel(factory = WalletViewModel.Factory(repository)),
            modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
