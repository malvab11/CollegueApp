package com.example.collegueapp.presentation.students.screens.grades

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
data class GradeItem2(
    val subject: String,
    val teacher: String,
    val grade: Double,
    val status: GradeStatus,
    val icon: @Composable () -> Unit
)
enum class GradeStatus { APPROVED, AT_RISK, FAILED }
@Composable
fun StudentGradesScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    horizontalPadding: Dp
) {

    val scrollState = rememberScrollState()

    // Selector de periodo
    var selectedPeriod by remember { mutableStateOf("2024-1") }

    // Lista de calificaciones
    val grades = listOf(
        GradeItem2(
            "Matemáticas Avanzadas",
            "Prof. Ana Torres",
            9.5,
            GradeStatus.APPROVED,
            icon = {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    tint = Color(0xFF207FDF)
                )
            }
        ),
        GradeItem2(
            "Historia Universal",
            "Prof. Carlos Ruiz",
            7.2,
            GradeStatus.AT_RISK,
            icon = {
                Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    tint = Color(0xFF207FDF)
                )
            }
        ),
        GradeItem2(
            "Ciencias Naturales",
            "Prof. Laura Gómez",
            9.1,
            GradeStatus.APPROVED,
            icon = {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    tint = Color(0xFF207FDF)
                )
            }
        ),
        GradeItem2(
            "Literatura Clásica",
            "Prof. Jorge Méndez",
            5.8,
            GradeStatus.FAILED,
            icon = {
                Icon(
                    Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = Color(0xFF207FDF)
                )
            }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues = paddingValues)
            .verticalScroll(scrollState)
    ) {

        // Selector de periodo
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedButton(
                onClick = { /* abrir selector */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            ) {
                Text(selectedPeriod, color = MaterialTheme.colorScheme.primary)
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Promedio general
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Promedio General",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "8.9/10",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF48BB78)
                        )
                        Text("¡Buen trabajo!", color = Color(0xFF48BB78), fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de asignaturas
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(horizontalPadding)
        ) {
            Text("Asignaturas", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            if (grades.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sin Calificaciones Aún", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Tus calificaciones para este período aún no están disponibles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                grades.forEach { grade ->
                    GradeCard(grade)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun GradeCard(gradeItem: GradeItem2) {
    val statusColor = when (gradeItem.status) {
        GradeStatus.APPROVED -> Color(0xFF48BB78)
        GradeStatus.AT_RISK -> Color(0xFFED8936)
        GradeStatus.FAILED -> Color(0xFFF56565)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* navegar a detalles */ },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    gradeItem.icon()
                }
                Column {
                    Text(gradeItem.subject, fontSize = 16.sp)
                    Text(gradeItem.teacher, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(gradeItem.grade.toString(), fontSize = 16.sp, color = statusColor)
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(statusColor, shape = CircleShape)
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}
