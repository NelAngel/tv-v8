# LYGANE Smart TV — v2.3 (modo App, compatible con Samsung DeX)

App (no launcher) para Android TV, teléfonos y Samsung DeX. Kotlin + Jetpack Compose.
No declara la categoría HOME, por eso DeX la acepta como una app normal.

## Novedades de diseño en v2.3
- **Cabecera limpia**: se quitaron los botones duplicados (Cartelera/Ajustes ya están en el dock). En pantallas estrechas el bloque de estado ya no desborda.
- **Minirreproductor rediseñado**: tarjeta propia a ancho completo con carátula, color de acento tomado de la carátula, ecualizador animado, barra de progreso con tiempos y botones grandes con foco visible. Pulsa la carátula para abrir la app de origen.
- **Dock compacto y centrado**: ya no se corta en ventanas de 720–950 dp; las etiquetas solo se muestran todas cuando hay espacio.
- **Centro de Control anclado sobre el dock** (donde está "Ajustes"), con scroll si la ventana es baja, botones de volumen más grandes y sin "focos invisibles" detrás del fondo oscuro.
- **Cartelera**: título y acciones en filas separadas, filtros en fila propia, tarjetas de altura uniforme, portada con fundido y sin recortes al enfocar en los bordes.
- **Ventanas bajas** (DeX redimensionado, móvil horizontal): el diseño se compacta para que el carrusel nunca se recorte.
- Transiciones entre secciones solo con fundido (sin deslizar listas enteras).
- **Wi-Fi/Ethernet**: se añadió el permiso `ACCESS_NETWORK_STATE`; antes el mosaico de Wi-Fi siempre decía "Abrir redes".

## Mini reproductor
Pulsa **Ajustes → Activar carátula y controles** y concede *Acceso a notificaciones* a LYGANE.

## Compilar
- **GitHub Actions**: sube la carpeta → *Actions → Build Android APK → Run workflow*.
- **Android Studio**: abre la carpeta y *Build → Build APK(s)*.
- **Terminal**: `gradle :app:assembleDebug` (JDK 17, Android SDK 35).
