package com.example.processing

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.BuiltInAssets
import com.example.data.ProjectEditorState
import com.example.model.ExportConfig
import com.example.model.ExportFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.min
import kotlin.random.Random

object ImageExportEngine {

    suspend fun renderAndExport(
        context: Context,
        imageUri: String,
        state: ProjectEditorState,
        exportConfig: ExportConfig,
        saveToGallery: Boolean = true
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            // 1. Load source bitmap
            val sourceBitmap = loadBitmap(context, imageUri)
                ?: return@withContext Result.failure(Exception("Failed to load source image"))

            // Determine dimensions with clamp up to 8192px (8K)
            val maxDimension = 8192
            val targetW = (if (exportConfig.targetWidth > 0) exportConfig.targetWidth else sourceBitmap.width).coerceIn(200, maxDimension)
            val targetH = (if (exportConfig.targetHeight > 0) exportConfig.targetHeight else sourceBitmap.height).coerceIn(200, maxDimension)

            // 2. Create destination canvas bitmap
            val outputBitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)

            // Determine image area inside frame
            val hasFrame = state.frameConfig.frameId != "none"
            val frameColor = try {
                Color.parseColor(state.frameConfig.colorHex)
            } catch (e: Exception) {
                Color.parseColor("#FFF9EE")
            }

            val paddingPx = if (hasFrame) {
                (state.frameConfig.paddingDp / 100f * min(targetW, targetH)).coerceIn(10f, min(targetW, targetH) * 0.25f)
            } else 0f

            val bottomExtra = if (state.frameConfig.frameId == "polaroid_classic") paddingPx * 2.5f else 0f

            val imageRect = RectF(
                paddingPx,
                paddingPx,
                targetW.toFloat() - paddingPx,
                targetH.toFloat() - paddingPx - bottomExtra
            )

            // Draw background frame if active
            if (hasFrame) {
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = frameColor
                    style = Paint.Style.FILL
                }
                canvas.drawRect(0f, 0f, targetW.toFloat(), targetH.toFloat(), bgPaint)

