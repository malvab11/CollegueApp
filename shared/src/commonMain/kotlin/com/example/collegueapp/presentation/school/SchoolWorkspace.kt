package com.example.collegueapp.presentation.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegueapp.data.school.AttendanceStatus
import com.example.collegueapp.data.school.SchoolAccount
import com.example.collegueapp.data.school.SchoolAccounts
import com.example.collegueapp.data.school.SchoolRole
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.platform.SchoolBackHandler
import com.example.collegueapp.platform.fullDate
import com.example.collegueapp.platform.nextMonday
import com.example.collegueapp.platform.now
import com.example.collegueapp.platform.value
import com.example.collegueapp.ui.theme.SchoolNavy
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

private data class WorkspaceTab(val key: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolWorkspace(account: SchoolAccount, store: SchoolStore, onLogout: () -> Unit) {
    val data = store.data
    var page by rememberSaveable(account.id) { mutableStateOf("home") }
    var pupilId by rememberSaveable(account.id) {
        mutableStateOf(
            store.engine().pupils(account).firstOrNull()?.id.orEmpty()
        )
    }
    var form by remember { mutableStateOf<SchoolForm?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pupils = store.engine().pupils(account)
    LaunchedEffect(pupils.map { it.id }) {
        if (pupils.none { it.id == pupilId }) pupilId = pupils.firstOrNull()?.id.orEmpty()
    }
    val tabs = listOf(WorkspaceTab("home", "Inicio", Icons.Outlined.Home)) + when (account.role) {
        SchoolRole.STUDENT -> listOf(
            WorkspaceTab("academic", "Académico", Icons.Outlined.School),
            WorkspaceTab("attendance", "Asistencia", Icons.AutoMirrored.Outlined.FactCheck)
        )

        SchoolRole.PARENT -> listOf(
            WorkspaceTab("academic", "Académico", Icons.Outlined.School),
            WorkspaceTab("payments", "Pagos", Icons.Outlined.Payments)
        )

        SchoolRole.TEACHER -> listOf(
            WorkspaceTab("academic", "Cursos", Icons.Outlined.School),
            WorkspaceTab("attendance", "Asistencia", Icons.AutoMirrored.Outlined.FactCheck)
        )

        SchoolRole.TUTOR -> listOf(
            WorkspaceTab("academic", "Cursos", Icons.Outlined.School),
            WorkspaceTab("tutor", "Tutoría", Icons.Outlined.Groups)
        )

        SchoolRole.ASSISTANT -> listOf(
            WorkspaceTab(
                "attendance",
                "Registro",
                Icons.AutoMirrored.Outlined.FactCheck
            ), WorkspaceTab("history", "Historial", Icons.Outlined.History)
        )

        SchoolRole.ADMIN -> listOf(
            WorkspaceTab("management", "Gestión", Icons.Outlined.School),
            WorkspaceTab("payments", "Pagos", Icons.Outlined.Payments)
        )
    } + listOf(
        WorkspaceTab("messages", "Mensajes", Icons.Outlined.Notifications),
        WorkspaceTab("profile", "Perfil", Icons.Outlined.Person)
    )
    val selectedTab = when (page) {
        "tasks", "grades", "schedule" -> "academic"; "notifications" -> "messages"; "support" -> "profile"; else -> page
    }
    val unread = data.alerts.count { account.id in it.recipientIds && account.id !in it.readBy }
    SchoolBackHandler(enabled = page != "home") { page = "home" }
    BoxWithConstraints {
        val wide = maxWidth >= 840.dp
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SchoolLogo(Modifier.size(44.dp))
                            Column {
                                Text("Collegue", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    SchoolName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (page != "home") IconButton(onClick = {
                            page = "home"
                        }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Volver al inicio") }
                    },
                    actions = {
                        IconButton(onClick = { page = "notifications" }) {
                            BadgedBox(badge = { if (unread > 0) Badge { Text(unread.toString()) } }) {
                                Icon(
                                    Icons.Outlined.Notifications,
                                    "Notificaciones"
                                )
                            }
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Logout,
                                "Cerrar sesión"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                if (!wide) NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab.key,
                            onClick = { page = tab.key },
                            icon = { Icon(tab.icon, null) },
                            label = {
                                Text(
                                    tab.label,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            })
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (wide) NavigationRail(
                    containerColor = MaterialTheme.colorScheme.background,
                    header = { SchoolLogo(Modifier.padding(12.dp).size(52.dp)) }) {
                    tabs.forEach { tab ->
                        NavigationRailItem(
                            selected = selectedTab == tab.key,
                            onClick = { page = tab.key },
                            icon = { Icon(tab.icon, null) },
                            label = { Text(tab.label) })
                    }
                }
                Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                    key(page, pupilId) {
                        Column(
                            Modifier.widthIn(max = 920.dp).fillMaxWidth().fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = if (wide) 32.dp else 20.dp)
                                .padding(top = 12.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (account.role == SchoolRole.PARENT && page in listOf(
                                    "home",
                                    "academic",
                                    "tasks",
                                    "grades",
                                    "schedule",
                                    "attendance",
                                    "payments"
                                )
                            ) {
                                ChoiceField(
                                    "Mis hijos",
                                    pupilId,
                                    pupils.map {
                                        it.id to "${it.name} · ${
                                            sectionLabel(
                                                store.data,
                                                it.sectionId
                                            )
                                        }"
                                    }) { pupilId = it }
                                if (pupils.isEmpty()) EmptyState("Secretaría debe vincular un alumno a tu cuenta.")
                            }
                            when (page) {
                                "home" -> SchoolHomeContent(account, store, pupilId) { page = it }
                                "academic", "tasks", "grades", "schedule" -> AcademicContent(
                                    account,
                                    store,
                                    pupilId,
                                    when (page) {
                                        "tasks" -> "Tareas"; "grades" -> "Notas"; "schedule" -> "Horario"; else -> "Cursos"
                                    }
                                ) { form = it }

                                "attendance" -> AttendanceContent(
                                    account,
                                    store,
                                    pupilId,
                                    { form = it })

                                "history" -> AttendanceContent(
                                    account,
                                    store,
                                    pupilId,
                                    { form = it },
                                    historyOnly = true
                                )

                                "payments" -> PaymentsContent(account, store, pupilId) { form = it }
                                "messages", "notifications" -> MessagesContent(
                                    account,
                                    store,
                                    { form = it },
                                    if (page == "notifications") "Avisos" else "Comunicados"
                                )

                                "management" -> ManagementContent(account, store) { form = it }
                                "tutor" -> TutorContent(account, store) { form = it }
                                "support" -> SupportContent(account, store) { form = it }
                                "profile" -> {
                                    PageHeading(
                                        "Mi perfil",
                                        "Tu información y responsabilidades en el colegio"
                                    )
                                    SchoolCard(account.name, account.username, account.role.label)
                                    SchoolCard(
                                        "Tu acceso",
                                        account.permissions.joinToString("\n") { "• ${it.label}" })
                                    SchoolCard(
                                        SchoolName,
                                        "$SchoolLocation · año escolar 2026\nTu comunidad educativa, conectada."
                                    )
                                    ActionRow(
                                        "Atención y soporte" to { page = "support" },
                                        "Cerrar sesión" to onLogout
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    form?.let {
        SchoolFormSheet(
            it,
            onDismiss = { form = null },
            onSaved = { scope.launch { snackbar.showSnackbar("Cambios guardados correctamente") } })
    }
}

@Composable
private fun WelcomeCard(name: String, date: String, context: String) {
    Row(
        Modifier.fillMaxWidth().background(
            Brush.linearGradient(listOf(SchoolNavy, Color(0xFF275883))),
            RoundedCornerShape(26.dp)
        ).padding(22.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(date, style = MaterialTheme.typography.labelSmall, color = Color(0xFFC6DCF5))
            Text("Hola, $name", style = MaterialTheme.typography.displayLarge, color = Color.White)
            Text(
                context,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        SchoolLogo(Modifier.size(56.dp))
    }
}

@Composable
private fun ImportantNotice(title: String, onOpen: () -> Unit) {
    OutlinedCard(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Campaign, null, tint = MaterialTheme.colorScheme.secondary)
            Column(Modifier.weight(1f)) {
                Text(
                    "Comunicado importante",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                ); Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            }
            Icon(Icons.Outlined.ChevronRight, null)
        }
    }
}

@Composable
fun SchoolHomeContent(
    account: SchoolAccount,
    store: SchoolStore,
    pupilId: String,
    onNavigate: (String) -> Unit
) {
    val data = store.data
    val engine = store.engine()
    val pupil = data.pupils.find { it.id == pupilId }
    val family = account.role in listOf(SchoolRole.STUDENT, SchoolRole.PARENT)
    val courses = engine.courses(account).filter { !family || it.sectionId == pupil?.sectionId }
    val courseIds = courses.map { it.id }
    val tasks =
        data.assignments.filter { it.courseId in courseIds && (!family || pupilId !in it.submittedBy) }
    WelcomeCard(
        account.name.substringBefore(' '),
        fullDate(LocalDate.now()),
        if (family) "${if (account.role == SchoolRole.PARENT) pupil?.name.orEmpty() + " · " else ""}${
            sectionLabel(
                data,
                pupil?.sectionId
            )
        }" else account.role.label
    )
    engine.notices(account).firstOrNull { it.urgent }
        ?.let { ImportantNotice(it.title) { onNavigate("messages") } }
    when (account.role) {
        SchoolRole.STUDENT, SchoolRole.PARENT -> {
            val marks =
                data.marks.filter { it.pupilId == pupilId && it.period == "IV bimestre" && it.courseId in courseIds }
            val records =
                data.attendance.filter { it.pupilId == pupilId && it.validated && it.courseId == null }
            val percent =
                if (records.isEmpty()) "—" else "${records.count { it.status != AttendanceStatus.ABSENT } * 100 / records.size}%"
            Metrics(
                "${tasks.size}" to "Tareas pendientes",
                if (marks.isEmpty()) "—" to "Promedio / 20" else decimal(marks.map { it.score }
                    .average()) to "Promedio / 20",
                percent to "Asistencia de ingreso",
                "${courses.size}" to "Cursos matriculados"
            )
            QuickActions(
                "Ver tareas pendientes" to { onNavigate("tasks") },
                "Consultar notas" to { onNavigate("grades") },
                "Consultar asistencia" to { onNavigate("attendance") })
            if (account.role == SchoolRole.PARENT) {
                val pending = engine.invoices(account).filter { it.pupilId == pupilId && !it.paid }
                SchoolCard(
                    "Estado de cuenta",
                    "${pending.size} cuotas pendientes",
                    money(pending.sumOf { it.amount })
                ) { ActionRow("Revisar pagos" to { onNavigate("payments") }) }
            }
        }

        SchoolRole.TEACHER, SchoolRole.TUTOR -> {
            Metrics(
                "${courses.size}" to "Cursos asignados",
                "${engine.pupils(account).size}" to "Alumnos vinculados",
                "${tasks.size}" to "Actividades publicadas",
                "${
                    data.assignments.filter { it.courseId in courseIds }
                        .sumOf { it.submittedBy.size }
                }" to "Entregas recibidas"
            )
            QuickActions(
                "Gestionar tareas" to { onNavigate("tasks") },
                "Registrar calificaciones" to { onNavigate("grades") },
                "Asistencia por curso" to { onNavigate("attendance") })
            if (account.role == SchoolRole.TUTOR) ActionRow("Seguimiento de tutoría" to {
                onNavigate(
                    "tutor"
                )
            })
        }

        SchoolRole.ASSISTANT -> {
            val records = data.attendance.filter {
                it.date == LocalDate.now().toString() && it.courseId == null
            }
            Metrics(
                "${records.size}/${data.pupils.count { it.active }}" to "Ingreso registrado hoy",
                "${records.count { !it.validated }}" to "Pendientes de validar",
                "${records.count { it.status == AttendanceStatus.LATE }}" to "Tardanzas",
                if (data.extraAssistant) "2" to "Puntos habilitados" else "1" to "Puntos habilitados"
            )
            QuickActions(
                "Registrar ingreso por grupo" to { onNavigate("attendance") },
                "Consultar historial" to { onNavigate("history") })
            SchoolCard(
                "Antes de avisar a las familias",
                "Completa el registro del grupo y valida la jornada. No se envían alertas de ausencias o tardanzas mientras el registro está en borrador."
            )
        }

        SchoolRole.ADMIN -> {
            Metrics(
                "${data.pupils.count { it.active }}" to "Alumnos matriculados",
                "${data.sections.size}" to "Aulas activas",
                money(data.invoices.filter { !it.paid }
                    .sumOf { it.amount }) to "Saldo por regularizar",
                "${data.tickets.count { !it.resolved }}" to "Solicitudes abiertas"
            )
            QuickActions(
                "Gestionar matrículas y aulas" to { onNavigate("management") },
                "Regularizar pagos" to { onNavigate("payments") },
                "Atender solicitudes" to { onNavigate("support") })
        }
    }
    if (account.role !in listOf(SchoolRole.ADMIN, SchoolRole.ASSISTANT)) {
        val today = LocalDate.now()
        val next = if (today.dayOfWeek.value > 5) today.nextMonday() else today
        val lessons =
            data.lessons.filter { it.courseId in courseIds && it.weekday == next.dayOfWeek.value }
                .sortedBy { it.start }
        Text(
            if (next == today) "Clases de hoy" else "Próxima jornada · ${dateLabel(next.toString())}",
            style = MaterialTheme.typography.titleMedium
        )
        if (lessons.isEmpty()) EmptyState("Sin clases programadas para esta jornada.")
        lessons.take(3).forEach { lesson ->
            val course = data.courses.first { it.id == lesson.courseId }; SchoolCard(
            course.name,
            "${course.room} · ${SchoolAccounts.all.first { it.id == course.teacherId }.name}",
            "${lesson.start} – ${lesson.end}"
        )
        }
        ActionRow("Ver horario completo" to { onNavigate("schedule") })
    }
}
