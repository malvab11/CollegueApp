package com.example.collegueapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.collegueapp.data.school.*

@RunWith(AndroidJUnit4::class)
class DemoFlowsInstrumentedTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun login(username: String) {
        compose.onNodeWithText("Usuario").performTextReplacement(username)
        compose.onNodeWithText("Contraseña").performTextReplacement(SchoolAccounts.PASSWORD)
        compose.onNodeWithText("Ingresar").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithContentDescription("Cerrar sesión").fetchSemanticsNodes().isNotEmpty() }
    }
    private fun logout() {
        compose.onNodeWithContentDescription("Cerrar sesión").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Bienvenido a tu colegio").fetchSemanticsNodes().isNotEmpty() }
    }
    private fun click(text: String) {
        val node = compose.onNode(hasText(text) and hasClickAction())
        runCatching { node.performScrollTo() }
        node.performClick()
    }

    @Test fun credentialsAndAllRolesAndSharedTaskAndEnrollment() {
        compose.waitUntil(10_000) {
            compose.onAllNodesWithContentDescription("Cerrar sesión").fetchSemanticsNodes().isNotEmpty() ||
                compose.onAllNodesWithText("Bienvenido a tu colegio").fetchSemanticsNodes().isNotEmpty()
        }
        if (compose.onAllNodesWithContentDescription("Cerrar sesión").fetchSemanticsNodes().isNotEmpty()) logout()
        compose.onNodeWithText("Bienvenido a tu colegio").assertExists()

        compose.runOnUiThread { compose.activity.schoolStore.reset(SchoolAccounts.all.first { it.role == SchoolRole.ADMIN }) }
        compose.onAllNodesWithText("123456", substring = true).assertCountEquals(0)
        compose.onAllNodesWithText("demo", substring = true, ignoreCase = true).assertCountEquals(0)

        login("ana.torres")
        click("Cursos")
        click("Tareas")
        click("Publicar tarea")
        compose.onNodeWithText("Título").performTextInput("Proyecto interdisciplinario")
        compose.onNodeWithText("Indicaciones").performTextInput("Resolver la actividad y entregar las conclusiones")
        compose.onNodeWithText("Publicar").performClick()
        compose.onNodeWithText("Proyecto interdisciplinario").performScrollTo().assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Cerrar sesión").assertExists()
        compose.onNodeWithText("Proyecto interdisciplinario").performScrollTo().assertIsDisplayed()
        logout()

        login("alex.mendoza")
        click("Ver tareas pendientes")
        compose.onNodeWithText("Proyecto interdisciplinario").performScrollTo().assertIsDisplayed()
        logout()

        login("lucia.mendoza")
        click("Alex Mendoza · 3.º secundaria · A")
        compose.onNodeWithText("Sofía Mendoza · 1.º secundaria · B").performClick()
        compose.onNode(hasText("Sofía Mendoza", substring = true) and hasClickAction()).assertExists()
        click("Pagos")
        compose.onNodeWithText("Estado de cuenta").assertExists()
        logout()

        login("carlos.ruiz")
        click("Tutoría")
        compose.onNodeWithText("Mi tutoría").assertExists()
        compose.onNodeWithText("Registrar incidencia").assertExists()
        logout()

        login("rosa.salazar")
        click("Registro")
        compose.onNodeWithText("Asistencia").assertExists()
        compose.onNodeWithText("Seleccionar / quitar grupo").assertExists()
        logout()

        login("maria.flores")
        click("Gestión")
        click("Matricular alumno")
        compose.onNodeWithText("Nombre y apellido").performTextInput("Camila Mendoza")
        compose.onNodeWithText("Código único").performTextInput("EST-006")
        compose.onNodeWithText("Guardar").performClick()
        compose.onNodeWithText("Camila Mendoza").performScrollTo().assertIsDisplayed()
        compose.runOnUiThread { compose.activity.schoolStore.reset(SchoolAccounts.all.first { it.role == SchoolRole.ADMIN }) }
        logout()
    }
}
