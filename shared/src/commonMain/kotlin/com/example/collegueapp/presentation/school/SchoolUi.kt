package com.example.collegueapp.presentation.school

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegueapp.platform.shortDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun money(value: Int): String =
    "S/ " + value.toString().reversed().chunked(3).joinToString(",").reversed() + ".00"

fun dateLabel(value: String): String =
    runCatching { shortDate(LocalDate.parse(value.take(10))) }.getOrDefault(value)

fun decimal(value: Double): String = (kotlin.math.round(value * 10) / 10).toString()
fun sectionLabel(data: com.example.collegueapp.data.school.SchoolData, id: String?) =
    data.sections.find { it.id == id }?.label ?: "Aula por asignar"

data class FormField(
    val id: String,
    val label: String,
    val initial: String = "",
    val choices: List<Pair<String, String>> = emptyList(),
    val multiline: Boolean = false
)

data class SchoolForm(
    val title: String,
    val fields: List<FormField> = emptyList(),
    val body: String = "",
    val confirm: String = "Guardar",
    val submit: (Map<String, String>) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolFormSheet(form: SchoolForm, onDismiss: () -> Unit, onSaved: () -> Unit = {}) {
    val values = remember(form) {
        mutableStateMapOf<String, String>().apply {
            form.fields.forEach {
                put(
                    it.id,
                    it.initial
                )
            }
        }
    }
    var error by remember(form) { mutableStateOf<String?>(null) }
    var dateField by remember { mutableStateOf<String?>(null) }
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            Modifier.widthIn(max = 720.dp).fillMaxWidth().heightIn(max = 720.dp)
                .padding(horizontal = 24.dp).navigationBarsPadding().imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(form.title, style = MaterialTheme.typography.headlineMedium)
            Column(
                Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (form.body.isNotEmpty()) Text(
                    form.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                form.fields.forEach { field ->
                    if (field.choices.isNotEmpty()) ChoiceField(
                        field.label,
                        values[field.id].orEmpty(),
                        field.choices
                    ) { values[field.id] = it; error = null }
                    else OutlinedTextField(
                        value = values[field.id].orEmpty(),
                        onValueChange = { values[field.id] = it; error = null },
                        label = { Text(field.label.replace(" (AAAA-MM-DD)", "")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = !field.multiline,
                        minLines = if (field.multiline) 3 else 1,
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = if (field.id == "due") ({
                            IconButton(onClick = {
                                dateField = field.id
                            }) { Icon(Icons.Outlined.CalendarMonth, "Elegir fecha") }
                        }) else null
                    )
                }
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Cerrar") }
                Button(
                    onClick = {
                        runCatching { form.submit(values.toMap()) }.onSuccess { onDismiss(); if (form.fields.isNotEmpty()) onSaved() }
                            .onFailure {
                                error = it.message ?: "Revisa los campos e inténtalo de nuevo."
                            }
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) { Text(form.confirm) }
            }
        }
    }
    dateField?.let { id ->
        val picker = rememberDatePickerState(initialSelectedDateMillis = runCatching {
            LocalDate.parse(values[id].orEmpty()).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        }.getOrNull())
        DatePickerDialog(
            onDismissRequest = { dateField = null },
            confirmButton = {
                TextButton(onClick = {
                    picker.selectedDateMillis?.let {
                        values[id] = Instant.fromEpochMilliseconds(it)
                            .toLocalDateTime(TimeZone.UTC).date.toString()
                    }; dateField = null
                }) { Text("Seleccionar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    dateField = null
                }) { Text("Cancelar") }
            }) { DatePicker(picker) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceField(
    label: String,
    value: String,
    choices: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = it }) {
        OutlinedTextField(
            value = choices.find { it.first == value }?.second ?: "Seleccionar",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(14.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            choices.forEach { (key, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = { onSelect(key); expanded = false })
            }
        }
    }
}

@Composable
fun PageHeading(title: String, subtitle: String = "") {
    Text(title, style = MaterialTheme.typography.headlineMedium)
    if (subtitle.isNotBlank()) Text(
        subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun SchoolCard(
    title: String,
    subtitle: String = "",
    badge: String = "",
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (badge.isNotBlank()) {
                val important = badge.contains("Vencido", true) || badge.contains(
                    "Ausente",
                    true
                ) || badge.contains("IMPORTANTE")
                Surface(
                    color = if (important) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        badge,
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (important) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle.isNotBlank()) Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

@Composable
fun Metrics(vararg metrics: Pair<String, String>) {
    metrics.toList().chunked(2).forEach { pair ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            pair.forEachIndexed { index, (value, label) ->
                Card(
                    Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = if (index == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            value,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Filters(selected: String, options: List<String>, onSelect: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    SchoolCard("Sin información para mostrar", message) {
        Icon(
            Icons.Outlined.CheckCircle,
            null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ActionRow(vararg actions: Pair<String, () -> Unit>) {
    actions.forEach { (label, action) ->
        OutlinedButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = RoundedCornerShape(14.dp)
        ) { Text(label) }
    }
}

@Composable
fun QuickActions(vararg actions: Pair<String, () -> Unit>) {
    actions.toList().chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            row.forEach { (label, action) ->
                val icon = when {
                    label.contains("tarea", true) -> Icons.AutoMirrored.Outlined.Assignment
                    label.contains("nota", true) || label.contains(
                        "calific",
                        true
                    ) -> Icons.Outlined.Grade

                    label.contains("asistencia", true) || label.contains(
                        "ingreso",
                        true
                    ) -> Icons.Outlined.CheckCircle

                    label.contains("pago", true) -> Icons.Outlined.Payments
                    label.contains("tutor", true) -> Icons.Outlined.Groups
                    else -> Icons.Outlined.Dashboard
                }
                OutlinedCard(
                    onClick = action,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            Modifier.size(40.dp).background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp)
                            ), contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                icon,
                                null,
                                Modifier.size(22.dp),
                                MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            null,
                            Modifier.size(18.dp),
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}
