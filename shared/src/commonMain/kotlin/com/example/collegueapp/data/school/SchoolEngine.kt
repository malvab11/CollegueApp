package com.example.collegueapp.data.school

import com.example.collegueapp.platform.*
import kotlinx.datetime.LocalDate


/** Shared school rules. All mutations validate the actor and keep related records together. */
class SchoolEngine(initial: SchoolData = SchoolSeed.create()) {
    var data: SchoolData = initial
        private set

    private fun roleOf(actor: SchoolAccount): SchoolRole = if (actor.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR)) {
        if (data.sections.any { it.tutorId == actor.id }) SchoolRole.TUTOR else SchoolRole.TEACHER
    } else actor.role
    fun effectiveAccount(actor: SchoolAccount): SchoolAccount = actor.copy(role = roleOf(actor))
    private fun id() = randomId()
    private fun today() = LocalDate.now().toString()
    private fun audit(actor: SchoolAccount, action: String, detail: String) {
        data = data.copy(audit = listOf(AuditEntry(id(), actor.name, action, detail, currentTimestamp())) + data.audit)
    }
    private fun admin(actor: SchoolAccount) = require(SchoolPermission.SCHOOL_MANAGEMENT in actor.permissions) { "Esta acción corresponde a Secretaría." }
    fun sections(actor: SchoolAccount) = when (roleOf(actor)) {
        SchoolRole.ADMIN, SchoolRole.ASSISTANT -> data.sections
        SchoolRole.TUTOR -> data.sections.filter { it.tutorId == actor.id || data.courses.any { c -> c.sectionId == it.id && c.teacherId == actor.id } }
        SchoolRole.TEACHER -> data.sections.filter { s -> data.courses.any { it.sectionId == s.id && it.teacherId == actor.id } }
        else -> data.sections.filter { s -> pupils(actor).any { it.sectionId == s.id } }
    }
    fun pupils(actor: SchoolAccount): List<Pupil> = data.pupils.filter { p -> p.active && when (roleOf(actor)) {
        SchoolRole.STUDENT -> p.id == actor.pupilId
        SchoolRole.PARENT -> p.parentId == actor.id
        SchoolRole.TEACHER -> data.courses.any { it.sectionId == p.sectionId && it.teacherId == actor.id }
        SchoolRole.TUTOR -> data.sections.any { it.id == p.sectionId && it.tutorId == actor.id } || data.courses.any { it.sectionId == p.sectionId && it.teacherId == actor.id }
        SchoolRole.ADMIN, SchoolRole.ASSISTANT -> true
    } }
    fun courses(actor: SchoolAccount) = data.courses.filter { c -> when (roleOf(actor)) {
        SchoolRole.TEACHER, SchoolRole.TUTOR -> c.teacherId == actor.id
        else -> sections(actor).any { it.id == c.sectionId }
    } }
    fun notices(actor: SchoolAccount) = data.notices.filter { n ->
        (n.sectionId == null || sections(actor).any { it.id == n.sectionId }) &&
            (n.courseId == null || courses(actor).any { it.id == n.courseId } || actor.role == SchoolRole.ADMIN || (roleOf(actor) == SchoolRole.TUTOR && data.sections.any { it.id == n.sectionId && it.tutorId == actor.id }))
    }
    fun invoices(actor: SchoolAccount) = if (actor.role in listOf(SchoolRole.ADMIN, SchoolRole.PARENT))
        data.invoices.filter { i -> pupils(actor).any { it.id == i.pupilId } } else emptyList()

    private fun teach(actor: SchoolAccount, courseId: String): SchoolCourse {
        val course = data.courses.first { it.id == courseId }
        require(SchoolPermission.SCHOOL_MANAGEMENT in actor.permissions || (SchoolPermission.COURSEWORK in actor.permissions && course.teacherId == actor.id)) { "Curso no asignado a tu cuenta." }
        return course
    }
    private fun recipients(pupilIds: List<String>): List<String> = data.pupils.filter { it.id in pupilIds }.flatMap { pupil ->
        SchoolAccounts.all.filter { account ->
            (account.role == SchoolRole.PARENT && account.id == pupil.parentId) ||
                (account.role == SchoolRole.STUDENT && account.pupilId == pupil.id)
        }.map { it.id }
    }.distinct()
    private fun alert(key: String, title: String, body: String, targets: List<String>) {
        if (targets.isNotEmpty() && data.alerts.none { it.id == key })
            data = data.copy(alerts = listOf(SchoolAlert(key, title, body, targets, today())) + data.alerts)
    }
    fun dailyReminders() {
        val date = LocalDate.now()
        data.invoices.filter { !it.paid && it.due <= date.plusDays(5).toString() }.forEach { invoice ->
            val overdue = invoice.due < date.toString()
            alert("auto-payment-${invoice.id}-$overdue", if (overdue) "Cuota vencida" else "Cuota próxima a vencer", "${invoice.concept} · S/ ${invoice.amount} · vence ${invoice.due}", recipients(listOf(invoice.pupilId)).filter { it != "student" })
        }
        data.assignments.filter { it.due <= date.plusDays(2).toString() }.forEach { task ->
            val course = data.courses.first { it.id == task.courseId }
            val pending = data.pupils.filter { it.active && it.sectionId == course.sectionId && it.id !in task.submittedBy }
            alert("auto-task-${task.id}-${task.due}", "Entrega próxima o pendiente", "${task.title} · ${course.name} · entrega ${task.due}", recipients(pending.map { it.id }))
        }
    }
    fun publishTask(actor: SchoolAccount, courseId: String, title: String, detail: String, due: String) {
        val course = teach(actor, courseId)
        require(title.isNotBlank() && detail.isNotBlank()) { "Completa título e indicaciones." }
        require(!LocalDate.parse(due).isBefore(LocalDate.now())) { "La entrega debe ser hoy o una fecha futura." }
        val task = Assignment(id(), courseId, title.trim(), detail.trim(), due)
        data = data.copy(assignments = data.assignments + task)
        alert("task-${task.id}", "Nueva tarea de ${course.name}", "${task.title} · entrega $due", recipients(data.pupils.filter { it.sectionId == course.sectionId && it.active }.map { it.id }))
        audit(actor, "Tarea publicada", "${course.name}: ${task.title}")
    }
    fun submitTask(actor: SchoolAccount, taskId: String) {
        require(actor.role == SchoolRole.STUDENT) { "Solo el alumno puede entregar su tarea." }
        val pupil = pupils(actor).singleOrNull() ?: error("Tu cuenta no tiene una matrícula activa. Comunícate con Secretaría.")
        val task = data.assignments.first { it.id == taskId }
        require(data.courses.first { it.id == task.courseId }.sectionId == pupil.sectionId) { "Tarea de otra sección." }
        data = data.copy(assignments = data.assignments.map { if (it.id == taskId) it.copy(submittedBy = (it.submittedBy + pupil.id).distinct()) else it })
        audit(actor, "Tarea entregada", task.title)
    }
    fun saveMark(actor: SchoolAccount, pupilId: String, courseId: String, period: String, assessment: String, score: Double, reason: String) {
        val course = teach(actor, courseId)
        val pupil = data.pupils.first { it.id == pupilId && it.active }
        require(pupil.sectionId == course.sectionId) { "El alumno no pertenece a ese curso." }
        require(score.isFinite() && score in 0.0..20.0 && assessment.isNotBlank()) { "Ingresa una nota de 0 a 20 y una evaluación." }
        val previous = data.marks.find { it.pupilId == pupilId && it.courseId == courseId && it.period == period && it.assessment == assessment.trim() }
        require(previous == null || reason.isNotBlank()) { "Indica el motivo de la rectificación." }
        val mark = Mark(previous?.id ?: id(), pupilId, courseId, period, assessment.trim(), score)
        data = data.copy(marks = data.marks.filterNot { it.id == mark.id } + mark)
        audit(actor, if (previous == null) "Nota registrada" else "Nota rectificada", "${pupil.name} · ${course.name}: $score/20. $reason")
    }
    fun recordAttendance(actor: SchoolAccount, pupilIds: List<String>, date: String, courseId: String?, status: AttendanceStatus, reason: String) {
        require(!LocalDate.parse(date).isAfter(LocalDate.now())) { "No se puede registrar una fecha futura." }
        if (courseId == null) require(actor.role in listOf(SchoolRole.ASSISTANT, SchoolRole.ADMIN)) { "El ingreso lo registra el auxiliar." }
        else teach(actor, courseId)
        require(pupilIds.isNotEmpty()) { "Selecciona al menos un alumno." }
        val allowed = pupils(actor).filter { p -> courseId == null || data.courses.first { it.id == courseId }.sectionId == p.sectionId }.map { it.id }
        require(pupilIds.all { it in allowed }) { "La selección incluye alumnos no asignados." }
        val existing = data.attendance.filter { it.pupilId in pupilIds && it.date == date && it.courseId == courseId }
        require(existing.none { it.validated } || reason.isNotBlank()) { "Para corregir un registro validado, indica el motivo." }
        require(status != AttendanceStatus.EXCUSED || reason.isNotBlank()) { "Indica la justificación." }
        val replacements = pupilIds.distinct().map { pupilId ->
            Attendance(existing.find { it.pupilId == pupilId }?.id ?: id(), pupilId, date, courseId, status, false, reason.trim())
        }
        data = data.copy(attendance = data.attendance.filterNot { it.pupilId in pupilIds && it.date == date && it.courseId == courseId } + replacements)
        audit(actor, "Asistencia en borrador", "${pupilIds.distinct().size} alumnos · $date · ${status.label}. $reason")
    }
    fun validateAttendance(actor: SchoolAccount, date: String, courseId: String?) {
        if (courseId == null) require(actor.role in listOf(SchoolRole.ASSISTANT, SchoolRole.ADMIN)) { "Sin autorización para validar ingreso." } else teach(actor, courseId)
        val records = data.attendance.filter { it.date == date && it.courseId == courseId && !it.validated }
        require(records.isNotEmpty()) { "No hay registros pendientes de validar." }
        data = data.copy(attendance = data.attendance.map { if (it in records) it.copy(validated = true) else it })
        records.filter { it.status in listOf(AttendanceStatus.LATE, AttendanceStatus.ABSENT, AttendanceStatus.EXCUSED) }.forEach { r ->
            val pupil = data.pupils.first { it.id == r.pupilId }
            alert("attendance-${r.id}-${r.status}", "Asistencia validada: ${r.status.label}", "${pupil.name} · $date · ${if (courseId == null) "Ingreso al colegio" else "Asistencia de curso"}. ${r.reason}", recipients(listOf(r.pupilId)))
        }
        audit(actor, "Asistencia validada", "${records.size} registros · $date")
    }
    fun publishNotice(actor: SchoolAccount, title: String, body: String, sectionId: String?, courseId: String?, urgent: Boolean) {
        require(title.isNotBlank() && body.isNotBlank()) { "Completa título y mensaje." }
        if (actor.role != SchoolRole.ADMIN) {
            require(actor.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR)) { "No puedes publicar comunicados." }
            if (courseId != null) {
                val course = teach(actor, courseId)
                require(sectionId == course.sectionId) { "El curso debe pertenecer al aula seleccionada." }
            } else require(roleOf(actor) == SchoolRole.TUTOR && data.sections.any { it.id == sectionId && it.tutorId == actor.id }) { "Selecciona un curso asignado o tu aula de tutoría." }
        }
        val notice = Notice(id(), title.trim(), body.trim(), actor.name, today(), sectionId, courseId, urgent)
        data = data.copy(notices = listOf(notice) + data.notices)
        val targets = SchoolAccounts.all.filter { a -> notices(a).any { it.id == notice.id } }.map { it.id }
        alert("notice-${notice.id}", if (urgent) "Comunicado importante" else "Nuevo comunicado", notice.title, targets)
        audit(actor, "Comunicado publicado", notice.title)
    }
    fun readNotice(actor: SchoolAccount, noticeId: String) {
        require(notices(actor).any { it.id == noticeId }) { "Comunicado no disponible para esta cuenta." }
        data = data.copy(notices = data.notices.map { if (it.id == noticeId) it.copy(readBy = (it.readBy + actor.id).distinct()) else it })
    }
    fun readAlerts(actor: SchoolAccount) {
        data = data.copy(alerts = data.alerts.map { if (actor.id in it.recipientIds) it.copy(readBy = (it.readBy + actor.id).distinct()) else it })
    }
    fun enroll(actor: SchoolAccount, pupilId: String?, name: String, code: String, sectionId: String, parentId: String) {
        admin(actor)
        require(name.isNotBlank() && code.isNotBlank()) { "Completa nombre y código del alumno." }
        require(data.sections.any { it.id == sectionId }) { "Aula inválida." }
        require(data.pupils.none { it.code.equals(code.trim(), true) && it.id != pupilId }) { "Ese código ya está matriculado." }
        require(parentId in listOf("parent", "other-parent")) { "Selecciona un apoderado." }
        val pupil = Pupil(pupilId ?: id(), name.trim(), sectionId, parentId, code.trim())
        val old = data.pupils.find { it.id == pupilId }
        data = data.copy(pupils = data.pupils.filterNot { it.id == pupil.id } + pupil)
        audit(actor, if (old == null) "Matrícula registrada" else "Matrícula actualizada", "${pupil.name} · ${data.sections.first { it.id == sectionId }.label} · apoderado ${if (parentId == "parent") "Lucía Mendoza" else "Familia vinculada"}")
    }
    fun assignCourse(actor: SchoolAccount, courseId: String, teacherId: String) {
        admin(actor)
        require(SchoolAccounts.all.any { it.id == teacherId && it.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR) }) { "Docente inválido." }
        data = data.copy(courses = data.courses.map { if (it.id == courseId) it.copy(teacherId = teacherId) else it })
        audit(actor, "Asignación docente", "${data.courses.first { it.id == courseId }.name} → ${SchoolAccounts.all.first { it.id == teacherId }.name}")
    }
    fun createSection(actor: SchoolAccount, grade: String, name: String) {
        admin(actor)
        require(grade.isNotBlank() && name.isNotBlank()) { "Completa grado y sección." }
        require(data.sections.none { it.grade.equals(grade.trim(), true) && it.name.equals(name.trim(), true) }) { "Esta aula ya existe en el año escolar." }
        val section = SchoolSection(id(), grade.trim(), name.trim().uppercase(), "tutor")
        data = data.copy(sections = data.sections + section)
        audit(actor, "Aula creada", "2026 · ${section.label}")
    }
    fun createCourse(actor: SchoolAccount, name: String, sectionId: String, teacherId: String, room: String) {
        admin(actor)
        require(name.isNotBlank() && room.isNotBlank() && data.sections.any { it.id == sectionId }) { "Completa curso, aula y ambiente." }
        require(SchoolAccounts.all.any { it.id == teacherId && it.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR) }) { "Selecciona un docente." }
        require(data.courses.none { it.sectionId == sectionId && it.name.equals(name.trim(), true) }) { "El curso ya existe en esta aula." }
        data = data.copy(courses = data.courses + SchoolCourse(id(), name.trim(), teacherId, sectionId, room.trim()))
        audit(actor, "Curso creado", "$name · ${data.sections.first { it.id == sectionId }.label} · ${SchoolAccounts.all.first { it.id == teacherId }.name}")
    }
    fun assignTutor(actor: SchoolAccount, sectionId: String, tutorId: String) {
        admin(actor)
        require(SchoolAccounts.all.any { it.id == tutorId && it.role in listOf(SchoolRole.TEACHER, SchoolRole.TUTOR) }) { "Selecciona una cuenta docente." }
        data = data.copy(sections = data.sections.map { if (it.id == sectionId) it.copy(tutorId = tutorId) else it })
        audit(actor, "Tutor asignado", "${data.sections.first { it.id == sectionId }.label} → ${SchoolAccounts.all.first { it.id == tutorId }.name}")
    }
    fun setExtraAssistant(actor: SchoolAccount, enabled: Boolean) {
        admin(actor)
        data = data.copy(extraAssistant = enabled)
        audit(actor, "Punto de ingreso", if (enabled) "Segundo punto de registro habilitado" else "Segundo punto deshabilitado")
    }
    fun updateLesson(actor: SchoolAccount, lessonId: String, start: String, end: String) {
        admin(actor)
        val pattern = Regex("([01][0-9]|2[0-3]):[0-5][0-9]")
        require(start.matches(pattern) && end.matches(pattern) && start < end) { "Usa HH:mm; la hora final debe ser posterior." }
        val lesson = data.lessons.first { it.id == lessonId }
        val course = data.courses.first { it.id == lesson.courseId }
        require(data.lessons.none { other -> other.id != lessonId && other.weekday == lesson.weekday && start < other.end && end > other.start && data.courses.first { it.id == other.courseId }.let { it.sectionId == course.sectionId || it.teacherId == course.teacherId } }) { "Este horario se cruza con otra clase del aula o del docente." }
        data = data.copy(lessons = data.lessons.map { if (it.id == lessonId) it.copy(start = start, end = end) else it })
        alert("schedule-${id()}", "Cambio de horario", "${course.name}: $start–$end", recipients(data.pupils.filter { it.sectionId == course.sectionId }.map { it.id }) + course.teacherId)
        audit(actor, "Horario actualizado", "${course.name} · $start–$end")
    }
    fun addLesson(actor: SchoolAccount, courseId: String, weekday: Int, start: String, end: String) {
        admin(actor)
        require(weekday in 1..5) { "Selecciona un día de lunes a viernes." }
        val pattern = Regex("([01][0-9]|2[0-3]):[0-5][0-9]")
        require(start.matches(pattern) && end.matches(pattern) && start < end) { "Usa HH:mm; la hora final debe ser posterior." }
        val course = data.courses.first { it.id == courseId }
        require(data.lessons.none { other -> other.weekday == weekday && start < other.end && end > other.start && data.courses.first { it.id == other.courseId }.let { it.sectionId == course.sectionId || it.teacherId == course.teacherId } }) { "Este horario se cruza con otra clase del aula o del docente." }
        val lesson = Lesson(id(), courseId, weekday, start, end)
        data = data.copy(lessons = data.lessons + lesson)
        alert("lesson-${lesson.id}", "Nuevo horario", "${course.name}: $start–$end", recipients(data.pupils.filter { it.sectionId == course.sectionId }.map { it.id }) + course.teacherId)
        audit(actor, "Bloque de horario creado", "${course.name} · día $weekday · $start–$end")
    }
    fun regularize(actor: SchoolAccount, invoiceId: String, evidence: String, paid: Boolean = true) {
        admin(actor)
        require(evidence.trim().length >= 4) { "Indica comprobante o motivo de corrección (mínimo 4 caracteres)." }
        val invoice = data.invoices.first { it.id == invoiceId }
        data = data.copy(invoices = data.invoices.map { if (it.id == invoiceId) it.copy(paid = paid, evidence = evidence.trim()) else it })
        alert("payment-${id()}", "Estado de pago actualizado", "${invoice.concept}: ${if (paid) "regularizado" else "pendiente"}.", recipients(listOf(invoice.pupilId)))
        audit(actor, if (paid) "Pago regularizado" else "Pago corregido", "${invoice.concept} · S/ ${invoice.amount} · evidencia: $evidence")
    }
    fun paymentReminders(actor: SchoolAccount) {
        admin(actor)
        data.invoices.filter { !it.paid }.forEach { i ->
            alert("reminder-${i.id}-${today()}", if (i.due < today()) "Cuota vencida" else "Recordatorio de cuota", "${i.concept} · S/ ${i.amount} · vence ${i.due}", recipients(listOf(i.pupilId)).filter { it != "student" })
        }
        audit(actor, "Recordatorios de pago", "Avisos de cuotas pendientes actualizados. Se omitieron los duplicados.")
    }
    fun incident(actor: SchoolAccount, pupilId: String, detail: String) {
        val pupil = data.pupils.first { it.id == pupilId }
        require(actor.role == SchoolRole.ADMIN || (roleOf(actor) == SchoolRole.TUTOR && data.sections.any { it.id == pupil.sectionId && it.tutorId == actor.id })) { "Solo el tutor de esta aula puede registrar incidencias." }
        require(detail.isNotBlank()) { "Describe la incidencia." }
        data = data.copy(incidents = listOf(Incident(id(), pupilId, detail.trim(), today())) + data.incidents)
        audit(actor, "Incidencia registrada", "${pupil.name}: $detail")
    }
    fun resolveIncident(actor: SchoolAccount, incidentId: String) {
        val item = data.incidents.first { it.id == incidentId }
        val pupil = data.pupils.first { it.id == item.pupilId }
        require(actor.role == SchoolRole.ADMIN || (roleOf(actor) == SchoolRole.TUTOR && data.sections.any { it.id == pupil.sectionId && it.tutorId == actor.id })) { "Incidencia de otra tutoría." }
        data = data.copy(incidents = data.incidents.map { if (it.id == incidentId) it.copy(resolved = true) else it })
        audit(actor, "Incidencia atendida", item.detail)
    }
    fun ticket(actor: SchoolAccount, subject: String, detail: String) {
        require(subject.isNotBlank() && detail.isNotBlank()) { "Completa asunto y detalle." }
        val ticket = SupportTicket(id(), actor.id, subject.trim(), detail.trim(), today())
        data = data.copy(tickets = listOf(ticket) + data.tickets)
        alert("ticket-${ticket.id}", "Nueva solicitud de atención", "${actor.name}: $subject", listOf("admin"))
        audit(actor, "Solicitud de atención", subject)
    }
    fun resolveTicket(actor: SchoolAccount, ticketId: String) {
        admin(actor)
        val ticket = data.tickets.first { it.id == ticketId }
        data = data.copy(tickets = data.tickets.map { if (it.id == ticketId) it.copy(resolved = true) else it })
        alert("resolved-${ticket.id}", "Solicitud atendida", "${ticket.subject}. Acércate a Secretaría para conocer el detalle de la atención.", listOf(ticket.accountId))
        audit(actor, "Solicitud atendida", ticket.subject)
    }
}
