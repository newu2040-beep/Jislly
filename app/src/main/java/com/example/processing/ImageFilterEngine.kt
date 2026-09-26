package com.example.processing

import android.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix as ComposeColorMatrix
import com.example.data.BuiltInAssets
import com.example.model.Adjustments
import kotlin.math.cos
import kotlin.math.sin

object ImageFilterEngine {

    /**
     * Computes the complete 4x5 ColorMatrix combining the selected filter preset (with intensity)
     * and all user adjustments (exposure, brightness, contrast, saturation, warmth, tint, fade,
     * clarity, dehaze, hue, and RGB balance).
     */
    fun buildCompositeColorMatrix(
        filterId: String,
        filterIntensity: Float,
        adjustments: Adjustments
    ): FloatArray {
        // 1. Start with base preset or identity
        val preset = BuiltInAssets.FILTER_PRESETS.find { it.id == filterId }
            ?: BuiltInAssets.FILTER_PRESETS.first()

        val baseMatrix = FloatArray(20)
        val idMatrix = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )

        // Lerp preset with identity according to filterIntensity
        val t = filterIntensity.coerceIn(0f, 1f)
        for (i in 0 until 20) {
            baseMatrix[i] = idMatrix[i] * (1f - t) + preset.colorMatrix[i] * t
        }

        val cm = ColorMatrix(baseMatrix)

        // 2. Exposure & Brightness & Whites & Blacks
        val expScale = 1f + (adjustments.exposure / 100f) * 0.7f + (adjustments.whites / 100f) * 0.25f
        val brTranslate = adjustments.brightness * 1.5f + adjustments.whites * 0.5f + adjustments.blacks * 0.5f
        if (adjustments.exposure != 0f || adjustments.brightness != 0f || adjustments.whites != 0f || adjustments.blacks != 0f) {
            val expMatrix = ColorMatrix(
                floatArrayOf(
                    expScale, 0f, 0f, 0f, brTranslate,
                    0f, expScale, 0f, 0f, brTranslate,
                    0f, 0f, expScale, 0f, brTranslate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(expMatrix)
        }

        // 3. Contrast & Clarity & Dehaze
        val totalContrast = adjustments.contrast + adjustments.clarity * 0.5f + adjustments.dehaze * 0.6f
        if (totalContrast != 0f) {
            val contrastFactor = (1f + (totalContrast / 100f)).coerceAtLeast(0.1f)
            val translate = (-0.5f * contrastFactor + 0.5f) * 255f
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    contrastFactor, 0f, 0f, 0f, translate,
                    0f, contrastFactor, 0f, 0f, translate,
                    0f, 0f, contrastFactor, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(contrastMatrix)
        }

        // 4. Saturation & Vibrance (with Dehaze saturation compensation)
        val totalSat = 1f + ((adjustments.saturation + adjustments.vibrance * 0.7f + adjustments.dehaze * 0.3f) / 100f)
        if (totalSat != 1f) {
            val satMatrix = ColorMatrix()
            satMatrix.setSaturation(totalSat.coerceAtLeast(0f))
            cm.postConcat(satMatrix)
        }

        // 5. Sepia Tone
        if (adjustments.sepia > 0f) {
            val s = (adjustments.sepia / 100f).coerceIn(0f, 1f)
            val invS = 1f - s
            val sepiaMatrix = ColorMatrix(
                floatArrayOf(
                    invS + s * 0.393f, s * 0.769f, s * 0.189f, 0f, 0f,
                    s * 0.349f, invS + s * 0.686f, s * 0.168f, 0f, 0f,
                    s * 0.272f, s * 0.534f, invS + s * 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(sepiaMatrix)
        }

        // 6. Warmth (Temperature) & Tint
        if (adjustments.temperature != 0f || adjustments.tint != 0f) {
            val warmVal = (adjustments.temperature / 100f) * 25f
            val tintVal = (adjustments.tint / 100f) * 20f

            val rOffset = warmVal + tintVal * 0.5f
            val gOffset = -tintVal
            val bOffset = -warmVal + tintVal * 0.5f

            val tempMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, rOffset,
                    0f, 1f, 0f, 0f, gOffset,
                    0f, 0f, 1f, 0f, bOffset,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(tempMatrix)
        }

        // 7. Fade (lift shadows / blacks for vintage matte effect) & Glow
        if (adjustments.fade > 0f || adjustments.glow > 0f) {
            val fadeVal = (adjustments.fade / 100f) * 45f + (adjustments.glow / 100f) * 22f
            val compScale = 1f - (adjustments.fade / 100f) * 0.15f + (adjustments.glow / 100f) * 0.1f
            val fadeMatrix = ColorMatrix(
                floatArrayOf(
                    compScale, 0f, 0f, 0f, fadeVal,
                    0f, compScale, 0f, 0f, fadeVal,
                    0f, 0f, compScale, 0f, fadeVal,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(fadeMatrix)
        }

        // 7. Individual RGB Balance Channels
        if (adjustments.channelRed != 0f || adjustments.channelGreen != 0f || adjustments.channelBlue != 0f) {
            val rScale = 1f + (adjustments.channelRed / 100f) * 0.5f
            val gScale = 1f + (adjustments.channelGreen / 100f) * 0.5f
            val bScale = 1f + (adjustments.channelBlue / 100f) * 0.5f
            val rgbMatrix = ColorMatrix(
                floatArrayOf(
                    rScale, 0f, 0f, 0f, 0f,
                    0f, gScale, 0f, 0f, 0f,
                    0f, 0f, bScale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(rgbMatrix)
        }

        // 8. Hue Rotation
        if (adjustments.hue != 0f) {
            val angle = Math.toRadians(adjustments.hue.toDouble())
            val cosA = cos(angle).toFloat()
            val sinA = sin(angle).toFloat()
            val lumR = 0.213f
            val lumG = 0.715f
            val lumB = 0.072f

            val hueMatrix = ColorMatrix(
                floatArrayOf(
                    lumR + cosA * (1 - lumR) + sinA * (-lumR),
                    lumG + cosA * (-lumG) + sinA * (-lumG),
                    lumB + cosA * (-lumB) + sinA * (1 - lumB),
                    0f, 0f,

                    lumR + cosA * (-lumR) + sinA * (0.143f),
                    lumG + cosA * (1 - lumG) + sinA * (0.140f),
                    lumB + cosA * (-lumB) + sinA * (-0.283f),
                    0f, 0f,

                    lumR + cosA * (-lumR) + sinA * (-(1 - lumR)),
                    lumG + cosA * (-lumG) + sinA * (lumG),
                    lumB + cosA * (1 - lumB) + sinA * (lumB),
                    0f, 0f,

                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(hueMatrix)
        }

        return cm.array
    }

    /**
     * Converts to Jetpack Compose ColorFilter for instant GPU-accelerated preview.
     */
    fun getComposeColorFilter(
        filterId: String,
        filterIntensity: Float,
        adjustments: Adjustments
    ): ColorFilter {
        val array = buildCompositeColorMatrix(filterId, filterIntensity, adjustments)
        return ColorFilter.colorMatrix(ComposeColorMatrix(array))
    }
}
