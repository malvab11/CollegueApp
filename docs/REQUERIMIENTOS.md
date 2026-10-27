# Collegue — alcance y responsabilidades

Fuente funcional: Solicitud_del_Cliente_App_Escolar.pages. Las instrucciones de ejecución pertenecen al usuario; el documento aporta necesidades del producto.

## Decisiones de producto

- Una cuenta representa a una persona. Tutoría amplía las responsabilidades del docente en lugar de crear una segunda identidad.
- El estudiante accede a su matrícula; el apoderado accede a cada hijo vinculado y cambia el contexto explícitamente.
- Aula, curso, año escolar y responsable delimitan cada registro. Las notas y tareas se gestionan únicamente en cursos asignados.
- La asistencia de ingreso se distingue de la asistencia por curso. Un registro único por alumno, fecha y tipo evita duplicados; las alertas se generan tras validar la jornada.
- Las correcciones de registros validados requieren justificación. Rectificaciones de notas y pagos quedan en auditoría con responsable y evidencia o motivo.
- Los comunicados se dirigen a comunidad, aula o curso. La consulta depende de las relaciones de cada cuenta.
- Los accesos académicos, avisos urgentes y atención de reclamos permanecen disponibles; no se han acordado bloqueos por deuda.

## Matriz de responsabilidades

| Persona | Consulta | Gestión | Límite |
| --- | --- | --- | --- |
| Estudiante | Aula, cursos, horarios, notas, asistencias, tareas, comunicados y avisos | Entrega de sus tareas y solicitudes de atención | Su matrícula activa |
| Apoderado | Información académica y estado de cuenta por hijo | Comprobantes, consultas de deuda y solicitudes | Hijos vinculados, sin edición de notas/asistencia |
| Docente | Cursos, grupos, horarios y entregas asignadas | Tareas, calificaciones, asistencia por curso y comunicados | Sus cursos; rectificaciones con motivo |
| Docente con tutoría | Lo anterior y seguimiento de su aula | Incidencias y comunicación con familias del aula | Cursos propios y secciones de tutoría |
| Auxiliar | Nóminas y registros de ingreso | Registro individual/grupal, justificaciones y validación | Asistencia de ingreso, sin edición académica ni pagos |
| Secretaría | Información escolar, pagos, solicitudes y auditoría | Matrículas, aulas, cursos, asignaciones, horarios, comunicados, regularizaciones y atención | Cambios relevantes identifican responsable y motivo |

La fuente ejecutable de capacidades es `SchoolPermission`, en `shared/src/commonMain`. `SchoolEngine` vuelve a validar los permisos y relaciones al modificar datos; ocultar un botón no es la única comprobación.

## Criterios de aceptación

1. Ingreso mediante credenciales; la interfaz no muestra cuentas de prueba, contraseñas ni selector de perfiles.
2. Cada responsabilidad presenta únicamente sus módulos de trabajo.
3. Cambio de hijo actualiza cursos, notas, asistencia y pagos sin mezclar registros.
4. Una tarea publicada por el docente aparece en las cuentas relacionadas; su entrega es visible para el responsable.
5. Notas entre 0 y 20; una rectificación conserva evaluación y alumno y requiere motivo.
6. El registro grupal puede corregirse sin duplicar filas. Los borradores no generan avisos de inasistencia.
7. Una regularización actualiza el estado de cuenta y la auditoría; un comprobante presentado permanece pendiente hasta revisión.
8. Los avisos se dirigen a destinatarios vinculados y no se duplican al repetir la validación.
9. Cierre de sesión elimina el acceso activo. Datos y sesión persisten al reiniciar la app.
10. Android e iOS usan los mismos modelos, permisos, reglas, componentes y pantallas KMP.

## Estado de implementación y límites técnicos

La entrega mantiene los datos locales solicitados para la presentación. Android usa SharedPreferences; iOS usa NSUserDefaults. No se conecta a un servidor, pasarela de pagos, correo o servicio push. La interfaz presenta los procesos escolares sin mensajes internos sobre fixtures o demostraciones.

Antes de una puesta en producción con familias reales se requieren servicios de autenticación/autorización, sincronización, recuperación de credenciales por canal verificado, subida de evidencias y entregas, comunicaciones push, protección del almacenamiento y operación del servicio. Estos servicios no se presentan como conectados en esta entrega.
