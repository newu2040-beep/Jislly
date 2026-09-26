package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BuiltInAssets
import com.example.data.ProjectEditorState
import com.example.data.ProjectRepository
import com.example.data.UserPreferences
import com.example.model.ProjectEntity
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavDestination {
    SPLASH,
    ONBOARDING,
    HOME,
    GALLERY,
    TEMPLATES,
    PROFILE,
    EDITOR,
    ALL_TOOLS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: ProjectRepository
    val userPreferences: UserPreferences

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(application, db.projectDao())
        userPreferences = UserPreferences(application)

        viewModelScope.launch {
            repository.seedInitialProjectsIfNeeded()
        }
    }

    private val _currentDestination = MutableStateFlow(AppNavDestination.SPLASH)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    private val _currentEditingProject = MutableStateFlow<ProjectEntity?>(null)
    val currentEditingProject: StateFlow<ProjectEntity?> = _currentEditingProject.asStateFlow()

    private val _initialEditorTool = MutableStateFlow<String?>("filters")
    val initialEditorTool: StateFlow<String?> = _initialEditorTool.asStateFlow()

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: StateFlow<List<ProjectEntity>> = repository.templates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<ProjectEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themeStyle: StateFlow<AppThemeStyle> = userPreferences.themeStyleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeStyle.ICY_BLUE)

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val userName: StateFlow<String> = userPreferences.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "JISLLY")

    val userAge: StateFlow<String> = userPreferences.userAgeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "22")

    val userBio: StateFlow<String> = userPreferences.userBioFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Edit your own kind of magic ♡")

    val userAvatarUri: StateFlow<String?> = userPreferences.userAvatarUriFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val compactMode: StateFlow<Boolean> = userPreferences.compactModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val permissionsAsked: StateFlow<Boolean> = userPreferences.permissionsAskedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hapticsEnabled: StateFlow<Boolean> = userPreferences.hapticsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val onboardingDone: StateFlow<Boolean> = userPreferences.onboardingDoneFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun navigateTo(destination: AppNavDestination) {
        _currentDestination.value = destination
    }

    fun finishSplash() {
        if (onboardingDone.value) {
            _currentDestination.value = AppNavDestination.HOME
        } else {
            _currentDestination.value = AppNavDestination.ONBOARDING
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.setOnboardingDone(true)
            _currentDestination.value = AppNavDestination.HOME
        }
    }

    fun setThemeStyle(style: AppThemeStyle) {
        viewModelScope.launch {
            userPreferences.setThemeStyle(style)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setProfile(name: String, age: String, bio: String, avatarUri: String?) {
        viewModelScope.launch {
            userPreferences.setProfile(name, age, bio, avatarUri)
        }
    }

    fun setCompactMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setCompactMode(enabled)
        }
    }

    fun setPermissionsAsked(asked: Boolean) {
        viewModelScope.launch {
            userPreferences.setPermissionsAsked(asked)
        }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setHaptics(enabled)
        }
    }

    fun openProjectInEditor(project: ProjectEntity, initialTool: String? = null) {
        _currentEditingProject.value = project
        _initialEditorTool.value = initialTool
        _currentDestination.value = AppNavDestination.EDITOR
    }

    fun createNewProjectFromUri(imageUri: String, initialTool: String? = null) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                title = "Aesthetic Edit #${(allProjects.value.size + 1)}",
                originalImageUri = imageUri,
                thumbnailUri = imageUri,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.saveProject(
                id = 0,
                title = newProject.title,
                thumbnailUri = imageUri,
                originalImageUri = imageUri,
                state = ProjectEditorState()
            )
            val created = repository.getProjectById(newId) ?: newProject.copy(id = newId)
            _currentEditingProject.value = created
            _initialEditorTool.value = initialTool
            _currentDestination.value = AppNavDestination.EDITOR
        }
    }

    fun createFromTemplate(template: com.example.data.TemplateItem) {
        val uri = "res://${template.sampleDrawableRes}"
        viewModelScope.launch {
            val state = ProjectEditorState(
                filterId = template.filterId,
                frameConfig = com.example.model.FrameConfig(frameId = template.frameId),
                textLayers = listOf(
                    com.example.model.TextLayer(
                        text = template.sampleText,
                        xNorm = 0.5f,
                        yNorm = 0.82f,
                        fontFamilyKey = "caveat",
                        colorHex = "#FFF9EE",
                        fontSizeSp = 28f
                    )
                )
            )
            val newId = repository.saveProject(
                title = template.title,
                thumbnailUri = uri,
                originalImageUri = uri,
                state = state
            )
            val created = repository.getProjectById(newId)
            _currentEditingProject.value = created
            _initialEditorTool.value = "filters"
            _currentDestination.value = AppNavDestination.EDITOR
        }
    }

    fun toggleFavorite(project: ProjectEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(project.id, project.isFavorite)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project.id)
            if (_currentEditingProject.value?.id == project.id) {
                _currentEditingProject.value = null
            }
        }
    }
}
