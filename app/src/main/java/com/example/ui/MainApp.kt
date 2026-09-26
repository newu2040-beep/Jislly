package com.example.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.JisllyTheme
import com.example.ui.theme.NunitoFontFamily
import com.example.viewmodel.AppNavDestination
import com.example.viewmodel.EditorViewModel
import com.example.viewmodel.MainViewModel

@Composable
fun MainApp(
    mainViewModel: MainViewModel = viewModel()
) {
    val destination by mainViewModel.currentDestination.collectAsState()
    val themeStyle by mainViewModel.themeStyle.collectAsState()
    val themeMode by mainViewModel.themeMode.collectAsState()
    val haptics by mainViewModel.hapticsEnabled.collectAsState()
    val compactMode by mainViewModel.compactMode.collectAsState()
    val allProjects by mainViewModel.allProjects.collectAsState()
    val favorites by mainViewModel.favorites.collectAsState()
    val currentEditingProject by mainViewModel.currentEditingProject.collectAsState()
    val initialEditorTool by mainViewModel.initialEditorTool.collectAsState()

    val userName by mainViewModel.userName.collectAsState()
    val userAge by mainViewModel.userAge.collectAsState()
    val userBio by mainViewModel.userBio.collectAsState()
    val userAvatarUri by mainViewModel.userAvatarUri.collectAsState()
    val permissionsAsked by mainViewModel.permissionsAsked.collectAsState()

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isSmallScreen = configuration.screenHeightDp < 700 || configuration.screenWidthDp < 380
    val effectiveCompactMode = compactMode || isSmallScreen

    var showPermissionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val editorViewModel: EditorViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EditorViewModel(context.applicationContext as android.app.Application, mainViewModel.repository) as T
            }
        }
    )

    // Permission launcher for Notification and Gallery full access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        mainViewModel.setPermissionsAsked(true)
        showPermissionDialog = false
    }

    LaunchedEffect(permissionsAsked) {
        if (!permissionsAsked && destination != AppNavDestination.SPLASH) {
            showPermissionDialog = true
        }
    }

    // Photo picker for central (+) Create button
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mainViewModel.createNewProjectFromUri(uri.toString())
        }
    }

    JisllyTheme(
        themeStyle = themeStyle,
        themeMode = themeMode
    ) {
        when (destination) {
            AppNavDestination.SPLASH -> {
                SplashScreen(onSplashFinished = { mainViewModel.finishSplash() })
            }

            AppNavDestination.ONBOARDING -> {
                OnboardingScreen(
                    currentTheme = themeStyle,
                    onThemeSelected = { mainViewModel.setThemeStyle(it) },
                    onComplete = {
                        mainViewModel.completeOnboarding()
                        if (!permissionsAsked) {
                            showPermissionDialog = true
                        }
                    }
                )
            }

            AppNavDestination.EDITOR -> {
                if (currentEditingProject != null) {
                    remember(currentEditingProject?.id) {
                        editorViewModel.loadProject(currentEditingProject!!, initialEditorTool)
                        true
                    }
                    EditorScreen(
                        project = currentEditingProject!!,
                        viewModel = editorViewModel,
                        compactMode = effectiveCompactMode,
                        onNavigateBack = { mainViewModel.navigateTo(AppNavDestination.HOME) }
                    )
                } else {
                    mainViewModel.navigateTo(AppNavDestination.HOME)
                }
            }

            AppNavDestination.ALL_TOOLS -> {
                ToolsScreen(
                    onToolSelected = { toolKey ->
                        if (allProjects.isNotEmpty()) {
                            mainViewModel.openProjectInEditor(allProjects.first(), toolKey)
                        } else {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    },
                    onNavigateBack = { mainViewModel.navigateTo(AppNavDestination.HOME) }
                )
            }

            else -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        JisllyBottomNavigation(
                            currentDestination = destination,
                            compactMode = effectiveCompactMode,
                            onNavigate = { mainViewModel.navigateTo(it) },
                            onCreateTapped = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        AnimatedContent(
                            targetState = destination,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "nav_transition"
                        ) { targetDest ->
                            when (targetDest) {
                                AppNavDestination.HOME -> {
                                    HomeScreen(
                                        userName = userName,
                                        compactMode = effectiveCompactMode,
                                        recentProjects = allProjects,
                                        onOpenProject = { proj, tool -> mainViewModel.openProjectInEditor(proj, tool) },
                                        onCreateNewPhoto = { uri, tool -> mainViewModel.createNewProjectFromUri(uri.toString(), tool) },
                                        onNavigateToGallery = { mainViewModel.navigateTo(AppNavDestination.GALLERY) },
                                        onNavigateToTemplates = { mainViewModel.navigateTo(AppNavDestination.TEMPLATES) },
                                        onNavigateToProfile = { mainViewModel.navigateTo(AppNavDestination.PROFILE) },
                                        onNavigateToTools = { mainViewModel.navigateTo(AppNavDestination.ALL_TOOLS) }
                                    )
                                }

                                AppNavDestination.GALLERY -> {
                                    GalleryScreen(
                                        projects = allProjects,
                                        onOpenProject = { mainViewModel.openProjectInEditor(it) },
                                        onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                                        onDeleteProject = { mainViewModel.deleteProject(it) },
                                        onCreateNewPhoto = { mainViewModel.createNewProjectFromUri(it.toString()) }
                                    )
                                }

                                AppNavDestination.TEMPLATES -> {
                                    TemplatesScreen(
                                        onSelectTemplate = { template ->
                                            mainViewModel.createFromTemplate(template)
                                        }
                                    )
                                }

                                AppNavDestination.PROFILE -> {
                                    ProfileScreen(
                                        userName = userName,
                                        userAge = userAge,
                                        userBio = userBio,
                                        userAvatarUri = userAvatarUri,
                                        compactMode = effectiveCompactMode,
                                        currentThemeStyle = themeStyle,
                                        currentThemeMode = themeMode,
                                        hapticsEnabled = haptics,
                                        projectCount = allProjects.size,
                                        favoriteCount = favorites.size,
                                        onUpdateProfile = { name, age, bio, avatar ->
                                            mainViewModel.setProfile(name, age, bio, avatar)
                                        },
                                        onToggleCompactMode = { mainViewModel.setCompactMode(it) },
                                        onSelectThemeStyle = { mainViewModel.setThemeStyle(it) },
                                        onSelectThemeMode = { mainViewModel.setThemeMode(it) },
                                        onToggleHaptics = { mainViewModel.setHaptics(it) },
                                        onNavigateToGallery = { mainViewModel.navigateTo(AppNavDestination.GALLERY) },
                                        onNavigateToTemplates = { mainViewModel.navigateTo(AppNavDestination.TEMPLATES) },
                                        onCreateNew = {
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        // Comprehensive Permissions Dialog (Notification & Gallery Access)
        if (showPermissionDialog) {
            Dialog(onDismissRequest = {
                showPermissionDialog = false
                mainViewModel.setPermissionsAsked(true)
            }) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoAlbum,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Enable Full Access",
                            fontFamily = ComfortaaFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "To import your real photos, export ultra-res 4K/8K edits to your gallery, and receive export completion alerts, JISLLY needs media and notification access.",
                            fontFamily = NunitoFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Collections, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Full photo gallery reading & saving", fontFamily = NunitoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Export status notifications", fontFamily = NunitoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showPermissionDialog = false
                                    mainViewModel.setPermissionsAsked(true)
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(23.dp)
                            ) {
                                Text("Later", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val permissions = buildList {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            add(Manifest.permission.POST_NOTIFICATIONS)
                                            add(Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            add(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        }
                                    }
                                    permissionLauncher.launch(permissions.toTypedArray())
                                },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(23.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Grant All", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JisllyBottomNavigation(
    currentDestination: AppNavDestination,
    compactMode: Boolean = false,
    onNavigate: (AppNavDestination) -> Unit,
    onCreateTapped: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compactMode) 58.dp else 68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                isSelected = currentDestination == AppNavDestination.HOME,
                compactMode = compactMode,
                onClick = { onNavigate(AppNavDestination.HOME) }
            )

            // Gallery
            BottomNavItem(
                title = "Gallery",
                selectedIcon = Icons.Filled.Collections,
                unselectedIcon = Icons.Outlined.Collections,
                isSelected = currentDestination == AppNavDestination.GALLERY,
                compactMode = compactMode,
                onClick = { onNavigate(AppNavDestination.GALLERY) }
            )

            // Center Elevated (+) Create Button
            Box(
                modifier = Modifier
                    .size(if (compactMode) 44.dp else 52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onCreateTapped)
                    .testTag("nav_create_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(if (compactMode) 22.dp else 28.dp)
                )
            }

            // Templates
            BottomNavItem(
                title = "Templates",
                selectedIcon = Icons.Filled.Wallpaper,
                unselectedIcon = Icons.Outlined.Wallpaper,
                isSelected = currentDestination == AppNavDestination.TEMPLATES,
                compactMode = compactMode,
                onClick = { onNavigate(AppNavDestination.TEMPLATES) }
            )

            // Profile
            BottomNavItem(
                title = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                isSelected = currentDestination == AppNavDestination.PROFILE,
                compactMode = compactMode,
                onClick = { onNavigate(AppNavDestination.PROFILE) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = if (compactMode) 6.dp else 10.dp, vertical = if (compactMode) 4.dp else 6.dp)
            .testTag("nav_item_${title.lowercase()}")
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = title,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(if (compactMode) 20.dp else 24.dp)
        )
        if (!compactMode) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontFamily = NunitoFontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
