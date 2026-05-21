package com.fiap.ariachallenge.ui.lider.analises

import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.export.AriaAnalyticsPdfExporter
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTextBtn
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.formatCurrencyCompact
import com.fiap.ariachallenge.util.formatCurrencyDisplayParts

@Composable
fun AnaliseScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: AnaliseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.surface)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
                ) {
                    Text(text = stringResource(R.string.analyses_screen_title), style = AriaText.titleMd, color = c.textPrimary, modifier = Modifier.weight(1f))
                    AriaTextBtn(
                        text = stringResource(R.string.action_export),
                        trailingArrow = true,
                        onClick = {
                            if (uiState.isLoading) return@AriaTextBtn
                            scope.launch {
                                try {
                                    val file = withContext(Dispatchers.IO) {
                                        AriaAnalyticsPdfExporter.export(context, uiState)
                                    }
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file,
                                    )
                                    val send = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.analyses_export_subject))
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(send, context.getString(R.string.export_chooser_title)),
                                    )
                                } catch (_: Exception) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.export_pdf_error),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        },
                    )
                    AriaNotificationBell(onClick = { onNavigate(AriaDestination.LiderNotificacoes.route) })
                }
                AriaTabs(
                    items = listOf(
                        stringResource(R.string.analyses_tab_roi),
                        stringResource(R.string.analyses_tab_trends),
                        stringResource(R.string.analyses_tab_ai),
                    ),
                    selected = uiState.selectedTab,
                    onSelect = viewModel::onTabSelected,
                )
            }
        },
        bottomBar = {
            AriaBottomNav(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            when (uiState.selectedTab) {
                0 -> RoiTab(uiState, onSelectRoiMonth = viewModel::onRoiMonthSelected)
                1 -> TrendsTab(
                    state = uiState,
                    onOpenFullTrends = { onNavigate(AriaDestination.LiderTendencias.route) },
                )
                else -> AiTab(uiState)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun RoiTab(state: AnaliseUiState, onSelectRoiMonth: (Int) -> Unit) {
    val c = AriaTheme.colors
    AriaCard(padding = 20.dp) {
        Column {
            Text(text = stringResource(R.string.analyses_roi_total_label), style = AriaText.labelMd, color = c.textTertiary)
            Spacer(modifier = Modifier.height(6.dp))
            val totalParts = formatCurrencyDisplayParts(state.months.sumOf { it.value.toDouble() })
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = totalParts.symbol,
                        color = c.textTertiary,
                        style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.ExtraLight, fontSize = 14.sp),
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = totalParts.amount,
                        color = c.textPrimary,
                        style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.ExtraLight, fontSize = 32.sp, lineHeight = 36.sp),
                    )
                    if (totalParts.suffix.isNotEmpty()) {
                        Text(
                            text = totalParts.suffix,
                            color = c.textTertiary,
                            style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.ExtraLight, fontSize = 22.sp),
                        )
                    }
                }
                if (state.roiDeltaPercent != 0) {
                    val roiUp = state.roiDeltaPercent > 0
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (roiUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (roiUp) c.success else c.error,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${if (roiUp) "+" else ""}${state.roiDeltaPercent}%",
                            color = if (roiUp) c.success else c.error,
                            style = AriaText.labelMd,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.analyses_roi_chart_hint),
                style = AriaText.labelMd,
                color = c.textTertiary,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            InteractiveRoiBarChart(
                months = state.months,
                selectedIndex = state.selectedRoiMonthIndex,
                onSelectMonth = onSelectRoiMonth,
            )
        }
    }

    AriaSectionTitle(text = stringResource(R.string.analyses_drilldown_title), sub = stringResource(R.string.analyses_drilldown_subtitle))
    if (state.topProjects.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_analyses_projects))
    } else {
        AriaCard(padding = 4.dp) {
            Column {
                state.topProjects.forEachIndexed { i, p ->
                    ProjectRow(p)
                    if (i < state.topProjects.lastIndex) AriaHairline()
                }
            }
        }
    }
}

