package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CreatorRed
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioIndigo
import com.example.ui.theme.StudioViolet

@Composable
fun CalendarScreen(
    projects: List<ProjectEntity>,
    onUpdateStatus: (ProjectEntity, newStatus: String) -> Unit,
    onUpdateScheduledDate: (ProjectEntity, newDate: String) -> Unit,
    onGenerateCsv: () -> String,
    onGenerateProductionExport: (ProjectEntity) -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Idea", "Scripting", "Recording", "Editing", "Ready", "Published")

    var projectToSchedule by remember { mutableStateOf<ProjectEntity?>(null) }
    var newDateInput by remember { mutableStateOf("") }

    var exportDialogText by remember { mutableStateOf<String?>(null) }
    var exportDialogTitle by remember { mutableStateOf("") }

    val filteredProjects = if (selectedFilter == "All") {
        projects
    } else {
        projects.filter { it.status.equals(selectedFilter, ignoreCase = true) }
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
                title = "Content Calendar",
                subtitle = "Schedule release dates, track status pipeline, and export production briefs"
            )
        }

        // Export Actions Toolbar
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Publishing & Team Export",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val csv = onGenerateCsv()
                                exportDialogTitle = "Content Calendar (CSV)"
                                exportDialogText = csv
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioIndigo),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_csv_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val topProject = projects.firstOrNull()
                                if (topProject != null) {
                                    val brief = onGenerateProductionExport(topProject)
                                    exportDialogTitle = "Production Brief — ${topProject.title}"
                                    exportDialogText = brief
                                } else {
                                    Toast.makeText(context, "No projects available to export", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("export_production_brief_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Production Brief",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Pipeline Status Filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filters) { filter ->
                    val isSel = selectedFilter == filter
                    val count = if (filter == "All") projects.size else projects.count { it.status.equals(filter, ignoreCase = true) }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) CreatorRed else MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                if (isSel) CreatorRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "$filter ($count)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Scheduled Items List
        if (filteredProjects.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No projects in '$selectedFilter' status",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredProjects, key = { it.id }) { project ->
                CalendarProjectCard(
                    project = project,
                    onStatusChange = { newStatus -> onUpdateStatus(project, newStatus) },
                    onEditDate = {
                        projectToSchedule = project
                        newDateInput = project.scheduledDate.ifBlank { "2026-10-31" }
                    },
                    onExportBrief = {
                        val brief = onGenerateProductionExport(project)
                        exportDialogTitle = "Production Brief — ${project.title}"
                        exportDialogText = brief
                    }
                )
            }
        }
    }

    // Date Scheduler Dialog
    if (projectToSchedule != null) {
        val targetProj = projectToSchedule!!
        AlertDialog(
            onDismissRequest = { projectToSchedule = null },
            title = {
                Text(
                    text = "Schedule Publishing Date",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = targetProj.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = newDateInput,
                        onValueChange = { newDateInput = it },
                        label = { Text("Publish Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick Dates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("2026-10-25", "2026-10-28", "2026-11-01").forEach { dateOption ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { newDateInput = dateOption }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = dateOption.substring(5),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDateInput.isNotBlank()) {
                            onUpdateScheduledDate(targetProj, newDateInput)
                            projectToSchedule = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed)
                ) {
                    Text("Save Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToSchedule = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Dialog (CSV or Brief)
    if (exportDialogText != null) {
        val textContent = exportDialogText!!
        AlertDialog(
            onDismissRequest = { exportDialogText = null },
            title = {
                Text(
                    text = exportDialogTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = textContent.take(800) + if (textContent.length > 800) "\n\n[... Remaining content included in copy]" else "",
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(textContent))
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        exportDialogText = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CreatorRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { exportDialogText = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun CalendarProjectCard(
    project: ProjectEntity,
    onStatusChange: (String) -> Unit,
    onEditDate: () -> Unit,
    onExportBrief: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses = listOf("Idea", "Scripting", "Recording", "Editing", "Ready", "Published")

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .testTag("calendar_project_card_${project.id}")
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
                        text = project.format,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Date Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioCyan.copy(alpha = 0.15f))
                        .clickable { onEditDate() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Schedule",
                            tint = StudioCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (project.scheduledDate.isNotBlank()) project.scheduledDate else "Set Date",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pipeline Step Switcher
            Text(
                text = "Move in Pipeline:",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(statuses) { st ->
                    val isCurrent = project.status.equals(st, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isCurrent) CreatorRed else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { if (!isCurrent) onStatusChange(st) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = st,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Niche: ${project.niche}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onExportBrief() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Export Brief",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
