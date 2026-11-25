package com.lygone.smarttvsimulator

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NowPlayingState(
    val isActive: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val appName: String = "",
    val albumArt: Bitmap? = null,
    val appPackage: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    /** Marca de tiempo (elapsedRealtime) en que se midió positionMs; permite animar la barra sin consultar. */
    val positionUpdatedAt: Long = 0L,
    val speed: Float = 1f
)

class MediaMonitorService : NotificationListenerService() {
    companion object {
        private val _nowPlaying = MutableStateFlow(NowPlayingState())
        val nowPlaying: StateFlow<NowPlayingState> = _nowPlaying
        private var activeController: MediaController? = null

        fun isPermissionGranted(context: Context): Boolean {
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            return enabled.contains(context.packageName)
        }

        fun togglePlayPause() {
            val state = activeController?.playbackState?.state
            if (state == PlaybackState.STATE_PLAYING) {
                activeController?.transportControls?.pause()
            } else {
                activeController?.transportControls?.play()
            }
        }

        fun skipNext() {
            activeController?.transportControls?.skipToNext()
        }

        fun skipPrevious() {
            activeController?.transportControls?.skipToPrevious()
        }

        fun openSourceApp(context: Context) {
            val pkg = _nowPlaying.value.appPackage
            if (pkg.isBlank()) return
            val pm = context.packageManager
            val intent = pm.getLeanbackLaunchIntentForPackage(pkg) ?: pm.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                runCatching {
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }

        fun refreshSessions(context: Context) {
            try {
                val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
                    ?: return
                val component = ComponentName(context, MediaMonitorService::class.java)
                val sessions = manager.getActiveSessions(component)
                val controller = sessions.firstOrNull {
                    it.playbackState?.state == PlaybackState.STATE_PLAYING
                } ?: sessions.firstOrNull {
                    it.playbackState?.state == PlaybackState.STATE_PAUSED ||
                        it.playbackState?.state == PlaybackState.STATE_BUFFERING
                }

                activeController = controller
                if (controller != null) {
                    val meta = controller.metadata
                    val ps = controller.playbackState
                    val pm = context.packageManager
                    val label = runCatching {
                        pm.getApplicationLabel(pm.getApplicationInfo(controller.packageName, 0)).toString()
                    }.getOrDefault("Multimedia")

                    val rawTitle = meta?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                        ?: "Reproducción activa"
                    val rawArtist = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
                        ?: label

                    // Cada consulta a metadata devuelve un Bitmap nuevo: se reutiliza el anterior si la pista no cambió
                    // para evitar parpadeos y recalcular colores en cada sondeo.
                    val prev = _nowPlaying.value
                    val sameTrack = prev.appPackage == controller.packageName &&
                        prev.title == rawTitle && prev.artist == rawArtist && prev.albumArt != null
                    val art = if (sameTrack) {
                        prev.albumArt
                    } else {
                        meta?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
                    }

                    _nowPlaying.value = NowPlayingState(
                        isActive = true,
                        isPlaying = ps?.state == PlaybackState.STATE_PLAYING,
                        title = rawTitle,
                        artist = rawArtist,
                        appName = label,
                        albumArt = art,
                        appPackage = controller.packageName,
                        positionMs = ps?.position ?: 0L,
                        durationMs = meta?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L,
                        positionUpdatedAt = ps?.lastPositionUpdateTime ?: 0L,
                        speed = ps?.playbackSpeed ?: 1f
                    )
                } else {
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                    if (audioManager?.isMusicActive == true) {
                        _nowPlaying.value = NowPlayingState(
                            isActive = true,
                            isPlaying = true,
                            title = "Audio / Video en reproducción",
                            artist = "Sonido activo del sistema",
                            appName = "Multimedia"
                        )
                    } else {
                        _nowPlaying.value = NowPlayingState(isActive = false)
                    }
                }
            } catch (_: SecurityException) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                if (audioManager?.isMusicActive == true) {
                    _nowPlaying.value = NowPlayingState(
                        isActive = true,
                        isPlaying = true,
                        title = "Audio / Video en reproducción",
                        artist = "Activa el permiso en Ajustes para ver carátula",
                        appName = "Sonido"
                    )
                } else {
                    _nowPlaying.value = NowPlayingState(isActive = false)
                }
            } catch (_: Exception) {
                _nowPlaying.value = NowPlayingState(isActive = false)
            }
        }
    }

    override fun onListenerConnected() {
        refreshSessions(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refreshSessions(this)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refreshSessions(this)
    }
}
