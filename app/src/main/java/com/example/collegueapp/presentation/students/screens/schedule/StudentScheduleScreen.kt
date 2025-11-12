package com.example.collegueapp.presentation.students.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ClassItem2(
    val startTime: String,
    val endTime: String,
    val subject: String,
    val teacher: String,
    val room: String,
    val color: Color,
    val isActive: Boolean = true
)

data class WeekDay(val dayLetter: String, val dayNumber: String)

@Composable
fun StudentScheduleScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    horizontalPadding: Dp
) {

    val weekDays = listOf(
        WeekDay("L", "15"),
        WeekDay("M", "16"),
        WeekDay("X", "17"),
        WeekDay("J", "18"),
        WeekDay("V", "19"),
        WeekDay("S", "20")
    )

    var selectedDayIndex by remember { mutableIntStateOf(0) }

    val classes = listOf(
        ClassItem2("08:00", "09:30", "Matemáticas Avanzadas", "Prof. García", "Aula 102", Color(0xFF3B82F6)),
        ClassItem2("09:45", "11:15", "Química General", "Prof. Torres", "Laboratorio 3", Color(0xFF10B981)),
        ClassItem2("11:30", "13:00", "Historia del Arte", "Prof. Reyes", "Aula 205", Color(0xFFF97316), isActive = false),
        ClassItem2("14:00", "15:30", "Literatura Universal", "Prof. Soto", "Biblioteca", Color(0xFFA855F7), isActive = false)
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Semana selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* retroceder semana */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Prev week")
            }
            Text(text = "15-21 ABRIL", fontSize = 14.sp)
            IconButton(onClick = { /* siguiente semana */ }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next week")
            }
        }

        // Tabs de días
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEachIndexed { index, day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedDayIndex = index }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        day.dayLetter,
                        fontSize = 12.sp,
                        color = if (index == selectedDayIndex) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Text(
                        day.dayNumber,
                        fontSize = 14.sp,
                        color = if (index == selectedDayIndex) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(24.dp)
                            .background(
                                if (index == selectedDayIndex) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de clases
        Column(
            verticalArrangement = Arrangement.spacedBy(horizontalPadding)
        ) {
            classes.forEach { classItem ->
                ClassCard(classItem)
            }
        }
    }
}

@Composable
fun ClassCard(classItem: ClassItem2) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (classItem.isActive) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(classItem.color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp) // ajusta según distancia entre clases
                    .background(classItem.color)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = classItem.subject, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${classItem.teacher} / ${classItem.room}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = classItem.startTime, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = classItem.endTime, fontSize = 12.sp)
        }
    }
}