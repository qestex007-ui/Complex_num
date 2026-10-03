package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ComplexNumber
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoPrimary
import kotlinx.coroutines.launch

@Composable
fun ResultCard(
    complexNumber: ComplexNumber,
    isSaved: Boolean,
    onSaveToHistory: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    useJ: Boolean = false,
    useDegreesInExp: Boolean = false,
    precision: Int = 4
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val algebraic = complexNumber.toAlgebraicString(useJ = useJ, precision = precision)
    val exponential = complexNumber.toExponentialString(useJ = useJ, useDegrees = useDegreesInExp, precision = precision)
    val trigonometric = complexNumber.toTrigonometricString(useJ = useJ, useDegrees = useDegreesInExp, precision = precision)
    val polar = complexNumber.toPolarNotationString(precision = precision)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Результаты вычисления",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onSaveToHistory,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) EmeraldAccent else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("save_history_button")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Save,
                        contentDescription = "Сохранить в историю",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSaved) "Сохранено" else "В историю",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Highlight: Exponential Form
            ResultItemRow(
                title = "Показательная форма",
                value = exponential,
                subtitle = "z = r · e^(i·φ)",
                highlightColor = CyanAccent,
                onCopy = {
                    copyToClipboard(context, exponential, "Показательная форма скопирована", snackbarHostState, scope)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Algebraic Form
            ResultItemRow(
                title = "Алгебраическая форма",
                value = algebraic,
                subtitle = "z = a + bi",
                highlightColor = MaterialTheme.colorScheme.primary,
                onCopy = {
                    copyToClipboard(context, algebraic, "Алгебраическая форма скопирована", snackbarHostState, scope)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Trigonometric Form
            ResultItemRow(
                title = "Тригонометрическая форма",
                value = trigonometric,
                subtitle = "z = r(cos φ + i·sin φ)",
                highlightColor = MaterialTheme.colorScheme.tertiary,
                onCopy = {
                    copyToClipboard(context, trigonometric, "Тригонометрическая форма скопирована", snackbarHostState, scope)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Polar engineering
            ResultItemRow(
                title = "Полярная запись (Steinmetz)",
                value = polar,
                subtitle = "z = r ∠ φ°",
                highlightColor = EmeraldAccent,
                onCopy = {
                    copyToClipboard(context, polar, "Полярная запись скопирована", snackbarHostState, scope)
                }
            )
        }
    }
}

@Composable
private fun ResultItemRow(
    title: String,
    value: String,
    subtitle: String,
    highlightColor: Color,
    onCopy: () -> Unit
) {
    var copied by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                copied = true
                onCopy()
            }
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(highlightColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "($subtitle)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    copied = true
                    onCopy()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Копировать",
                    tint = highlightColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun copyToClipboard(
    context: Context,
    text: String,
    message: String,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Complex Number", text)
    clipboard.setPrimaryClip(clip)
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}
