package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.CalculationRepository
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.calculator.CalculatorViewModel
import com.example.ui.history.HistoryScreen
import com.example.ui.history.HistoryViewModel
import com.example.ui.reference.ReferenceScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    CALCULATOR,
    HISTORY,
    THEORY
}

class MainViewModelFactory(
    private val repository: CalculationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CalculationRepository(database.calculationDao())
        val factory = MainViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                MainApp(factory = factory)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    factory: MainViewModelFactory
) {
    val calculatorViewModel: CalculatorViewModel = viewModel(factory = factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)
    val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(AppScreen.CALCULATOR) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Android back navigation: if not on CALCULATOR, go to CALCULATOR
    BackHandler(enabled = currentScreen != AppScreen.CALCULATOR) {
        currentScreen = AppScreen.CALCULATOR
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Комплексные числа",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "z = a + bi ⇄ r·e^(iφ)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = CyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Calculator
                NavigationBarItem(
                    selected = currentScreen == AppScreen.CALCULATOR,
                    onClick = { currentScreen = AppScreen.CALCULATOR },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.CALCULATOR) Icons.Filled.Calculate else Icons.Outlined.Calculate,
                            contentDescription = "Калькулятор"
                        )
                    },
                    label = { Text("Калькулятор") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_calculator")
                )

                // 2. History
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HISTORY,
                    onClick = { currentScreen = AppScreen.HISTORY },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (historyState.items.isNotEmpty()) {
                                    Badge(
                                        containerColor = IndigoPrimary,
                                        contentColor = androidx.compose.ui.graphics.Color.White
                                    ) {
                                        Text("${historyState.items.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = "История"
                            )
                        }
                    },
                    label = { Text("История") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_history")
                )

                // 3. Theory / Reference
                NavigationBarItem(
                    selected = currentScreen == AppScreen.THEORY,
                    onClick = { currentScreen = AppScreen.THEORY },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.THEORY) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Теория"
                        )
                    },
                    label = { Text("Теория") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = IndigoPrimary,
                        indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_theory")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.CALCULATOR -> {
                        CalculatorScreen(
                            viewModel = calculatorViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }

                    AppScreen.HISTORY -> {
                        HistoryScreen(
                            viewModel = historyViewModel,
                            onLoadCalculation = { re, im ->
                                calculatorViewModel.loadFromHistory(re, im)
                                currentScreen = AppScreen.CALCULATOR
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }

                    AppScreen.THEORY -> {
                        ReferenceScreen(
                            onLoadPreset = { re, im ->
                                calculatorViewModel.applyPreset(re, im)
                                currentScreen = AppScreen.CALCULATOR
                            }
                        )
                    }
                }
            }
        }
    }
}
