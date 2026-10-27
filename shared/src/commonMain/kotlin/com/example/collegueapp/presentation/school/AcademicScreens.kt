package com.example.collegueapp.presentation.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.collegueapp.data.school.Attendance
import com.example.collegueapp.data.school.AttendanceStatus
import com.example.collegueapp.data.school.Mark
import com.example.collegueapp.data.school.SchoolAccount
import com.example.collegueapp.data.school.SchoolAccounts
import com.example.collegueapp.data.school.SchoolCourse
import com.example.collegueapp.data.school.SchoolRole
import com.example.collegueapp.data.school.SchoolStore
import com.example.collegueapp.platform.minusDays
import com.example.collegueapp.platform.now
import com.example.collegueapp.platform.plusDays
import com.example.collegueapp.platform.plusWeeks
import com.example.collegueapp.platform.startOfWeek
import com.example.collegueapp.platform.value
import kotlinx.datetime.LocalDate

@Composable
fun AcademicContent(
    account: SchoolAccount,
    store: SchoolStore,
    pupilId: String,
    initial: String = "Cursos",
    onForm: (SchoolForm) -> Unit
) {
    val data = store.data
    val teaching = account.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR)
    var tab by rememberSaveable(initial) { mutableStateOf(initial) }
    var courseFilter by rememberSaveable { mutableStateOf("all") }
    val courses = store.engine().courses(account)
        .filter { c -> teaching || data.pupils.find { it.id == pupilId }?.sectionId == c.sectionId }
    val selectedCourses = courses.filter { courseFilter == "all" || it.id == courseFilter }
    val ids = selectedCourses.map { it.id }
    PageHeading(
        if (teaching) "Mis cursos" else "Mi vida escolar",
        if (teaching) "Solo cursos asignados a tu cuenta" else "Año escolar 2026 · información del alumno seleccionado"
    )
    Filters(tab, listOf("Cursos", "Tareas", "Notas", "Horario", "Asistencia")) { tab = it }
    if (teaching && courses.isNotEmpty()) ChoiceField(
        "Curso",
        courseFilter,
        listOf("all" to "Todos mis cursos") + courses.map { it.id to "${it.name} · ${data.sections.first { s -> s.id == it.sectionId }.label}" }) {
        courseFilter = it
    }
    when (tab) {
        "Cursos" -> {
            if (selectedCourses.isEmpty()) EmptyState("No hay cursos asignados.")
            selectedCourses.forEach { c ->
                SchoolCard(
                    c.name,
                    "${SchoolAccounts.all.first { it.id == c.teacherId }.name} · ${c.room}",
                    data.sections.first { it.id == c.sectionId }.label
                ) {
                    Text(
                        "${data.pupils.count { it.sectionId == c.sectionId && it.active }} alumnos matriculados",
                        style = MaterialTheme.typography.bodySmall
                    )
                    ActionRow("Ver tareas de ${c.name}" to { courseFilter = c.id; tab = "Tareas" })
                }
            }
        }

        "Tareas" -> {
            val tasks = data.assignments.filter { it.courseId in ids }
            if (teaching && selectedCourses.isNotEmpty()) Button(onClick = {
                onForm(
                    taskForm(
                        account,
                        store,
                        selectedCourses
                    )
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Publicar tarea") }
            if (tasks.isEmpty()) EmptyState("No hay tareas para este curso.")
            tasks.sortedBy { it.due }.forEach { task ->
                val course = data.courses.first { it.id == task.courseId }
                val roster = data.pupils.filter { it.sectionId == course.sectionId && it.active }
                val submitted = pupilId in task.submittedBy
                SchoolCard(
                    task.title,
                    "${course.name} · entrega ${dateLabel(task.due)}",
                    if (teaching) "${roster.count { it.id in task.submittedBy }}/${roster.size} entregas" else if (submitted) "Entregada" else if (task.due < LocalDate.now()
                            .toString()
                    ) "Vencida · pendiente" else "Pendiente"
                ) {
                    Text(task.details, style = MaterialTheme.typography.bodySmall)
                    if (teaching) ActionRow("Revisar entregas" to {
                        onForm(
                            SchoolForm(
                                "Entregas de la tarea",
                                body = roster.joinToString("\n\n") { "${it.name}: ${if (it.id in task.submittedBy) "Entregada" else "Pendiente"}" },
                                confirm = "Entendido",
                                submit = {})
                        )
                    })
                    else if (account.role == SchoolRole.STUDENT && !submitted) Button(onClick = {
                        onForm(
                            SchoolForm(
                                "Entregar tarea",
                                body = "Se registrará la entrega de «${task.title}».",
                                confirm = "Confirmar entrega",
                                submit = { store.mutate { submitTask(account, task.id) } })
                        )
                    }) { Text("Entregar tarea") }
                }
            }
        }

        "Notas" -> GradesContent(account, store, pupilId, ids, onForm)
        "Horario" -> ScheduleContent(account, store, pupilId, ids, onForm)
        "Asistencia" -> AttendanceContent(account, store, pupilId, onForm)
    }
}

private fun taskForm(account: SchoolAccount, store: SchoolStore, courses: List<SchoolCourse>) =
    SchoolForm(
        "Publicar tarea",
        listOf(
            FormField(
                "course",
                "Curso",
                courses.first().id,
                courses.map { it.id to "${it.name} · ${sectionLabel(store.data, it.sectionId)}" }),
            FormField("title", "Título"), FormField("detail", "Indicaciones", multiline = true),
            FormField("due", "Entrega (AAAA-MM-DD)", LocalDate.now().plusDays(3).toString())
        ),
        confirm = "Publicar",
        submit = { v ->
            store.mutate {
                publishTask(
                    account,
                    v.getValue("course"),
                    v.getValue("title"),
                    v.getValue("detail"),
                    v.getValue("due")
                )
            }
        })

@Composable
fun GradesContent(
    account: SchoolAccount,
    store: SchoolStore,
    pupilId: String,
    courseIds: List<String>,
    onForm: (SchoolForm) -> Unit
) {
    val data = store.data
    var period by rememberSaveable { mutableStateOf("IV bimestre") }
    val teaching = account.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR)
    Filters(period, listOf("III bimestre", "IV bimestre")) { period = it }
    val marks =
        data.marks.filter { it.period == period && it.courseId in courseIds && (teaching || it.pupilId == pupilId) }
    if (marks.isNotEmpty() && !teaching) Metrics(
        "${
            decimal(marks.map { it.score }.average())
        }/20" to "Promedio del bimestre",
        "${marks.count { it.score >= 11 }}/${marks.size}" to "Evaluaciones aprobadas"
    )
    if (teaching && courseIds.isNotEmpty()) Button(onClick = {
        onForm(
            markForm(
                account,
                store,
                courseIds.first(),
                period
            )
        )
    }, modifier = Modifier.fillMaxWidth()) { Text("Registrar nota") }
    if (marks.isEmpty()) EmptyState("Aún no hay notas registradas en este periodo.")
    marks.sortedBy { it.courseId }.forEach { mark ->
        val pupil = data.pupils.first { it.id == mark.pupilId }
        SchoolCard(
            data.courses.first { it.id == mark.courseId }.name,
            "${if (teaching) "${pupil.name} · " else ""}${mark.assessment}",
            "${decimal(mark.score)} / 20 · ${if (mark.score >= 11) "Aprobado" else "En seguimiento"}"
        ) {
            if (teaching) ActionRow("Rectificar nota de ${pupil.name}" to {
                onForm(
                    markForm(
                        account,
                        store,
                        mark.courseId,
                        period,
                        mark
                    )
                )
            })
        }
    }
}

