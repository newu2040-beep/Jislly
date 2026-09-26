package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BuiltInAssets
import com.example.data.ProjectEditorState
import com.example.data.ProjectRepository
import com.example.model.Adjustments
import com.example.model.DoodleStroke
import com.example.model.ExportConfig
import com.example.model.ExportFormat
import com.example.model.ExportPreset
import com.example.model.FrameConfig
import com.example.model.OffsetPoint
import com.example.model.ProjectEntity
import com.example.model.StickerLayer
import com.example.model.TextLayer
import com.example.processing.ImageExportEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class EditorToolSheet {
    NONE,
    FILTERS,
    ADJUST,
    CROP,
    STICKERS,
    FRAMES,
    TEXT,
    DOODLE,
    TOOLS_GRID,
    EXPORT
}

sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    data class Success(val uri: Uri, val isShared: Boolean = false) : ExportState()
    data class Error(val message: String) : ExportState()
}

class EditorViewModel(
    application: Application,
    private val repository: ProjectRepository
) : AndroidViewModel(application) {

    private var currentProjectId: Long = 0
    private var originalUri: String = ""
    private var projectTitle: String = "Untitled Edit"

    private val _editorState = MutableStateFlow(ProjectEditorState())
    val editorState: StateFlow<ProjectEditorState> = _editorState.asStateFlow()

    private val _activeSheet = MutableStateFlow(EditorToolSheet.NONE)
    val activeSheet: StateFlow<EditorToolSheet> = _activeSheet.asStateFlow()

    private val _selectedStickerId = MutableStateFlow<String?>(null)
    val selectedStickerId: StateFlow<String?> = _selectedStickerId.asStateFlow()

    private val _selectedTextId = MutableStateFlow<String?>(null)
    val selectedTextId: StateFlow<String?> = _selectedTextId.asStateFlow()

    private val _isComparingOriginal = MutableStateFlow(false)
    val isComparingOriginal: StateFlow<Boolean> = _isComparingOriginal.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<ProjectEditorState>()
    private val redoStack = mutableListOf<ProjectEditorState>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    // Export
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _exportConfig = MutableStateFlow(
        ExportConfig(
            format = ExportFormat.JPEG,
            quality = 95,
            preset = ExportPreset.ORIGINAL,
            targetWidth = 0,
            targetHeight = 0
        )
    )
    val exportConfig: StateFlow<ExportConfig> = _exportConfig.asStateFlow()

    // Doodle state
    private val _activeDoodleColor = MutableStateFlow("#263443")
    val activeDoodleColor: StateFlow<String> = _activeDoodleColor.asStateFlow()

    private val _activeDoodleBrush = MutableStateFlow("marker") // marker, pencil, glow, eraser
    val activeDoodleBrush: StateFlow<String> = _activeDoodleBrush.asStateFlow()

    private val _doodleStrokeWidth = MutableStateFlow(8f)
    val doodleStrokeWidth: StateFlow<Float> = _doodleStrokeWidth.asStateFlow()

    fun loadProject(project: ProjectEntity, initialTool: String? = null) {
        currentProjectId = project.id
        originalUri = project.originalImageUri
        projectTitle = project.title

        val deserialized = repository.deserializeState(project.projectDataJson)
        _editorState.value = deserialized

        undoStack.clear()
        redoStack.clear()
        updateUndoRedoStates()

        when (initialTool) {
            "filters" -> _activeSheet.value = EditorToolSheet.FILTERS
            "adjust" -> _activeSheet.value = EditorToolSheet.ADJUST
            "stickers" -> _activeSheet.value = EditorToolSheet.STICKERS
            "frames" -> _activeSheet.value = EditorToolSheet.FRAMES
            "crop" -> _activeSheet.value = EditorToolSheet.CROP
            "doodle" -> _activeSheet.value = EditorToolSheet.DOODLE
            "tools" -> _activeSheet.value = EditorToolSheet.TOOLS_GRID
            else -> _activeSheet.value = EditorToolSheet.NONE
        }
    }

    fun setActiveSheet(sheet: EditorToolSheet) {
        _activeSheet.value = sheet
    }

    fun setComparingOriginal(comparing: Boolean) {
        _isComparingOriginal.value = comparing
    }

    private fun pushUndo() {
        undoStack.add(_editorState.value)
        if (undoStack.size > 30) undoStack.removeAt(0)
        redoStack.clear()
        updateUndoRedoStates()
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_editorState.value)
            _editorState.value = prev
            updateUndoRedoStates()
            autoSaveDraft()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_editorState.value)
            _editorState.value = next
            updateUndoRedoStates()
            autoSaveDraft()
        }
    }

    // --- Filters ---
    fun selectFilter(filterId: String) {
        pushUndo()
        _editorState.value = _editorState.value.copy(filterId = filterId)
        autoSaveDraft()
    }

    fun setFilterIntensity(intensity: Float) {
        _editorState.value = _editorState.value.copy(filterIntensity = intensity.coerceIn(0f, 1f))
        autoSaveDraft()
    }

    // --- Adjustments ---
    fun updateAdjustments(newAdjustments: Adjustments) {
        _editorState.value = _editorState.value.copy(adjustments = newAdjustments)
        autoSaveDraft()
    }

    fun resetAdjustments() {
        pushUndo()
        _editorState.value = _editorState.value.copy(adjustments = Adjustments())
        autoSaveDraft()
    }

    fun applyAutoEnhance() {
        pushUndo()
        // Deterministic balanced aesthetic enhancement
        val enhanced = _editorState.value.adjustments.copy(
            exposure = 8f,
            contrast = 12f,
            highlights = -6f,
            shadows = 14f,
            vibrance = 15f,
            temperature = 5f,
            sharpen = 18f
        )
        _editorState.value = _editorState.value.copy(adjustments = enhanced)
        autoSaveDraft()
    }

    // --- Transform (Crop, Rotate, Flip) ---
    fun rotate90() {
        pushUndo()
        val next = (_editorState.value.rotationDegrees + 90f) % 360f
        _editorState.value = _editorState.value.copy(rotationDegrees = next)
        autoSaveDraft()
    }

    fun flipHorizontal() {
        pushUndo()
        _editorState.value = _editorState.value.copy(isFlippedH = !_editorState.value.isFlippedH)
        autoSaveDraft()
    }

    fun flipVertical() {
        pushUndo()
        _editorState.value = _editorState.value.copy(isFlippedV = !_editorState.value.isFlippedV)
        autoSaveDraft()
    }

    // --- Frames ---
    fun selectFrame(frameId: String) {
        pushUndo()
        val frameItem = BuiltInAssets.FRAMES.find { it.id == frameId }
        val color = frameItem?.defaultColorHex ?: "#FFF9EE"
        val padding = frameItem?.defaultPadding ?: 16f
        val radius = frameItem?.defaultCornerRadius ?: 12f

        _editorState.value = _editorState.value.copy(
            frameConfig = FrameConfig(
                frameId = frameId,
                paddingDp = padding,
                colorHex = color,
                cornerRadiusDp = radius
            )
        )
        autoSaveDraft()
    }

    fun updateFramePadding(padding: Float) {
        _editorState.value = _editorState.value.copy(
            frameConfig = _editorState.value.frameConfig.copy(paddingDp = padding)
        )
        autoSaveDraft()
    }

    fun updateFrameColor(colorHex: String) {
        _editorState.value = _editorState.value.copy(
            frameConfig = _editorState.value.frameConfig.copy(colorHex = colorHex)
        )
        autoSaveDraft()
    }

    // --- Stickers ---
    fun addSticker(stickerId: String) {
        pushUndo()
        val newSticker = StickerLayer(
            stickerId = stickerId,
            xNorm = 0.5f,
            yNorm = 0.5f,
            scale = 1.0f
        )
        val updated = _editorState.value.stickers + newSticker
        _editorState.value = _editorState.value.copy(stickers = updated)
        _selectedStickerId.value = newSticker.id
        autoSaveDraft()
    }

    fun selectSticker(id: String?) {
        _selectedStickerId.value = id
        _selectedTextId.value = null
    }

    fun updateStickerPosition(id: String, xNorm: Float, yNorm: Float) {
        val updated = _editorState.value.stickers.map {
            if (it.id == id) it.copy(xNorm = xNorm, yNorm = yNorm) else it
        }
        _editorState.value = _editorState.value.copy(stickers = updated)
        autoSaveDraft()
    }

    fun updateStickerTransform(id: String, scale: Float, rotation: Float) {
        val updated = _editorState.value.stickers.map {
            if (it.id == id) it.copy(scale = scale.coerceIn(0.3f, 4f), rotation = rotation) else it
        }
        _editorState.value = _editorState.value.copy(stickers = updated)
        autoSaveDraft()
    }

    fun deleteSticker(id: String) {
        pushUndo()
        val updated = _editorState.value.stickers.filterNot { it.id == id }
        _editorState.value = _editorState.value.copy(stickers = updated)
        if (_selectedStickerId.value == id) _selectedStickerId.value = null
        autoSaveDraft()
    }

    fun duplicateSticker(id: String) {
        pushUndo()
        val target = _editorState.value.stickers.find { it.id == id } ?: return
        val dup = target.copy(
            id = java.util.UUID.randomUUID().toString(),
            xNorm = (target.xNorm + 0.05f).coerceAtMost(0.9f),
            yNorm = (target.yNorm + 0.05f).coerceAtMost(0.9f)
        )
        _editorState.value = _editorState.value.copy(stickers = _editorState.value.stickers + dup)
        _selectedStickerId.value = dup.id
        autoSaveDraft()
    }

    // --- Text Layers ---
    fun addTextLayer(text: String = "JiSLLY ♡", fontKey: String = "caveat") {
        pushUndo()
        val newText = TextLayer(
            text = text,
            xNorm = 0.5f,
            yNorm = 0.5f,
            fontFamilyKey = fontKey,
            colorHex = "#263443",
            fontSizeSp = 28f
        )
        val updated = _editorState.value.textLayers + newText
        _editorState.value = _editorState.value.copy(textLayers = updated)
        _selectedTextId.value = newText.id
        autoSaveDraft()
    }

    fun selectTextLayer(id: String?) {
        _selectedTextId.value = id
        _selectedStickerId.value = null
    }

    fun updateTextLayer(
        id: String,
        text: String,
        fontKey: String,
        colorHex: String,
        fontSizeSp: Float,
        hasBadge: Boolean,
        badgeColorHex: String
    ) {
        val updated = _editorState.value.textLayers.map {
            if (it.id == id) {
                it.copy(
                    text = text,
                    fontFamilyKey = fontKey,
                    colorHex = colorHex,
                    fontSizeSp = fontSizeSp,
                    hasBackgroundBadge = hasBadge,
                    badgeColorHex = badgeColorHex
                )
            } else it
        }
        _editorState.value = _editorState.value.copy(textLayers = updated)
        autoSaveDraft()
    }

    fun updateTextPosition(id: String, xNorm: Float, yNorm: Float) {
        val updated = _editorState.value.textLayers.map {
            if (it.id == id) it.copy(xNorm = xNorm, yNorm = yNorm) else it
        }
        _editorState.value = _editorState.value.copy(textLayers = updated)
        autoSaveDraft()
    }

    fun deleteTextLayer(id: String) {
        pushUndo()
        val updated = _editorState.value.textLayers.filterNot { it.id == id }
        _editorState.value = _editorState.value.copy(textLayers = updated)
        if (_selectedTextId.value == id) _selectedTextId.value = null
        autoSaveDraft()
    }

    // --- Doodles ---
    fun setDoodleBrush(brush: String) {
        _activeDoodleBrush.value = brush
    }

    fun setDoodleColor(colorHex: String) {
        _activeDoodleColor.value = colorHex
    }

    fun setDoodleStrokeWidth(width: Float) {
        _doodleStrokeWidth.value = width
    }

    fun addDoodleStroke(stroke: DoodleStroke) {
        pushUndo()
        val updated = _editorState.value.doodleStrokes + stroke
        _editorState.value = _editorState.value.copy(doodleStrokes = updated)
        autoSaveDraft()
    }

    fun clearDoodles() {
        pushUndo()
        _editorState.value = _editorState.value.copy(doodleStrokes = emptyList())
        autoSaveDraft()
    }

    // --- Export ---
    fun setExportConfig(config: ExportConfig) {
        _exportConfig.value = config
    }

    fun exportToGallery(onComplete: ((Uri) -> Unit)? = null) {
        setActiveSheet(EditorToolSheet.EXPORT)
        exportPhoto(saveToGallery = true, onComplete = onComplete)
    }

    fun exportPhoto(saveToGallery: Boolean = true, onComplete: ((Uri) -> Unit)? = null) {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            val result = ImageExportEngine.renderAndExport(
                context = getApplication(),
                imageUri = originalUri,
                state = _editorState.value,
                exportConfig = _exportConfig.value,
                saveToGallery = saveToGallery
            )
            result.onSuccess { uri ->
                _exportState.value = ExportState.Success(uri, isShared = !saveToGallery)
                onComplete?.invoke(uri)
            }.onFailure { error ->
                _exportState.value = ExportState.Error(error.localizedMessage ?: "Export failed")
            }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    private fun autoSaveDraft() {
        viewModelScope.launch {
            repository.saveProject(
                id = currentProjectId,
                title = projectTitle,
                thumbnailUri = originalUri,
                originalImageUri = originalUri,
                state = _editorState.value
            )
        }
    }
}
