package com.example.collegueapp.presentation.students.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegueapp.domain.model.student.ClassModel
import com.example.collegueapp.presentation.students.viewmodels.StudentsViewModel
import com.example.collegueapp.ui.theme.Primary
import com.example.collegueapp.ui.theme.Success

@Composable
fun StudentHomeScreen(
    paddingValues: PaddingValues,
    horizontalPadding: Dp,
    userId: String,
    onLogout: () -> Unit,
    studentsViewModel: StudentsViewModel = hiltViewModel()
) {

    val uiState by studentsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        studentsViewModel.loadAllClasses(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DashboardTopBar(
            userName = "Marlon",
            dateText = "Lunes, 24 de octubre",
            onLogout = onLogout,
        )

        when (uiState) {
            is StudentHomeUiState.Loading -> Text("Cargando clases...")
            is StudentHomeUiState.Error -> Text("Error al cargar clases")
            is StudentHomeUiState.Success -> {
                TodayClassesCard(
                    classes = (uiState as StudentHomeUiState.Success).classes
                )
            }

            else -> Unit
        }

        TasksCard()
        RecentGradesCard(
            listOf(
                GradeItem("Ciencias", "Reporte de Lab.", "14 / 20", Success),
                GradeItem("Literatura", "Análisis de Libro", "12 / 20", Primary),
                GradeItem("Arte", "Proyecto de Escultura", "10 / 20", Success),
                GradeItem("Educación Física", "Examen práctico", "8.5 / 20", Primary)
            )
        )
    }
}


@Composable
fun DashboardTopBar(
    onLogout: () -> Unit,
    userName: String = "Marlon",
    dateText: String = "Lunes, 24 de octubre",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, $userName",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun TodayClassesCard(classes: List<ClassModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp), // Matches mt-4
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Matches shadow-sm
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Matches p-4
        ) {
            Text(
                text = "Clases de Hoy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp) // Matches mb-4
            )
            classes.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index < classes.size - 1) 16.dp else 0.dp), // Matches pb-4
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Matches gap-x-3
                ) {
                    // Timeline
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (1 == 1) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp) // Matches text-2xl
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(8.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(8.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp) // Matches text-2xl
                                )
                            }
                            if (index < classes.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(8.dp)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                            }
                        }
                    }
                    // Content
                    Column {
                        Text(
                            text = item.clase,
                            style = if (1 == 1) MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ) else MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.horas + " - " + item.profesor + " - " + item.tipoAula + " " + item.aula,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TasksCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text("Tareas Pendientes", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Para Hoy",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(2.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Próximas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        HorizontalDivider()

        Spacer(Modifier.height(16.dp))

        TaskRow("Hoja de trabajo - Capítulo 5", "Matemáticas", urgent = true)
        TaskRow("Leer \"Cien Años de Soledad\"", "Literatura")
    }
}

@Composable
fun TaskRow(title: String, subject: String, urgent: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(3.dp))
            Text(
                subject,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
        if (urgent) {
            Box(
                modifier = Modifier
                    .clip(shape = MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(
                    "Urgente",
                    modifier = Modifier.padding(6.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

data class GradeItem(
    val subject: String,
    val title: String,
    val score: String,
    val color: Color // normalmente Primary o Success
)

@Composable
fun GradeCard(item: GradeItem) {
    Card(
        modifier = Modifier
            .width(160.dp) // Matches w-40
            .padding(end = 12.dp), // Matches gap-3
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp), // Matches p-3
            verticalArrangement = Arrangement.spacedBy(8.dp) // Matches gap-2
        ) {
            Text(
                text = item.subject,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.score.split("/")[0].trim(),
                    style = MaterialTheme.typography.titleLarge,
                    color = item.color
                )
                Text(
                    text = " / ${item.score.split("/")[1].trim()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecentGradesCard(grades: List<GradeItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp) // Matches mt-6
    ) {
        Text(
            text = "Calificaciones Recientes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp) // Matches px-1 mb-3
        )
        LazyRow(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 0.dp
            ), // Matches -mx-4 px-4
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Matches gap-3
        ) {
            items(items = grades) { grade ->
                GradeCard(grade)
            }
        }
    }
}