private fun markForm(
    account: SchoolAccount,
    store: SchoolStore,
    courseId: String,
    period: String,
    existing: Mark? = null
): SchoolForm {
    val courses = store.engine().courses(account)
    val course = store.data.courses.first { it.id == courseId }
    val roster = store.data.pupils.filter { it.sectionId == course.sectionId && it.active }
    return SchoolForm(
        if (existing == null) "Registrar nota" else "Rectificar nota",
        listOf(
            FormField(
                "course",
                "Curso",
                courseId,
                if (existing == null) courses.map {
                    it.id to "${it.name} · ${
                        sectionLabel(
                            store.data,
                            it.sectionId
                        )
                    }"
                } else listOf(course.id to course.name)),
            FormField(
                "pupil",
                "Alumno (debe pertenecer al curso)",
                existing?.pupilId ?: roster.firstOrNull()?.id.orEmpty(),
                store.engine().pupils(account)
                    .map { it.id to "${it.name} · ${sectionLabel(store.data, it.sectionId)}" }),
            FormField(
                "period",
                "Periodo",
                period,
                listOf("III bimestre", "IV bimestre").map { it to it }),
            FormField("assessment", "Evaluación", existing?.assessment ?: "Práctica calificada"),
            FormField("score", "Nota de 0 a 20", existing?.score?.let { decimal(it) }.orEmpty()),
            FormField("reason", "Motivo de rectificación", multiline = true)
        ),
        body = "Las notas se califican sobre 20. Las rectificaciones requieren motivo y quedan en el historial.",
        submit = { v ->
            require(existing == null || (v["assessment"] == existing.assessment && v["pupil"] == existing.pupilId && v["period"] == existing.period)) { "Para rectificar, conserva alumno, evaluación y periodo originales." }
            store.mutate {
                saveMark(
                    account,
                    v.getValue("pupil"),
                    v.getValue("course"),
                    v.getValue("period"),
                    v.getValue("assessment"),
                    v.getValue("score").replace(',', '.').toDoubleOrNull()
                        ?: error("Ingresa una nota numérica."),
                    v.getValue("reason")
                )
            }
        })
}

