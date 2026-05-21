package com.fiap.ariachallenge.ui.lider.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaMetricCard
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaPickerOption
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaSingleChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaTrendDir
import com.fiap.ariachallenge.ui.components.InteractiveSparkline
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.test.AriaTestTags
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.formatCurrencyCompact
import com.fiap.ariachallenge.util.formatCurrencyDisplayParts

@Composable
fun DashboardLiderScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onProjectClick: (String) -> Unit = {},
    viewModel: DashboardLiderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors

    Scaffold(
        modifier = Modifier.testTag(AriaTestTags.DashboardScreen),
        containerColor = c.bgPrimary,
        topBar = {
            DashboardHeader(
                selectedMonth = uiState.selectedMonth,
                availableMonthCount = uiState.availableMonthCount,
                onSelectMonth = { month ->
                    viewModel.onMonthSelected(month)
                    viewModel.onMonthSelectionFinished(month)
                },
                onNotifClick = { onNavigate(AriaDestination.LiderNotificacoes.route) },
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.label_loading), style = AriaText.bodyMd, color = c.textTertiary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroRoiCard(
                    uiState,
                    selectedMonth = uiState.selectedMonth,
                    onMonthSelected = viewModel::onMonthSelected,
                    onMonthSelectionFinished = viewModel::onMonthSelectionFinished,
                )
            }
        item { AccumulatedRoiCard(uiState) }
            item { MetricsGrid(uiState) }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            if (uiState.categoryDistribution.isNotEmpty()) {
                item { CategoryDistributionSection(uiState) }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
            if (uiState.topProjects.isNotEmpty()) {
                item { TopProjectsSection(uiState, onProjectClick = onProjectClick) }
            } else {
                item {
                    AriaSectionTitle(
                        text = stringResource(R.string.dashboard_top_projects_title),
                        sub = stringResource(R.string.dashboard_top_projects_sub),
                    )
                }
                item { AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_top_projects)) }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { FunnelSection(uiState) }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { AiInsightsSection(uiState) }
        }
    }
}

@Composable
private fun DashboardHeader(
    selectedMonth: Int,
    availableMonthCount: Int,
    onSelectMonth: (Int) -> Unit,
    onNotifClick: () -> Unit,
) {
    val c = AriaTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = AriaText.displayMd.copy(fontSize = 18.sp, letterSpacing = 4.sp),
                    color = c.textPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.role_director_eyebrow, "CARLOS MENDES"),
                    style = AriaText.labelMd,
                    color = c.textTertiary,
                )
            }
            MonthSelector(selected = selectedMonth, availableMonthCount = availableMonthCount, onSelect = onSelectMonth)
            AriaNotificationBell(onClick = onNotifClick)
        }
        AriaHairline()
    }
}

