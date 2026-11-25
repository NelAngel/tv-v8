@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.lygone.smarttvsimulator

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ───────────────────────────── Paleta Hisense VIDAA + Sunset ─────────────────────────────

private val Ink = Color(0xFF070B14)
private val SurfaceGlass = Color(0xC20B101D)
private val SurfaceCard = Color(0xE6141C2E)
private val TextHi = Color(0xFFFFFFFF)
private val TextLo = Color(0xFFD0D8EA)
private val AngelCyan = Color(0xFF4FA8FF)
private val VidaaTeal = Color(0xFF00D4C5)
private val Gold = Color(0xFFFFD166)

private enum class NavDestination(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Rounded.Home),
    Billboard("Cartelera", Icons.Rounded.LiveTv),
    Apps("Apps", Icons.Rounded.Apps),
    Favorites("Favoritos", Icons.Rounded.Star),
    Search("Buscar", Icons.Rounded.Search),
    Settings("Ajustes", Icons.Rounded.Settings)
}

// ───────────────────────────── Pantalla principal ─────────────────────────────

@Composable
fun LauncherApp(homeSignal: Int, packageSignal: Int, resumeSignal: Int) {
    val context = LocalContext.current
    val isTv = remember { isTelevision(context) }
    val dpad = remember { isTv || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN) }

    var apps by remember { mutableStateOf<List<TvApp>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var favorites by remember { mutableStateOf(Prefs.favorites(context)) }
    var recents by remember { mutableStateOf(Prefs.recents(context)) }
    var activeSection by remember { mutableStateOf(NavDestination.Home) }
    var search by remember { mutableStateOf("") }
    var showSettingsHub by remember { mutableStateOf(false) }
    val spotlight = remember { mutableStateOf<TvApp?>(null) }

    // Estado del sistema en vivo para la Estación de Ajustes (Wi-Fi, Bluetooth, Volumen)
    var quickStatus by remember(resumeSignal, showSettingsHub) {
        mutableStateOf(SystemSettingsHelper.readQuickStatus(context))
    }

    // Estados en vivo: Clima, Noticias de Perú y Cartelera con imágenes
    var weather by remember { mutableStateOf<WeatherInfo?>(null) }
    var peruNews by remember { mutableStateOf<List<String>>(emptyList()) }
    var billboardEvents by remember { mutableStateOf(LiveInfoRepository.getDefaultBillboardEvents()) }

    val nowPlaying by MediaMonitorService.nowPlaying.collectAsState()
    val hasMediaPermission = remember(resumeSignal, showSettingsHub) {
        MediaMonitorService.isPermissionGranted(context)
    }

    val now = rememberNow()
    val scope = rememberCoroutineScope()
    val firstFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }
    val settingsFocus = remember { FocusRequester() }

    LaunchedEffect(packageSignal, reload) {
        apps = withContext(Dispatchers.Default) { AppRepository.load(context, isTv) }
        loaded = true
    }

    LaunchedEffect(Unit) {
        while (true) {
            MediaMonitorService.refreshSessions(context)
            delay(2500L)
        }
    }

    LaunchedEffect(reload) {
        while (true) {
            val w = withContext(Dispatchers.IO) { LiveInfoRepository.fetchWeather() }
            if (w != null) weather = w
            delay(30 * 60 * 1000L)
        }
    }
    LaunchedEffect(reload) {
        while (true) {
            val news = withContext(Dispatchers.IO) { LiveInfoRepository.fetchPeruNews(limit = 10) }
            if (news.isNotEmpty()) peruNews = news
            val events = withContext(Dispatchers.IO) { LiveInfoRepository.fetchBillboardEvents() }
            if (events.isNotEmpty()) billboardEvents = events
            delay(15 * 60 * 1000L)
        }
    }

    val byPackage = remember(apps) { apps.associateBy { it.packageName } }
    val recentApps = remember(apps, recents) { recents.mapNotNull { byPackage[it] } }
    val lastUsedApp = recentApps.firstOrNull() ?: apps.firstOrNull()
    val favoriteApps = remember(apps, favorites) { apps.filter { it.packageName in favorites } }

    val homeCarouselApps = remember(apps, recentApps, favoriteApps) {
        val ordered = LinkedHashMap<String, TvApp>()
        recentApps.forEach { ordered[it.packageName] = it }
        favoriteApps.forEach { ordered[it.packageName] = it }
        apps.forEach { ordered[it.packageName] = it }
        ordered.values.toList()
    }

    val query = search.trim()
    val searchResults = remember(apps, query) {
        if (query.isEmpty()) apps else apps.filter { it.name.contains(query, ignoreCase = true) }
    }

    val openApp: (TvApp) -> Unit = { app ->
        if (AppRepository.launch(context, app)) {
            recents = Prefs.pushRecent(context, app.packageName)
        }
    }
    val toggleFavorite: (TvApp) -> Unit = { app ->
        favorites = if (app.packageName in favorites) favorites - app.packageName else favorites + app.packageName
        Prefs.saveFavorites(context, favorites)
    }
    val onFocused: (TvApp) -> Unit = { spotlight.value = it }

    fun goHome() {
        activeSection = NavDestination.Home
        search = ""
        showSettingsHub = false
        scope.launch {
            if (dpad) {
                delay(120)
                runCatching { firstFocus.requestFocus() }
            }
        }
    }

    LaunchedEffect(homeSignal) { if (homeSignal > 0) goHome() }
    LaunchedEffect(loaded) {
        if (loaded && dpad) {
            delay(220)
            runCatching { firstFocus.requestFocus() }
        }
    }
    LaunchedEffect(activeSection) {
        if (activeSection == NavDestination.Search) {
            delay(120)
            runCatching { searchFocus.requestFocus() }
        }
    }

    // Al cerrar el Centro de Control con mando, el foco vuelve al botón "Ajustes" del dock.
    val closeHub: () -> Unit = {
        showSettingsHub = false
        scope.launch {
            delay(80)
            if (dpad) runCatching { settingsFocus.requestFocus() }
        }
    }

    BackHandler(enabled = showSettingsHub || activeSection != NavDestination.Home) {
        if (showSettingsHub) {
            closeHub()
        } else {
            goHome()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AngelCyan, background = Ink, surface = SurfaceCard,
            onBackground = TextHi, onSurface = TextHi
        )
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Ink)) {
            val wide = maxWidth >= 720.dp
            val pad = if (wide) 52.dp else 20.dp
            // Ventanas bajas (DeX redimensionado, móvil en horizontal): se compacta para que nada se recorte.
            val compactH = maxHeight < 540.dp
            val showAllLabels = maxWidth >= 980.dp
            val panelMaxHeight = (maxHeight - 110.dp).coerceAtLeast(220.dp)
            val cardW = if (wide) (if (compactH) 168.dp else 196.dp) else 132.dp
            val cardH = if (wide) (if (compactH) 96.dp else 114.dp) else 84.dp

            ScenicWallpaperLayer(spotlight)

            Column(
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    // Con el Centro de Control abierto, el mando no debe poder enfocar lo que queda detrás.
                    .focusProperties { canFocus = !showSettingsHub }
            ) {
                // 1. Barra superior
                TopStatusHeader(now = now, weather = weather, wide = wide, pad = pad)

                // 2. Contenido central con transiciones fluidas
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                        targetState = Pair(loaded, activeSection),
                        // Solo fundido: el deslizamiento vertical movía listas completas y dejaba dos pantallas a la vez.
                        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
                        label = "section-transition",
                        modifier = Modifier.fillMaxSize()
                    ) { (isLoaded, section) ->
                        when {
                            !isLoaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Cargando aplicaciones…", color = TextHi, fontSize = 16.sp)
                            }

                            section == NavDestination.Home -> {
                                VidaaHomeStage(
                                    wide = wide,
                                    compactH = compactH,
                                    pad = pad,
                                    cardW = cardW,
                                    cardH = cardH,
                                    lastUsedApp = lastUsedApp,
                                    hasRecents = recentApps.isNotEmpty(),
                                    focusedApp = spotlight.value ?: lastUsedApp,
                                    carouselApps = homeCarouselApps,
                                    favorites = favorites,
                                    peruNews = peruNews,
                                    nowPlaying = nowPlaying,
                                    firstFocus = firstFocus,
                                    onFocused = onFocused,
                                    onOpen = openApp,
                                    onToggleFavorite = toggleFavorite,
                                    onOpenMediaSource = { MediaMonitorService.openSourceApp(context) },
                                    onOpenBillboard = { activeSection = NavDestination.Billboard }
                                )
                            }

                            section == NavDestination.Billboard -> {
                                BillboardSectionOverlay(
                                    events = billboardEvents,
                                    wide = wide,
                                    pad = pad,
                                    onEventClick = { ev ->
                                        SystemSettingsHelper.openWebOrYouTubeSearch(
                                            context = context,
                                            url = ev.linkUrl,
                                            searchQuery = ev.title
                                        )
                                    },
                                    onRefresh = { reload++ },
                                    onClose = { goHome() }
                                )
                            }

                            section == NavDestination.Apps -> {
                                AppGridOverlay(
                                    title = "Todas las aplicaciones (${apps.size})",
                                    apps = apps,
                                    emptyText = "No se encontraron aplicaciones compatibles.",
                                    cardW = cardW,
                                    cardH = cardH,
                                    wide = wide,
                                    pad = pad,
                                    favorites = favorites,
                                    focusFirst = firstFocus,
                                    onFocused = onFocused,
                                    onOpen = openApp,
                                    onToggleFavorite = toggleFavorite
                                )
                            }

                            section == NavDestination.Favorites -> {
                                AppGridOverlay(
                                    title = "Favoritos",
                                    apps = favoriteApps,
                                    emptyText = "Aún no tienes favoritos.\nMantén pulsado OK sobre una app para fijarla aquí.",
                                    cardW = cardW,
                                    cardH = cardH,
                                    wide = wide,
                                    pad = pad,
                                    favorites = favorites,
                                    focusFirst = firstFocus,
                                    onFocused = onFocused,
                                    onOpen = openApp,
                                    onToggleFavorite = toggleFavorite
                                )
                            }

                            section == NavDestination.Search -> {
                                Column(Modifier.fillMaxSize()) {
                                    Box(Modifier.padding(horizontal = pad, vertical = 8.dp)) {
                                        SearchField(
                                            value = search,
                                            onChange = { search = it },
                                            onClose = { goHome() },
                                            focusRequester = searchFocus,
                                            modifier = Modifier.fillMaxWidth(if (wide) 0.55f else 1f)
                                        )
                                    }
                                    AppGridOverlay(
                                        title = if (query.isEmpty()) "Explorar aplicaciones" else "Resultados para “$query”",
                                        apps = searchResults,
                                        emptyText = "No se encontró ninguna aplicación.",
                                        cardW = cardW,
                                        cardH = cardH,
                                        wide = wide,
                                        pad = pad,
                                        favorites = favorites,
                                        focusFirst = null,
                                        onFocused = onFocused,
                                        onOpen = openApp,
                                        onToggleFavorite = toggleFavorite
                                    )
                                }
                            }

                            else -> Unit
                        }
                    }
                }

                // 3. Dock inferior tipo píldora
                BottomVidaaDock(
                    selected = if (showSettingsHub) NavDestination.Settings else activeSection,
                    wide = wide,
                    pad = pad,
                    showAllLabels = showAllLabels,
                    settingsFocus = settingsFocus,
                    onSelect = { dest ->
                        if (dest == NavDestination.Settings) {
                            quickStatus = SystemSettingsHelper.readQuickStatus(context)
                            showSettingsHub = !showSettingsHub
                        } else {
                            showSettingsHub = false
                            activeSection = dest
                            if (dest != NavDestination.Search) search = ""
                        }
                    }
                )
            }

            // Centro de Control Flotante estilo Smart TV (reemplaza la lista lateral anterior)
            SmartTvControlCenterOverlay(
                visible = showSettingsHub,
                wide = wide,
                pad = pad,
                status = quickStatus,
                hasMediaPermission = hasMediaPermission,
                maxPanelHeight = panelMaxHeight,
                onWifi = { SystemSettingsHelper.openWifiSettings(context) },
                onBluetooth = { SystemSettingsHelper.openBluetoothSettings(context) },
                onVolumeDown = {
                    SystemSettingsHelper.adjustMusicVolume(context, -1)
                    quickStatus = SystemSettingsHelper.readQuickStatus(context)
                },
                onVolumeUp = {
                    SystemSettingsHelper.adjustMusicVolume(context, +1)
                    quickStatus = SystemSettingsHelper.readQuickStatus(context)
                },
                onSoundSettings = { SystemSettingsHelper.openSoundSettings(context) },
                onDisplay = { SystemSettingsHelper.openDisplaySettings(context) },
                onSystemSettings = { SystemSettingsHelper.openAllSettings(context) },
                onGrantMediaPermission = { SystemSettingsHelper.openNotificationListenerSettings(context) },
                onRefreshData = { showSettingsHub = false; reload++ },
                onClose = closeHub
            )
        }
    }
}

