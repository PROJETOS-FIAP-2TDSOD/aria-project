package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.fiap.ariachallenge.ui.theme.AriaText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun InteractiveSparkline(
    points: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    fillTopAlpha: Float = 0.25f,
    chartHeight: Dp = 52.dp,
    externalSelectedIndex: Int? = null,
    maxSelectableIndex: Int? = null,
    onIndexChanged: (Int) -> Unit = {},
    onSelectionFinished: (Int) -> Unit = {},
    formatTooltip: (point: Float, index: Int, label: String) -> String = { p, _, lbl ->
        "$lbl · ${p.roundToInt()}"
    },
) {
    if (points.isEmpty()) return
    val n = points.size
    val maxIdx = (maxSelectableIndex ?: (n - 1)).coerceIn(0, n - 1)
    var isDragging by remember { mutableStateOf(false) }
    var dragIndex by remember { mutableIntStateOf(-1) }

    val selectedIndex = when {
        isDragging && dragIndex in 0..maxIdx -> dragIndex
        externalSelectedIndex != null -> externalSelectedIndex.coerceIn(0, maxIdx)
        else -> maxIdx
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val maxV = points.max().coerceAtLeast(1f)
                val pts = points.mapIndexed { i, v ->
                    val x = if (i == 0 && n == 1) 0f else (i.toFloat() / (n - 1)) * w
                    val y = h - (v / maxV) * (h - 8f) - 4f
                    Offset(x, y)
                }
                val sel = pts[selectedIndex.coerceIn(0, pts.lastIndex)]
                val areaPath = Path().apply {
                    moveTo(0f, h)
                    pts.forEach { lineTo(it.x, it.y) }
                    lineTo(w, h)
                    close()
                }
                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = fillTopAlpha),
                            lineColor.copy(alpha = 0f),
                        ),
                    ),
                )
                val linePath = Path().apply {
                    moveTo(pts.first().x, pts.first().y)
                    pts.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
                drawLine(
                    color = lineColor.copy(alpha = 0.35f),
                    start = Offset(sel.x, 0f),
                    end = Offset(sel.x, h),
                    strokeWidth = 1.dp.toPx(),
                )
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = sel,
                )
                drawCircle(
                    color = lineColor.copy(alpha = 0.4f),
                    radius = 9.dp.toPx(),
                    center = sel,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(points, n, maxIdx) {
                        fun indexAt(x: Float): Int {
                            val span = maxIdx.coerceAtLeast(1)
                            return ((x / size.width) * span).roundToInt().coerceIn(0, maxIdx)
                        }
                        coroutineScope {
                            launch {
                                detectTapGestures { off ->
                                    isDragging = false
                                    val idx = indexAt(off.x)
                                    dragIndex = -1
                                    onIndexChanged(idx)
                                    onSelectionFinished(idx)
                                }
                            }
                            launch {
                                detectHorizontalDragGestures(
                                    onDragStart = { offset ->
                                        isDragging = true
                                        val idx = indexAt(offset.x)
                                        dragIndex = idx
                                        onIndexChanged(idx)
                                    },
                                    onDragEnd = {
                                        val idx = dragIndex.coerceIn(0, maxIdx)
                                        isDragging = false
                                        dragIndex = -1
                                        onSelectionFinished(idx)
                                    },
                                    onDragCancel = {
                                        isDragging = false
                                        dragIndex = -1
                                    },
                                ) { change, _ ->
                                    val idx = indexAt(change.position.x)
                                    if (idx != dragIndex) {
                                        dragIndex = idx
                                        onIndexChanged(idx)
                                    }
                                    change.consume()
                                }
                            }
                        }
                    },
            )
        }
        val lbl = labels.getOrNull(selectedIndex) ?: ""
        val pt = points[selectedIndex.coerceIn(0, points.lastIndex)]
        Text(
            text = formatTooltip(pt, selectedIndex, lbl),
            style = AriaText.labelMd,
            color = lineColor.copy(alpha = 0.88f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}
