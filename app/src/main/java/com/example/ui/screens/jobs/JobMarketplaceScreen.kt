package com.example.ui.screens.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.CampusEarnRepository
import com.example.ui.components.CategoryIcon
import com.example.ui.components.EmptyStateView
import com.example.ui.components.JobItemCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobMarketplaceScreen(
    repository: CampusEarnRepository,
    selectedCategoryFilter: JobCategory? = null,
    onNavigateToJobDetails: (String) -> Unit
) {
    val allJobs by repository.jobs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedWorkType by remember { mutableStateOf<WorkType?>(null) } // null = All
    var selectedCategory by remember { mutableStateOf(selectedCategoryFilter) }
    var selectedMinPay by remember { mutableStateOf(0) } // 0 = Any
    var selectedLocation by remember { mutableStateOf<String?>(null) } // e.g. "Delhi", "Bengaluru", null = All
    var sortByNewest by remember { mutableStateOf(true) }

    // Sync with external parameter if passed
    LaunchedEffect(selectedCategoryFilter) {
        if (selectedCategoryFilter != null) {
            selectedCategory = selectedCategoryFilter
        }
    }

    // Filter logic
    val filteredJobs = remember(allJobs, searchQuery, selectedWorkType, selectedCategory, selectedMinPay, selectedLocation, sortByNewest) {
        allJobs.filter { job ->
            val matchesQuery = searchQuery.isBlank() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.description.contains(searchQuery, ignoreCase = true) ||
                    job.employerName.contains(searchQuery, ignoreCase = true) ||
                    job.requiredSkills.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesWorkType = selectedWorkType == null || job.workType == selectedWorkType
            val matchesCategory = selectedCategory == null || job.category == selectedCategory
            val matchesPay = job.paymentAmount >= selectedMinPay
            val matchesLocation = selectedLocation == null ||
                    (selectedLocation == "Online" && job.workType == WorkType.ONLINE) ||
                    job.location.contains(selectedLocation!!, ignoreCase = true)

            matchesQuery && matchesWorkType && matchesCategory && matchesPay && matchesLocation
        }.let { list ->
            if (sortByNewest) list else list.sortedByDescending { it.paymentAmount }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Search & Filters Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search college gigs, skills, roles...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Slate500
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepBluePrimary,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                // Work Type Segment (All, Online, Offline)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedWorkType == null,
                        onClick = { selectedWorkType = null },
                        label = { Text("All Gigs (${allJobs.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedWorkType == WorkType.ONLINE,
                        onClick = { selectedWorkType = if (selectedWorkType == WorkType.ONLINE) null else WorkType.ONLINE },
                        label = { Text("🌐 Online Remote") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleAccent,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedWorkType == WorkType.OFFLINE,
                        onClick = { selectedWorkType = if (selectedWorkType == WorkType.OFFLINE) null else WorkType.OFFLINE },
                        label = { Text("📍 Campus / Offline") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPending,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Horizontal Filters (Category, Pay, Sort)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Dropdown / Reset
                    if (selectedCategory != null) {
                        InputChip(
                            selected = true,
                            onClick = { selectedCategory = null },
                            label = { Text(selectedCategory!!.title) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp)) },
                            colors = InputChipDefaults.inputChipColors(selectedContainerColor = Color(0xFFDBEAFE))
                        )
                    }

                    // Pay filter chips
                    val payOptions = listOf(0 to "Any Pay", 2000 to "₹2k+", 4000 to "₹4k+", 6000 to "₹6k+")
                    payOptions.forEach { (amount, label) ->
                        FilterChip(
                            selected = selectedMinPay == amount,
                            onClick = { selectedMinPay = amount },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFD1FAE5),
                                selectedLabelColor = EmeraldEarn
                            )
                        )
                    }

                    // Sort toggle
                    FilterChip(
                        selected = !sortByNewest,
                        onClick = { sortByNewest = !sortByNewest },
                        label = { Text(if (sortByNewest) "Sort: Newest" else "Sort: Highest Pay") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (sortByNewest) Icons.Default.Schedule else Icons.Default.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }
        }

        // Category Quick Bar (Horizontal Icons)
        Surface(
            color = Color(0xFFF1F5F9),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Categories", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                items(JobCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text(cat.title, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            CategoryIcon(category = cat, modifier = Modifier.size(14.dp))
                        }
                    )
                }
            }
        }

        // Job Results Count & Feed
        if (filteredJobs.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.WorkOff,
                title = "No Matching Gigs Found",
                description = "Try resetting filters or adjusting search keywords.",
                actionButtonText = "Reset All Filters",
                onActionClick = {
                    searchQuery = ""
                    selectedWorkType = null
                    selectedCategory = null
                    selectedMinPay = 0
                    selectedLocation = null
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 900.dp)
                    .align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp)
            ) {
                item {
                    Text(
                        text = "Showing ${filteredJobs.size} available gigs across India",
                        style = MaterialTheme.typography.labelMedium.copy(color = Slate500),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                items(filteredJobs) { job ->
                    JobItemCard(
                        job = job,
                        onClick = { onNavigateToJobDetails(job.id) }
                    )
                }
            }
        }
    }
}
