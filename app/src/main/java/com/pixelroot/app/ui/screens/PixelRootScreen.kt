package com.pixelroot.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelroot.app.data.PrefsManager
import com.pixelroot.app.root.RootExecutor
import com.pixelroot.app.root.RootLogEntry
import com.pixelroot.app.root.SuManagerType
import com.pixelroot.app.root.TargetAppInfo
import com.pixelroot.app.ui.components.PixelRootEmblem
import com.pixelroot.app.ui.components.RootLogPanel
import com.pixelroot.app.ui.components.SettingsBottomSheet
import com.pixelroot.app.ui.components.SuManagerBadge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixelRootScreen(
    prefs: PrefsManager,
    isDarkMonochrome: Boolean,
    onToggleDarkMonochrome: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showManagerSelector by remember { mutableStateOf(false) }
    var showAppPicker by remember { mutableStateOf(false) }
    var isLaunching by remember { mutableStateOf(false) }

    var startAtBoot by remember { mutableStateOf(prefs.startAtBoot) }
    var autoSoftReboot by remember { mutableStateOf(prefs.autoSoftReboot) }
    var disableKsuModules by remember { mutableStateOf(prefs.disableKsuModules) }
    var selectedManager by remember {
        mutableStateOf(
            if (prefs.selectedManager == SuManagerType.KERNEL_SU_NEXT.displayName) {
                SuManagerType.KERNEL_SU_NEXT
            } else {
                SuManagerType.KERNEL_SU
            }
        )
    }

    var selectedTargetApp by remember { mutableStateOf<TargetAppInfo?>(null) }
    var installedApps by remember { mutableStateOf<List<TargetAppInfo>>(emptyList()) }
    val rootLogs = remember { mutableStateListOf<RootLogEntry>() }

    LaunchedEffect(Unit) {
        installedApps = RootExecutor.getInstalledUserApps(context)
    }

    fun executeRoot(manager: SuManagerType, app: TargetAppInfo?) {
        if (isLaunching) return
        isLaunching = true
        selectedManager = manager
        prefs.selectedManager = manager.displayName
        scope.launch {
            val newEntries = RootExecutor.launchRootSession(
                context = context,
                manager = manager,
                prefs = prefs,
                targetApp = app
            )
            rootLogs.addAll(newEntries)
            isLaunching = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Column {
                        Text(
                            text = "PixelRoot",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Material 3 • Monochrome SU Bridge",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Open Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Material 3 Bottom Bar with Home, Root Log, and Settings trigger
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0 && !showSettingsSheet,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Launcher") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1 && !showSettingsSheet,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Terminal, contentDescription = "Root Logs") },
                    label = { Text("Root Log") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
                NavigationBarItem(
                    selected = showSettingsSheet,
                    onClick = { showSettingsSheet = true },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (selectedTab == 0) {
                // 1. Center DFRoot/PixelRoot Checkered Emblem
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    PixelRootEmblem(size = 88.dp)
                }

                // 2. Primary Pill Action Button ("LAUNCH ROOT")
                item {
                    Button(
                        onClick = {
                            showManagerSelector = true
                            executeRoot(selectedManager, selectedTargetApp)
                        },
                        enabled = !isLaunching,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Text(
                            text = if (isLaunching) "LAUNCHING ROOT..." else "LAUNCH ROOT",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // 3. Target App Selector Card (Run root on a specific app & capture log)
                item {
                    OutlinedCard(
                        onClick = { showAppPicker = true },
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Apps,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedTargetApp?.appName ?: "Target App (Optional)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = selectedTargetApp?.packageName
                                        ?: "Tap to pick an app to run root & inspect root log",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 4. Select a SU Manager section (matching DFRoot screenshot 3)
                item {
                    AnimatedVisibility(
                        visible = showManagerSelector || rootLogs.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Select a SU Manager",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )

                            SuManagerType.entries.forEach { manager ->
                                val isSelected = selectedManager == manager
                                Surface(
                                    onClick = {
                                        executeRoot(manager, selectedTargetApp)
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainer
                                    },
                                    tonalElevation = if (isSelected) 3.dp else 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SuManagerBadge(isNext = manager == SuManagerType.KERNEL_SU_NEXT)
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = manager.displayName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = manager.packageName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Automatic Root Log Console after using on an app or launching root
                item {
                    AnimatedVisibility(
                        visible = rootLogs.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        RootLogPanel(
                            logs = rootLogs,
                            activeManagerPackage = RootExecutor.findInstalledManagerPackage(context, selectedManager),
                            onClearLogs = { rootLogs.clear() }
                        )
                    }
                }
            } else {
                // Dedicated Root Log Tab
                item {
                    if (rootLogs.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Terminal,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Root Logs Yet",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Launch root or run PixelRoot on an app to record live superuser logs here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                FilledTonalButton(
                                    onClick = {
                                        selectedTab = 0
                                        executeRoot(selectedManager, selectedTargetApp)
                                    }
                                ) {
                                    Text("Run Root Check Now")
                                }
                            }
                        }
                    } else {
                        RootLogPanel(
                            logs = rootLogs,
                            activeManagerPackage = RootExecutor.findInstalledManagerPackage(context, selectedManager),
                            onClearLogs = { rootLogs.clear() }
                        )
                    }
                }
            }
        }
    }

    // Target App Selection Dialog
    if (showAppPicker) {
        AlertDialog(
            onDismissRequest = { showAppPicker = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text("Select Target App") },
            text = {
                LazyColumn(modifier = Modifier.height(320.dp)) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTargetApp = null
                                    showAppPicker = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "None (Global SU Session)",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    items(installedApps) { app ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTargetApp = app
                                    showAppPicker = false
                                    showManagerSelector = true
                                    executeRoot(selectedManager, app)
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = app.appName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = app.packageName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Animated Bottom Settings Sheet / Bar
    SettingsBottomSheet(
        visible = showSettingsSheet,
        startAtBoot = startAtBoot,
        onStartAtBootChange = {
            startAtBoot = it
            prefs.startAtBoot = it
        },
        autoSoftReboot = autoSoftReboot,
        onAutoSoftRebootChange = {
            autoSoftReboot = it
            prefs.autoSoftReboot = it
        },
        disableKsuModules = disableKsuModules,
        onDisableKsuModulesChange = {
            disableKsuModules = it
            prefs.disableKsuModules = it
        },
        isDarkMonochrome = isDarkMonochrome,
        onDarkMonochromeChange = onToggleDarkMonochrome,
        onDismiss = { showSettingsSheet = false }
    )
}
