package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val thumbnailUri: String? = null,
    val originalImageUri: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isTemplate: Boolean = false,
    val canvasWidth: Int = 1080,
    val canvasHeight: Int = 1080,
    val projectDataJson: String = ""
)

data class Adjustments(
    val exposure: Float = 0f,       // -100 to 100
    val brightness: Float = 0f,     // -100 to 100
    val contrast: Float = 0f,       // -100 to 100
    val highlights: Float = 0f,     // -100 to 100
    val shadows: Float = 0f,        // -100 to 100
    val whites: Float = 0f,         // -100 to 100
    val blacks: Float = 0f,         // -100 to 100
    val saturation: Float = 0f,     // -100 to 100
    val vibrance: Float = 0f,       // -100 to 100
    val temperature: Float = 0f,    // -100 (cool) to 100 (warm)
    val tint: Float = 0f,           // -100 (green) to 100 (magenta)
    val sepia: Float = 0f,          // 0 to 100
    val sharpen: Float = 0f,        // 0 to 100
    val vignette: Float = 0f,       // 0 to 100
    val grain: Float = 0f,          // 0 to 100
    val fade: Float = 0f,           // 0 to 100
    val glow: Float = 0f,           // 0 to 100
    val clarity: Float = 0f,        // -100 to 100
    val dehaze: Float = 0f,         // -100 to 100
    val hue: Float = 0f,            // -180 to 180
    val channelRed: Float = 0f,     // -100 to 100
    val channelGreen: Float = 0f,   // -100 to 100
    val channelBlue: Float = 0f     // -100 to 100
) {
    val isDefault: Boolean
        get() = exposure == 0f && brightness == 0f && contrast == 0f &&
                highlights == 0f && shadows == 0f && whites == 0f && blacks == 0f &&
                saturation == 0f && vibrance == 0f && temperature == 0f && tint == 0f &&
                sepia == 0f && sharpen == 0f && vignette == 0f && grain == 0f &&
                fade == 0f && glow == 0f && clarity == 0f && dehaze == 0f && hue == 0f &&
                channelRed == 0f && channelGreen == 0f && channelBlue == 0f
}

data class CropConfig(
    val left: Float = 0f,
    val top: Float = 0f,
    val right: Float = 1f,
    val bottom: Float = 1f,
    val aspectRatioName: String = "Free"
)

data class StickerLayer(
    val id: String = UUID.randomUUID().toString(),
    val stickerId: String,
    val customUri: String? = null,
    val xNorm: Float = 0.5f,
    val yNorm: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val isFlipped: Boolean = false,
    val tintHex: String? = null
)

data class TextLayer(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Hello ♡",
    val xNorm: Float = 0.5f,
    val yNorm: Float = 0.5f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val fontFamilyKey: String = "caveat",
    val colorHex: String = "#263443",
    val fontSizeSp: Float = 26f,
    val letterSpacingSp: Float = 0f,
    val opacity: Float = 1.0f,
    val hasBackgroundBadge: Boolean = false,
    val badgeColorHex: String = "#FFF9EE",
    val hasOutline: Boolean = false,
    val outlineColorHex: String = "#FFFFFF",
    val hasShadow: Boolean = false
)

data class OffsetPoint(
    val xNorm: Float,
    val yNorm: Float
)

data class DoodleStroke(
    val id: String = UUID.randomUUID().toString(),
    val brushType: String = "marker", // marker, pencil, glow, eraser
    val colorHex: String = "#263443",
    val strokeWidth: Float = 8f,
    val points: List<OffsetPoint> = emptyList()
)

data class FrameConfig(
    val frameId: String = "none",
    val paddingDp: Float = 16f,
    val colorHex: String = "#FFF9EE",
    val cornerRadiusDp: Float = 12f,
    val hasShadow: Boolean = true
)

data class FilterPreset(
    val id: String,
    val displayName: String,
    val category: String,
    val description: String,
    val colorMatrix: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FilterPreset) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class ExportFormat(val extension: String, val mimeType: String) {
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp")
}

enum class ExportPreset(val displayName: String, val width: Int, val height: Int) {
    ORIGINAL("Original Quality", 0, 0),
    IG_POST_SQUARE("Instagram Square (1:1)", 1080, 1080),
    IG_POST_PORTRAIT("Instagram Portrait (4:5)", 1080, 1350),
    IG_STORY("Story / Reel (9:16)", 1080, 1920),
    WALLPAPER("HD Wallpaper (9:16)", 1080, 2400),
    QHD_2K("2K QHD (2048 x 2048)", 2048, 2048),
    UHD_4K("4K Ultra HD (3840 x 2160)", 3840, 2160),
    UHD_4K_SQUARE("4K UHD Square (3840 x 3840)", 3840, 3840),
    UHD_8K("8K Ultra (7680 x 4320)", 7680, 4320),
    UHD_8K_SQUARE("8K Ultra Square (7680 x 7680)", 7680, 7680),
    CUSTOM("Custom Dimensions", 0, 0)
}

data class ExportConfig(
    val format: ExportFormat = ExportFormat.JPEG,
    val quality: Int = 95,
    val preset: ExportPreset = ExportPreset.ORIGINAL,
    val targetWidth: Int = 0,
    val targetHeight: Int = 0
)
