package com.pixelroot.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelroot.app.data.PrefsManager
import com.pixelroot.app.root.RootExecutor
import com.pixelroot.app.root.RootLogEntry
import com.pixelroot.app.root.SuManagerType
import com.pixelroot.app.ui.components.RootLogPanel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun TempRootScreen(
    prefs: PrefsManager,
    selectedManager: SuManagerType
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var imageLabel by remember { mutableStateOf(prefs.tempRootImageLabel) }
    var sessionActive by remember { mutableStateOf(prefs.tempRootSessionActive) }
    var sessionStartedAt by remember { mutableStateOf(prefs.tempRootSessionStartedAt) }
    var isVerifying by remember { mutableStateOf(false) }
    var showEndSessionDialog by remember { mutableStateOf(false) }
    var elapsedLabel by remember { mutableStateOf("") }
    val logs = remember { mutableStateListOf<RootLogEntry>() }

    val imageName = imageLabel.ifBlank { "<your-image>.img" }

    LaunchedEffect(sessionActive, sessionStartedAt) {
        while (sessionActive) {
            val elapsedMs = System.currentTimeMillis() - sessionStartedAt
            val mins = TimeUnit.MILLISECONDS.toMinutes(elapsedMs)
            val secs = TimeUnit.MILLISECONDS.toSeconds(elapsedMs) % 60
            elapsedLabel = "Active for ${mins}m ${secs}s"
            kotlinx.coroutines.delay(1000)
        }
    }

    fun copyCommand(text: String) {
        clipboard.setText(AnnotatedString(text))
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }

    fun verifySession() {
        if (isVerifying) return
        isVerifying = true
        scope.launch {
            val newLogs = RootExecutor.verifyRootSession(selectedManager)
            logs.addAll(newLogs)
            val success = newLogs.lastOrNull()?.isSuccess == true
            sessionActive = success
            prefs.tempRootSessionActive = success
            if (success && sessionStartedAt == 0L) {
                sessionStartedAt = System.currentTimeMillis()
                prefs.tempRootSessionStartedAt = sessionStartedAt
            }
            if (!success) {
                sessionStartedAt = 0L
                prefs.tempRootSessionStartedAt = 0L
            }
            isVerifying = false
        }
    }

    fun endSession() {
        scope.launch {
            val entry = RootExecutor.rebootDevice()
            logs.add(entry)
            sessionActive = false
            sessionStartedAt = 0L
            prefs.tempRootSessionActive = false
            prefs.tempRootSessionStartedAt = 0L
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Temp Root Session",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "fastboot boot — nothing is flashed to this device",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Status card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (sessionActive) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (sessionActive) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (sessionActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (sessionActive) "Session Active" else "No Session Verified",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (sessionActive) {
                                elapsedLabel.ifBlank { "Verified just now" }
                            } else {
                                "Boot the image, then tap Verify Session below"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Step-by-step instructions
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "From your PC",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = imageLabel,
                        onValueChange = {
                            imageLabel = it
                            prefs.tempRootImageLabel = it
                        },
                        label = { Text("Boot image filename (optional label)") },
                        placeholder = { Text("kernelsu-boot.img") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CommandRow(
                        step = "1",
                        command = "adb reboot bootloader",
                        onCopy = { copyCommand("adb reboot bootloader") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CommandRow(
                        step = "2",
                        command = "fastboot boot $imageName",
                        onCopy = { copyCommand("fastboot boot $imageName") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "3. Wait for the device to finish booting, then open " +
                            "${selectedManager.displayName} and confirm it shows \"Running\".",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "4. Tap Verify Session below. Nothing is written to the " +
                            "device at any point — a normal reboot (or the End Session " +
                            "button) drops back to your original boot image automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        // Actions
        item {
            Button(
                onClick = { verifySession() },
                enabled = !isVerifying,
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
                    text = if (isVerifying) "VERIFYING..." else "VERIFY SESSION",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (sessionActive) {
            item {
                OutlinedButton(
                    onClick = { showEndSessionDialog = true },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.width(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End Session (Reboot)")
                }
            }
        }

        if (logs.isNotEmpty()) {
            item {
                RootLogPanel(
                    logs = logs,
                    activeManagerPackage = RootExecutor.findInstalledManagerPackage(context, selectedManager),
                    onClearLogs = { logs.clear() }
                )
            }
        }
    }

    if (showEndSessionDialog) {
        AlertDialog(
            onDismissRequest = { showEndSessionDialog = false },
            icon = { Icon(Icons.Rounded.RestartAlt, contentDescription = null) },
            title = { Text("End Temp Root Session?") },
            text = {
                Text(
                    "This reboots the device normally. Since the boot image was never " +
                        "flashed, it will come back up on your original boot image with " +
                        "no trace of this session. Run fastboot boot again next time you " +
                        "need root."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showEndSessionDialog = false
                    endSession()
                }) {
                    Text("Reboot Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndSessionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CommandRow(
    step: String,
    command: String,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(16.dp)
            )
            Text(
                text = command,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = "Copy command",
                    modifier = Modifier.width(18.dp)
                )
            }
        }
    }
}
