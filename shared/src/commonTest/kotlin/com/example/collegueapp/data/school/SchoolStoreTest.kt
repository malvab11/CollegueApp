package com.example.collegueapp.data.school

import com.example.collegueapp.platform.*
import kotlinx.datetime.LocalDate
import kotlin.test.*

class SchoolStoreTest {
    private class MemoryStorage : SchoolStorage {
        val values = mutableMapOf<String, String>()
        override fun get(key: String) = values[key]
        override fun put(key: String, value: String?) { if (value == null) values.remove(key) else values[key] = value }
    }
    @Test fun sessionAndCourseworkSurviveNewStoreAndLogoutRemovesOnlySession() {
        val storage = MemoryStorage()
        val teacher = SchoolStore(storage)
        teacher.login("ana.torres", SchoolAccounts.PASSWORD)
        teacher.mutate { publishTask(teacher.account!!, "math3", "Trabajo compartido", "Resolver y entregar", LocalDate.now().plusDays(2).toString()) }
        val restored = SchoolStore(storage)
        assertEquals("teacher", restored.account?.id)
        restored.logout()
        assertNull(SchoolStore(storage).account)
        val student = SchoolStore(storage)
        student.login("alex.mendoza", SchoolAccounts.PASSWORD)
        assertTrue(student.data.assignments.any { it.title == "Trabajo compartido" })
    }
    @Test fun invalidCredentialsDoNotCreateSession() {
        val store = SchoolStore(MemoryStorage())
        assertFailsWith<IllegalArgumentException> { store.login("ana.torres", "incorrecta") }
        assertNull(store.account)
    }
    @Test fun recoveryDoesNotExposeWhetherUserExists() {
        val store = SchoolStore(MemoryStorage())
        store.requestRecovery("no.existe")
        assertTrue(store.data.tickets.isEmpty())
        store.requestRecovery("lucia.mendoza")
        assertEquals("parent", store.data.tickets.single().accountId)
        assertNull(store.account)
    }
    @Test fun tutoringAddsResponsibilitiesToSameTeachingAccount() {
        val teacher = SchoolAccounts.all.first { it.role == SchoolRole.TEACHER }
        val tutor = SchoolAccounts.all.first { it.role == SchoolRole.TUTOR }
        assertTrue(tutor.permissions.containsAll(teacher.permissions))
        assertTrue(SchoolPermission.TUTORING in tutor.permissions)
        assertFalse(SchoolPermission.TUTORING in teacher.permissions)
        assertFalse(SchoolPermission.PAYMENTS in teacher.permissions)
        assertFalse(SchoolPermission.SCHOOL_MANAGEMENT in SchoolRole.PARENT.permissions)
    }
    @Test fun studentScopeUsesEnrollmentLinkRatherThanHardcodedIdentity() {
        val student = SchoolAccounts.all.first { it.role == SchoolRole.STUDENT }.copy(id = "student-sofia", pupilId = "sofia")
        assertEquals(listOf("sofia"), SchoolEngine().pupils(student).map { it.id })
    }
    @Test fun secretariatCanAddTutoringToAnExistingTeacher() {
        val engine = SchoolEngine()
        val teacher = SchoolAccounts.all.first { it.role == SchoolRole.TEACHER }
        val admin = SchoolAccounts.all.first { it.role == SchoolRole.ADMIN }
        engine.assignTutor(admin, "3a", teacher.id)
        val current = engine.effectiveAccount(teacher)
        assertEquals(teacher.id, current.id)
        assertEquals(teacher.username, current.username)
        assertEquals(SchoolRole.TUTOR, current.role)
        assertTrue(SchoolPermission.TUTORING in current.permissions)
        engine.incident(teacher, "alex", "Seguimiento de puntualidad")
        engine.publishNotice(teacher, "Reunión de tutoría", "Reunión con familias", "3a", null, false)
        assertEquals("Reunión de tutoría", engine.data.notices.first().title)
    }

}
