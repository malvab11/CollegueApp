package com.example.collegueapp.data.school

import com.example.collegueapp.platform.now
import com.example.collegueapp.platform.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class SchoolEngineTest {
    private fun account(role: SchoolRole) = SchoolAccounts.all.first { it.role == role }
    private fun engine() = SchoolEngine(SchoolSeed.create(LocalDate.now()))
    private fun rejects(action: () -> Unit) {
        try { action(); fail("La acción debía rechazarse") } catch (_: IllegalArgumentException) { }
    }

    @Test fun parentSeesBothChildrenButNotOtherFamilies() {
        val engine = engine()
        assertEquals(setOf("alex", "sofia"), engine.pupils(account(SchoolRole.PARENT)).map { it.id }.toSet())
        assertEquals(listOf("alex"), engine.pupils(account(SchoolRole.STUDENT)).map { it.id })
        assertTrue(engine.invoices(account(SchoolRole.STUDENT)).isEmpty())
    }

    @Test fun teacherCannotWriteAnotherTeachersCourse() {
        val engine = engine()
        val before = engine.data
        rejects { engine.publishTask(account(SchoolRole.TEACHER), "history3", "Actividad", "Detalle", LocalDate.now().plusDays(1).toString()) }
        assertEquals(before, engine.data)
    }

    @Test fun taskFlowsFromTeacherToStudentAndPersistsInSerializedData() {
        val engine = engine()
        engine.publishTask(account(SchoolRole.TEACHER), "math3", "Actividad de demo", "Resolver", LocalDate.now().toString())
        val task = engine.data.assignments.last()
        assertTrue(engine.data.alerts.first().recipientIds.containsAll(listOf("parent", "student")))
        engine.submitTask(account(SchoolRole.STUDENT), task.id)
        engine.submitTask(account(SchoolRole.STUDENT), task.id)
        assertEquals(listOf("alex"), engine.data.assignments.last().submittedBy)
        val restored = Json.decodeFromString<SchoolData>(Json.encodeToString(engine.data))
        assertEquals(engine.data, restored)
    }

    @Test fun studentCannotSubmitSiblingsTask() {
        val engine = engine()
        rejects { engine.submitTask(account(SchoolRole.STUDENT), engine.data.assignments.first { it.courseId == "math1" }.id) }
    }

    @Test fun bulkAttendanceIsUniqueAndNotifiesOnlyAfterValidation() {
        val engine = engine()
        val assistant = account(SchoolRole.ASSISTANT)
        val date = LocalDate.now().toString()
        val alertCount = engine.data.alerts.size
        repeat(2) { engine.recordAttendance(assistant, listOf("alex", "diego", "alex"), date, null, AttendanceStatus.LATE, "") }
        assertEquals(2, engine.data.attendance.count { it.date == date })
        assertEquals(alertCount, engine.data.alerts.size)
        engine.validateAttendance(assistant, date, null)
        assertTrue(engine.data.attendance.filter { it.date == date }.all { it.validated })
        assertEquals(alertCount + 1, engine.data.alerts.size)
        rejects { engine.validateAttendance(assistant, date, null) }
        rejects { engine.recordAttendance(assistant, listOf("alex"), date, null, AttendanceStatus.PRESENT, "") }
        engine.recordAttendance(assistant, listOf("alex"), date, null, AttendanceStatus.EXCUSED, "Constancia médica demo")
        assertEquals(1, engine.data.attendance.count { it.date == date && it.pupilId == "alex" })
        assertTrue(engine.data.audit.first().detail.contains("Constancia médica"))
    }

    @Test fun teacherCannotRegisterSchoolEntranceOrOtherSectionInCourse() {
        val engine = engine()
        val teacher = account(SchoolRole.TEACHER)
        val date = LocalDate.now().toString()
        rejects { engine.recordAttendance(teacher, listOf("alex"), date, null, AttendanceStatus.PRESENT, "") }
        rejects { engine.recordAttendance(teacher, listOf("sofia"), date, "math3", AttendanceStatus.PRESENT, "") }
        engine.recordAttendance(teacher, listOf("alex"), date, "math3", AttendanceStatus.PRESENT, "")
        assertEquals("math3", engine.data.attendance.last().courseId)
    }

    @Test fun marksRequireValidScaleMatchingPupilAndCorrectionReason() {
        val engine = engine()
        val teacher = account(SchoolRole.TEACHER)
        rejects { engine.saveMark(teacher, "sofia", "math3", "IV bimestre", "Examen", 15.0, "") }
        rejects { engine.saveMark(teacher, "alex", "math3", "IV bimestre", "Examen", Double.NaN, "") }
        rejects { engine.saveMark(teacher, "alex", "math3", "IV bimestre", "Examen", 21.0, "") }
        rejects { engine.saveMark(teacher, "alex", "math3", "IV bimestre", "Práctica calificada", 18.0, "") }
        engine.saveMark(teacher, "alex", "math3", "IV bimestre", "Práctica calificada", 18.0, "Revisión autorizada")
        assertEquals("Nota rectificada", engine.data.audit.first().action)
        assertEquals(1, engine.data.marks.count { it.pupilId == "alex" && it.courseId == "math3" && it.period == "IV bimestre" })
    }

    @Test fun enrollmentEnforcesUniqueCodesAndUpdatesFamilyScope() {
        val engine = engine()
        val admin = account(SchoolRole.ADMIN)
        rejects { engine.enroll(admin, null, "Nueva Alumna", "EST-001", "3a", "parent") }
        engine.enroll(admin, null, "Camila Mendoza", "EST-006", "1b", "parent")
        assertEquals(3, engine.pupils(account(SchoolRole.PARENT)).size)
        assertEquals(2, engine.courses(account(SchoolRole.PARENT)).count { it.sectionId == "1b" })
        rejects { engine.enroll(account(SchoolRole.PARENT), null, "Alumno", "EST-007", "3a", "parent") }
    }

    @Test fun paymentsRequireSecretariatAndEvidenceAndRemindersDeduplicate() {
        val engine = engine()
        val admin = account(SchoolRole.ADMIN)
        val invoice = engine.data.invoices.first()
        rejects { engine.regularize(account(SchoolRole.PARENT), invoice.id, "REC-123") }
        rejects { engine.regularize(admin, invoice.id, "") }
        engine.regularize(admin, invoice.id, "REC-123")
        assertTrue(engine.invoices(account(SchoolRole.PARENT)).first { it.id == invoice.id }.paid)
        engine.paymentReminders(admin)
        val count = engine.data.alerts.size
        engine.paymentReminders(admin)
        assertEquals(count, engine.data.alerts.size)
    }

    @Test fun courseNoticesOnlyReachLinkedAccounts() {
        val engine = engine()
        engine.publishNotice(account(SchoolRole.TEACHER), "Solo sección B", "Mensaje", "1b", "math1", true)
        val notice = engine.data.notices.first()
        assertFalse(engine.notices(account(SchoolRole.STUDENT)).any { it.id == notice.id })
        assertTrue(engine.notices(account(SchoolRole.PARENT)).any { it.id == notice.id })
        rejects { engine.publishNotice(account(SchoolRole.TEACHER), "General", "Mensaje", null, null, true) }
    }

    @Test fun tutorCombinesCourseAndClassroomResponsibilities() {
        val engine = engine()
        val tutor = account(SchoolRole.TUTOR)
        assertTrue(engine.courses(tutor).isNotEmpty())
        engine.incident(tutor, "alex", "Seguimiento demo")
        engine.publishNotice(tutor, "Tutoría", "Reunión", "3a", null, false)
        rejects { engine.incident(account(SchoolRole.TEACHER), "alex", "Sin autorización") }
    }

    @Test fun scheduleRejectsTeacherAndSectionCollisions() {
        val engine = engine()
        val admin = account(SchoolRole.ADMIN)
        rejects { engine.addLesson(admin, "math3", 1, "10:00", "09:00") }
        rejects { engine.addLesson(admin, "math3", 1, "08:15", "08:45") }
        engine.addLesson(admin, "math3", 1, "17:00", "18:00")
        assertEquals("Bloque de horario creado", engine.data.audit.first().action)
    }

    @Test fun automaticRemindersAreScopedAndDoNotRepeatOnLogin() {
        val engine = engine()
        engine.dailyReminders()
        val count = engine.data.alerts.size
        assertTrue(engine.data.alerts.any { it.id.startsWith("auto-payment") && it.recipientIds == listOf("parent") })
        engine.dailyReminders()
        assertEquals(count, engine.data.alerts.size)
    }
}
