package com.smokebreakbuddy.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smokebreakbuddy.ui.theme.*
import com.smokebreakbuddy.ui.viewmodel.BreakInvitationViewModel
import java.text.SimpleDateFormat
import java.util.*

data class AnalyticsStat(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val trend: String? = null,
    val trendPositive: Boolean = true
)

data class WeeklyData(
    val day: String,
    val breaks: Int,
    val duration: Int // in minutes
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    breakInvitationViewModel: BreakInvitationViewModel = hiltViewModel()
) {
    val todayInvitationCount by breakInvitationViewModel.todayInvitationCount.collectAsState()
    
    // Mock analytics data for demonstration
    val stats = remember {
        listOf(
            AnalyticsStat(
                title = "Today's Breaks",
                value = todayInvitationCount.toString(),
                subtitle = "invitations sent",
                icon = Icons.Default.Today,
                trend = "+2 vs yesterday",
                trendPositive = true
            ),
            AnalyticsStat(
                title = "This Week",
                value = "12",
                subtitle = "total breaks",
                icon = Icons.Default.DateRange,
                trend = "-3 vs last week",
                trendPositive = false
            ),
            AnalyticsStat(
                title = "Average Duration",
                value = "8.5",
                subtitle = "minutes per break",
                icon = Icons.Default.AccessTime,
                trend = "+1.2 min",
                trendPositive = true
            ),
            AnalyticsStat(
                title = "Response Rate",
                value = "78%",
                subtitle = "accept invitations",
                icon = Icons.Default.TrendingUp,
                trend = "+5%",
                trendPositive = true
            )
        )
    }
    
    val weeklyData = remember {
        listOf(
            WeeklyData("Mon", 3, 24),
            WeeklyData("Tue", 2, 18),
            WeeklyData("Wed", 4, 32),
            WeeklyData("Thu", 1, 8),
            WeeklyData("Fri", 2, 20),
            WeeklyData("Sat", 0, 0),
            WeeklyData("Sun", 0, 0)
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Analytics",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your break habits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = { /* TODO: Export analytics */ }
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Export data")
                }
            }
        }
        
        // Quick Stats Grid
        item {
            Text(
                text = "Quick Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stats) { stat ->
                    StatCard(stat = stat)
                }
            }
        }
        
        // Weekly Chart
        item {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            WeeklyChartCard(weeklyData = weeklyData)
        }
        
        // Insights
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            InsightsCard()
        }
        
        // Break Patterns
        item {
            Text(
                text = "Break Patterns",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            BreakPatternsCard()
        }
        
        // Goals
        item {
            Text(
                text = "Goals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            GoalsCard()
        }
    }
}

@Composable
fun StatCard(stat: AnalyticsStat) {
    Card(
        modifier = Modifier.width(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    stat.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                stat.trend?.let { trend ->
                    Surface(
                        color = if (stat.trendPositive) BreakGreen.copy(alpha = 0.1f) 
                               else DeclineRed.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (stat.trendPositive) BreakGreen else DeclineRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = stat.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyChartCard(weeklyData: List<WeeklyData>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Break Count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple bar chart visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyData.forEach { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Bar
                        val maxBreaks = weeklyData.maxOfOrNull { it.breaks } ?: 1
                        val barHeight = if (data.breaks > 0) {
                            (data.breaks.toFloat() / maxBreaks * 60).coerceAtLeast(8f)
                        } else 4f
                        
                        Surface(
                            modifier = Modifier
                                .width(24.dp)
                                .height(barHeight.dp),
                            color = if (data.breaks > 0) BreakGreen else SmokeGray.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.small
                        ) {}
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = data.breaks.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = data.day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${weeklyData.sumOf { it.duration }} minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Average per Day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${weeklyData.sumOf { it.duration } / 7} minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaybeOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Smart Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InsightItem(
                icon = Icons.Default.TrendingUp,
                title = "Peak Break Time",
                description = "You take most breaks around 2:30 PM",
                positive = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InsightItem(
                icon = Icons.Default.Group,
                title = "Social Breaks",
                description = "Your breaks are 40% more successful in groups",
                positive = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InsightItem(
                icon = Icons.Default.Schedule,
                title = "Break Frequency",
                description = "Consider shorter, more frequent breaks",
                positive = false
            )
        }
    }
}

@Composable
fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    positive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = if (positive) BreakGreen.copy(alpha = 0.1f) else MaybeOrange.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.size(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (positive) BreakGreen else MaybeOrange
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BreakPatternsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "When do you take breaks?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val timeSlots = listOf(
                "9-11 AM" to 20,
                "11-1 PM" to 35,
                "1-3 PM" to 25,
                "3-5 PM" to 40,
                "5-7 PM" to 15
            )
            
            timeSlots.forEach { (time, percentage) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    LinearProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier.weight(1f),
                        color = BreakGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun GoalsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Break Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = { /* TODO: Edit goals */ }
                ) {
                    Text("Edit")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GoalItem(
                title = "Daily Break Limit",
                current = 3,
                target = 5,
                unit = "breaks"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            GoalItem(
                title = "Weekly Break Time",
                current = 102,
                target = 150,
                unit = "minutes"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            GoalItem(
                title = "Response Rate",
                current = 78,
                target = 80,
                unit = "%"
            )
        }
    }
}

@Composable
fun GoalItem(
    title: String,
    current: Int,
    target: Int,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "$current / $target $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = (current.toFloat() / target).coerceAtMost(1f),
            modifier = Modifier.fillMaxWidth(),
            color = when {
                current >= target -> OnlineGreen
                current >= target * 0.8f -> MaybeOrange
                else -> DeclineRed
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
