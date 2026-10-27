package com.example.collegueapp.data.school

import com.example.collegueapp.platform.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
enum class SchoolRole(val key: String, val label: String) {
    STUDENT("estudiante", "Estudiante"), PARENT("apoderado", "Apoderado"),
    TEACHER("profesor", "Docente"), TUTOR("tutor", "Docente y tutor"),
    ASSISTANT("auxiliar", "Auxiliar"), ADMIN("admin", "Secretaría")
}

@Serializable
data class SchoolAccount(val id: String, val username: String, val name: String, val role: SchoolRole, val pupilId: String? = null) {
    val permissions: Set<SchoolPermission> get() = role.permissions
}
enum class SchoolPermission(val label: String) {
    OWN_ACADEMICS("Mi información académica"), FAMILY_ACADEMICS("Seguimiento de mis hijos"),
    COURSEWORK("Tareas, notas y asistencia de mis cursos"), TUTORING("Tutoría de mis aulas"),
    ENTRANCE("Control de ingreso"), SCHOOL_MANAGEMENT("Gestión de matrículas y asignaciones"),
    PAYMENTS("Gestión de pagos"), PUBLISH_NOTICES("Publicación de comunicados"),
    READ_NOTICES("Consulta de comunicados"), SUPPORT("Atención y soporte")
}
val SchoolRole.permissions: Set<SchoolPermission> get() = setOf(SchoolPermission.READ_NOTICES, SchoolPermission.SUPPORT) + when (this) {
    SchoolRole.STUDENT -> setOf(SchoolPermission.OWN_ACADEMICS)
    SchoolRole.PARENT -> setOf(SchoolPermission.FAMILY_ACADEMICS)
    SchoolRole.TEACHER -> setOf(SchoolPermission.COURSEWORK, SchoolPermission.PUBLISH_NOTICES)
    SchoolRole.TUTOR -> SchoolRole.TEACHER.permissions + SchoolPermission.TUTORING
    SchoolRole.ASSISTANT -> setOf(SchoolPermission.ENTRANCE)
    SchoolRole.ADMIN -> setOf(SchoolPermission.SCHOOL_MANAGEMENT, SchoolPermission.PAYMENTS, SchoolPermission.ENTRANCE, SchoolPermission.PUBLISH_NOTICES)
}
object SchoolAccounts {
    const val PASSWORD = "Colegio2026!"
    val all = listOf(
        SchoolAccount("student", "alex.mendoza", "Alex Mendoza", SchoolRole.STUDENT, pupilId = "alex"),
        SchoolAccount("parent", "lucia.mendoza", "Lucía Mendoza", SchoolRole.PARENT),
        SchoolAccount("teacher", "ana.torres", "Ana Torres", SchoolRole.TEACHER),
        SchoolAccount("tutor", "carlos.ruiz", "Carlos Ruiz", SchoolRole.TUTOR),
        SchoolAccount("assistant", "rosa.salazar", "Rosa Salazar", SchoolRole.ASSISTANT),
        SchoolAccount("admin", "maria.flores", "María Flores", SchoolRole.ADMIN)
    )
    fun find(username: String) = all.find { it.username == username.trim().lowercase() }
}

@Serializable
data class SchoolSection(val id: String, val grade: String, val name: String, val tutorId: String, val year: Int = 2026) {
    val label get() = "$grade · $name"
}
@Serializable
data class Pupil(val id: String, val name: String, val sectionId: String, val parentId: String, val code: String, val active: Boolean = true)
@Serializable
data class SchoolCourse(val id: String, val name: String, val teacherId: String, val sectionId: String, val room: String)
@Serializable
data class Lesson(val id: String, val courseId: String, val weekday: Int, val start: String, val end: String)
@Serializable
data class Assignment(val id: String, val courseId: String, val title: String, val details: String, val due: String, val submittedBy: List<String> = emptyList())
@Serializable
data class Mark(val id: String, val pupilId: String, val courseId: String, val period: String, val assessment: String, val score: Double)
@Serializable
enum class AttendanceStatus(val label: String) { PRESENT("Presente"), LATE("Tardanza"), ABSENT("Ausente"), EXCUSED("Justificado") }
@Serializable
data class Attendance(val id: String, val pupilId: String, val date: String, val courseId: String?, val status: AttendanceStatus, val validated: Boolean, val reason: String = "")
@Serializable
data class Notice(val id: String, val title: String, val body: String, val author: String, val date: String, val sectionId: String? = null, val courseId: String? = null, val urgent: Boolean = false, val readBy: List<String> = emptyList())
@Serializable
data class Invoice(val id: String, val pupilId: String, val concept: String, val amount: Int, val due: String, val paid: Boolean = false, val evidence: String = "")
@Serializable
data class SchoolAlert(val id: String, val title: String, val body: String, val recipientIds: List<String>, val date: String, val readBy: List<String> = emptyList())
@Serializable
data class Incident(val id: String, val pupilId: String, val detail: String, val date: String, val resolved: Boolean = false)
@Serializable
data class AuditEntry(val id: String, val actor: String, val action: String, val detail: String, val date: String)
@Serializable
data class SupportTicket(val id: String, val accountId: String, val subject: String, val detail: String, val date: String, val resolved: Boolean = false)
@Serializable
data class SchoolData(
    val sections: List<SchoolSection>, val pupils: List<Pupil>, val courses: List<SchoolCourse>,
    val lessons: List<Lesson>, val assignments: List<Assignment>, val marks: List<Mark>,
    val attendance: List<Attendance>, val notices: List<Notice>, val invoices: List<Invoice>,
    val alerts: List<SchoolAlert>, val incidents: List<Incident>, val audit: List<AuditEntry>,
    val tickets: List<SupportTicket> = emptyList(), val extraAssistant: Boolean = false
)

