package com.lygone.smarttvsimulator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

data class WeatherInfo(
    val tempCelsius: Int,
    val conditionText: String,
    val isDay: Boolean,
    val code: Int
)

enum class EventCategory(val label: String) {
    ALL("Todo"),
    FOOTBALL("Fútbol"),
    MOVIES("Películas"),
    SERIES("Series")
}

data class UpcomingEvent(
    val category: EventCategory,
    val badge: String,
    val title: String,
    val subtitle: String,
    val schedule: String,
    val platform: String,
    val accentHex: Long,
    val imageUrl: String? = null,
    val linkUrl: String? = null
)

private data class RssItem(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val linkUrl: String?
)

object LiveInfoRepository {

    private const val WEATHER_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=-16.4090&longitude=-71.5375&current=temperature_2m,is_day,weather_code&timezone=America%2FLima"

    private const val NEWS_RSS_URL = "https://rpp.pe/feed"
    private const val SPORTS_RSS_URL = "https://rpp.pe/feed/deportes"
    private const val ENTERTAINMENT_RSS_URL = "https://rpp.pe/feed/entretenimiento"

    private val imageCache = ConcurrentHashMap<String, Bitmap>()

    fun fetchWeather(): WeatherInfo? {
        return try {
            val jsonStr = httpGet(WEATHER_URL) ?: return null
            val current = JSONObject(jsonStr).getJSONObject("current")
            val temp = current.getDouble("temperature_2m").roundToInt()
            val isDay = current.optInt("is_day", 1) == 1
            val code = current.optInt("weather_code", 0)
            WeatherInfo(
                tempCelsius = temp,
                conditionText = weatherDescription(code),
                isDay = isDay,
                code = code
            )
        } catch (_: Exception) {
            null
        }
    }

    fun fetchPeruNews(limit: Int = 10): List<String> {
        return parseRssItems(NEWS_RSS_URL, limit).map { it.title }
    }

