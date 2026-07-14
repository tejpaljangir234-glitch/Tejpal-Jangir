package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.WalletDatabase
import com.example.data.WalletRepository
import com.example.ui.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WalletViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var database: WalletDatabase
  private lateinit var repository: WalletRepository
  private lateinit var viewModel: WalletViewModel

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, WalletDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    repository = WalletRepository(database.walletDao())
    viewModel = WalletViewModel(repository)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        WalletScreen(viewModel = viewModel)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