// ───────────────────────────── Fondo Escénico Sin Rayas ─────────────────────────────

@Composable
private fun ScenicWallpaperLayer(spotlight: State<TvApp?>) {
    val accent = spotlight.value?.accent ?: AngelCyan
    val animatedAccent by animateColorAsState(
        targetValue = accent,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "accent-glow"
    )

    val pulseTransition = rememberInfiniteTransition(label = "ambient-pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.24f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.launcher_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.0f to Color(0x45060A14),
                    0.42f to Color(0x14060A14),
                    0.72f to Color(0x88060A14),
                    1.0f to Color(0xE6050811)
                )
            )
        )

        Box(
            Modifier.fillMaxSize().drawBehind {
                val maxR = size.width.coerceAtLeast(size.height) * 0.95f
                drawRect(
                    Brush.radialGradient(
                        0.0f to animatedAccent.copy(alpha = pulseAlpha),
                        0.32f to animatedAccent.copy(alpha = pulseAlpha * 0.62f),
                        0.64f to animatedAccent.copy(alpha = pulseAlpha * 0.22f),
                        0.88f to animatedAccent.copy(alpha = 0.04f),
                        1.0f to Color.Transparent,
                        center = Offset(size.width * 0.28f, size.height * 0.80f),
                        radius = maxR
                    )
                )
            }
        )
    }
}

