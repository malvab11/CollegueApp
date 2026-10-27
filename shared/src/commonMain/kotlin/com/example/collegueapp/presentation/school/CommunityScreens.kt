package com.example.collegueapp.presentation.school

import com.example.collegueapp.platform.*
import kotlinx.datetime.LocalDate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.collegueapp.data.school.*

@Composable
fun MessagesContent(account: SchoolAccount, store: SchoolStore, onForm: (SchoolForm) -> Unit, initial: String = "Comunicados") {
    var tab by rememberSaveable(initial) { mutableStateOf(initial) }
    val data = store.data
    PageHeading("Centro de mensajes", "Comunicados e historial de avisos de tu cuenta")
    Filters(tab, listOf("Comunicados", "Avisos")) { tab = it }
    if (tab == "Comunicados") {
        if (account.role in listOf(SchoolRole.ADMIN, SchoolRole.TEACHER, SchoolRole.TUTOR)) Button(onClick = { onForm(noticeForm(account, store)) }, modifier = Modifier.fillMaxWidth()) { Text("Publicar comunicado") }
        val notices = store.engine().notices(account)
        if (notices.isEmpty()) EmptyState("No hay comunicados dirigidos a esta cuenta.")
        notices.sortedByDescending { it.urgent }.forEach { notice ->
            SchoolCard(notice.title, "${notice.author} · ${dateLabel(notice.date)}", "${if (notice.urgent) "IMPORTANTE · " else ""}${if (account.id in notice.readBy) "Leído" else "Nuevo"}") {
                Text(notice.body.take(140), style = MaterialTheme.typography.bodySmall)
                ActionRow("Leer comunicado" to {
                    store.mutate { readNotice(account, notice.id) }
                    onForm(SchoolForm(notice.title, body = notice.body + "\n\nDestino: " + (notice.courseId?.let { id -> data.courses.first { it.id == id }.name } ?: notice.sectionId?.let { sectionLabel(data, it) } ?: "Toda la comunidad") + "\n\nPublicado por ${notice.author}", confirm = "Entendido", submit = {}))
                })
            }
        }
    } else {
        val alerts = data.alerts.filter { account.id in it.recipientIds }
        Text("${alerts.count { account.id !in it.readBy }} avisos sin leer", style = MaterialTheme.typography.bodySmall)
        if (alerts.any { account.id !in it.readBy }) TextButton(onClick = { store.mutate { readAlerts(account) } }) { Text("Marcar avisos como leídos") }
        if (alerts.isEmpty()) EmptyState("No tienes avisos nuevos. El historial aparecerá aquí.")
        alerts.forEach { alert -> SchoolCard(alert.title, alert.body, "${dateLabel(alert.date)} · ${if (account.id in alert.readBy) "Leído" else "Nuevo"}") }
    }
}

fun noticeForm(account: SchoolAccount, store: SchoolStore, presetSection: String? = null): SchoolForm {
    val engine = store.engine()
    val audiences = (if (account.role == SchoolRole.ADMIN) listOf("all" to "Toda la comunidad") else emptyList()) +
        engine.sections(account).filter { account.role == SchoolRole.ADMIN || (account.role == SchoolRole.TUTOR && it.tutorId == account.id) }.map { "section:${it.id}" to "Aula: ${it.label}" } +
        engine.courses(account).map { "course:${it.id}" to "Curso: ${it.name} · ${sectionLabel(store.data, it.sectionId)}" }
    return SchoolForm("Publicar comunicado", listOf(
        FormField("audience", "Destinatarios", presetSection?.let { "section:$it" } ?: audiences.firstOrNull()?.first.orEmpty(), audiences),
        FormField("title", "Título"), FormField("body", "Mensaje", multiline = true),
        FormField("urgent", "Prioridad", "normal", listOf("normal" to "Normal", "urgent" to "Importante"))
    ), body = "El comunicado y su aviso aparecerán solo en las cuentas vinculadas al destino seleccionado.", confirm = "Publicar", submit = { v ->
        val audience = v.getValue("audience")
        val course = store.data.courses.find { "course:${it.id}" == audience }
        val section = if (course != null) course.sectionId else audience.takeIf { it.startsWith("section:") }?.substringAfter(':')
        store.mutate { publishNotice(account, v.getValue("title"), v.getValue("body"), section, course?.id, v["urgent"] == "urgent") }
    })
}

