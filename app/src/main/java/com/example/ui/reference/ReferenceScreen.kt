package com.example.ui.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoPrimary

@Composable
fun ReferenceScreen(
    onLoadPreset: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reference_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Справочник и теория",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Основные формы, формулы перехода и тождество Эйлера",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section 1: Forms of Complex Numbers
        item {
            TheoryCard(
                title = "Формы комплексного числа",
                subtitle = "Три равносильных способа математической записи"
            ) {
                FormulaBlock(
                    title = "1. Алгебраическая форма",
                    formula = "z = a + bi",
                    explanation = "a = Re(z) — вещественная часть, b = Im(z) — мнимая часть, i — мнимая единица (i² = -1)."
                )
                Spacer(modifier = Modifier.height(10.dp))
                FormulaBlock(
                    title = "2. Показательная (экспоненциальная) форма",
                    formula = "z = r · e^(i·φ)",
                    explanation = "r = |z| — модуль числа (длина вектора), φ = Arg(z) — аргумент (угол в радианах или градусах)."
                )
                Spacer(modifier = Modifier.height(10.dp))
                FormulaBlock(
                    title = "3. Тригонометрическая форма",
                    formula = "z = r · (cos φ + i · sin φ)",
                    explanation = "Связывает декартовы координаты (a, b) с полярными (r, φ) через проекции."
                )
            }
        }

        // Section 2: Conversion formulas
        item {
            TheoryCard(
                title = "Формулы перехода",
                subtitle = "Связь между алгебраической и показательной формами"
            ) {
                FormulaBlock(
                    title = "Из алгебраической в показательную",
                    formula = "r = √(a² + b²)\nφ = arctg(b / a)  [с учётом знаков a и b]",
                    explanation = "• Квадрант I (a>0, b>0): φ = arctg(b/a)\n• Квадрант II (a<0, b>0): φ = π + arctg(b/a)\n• Квадрант III (a<0, b<0): φ = -π + arctg(b/a)\n• Квадрант IV (a>0, b<0): φ = arctg(b/a)"
                )
                Spacer(modifier = Modifier.height(10.dp))
                FormulaBlock(
                    title = "Из показательной в алгебраическую",
                    formula = "a = r · cos(φ)\nb = r · sin(φ)",
                    explanation = "Вещественная и мнимая части получаются через косинус и синус угла φ."
                )
            }
        }

        // Section 3: Famous Euler's Identity
        item {
            TheoryCard(
                title = "Тождество Эйлера",
                subtitle = "«Самая красивая формула в математике»"
            ) {
                FormulaBlock(
                    title = "Формула Эйлера",
                    formula = "e^(i·φ) = cos(φ) + i · sin(φ)",
                    explanation = "При φ = π получаем: e^(i·π) = cos(π) + i·sin(π) = -1 + 0 = -1.\nСледовательно:\n\ne^(i·π) + 1 = 0\n\nФормула объединяет пять фундаментальных математических констант: 0, 1, e, i и π."
                )
            }
        }

        // Section 4: Famous numbers presets
        item {
            TheoryCard(
                title = "Классические примеры",
                subtitle = "Нажмите, чтобы загрузить число в калькулятор"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PresetButton(
                        label = "1 + i",
                        detail = "r = √2 ≈ 1.414, φ = 45° (π/4)",
                        onClick = { onLoadPreset(1.0, 1.0) }
                    )
                    PresetButton(
                        label = "3 + 4i",
                        detail = "Пифагоров треугольник: r = 5, φ ≈ 53.13°",
                        onClick = { onLoadPreset(3.0, 4.0) }
                    )
                    PresetButton(
                        label = "1 - √3·i",
                        detail = "r = 2, φ = -60° (-π/3)",
                        onClick = { onLoadPreset(1.0, -1.73205) }
                    )
                    PresetButton(
                        label = "e^(i·π) = -1",
                        detail = "r = 1, φ = 180° (π)",
                        onClick = { onLoadPreset(-1.0, 0.0) }
                    )
                    PresetButton(
                        label = "0 + 5i",
                        detail = "Чисто мнимое: r = 5, φ = 90° (π/2)",
                        onClick = { onLoadPreset(0.0, 5.0) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TheoryCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FormulaBlock(
    title: String,
    formula: String,
    explanation: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyanAccent
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PresetButton(
    label: String,
    detail: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Запустить",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
