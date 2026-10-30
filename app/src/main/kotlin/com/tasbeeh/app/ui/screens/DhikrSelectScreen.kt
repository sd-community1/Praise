package com.tasbeeh.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasbeeh.app.data.DHIKR_LIST
import com.tasbeeh.app.data.DhikrItem
import com.tasbeeh.app.data.PRESET_TARGETS
import com.tasbeeh.app.data.TasbeehState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrSelectScreen(
    state: TasbeehState,
    onSelectDhikr: (DhikrItem) -> Unit,
    onSetCustomDhikr: (String) -> Unit,
    onSetTarget: (Int) -> Unit,
    onSetCustomTarget: (Int) -> Unit,
    onBack: () -> Unit
) {
    var customDhikrText by remember { mutableStateOf(state.customDhikr) }
    var customTargetText by remember { mutableStateOf("") }
    var showCustomTargetField by remember { mutableStateOf(false) }
    val currentTarget = if (state.isCustomTarget) state.customTargetValue else state.target

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اختيار الذكر والهدف") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "الأذكار",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(DHIKR_LIST) { dhikr ->
                val isSelected = state.selectedDhikr.id == dhikr.id
                DhikrCard(
                    dhikr = dhikr,
                    isSelected = isSelected,
                    onClick = {
                        onSelectDhikr(dhikr)
                        if (dhikr.id != 0) onBack()
                    }
                )
                if (dhikr.id == 0 && isSelected) {
                    OutlinedTextField(
                        value = customDhikrText,
                        onValueChange = { customDhikrText = it },
                        label = { Text("أدخل الذكر المخصص") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
                        trailingIcon = {
                            if (customDhikrText.isNotBlank()) {
                                IconButton(onClick = {
                                    onSetCustomDhikr(customDhikrText)
                                    onBack()
                                }) {
                                    Icon(Icons.Default.Check, contentDescription = "حفظ")
                                }
                            }
                        }
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    "الهدف",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PRESET_TARGETS.forEach { t ->
                        val isSelected = currentTarget == t && !state.isCustomTarget
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetTarget(t) },
                            label = { Text(t.toString()) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { showCustomTargetField = !showCustomTargetField },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.isCustomTarget) "هدف مخصص: ${state.customTargetValue}" else "هدف مخصص ✎")
                }
                if (showCustomTargetField) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = customTargetText,
                            onValueChange = { customTargetText = it.filter { c -> c.isDigit() } },
                            label = { Text("عدد التكرارات") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val v = customTargetText.toIntOrNull()
                            if (v != null && v > 0) {
                                onSetCustomTarget(v)
                                showCustomTargetField = false
                                customTargetText = ""
                            }
                        }) { Text("حفظ") }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun DhikrCard(dhikr: DhikrItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dhikr.arabic,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = if (dhikr.arabic.length > 20) 18.sp else 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