@Composable
private fun MonthSelector(selected: Int, availableMonthCount: Int, onSelect: (Int) -> Unit) {
    val c = AriaTheme.colors
    var sheetVisible by remember { mutableStateOf(false) }
    val months = stringArrayResource(R.array.months_short)
    AriaSingleChoiceBottomSheet(
        visible = sheetVisible,
        title = stringResource(R.string.picker_title_month),
        options = months.take(availableMonthCount).mapIndexed { index, label -> AriaPickerOption((index + 1).toString(), label) },
        onDismiss = { sheetVisible = false },
        onSelected = { id -> id.toIntOrNull()?.let(onSelect) },
        selectedOptionId = selected.toString(),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(BorderStroke(0.5.dp, c.borderSecondary), RoundedCornerShape(8.dp))
            .clickable { sheetVisible = true }
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = months.getOrElse(selected - 1) { stringResource(R.string.month_short_label) },
            style = AriaText.labelMd,
            color = c.textSecondary,
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = stringResource(R.string.cd_select_month),
            tint = c.textSecondary,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun HeroRoiCard(
    state: DashboardLiderUiState,
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    onMonthSelectionFinished: (Int) -> Unit,
) {
    val c = AriaTheme.colors
    val displayAmount = state.roiDisplayAmount.takeIf { it > 0 } ?: state.roiAmount
    val displayDelta = state.roiDisplayDeltaPercent
    val year = currentYearLabel()
    val chartPoints = state.monthlyRoiPoints.ifEmpty { state.roiSparkline }
    val chartLabels = state.monthlyMonthRes.map { "${stringResource(it)}/$year" }
    val maxChartIndex = (chartPoints.size - 1).coerceAtLeast(0)
    val displayLabel = stringResource(
        state.monthlyMonthRes.getOrElse(selectedMonth - 1) { state.roiDisplayMonthRes },
    )
    val isPositive = displayDelta >= 0
    val badgeColor = if (isPositive) Color(0xFFA5D6A7) else Color(0xFFEF9A9A)
    val trendIcon = if (isPositive) Icons.AutoMirrored.Outlined.TrendingUp else Icons.AutoMirrored.Outlined.TrendingDown
    val roiParts = formatCurrencyDisplayParts(displayAmount)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(c.primaryMain)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.dashboard_roi_label, displayLabel, currentYearLabel()),
                style = AriaText.labelMd,
                color = Color.White.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = roiParts.symbol,
                    color = Color.White.copy(alpha = 0.7f),
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.ExtraLight,
                        fontSize = 14.sp,
                    )
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = roiParts.amount,
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.ExtraLight,
                        fontSize = 36.sp,
                        lineHeight = 40.sp,
                        letterSpacing = (-1).sp,
                    )
                )
                if (roiParts.suffix.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = roiParts.suffix,
                        color = Color.White.copy(alpha = 0.7f),
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 22.sp,
                        ),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}$displayDelta%",
                        color = badgeColor,
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            InteractiveSparkline(
                points = chartPoints,
                labels = chartLabels,
                externalSelectedIndex = (selectedMonth - 1).coerceIn(0, maxChartIndex),
                maxSelectableIndex = maxChartIndex,
                lineColor = Color.White,
                chartHeight = 64.dp,
                onIndexChanged = { idx -> onMonthSelected(idx + 1) },
                onSelectionFinished = { idx -> onMonthSelectionFinished(idx + 1) },
                formatTooltip = { point, _, label ->
                    "${formatCurrencyCompact(point.toDouble())} · $label"
                },
            )
        }
    }
}

@Composable
private fun AccumulatedRoiCard(state: DashboardLiderUiState) {
    val c = AriaTheme.colors
    val isPositive = state.roiDeltaPercent >= 0
    val deltaColor = if (isPositive) c.success else c.error
    val deltaSign = if (isPositive) "+" else ""
    val months = stringArrayResource(R.array.months_short)
    val monthLabel = stringResource(
        state.monthlyMonthRes.getOrElse(state.selectedMonth - 1) { state.roiDisplayMonthRes },
    )
    val monthRoi = state.roiDisplayAmount.takeIf { it > 0 } ?: state.roiAmount
    val hasPreviousMonth = state.selectedMonth > 1
    val previousMonth = if (hasPreviousMonth) {
        months.getOrElse(state.selectedMonth - 2) { "" }
    } else {
        ""
    }
    val accumulatedParts = formatCurrencyDisplayParts(state.roiAccumulated)

    AriaCard(padding = 16.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_roi_accumulated_label),
                    style = AriaText.labelMd,
                    color = c.textTertiary,
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = accumulatedParts.symbol,
                        style = AriaText.bodyMd,
                        color = c.textSecondary,
                    )
                    Text(
                        text = accumulatedParts.amount,
                        color = c.textPrimary,
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            letterSpacing = (-0.5).sp,
                        ),
                    )
                    if (accumulatedParts.suffix.isNotEmpty()) {
                        Text(
                            text = accumulatedParts.suffix,
                            color = c.textSecondary,
                            style = TextStyle(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                            ),
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                }
            }

            AriaHairline()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_roi_in_month, monthLabel),
                    style = AriaText.labelMd,
                    color = c.textTertiary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatCurrencyCompact(monthRoi),
                        style = AriaText.titleMd.copy(fontSize = 16.sp),
                        color = c.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    if (hasPreviousMonth) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(deltaColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Icon(
                                    imageVector = if (isPositive) {
                                        Icons.AutoMirrored.Outlined.TrendingUp
                                    } else {
                                        Icons.AutoMirrored.Outlined.TrendingDown
                                    },
                                    contentDescription = null,
                                    tint = deltaColor,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = "$deltaSign${state.roiDeltaPercent}%",
                                    color = deltaColor,
                                    style = TextStyle(
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                    ),
                                )
                            }
                            Text(
                                text = stringResource(R.string.dashboard_delta_vs_previous, previousMonth),
                                style = AriaText.labelMd,
                                color = c.textTertiary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(state: DashboardLiderUiState) {
    val months = stringArrayResource(R.array.months_short)
    val previousMonthIdx = (state.selectedMonth - 2).coerceIn(0, (state.availableMonthCount - 1).coerceAtLeast(0))
    val previousMonth = months.getOrElse(previousMonthIdx) { "" }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.dashboard_metric_ideas_submitted),
                value = "${state.ideasSubmitted}",
                trend = formatDelta(state.ideasSubmittedDelta, percent = false),
                trendDir = trendDirOf(state.ideasSubmittedDelta),
                sub = stringResource(R.string.dashboard_delta_vs_previous, previousMonth),
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.dashboard_metric_approval_rate),
                value = "${state.approvalRatePercent}%",
                trend = formatDelta(state.approvalRateDelta, percent = true),
                trendDir = trendDirOf(state.approvalRateDelta),
                modifier = Modifier.weight(1f),
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.dashboard_metric_active_projects),
                value = "${state.activeProjects}",
                trend = formatDelta(state.activeProjectsDelta, percent = false),
                trendDir = trendDirOf(state.activeProjectsDelta),
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.dashboard_metric_conversion),
                value = "${state.conversionPercent}%",
                trend = formatDelta(state.conversionDelta, percent = true),
                trendDir = trendDirOf(state.conversionDelta),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private val CategoryColorMap: Map<IdeaCategory, Color> = mapOf(
    IdeaCategory.TECNOLOGIA to Color(0xFF1A2540),
    IdeaCategory.PROCESSO to Color(0xFFC87D0E),
    IdeaCategory.PRODUTO to Color(0xFF4A6BB5),
    IdeaCategory.SUSTENTABILIDADE to Color(0xFF34A853),
    IdeaCategory.ATENDIMENTO to Color(0xFF7A7060),
)