@Composable
private fun InteractiveRoiBarChart(
    months: List<MonthRoiUi>,
    selectedIndex: Int,
    onSelectMonth: (Int) -> Unit,
) {
    val c = AriaTheme.colors
    if (months.isEmpty()) return
    val maxV = months.maxOfOrNull { it.value } ?: 1f
    val sel = months.getOrNull(selectedIndex)

    Column {
        if (sel != null) {
            Text(
                text = stringResource(
                    R.string.analyses_roi_bar_selected,
                    stringResource(sel.monthRes),
                    formatCurrencyCompact(sel.value.toDouble()),
                ),
                style = AriaText.titleMd.copy(fontSize = 15.sp),
                color = c.textPrimary,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        val barCap = 72.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
        ) {
            months.forEachIndexed { i, m ->
                val selected = i == selectedIndex
                val barH = ((m.value / maxV) * barCap.value).dp.coerceAtLeast(4.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectMonth(i) }
                        .padding(horizontal = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        text = formatCurrencyCompact(m.value.toDouble()),
                        color = if (selected) c.accentMain else c.textTertiary,
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.sp,
                            lineHeight = 9.sp,
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barH)
                            .clip(RoundedCornerShape(3.dp, 3.dp, 0.dp, 0.dp))
                            .background(if (selected) c.accentMain else c.primaryMain.copy(alpha = 0.55f))
                            .then(
                                if (selected) {
                                    Modifier.border(
                                        BorderStroke(1.dp, c.accentLight),
                                        RoundedCornerShape(3.dp, 3.dp, 0.dp, 0.dp),
                                    )
                                } else {
                                    Modifier
                                },
                            ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(m.monthRes),
                        style = AriaText.labelMd.copy(fontSize = 9.sp, lineHeight = 10.sp),
                        color = if (selected) c.textPrimary else c.textTertiary,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectRow(p: ProjectRoiUi) {
    val c = AriaTheme.colors
    val above = p.realizedReais >= p.targetReais
    val progress = (p.realizedReais.toFloat() / p.targetReais.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = p.title, style = AriaText.bodyMd, color = c.textPrimary)
            Row {
                Text(
                    text = formatCurrencyCompact(p.realizedReais),
                    color = if (above) c.success else c.textPrimary,
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                )
                Text(
                    text = " / ${formatCurrencyCompact(p.targetReais)}",
                    color = c.textTertiary,
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp),
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        AriaProgressLine(value = progress, color = if (above) c.success else c.primaryMain)
    }
}

@Composable
private fun TrendsTab(state: AnaliseUiState, onOpenFullTrends: () -> Unit) {
    val c = AriaTheme.colors
    AriaTextBtn(
        text = stringResource(R.string.analyses_open_trends_screen),
        trailingArrow = true,
        onClick = onOpenFullTrends,
        modifier = Modifier.padding(bottom = 12.dp),
    )
    AriaCard(padding = 16.dp) {
        Column {
            Text(text = stringResource(R.string.analyses_quarter_variation), style = AriaText.labelMd, color = c.textTertiary)
            Spacer(modifier = Modifier.height(16.dp))
            if (state.trends.isEmpty()) {
                Text(
                    text = stringResource(R.string.state_section_empty_analyses_trends),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.trends.forEach { t ->
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = stringResource(t.labelRes), style = AriaText.bodyMd, color = c.textPrimary)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = if (t.up) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if (t.up) c.success else c.error,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = t.changeLabel,
                                    color = if (t.up) c.success else c.error,
                                    style = AriaText.labelMd,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        AriaProgressLine(
                            value = (t.percent * 2.4f / 100f).coerceIn(0f, 1f),
                            color = if (t.up) c.primaryMain else c.textTertiary,
                        )
                    }
                }
            }
            }
        }
    }

    if (state.emerging.isNotEmpty()) {
    AriaSectionTitle(text = stringResource(R.string.analyses_emerging_trends), sub = stringResource(R.string.analyses_emerging_trends_sub))
    AriaCard(padding = 16.dp) {
        Column {
            state.emerging.forEachIndexed { i, e ->
                if (i > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AriaHairline()
                    Spacer(modifier = Modifier.height(14.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val (bg, fg, ic) = when (e.tone) {
                        EmergingTone.Accent -> Triple(c.accentSubtle, c.accentMain, "🔥")
                        EmergingTone.Info -> Triple(c.infoBg, c.info, "✦")
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = ic, style = TextStyle(fontSize = 14.sp))
                    }
                    Column {
                        Text(text = e.title, style = AriaText.titleMd.copy(fontSize = 14.sp), color = c.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = e.body, style = AriaText.bodyMd, color = c.textSecondary)
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun AiTab(state: AnaliseUiState) {
    val c = AriaTheme.colors
    AriaCard(padding = 16.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(c.accentSubtle),
                    contentAlignment = Alignment.Center,
                ) { Text(text = "✦", color = c.accentMain, style = TextStyle(fontSize = 18.sp)) }
                Column {
                    Text(text = state.aiPredictionSub, style = AriaText.labelMd, color = c.accentMain)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = state.aiPredictionTitle, style = AriaText.titleMd.copy(fontSize = 14.sp), color = c.textPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (state.aiPredictions.isEmpty()) {
                Text(
                    text = stringResource(R.string.state_section_empty_analyses_ai),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.aiPredictions.forEach { pred ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(c.bgSecondary)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        val accentColor = when (pred.accentColor) {
                            AiAccentColor.Success -> c.success
                            AiAccentColor.Accent -> c.accentMain
                            AiAccentColor.Info -> c.info
                        }
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(accentColor)
                        )
                        Text(text = pred.title, style = AriaText.bodyMd, color = c.textPrimary)
                    }
                }
            }
            }
        }
    }

    if (state.aiRecommendations.isNotEmpty()) {
    AriaSectionTitle(text = stringResource(R.string.analyses_recommendations))
    AriaCard(padding = 4.dp) {
        Column {
            state.aiRecommendations.forEachIndexed { i, r ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(c.primarySubtle),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = c.primaryMain,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Text(text = r.text, style = AriaText.bodyMd, color = c.textPrimary, modifier = Modifier.weight(1f))
                }
                if (i < state.aiRecommendations.lastIndex) AriaHairline()
            }
        }
    }
    } else {
        AriaSectionTitle(text = stringResource(R.string.analyses_recommendations))
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_analyses_recommendations))
    }
}