@Composable
fun PaymentsContent(account: SchoolAccount, store: SchoolStore, pupilId: String, onForm: (SchoolForm) -> Unit) {
    var filter by rememberSaveable { mutableStateOf("Pendientes") }
    val admin = account.role == SchoolRole.ADMIN
    val invoices = store.engine().invoices(account).filter { admin || it.pupilId == pupilId }
    val pending = invoices.filter { !it.paid }
    PageHeading(if (admin) "Control de pagos" else "Estado de cuenta", "Consulta tus cuotas, vencimientos y comprobantes")
    Metrics(money(pending.sumOf { it.amount }) to "Saldo pendiente", money(pending.filter { it.due < LocalDate.now().toString() }.sumOf { it.amount }) to "Vencido")
    SchoolCard("Estamos para ayudarte", "Presenta tu comprobante o consulta cualquier diferencia en tu estado de cuenta. Secretaría revisará tu solicitud.")
    if (admin) ActionRow("Generar recordatorios" to { onForm(SchoolForm("Recordatorios de pago", body = "Se agregarán recordatorios al historial de los apoderados. Se omitirá un aviso ya generado hoy para la misma cuota.", confirm = "Generar avisos", submit = { store.mutate { paymentReminders(account) } })) })
    Filters(filter, listOf("Pendientes", "Pagados", "Todos")) { filter = it }
    val visible = invoices.filter { filter == "Todos" || (if (filter == "Pagados") it.paid else !it.paid) }
    if (visible.isEmpty()) EmptyState("No hay cuotas en esta categoría.")
    visible.sortedBy { it.due }.forEach { invoice ->
        val pupil = store.data.pupils.first { it.id == invoice.pupilId }
        SchoolCard(invoice.concept, "${pupil.name} · vence ${dateLabel(invoice.due)}", "${money(invoice.amount)} · ${if (invoice.paid) "Pagado" else if (invoice.due < LocalDate.now().toString()) "Vencido" else "Pendiente"}") {
            if (invoice.evidence.isNotEmpty()) Text("Referencia: ${invoice.evidence}", style = MaterialTheme.typography.bodySmall)
            if (admin) ActionRow((if (invoice.paid) "Corregir regularización" else "Registrar regularización") to {
                onForm(SchoolForm(if (invoice.paid) "Corregir estado del pago" else "Regularizar pago", listOf(FormField("evidence", if (invoice.paid) "Motivo y autorización de corrección" else "N.º de comprobante / evidencia")), body = if (invoice.paid) "La cuota volverá a pendiente. La corrección quedará registrada en el historial." else "Confirma que el comprobante fue revisado. Se actualizará el estado de cuenta del apoderado.", submit = { v -> store.mutate { regularize(account, invoice.id, v.getValue("evidence"), !invoice.paid) } }))
            }) else {
                ActionRow("Consultar / reclamar esta cuota" to {
                    onForm(SchoolForm("Consulta de pago", listOf(FormField("detail", "Detalle de la consulta", multiline = true)), body = "${invoice.concept} · ${pupil.name}\nSecretaría verá esta solicitud en su bandeja de atención.", confirm = "Registrar consulta", submit = { v ->
                        require(v.getValue("detail").isNotBlank()) { "Describe tu consulta." }
                        store.mutate { ticket(account, "Consulta de cuota ${invoice.id}", "${invoice.concept}: ${v.getValue("detail")}") }
                    }))
                })
                if (!invoice.paid) ActionRow("Presentar comprobante" to {
                    onForm(SchoolForm("Registrar comprobante", listOf(FormField("reference", "Referencia de comprobante", "REC-2026-")), body = "Se registrará una solicitud de validación. El pago permanece pendiente hasta que Secretaría lo regularice.", confirm = "Presentar comprobante", submit = { v ->
                        require(v.getValue("reference").trim().length >= 5) { "Ingresa el número de comprobante." }
                        store.mutate { ticket(account, "Validar pago ${invoice.id}", "${pupil.name} · ${invoice.concept} · ${v.getValue("reference")}") }
                    }))
                })
            }
        }
    }
}

