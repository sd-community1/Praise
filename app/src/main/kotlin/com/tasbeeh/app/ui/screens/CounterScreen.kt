package com.tasbeeh.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasbeeh.app.data.TasbeehState
import com.tasbeeh.app.data.DHIKR_LIST
import com.tasbeeh.app.ui.theme.GoldAccent
import com.tasbeeh.app.ui.theme.EmeraldLight
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    state: TasbeehState,
    showTargetReached: Boolean,
    onIncrement: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onDismissTargetReached: () -> Unit,
    onSetTarget: (Int) -> Unit = {},
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDhikrSelect: () -> Unit
) {
    val target = if (state.isCustomTarget) state.customTargetValue else state.target
    val progress = if (target > 0) min(state.currentCount.toFloat() / target.toFloat(), 1f) else 0f
    val isCompleted = state.currentCount >= target && target > 0

    var showResetDialog by remember { mutableStateOf(false) }

    // Button press animation
    val buttonScale = remember { Animatable(1f) }

    // Pulsing animation for completed state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCompleted) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    if (showTargetReached) {
        AlertDialog(
            onDismissRequest = onDismissTargetReached,
            title = {
                Text(
                    text = "أحسنت! 🌟",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "لقد أكملت $target من ${getDhikrDisplayText(state)}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismissTargetReached()
                    onReset()
                }) { Text("بدء جديد") }
            },
            dismissButton = {
                TextButton(onClick = onDismissTargetReached) { Text("متابعة") }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة تعيين؟", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("هل تريد إعادة العدّاد إلى الصفر؟", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = { showResetDialog = false; onReset() }) {
                    Text("نعم", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("لا") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تسبيح", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "السجل")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "الإعدادات")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Dhikr selector
            DhikrSelector(
                currentDhikr = getDhikrDisplayText(state),
                onClick = onNavigateToDhikrSelect
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Circular progress + counter
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .scale(pulseScale)
            ) {
                CircularProgressCanvas(
                    progress = progress,
                    isCompleted = isCompleted,
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = state.currentCount,
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() togetherWith
                                    slideOutVertically { it } + fadeOut()
                        },
                        label = "countAnim"
                    ) { count ->
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) GoldAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "/ $target",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isCompleted) {
                        Text(
                            text = "اكتمل ✓",
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Target chips
            TargetRow(
                currentTarget = target,
                isCustomTarget = state.isCustomTarget,
                onSelectTarget = { onSetTarget(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main count button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .clickable {
                        onIncrement()
                    },
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = getDhikrShortText(state),
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اضغط للتسبيح",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Undo and Reset row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = onUndo,
                    enabled = state.currentCount > 0,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("تراجع")
                }
                Button(
                    onClick = { showResetDialog = true },
                    enabled = state.currentCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CircularProgressCanvas(
    progress: Float,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = if (isCompleted) GoldAccent else EmeraldLight

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val strokeWidth = 16.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = androidx.compose.ui.geometry.Offset(
            (size.width - diameter) / 2f, (size.height - diameter) / 2f
        )
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

        // Track
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Progress
        if (animatedProgress > 0f) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun DhikrSelector(currentDhikr: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDhikr,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("▼", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
fun TargetRow(
    currentTarget: Int,
    isCustomTarget: Boolean,
    onSelectTarget: (Int) -> Unit
) {
    val presets = listOf(33, 99, 100, 500)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        presets.forEach { t ->
            val isSelected = currentTarget == t && !isCustomTarget
            FilterChip(
                selected = isSelected,
                onClick = { onSelectTarget(t) },
                label = { Text(t.toString()) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White
                )
            )
        }
        if (isCustomTarget) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("$currentTarget ✎") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldAccent,
                    selectedLabelColor = Color.Black
                )
            )
        }
    }
}

private fun getDhikrDisplayText(state: TasbeehState): String {
    return if (state.selectedDhikr.id == 0) {
        state.customDhikr.ifBlank { "ذكر مخصص" }
    } else {
        state.selectedDhikr.arabic
    }
}

private fun getDhikrShortText(state: TasbeehState): String {
    val full = getDhikrDisplayText(state)
    return if (full.length > 20) full.take(18) + "…" else full
}