// ───────────────────────────── Header Superior ─────────────────────────────

@Composable
private fun TopStatusHeader(
    now: State<Long>,
    weather: WeatherInfo?,
    wide: Boolean,
    pad: Dp
) {
    val context = LocalContext.current
    val ms = now.value

    Row(
        Modifier.fillMaxWidth().padding(horizontal = pad, vertical = if (wide) 14.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.clip(RoundedCornerShape(50))
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(VidaaTeal))
            Text(
                "LYGANE TV",
                color = TextHi,
                fontSize = if (wide) 13.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                maxLines = 1
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.clip(RoundedCornerShape(22.dp))
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                .padding(horizontal = if (wide) 18.dp else 12.dp, vertical = if (wide) 9.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (wide) 14.dp else 9.dp)
        ) {
            Box(
                Modifier.size(if (wide) 38.dp else 30.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AngelCyan, VidaaTeal))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Ink,
                    modifier = Modifier.size(if (wide) 22.dp else 18.dp)
                )
            }

            // En pantallas estrechas se omite nombre y fecha para que el bloque no desborde.
            if (wide) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "ANGEL",
                            color = TextHi,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            Icons.Rounded.Wifi,
                            contentDescription = "Wi-Fi",
                            tint = VidaaTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(formatDateShort(ms), color = TextLo, fontSize = 11.sp, maxLines = 1)
                }
            }

            if (weather != null) {
                Box(
                    Modifier.width(1.dp).height(if (wide) 34.dp else 26.dp)
                        .background(Color.White.copy(alpha = 0.20f))
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = weatherIcon(weather),
                        contentDescription = weather.conditionText,
                        tint = Gold,
                        modifier = Modifier.size(if (wide) 26.dp else 20.dp)
                    )
                    Column {
                        Text(
                            text = "${weather.tempCelsius}°C",
                            color = TextHi,
                            fontSize = if (wide) 20.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (wide) {
                            Text(text = weather.conditionText, color = TextLo, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }

            Box(
                Modifier.width(1.dp).height(if (wide) 34.dp else 26.dp)
                    .background(Color.White.copy(alpha = 0.22f))
            )

            Text(
                text = formatTime(context, ms),
                color = TextHi,
                fontSize = if (wide) 40.sp else 26.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0x80000000),
                        offset = Offset(0f, 3f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

/** Botón redondo con anillo de foco visible (los botones sueltos con clickable no mostraban el foco del mando). */
@Composable
private fun FocusIconButton(
    icon: ImageVector,
    desc: String,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    tint: Color = TextHi,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.1f else 1f, spring(stiffness = 500f), label = "icon-btn-scale")
    Box(
        modifier.size(size)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(if (focused) Color.White else SurfaceGlass)
            .border(1.dp, if (focused) Color.White else Color.White.copy(alpha = 0.16f), CircleShape)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = if (focused) Ink else tint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

private fun weatherIcon(info: WeatherInfo): ImageVector = when (info.code) {
    0 -> if (info.isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay
    1, 2, 3, 45, 48 -> Icons.Rounded.Cloud
    51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Rounded.WaterDrop
    95, 96, 99 -> Icons.Rounded.Thunderstorm
    else -> Icons.Rounded.Cloud
}

// ───────────────────────────── Escenario Principal + Minirreproductor + Noticias ─────────────────────────────

@Composable
private fun VidaaHomeStage(
    wide: Boolean,
    compactH: Boolean,
    pad: Dp,
    cardW: Dp,
    cardH: Dp,
    lastUsedApp: TvApp?,
    hasRecents: Boolean,
    focusedApp: TvApp?,
    carouselApps: List<TvApp>,
    favorites: Set<String>,
    peruNews: List<String>,
    nowPlaying: NowPlayingState,
    firstFocus: FocusRequester,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit,
    onOpenMediaSource: () -> Unit,
    onOpenBillboard: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Saludo + última app (el minirreproductor ya no compite por este espacio)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = pad),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextHi, fontWeight = FontWeight.ExtraBold)) {
                            append("¡Hola, ")
                        }
                        withStyle(SpanStyle(color = AngelCyan, fontWeight = FontWeight.ExtraBold)) {
                            append("ANGEL")
                        }
                        withStyle(SpanStyle(color = TextHi, fontWeight = FontWeight.ExtraBold)) {
                            append("!")
                        }
                    },
                    fontSize = if (wide) (if (compactH) 36.sp else 48.sp) else 28.sp,
                    lineHeight = if (wide) (if (compactH) 40.sp else 52.sp) else 32.sp,
                    maxLines = 1,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0x99000000),
                            offset = Offset(0f, 4f),
                            blurRadius = 12f
                        )
                    )
                )
                if (!compactH) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Estamos listos para usar",
                        color = TextHi.copy(alpha = 0.94f),
                        fontSize = if (wide) 22.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0x88000000),
                                offset = Offset(0f, 2f),
                                blurRadius = 8f
                            )
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier.width(if (wide) 76.dp else 52.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(AngelCyan, VidaaTeal)))
                )
            }

            // En pantallas estrechas la primera tarjeta ya lleva la etiqueta RECIENTE; así el saludo no se aprieta.
            if (lastUsedApp != null && wide) {
                LastUsedAppBadge(
                    app = lastUsedApp,
                    isRecent = hasRecents,
                    wide = true,
                    onOpen = { onOpen(lastUsedApp) }
                )
            }
        }

        // Minirreproductor: tarjeta propia, de ancho completo, solo cuando hay sonido activo
        AnimatedVisibility(
            visible = nowPlaying.isActive,
            enter = fadeIn(tween(260)) + expandVertically(tween(260)),
            exit = fadeOut(tween(160)) + shrinkVertically(tween(200))
        ) {
            Column {
                Spacer(Modifier.height(if (wide) 14.dp else 10.dp))
                MiniMediaPlayerCard(
                    state = nowPlaying,
                    wide = wide,
                    compact = compactH,
                    modifier = Modifier.padding(horizontal = pad).widthIn(max = 680.dp),
                    onPrevious = { MediaMonitorService.skipPrevious() },
                    onTogglePlay = { MediaMonitorService.togglePlayPause() },
                    onNext = { MediaMonitorService.skipNext() },
                    onOpenApp = onOpenMediaSource
                )
            }
        }

        // En ventanas bajas se omiten las noticias para que el carrusel nunca se recorte
        if (peruNews.isNotEmpty() && !compactH) {
            Spacer(Modifier.height(if (wide) 14.dp else 10.dp))
            PeruNewsTicker(
                headlines = peruNews,
                wide = wide,
                pad = pad,
                onOpenBillboard = onOpenBillboard
            )
        }

        Spacer(Modifier.height(if (wide) 12.dp else 8.dp))

        if (focusedApp != null) {
            Row(
                Modifier.padding(horizontal = pad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.size(8.dp)
                        .clip(CircleShape)
                        .background(focusedApp.accent)
                )
                AnimatedContent(
                    targetState = focusedApp.name,
                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(140)) },
                    label = "focused-app-title"
                ) { appName ->
                    Text(
                        text = appName,
                        color = TextHi,
                        fontSize = if (wide) 15.sp else 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                if (wide) {
                    Text(
                        text = "•  OK para abrir  •  Mantén OK para favorito",
                        color = TextLo.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = pad, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(if (wide) 18.dp else 12.dp)
        ) {
            itemsIndexed(carouselApps, key = { _, a -> a.packageName }) { index, app ->
                val isLastUsed = index == 0 && hasRecents
                VidaaAppCard(
                    app = app,
                    width = cardW,
                    height = cardH,
                    wide = wide,
                    isFavorite = app.packageName in favorites,
                    isLastUsed = isLastUsed,
                    focusRequester = if (index == 0) firstFocus else null,
                    onFocused = onFocused,
                    onOpen = onOpen,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

// ───────────────────────────── Minirreproductor Multimedia ─────────────────────────────

@Composable
private fun MiniMediaPlayerCard(
    state: NowPlayingState,
    wide: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onOpenApp: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val art = remember(state.albumArt) { state.albumArt?.asImageBitmap() }
    val accentTarget = remember(state.albumArt) {
        state.albumArt?.let { bmp -> runCatching { AppRepository.dominantColor(bmp) }.getOrNull() } ?: VidaaTeal
    }
    val accent by animateColorAsState(accentTarget, tween(500), label = "player-accent")

    // La barra avanza sola entre sondeos: se interpola con la hora del último dato real.
    var livePos by remember { mutableLongStateOf(0L) }
    LaunchedEffect(state.positionMs, state.positionUpdatedAt, state.isPlaying, state.durationMs, state.speed) {
        while (true) {
            var pos = state.positionMs
            if (state.isPlaying && state.positionUpdatedAt > 0L) {
                pos += ((SystemClock.elapsedRealtime() - state.positionUpdatedAt) * state.speed).toLong()
            }
            livePos = if (state.durationMs > 0L) pos.coerceIn(0L, state.durationMs) else pos.coerceAtLeast(0L)
            delay(500)
        }
    }

    val artSize = if (wide) 64.dp else 52.dp
    val artShape = RoundedCornerShape(14.dp)
    val artSource = remember { MutableInteractionSource() }
    val artFocused by artSource.collectIsFocusedAsState()
    val showProgress = state.durationMs > 0L && !compact

    Row(
        modifier.fillMaxWidth()
            .clip(shape)
            .background(Color(0xF00B1322))
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.34f), Color.Transparent)))
            .border(1.2.dp, accent.copy(alpha = 0.55f), shape)
            .padding(horizontal = if (wide) 14.dp else 10.dp, vertical = if (wide) 12.dp else 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (wide) 14.dp else 10.dp)
    ) {
        Box(
            Modifier.size(artSize)
                .clip(artShape)
                .background(accent.copy(alpha = 0.22f))
                .then(if (artFocused) Modifier.border(2.5.dp, Color.White, artShape) else Modifier)
                .clickable(interactionSource = artSource, indication = null, role = Role.Button, onClick = onOpenApp),
            contentAlignment = Alignment.Center
        ) {
            if (art != null) {
                Image(
                    bitmap = art,
                    contentDescription = state.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(artSize * 0.5f)
                )
            }
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EqualizerBars(playing = state.isPlaying, color = accent)
                Text(
                    text = if (state.appName.isNotBlank()) state.appName.uppercase() else "SONIDO ACTIVO",
                    color = accent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = state.title,
                color = TextHi,
                fontSize = if (wide) 16.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = state.artist,
                color = TextLo.copy(alpha = 0.85f),
                fontSize = if (wide) 12.sp else 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showProgress) {
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(formatDuration(livePos), color = TextLo, fontSize = 10.sp, maxLines = 1)
                    Box(
                        Modifier.weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                    ) {
                        Box(
                            Modifier.fillMaxWidth((livePos.toFloat() / state.durationMs).coerceIn(0.001f, 1f))
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(accent)
                        )
                    }
                    Text(formatDuration(state.durationMs), color = TextLo, fontSize = 10.sp, maxLines = 1)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (wide) 8.dp else 4.dp)
        ) {
            MediaControlButton(
                icon = Icons.Rounded.SkipPrevious,
                desc = "Anterior",
                primary = false,
                size = if (wide) 42.dp else 36.dp,
                accent = accent,
                onClick = onPrevious
            )
            MediaControlButton(
                icon = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                desc = if (state.isPlaying) "Pausar" else "Reproducir",
                primary = true,
                size = if (wide) 52.dp else 44.dp,
                accent = accent,
                onClick = onTogglePlay
            )
            MediaControlButton(
                icon = Icons.Rounded.SkipNext,
                desc = "Siguiente",
                primary = false,
                size = if (wide) 42.dp else 36.dp,
                accent = accent,
                onClick = onNext
            )
        }
    }
}

@Composable
private fun EqualizerBars(playing: Boolean, color: Color) {
    val transition = rememberInfiniteTransition(label = "eq")
    val a by transition.animateFloat(
        initialValue = 0.25f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(480), RepeatMode.Reverse), label = "eq-a"
    )
    val b by transition.animateFloat(
        initialValue = 0.30f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(640), RepeatMode.Reverse), label = "eq-b"
    )
    val c by transition.animateFloat(
        initialValue = 0.20f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(560), RepeatMode.Reverse), label = "eq-c"
    )
    Row(
        Modifier.height(12.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(a, b, c).forEach { f ->
            Box(
                Modifier.width(3.dp)
                    .fillMaxHeight(if (playing) f else 0.3f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
private fun MediaControlButton(
    icon: ImageVector,
    desc: String,
    primary: Boolean,
    size: Dp,
    accent: Color,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.12f else 1f, spring(stiffness = 500f), label = "media-btn-scale")
    val bg = when {
        focused -> Color.White
        primary -> accent
        else -> Color.White.copy(alpha = 0.12f)
    }
    val tint = if (focused || primary) Ink else TextHi

    Box(
        Modifier.size(size)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(bg)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, tint = tint, modifier = Modifier.size(size * 0.54f))
    }
}

private fun formatDuration(ms: Long): String {
    val total = (ms / 1000L).coerceAtLeast(0L)
    val h = total / 3600L
    val m = (total % 3600L) / 60L
    val s = total % 60L
    return if (h > 0L) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%d:%02d", m, s)
}

@Composable
private fun PeruNewsTicker(
    headlines: List<String>,
    wide: Boolean,
    pad: Dp,
    onOpenBillboard: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(headlines) {
        index = 0
        while (headlines.size > 1) {
            delay(7000L)
            index = (index + 1) % headlines.size
        }
    }

    val currentHeadline = headlines.getOrElse(index) { "" }
    val shape = RoundedCornerShape(12.dp)

    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = pad)
            .clip(shape)
            .background(Color(0x99090E1A))
            .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
            .padding(horizontal = if (wide) 14.dp else 10.dp, vertical = if (wide) 7.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            Modifier.clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFD92B3A))
                .padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Rounded.Newspaper,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(if (wide) 13.dp else 11.dp)
            )
            Text(
                text = "PERÚ",
                color = Color.White,
                fontSize = if (wide) 10.sp else 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        AnimatedContent(
            targetState = currentHeadline,
            transitionSpec = {
                (slideInVertically { it } + fadeIn(tween(350))) togetherWith
                    (slideOutVertically { -it } + fadeOut(tween(300)))
            },
            label = "news-ticker",
            modifier = Modifier.weight(1f)
        ) { headline ->
            Text(
                text = headline,
                color = TextHi.copy(alpha = 0.95f),
                fontSize = if (wide) 13.sp else 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        val evSource = remember { MutableInteractionSource() }
        val evFocused by evSource.collectIsFocusedAsState()
        Row(
            Modifier.clip(RoundedCornerShape(8.dp))
                .background(if (evFocused) Color.White else Gold.copy(alpha = 0.22f))
                .clickable(interactionSource = evSource, indication = null, role = Role.Button, onClick = onOpenBillboard)
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Rounded.SportsSoccer, null, tint = if (evFocused) Ink else Gold, modifier = Modifier.size(12.dp))
            if (wide) {
                Text(
                    text = "PRÓXIMOS EVENTOS",
                    color = if (evFocused) Ink else Gold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// ───────────────────────────── Apartado de Cartelera con Imágenes y Clic Directo ─────────────────────────────

@Composable
private fun BillboardSectionOverlay(
    events: List<UpcomingEvent>,
    wide: Boolean,
    pad: Dp,
    onEventClick: (UpcomingEvent) -> Unit,
    onRefresh: () -> Unit,
    onClose: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(EventCategory.ALL) }
    val filtered = remember(events, selectedFilter) {
        if (selectedFilter == EventCategory.ALL) events else events.filter { it.category == selectedFilter }
    }

    Column(Modifier.fillMaxSize()) {
        // Fila 1: título + acciones (antes, título y 6 botones competían en una sola fila y el título se aplastaba)
        Row(
            Modifier.fillMaxWidth().padding(horizontal = pad, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Cartelera, Series y Fútbol",
                    color = TextHi,
                    fontSize = if (wide) 24.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Elige una tarjeta para ver el tráiler, resumen o noticia",
                    color = TextLo,
                    fontSize = if (wide) 13.sp else 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FocusIconButton(
                icon = Icons.Rounded.Refresh,
                desc = "Actualizar cartelera",
                tint = VidaaTeal,
                onClick = onRefresh
            )
            FocusIconButton(
                icon = Icons.Rounded.Close,
                desc = "Volver a Inicio",
                onClick = onClose
            )
        }

        // Fila 2: filtros
        LazyRow(
            contentPadding = PaddingValues(horizontal = pad, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(EventCategory.values().toList()) { _, cat ->
                BillboardFilterChip(
                    label = cat.label,
                    selected = selectedFilter == cat,
                    wide = wide,
                    onClick = { selectedFilter = cat }
                )
            }
        }

        // El padding horizontal va dentro de la cuadrícula: así la tarjeta enfocada (que crece) ya no se recorta en los bordes.
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = if (wide) 300.dp else 230.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = pad, end = pad, top = 10.dp, bottom = 20.dp)
        ) {
            if (filtered.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "No hay eventos en esta categoría por ahora.",
                        color = TextLo,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }
            items(filtered) { event ->
                BillboardEventCard(
                    event = event,
                    wide = wide,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun BillboardFilterChip(label: String, selected: Boolean, wide: Boolean, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.06f else 1f, spring(stiffness = 500f), label = "chip-scale")
    val bg = when {
        focused -> Color.White
        selected -> AngelCyan
        else -> SurfaceGlass
    }
    val fg = if (focused || selected) Ink else TextHi
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(bg)
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
            .clickable(interactionSource = source, indication = null, role = Role.Tab, onClick = onClick)
            .padding(horizontal = if (wide) 18.dp else 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = fg, fontSize = if (wide) 13.sp else 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun BillboardEventCard(
    event: UpcomingEvent,
    wide: Boolean,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        if (focused) 1.04f else 1f,
        spring(dampingRatio = 0.8f, stiffness = 420f),
        label = "billboard-card-scale"
    )
    val accent = Color(event.accentHex)
    val shape = RoundedCornerShape(20.dp)
    val icon = when (event.category) {
        EventCategory.FOOTBALL -> Icons.Rounded.SportsSoccer
        EventCategory.MOVIES -> Icons.Rounded.Movie
        EventCategory.SERIES -> Icons.Rounded.Tv
        EventCategory.ALL -> Icons.Rounded.LiveTv
    }

    var coverBitmap by remember(event.imageUrl) { mutableStateOf<Bitmap?>(null) }
    val coverAlpha by animateFloatAsState(if (coverBitmap != null) 1f else 0f, tween(450), label = "cover-fade")
    LaunchedEffect(event.imageUrl) {
        if (!event.imageUrl.isNullOrBlank()) {
            coverBitmap = withContext(Dispatchers.IO) {
                LiveInfoRepository.loadNetworkBitmap(event.imageUrl)
            }
        }
    }

    Column(
        Modifier.fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(SurfaceCard)
            .border(
                width = if (focused) 2.5.dp else 1.dp,
                color = if (focused) Color.White else accent.copy(alpha = 0.45f),
                shape = shape
            )
            .onKeyEvent { e ->
                if (e.type == KeyEventType.KeyUp && (e.key == Key.Enter || e.key == Key.DirectionCenter)) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
    ) {
        Box(
            Modifier.fillMaxWidth()
                .height(if (wide) 148.dp else 116.dp)
                .background(
                    Brush.linearGradient(
                        listOf(accent.copy(alpha = 0.55f), Color(0xFF0E172A))
                    )
                )
        ) {
            val cover = coverBitmap
            if (cover != null) {
                Image(
                    bitmap = cover.asImageBitmap(),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().graphicsLayer { alpha = coverAlpha }
                )
            } else {
                Box(
                    Modifier.fillMaxSize().drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to accent.copy(alpha = 0.50f),
                                1f to Color.Transparent,
                                center = Offset(size.width * 0.82f, size.height * 0.30f),
                                radius = size.width * 0.65f
                            )
                        )
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.24f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0.0f to Color(0x22050811),
                        0.55f to Color.Transparent,
                        1.0f to Color(0xEE0D1424)
                    )
                )
            )

            Row(
                Modifier.align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC070B14))
                    .border(1.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
                Text(
                    text = event.badge,
                    color = TextHi,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }

            Box(
                Modifier.align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Gold)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = event.schedule,
                    color = Ink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = event.title,
                color = TextHi,
                fontSize = if (wide) 15.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = event.subtitle,
                color = TextLo.copy(alpha = 0.85f),
                fontSize = 11.sp,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.platform,
                    color = VidaaTeal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Abrir", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = "Abrir",
                        tint = Gold,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LastUsedAppBadge(
    app: TvApp,
    isRecent: Boolean,
    wide: Boolean,
    onOpen: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.06f else 1f, spring(stiffness = 450f), label = "resume-scale")
    val shape = RoundedCornerShape(18.dp)

    Row(
        Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(SurfaceGlass)
            .border(
                width = if (focused) 2.5.dp else 1.dp,
                color = if (focused) Color.White else app.accent.copy(alpha = 0.65f),
                shape = shape
            )
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onOpen)
            .padding(horizontal = if (wide) 16.dp else 12.dp, vertical = if (wide) 11.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(if (wide) 44.dp else 34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(app.accent.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Image(app.icon, contentDescription = app.name, modifier = Modifier.size(if (wide) 32.dp else 24.dp))
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Rounded.History, contentDescription = null, tint = VidaaTeal, modifier = Modifier.size(13.dp))
                Text(
                    text = if (isRecent) "ÚLTIMA APP USADA" else "LISTA PARA ABRIR",
                    color = VidaaTeal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = app.name,
                color = TextHi,
                fontSize = if (wide) 15.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = if (wide) 180.dp else 110.dp)
            )
        }

        if (wide) {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(if (focused) Color.White else AngelCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Abrir", tint = Ink, modifier = Modifier.size(19.dp))
            }
        }
    }
}

// ───────────────────────────── Tarjetas Panorámicas Estilo VIDAA ─────────────────────────────

@Composable
private fun VidaaAppCard(
    app: TvApp,
    width: Dp,
    height: Dp,
    wide: Boolean,
    isFavorite: Boolean,
    isLastUsed: Boolean,
    focusRequester: FocusRequester?,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        if (focused) 1.09f else 1f,
        spring(dampingRatio = 0.76f, stiffness = 420f),
        label = "card-scale"
    )
    val iconScale by animateFloatAsState(
        if (focused) 1.08f else 1f,
        spring(dampingRatio = 0.72f, stiffness = 450f),
        label = "icon-scale"
    )
    val gradientAlpha by animateFloatAsState(
        if (focused) 0.62f else 0.28f,
        tween(240),
        label = "card-grad-alpha"
    )
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(18.dp)
    val currentOnFocused by rememberUpdatedState(onFocused)

    LaunchedEffect(focused) { if (focused) currentOnFocused(app) }

    Column(
        Modifier.width(width).onKeyEvent { e ->
            if (e.type == KeyEventType.KeyUp && e.key == Key.Menu) {
                onToggleFavorite(app)
                true
            } else {
                false
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(width, height)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(shape)
                .background(SurfaceCard)
                .background(
                    Brush.linearGradient(
                        listOf(
                            app.accent.copy(alpha = gradientAlpha),
                            Color(0xD90D1322)
                        )
                    )
                )
                .border(
                    width = if (focused) 2.5.dp else 1.dp,
                    color = if (focused) Color.White else Color.White.copy(alpha = 0.16f),
                    shape = shape
                )
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .combinedClickable(
                    interactionSource = source,
                    indication = null,
                    role = Role.Button,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleFavorite(app)
                    },
                    onClick = { onOpen(app) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                app.icon,
                contentDescription = app.name,
                modifier = Modifier.size(height * 0.56f)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
            )

            if (isLastUsed) {
                Box(
                    Modifier.align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(VidaaTeal)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("RECIENTE", color = Ink, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            if (isFavorite) {
                Box(
                    Modifier.align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xB3000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Star, null, tint = Gold, modifier = Modifier.size(14.dp))
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = focused,
                enter = fadeIn(tween(180)) + slideInVertically(tween(200)) { it / 2 },
                exit = fadeOut(tween(140)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Color(0xD9060913))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.name,
                        color = TextHi,
                        fontSize = if (wide) 12.sp else 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ───────────────────────────── Cuadrícula para Apps / Favoritos / Búsqueda ─────────────────────────────

@Composable
private fun AppGridOverlay(
    title: String,
    apps: List<TvApp>,
    emptyText: String,
    cardW: Dp,
    cardH: Dp,
    wide: Boolean,
    pad: Dp,
    favorites: Set<String>,
    focusFirst: FocusRequester?,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit
) {
    if (apps.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(pad),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.Apps, null, tint = TextLo.copy(alpha = 0.6f), modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(emptyText, color = TextHi, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cardW + 16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = pad, end = pad, top = 8.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                title,
                color = TextHi,
                fontSize = if (wide) 22.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        gridItemsIndexed(apps, key = { _, a -> a.packageName }) { index, app ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VidaaAppCard(
                    app = app,
                    width = cardW,
                    height = cardH,
                    wide = wide,
                    isFavorite = app.packageName in favorites,
                    isLastUsed = false,
                    focusRequester = if (index == 0) focusFirst else null,
                    onFocused = onFocused,
                    onOpen = onOpen,
                    onToggleFavorite = onToggleFavorite
                )
                Text(
                    text = app.name,
                    color = TextHi,
                    fontSize = if (wide) 13.sp else 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp).width(cardW),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ───────────────────────────── Dock Inferior Tipo Píldora ─────────────────────────────

@Composable
private fun BottomVidaaDock(
    selected: NavDestination,
    wide: Boolean,
    pad: Dp,
    showAllLabels: Boolean,
    settingsFocus: FocusRequester,
    onSelect: (NavDestination) -> Unit
) {
    val shape = RoundedCornerShape(50)
    // Dock compacto y centrado: con 6 pestañas el dock a ancho completo desbordaba en ventanas de 720–950 dp.
    Box(
        Modifier.fillMaxWidth().padding(horizontal = pad, vertical = if (wide) 12.dp else 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.clip(shape)
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
                .padding(horizontal = if (wide) 10.dp else 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(if (wide) 6.dp else 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.values().forEach { item ->
                val isSelected = item == selected
                val showLabel = showAllLabels || isSelected
                val source = remember { MutableInteractionSource() }
                val focused by source.collectIsFocusedAsState()
                val scale by animateFloatAsState(if (focused) 1.06f else 1f, spring(stiffness = 500f), label = "dock-scale")

                val bg by animateColorAsState(
                    when {
                        focused -> Color.White
                        isSelected -> Color.White.copy(alpha = 0.18f)
                        else -> Color.Transparent
                    },
                    tween(180),
                    label = "dock-bg"
                )
                val fg = when {
                    focused -> Ink
                    isSelected -> TextHi
                    else -> TextLo
                }

                Row(
                    Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
                        .clip(RoundedCornerShape(50))
                        .background(bg)
                        .then(if (item == NavDestination.Settings) Modifier.focusRequester(settingsFocus) else Modifier)
                        .clickable(interactionSource = source, indication = null, role = Role.Tab) { onSelect(item) }
                        .padding(
                            horizontal = when {
                                showAllLabels -> if (wide) 20.dp else 14.dp
                                isSelected -> 14.dp
                                else -> 9.dp
                            },
                            vertical = if (wide) 9.dp else 7.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(item.icon, contentDescription = item.label, tint = fg, modifier = Modifier.size(if (wide) 20.dp else 18.dp))
                    if (showLabel) {
                        Text(
                            text = item.label,
                            color = fg,
                            fontSize = if (wide) 14.sp else 11.sp,
                            fontWeight = if (isSelected || focused) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ───────────────────────────── Centro de Control Flotante (Estilo Smart TV / DeX) ─────────────────────────────

@Composable
private fun SmartTvControlCenterOverlay(
    visible: Boolean,
    wide: Boolean,
    pad: Dp,
    status: DeviceQuickStatus,
    hasMediaPermission: Boolean,
    maxPanelHeight: Dp,
    onWifi: () -> Unit,
    onBluetooth: () -> Unit,
    onVolumeDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onSoundSettings: () -> Unit,
    onDisplay: () -> Unit,
    onSystemSettings: () -> Unit,
    onGrantMediaPermission: () -> Unit,
    onRefreshData: () -> Unit,
    onClose: () -> Unit
) {
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(visible) {
        if (visible) {
            delay(130)
            runCatching { firstFocus.requestFocus() }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            // Con pointerInput (no clickable) el fondo oscuro deja de ser un destino de foco invisible para el mando.
            Box(
                Modifier.fillMaxSize()
                    .background(Color(0x8803060D))
                    .pointerInput(Unit) { detectTapGestures { onClose() } }
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(240)) + scaleIn(initialScale = 0.94f, animationSpec = tween(260)) +
                slideInVertically(tween(260)) { 36 },
            exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.94f, animationSpec = tween(180)) +
                slideOutVertically(tween(180)) { 30 },
            // Anclado sobre el dock (donde está "Ajustes"), no flotando bajo la cabecera.
            modifier = Modifier.align(if (wide) Alignment.BottomEnd else Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = pad, end = pad, bottom = if (wide) 84.dp else 70.dp)
        ) {
            val hubShape = RoundedCornerShape(26.dp)
            Column(
                Modifier.widthIn(min = if (wide) 440.dp else 300.dp, max = if (wide) 520.dp else 380.dp)
                    .heightIn(max = maxPanelHeight)
                    .clip(hubShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xF7111A2E), Color(0xFA090E1B))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.18f), hubShape)
                    .pointerInput(Unit) { detectTapGestures { } }
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cabecera compacta
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(VidaaTeal))
                        Text(
                            text = "Centro de control",
                            color = TextHi,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    FocusIconButton(icon = Icons.Rounded.Close, desc = "Cerrar", size = 36.dp, onClick = onClose)
                }

                // Fila 1: Mosaicos Grandes Interactivos Wi-Fi & Bluetooth con estado real
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ControlHubMainTile(
                        icon = Icons.Rounded.Wifi,
                        title = "Wi-Fi",
                        statusText = when {
                            status.wifiConnected -> "Conectado"
                            status.ethernetConnected -> "Ethernet"
                            else -> "Abrir redes"
                        },
                        active = status.wifiConnected || status.ethernetConnected,
                        accent = VidaaTeal,
                        modifier = Modifier.weight(1f).focusRequester(firstFocus),
                        onClick = onWifi
                    )
                    ControlHubMainTile(
                        icon = Icons.Rounded.Bluetooth,
                        title = "Bluetooth",
                        statusText = if (status.bluetoothEnabled) "Activado" else "Desactivado",
                        active = status.bluetoothEnabled,
                        accent = AngelCyan,
                        modifier = Modifier.weight(1f),
                        onClick = onBluetooth
                    )
                }

                // Fila 2: Barra de Volumen Interactivo (- / Nivel % / + / Ajustes de sonido)
                Row(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Volumen",
                        tint = Gold,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Volumen Multimedia", color = TextHi, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${status.volumePercent}%", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier.fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                        ) {
                            Box(
                                Modifier.fillMaxWidth((status.volumePercent / 100f).coerceIn(0.04f, 1f))
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(AngelCyan, Gold)))
                            )
                        }
                    }
                    HubCircleAction(icon = Icons.AutoMirrored.Rounded.VolumeDown, desc = "Bajar volumen", onClick = onVolumeDown)
                    HubCircleAction(icon = Icons.AutoMirrored.Rounded.VolumeUp, desc = "Subir volumen", onClick = onVolumeUp)
                }

                // Fila 3: Accesos directos compactos (Sonido, Pantalla DeX, Actualizar, Sistema)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ControlHubMiniTile(
                        icon = Icons.Rounded.GraphicEq,
                        label = "Audio",
                        accent = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f),
                        onClick = onSoundSettings
                    )
                    ControlHubMiniTile(
                        icon = Icons.Rounded.DisplaySettings,
                        label = "Pantalla",
                        accent = Color(0xFFA78BFA),
                        modifier = Modifier.weight(1f),
                        onClick = onDisplay
                    )
                    ControlHubMiniTile(
                        icon = Icons.Rounded.Refresh,
                        label = "Recargar",
                        accent = VidaaTeal,
                        modifier = Modifier.weight(1f),
                        onClick = onRefreshData
                    )
                    ControlHubMiniTile(
                        icon = Icons.Rounded.Settings,
                        label = "Sistema",
                        accent = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f),
                        onClick = onSystemSettings
                    )
                }

                if (!hasMediaPermission) {
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AngelCyan.copy(alpha = 0.22f))
                            .border(1.dp, AngelCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable(onClick = onGrantMediaPermission)
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Rounded.NotificationsActive, null, tint = AngelCyan, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Activar carátula y controles en minirreproductor",
                            color = TextHi,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlHubMainTile(
    icon: ImageVector,
    title: String,
    statusText: String,
    active: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, spring(stiffness = 480f), label = "hub-tile-scale")
    val shape = RoundedCornerShape(18.dp)
    val bg = when {
        focused -> Color.White
        active -> accent.copy(alpha = 0.24f)
        else -> Color.White.copy(alpha = 0.07f)
    }
    val fg = if (focused) Ink else TextHi
    val subFg = if (focused) Ink.copy(alpha = 0.72f) else if (active) accent else TextLo

    Row(
        modifier.clip(shape)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(bg)
            .border(
                width = if (focused) 2.dp else 1.dp,
                color = if (focused) Color.White else if (active) accent.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.10f),
                shape = shape
            )
            .onKeyEvent { e ->
                if (e.type == KeyEventType.KeyUp && (e.key == Key.Enter || e.key == Key.DirectionCenter)) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(38.dp)
                .clip(CircleShape)
                .background(if (focused) accent.copy(alpha = 0.25f) else if (active) accent else Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (focused || active) Ink else TextHi,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = fg, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(statusText, color = subFg, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}

@Composable
private fun ControlHubMiniTile(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val shape = RoundedCornerShape(14.dp)
    val bg = if (focused) Color.White else Color.White.copy(alpha = 0.06f)
    val fg = if (focused) Ink else TextHi

    Column(
        modifier.clip(shape)
            .background(bg)
            .border(1.dp, if (focused) Color.White else Color.White.copy(alpha = 0.08f), shape)
            .onKeyEvent { e ->
                if (e.type == KeyEventType.KeyUp && (e.key == Key.Enter || e.key == Key.DirectionCenter)) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (focused) Ink else accent, modifier = Modifier.size(19.dp))
        Text(label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun HubCircleAction(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    Box(
        Modifier.size(40.dp)
            .clip(CircleShape)
            .background(if (focused) Color.White else Color.White.copy(alpha = 0.12f))
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, tint = if (focused) Ink else TextHi, modifier = Modifier.size(20.dp))
    }
}

// ───────────────────────────── Buscador y Utilidades ─────────────────────────────

@Composable
private fun SearchField(
    value: String,
    onChange: (String) -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier.height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, null, tint = AngelCyan, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) Text("Buscar aplicación…", color = TextLo, fontSize = 15.sp)
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = TextHi, fontSize = 15.sp),
                cursorBrush = SolidColor(AngelCyan),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }
        FocusIconButton(icon = Icons.Rounded.Close, desc = "Cerrar", size = 34.dp, onClick = onClose)
    }
}

@Composable
private fun rememberNow(): State<Long> {
    val now = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now.value = System.currentTimeMillis()
            delay(10_000)
        }
    }
    return now
}

private fun isTelevision(context: Context): Boolean {
    val mode = (context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager)?.currentModeType
    return mode == Configuration.UI_MODE_TYPE_TELEVISION ||
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

private fun formatTime(context: Context, millis: Long): String =
    android.text.format.DateFormat.getTimeFormat(context).format(Date(millis))

private fun formatDateShort(millis: Long): String {
    val spanish = Locale("es", "PE")
    return SimpleDateFormat("EEE, d MMM", spanish).format(Date(millis))
        .replaceFirstChar { it.titlecase(spanish) }
}