@Composable
fun TutorContent(account: SchoolAccount, store: SchoolStore, onForm: (SchoolForm) -> Unit) {
    val sections = store.data.sections.filter { it.tutorId == account.id }
    var sectionId by rememberSaveable { mutableStateOf(sections.firstOrNull()?.id.orEmpty()) }
    PageHeading("Mi tutoría", "Responsabilidad adicional de tu misma cuenta docente")
    if (sections.isEmpty()) { EmptyState("No tienes un aula de tutoría asignada."); return }
    ChoiceField("Aula de tutoría", sectionId, sections.map { it.id to it.label }) { sectionId = it }
    val pupils = store.data.pupils.filter { it.sectionId == sectionId && it.active }
    val ids = pupils.map { it.id }
    val records = store.data.attendance.filter { it.pupilId in ids && it.validated && it.courseId == null }
    val incidents = store.data.incidents.filter { it.pupilId in ids }
    Metrics("${pupils.size}" to "Alumnos de tutoría", "${incidents.count { !it.resolved }}" to "Incidencias abiertas", "${records.count { it.status == AttendanceStatus.LATE }}" to "Tardanzas registradas", "${records.count { it.status == AttendanceStatus.ABSENT }}" to "Ausencias registradas")
    ActionRow("Comunicarse con las familias" to { onForm(noticeForm(account, store, sectionId)) }, "Registrar incidencia" to {
        onForm(SchoolForm("Nueva incidencia de tutoría", listOf(FormField("pupil", "Alumno", pupils.firstOrNull()?.id.orEmpty(), pupils.map { it.id to it.name }), FormField("detail", "Detalle y seguimiento", multiline = true)), submit = { v -> store.mutate { incident(account, v.getValue("pupil"), v.getValue("detail")) } }))
    })
    Text("Alumnos y asistencia", style = MaterialTheme.typography.titleMedium)
    pupils.forEach { p ->
        val own = records.filter { it.pupilId == p.id }
        SchoolCard(p.name, "${p.code} · ${if (p.parentId == "parent") "Apoderada: Lucía Mendoza" else "Apoderado: familia ${p.name.substringAfter(' ')}"}", "${own.count { it.status == AttendanceStatus.LATE }} tardanzas · ${own.count { it.status == AttendanceStatus.ABSENT }} ausencias")
    }
    Text("Incidencias de seguimiento", style = MaterialTheme.typography.titleMedium)
    if (incidents.isEmpty()) EmptyState("No hay incidencias en esta aula.")
    incidents.forEach { incident -> SchoolCard(store.data.pupils.first { it.id == incident.pupilId }.name, incident.detail, "${dateLabel(incident.date)} · ${if (incident.resolved) "Atendida" else "Abierta"}") {
        if (!incident.resolved) ActionRow("Marcar incidencia atendida" to { store.mutate { resolveIncident(account, incident.id) } })
    } }
}

@Composable
fun SupportContent(account: SchoolAccount, store: SchoolStore, onForm: (SchoolForm) -> Unit) {
    PageHeading("Atención y soporte", "Consultas académicas, pagos, corrección de datos y acceso")
    ActionRow("Nueva solicitud" to { onForm(SchoolForm("Solicitar atención", listOf(FormField("subject", "Asunto"), FormField("detail", "Detalle", multiline = true)), confirm = "Registrar solicitud", submit = { v -> store.mutate { ticket(account, v.getValue("subject"), v.getValue("detail")) } })) })
    val tickets = store.data.tickets.filter { account.role == SchoolRole.ADMIN || it.accountId == account.id }
    if (tickets.isEmpty()) EmptyState("No hay solicitudes. Las consultas que registres aparecerán aquí.")
    tickets.forEach { ticket -> SchoolCard(ticket.subject, "${SchoolAccounts.all.find { it.id == ticket.accountId }?.name.orEmpty()} · ${dateLabel(ticket.date)}\n${ticket.detail}", if (ticket.resolved) "Atendida" else "Pendiente de atención") {
        if (account.role == SchoolRole.ADMIN && !ticket.resolved) ActionRow("Atender solicitud" to { store.mutate { resolveTicket(account, ticket.id) } })
    } }
}