    fun loadNetworkBitmap(urlStr: String?): Bitmap? {
        if (urlStr.isNullOrBlank()) return null
        val cleanUrl = urlStr.trim().replace("http://", "https://")
        imageCache[cleanUrl]?.let { return it }
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(cleanUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                instanceFollowRedirects = true
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/124.0 Mobile Safari/537.36"
                )
                setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
            }
            if (conn.responseCode in 200..299) {
                val bytes = conn.inputStream.use { it.readBytes() }
                if (bytes.isEmpty()) return null
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                var sample = 1
                while (bounds.outWidth / sample > 640 || bounds.outHeight / sample > 400) {
                    sample *= 2
                }
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
                if (bmp != null) {
                    imageCache[cleanUrl] = bmp
                }
                bmp
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    fun fetchBillboardEvents(): List<UpcomingEvent> {
        val events = ArrayList<UpcomingEvent>()

        val sportsItems = parseRssItems(SPORTS_RSS_URL, 6)
        if (sportsItems.isNotEmpty()) {
            sportsItems.take(4).forEachIndexed { idx, item ->
                events.add(
                    UpcomingEvent(
                        category = EventCategory.FOOTBALL,
                        badge = "FÚTBOL • EN AGENDA",
                        title = item.title,
                        subtitle = item.description.ifBlank { "Toca para ver la previa y cobertura en vivo" },
                        schedule = if (idx == 0) "Destacado Hoy" else "Próxima Jornada",
                        platform = "L1 Max / ESPN / YouTube",
                        accentHex = 0xFF10B981,
                        imageUrl = item.imageUrl ?: fallbackCoverFor(EventCategory.FOOTBALL, idx),
                        linkUrl = item.linkUrl
                    )
                )
            }
        } else {
            events.addAll(defaultFootballEvents())
        }

        val entItems = parseRssItems(ENTERTAINMENT_RSS_URL, 6)
        if (entItems.isNotEmpty()) {
            entItems.take(4).forEachIndexed { idx, item ->
                val isSeries = item.title.contains("serie", ignoreCase = true) ||
                    item.title.contains("temporada", ignoreCase = true) ||
                    item.title.contains("capítulo", ignoreCase = true) ||
                    idx % 2 == 1
                val cat = if (isSeries) EventCategory.SERIES else EventCategory.MOVIES
                events.add(
                    UpcomingEvent(
                        category = cat,
                        badge = if (isSeries) "SERIE • ESTRENO" else "PELÍCULA • CARTELERA",
                        title = item.title,
                        subtitle = item.description.ifBlank {
                            if (isSeries) "Toca para ver tráiler y episodios" else "Toca para ver tráiler y sinopsis"
                        },
                        schedule = "Esta semana",
                        platform = "YouTube / Xuper / Prime / Netflix",
                        accentHex = if (isSeries) 0xFFEC4899 else 0xFFF59E0B,
                        imageUrl = item.imageUrl ?: fallbackCoverFor(cat, idx),
                        linkUrl = item.linkUrl
                    )
                )
            }
        } else {
            events.addAll(defaultMediaEvents())
        }

        return events
    }

    fun getDefaultBillboardEvents(): List<UpcomingEvent> =
        defaultFootballEvents() + defaultMediaEvents()

    private fun fallbackCoverFor(cat: EventCategory, idx: Int): String = when (cat) {
        EventCategory.FOOTBALL -> if (idx % 2 == 0) {
            "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=640&q=80"
        } else {
            "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=640&q=80"
        }
        EventCategory.MOVIES -> "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=640&q=80"
        EventCategory.SERIES -> "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=640&q=80"
        EventCategory.ALL -> "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=640&q=80"
    }

    private fun defaultFootballEvents(): List<UpcomingEvent> = listOf(
        UpcomingEvent(
            category = EventCategory.FOOTBALL,
            badge = "FÚTBOL • LIGA 1 & SELECCIÓN",
            title = "Jornada de Fútbol Peruano e Internacional",
            subtitle = "Toca para buscar resúmenes y transmisión en vivo",
            schedule = "Hoy • 20:00 hrs",
            platform = "L1 Max / Movistar Deportes",
            accentHex = 0xFF10B981,
            imageUrl = fallbackCoverFor(EventCategory.FOOTBALL, 0)
        ),
        UpcomingEvent(
            category = EventCategory.FOOTBALL,
            badge = "FÚTBOL • CONMEBOL & CHAMPIONS",
            title = "Cruces de Fase Final y Partidos Estelares",
            subtitle = "Toca para ver horarios y goles en YouTube",
            schedule = "Mañana • 15:00 hrs",
            platform = "ESPN / Disney+ / YouTube",
            accentHex = 0xFF3B82F6,
            imageUrl = fallbackCoverFor(EventCategory.FOOTBALL, 1)
        )
    )

    private fun defaultMediaEvents(): List<UpcomingEvent> = listOf(
        UpcomingEvent(
            category = EventCategory.MOVIES,
            badge = "PELÍCULA • ESTRENO DESTACADO",
            title = "Cartelera de Estrenos y Acción 4K",
            subtitle = "Toca para ver tráilers oficiales",
            schedule = "Disponible ahora",
            platform = "Xuper / Prime Video / Max",
            accentHex = 0xFFF59E0B,
            imageUrl = fallbackCoverFor(EventCategory.MOVIES, 0)
        ),
        UpcomingEvent(
            category = EventCategory.SERIES,
            badge = "SERIE • NUEVA TEMPORADA",
            title = "Maratón de Series y Estrenos Semanales",
            subtitle = "Toca para explorar episodios y adelantos",
            schedule = "Este fin de semana",
            platform = "Netflix / Disney+ / YouTube",
            accentHex = 0xFFEC4899,
            imageUrl = fallbackCoverFor(EventCategory.SERIES, 1)
        )
    )

    private fun parseRssItems(urlStr: String, limit: Int): List<RssItem> {
        return try {
            val xml = httpGet(urlStr) ?: return emptyList()
            val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            val items = ArrayList<RssItem>()
            var insideItem = false
            var title = ""
            var desc = ""
            var imageUrl: String? = null
            var linkUrl: String? = null
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT && items.size < limit) {
                if (eventType == XmlPullParser.START_TAG) {
                    val tag = parser.name.lowercase()
                    if (tag == "item") {
                        insideItem = true
                        title = ""
                        desc = ""
                        imageUrl = null
                        linkUrl = null
                    } else if (insideItem) {
                        when {
                            tag == "title" -> title = parser.nextText().trim()
                            tag == "link" -> linkUrl = parser.nextText().trim()
                            tag == "description" || tag == "content:encoded" -> {
                                val raw = parser.nextText()
                                if (imageUrl == null) {
                                    imageUrl = extractImgSrc(raw)
                                }
                                if (desc.isBlank()) {
                                    desc = raw.replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ").trim()
                                }
                            }
                            tag.endsWith("content") || tag.endsWith("thumbnail") || tag == "enclosure" -> {
                                val urlAttr = parser.getAttributeValue(null, "url")
                                if (!urlAttr.isNullOrBlank() && imageUrl == null) {
                                    imageUrl = urlAttr
                                }
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.name.equals("item", ignoreCase = true)) {
                    if (title.isNotBlank()) {
                        items.add(RssItem(title = title, description = desc, imageUrl = imageUrl, linkUrl = linkUrl))
                    }
                    insideItem = false
                }
                eventType = parser.next()
            }
            items
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun extractImgSrc(html: String): String? {
        val match = Regex("<img[^>]+src=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(html)
        return match?.groupValues?.getOrNull(1)
    }

    private fun httpGet(urlStr: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 7000
                readTimeout = 7000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) LYGANE-SmartTV/2.3")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun weatherDescription(code: Int): String = when (code) {
        0 -> "Despejado"
        1, 2 -> "Parc. nublado"
        3 -> "Nublado"
        45, 48 -> "Neblina"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Lluvia"
        71, 73, 75 -> "Nieve"
        95, 96, 99 -> "Tormenta"
        else -> "Arequipa"
    }
}
