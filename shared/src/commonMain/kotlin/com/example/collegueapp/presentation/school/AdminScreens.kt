package com.example.collegueapp.presentation.school

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.collegueapp.data.school.Pupil
import com.example.collegueapp.data.school.SchoolAccount
import com.example.collegueapp.data.school.SchoolAccounts
import com.example.collegueapp.data.school.SchoolRole
import com.example.collegueapp.data.school.SchoolStore

@Composable
fun ManagementContent(account: SchoolAccount, store: SchoolStore, onForm: (SchoolForm) -> Unit) {
    var tab by rememberSaveable { mutableStateOf("Matrículas") }
    var query by rememberSaveable { mutableStateOf("") }
    val data = store.data
    PageHeading("Gestión escolar", "Año escolar 2026 · sede principal · turno mañana")
    ChoiceField(
        "Área de gestión",
        tab,
        listOf(
            "Matrículas",
            "Aulas",
            "Cursos",
            "Horarios",
            "Asistencia",
            "Historial",
            "Atención"
        ).map { it to it }) { tab = it }
    when (tab) {
        "Matrículas" -> {
            Button(
                onClick = { onForm(enrollmentForm(account, store)) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Matricular alumno") }
            OutlinedTextField(
                query,
                { query = it },
                label = { Text("Buscar por nombre o código") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            data.pupils.filter { it.name.contains(query, true) || it.code.contains(query, true) }
                .forEach { pupil ->
                    SchoolCard(
                        pupil.name,
                        "${pupil.code} · ${data.sections.first { it.id == pupil.sectionId }.label}",
                        "Apoderado: ${if (pupil.parentId == "parent") "Lucía Mendoza" else "Familia vinculada"}"
                    ) {
                        Text(
                            "${data.courses.count { it.sectionId == pupil.sectionId }} cursos según su matrícula",
                            style = MaterialTheme.typography.bodySmall
                        )
                        ActionRow("Editar matrícula de ${pupil.name}" to {
                            onForm(
                                enrollmentForm(
                                    account,
                                    store,
                                    pupil
                                )
                            )
                        })
                    }
                }
        }

        "Aulas" -> {
            Button(onClick = {
                onForm(
                    SchoolForm(
                        "Crear aula",
                        listOf(
                            FormField("grade", "Grado", "2.º secundaria"),
                            FormField("name", "Sección", "A")
                        ),
                        submit = { v ->
                            store.mutate {
                                createSection(
                                    account,
                                    v.getValue("grade"),
                                    v.getValue("name")
                                )
                            }
                        })
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Crear aula") }
            data.sections.forEach { section ->
                SchoolCard(
                    section.label,
                    "${data.pupils.count { it.sectionId == section.id && it.active }} alumnos · ${data.courses.count { it.sectionId == section.id }} cursos",
                    "Año ${section.year} · tutor: ${SchoolAccounts.all.find { it.id == section.tutorId }?.name.orEmpty()}"
                ) {
                    ActionRow("Asignar tutor" to {
                        onForm(
                            SchoolForm(
                                "Asignación de tutoría",
                                listOf(
                                    FormField(
                                        "tutor",
                                        "Docente responsable",
                                        section.tutorId,
                                        SchoolAccounts.all.filter {
                                            it.role in listOf(
                                                SchoolRole.TEACHER,
                                                SchoolRole.TUTOR
                                            )
                                        }.map { it.id to it.name })
                                ),
                                body = "La tutoría se agrega a las responsabilidades de la misma cuenta docente.",
                                submit = { v ->
                                    store.mutate {
                                        assignTutor(
                                            account,
                                            section.id,
                                            v.getValue("tutor")
                                        )
                                    }
                                })
                        )
                    })
                }
            }
            SchoolCard(
                "Responsables de ingreso",
                "El auxiliar puede usar un segundo punto de registro adicional. Ambos comparten el registro para evitar duplicados."
            ) {
                Row {
                    Switch(
                        checked = data.extraAssistant,
                        onCheckedChange = { enabled ->
                            store.mutate {
                                setExtraAssistant(
                                    account,
                                    enabled
                                )
                            }
                        }); Text("Habilitar segundo punto de registro")
                }
            }
        }

        "Cursos" -> {
            val teachers = SchoolAccounts.all.filter {
                it.role in listOf(
                    SchoolRole.TEACHER,
                    SchoolRole.TUTOR
                )
            }.map { it.id to it.name }
            Button(onClick = {
                onForm(
                    SchoolForm(
                        "Crear curso",
                        listOf(
                            FormField("name", "Nombre del curso"),
                            FormField(
                                "section",
                                "Aula",
                                data.sections.first().id,
                                data.sections.map { it.id to it.label }),
                            FormField("teacher", "Docente", teachers.first().first, teachers),
                            FormField("room", "Ambiente / salón")
                        ),
                        submit = { v ->
                            store.mutate {
                                createCourse(
                                    account,
                                    v.getValue("name"),
                                    v.getValue("section"),
                                    v.getValue("teacher"),
                                    v.getValue("room")
                                )
                            }
                        })
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Crear curso") }
            data.courses.forEach { course ->
                SchoolCard(
                    course.name,
                    "${data.sections.first { it.id == course.sectionId }.label} · ${course.room}",
                    SchoolAccounts.all.first { it.id == course.teacherId }.name
                ) {
                    ActionRow("Asignar docente" to {
                        onForm(
                            SchoolForm(
                                "Asignar docente al curso",
                                listOf(FormField("teacher", "Docente", course.teacherId, teachers)),
                                submit = { v ->
                                    store.mutate {
                                        assignCourse(
                                            account,
                                            course.id,
                                            v.getValue("teacher")
                                        )
                                    }
                                })
                        )
                    })
                }
            }
        }

        "Horarios" -> {
            var courseId by rememberSaveable { mutableStateOf(data.courses.first().id) }
            ChoiceField(
                "Curso",
                courseId,
                data.courses.map {
                    it.id to "${it.name} · ${
                        sectionLabel(
                            store.data,
                            it.sectionId
                        )
                    }"
                }) { courseId = it }
            ActionRow("Agregar bloque de horario" to {
                onForm(
                    SchoolForm(
                        "Nuevo bloque semanal",
                        listOf(
                            FormField(
                                "day",
                                "Día",
                                "1",
                                listOf(
                                    "Lunes",
                                    "Martes",
                                    "Miércoles",
                                    "Jueves",
                                    "Viernes"
                                ).mapIndexed { i, text -> (i + 1).toString() to text }),
                            FormField("start", "Inicio (HH:mm)", "08:00"),
                            FormField("end", "Fin (HH:mm)", "09:00")
                        ),
                        submit = { v ->
                            store.mutate {
                                addLesson(
                                    account,
                                    courseId,
                                    v.getValue("day").toInt(),
                                    v.getValue("start"),
                                    v.getValue("end")
                                )
                            }
                        })
                )
            })
            ScheduleContent(account, store, "", listOf(courseId), onForm)
        }

        "Asistencia" -> AttendanceContent(account, store, "", onForm)
        "Historial" -> {
            Text(
                "Cambios relevantes registrados con responsable y fecha",
                style = MaterialTheme.typography.bodySmall
            )
            data.audit.forEach { entry ->
                SchoolCard(
                    entry.action,
                    entry.detail,
                    "${entry.actor} · ${entry.date.replace('T', ' ').take(19)}"
                )
            }
        }

        "Atención" -> SupportContent(account, store, onForm)
    }
}

private fun enrollmentForm(account: SchoolAccount, store: SchoolStore, pupil: Pupil? = null) =
    SchoolForm(
        if (pupil == null) "Matricular alumno" else "Actualizar matrícula",
        listOf(
            FormField("name", "Nombre y apellido", pupil?.name.orEmpty()),
            FormField("code", "Código único", pupil?.code.orEmpty()),
            FormField(
                "section",
                "Aula del año 2026",
                pupil?.sectionId ?: store.data.sections.first().id,
                store.data.sections.map { it.id to it.label }),
            FormField(
                "parent",
                "Apoderado vinculado",
                pupil?.parentId ?: "parent",
                listOf(
                    "parent" to "Lucía Mendoza (cuenta apoderado)",
                    "other-parent" to "Otra familia vinculada"
                )
            )
        ),
        body = "Selecciona el aula y el apoderado. Se asignarán los cursos del aula; cualquier cambio conservará el historial académico anterior.",
        submit = { v ->
            store.mutate {
                enroll(
                    account,
                    pupil?.id,
                    v.getValue("name"),
                    v.getValue("code"),
                    v.getValue("section"),
                    v.getValue("parent")
                )
            }
        }
    )
