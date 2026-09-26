package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BuiltInAssets
import com.example.model.ProjectEntity
import com.example.processing.ImageFilterEngine
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.Cream
import com.example.ui.theme.IcyBlue
import com.example.ui.theme.Ink
import com.example.ui.theme.NunitoFontFamily
import com.example.viewmodel.EditorToolSheet
import com.example.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    project: ProjectEntity,
    viewModel: EditorViewModel,
    compactMode: Boolean = false,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val state by viewModel.editorState.collectAsState()
    val activeSheet by viewModel.activeSheet.collectAsState()
    val isComparing by viewModel.isComparingOriginal.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()
    val selectedStickerId by viewModel.selectedStickerId.collectAsState()
    val selectedTextId by viewModel.selectedTextId.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val exportConfig by viewModel.exportConfig.collectAsState()

    val activeBrush by viewModel.activeDoodleBrush.collectAsState()
    val activeDoodleColor by viewModel.activeDoodleColor.collectAsState()
    val strokeWidth by viewModel.doodleStrokeWidth.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Editor Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (compactMode) 10.dp else 14.dp, vertical = if (compactMode) 4.dp else 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Undo / Redo / Compare Group
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = canUndo,
                        modifier = Modifier.testTag("undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = canRedo,
                        modifier = Modifier.testTag("redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }

                    // Compare Sun Button (Hold to compare original untouched image)
                    Box(
                        modifier = Modifier
                            .size(if (compactMode) 32.dp else 38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isComparing) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        viewModel.setComparingOriginal(true)
                                        tryAwaitRelease()
                                        viewModel.setComparingOriginal(false)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Hold to compare original",
                            tint = if (isComparing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            modifier = Modifier.size(if (compactMode) 18.dp else 20.dp)
                        )
                    }
                }

                // Save to Gallery & Full Export Actions
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Quick Save to Gallery 1-Tap Action
                    IconButton(
                        onClick = { viewModel.exportPhoto(saveToGallery = true) },
                        modifier = Modifier.testTag("quick_save_gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Quick Save to Gallery",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Save / Export Modal Dialog Button
                    Button(
                        onClick = { viewModel.setActiveSheet(EditorToolSheet.EXPORT) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .height(if (compactMode) 34.dp else 38.dp)
                            .testTag("save_draft_button")
                    ) {
                        Text(
                            text = "Export",
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (compactMode) 12.sp else 14.sp
                        )
                    }
                }
            }

            // Canvas Workspace Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Determine Frame background color
                val hasFrame = state.frameConfig.frameId != "none"
                val frameColor = try {
                    Color(android.graphics.Color.parseColor(state.frameConfig.colorHex))
                } catch (e: Exception) {
                    Cream
                }

                val framePadding = if (hasFrame) state.frameConfig.paddingDp.dp else 0.dp
                val cornerRadius = if (hasFrame) state.frameConfig.cornerRadiusDp.dp else 16.dp
                val bottomExtra = if (state.frameConfig.frameId == "polaroid_classic") 48.dp else 0.dp

                // Frame Outer Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .then(
                            if (hasFrame && state.frameConfig.hasShadow) {
                                Modifier.shadow(8.dp, RoundedCornerShape(cornerRadius + 4.dp))
                            } else Modifier
                        ),
                    shape = RoundedCornerShape(cornerRadius + 4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (hasFrame) frameColor else Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = framePadding,
                                top = framePadding,
                                end = framePadding,
                                bottom = framePadding + bottomExtra
                            )
                            .clip(RoundedCornerShape(cornerRadius))
                    ) {
                        // 1. Base Photo
                        val colorFilter = if (isComparing) null else ImageFilterEngine.getComposeColorFilter(
                            state.filterId,
                            state.filterIntensity,
                            state.adjustments
                        )

                        val photoModifier = Modifier
                            .fillMaxSize()
                            .rotate(state.rotationDegrees)
                            .scale(
                                scaleX = if (state.isFlippedH) -1f else 1f,
                                scaleY = if (state.isFlippedV) -1f else 1f
                            )

                        if (project.originalImageUri.startsWith("res://")) {
                            val resId = project.originalImageUri.removePrefix("res://").toIntOrNull() ?: BuiltInAssets.SAMPLE_DRAWABLE_COFFEE
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Editing Canvas",
                                contentScale = ContentScale.Crop,
                                colorFilter = colorFilter,
                                modifier = photoModifier
                            )
                        } else {
                            AsyncImage(
                                model = project.originalImageUri,
                                contentDescription = "Editing Canvas",
                                contentScale = ContentScale.Crop,
                                colorFilter = colorFilter,
                                modifier = photoModifier
                            )
                        }

                        // 2. Interactive Doodles
                        InteractiveDoodleCanvas(
                            strokes = state.doodleStrokes,
                            isDoodleMode = activeSheet == EditorToolSheet.DOODLE,
                            activeColorHex = activeDoodleColor,
                            activeBrush = activeBrush,
                            activeStrokeWidth = strokeWidth,
                            onStrokeFinished = { viewModel.addDoodleStroke(it) }
                        )

                        // 3. Interactive Stickers
                        state.stickers.forEach { sticker ->
                            InteractiveStickerOverlay(
                                sticker = sticker,
                                isSelected = selectedStickerId == sticker.id,
                                onSelect = { viewModel.selectSticker(sticker.id) },
                                onPositionChanged = { x, y -> viewModel.updateStickerPosition(sticker.id, x, y) },
                                onDelete = { viewModel.deleteSticker(sticker.id) },
                                onDuplicate = { viewModel.duplicateSticker(sticker.id) }
                            )
                        }

                        // 4. Interactive Text Layers
                        state.textLayers.forEach { textLayer ->
                            InteractiveTextOverlay(
                                textLayer = textLayer,
                                isSelected = selectedTextId == textLayer.id,
                                onSelect = { viewModel.selectTextLayer(textLayer.id) },
                                onPositionChanged = { x, y -> viewModel.updateTextPosition(textLayer.id, x, y) },
                                onDelete = { viewModel.deleteTextLayer(textLayer.id) }
                            )
                        }

                        // Comparing Indicator Banner
                        if (isComparing) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "ORIGINAL PHOTO",
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Quick Transform Row (Rotate 90, Flip H, Flip V)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.rotate90() }, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.RotateRight, contentDescription = "Rotate 90", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { viewModel.flipHorizontal() }, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Flip, contentDescription = "Flip Horizontal", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }

            // Horizontally Scrollable Tool Rail
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (compactMode) 8.dp else 12.dp, vertical = if (compactMode) 6.dp else 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (compactMode) 10.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        EditorToolButton(
                            title = "Filters",
                            icon = Icons.Default.FilterVintage,
                            isActive = activeSheet == EditorToolSheet.FILTERS,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.FILTERS) }
                        )
                    }
                    item {
                        EditorToolButton(
                            title = "Adjust",
                            icon = Icons.Default.Tune,
                            isActive = activeSheet == EditorToolSheet.ADJUST,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.ADJUST) }
                        )
                    }
                    item {
                        EditorToolButton(
                            title = "Stickers",
                            icon = Icons.Outlined.Face,
                            isActive = activeSheet == EditorToolSheet.STICKERS,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.STICKERS) }
                        )
                    }
                    item {
                        EditorToolButton(
                            title = "Frames",
                            icon = Icons.Default.Wallpaper,
                            isActive = activeSheet == EditorToolSheet.FRAMES,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.FRAMES) }
                        )
                    }
                    item {
                        EditorToolButton(
                            title = "Text",
                            icon = Icons.Default.TextFields,
                            isActive = activeSheet == EditorToolSheet.TEXT,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.TEXT) }
                        )
                    }
                    item {
                        EditorToolButton(
                            title = "Doodle",
                            icon = Icons.Default.Brush,
                            isActive = activeSheet == EditorToolSheet.DOODLE,
                            compactMode = compactMode,
                            onClick = { viewModel.setActiveSheet(EditorToolSheet.DOODLE) }
                        )
                    }
                }
            }
        }

        // Active Bottom Sheets
        when (activeSheet) {
            EditorToolSheet.FILTERS -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    FiltersSheet(
                        selectedFilterId = state.filterId,
                        filterIntensity = state.filterIntensity,
                        onFilterSelected = { viewModel.selectFilter(it) },
                        onIntensityChanged = { viewModel.setFilterIntensity(it) },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                        onApply = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.ADJUST -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    AdjustSheet(
                        adjustments = state.adjustments,
                        onAdjustmentsChanged = { viewModel.updateAdjustments(it) },
                        onAutoEnhance = { viewModel.applyAutoEnhance() },
                        onReset = { viewModel.resetAdjustments() },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.STICKERS -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    StickerSheet(
                        onStickerSelected = {
                            viewModel.addSticker(it)
                        },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.FRAMES -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    FramesSheet(
                        frameConfig = state.frameConfig,
                        onFrameSelected = { viewModel.selectFrame(it) },
                        onPaddingChanged = { viewModel.updateFramePadding(it) },
                        onColorChanged = { viewModel.updateFrameColor(it) },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.TEXT -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    TextDoodleSheet(
                        initialTab = 0,
                        onAddText = { text, font, color, badge ->
                            viewModel.addTextLayer(text, font)
                        },
                        activeBrush = activeBrush,
                        activeColor = activeDoodleColor,
                        strokeWidth = strokeWidth,
                        onBrushChanged = { viewModel.setDoodleBrush(it) },
                        onDoodleColorChanged = { viewModel.setDoodleColor(it) },
                        onStrokeWidthChanged = { viewModel.setDoodleStrokeWidth(it) },
                        onClearDoodles = { viewModel.clearDoodles() },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.DOODLE -> {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.setActiveSheet(EditorToolSheet.NONE) },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    TextDoodleSheet(
                        initialTab = 1,
                        onAddText = { _, _, _, _ -> },
                        activeBrush = activeBrush,
                        activeColor = activeDoodleColor,
                        strokeWidth = strokeWidth,
                        onBrushChanged = { viewModel.setDoodleBrush(it) },
                        onDoodleColorChanged = { viewModel.setDoodleColor(it) },
                        onStrokeWidthChanged = { viewModel.setDoodleStrokeWidth(it) },
                        onClearDoodles = { viewModel.clearDoodles() },
                        onDismiss = { viewModel.setActiveSheet(EditorToolSheet.NONE) }
                    )
                }
            }

            EditorToolSheet.EXPORT -> {
                ExportDialog(
                    exportConfig = exportConfig,
                    exportState = exportState,
                    onConfigChanged = { viewModel.setExportConfig(it) },
                    onExportToGallery = { viewModel.exportPhoto(saveToGallery = true) },
                    onShare = { viewModel.exportPhoto(saveToGallery = false) },
                    onDismiss = {
                        viewModel.resetExportState()
                        viewModel.setActiveSheet(EditorToolSheet.NONE)
                    }
                )
            }

            else -> {}
        }
    }
}

@Composable
fun EditorToolButton(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = if (compactMode) 2.dp else 4.dp)
            .testTag("tool_button_${title.lowercase()}")
    ) {
        Box(
            modifier = Modifier
                .size(if (compactMode) 38.dp else 46.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(if (compactMode) 18.dp else 22.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (compactMode) 2.dp else 4.dp))
        Text(
            text = title,
            fontFamily = NunitoFontFamily,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            fontSize = if (compactMode) 10.sp else 11.sp,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
