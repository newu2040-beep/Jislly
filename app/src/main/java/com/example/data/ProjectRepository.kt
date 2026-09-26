package com.example.data

import android.content.Context
import com.example.model.Adjustments
import com.example.model.CropConfig
import com.example.model.DoodleStroke
import com.example.model.FrameConfig
import com.example.model.OffsetPoint
import com.example.model.ProjectEntity
import com.example.model.StickerLayer
import com.example.model.TextLayer
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

data class ProjectEditorState(
    val filterId: String = "original",
    val filterIntensity: Float = 1.0f,
    val adjustments: Adjustments = Adjustments(),
    val cropConfig: CropConfig = CropConfig(),
    val rotationDegrees: Float = 0f,
    val isFlippedH: Boolean = false,
    val isFlippedV: Boolean = false,
    val frameConfig: FrameConfig = FrameConfig(),
    val stickers: List<StickerLayer> = emptyList(),
    val textLayers: List<TextLayer> = emptyList(),
    val doodleStrokes: List<DoodleStroke> = emptyList()
)

class ProjectRepository(private val context: Context, private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val templates: Flow<List<ProjectEntity>> = projectDao.getTemplates()
    val favorites: Flow<List<ProjectEntity>> = projectDao.getFavorites()

    suspend fun getProjectById(id: Long): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun saveProject(
        id: Long = 0,
        title: String,
        thumbnailUri: String?,
        originalImageUri: String,
        state: ProjectEditorState,
        isFavorite: Boolean = false,
        isTemplate: Boolean = false,
        width: Int = 1080,
        height: Int = 1080
    ): Long {
        val json = serializeState(state)
        val entity = ProjectEntity(
            id = id,
            title = title,
            thumbnailUri = thumbnailUri,
            originalImageUri = originalImageUri,
            createdAt = if (id == 0L) System.currentTimeMillis() else (getProjectById(id)?.createdAt ?: System.currentTimeMillis()),
            updatedAt = System.currentTimeMillis(),
            isFavorite = isFavorite,
            isTemplate = isTemplate,
            canvasWidth = width,
            canvasHeight = height,
            projectDataJson = json
        )
        return if (id == 0L) {
            projectDao.insertProject(entity)
        } else {
            projectDao.updateProject(entity)
            id
        }
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        projectDao.setFavorite(id, !currentFavorite)
    }

    suspend fun deleteProject(id: Long) {
        projectDao.deleteProjectById(id)
    }

    suspend fun seedInitialProjectsIfNeeded() {
        // Create initial sample projects matching the mockup if database is empty
        val sample1 = ProjectEntity(
            title = "Golden Sunset",
            originalImageUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_SUNSET}",
            thumbnailUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_SUNSET}",
            canvasWidth = 1080,
            canvasHeight = 1080,
            projectDataJson = serializeState(
                ProjectEditorState(
                    filterId = "fuji_astia",
                    filterIntensity = 0.85f,
                    adjustments = Adjustments(temperature = 20f, vignette = 15f),
                    frameConfig = FrameConfig("floral_icy_blue", 20f, "#8FBFE3"),
                    textLayers = listOf(
                        TextLayer(
                            text = "sunset mood ♡",
                            xNorm = 0.82f,
                            yNorm = 0.16f,
                            fontFamilyKey = "caveat",
                            colorHex = "#FFF9EE",
                            fontSizeSp = 28f
                        )
                    )
                )
            )
        )

        val sample2 = ProjectEntity(
            title = "Cafe Afternoon",
            originalImageUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_COFFEE}",
            thumbnailUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_COFFEE}",
            canvasWidth = 1080,
            canvasHeight = 1080,
            projectDataJson = serializeState(
                ProjectEditorState(
                    filterId = "kodak_gold",
                    filterIntensity = 0.9f,
                    adjustments = Adjustments(temperature = 15f),
                    frameConfig = FrameConfig("polaroid_classic", 24f, "#FFFDF8"),
                    textLayers = listOf(
                        TextLayer(
                            text = "Good Day ♡",
                            xNorm = 0.35f,
                            yNorm = 0.38f,
                            fontFamilyKey = "caveat",
                            colorHex = "#FFF9EE",
                            fontSizeSp = 30f
                        )
                    ),
                    stickers = listOf(
                        StickerLayer(stickerId = "sweet_0", xNorm = 0.2f, yNorm = 0.85f, scale = 1.2f)
                    )
                )
            )
        )

        val sample3 = ProjectEntity(
            title = "Retro Camera Scrapbook",
            originalImageUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_CAMERA}",
            thumbnailUri = "res://${BuiltInAssets.SAMPLE_DRAWABLE_CAMERA}",
            canvasWidth = 1080,
            canvasHeight = 1080,
            projectDataJson = serializeState(
                ProjectEditorState(
                    filterId = "portra_400",
                    filterIntensity = 0.8f,
                    frameConfig = FrameConfig("washi_tape_corners", 18f, "#FFF9EE"),
                    textLayers = listOf(
                        TextLayer(
                            text = "good things\ntake time ✦",
                            xNorm = 0.75f,
                            yNorm = 0.18f,
                            fontFamilyKey = "caveat",
                            colorHex = "#FFF9EE",
                            fontSizeSp = 26f
                        )
                    ),
                    stickers = listOf(
                        StickerLayer(stickerId = "bow_0", xNorm = 0.85f, yNorm = 0.82f, scale = 1.3f),
                        StickerLayer(stickerId = "sweet_1", xNorm = 0.18f, yNorm = 0.82f, scale = 1.4f)
                    )
                )
            )
        )

        projectDao.insertProject(sample1)
        projectDao.insertProject(sample2)
        projectDao.insertProject(sample3)
    }

    fun serializeState(state: ProjectEditorState): String {
        val root = JSONObject()
        root.put("filterId", state.filterId)
        root.put("filterIntensity", state.filterIntensity.toDouble())
        root.put("rotationDegrees", state.rotationDegrees.toDouble())
        root.put("isFlippedH", state.isFlippedH)
        root.put("isFlippedV", state.isFlippedV)

        // Adjustments
        val adj = JSONObject().apply {
            put("exposure", state.adjustments.exposure.toDouble())
            put("brightness", state.adjustments.brightness.toDouble())
            put("contrast", state.adjustments.contrast.toDouble())
            put("highlights", state.adjustments.highlights.toDouble())
            put("shadows", state.adjustments.shadows.toDouble())
            put("saturation", state.adjustments.saturation.toDouble())
            put("vibrance", state.adjustments.vibrance.toDouble())
            put("temperature", state.adjustments.temperature.toDouble())
            put("tint", state.adjustments.tint.toDouble())
            put("sharpen", state.adjustments.sharpen.toDouble())
            put("vignette", state.adjustments.vignette.toDouble())
            put("grain", state.adjustments.grain.toDouble())
            put("fade", state.adjustments.fade.toDouble())
            put("clarity", state.adjustments.clarity.toDouble())
            put("dehaze", state.adjustments.dehaze.toDouble())
            put("hue", state.adjustments.hue.toDouble())
            put("channelRed", state.adjustments.channelRed.toDouble())
            put("channelGreen", state.adjustments.channelGreen.toDouble())
            put("channelBlue", state.adjustments.channelBlue.toDouble())
        }
        root.put("adjustments", adj)

        // Frame
        val frame = JSONObject().apply {
            put("frameId", state.frameConfig.frameId)
            put("paddingDp", state.frameConfig.paddingDp.toDouble())
            put("colorHex", state.frameConfig.colorHex)
            put("cornerRadiusDp", state.frameConfig.cornerRadiusDp.toDouble())
            put("hasShadow", state.frameConfig.hasShadow)
        }
        root.put("frame", frame)

        // Stickers
        val stArr = JSONArray()
        for (st in state.stickers) {
            val sObj = JSONObject().apply {
                put("id", st.id)
                put("stickerId", st.stickerId)
                put("customUri", st.customUri ?: "")
                put("xNorm", st.xNorm.toDouble())
                put("yNorm", st.yNorm.toDouble())
                put("scale", st.scale.toDouble())
                put("rotation", st.rotation.toDouble())
                put("opacity", st.opacity.toDouble())
                put("isFlipped", st.isFlipped)
                put("tintHex", st.tintHex ?: "")
            }
            stArr.put(sObj)
        }
        root.put("stickers", stArr)

        // Text Layers
        val txtArr = JSONArray()
        for (t in state.textLayers) {
            val tObj = JSONObject().apply {
                put("id", t.id)
                put("text", t.text)
                put("xNorm", t.xNorm.toDouble())
                put("yNorm", t.yNorm.toDouble())
                put("scale", t.scale.toDouble())
                put("rotation", t.rotation.toDouble())
                put("fontFamilyKey", t.fontFamilyKey)
                put("colorHex", t.colorHex)
                put("fontSizeSp", t.fontSizeSp.toDouble())
                put("letterSpacingSp", t.letterSpacingSp.toDouble())
                put("opacity", t.opacity.toDouble())
                put("hasBackgroundBadge", t.hasBackgroundBadge)
                put("badgeColorHex", t.badgeColorHex)
                put("hasOutline", t.hasOutline)
                put("outlineColorHex", t.outlineColorHex)
                put("hasShadow", t.hasShadow)
            }
            txtArr.put(tObj)
        }
        root.put("textLayers", txtArr)

        // Doodle Strokes
        val dArr = JSONArray()
        for (d in state.doodleStrokes) {
            val dObj = JSONObject().apply {
                put("id", d.id)
                put("brushType", d.brushType)
                put("colorHex", d.colorHex)
                put("strokeWidth", d.strokeWidth.toDouble())
                val pts = JSONArray()
                for (pt in d.points) {
                    val p = JSONObject().apply {
                        put("x", pt.xNorm.toDouble())
                        put("y", pt.yNorm.toDouble())
                    }
                    pts.put(p)
                }
                put("points", pts)
            }
            dArr.put(dObj)
        }
        root.put("doodleStrokes", dArr)

        return root.toString()
    }

    fun deserializeState(json: String): ProjectEditorState {
        if (json.isBlank()) return ProjectEditorState()
        return try {
            val root = JSONObject(json)
            val filterId = root.optString("filterId", "original")
            val filterIntensity = root.optDouble("filterIntensity", 1.0).toFloat()
            val rotationDegrees = root.optDouble("rotationDegrees", 0.0).toFloat()
            val isFlippedH = root.optBoolean("isFlippedH", false)
            val isFlippedV = root.optBoolean("isFlippedV", false)

            val adjObj = root.optJSONObject("adjustments")
            val adjustments = if (adjObj != null) {
                Adjustments(
                    exposure = adjObj.optDouble("exposure", 0.0).toFloat(),
                    brightness = adjObj.optDouble("brightness", 0.0).toFloat(),
                    contrast = adjObj.optDouble("contrast", 0.0).toFloat(),
                    highlights = adjObj.optDouble("highlights", 0.0).toFloat(),
                    shadows = adjObj.optDouble("shadows", 0.0).toFloat(),
                    saturation = adjObj.optDouble("saturation", 0.0).toFloat(),
                    vibrance = adjObj.optDouble("vibrance", 0.0).toFloat(),
                    temperature = adjObj.optDouble("temperature", 0.0).toFloat(),
                    tint = adjObj.optDouble("tint", 0.0).toFloat(),
                    sharpen = adjObj.optDouble("sharpen", 0.0).toFloat(),
                    vignette = adjObj.optDouble("vignette", 0.0).toFloat(),
                    grain = adjObj.optDouble("grain", 0.0).toFloat(),
                    fade = adjObj.optDouble("fade", 0.0).toFloat(),
                    clarity = adjObj.optDouble("clarity", 0.0).toFloat(),
                    dehaze = adjObj.optDouble("dehaze", 0.0).toFloat(),
                    hue = adjObj.optDouble("hue", 0.0).toFloat(),
                    channelRed = adjObj.optDouble("channelRed", 0.0).toFloat(),
                    channelGreen = adjObj.optDouble("channelGreen", 0.0).toFloat(),
                    channelBlue = adjObj.optDouble("channelBlue", 0.0).toFloat()
                )
            } else Adjustments()

            val fObj = root.optJSONObject("frame")
            val frameConfig = if (fObj != null) {
                FrameConfig(
                    frameId = fObj.optString("frameId", "none"),
                    paddingDp = fObj.optDouble("paddingDp", 16.0).toFloat(),
                    colorHex = fObj.optString("colorHex", "#FFF9EE"),
                    cornerRadiusDp = fObj.optDouble("cornerRadiusDp", 12.0).toFloat(),
                    hasShadow = fObj.optBoolean("hasShadow", true)
                )
            } else FrameConfig()

            val stickers = mutableListOf<StickerLayer>()
            val stArr = root.optJSONArray("stickers")
            if (stArr != null) {
                for (i in 0 until stArr.length()) {
                    val s = stArr.getJSONObject(i)
                    stickers.add(
                        StickerLayer(
                            id = s.optString("id"),
                            stickerId = s.optString("stickerId"),
                            customUri = s.optString("customUri").takeIf { it.isNotBlank() },
                            xNorm = s.optDouble("xNorm", 0.5).toFloat(),
                            yNorm = s.optDouble("yNorm", 0.5).toFloat(),
                            scale = s.optDouble("scale", 1.0).toFloat(),
                            rotation = s.optDouble("rotation", 0.0).toFloat(),
                            opacity = s.optDouble("opacity", 1.0).toFloat(),
                            isFlipped = s.optBoolean("isFlipped", false),
                            tintHex = s.optString("tintHex").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }

            val textLayers = mutableListOf<TextLayer>()
            val txtArr = root.optJSONArray("textLayers")
            if (txtArr != null) {
                for (i in 0 until txtArr.length()) {
                    val t = txtArr.getJSONObject(i)
                    textLayers.add(
                        TextLayer(
                            id = t.optString("id"),
                            text = t.optString("text", "Text"),
                            xNorm = t.optDouble("xNorm", 0.5).toFloat(),
                            yNorm = t.optDouble("yNorm", 0.5).toFloat(),
                            scale = t.optDouble("scale", 1.0).toFloat(),
                            rotation = t.optDouble("rotation", 0.0).toFloat(),
                            fontFamilyKey = t.optString("fontFamilyKey", "caveat"),
                            colorHex = t.optString("colorHex", "#263443"),
                            fontSizeSp = t.optDouble("fontSizeSp", 26.0).toFloat(),
                            letterSpacingSp = t.optDouble("letterSpacingSp", 0.0).toFloat(),
                            opacity = t.optDouble("opacity", 1.0).toFloat(),
                            hasBackgroundBadge = t.optBoolean("hasBackgroundBadge", false),
                            badgeColorHex = t.optString("badgeColorHex", "#FFF9EE"),
                            hasOutline = t.optBoolean("hasOutline", false),
                            outlineColorHex = t.optString("outlineColorHex", "#FFFFFF"),
                            hasShadow = t.optBoolean("hasShadow", false)
                        )
                    )
                }
            }

            val doodleStrokes = mutableListOf<DoodleStroke>()
            val dArr = root.optJSONArray("doodleStrokes")
            if (dArr != null) {
                for (i in 0 until dArr.length()) {
                    val d = dArr.getJSONObject(i)
                    val pts = mutableListOf<OffsetPoint>()
                    val ptsArr = d.optJSONArray("points")
                    if (ptsArr != null) {
                        for (j in 0 until ptsArr.length()) {
                            val p = ptsArr.getJSONObject(j)
                            pts.add(OffsetPoint(p.optDouble("x").toFloat(), p.optDouble("y").toFloat()))
                        }
                    }
                    doodleStrokes.add(
                        DoodleStroke(
                            id = d.optString("id"),
                            brushType = d.optString("brushType", "marker"),
                            colorHex = d.optString("colorHex", "#263443"),
                            strokeWidth = d.optDouble("strokeWidth", 8.0).toFloat(),
                            points = pts
                        )
                    )
                }
            }

            ProjectEditorState(
                filterId = filterId,
                filterIntensity = filterIntensity,
                adjustments = adjustments,
                cropConfig = CropConfig(),
                rotationDegrees = rotationDegrees,
                isFlippedH = isFlippedH,
                isFlippedV = isFlippedV,
                frameConfig = frameConfig,
                stickers = stickers,
                textLayers = textLayers,
                doodleStrokes = doodleStrokes
            )
        } catch (e: Exception) {
            ProjectEditorState()
        }
    }
}
