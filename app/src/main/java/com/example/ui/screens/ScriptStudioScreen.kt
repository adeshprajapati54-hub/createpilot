package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioIndigo
import com.example.ui.theme.StudioViolet

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptStudioScreen(
    projects: List<ProjectEntity>,
    selectedProject: ProjectEntity?,
    isGeneratingScript: Boolean,
    isTtsPlaying: Boolean,
    ttsStatus: String,
    isRecordingAudio: Boolean = false,
    isTranscribingAudio: Boolean = false,
    onSelectProject: (ProjectEntity) -> Unit,
    onGenerateScript: (ProjectEntity, tone: String, notes: String) -> Unit,
    onSaveProject: (ProjectEntity) -> Unit,
    onPlayTTS: (text: String, voice: String) -> Unit,
    onStopAudio: () -> Unit,
    onStartVoiceDictation: () -> Unit = {},
    onStopVoiceDictation: ((String) -> Unit) -> Unit = {},
    onProceedToScenes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Active project state
    val project = selectedProject ?: projects.firstOrNull()

    var editableHook by remember(project?.id) { mutableStateOf(project?.hook ?: "") }
    var editableOutline by remember(project?.id) { mutableStateOf(project?.outline ?: "") }
    var editableScript by remember(project?.id) { mutableStateOf(project?.fullScript ?: "") }
    var selectedTone by remember(project?.id) { mutableStateOf(project?.tone ?: "Energetic & Engaging") }
    var additionalNotes by remember { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf("Kore") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val tones = listOf(
        "Energetic & Engaging",
        "Analytical & Authoritative",
        "Storyteller & Cinematic",
        "Casual & Conversational"
    )

    val voices = listOf("Kore", "Fenrir", "Puck", "Aoede")

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartVoiceDictation()
        } else {
            Toast.makeText(context, "Microphone permission needed to dictate narration", Toast.LENGTH_SHORT).show()
        }
    }

    // Duration calculation (approx 150 words per minute for YouTube pacing)
    val wordCount = editableScript.split("\\s+".toRegex()).count { it.isNotBlank() }
    val estMinutes = (wordCount / 150)
    val estSeconds = ((wordCount % 150) * 60) / 150

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Video Project Selected",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select an existing project or create one in Idea Lab to begin writing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Script Studio",
                subtitle = "Craft high-retention video narration, opening hooks, and chapters"
            )
        }

        // Project Switcher
        item {
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = project.title,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Active Project") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("project_selector_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    projects.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.title, maxLines = 1) },
                            onClick = {
                                onSelectProject(p)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Script Controls & Generation Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Script Tone & Style",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tones) { tone ->
                            val isSel = selectedTone == tone
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSel) StudioViolet else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedTone = tone }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tone,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        placeholder = { Text("Specific instructions (e.g. Include 3 stats, emphasize chapter 2)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val updated = project.copy(
                                hook = editableHook,
                                outline = editableOutline,
                                fullScript = editableScript,
                                tone = selectedTone
                            )
                            onGenerateScript(updated, selectedTone, additionalNotes)
                        },
                        enabled = !isGeneratingScript,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioViolet),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_script_button")
                    ) {
                        if (isGeneratingScript) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Drafting Script with Gemini 3.8 Flash...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Generate Full Script & Outline",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Metrics Banner: Word count & read time
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Words: $wordCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Est. Pacing: ~${estMinutes}m ${estSeconds}s",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreatorRed
                )
            }
        }

        // Section 1: Opening Hook
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "1. Opening Hook (0:00 - 0:30)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CreatorRed
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(editableHook))
                                Toast.makeText(context, "Copied hook", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Hook",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editableHook,
                        onValueChange = { editableHook = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("script_hook_input")
                    )
                }
            }
        }

        // Section 2: Outline & Chapters
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "2. Video Chapters & Outline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = StudioIndigo
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editableOutline,
                        onValueChange = { editableOutline = it },
                        minLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("script_outline_input")
                    )
                }
            }
        }

        // Voice Dictation (gemini-3.5-transcribe) Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isRecordingAudio) CreatorRed else StudioAmber.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("voice_dictation_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isRecordingAudio) CreatorRed.copy(alpha = 0.2f)
                                        else StudioAmber.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isRecordingAudio) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = if (isRecordingAudio) CreatorRed else StudioAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Voice Dictation & Transcription",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Transcribe microphone audio into narration",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "gemini-3.5-transcribe",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (isRecordingAudio) {
                                onStopVoiceDictation { text ->
                                    editableScript = if (editableScript.isBlank()) text else "$editableScript\n\n$text"
                                    onSaveProject(
                                        project.copy(
                                            fullScript = editableScript,
                                            updatedAt = System.currentTimeMillis()
                                        )
                                    )
                                }
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecordingAudio) CreatorRed else StudioAmber
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("voice_dictate_button")
                    ) {
                        if (isTranscribingAudio) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Transcribing Audio with gemini-3.5-transcribe...")
                        } else if (isRecordingAudio) {
                            Icon(Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recording... Tap to Transcribe", fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Audio (gemini-3.5-transcribe)", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Section 3: Full Narration Voiceover
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "3. Full Voiceover Narration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StudioEmerald
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(editableScript))
                                Toast.makeText(context, "Copied full script", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Script",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editableScript,
                        onValueChange = { editableScript = it },
                        minLines = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("script_full_narration_input")
                    )
                }
            }
        }

        // TTS Speech Narration Studio Card (gemini-3.8-flash-tts)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        StudioCyan.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("tts_studio_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(StudioCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Voiceover Studio (gemini-3.8-flash-tts)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Preview your narration pacing with realistic AI speech",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Voice Persona",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        voices.forEach { v ->
                            val isSel = selectedVoice == v
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSel) StudioCyan else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedVoice = v }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = v,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onPlayTTS(editableScript.ifBlank { editableHook }, selectedVoice)
                            },
                            enabled = !isTtsPlaying,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("play_tts_button")
                        ) {
                            if (isTtsPlaying) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Streaming Voice...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Listen Narration")
                            }
                        }

                        Button(
                            onClick = onStopAudio,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("stop_tts_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (ttsStatus.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = ttsStatus,
                            fontSize = 10.sp,
                            color = StudioCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Action Buttons: Save & Proceed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val updated = project.copy(
                            hook = editableHook,
                            outline = editableOutline,
                            fullScript = editableScript,
                            tone = selectedTone
                        )
                        onSaveProject(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_script_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save Script",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val updated = project.copy(
                            hook = editableHook,
                            outline = editableOutline,
                            fullScript = editableScript,
                            tone = selectedTone
                        )
                        onSaveProject(updated)
                        onProceedToScenes()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("proceed_to_scenes_button")
                ) {
                    Text(
                        text = "Next: Scene Studio",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
