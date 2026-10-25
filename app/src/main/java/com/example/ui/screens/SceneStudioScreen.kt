package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun SceneStudioScreen(
    projects: List<ProjectEntity>,
    selectedProject: ProjectEntity?,
    isGeneratingScenes: Boolean,
    isGeneratingImage: Boolean,
    isGeneratingVideo: Boolean,
    videoGenStatus: String,
    onGenerateScenes: (ProjectEntity) -> Unit,
    onGenerateThumbnail: (ProjectEntity, prompt: String, resolution: String) -> Unit,
    onGenerateVideo: (ProjectEntity, prompt: String, aspectRatio: String) -> Unit,
    onUpdateProject: (ProjectEntity) -> Unit,
    onNavigateToCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val project = selectedProject ?: projects.firstOrNull()

    var sceneNotesInput by remember(project?.id) { mutableStateOf(project?.sceneNotes ?: "") }
    var thumbPromptInput by remember(project?.id) {
        mutableStateOf(
            project?.thumbnailPrompt.takeUnless { it.isNullOrBlank() }
                ?: "Hyper-realistic YouTube thumbnail: High-contrast split scene for '${project?.title ?: "My Video"}', bold yellow 3D text overlay 'WATCH THIS', cinematic lighting, 8k render"
        )
    }
    var selectedResolution by remember(project?.id) { mutableStateOf(project?.thumbnailResolution ?: "2K") }
    val resolutions = listOf("1K", "2K", "4K")

    var videoPromptInput by remember(project?.id) {
        mutableStateOf("Cinematic high-octane 5-second teaser: Dramatic zoom into illuminated digital displays for ${project?.title ?: "the video"}")
    }
    var selectedVideoAspect by remember { mutableStateOf("16:9") }
    val videoAspects = listOf("16:9", "9:16")

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a project to access Scene & Thumbnail Studio.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                title = "Scene & Thumbnail Studio",
                subtitle = "Visual prompts, 1K-4K thumbnails (Gemini 3 Pro), and AI video synthesis"
            )
        }

        // Project Info Bar
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROJECT BRIEF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CreatorRed
                        )
                        Text(
                            text = project.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(StudioViolet.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = project.format,
                            fontSize = 11.sp,
                            color = StudioViolet,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section 1: Scene & B-Roll Prompt Generator
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = CreatorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1. Scene & B-Roll Timing Prompts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(sceneNotesInput))
                                Toast.makeText(context, "Copied scene prompts", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = sceneNotesInput,
                        onValueChange = {
                            sceneNotesInput = it
                            onUpdateProject(project.copy(sceneNotes = it))
                        },
                        placeholder = { Text("Generate scenes or enter timestamps, B-roll prompts, and sound cues...") },
                        minLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scene_notes_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onGenerateScenes(project) },
                        enabled = !isGeneratingScenes,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_scenes_button")
                    ) {
                        if (isGeneratingScenes) {
                            CircularProgressIndicator(
                                color = CreatorRed,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Scene Breakdown...", color = MaterialTheme.colorScheme.onSurface)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CreatorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Auto-Generate Scene & Audio Cues",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Section 2: High-Quality Thumbnail Studio (gemini-3-pro-image-preview with 1K, 2K, 4K)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        CreatorRed.copy(alpha = 0.35f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("thumbnail_studio_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = CreatorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. High-Quality Thumbnail Studio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Model Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CreatorRed.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "gemini-3-pro-image-preview",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CreatorRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Resolution Affordance: 1K, 2K, 4K (Mandatory User Requirement!)
                    Text(
                        text = "Image Resolution Specification",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        resolutions.forEach { res ->
                            val isSel = selectedResolution == res
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSel) CreatorRed else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        selectedResolution = res
                                        onUpdateProject(project.copy(thumbnailResolution = res))
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HighQuality,
                                        contentDescription = res,
                                        tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$res UHD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Visual Composition & Typography Prompt",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = thumbPromptInput,
                        onValueChange = { thumbPromptInput = it },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("thumbnail_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Thumbnail Render Canvas / Visual Preview
                    ThumbnailPreviewCanvas(
                        project = project,
                        resolution = selectedResolution,
                        prompt = thumbPromptInput
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onGenerateThumbnail(project, thumbPromptInput, selectedResolution)
                        },
                        enabled = !isGeneratingImage,
                        colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_thumbnail_button")
                    ) {
                        if (isGeneratingImage) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rendering $selectedResolution Thumbnail...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Render $selectedResolution Thumbnail Image",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Video Generation with gemini-omni-1.1-flash (Interactions API)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        StudioViolet.copy(alpha = 0.35f),
                        RoundedCornerShape(14.dp)
                    )
                    .testTag("video_studio_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = StudioViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3. Video Generation (Interactions API)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioViolet.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "gemini-omni-1.1-flash",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioViolet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Aspect Ratio (16:9 Landscape / 9:16 Shorts)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        videoAspects.forEach { aspect ->
                            val isSel = selectedVideoAspect == aspect
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSel) StudioViolet else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedVideoAspect = aspect }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (aspect == "16:9") "16:9 Landscape (YouTube)" else "9:16 Portrait (Shorts)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = videoPromptInput,
                        onValueChange = { videoPromptInput = it },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onGenerateVideo(project, videoPromptInput, selectedVideoAspect)
                        },
                        enabled = !isGeneratingVideo,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioViolet),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("generate_video_button")
                    ) {
                        if (isGeneratingVideo) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing Video Frames...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Synthesize 5s Video Clip ($selectedVideoAspect)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (videoGenStatus.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = videoGenStatus,
                            fontSize = 11.sp,
                            color = StudioViolet,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Action: Next to Calendar
        item {
            Button(
                onClick = onNavigateToCalendar,
                colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("navigate_to_calendar_button")
            ) {
                Text(
                    text = "Next: Content Calendar & Publishing Schedule",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ThumbnailPreviewCanvas(
    project: ProjectEntity,
    resolution: String,
    prompt: String
) {
    val imageUrl = project.thumbnailImageUrl

    // 16:9 Standard YouTube Canvas
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF881337)
                    )
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val decodedBitmap = remember(imageUrl) {
            if (!imageUrl.isNullOrBlank() && imageUrl.startsWith("data:image")) {
                try {
                    val cleanBase64 = imageUrl.substringAfter("base64,")
                    val decoded = Base64.decode(cleanBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

        if (decodedBitmap != null) {
            Image(
                bitmap = decodedBitmap.asImageBitmap(),
                contentDescription = "Generated Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Overlay text & graphic mockup representing high-CTR composition
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(CreatorRed)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "VIRAL CTR THUMBNAIL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title.take(45).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = StudioAmber,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "100K BLUEPRINT",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        // Top Right Resolution Tag (1K, 2K, 4K)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .border(0.5.dp, StudioCyan, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$resolution UHD",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = StudioCyan
            )
        }

        // Bottom Left YouTube Duration Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text(
                text = "12:45",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
