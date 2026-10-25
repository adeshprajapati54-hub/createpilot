package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioIndigo
import com.example.ui.theme.StudioViolet

@Composable
fun HomeScreen(
    projects: List<ProjectEntity>,
    selectedProject: ProjectEntity?,
    onSelectProject: (ProjectEntity) -> Unit,
    onDeleteProject: (ProjectEntity) -> Unit,
    onNavigateToIdeaLab: () -> Unit,
    onNavigateToScriptStudio: () -> Unit,
    onNavigateToSceneStudio: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToArgonAnalyzer: () -> Unit,
    onNavigateToPricing: () -> Unit,
    onCreateNewProject: (title: String, niche: String, format: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newNiche by remember { mutableStateOf("Tech & AI") }
    var newFormat by remember { mutableStateOf("Long-form (8-15 min)") }

    val activeCount = projects.count { it.status in listOf("Idea", "Scripting", "Recording", "Editing") }
    val readyCount = projects.count { it.status in listOf("Ready", "Published") }
    val avgRetention = if (projects.isNotEmpty()) projects.map { it.retentionScore }.average().toInt() else 85

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner & Quick Stats
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_stats_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "CREATOR WORKSPACE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CreatorRed,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "YouTube Production Pipeline",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CreatorRed)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .clickable { showNewProjectDialog = true }
                                    .testTag("quick_new_video_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "New",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "New Draft",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                title = "Total Videos",
                                value = "${projects.size}",
                                color = StudioIndigo,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "In Progress",
                                value = "$activeCount",
                                color = StudioViolet,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "Ready / Live",
                                value = "$readyCount",
                                color = StudioEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "Avg Retention",
                                value = "$avgRetention%",
                                color = StudioAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Studio Workflow Quick Launchers
            item {
                SectionHeader(
                    title = "Production Studios",
                    subtitle = "One complete workflow: Idea → Script → Scenes → Calendar"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkflowLauncherCard(
                        title = "Idea Lab",
                        desc = "Hooks & Titles",
                        icon = Icons.Default.Lightbulb,
                        color = StudioAmber,
                        onClick = onNavigateToIdeaLab,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_idea_lab")
                    )
                    WorkflowLauncherCard(
                        title = "Script Studio",
                        desc = "Outlines & Narration",
                        icon = Icons.Default.Description,
                        color = StudioViolet,
                        onClick = onNavigateToScriptStudio,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_script_studio")
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkflowLauncherCard(
                        title = "Scene & Thumb",
                        desc = "1K-4K & Video",
                        icon = Icons.Default.Movie,
                        color = CreatorRed,
                        onClick = onNavigateToSceneStudio,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_scene_studio")
                    )
                    WorkflowLauncherCard(
                        title = "Argon Analysis",
                        desc = "Retention & Drop-off",
                        icon = Icons.Default.Analytics,
                        color = StudioCyan,
                        onClick = onNavigateToArgonAnalyzer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("launch_argon_analyzer")
                    )
                }
            }

            // Projects List
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader(
                        title = "Saved Video Projects",
                        subtitle = "${projects.size} drafts stored locally in Room"
                    )
                }
            }

            if (projects.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = StudioAmber,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Projects Yet",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Generate your first viral idea in Idea Lab or add a draft manually.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToIdeaLab,
                                colors = ButtonDefaults.buttonColors(containerColor = CreatorRed)
                            ) {
                                Text("Open Idea Lab")
                            }
                        }
                    }
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    val isSelected = selectedProject?.id == project.id
                    ProjectCard(
                        project = project,
                        isSelected = isSelected,
                        onSelect = { onSelectProject(project) },
                        onEditScript = {
                            onSelectProject(project)
                            onNavigateToScriptStudio()
                        },
                        onEditScenes = {
                            onSelectProject(project)
                            onNavigateToSceneStudio()
                        },
                        onAnalyze = {
                            onSelectProject(project)
                            onNavigateToArgonAnalyzer()
                        },
                        onDelete = { onDeleteProject(project) }
                    )
                }
            }

            // Upgrade Banner
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(StudioAmber, StudioViolet)),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onNavigateToPricing() }
                        .testTag("pricing_banner_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(StudioAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StudioAmber
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CreatorPilot Plans — $0 to $79/mo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Free ($0), Creator ($12), Pro ($29), and Studio ($79) plans. Up to 1,500 AI actions, team workflows, and 4K assets.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Go",
                            tint = StudioAmber
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add New Project
        FloatingActionButton(
            onClick = { showNewProjectDialog = true },
            containerColor = CreatorRed,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
                .testTag("fab_new_project")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Project")
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = {
                Text(
                    text = "New Video Draft",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Working Title or Topic") },
                        placeholder = { Text("e.g. Why AI Agents Change Everything") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_project_title_input")
                    )

                    Text(text = "Niche", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    val niches = listOf("Tech & AI", "Faceless Documentary", "Gaming", "Finance & Business", "Lifestyle")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        niches.take(3).forEach { n ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newNiche == n) CreatorRed else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { newNiche = n }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = n,
                                    fontSize = 10.sp,
                                    color = if (newNiche == n) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(text = "Format", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    val formats = listOf("Long-form (8-15 min)", "Shorts (60s)", "Deep Dive (20m+)")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        formats.forEach { f ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newFormat == f) StudioViolet else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { newFormat = f }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = f.substringBefore(" "),
                                    fontSize = 10.sp,
                                    color = if (newFormat == f) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            onCreateNewProject(newTitle.trim(), newNiche, newFormat)
                            newTitle = ""
                            showNewProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed),
                    modifier = Modifier.testTag("confirm_create_project_button")
                ) {
                    Text("Create Draft")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun WorkflowLauncherCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEditScript: () -> Unit,
    onEditScenes: () -> Unit,
    onAnalyze: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                if (isSelected) 1.5.dp else 0.5.dp,
                if (isSelected) CreatorRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .testTag("project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = project.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project.niche,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StudioEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${project.retentionScore}% Score",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioEmerald
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (project.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hook: \"${project.hook}\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioViolet.copy(alpha = 0.15f))
                        .clickable { onEditScript() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = StudioViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Script",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioViolet
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CreatorRed.copy(alpha = 0.15f))
                        .clickable { onEditScenes() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = CreatorRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Scenes & Thumb",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CreatorRed
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioCyan.copy(alpha = 0.15f))
                        .clickable { onAnalyze() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Argon Analysis",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan
                        )
                    }
                }
            }
        }
    }
}