@Composable
fun ScheduleContent(
    account: SchoolAccount,
    store: SchoolStore,
    pupilId: String,
    courseIds: List<String>,
    onForm: (SchoolForm) -> Unit
) {
    var weekOffset by rememberSaveable { mutableIntStateOf(0) }
    var weekday by rememberSaveable {
        mutableIntStateOf(
            LocalDate.now().dayOfWeek.value.coerceAtMost(
                5
            )
        )
    }
    val monday = LocalDate.now().startOfWeek().plusWeeks(weekOffset.toLong())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { weekOffset-- }) { Text("‹ Anterior") }
        TextButton(onClick = { weekOffset = 0 }) { Text("Semana actual") }
        TextButton(onClick = { weekOffset++ }) { Text("Siguiente ›") }
    }
    Text(
        "${dateLabel(monday.toString())} – ${dateLabel(monday.plusDays(4).toString())}",
        style = MaterialTheme.typography.bodySmall
    )
    val days = (1..5).map {
        "${
            listOf(
                "Lun",
                "Mar",
                "Mié",
                "Jue",
                "Vie"
            )[it - 1]
        } ${monday.plusDays((it - 1).toLong()).dayOfMonth}"
    }
    Filters(days[weekday - 1], days) { weekday = days.indexOf(it) + 1 }
    val lessons = store.data.lessons.filter { it.courseId in courseIds && it.weekday == weekday }
        .sortedBy { it.start }
    if (lessons.isEmpty()) EmptyState("Sin clases programadas para este día.")
    lessons.forEach { lesson ->
        val course = store.data.courses.first { it.id == lesson.courseId }
        SchoolCard(
            course.name,
            "${SchoolAccounts.all.first { it.id == course.teacherId }.name}\n${course.room} · ${
                sectionLabel(
                    store.data,
                    course.sectionId
                )
            }",
            "${lesson.start} – ${lesson.end}"
        ) {
            if (account.role == SchoolRole.ADMIN) ActionRow("Modificar horario" to {
                onForm(
                    SchoolForm(
                        "Modificar horario",
                        listOf(
                            FormField("start", "Inicio (HH:mm)", lesson.start),
                            FormField("end", "Fin (HH:mm)", lesson.end)
                        ),
                        body = "Se actualizará este bloque semanal y se registrará un aviso local para las familias.",
                        submit = { v ->
                            store.mutate {
                                updateLesson(
                                    account,
                                    lesson.id,
                                    v.getValue("start"),
                                    v.getValue("end")
                                )
                            }
                        })
                )
            })
        }
    }
}

