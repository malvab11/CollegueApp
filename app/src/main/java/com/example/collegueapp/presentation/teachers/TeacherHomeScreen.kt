//package com.example.collegueapp.presentation.teachers
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//
//@Preview(showSystemUi = true)
//@Composable
//fun TeacherHomeScreen() {
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background),
//        contentPadding = PaddingValues(bottom = 90.dp)
//    ) {
//
//        item {
//            TopGreetingSection(
//                name = "Dr. Smith",
//                date = "Martes, 26 de Octubre",
//                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAlmYVl..."
//            )
//        }
//
//        item { StatsGridSection() }
//
//        item {
//            SectionTitle(
//                title = "Próximas Clases",
//                actionText = "Ver Todas"
//            )
//        }
//
//        item {
//            NextClassCard(
//                time = "10:00 AM - 11:30 AM",
//                className = "QUÍM-101",
//                location = "Salón 304",
//                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAtuVvQb..."
//            )
//        }
//
//        item {
//            NextClassCard(
//                time = "1:00 PM - 2:30 PM",
//                className = "FÍS-201",
//                location = "Lab 12",
//                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDED_Ax6u..."
//            )
//        }
//
//        item {
//            SectionTitle(
//                title = "Actividades Diarias",
//                actionText = "Ver Todas"
//            )
//        }
//
//        item { DailyActivityList() }
//    }
//
//    TeacherBottomBar()
//}
//
//@Composable
//private fun TopGreetingSection(name: String, date: String, imageUrl: String) {
//    Column(modifier = Modifier.padding(16.dp)) {
//
//        Row(
//            Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            AsyncImage(
//                model = imageUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(Modifier.weight(1f))
//
//            Icon(
//                imageVector = Icons.Outlined.Notifications,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onSurface
//            )
//        }
//
//        Text(
//            text = "Buenos días, $name",
//            style = MaterialTheme.typography.displayLarge,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//
//        Text(
//            text = date,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}
//
//@Composable
//private fun StatsGridSection() {
//    Column(Modifier.padding(16.dp)) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            StatCard("Asistencia de hoy", "92%", MaterialTheme.colorScheme.primary)
//            StatCard("Tareas Creadas", "12", MaterialTheme.colorScheme.tertiary)
//        }
//        Spacer(Modifier.height(12.dp))
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            StatCard("Próximas Clases", "2", MaterialTheme.colorScheme.onBackground)
//            StatCard("Actividades Diarias", "5", MaterialTheme.colorScheme.onBackground)
//        }
//    }
//}
//
//@Composable
//private fun StatCard(title: String, value: String, valueColor: Color) {
//    Surface(
//        shape = MaterialTheme.shapes.medium,
//        color = MaterialTheme.colorScheme.surface,
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
//        modifier = Modifier.weight(1f)
//    ) {
//        Column(Modifier.padding(16.dp)) {
//            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//            Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor)
//        }
//    }
//}
//
//
//@Composable
//private fun NextClassCard(time: String, className: String, location: String, imageUrl: String) {
//    Row(
//        modifier = Modifier
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
//            .padding(16.dp)
//    ) {
//        Column(Modifier.weight(1f)) {
//            Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//            Text(className, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
//            Text(location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//
//            Spacer(Modifier.height(12.dp))
//
//            Text(
//                "Ver Detalles",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier
//                    .clip(MaterialTheme.shapes.small)
//                    .clickable {}
//            )
//        }
//
//        AsyncImage(
//            model = imageUrl,
//            contentDescription = null,
//            modifier = Modifier
//                .size(80.dp)
//                .clip(MaterialTheme.shapes.medium),
//            contentScale = ContentScale.Crop
//        )
//    }
//}
//
//
//@Composable
//private fun TeacherBottomBar() {
//    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
//        listOf(
//            Icons.Outlined.Home to "Inicio",
//            Icons.Outlined.School to "Clases",
//            Icons.Outlined.Mail to "Mensajes",
//            Icons.Outlined.Person to "Perfil"
//        ).forEach { (icon, label) ->
//            NavigationBarItem(
//                selected = label == "Inicio",
//                onClick = {},
//                icon = { Icon(icon, null) },
//                label = { Text(label) },
//                colors = NavigationBarItemDefaults.colors(
//                    indicatorColor = Color.Transparent,
//                    selectedIconColor = MaterialTheme.colorScheme.primary,
//                    selectedTextColor = MaterialTheme.colorScheme.primary,
//                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            )
//        }
//    }
//}
