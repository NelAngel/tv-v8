package com.lygone.smarttvsimulator

import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.Locale
import android.graphics.Color as AndroidColor

@Immutable
data class TvApp(
    val name: String,
    val packageName: String,
    val component: ComponentName,
    val icon: ImageBitmap,
    val accent: Color
)

data class DeviceQuickStatus(
    val wifiConnected: Boolean,
    val bluetoothEnabled: Boolean,
    val volumePercent: Int,
    val ethernetConnected: Boolean = false
)

object AppRepository {
    private const val ICON_PX = 192
    private val NeutralAccent = Color(0xFF4F7BD9)

    fun load(context: Context, preferTv: Boolean): List<TvApp> {
        val pm = context.packageManager
        val categories = if (preferTv) {
            listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)
        } else {
            listOf(Intent.CATEGORY_LAUNCHER, Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val seen = HashSet<String>()
        val result = ArrayList<TvApp>()
        for (category in categories) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            val infos = try {
                pm.queryIntentActivities(query, 0)
            } catch (_: Exception) {
                emptyList()
            }
            for (info in infos) {
                val activity = info.activityInfo ?: continue
                val pkg = activity.packageName
                if (pkg == context.packageName || !seen.add(pkg)) continue
                try {
                    val bitmap = drawableToBitmap(info.loadIcon(pm), ICON_PX)
                    result.add(
                        TvApp(
                            name = info.loadLabel(pm).toString(),
                            packageName = pkg,
                            component = ComponentName(pkg, activity.name),
                            icon = bitmap.asImageBitmap(),
                            accent = dominantColor(bitmap)
                        )
                    )
                } catch (_: Exception) {
                }
            }
        }
        return result.sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    fun launch(context: Context, app: TvApp): Boolean {
        val direct = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(app.component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        if (tryStart(context, direct)) return true

        val pm = context.packageManager
        val fallback = pm.getLeanbackLaunchIntentForPackage(app.packageName)
            ?: pm.getLaunchIntentForPackage(app.packageName)
        return fallback != null && tryStart(context, fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun tryStart(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }

    private fun drawableToBitmap(drawable: Drawable, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    internal fun dominantColor(source: Bitmap): Color {
        val side = 16
        val small = Bitmap.createScaledBitmap(source, side, side, true)
        val pixels = IntArray(side * side)
        small.getPixels(pixels, 0, side, 0, 0, side, side)
        val hsv = FloatArray(3)
        var r = 0f
        var g = 0f
        var b = 0f
        var total = 0f
        for (p in pixels) {
            if (AndroidColor.alpha(p) < 128) continue
            AndroidColor.colorToHSV(p, hsv)
            val weight = hsv[1] * hsv[2] + 0.02f
            r += AndroidColor.red(p) * weight
            g += AndroidColor.green(p) * weight
            b += AndroidColor.blue(p) * weight
            total += weight
        }
        if (total == 0f) return NeutralAccent
        AndroidColor.RGBToHSV((r / total).toInt(), (g / total).toInt(), (b / total).toInt(), hsv)
        if (hsv[1] < 0.12f) return NeutralAccent
        hsv[1] = hsv[1].coerceAtLeast(0.5f)
        hsv[2] = hsv[2].coerceIn(0.55f, 0.95f)
        return Color(AndroidColor.HSVToColor(hsv))
    }
}

object Prefs {
    private fun prefs(context: Context) = context.getSharedPreferences("smart_tv", Context.MODE_PRIVATE)

    fun favorites(context: Context): Set<String> =
        prefs(context).getStringSet("favorites", emptySet())?.toSet() ?: emptySet()

    fun saveFavorites(context: Context, favorites: Set<String>) {
        prefs(context).edit().putStringSet("favorites", favorites).apply()
    }

    fun recents(context: Context): List<String> =
        (prefs(context).getString("recents", "") ?: "").split(",").filter { it.isNotBlank() }

    fun pushRecent(context: Context, packageName: String): List<String> {
        val updated = (listOf(packageName) + recents(context).filter { it != packageName }).take(12)
        prefs(context).edit().putString("recents", updated.joinToString(",")).apply()
        return updated
    }
}

object SystemSettingsHelper {
    fun readQuickStatus(context: Context): DeviceQuickStatus {
        val caps = runCatching {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val net = cm?.activeNetwork
            if (net == null) null else cm?.getNetworkCapabilities(net)
        }.getOrNull()
        val wifiOk = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val ethernetOk = caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true

        val btOk = runCatching {
            val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            bm?.adapter?.isEnabled == true
        }.getOrDefault(false)

        val volPct = runCatching {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val cur = am?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
            val max = (am?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15).coerceAtLeast(1)
            ((cur * 100f) / max).toInt().coerceIn(0, 100)
        }.getOrDefault(50)

        return DeviceQuickStatus(
            wifiConnected = wifiOk,
            bluetoothEnabled = btOk,
            volumePercent = volPct,
            ethernetConnected = ethernetOk
        )
    }

    fun adjustMusicVolume(context: Context, deltaSteps: Int) {
        runCatching {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val dir = if (deltaSteps >= 0) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, dir, AudioManager.FLAG_SHOW_UI)
        }
    }

    // Abre la actividad completa de Wi-Fi (funciona en Samsung DeX sin ser bloqueado como Settings.Panel)
    fun openWifiSettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    fun openBluetoothSettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    fun openSoundSettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_SOUND_SETTINGS))
    }

    fun openDisplaySettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }

    fun openAllSettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_SETTINGS))
    }

    fun openNotificationListenerSettings(context: Context) {
        startSafe(context, Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    fun openWebOrYouTubeSearch(context: Context, url: String?, searchQuery: String) {
        if (!url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://"))) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (tryStart(context, webIntent)) return
        }
        val encoded = Uri.encode(searchQuery)
        val ytSearch = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        tryStart(context, ytSearch)
    }

    private fun tryStart(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }

    private fun startSafe(context: Context, intent: Intent) {
        if (!tryStart(context, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))) {
            runCatching {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }
}