                // Draw frame details (e.g. film sprockets, washi tape, floral corners)
                drawFrameAccents(canvas, state.frameConfig.frameId, targetW, targetH, paddingPx, frameColor)
            }

            // 3. Draw base filtered photo into imageRect
            val filterMatrix = ImageFilterEngine.buildCompositeColorMatrix(
                state.filterId,
                state.filterIntensity,
                state.adjustments
            )
            val filterPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                colorFilter = ColorMatrixColorFilter(filterMatrix)
            }

            // Calculate matrix to scale & center crop the source image into imageRect
            val imageMatrix = Matrix()
            val srcW = sourceBitmap.width.toFloat()
            val srcH = sourceBitmap.height.toFloat()
            val dstW = imageRect.width()
            val dstH = imageRect.height()

            val scale = maxOf(dstW / srcW, dstH / srcH)
            val scaledW = srcW * scale
            val scaledH = srcH * scale
            val dx = imageRect.left + (dstW - scaledW) / 2f
            val dy = imageRect.top + (dstH - scaledH) / 2f

            imageMatrix.postScale(scale, scale)
            if (state.rotationDegrees != 0f) {
                imageMatrix.postRotate(state.rotationDegrees, scaledW / 2f, scaledH / 2f)
            }
            if (state.isFlippedH || state.isFlippedV) {
                imageMatrix.postScale(
                    if (state.isFlippedH) -1f else 1f,
                    if (state.isFlippedV) -1f else 1f,
                    scaledW / 2f,
                    scaledH / 2f
                )
            }
            imageMatrix.postTranslate(dx, dy)

            // Clip to imageRect with rounded corners
            canvas.save()
            val clipPath = Path()
            val cornerR = if (hasFrame) state.frameConfig.cornerRadiusDp * (targetW / 400f) else 0f
            clipPath.addRoundRect(imageRect, cornerR, cornerR, Path.Direction.CW)
            canvas.clipPath(clipPath)

            canvas.drawBitmap(sourceBitmap, imageMatrix, filterPaint)

            // Vignette overlay
            if (state.adjustments.vignette > 0f) {
                val vigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    val radius = Math.hypot(dstW.toDouble(), dstH.toDouble()).toFloat() / 2f
                    val alpha = ((state.adjustments.vignette / 100f) * 200).toInt().coerceIn(0, 255)
                    shader = RadialGradient(
                        imageRect.centerX(),
                        imageRect.centerY(),
                        radius,
                        Color.TRANSPARENT,
                        Color.argb(alpha, 10, 10, 15),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(imageRect, vigPaint)
            }

            // Grain overlay
            if (state.adjustments.grain > 0f) {
                drawGrain(canvas, imageRect, state.adjustments.grain)
            }

            canvas.restore()

            // 4. Draw Doodles
            for (stroke in state.doodleStrokes) {
                if (stroke.points.size < 2) continue
                val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                    strokeWidth = stroke.strokeWidth * (targetW / 400f)
                    if (stroke.brushType == "eraser") {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    } else {
                        color = try { Color.parseColor(stroke.colorHex) } catch (e: Exception) { Color.WHITE }
                        if (stroke.brushType == "glow") {
                            alpha = 180
                            strokeWidth *= 1.4f
                        }
                    }
                }

                val path = Path()
                val first = stroke.points.first()
                path.moveTo(first.xNorm * targetW, first.yNorm * targetH)
                for (i in 1 until stroke.points.size) {
                    val pt = stroke.points[i]
                    path.lineTo(pt.xNorm * targetW, pt.yNorm * targetH)
                }
                canvas.drawPath(path, strokePaint)
            }

            // 5. Draw Stickers
            val stickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.CENTER
            }
            for (st in state.stickers) {
                val item = BuiltInAssets.STICKERS.find { it.id == st.stickerId }
                val symbol = item?.emojiOrVector ?: "✨"
                val textSize = 56f * st.scale * (targetW / 400f)
                stickerPaint.textSize = textSize
                stickerPaint.alpha = (st.opacity * 255).toInt().coerceIn(0, 255)

                val x = st.xNorm * targetW
                val y = st.yNorm * targetH

                canvas.save()
                canvas.translate(x, y)
                if (st.rotation != 0f) canvas.rotate(st.rotation)
                if (st.isFlipped) canvas.scale(-1f, 1f)

                canvas.drawText(symbol, 0f, textSize / 3f, stickerPaint)
                canvas.restore()
            }

            // 6. Draw Text Layers
            for (textLayer in state.textLayers) {
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textAlign = Paint.Align.CENTER
                    textSize = textLayer.fontSizeSp * (targetW / 400f)
                    color = try { Color.parseColor(textLayer.colorHex) } catch (e: Exception) { Color.BLACK }
                    alpha = (textLayer.opacity * 255).toInt().coerceIn(0, 255)
                    letterSpacing = textLayer.letterSpacingSp * 0.05f

                    // Font Typeface
                    val fontResId = when (textLayer.fontFamilyKey) {
                        "comfortaa" -> R.font.comfortaa
                        "caveat" -> R.font.caveat
                        "special_elite" -> R.font.special_elite
                        "playfair" -> R.font.playfair_display
                        else -> R.font.nunito
                    }
                    typeface = try {
                        ResourcesCompat.getFont(context, fontResId)
                    } catch (e: Exception) {
                        null
                    }
                }

                val x = textLayer.xNorm * targetW
                val y = textLayer.yNorm * targetH

                canvas.save()
                canvas.translate(x, y)
                if (textLayer.rotation != 0f) canvas.rotate(textLayer.rotation)
                canvas.scale(textLayer.scale, textLayer.scale)

                // Background Badge if enabled
                if (textLayer.hasBackgroundBadge) {
                    val bounds = Rect()
                    textPaint.getTextBounds(textLayer.text, 0, textLayer.text.length, bounds)
                    val badgePadding = 16f * (targetW / 400f)
                    val badgeRect = RectF(
                        -bounds.width() / 2f - badgePadding,
                        -bounds.height() - badgePadding / 2f,
                        bounds.width() / 2f + badgePadding,
                        badgePadding / 2f
                    )
                    val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = try { Color.parseColor(textLayer.badgeColorHex) } catch (e: Exception) { Color.WHITE }
                        alpha = (textLayer.opacity * 240).toInt().coerceIn(0, 255)
                    }
                    canvas.drawRoundRect(badgeRect, 10f, 10f, badgePaint)
                }

                // Text Shadow
                if (textLayer.hasShadow) {
                    textPaint.setShadowLayer(8f, 3f, 3f, Color.argb(120, 0, 0, 0))
                }

                // Split lines
                val lines = textLayer.text.split("\n")
                val lineHeight = textPaint.fontSpacing
                val startY = -(lines.size - 1) * lineHeight / 2f
                lines.forEachIndexed { i, line ->
                    canvas.drawText(line, 0f, startY + i * lineHeight, textPaint)
                }

                canvas.restore()
            }

            // 7. Save to MediaStore or Cache
            val uri = saveBitmap(context, outputBitmap, exportConfig, saveToGallery)
            Result.success(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun loadBitmap(context: Context, uriString: String): Bitmap? {
        return try {
            if (uriString.startsWith("res://")) {
                val resId = uriString.removePrefix("res://").toIntOrNull() ?: return null
                BitmapFactory.decodeResource(context.resources, resId)
            } else {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun drawGrain(canvas: Canvas, rect: RectF, grainValue: Float) {
        val paint = Paint().apply {
            color = Color.WHITE
            alpha = ((grainValue / 100f) * 45).toInt().coerceIn(0, 80)
        }
        val random = Random(42)
        val density = (rect.width() * rect.height() * (grainValue / 100f) * 0.0003f).toInt().coerceIn(0, 10000)
        val pts = FloatArray(density * 2)
        for (i in 0 until density) {
            pts[i * 2] = rect.left + random.nextFloat() * rect.width()
            pts[i * 2 + 1] = rect.top + random.nextFloat() * rect.height()
        }
        canvas.drawPoints(pts, paint)
    }

    private fun drawFrameAccents(canvas: Canvas, frameId: String, w: Int, h: Int, pad: Float, frameColor: Int) {
        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (frameId) {
            "film_strip_35mm", "darkroom_contact" -> {
                // Draw film sprockets (white or charcoal rounded rectangles)
                accentPaint.color = Color.WHITE
                val sprocketW = pad * 0.45f
                val sprocketH = pad * 0.35f
                val count = (w / (sprocketW * 2.2f)).toInt().coerceAtLeast(4)
                val spacing = w.toFloat() / count
                for (i in 0 until count) {
                    val x = i * spacing + spacing / 4f
                    // Top sprockets
                    canvas.drawRoundRect(RectF(x, pad * 0.25f, x + sprocketW, pad * 0.25f + sprocketH), 4f, 4f, accentPaint)
                    // Bottom sprockets
                    canvas.drawRoundRect(RectF(x, h - pad * 0.65f, x + sprocketW, h - pad * 0.65f + sprocketH), 4f, 4f, accentPaint)
                }
            }
            "washi_tape_corners" -> {
                // Draw 4 diagonal washi tape strips
                accentPaint.color = Color.argb(200, 245, 235, 195)
                val tapeW = pad * 2.5f
                val tapeH = pad * 0.7f
                // Top-left tape
                canvas.save()
                canvas.rotate(-45f, pad * 1.5f, pad * 1.5f)
                canvas.drawRect(pad * 1.5f - tapeW / 2, pad * 1.5f - tapeH / 2, pad * 1.5f + tapeW / 2, pad * 1.5f + tapeH / 2, accentPaint)
                canvas.restore()
                // Top-right tape
                canvas.save()
                canvas.rotate(45f, w - pad * 1.5f, pad * 1.5f)
                canvas.drawRect(w - pad * 1.5f - tapeW / 2, pad * 1.5f - tapeH / 2, w - pad * 1.5f + tapeW / 2, pad * 1.5f + tapeH / 2, accentPaint)
                canvas.restore()
            }
            "postage_stamp", "scalloped_cloud" -> {
                // Perforated stamp or scalloped cloud edge circles
                accentPaint.color = Color.WHITE
                val radius = pad * 0.35f
                val countX = (w / (radius * 3f)).toInt()
                val stepX = w.toFloat() / countX
                for (i in 0..countX) {
                    canvas.drawCircle(i * stepX, 0f, radius, accentPaint)
                    canvas.drawCircle(i * stepX, h.toFloat(), radius, accentPaint)
                }
                val countY = (h / (radius * 3f)).toInt()
                val stepY = h.toFloat() / countY
                for (i in 0..countY) {
                    canvas.drawCircle(0f, i * stepY, radius, accentPaint)
                    canvas.drawCircle(w.toFloat(), i * stepY, radius, accentPaint)
                }
            }
            "double_film_leader" -> {
                accentPaint.color = Color.argb(180, 255, 255, 255)
                val sprocketW = pad * 0.4f
                val sprocketH = pad * 0.3f
                val count = (w / (sprocketW * 2f)).toInt().coerceAtLeast(4)
                val spacing = w.toFloat() / count
                for (i in 0 until count) {
                    val x = i * spacing + spacing / 4f
                    canvas.drawRoundRect(RectF(x, pad * 0.2f, x + sprocketW, pad * 0.2f + sprocketH), 3f, 3f, accentPaint)
                    canvas.drawRoundRect(RectF(x, h - pad * 0.5f, x + sprocketW, h - pad * 0.5f + sprocketH), 3f, 3f, accentPaint)
                }
            }
            "y2k_cyber_glow" -> {
                accentPaint.color = Color.argb(220, 255, 255, 255)
                accentPaint.style = Paint.Style.STROKE
                accentPaint.strokeWidth = pad * 0.15f
                canvas.drawRoundRect(RectF(pad * 0.5f, pad * 0.5f, w - pad * 0.5f, h - pad * 0.5f), 16f, 16f, accentPaint)
            }
        }
    }

    private fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        exportConfig: ExportConfig,
        saveToGallery: Boolean
    ): Uri {
        val filename = "JISLLY_${System.currentTimeMillis()}.${exportConfig.format.extension}"
        val compressFormat = when (exportConfig.format) {
            ExportFormat.PNG -> Bitmap.CompressFormat.PNG
            ExportFormat.WEBP -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
            ExportFormat.JPEG -> Bitmap.CompressFormat.JPEG
        }

        if (saveToGallery) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, exportConfig.format.mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/JISLLY")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("Failed to create MediaStore entry")

            context.contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(compressFormat, exportConfig.quality, out)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
            return uri
        } else {
            // Save to app cache directory for instant sharing
            val cacheDir = File(context.cacheDir, "shared_edits").apply { mkdirs() }
            val file = File(cacheDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(compressFormat, exportConfig.quality, out)
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }
    }
}
