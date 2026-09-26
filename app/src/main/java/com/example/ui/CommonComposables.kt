package com.example.ui

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BuiltInAssets
import com.example.model.DoodleStroke
import com.example.model.FrameConfig
import com.example.model.OffsetPoint
import com.example.model.StickerLayer
import com.example.model.TextLayer
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.Ink
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.PlayfairFontFamily
import com.example.ui.theme.SpecialEliteFontFamily
import kotlin.math.roundToInt

@Composable
fun InteractiveStickerOverlay(
    sticker: StickerLayer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPositionChanged: (Float, Float) -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = BuiltInAssets.STICKERS.find { it.id == sticker.stickerId }
    val symbol = item?.emojiOrVector ?: "✨"

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        var currentX by remember(sticker.xNorm) { mutableStateOf(sticker.xNorm * widthPx) }
        var currentY by remember(sticker.yNorm) { mutableStateOf(sticker.yNorm * heightPx) }

        val xDp = with(density) { currentX.toDp() }
        val yDp = with(density) { currentY.toDp() }

        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt() - 40, currentY.roundToInt() - 40) }
                .pointerInput(sticker.id) {
                    detectTapGestures {
                        onSelect()
                    }
                }
                .pointerInput(sticker.id) {
                    detectDragGestures(
                        onDragStart = { onSelect() },
                        onDragEnd = {
                            val normX = (currentX / widthPx).coerceIn(0.05f, 0.95f)
                            val normY = (currentY / heightPx).coerceIn(0.05f, 0.95f)
                            onPositionChanged(normX, normY)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        currentX += dragAmount.x
                        currentY += dragAmount.y
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .rotate(sticker.rotation)
                    .scale(sticker.scale)
                    .alpha(sticker.opacity)
                    .padding(8.dp)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = Color(0xFF8FBFE3),
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 44.sp
                )
            }

            // Controls when selected
            if (isSelected) {
                // Delete button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF476F))
                        .pointerInput(Unit) {
                            detectTapGestures { onDelete() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Duplicate button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8FBFE3))
                        .pointerInput(Unit) {
                            detectTapGestures { onDuplicate() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Duplicate",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveTextOverlay(
    textLayer: TextLayer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPositionChanged: (Float, Float) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        var currentX by remember(textLayer.xNorm) { mutableStateOf(textLayer.xNorm * widthPx) }
        var currentY by remember(textLayer.yNorm) { mutableStateOf(textLayer.yNorm * heightPx) }

        val font = when (textLayer.fontFamilyKey) {
            "comfortaa" -> ComfortaaFontFamily
            "caveat" -> CaveatFontFamily
            "special_elite" -> SpecialEliteFontFamily
            "playfair" -> PlayfairFontFamily
            else -> NunitoFontFamily
        }

        val textColor = try {
            Color(AndroidColor.parseColor(textLayer.colorHex))
        } catch (e: Exception) {
            Ink
        }

        val badgeColor = try {
            Color(AndroidColor.parseColor(textLayer.badgeColorHex))
        } catch (e: Exception) {
            Color(0xFFFFF9EE)
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt() - 60, currentY.roundToInt() - 25) }
                .pointerInput(textLayer.id) {
                    detectTapGestures { onSelect() }
                }
                .pointerInput(textLayer.id) {
                    detectDragGestures(
                        onDragStart = { onSelect() },
                        onDragEnd = {
                            val normX = (currentX / widthPx).coerceIn(0.05f, 0.95f)
                            val normY = (currentY / heightPx).coerceIn(0.05f, 0.95f)
                            onPositionChanged(normX, normY)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        currentX += dragAmount.x
                        currentY += dragAmount.y
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .rotate(textLayer.rotation)
                    .scale(textLayer.scale)
                    .alpha(textLayer.opacity)
                    .padding(6.dp)
                    .then(
                        if (textLayer.hasBackgroundBadge) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        } else Modifier
                    )
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = Color(0xFF8FBFE3),
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textLayer.text,
                    fontFamily = font,
                    fontSize = textLayer.fontSizeSp.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF476F))
                        .pointerInput(Unit) {
                            detectTapGestures { onDelete() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveDoodleCanvas(
    strokes: List<DoodleStroke>,
    isDoodleMode: Boolean,
    activeColorHex: String,
    activeBrush: String,
    activeStrokeWidth: Float,
    onStrokeFinished: (DoodleStroke) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPoints by remember { mutableStateOf<List<OffsetPoint>>(emptyList()) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isDoodleMode) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val normX = offset.x / widthPx
                                    val normY = offset.y / heightPx
                                    currentPoints = listOf(OffsetPoint(normX, normY))
                                },
                                onDragEnd = {
                                    if (currentPoints.size > 1) {
                                        onStrokeFinished(
                                            DoodleStroke(
                                                brushType = activeBrush,
                                                colorHex = activeColorHex,
                                                strokeWidth = activeStrokeWidth,
                                                points = currentPoints
                                            )
                                        )
                                    }
                                    currentPoints = emptyList()
                                },
                                onDragCancel = {
                                    currentPoints = emptyList()
                                }
                            ) { change, _ ->
                                change.consume()
                                val normX = change.position.x / widthPx
                                val normY = change.position.y / heightPx
                                currentPoints = currentPoints + OffsetPoint(normX, normY)
                            }
                        }
                    } else Modifier
                )
        ) {
            // Draw past strokes
            for (stroke in strokes) {
                if (stroke.points.size < 2) continue
                val strokeColor = try {
                    Color(AndroidColor.parseColor(stroke.colorHex))
                } catch (e: Exception) {
                    Ink
                }

                val path = Path()
                val start = stroke.points.first()
                path.moveTo(start.xNorm * size.width, start.yNorm * size.height)
                for (i in 1 until stroke.points.size) {
                    val p = stroke.points[i]
                    path.lineTo(p.xNorm * size.width, p.yNorm * size.height)
                }

                drawPath(
                    path = path,
                    color = if (stroke.brushType == "glow") strokeColor.copy(alpha = 0.5f) else strokeColor,
                    style = Stroke(
                        width = stroke.strokeWidth * (if (stroke.brushType == "glow") 1.8f else 1f),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw current active stroke
            if (currentPoints.size > 1) {
                val activeColor = try {
                    Color(AndroidColor.parseColor(activeColorHex))
                } catch (e: Exception) {
                    Ink
                }

                val currentPath = Path()
                val start = currentPoints.first()
                currentPath.moveTo(start.xNorm * size.width, start.yNorm * size.height)
                for (i in 1 until currentPoints.size) {
                    val p = currentPoints[i]
                    currentPath.lineTo(p.xNorm * size.width, p.yNorm * size.height)
                }

                drawPath(
                    path = currentPath,
                    color = if (activeBrush == "glow") activeColor.copy(alpha = 0.6f) else activeColor,
                    style = Stroke(
                        width = activeStrokeWidth * (if (activeBrush == "glow") 1.8f else 1f),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
