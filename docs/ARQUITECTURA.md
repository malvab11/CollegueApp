# Arquitectura KMP

- `shared/src/commonMain`: modelos serializables, capacidades, reglas escolares, persistencia abstracta, sesión y toda la interfaz Compose Multiplatform.
- `shared/src/androidMain`: almacenamiento Android y navegación Atrás de la plataforma.
- `shared/src/iosMain`: almacenamiento iOS y controlador compartido UIKit.
- `shared/src/commonTest`: pruebas de reglas de negocio y persistencia, ejecutables en los targets de prueba.
- `app`: contenedor Android de una única actividad.
- `iosApp`: contenedor SwiftUI para iPhone/iPad con proyecto y scheme compartido de Xcode.

No hay una segunda implementación de las pantallas en Swift. Los contenedores usan `SchoolApp`, y las fechas e identificadores no dependen de Java en código común.

## Identidad institucional

El escudo original de Albert Einstein de Celendín está en `app/src/main/res/mipmap-xxxhdpi/ic_logo.png`. `swift scripts/generate-brand-assets.swift` genera el recurso de Compose Multiplatform y los iconos de Android e iOS a partir de ese archivo, conservando sus colores y proporciones. Solo ajusta el margen blanco y las dimensiones necesarias para cada plataforma. El componente compartido `SchoolLogo` lo presenta sobre fondo blanco en ambos temas.

## Compilar

Android: `./gradlew :app:assembleDebug`

Reglas compartidas: `./gradlew :shared:jvmTest`

Framework de simulador: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`

Framework de dispositivo: `./gradlew :shared:linkDebugFrameworkIosArm64`

iOS: abrir `iosApp/Collegue.xcodeproj`, elegir el scheme Collegue y un simulador. La fase de compilación integra el framework con `embedAndSignAppleFrameworkForXcode`. Para dispositivo físico, seleccionar el equipo de firma de Apple en Xcode.

Android UI: `./gradlew :app:connectedDebugAndroidTest`

Se requiere un JDK 17 y Xcode completo para los targets Apple. No hace falta google-services.json.

## Validación de esta entrega

- 19 pruebas de reglas, permisos, sesión y persistencia aprobadas en el target JVM.
- Recorrido instrumentado aprobado en Android: credenciales, seis responsabilidades, tarea entre cuentas, recreación de actividad, cambio de hijo, pagos, tutoría, asistencia y matrícula.
- Android Lint completado sin errores.
- Frameworks KMP generados para `iosSimulatorArm64` e `iosArm64`.
- Aplicación SwiftUI compilada para simulador y dispositivo iPhone; el binario de dispositivo queda sin firma de distribución.
- Inicio de la aplicación y login revisados visualmente en Android e iOS.

Los paquetes de entrega se guardan en `dist/`. El ZIP de iOS está destinado al simulador Apple Silicon. Una instalación en iPhone físico requiere firmar la app con un equipo de desarrollo de Apple.