object SchoolSeed {
    fun create(today: LocalDate = LocalDate.now()): SchoolData {
        val day = today.toString()
        val sections = listOf(SchoolSection("3a", "3.º secundaria", "A", "tutor"), SchoolSection("1b", "1.º secundaria", "B", "tutor"))
        val pupils = listOf(
            Pupil("alex", "Alex Mendoza", "3a", "parent", "EST-001"),
            Pupil("sofia", "Sofía Mendoza", "1b", "parent", "EST-002"),
            Pupil("diego", "Diego Rojas", "3a", "other-parent", "EST-003"),
            Pupil("valeria", "Valeria Quispe", "3a", "other-parent", "EST-004"),
            Pupil("mateo", "Mateo Vargas", "1b", "other-parent", "EST-005")
        )
        val courses = listOf(
            SchoolCourse("math3", "Matemáticas", "teacher", "3a", "Aula 301"),
            SchoolCourse("history3", "Historia", "tutor", "3a", "Aula 301"),
            SchoolCourse("science3", "Ciencias", "teacher", "3a", "Laboratorio 2"),
            SchoolCourse("language3", "Comunicación", "tutor", "3a", "Biblioteca"),
            SchoolCourse("math1", "Matemáticas", "teacher", "1b", "Aula 102"),
            SchoolCourse("history1", "Historia", "tutor", "1b", "Aula 102")
        )
        return SchoolData(
            sections, pupils, courses,
            courses.flatMapIndexed { index, c -> (1..5).filter { (it + index) % 2 == 0 || it == 1 }.map { d ->
                val start = 480 + index * 90
                Lesson("${c.id}-$d", c.id, d, timeLabel(start), timeLabel(start + 60))
            } },
            courses.mapIndexed { index, c -> Assignment("task-$index", c.id,
                if (c.name == "Matemáticas") "Resuelve problemas de fracciones" else "Proyecto de ${c.name.lowercase()}",
                "Revisa el material de clase y presenta tus conclusiones con ejemplos. Se evaluará claridad y desarrollo.",
                today.plusDays((index + 2).toLong()).toString()) },
            pupils.flatMap { p -> courses.filter { it.sectionId == p.sectionId }.flatMapIndexed { i, c ->
                listOf(Mark("${p.id}-${c.id}-1", p.id, c.id, "III bimestre", "Evaluación de unidad", 14.0 + i),
                    Mark("${p.id}-${c.id}-2", p.id, c.id, "IV bimestre", "Práctica calificada", 15.0 + i))
            } },
            pupils.flatMapIndexed { index, p -> (1..5).map { offset -> Attendance("${p.id}-$offset", p.id,
                today.minusDays(offset.toLong()).toString(), null,
                if (offset == 2 && index == 0) AttendanceStatus.LATE else AttendanceStatus.PRESENT, true) } },
            listOf(Notice("notice-1", "Reunión de familias", "Los esperamos el viernes a las 17:00 en el auditorio. Revisaremos los avances del bimestre y las actividades de cierre.", "Secretaría", day, urgent = true),
                Notice("notice-2", "Materiales para el proyecto de ciencias", "Traer cartulina y materiales reciclables para el trabajo en equipo del próximo lunes.", "Ana Torres", day, "3a", "science3"),
                Notice("notice-3", "Visita a la biblioteca", "La sección B visitará la biblioteca durante la clase de Historia. Traer el cuaderno de lectura.", "Carlos Ruiz", day, "1b", "history1")),
            pupils.take(2).flatMap { p -> listOf(
                Invoice("${p.id}-sep", p.id, "Pensión de setiembre", 450, today.minusDays(20).toString()),
                Invoice("${p.id}-oct", p.id, "Pensión de octubre", 450, today.plusDays(5).toString()),
                Invoice("${p.id}-aug", p.id, "Pensión de agosto", 450, today.minusDays(50).toString(), true, "REC-2026-0801")) },
            listOf(SchoolAlert("alert-1", "Cuota próxima a vencer", "La pensión de octubre vence en 5 días. Consulta el detalle en Pagos.", listOf("parent"), day),
                SchoolAlert("alert-2", "Tardanza validada", "Alex registró una tardanza de ingreso. El registro fue validado por asistencia.", listOf("student", "parent"), day),
                SchoolAlert("alert-3", "Nueva tarea de Matemáticas", "Revisa la actividad y su fecha de entrega en Tareas.", listOf("student", "parent"), day)),
            listOf(Incident("incident-1", "alex", "Seguimiento de puntualidad: acordar rutina de ingreso con la familia.", day)),
            listOf(AuditEntry("seed", "Secretaría", "Datos iniciales", "Apertura del año escolar 2026", day))
        )
    }
}