@Composable
fun AttendanceContent(
    account: SchoolAccount,
    store: SchoolStore,
    pupilId: String,
    onForm: (SchoolForm) -> Unit,
    historyOnly: Boolean = false
) {
    val data = store.data
    val staff = account.role in listOf(
        SchoolRole.ADMIN,
        SchoolRole.ASSISTANT,
        SchoolRole.TEACHER,
        SchoolRole.TUTOR
    )
    val teacher = account.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR)
    val courses = store.engine().courses(account)
    var courseId by rememberSaveable { mutableStateOf(if (teacher) courses.firstOrNull()?.id.orEmpty() else "entry") }
    var sectionId by rememberSaveable {
        mutableStateOf(
            store.engine().sections(account).firstOrNull()?.id.orEmpty()
        )
    }
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var search by rememberSaveable { mutableStateOf("") }
    var selected by remember { mutableStateOf(setOf<String>()) }
    PageHeading(
        if (historyOnly) "Historial de asistencia" else "Asistencia",
        if (staff) "Registro por alumno o grupo · valida antes de avisar" else "Solo registros confirmados por el colegio"
    )
    val courseChoices =
        (if (!teacher) listOf("entry" to "Ingreso al colegio") else emptyList()) + courses.map {
            it.id to "Curso: ${it.name} · ${
                sectionLabel(
                    store.data,
                    it.sectionId
                )
            }"
        }
    if (account.role != SchoolRole.ASSISTANT || !staff) ChoiceField(
        "Tipo de asistencia",
        courseId,
        courseChoices
    ) { courseId = it; selected = emptySet() }
    val course = data.courses.find { it.id == courseId }
    val actualCourse = courseId.takeIf { it != "entry" && it.isNotBlank() }
    if (staff) {
        ChoiceField(
            "Aula",
            sectionId,
            store.engine().sections(account).map { it.id to it.label }) {
            sectionId = it; selected = emptySet()
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = {
                date = LocalDate.parse(date).minusDays(1).toString(); selected = emptySet()
            }) { Text("‹ Día anterior") }
            TextButton(onClick = {
                date = LocalDate.now().toString(); selected = emptySet()
            }) { Text("Hoy") }
            TextButton(
                enabled = date < LocalDate.now().toString(),
                onClick = {
                    date = LocalDate.parse(date).plusDays(1).toString(); selected = emptySet()
                }) { Text("Siguiente ›") }
        }
        Text(dateLabel(date), style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            search,
            { search = it },
            label = { Text("Buscar alumno o código") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
    val pupils = store.engine().pupils(account).filter { p ->
        if (staff) p.sectionId == sectionId && (course == null || p.sectionId == course.sectionId) && (p.name.contains(
            search,
            true
        ) || p.code.contains(search, true)) else p.id == pupilId
    }
    val records =
        data.attendance.filter { r -> r.pupilId in pupils.map { it.id } && r.courseId == actualCourse && (if (staff && !historyOnly) r.date == date else r.validated) }
    if (staff && !historyOnly) {
        Text(
            "${records.size}/${pupils.size} registros · ${records.count { !it.validated }} borradores",
            style = MaterialTheme.typography.bodySmall
        )
        if (account.role == SchoolRole.ASSISTANT) Text(
            if (data.extraAssistant) "2 puntos de ingreso habilitados" else "1 punto de ingreso habilitado",
            style = MaterialTheme.typography.labelSmall
        )
        if (pupils.isNotEmpty()) {
            TextButton(onClick = {
                selected =
                    if (pupils.all { it.id in selected }) emptySet() else pupils.map { it.id }
                        .toSet()
            }) { Text("Seleccionar / quitar grupo") }
            Button(
                enabled = selected.isNotEmpty(),
                onClick = {
                    onForm(
                        attendanceForm(
                            account,
                            store,
                            selected.toList(),
                            date,
                            actualCourse
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar grupo (${selected.size})") }
        }
        pupils.forEach { p ->
            val record = records.find { it.pupilId == p.id }
            SchoolCard(
                p.name,
                p.code,
                record?.let { "${it.status.label} · ${if (it.validated) "Validado" else "Borrador"}" }
                    ?: "Sin registro") {
                Row {
                    Checkbox(
                        checked = p.id in selected,
                        onCheckedChange = {
                            selected = if (it) selected + p.id else selected - p.id
                        })
                    TextButton(onClick = {
                        onForm(
                            attendanceForm(
                                account,
                                store,
                                listOf(p.id),
                                date,
                                actualCourse,
                                record
                            )
                        )
                    }) { Text(if (record == null) "Registrar" else "Corregir / justificar") }
                }
            }
        }
        if (data.attendance.any { it.date == date && it.courseId == actualCourse && !it.validated }) Button(
            onClick = {
                onForm(
                    SchoolForm(
                        "Validar jornada",
                        body = "Se validarán todos los borradores del $date para ${course?.name ?: "ingreso al colegio"}, incluidas otras aulas de este registro. Se crearán avisos de ausencias y tardanzas, sin duplicarlos.",
                        confirm = "Validar y generar avisos",
                        submit = {
                            store.mutate {
                                validateAttendance(
                                    account,
                                    date,
                                    actualCourse
                                )
                            }
                        })
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Validar jornada") }
        if (pupils.isEmpty()) EmptyState("No hay alumnos que coincidan con el aula y curso seleccionados.")
    } else {
        Metrics(
            "${records.count { it.status == AttendanceStatus.PRESENT }}" to "Presencias",
            "${records.count { it.status == AttendanceStatus.LATE }}" to "Tardanzas",
            "${records.count { it.status == AttendanceStatus.ABSENT }}" to "Ausencias",
            "${records.count { it.status == AttendanceStatus.EXCUSED }}" to "Justificaciones"
        )
        if (records.isEmpty()) EmptyState("No hay registros validados para esta selección.")
        records.sortedByDescending { it.date }.forEach { r ->
            SchoolCard(
                r.status.label,
                "${data.pupils.first { it.id == r.pupilId }.name} · ${dateLabel(r.date)}",
                if (r.courseId == null) "Ingreso al colegio" else "Asistencia de curso"
            ) {
                if (r.reason.isNotBlank()) Text(
                    r.reason,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun attendanceForm(
    account: SchoolAccount,
    store: SchoolStore,
    pupilIds: List<String>,
    date: String,
    courseId: String?,
    existing: Attendance? = null
) = SchoolForm(
    "Registrar asistencia",
    listOf(
        FormField(
            "status",
            "Estado",
            existing?.status?.name ?: AttendanceStatus.PRESENT.name,
            AttendanceStatus.entries.map { it.name to it.label }),
        FormField("reason", "Motivo / justificación", existing?.reason.orEmpty(), multiline = true)
    ),
    body = "${pupilIds.size} alumno(s) · ${dateLabel(date)}. Las correcciones de registros validados requieren motivo. Las alertas se generan al validar.",
    submit = { v ->
        store.mutate {
            recordAttendance(
                account,
                pupilIds,
                date,
                courseId,
                AttendanceStatus.valueOf(v.getValue("status")),
                v.getValue("reason")
            )
        }
    }
)
