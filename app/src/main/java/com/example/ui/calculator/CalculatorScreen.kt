package com.example.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ArgandPlane
import com.example.ui.components.ResultCard
import com.example.ui.components.StepByStepCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import kotlin.math.PI
import kotlin.math.sqrt

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var showSettingsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("calculator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: Algebraic <-> Polar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Tab 1: Alg -> Polar
                val isAlg = state.mode == ConversionMode.ALGEBRAIC_TO_POLAR
                Surface(
                    onClick = { viewModel.setMode(ConversionMode.ALGEBRAIC_TO_POLAR) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("tab_alg_to_polar"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAlg) MaterialTheme.colorScheme.surface else androidx.compose.ui.graphics.Color.Transparent,
                    tonalElevation = if (isAlg) 2.dp else 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "a + bi → r·e^(iφ)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isAlg) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isAlg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Алгебраическая",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isAlg) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Tab 2: Polar -> Alg
                val isPolar = state.mode == ConversionMode.POLAR_TO_ALGEBRAIC
                Surface(
                    onClick = { viewModel.setMode(ConversionMode.POLAR_TO_ALGEBRAIC) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("tab_polar_to_alg"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPolar) MaterialTheme.colorScheme.surface else androidx.compose.ui.graphics.Color.Transparent,
                    tonalElevation = if (isPolar) 2.dp else 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "r·e^(iφ) → a + bi",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isPolar) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (isPolar) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Показательная",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isPolar) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Quick Presets Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Примеры:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AssistChip(
                onClick = { viewModel.applyPreset(3.0, 4.0) },
                label = { Text("3 + 4i") },
                modifier = Modifier.testTag("preset_3_4i")
            )
            AssistChip(
                onClick = { viewModel.applyPreset(1.0, 1.0) },
                label = { Text("1 + i") },
                modifier = Modifier.testTag("preset_1_1i")
            )
            AssistChip(
                onClick = { viewModel.applyPreset(1.0, -sqrt(3.0)) },
                label = { Text("1 - √3·i") },
                modifier = Modifier.testTag("preset_1_neg_sqrt3")
            )
            AssistChip(
                onClick = { viewModel.applyPreset(-2.0, 2.0) },
                label = { Text("-2 + 2i") }
            )
            AssistChip(
                onClick = { viewModel.applyPreset(0.0, 5.0) },
                label = { Text("0 + 5i") }
            )
            AssistChip(
                onClick = { viewModel.applyPreset(-1.0, 0.0) },
                label = { Text("e^(i·π) = -1") }
            )
        }

        // Input Fields Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Section Title + Settings Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.mode == ConversionMode.ALGEBRAIC_TO_POLAR)
                            "Параметры комплексного числа (a, b)"
                        else
                            "Параметры полярной формы (r, φ)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box {
                        IconButton(
                            onClick = { showSettingsMenu = true },
                            modifier = Modifier.size(32.dp).testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Настройки отображения",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSettingsMenu,
                            onDismissRequest = { showSettingsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (state.useJNotation) "Нотация: j (физика/ТОЭ)" else "Нотация: i (математика)") },
                                onClick = {
                                    viewModel.toggleJNotation()
                                    showSettingsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (state.useDegreesInExp) "Экспонента: в градусах (e^i·φ°)" else "Экспонента: в радианах (e^i·φ)") },
                                onClick = {
                                    viewModel.toggleDegreesInExp()
                                    showSettingsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Точность: 2 знака") },
                                onClick = {
                                    viewModel.setPrecision(2)
                                    showSettingsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Точность: 4 знака") },
                                onClick = {
                                    viewModel.setPrecision(4)
                                    showSettingsMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Точность: 6 знаков") },
                                onClick = {
                                    viewModel.setPrecision(6)
                                    showSettingsMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (state.mode == ConversionMode.ALGEBRAIC_TO_POLAR) {
                    // Algebraic Inputs: a (Real) and b (Imag)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Real Part TextField
                        OutlinedTextField(
                            value = state.inputReal,
                            onValueChange = { viewModel.setInputReal(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_real"),
                            label = { Text("Вещественная (Re, a)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.negateReal() }) {
                                    Text("±", fontWeight = FontWeight.Bold)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Imaginary Part TextField
                        OutlinedTextField(
                            value = state.inputImag,
                            onValueChange = { viewModel.setInputImag(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_imag"),
                            label = { Text("Мнимая (Im, b)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.negateImag() }) {
                                    Text("±", fontWeight = FontWeight.Bold)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                } else {
                    // Polar Inputs: r (Modulus) and phi (Angle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Modulus r
                        OutlinedTextField(
                            value = state.inputR,
                            onValueChange = { viewModel.setInputR(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_r"),
                            label = { Text("Модуль (r, |z|)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Angle phi
                        OutlinedTextField(
                            value = state.inputAngle,
                            onValueChange = { viewModel.setInputAngle(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_angle"),
                            label = { Text("Угол (φ)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.negateAngle() }) {
                                    Text("±", fontWeight = FontWeight.Bold)
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Angle unit toggle: Degrees vs Radians
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Единица угла:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = state.angleUnit == AngleUnit.DEGREES,
                                onClick = { viewModel.setAngleUnit(AngleUnit.DEGREES) },
                                label = { Text("Градусы (°)") },
                                modifier = Modifier.testTag("unit_degrees_chip")
                            )

                            FilterChip(
                                selected = state.angleUnit == AngleUnit.RADIANS,
                                onClick = { viewModel.setAngleUnit(AngleUnit.RADIANS) },
                                label = { Text("Радианы (рад)") },
                                modifier = Modifier.testTag("unit_radians_chip")
                            )
                        }
                    }

                    // Quick Angle Presets
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (state.angleUnit == AngleUnit.RADIANS) {
                            listOf(
                                "0" to 0.0,
                                "π/6" to 1.0 / 6.0,
                                "π/4" to 1.0 / 4.0,
                                "π/3" to 1.0 / 3.0,
                                "π/2" to 1.0 / 2.0,
                                "2π/3" to 2.0 / 3.0,
                                "3π/4" to 3.0 / 4.0,
                                "π" to 1.0,
                                "-π/2" to -1.0 / 2.0
                            ).forEach { (label, factor) ->
                                AssistChip(
                                    onClick = { viewModel.applyPiFractionToAngle(factor) },
                                    label = { Text(label) }
                                )
                            }
                        } else {
                            listOf("0°", "30°", "45°", "60°", "90°", "120°", "135°", "180°", "-90°", "-45°").forEach { deg ->
                                AssistChip(
                                    onClick = {
                                        viewModel.setInputAngle(deg.replace("°", ""))
                                    },
                                    label = { Text(deg) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Argand Plane (Gauss Complex Plane)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Комплексная плоскость (Гаусс)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Коснитесь для перемещения",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ArgandPlane(
                real = state.currentComplex.real,
                imag = state.currentComplex.imag,
                onPointChange = { re, im ->
                    viewModel.updateFromPlane(re, im)
                },
                useJ = state.useJNotation
            )
        }

        // Converted Results Card
        ResultCard(
            complexNumber = state.currentComplex,
            isSaved = state.isSaved,
            onSaveToHistory = { viewModel.saveToHistory() },
            snackbarHostState = snackbarHostState,
            useJ = state.useJNotation,
            useDegreesInExp = state.useDegreesInExp,
            precision = state.precision
        )

        // Step by step derivation card
        StepByStepCard(steps = state.steps)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