@Composable
private fun categoryShortLabel(c: IdeaCategory): String = when (c) {
    IdeaCategory.TECNOLOGIA -> stringResource(R.string.idea_category_technology)
    IdeaCategory.PROCESSO -> stringResource(R.string.idea_category_process)
    IdeaCategory.PRODUTO -> stringResource(R.string.idea_category_product)
    IdeaCategory.SUSTENTABILIDADE -> stringResource(R.string.idea_category_short_sustain)
    IdeaCategory.ATENDIMENTO -> stringResource(R.string.idea_category_service)
    else -> stringResource(R.string.idea_category_others)
}

@Composable
private fun CategoryDistributionSection(state: DashboardLiderUiState) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.dashboard_distribution_title))
    AriaCard(padding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DonutChart(
                slices = state.categoryDistribution,
                centerText = "${state.ideasSubmitted}",
                centerSub = stringResource(R.string.dashboard_distribution_center_label),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.categoryDistribution.forEach { share ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CategoryColorMap[share.category] ?: c.primaryMain)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = categoryShortLabel(share.category),
                            color = c.textPrimary,
                            style = AriaText.bodyMd,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${share.percent}%",
                            color = c.textSecondary,
                            style = TextStyle(
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(slices: List<CategoryShare>, centerText: String, centerSub: String) {
    val c = AriaTheme.colors
    val ringSize = 120.dp
    val strokeWidth = 12.dp
    Box(modifier = Modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val r = (size.minDimension - sw) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val arcSize = androidx.compose.ui.geometry.Size(r * 2f, r * 2f)
            val topLeft = Offset(center.x - r, center.y - r)
            val total = slices.sumOf { it.percent }.coerceAtLeast(1)
            drawArc(
                color = c.bgTertiary,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(sw, cap = StrokeCap.Round),
            )
            var startAngle = -90f
            slices.forEach { share ->
                val sweep = (share.percent.toFloat() / total.toFloat()) * 360f
                if (sweep > 0f) {
                    drawArc(
                        color = CategoryColorMap[share.category] ?: c.primaryMain,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(sw, cap = StrokeCap.Round),
                    )
                }
                startAngle += sweep
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(strokeWidth + 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = centerText,
                color = c.textPrimary,
                style = TextStyle(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = centerSub,
                color = c.textTertiary,
                style = AriaText.labelMd.copy(fontSize = 9.sp, lineHeight = 11.sp),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun TopProjectsSection(state: DashboardLiderUiState, onProjectClick: (String) -> Unit) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.dashboard_top_projects_title), sub = stringResource(R.string.dashboard_top_projects_sub))
    AriaCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            state.topProjects.forEachIndexed { index, p ->
                Column(modifier = Modifier.clickable { onProjectClick(p.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = p.title, style = AriaText.bodyMd, color = c.textPrimary)
                        Text(
                            text = formatCurrencyCompact(p.valueReais),
                            color = c.textPrimary,
                            style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    AriaProgressLine(
                        value = p.valueReais.toFloat() / p.maxReais.toFloat(),
                        color = if (index == 0) c.accentMain else c.primaryMain,
                    )
                }
            }
        }
    }
}

@Composable
private fun FunnelSection(state: DashboardLiderUiState) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.dashboard_funnel_title))
    AriaCard(padding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.funnel.forEachIndexed { i, step ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text(text = stringResource(step.labelRes), style = AriaText.bodyMd, color = c.textSecondary)
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (step.convertPercent != null) Text(
                                text = step.convertPercent,
                                color = c.success,
                                style = AriaText.labelMd,
                            )
                            Text(
                                text = "${step.count}",
                                color = c.textPrimary,
                                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val barColor = when (i) {
                        0 -> c.primaryMain
                        1 -> c.primaryMedium
                        2 -> c.primaryLight
                        else -> c.accentMain
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(step.widthPercent / 100f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiInsightsSection(state: DashboardLiderUiState) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.dashboard_ai_insights_title))
    if (state.aiInsights.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_ai_insights))
        return
    }
    AriaCard(padding = 16.dp) {
        Column {
            state.aiInsights.forEachIndexed { i, insight ->
                if (i > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AriaHairline()
                    Spacer(modifier = Modifier.height(14.dp))
                }
                AiInsightRow(insight)
            }
        }
    }
}

@Composable
private fun AiInsightRow(insight: AiInsightUi) {
    val c = AriaTheme.colors
    val (bgIcon, fgIcon, eyebrowColor, overlay) = when (insight.tone) {
        AiInsightTone.Accent -> AiTuple(c.accentSubtle, c.accentMain, c.accentMain, AiInsightSlot.Text("✦"))
        AiInsightTone.Success -> AiTuple(c.successBg, c.success, c.success, AiInsightSlot.Vector(Icons.AutoMirrored.Outlined.TrendingUp))
        AiInsightTone.Info -> AiTuple(c.infoBg, c.info, c.info, AiInsightSlot.Text("ℹ"))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgIcon),
            contentAlignment = Alignment.Center
        ) {
            when (overlay) {
                is AiInsightSlot.Text -> Text(text = overlay.s, color = fgIcon, style = TextStyle(fontSize = 14.sp))
                is AiInsightSlot.Vector -> Icon(
                    imageVector = overlay.v,
                    contentDescription = null,
                    tint = fgIcon,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column {
            Text(text = stringResource(insight.eyebrowRes), style = AriaText.labelMd, color = eyebrowColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = insight.body, style = AriaText.bodyLg, color = c.textPrimary)
        }
    }
}

private data class AiTuple(val bg: Color, val fg: Color, val eyebrow: Color, val slot: AiInsightSlot)

private sealed class AiInsightSlot {
    data class Text(val s: String) : AiInsightSlot()
    data class Vector(val v: ImageVector) : AiInsightSlot()
}

private fun formatDelta(delta: Int, percent: Boolean): String {
    val sign = if (delta > 0) "+" else ""
    val suffix = if (percent) "%" else ""
    return "$sign$delta$suffix"
}

private fun trendDirOf(delta: Int): AriaTrendDir = when {
    delta > 0 -> AriaTrendDir.Up
    delta < 0 -> AriaTrendDir.Down
    else -> AriaTrendDir.None
}

@Composable
private fun currentMonthLabel(months: Array<String>): String {
    val now = remember { java.time.LocalDate.now() }
    return months.getOrElse(now.monthValue - 1) { "" }
}

@Composable
private fun currentYearLabel(): String {
    val now = remember { java.time.LocalDate.now() }
    return now.year.toString()
}
