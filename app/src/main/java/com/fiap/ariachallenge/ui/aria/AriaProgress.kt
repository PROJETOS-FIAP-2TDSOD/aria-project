package com.fiap.ariachallenge.ui.aria

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import kotlin.math.min

object AriaScoreRingDefaults {
    val Card = 72.dp
    val Detail = 132.dp
    val Hero = 140.dp
}

@Composable
fun AriaProgressLine(
    value: Float,
    modifier: Modifier = Modifier,
    color: Color? = null,
    trackColor: Color? = null,
    height: Dp = 6.dp,
) {
    val c = AriaTheme.colors
    val barColor = color ?: c.primaryMain
    val track = trackColor ?: c.bgTertiary
    val anim = remember { Animatable(0f) }
    LaunchedEffect(value) {
        anim.animateTo(value.coerceIn(0f, 1f), tween(600, easing = FastOutSlowInEasing))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(track)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(anim.value)
                .height(height)
                .background(barColor)
        )
    }
}

@Composable
fun AriaScoreRing(
    value: Int,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    label: String? = "VIABILIDADE",
    strokeWidth: Dp? = null,
    maxValue: Int = 100,
) {
    val c = AriaTheme.colors
    val scoreColor = when {
        value < 41 -> c.error
        value < 71 -> c.warning
        else -> c.success
    }
    val resolvedStroke = strokeWidth ?: when {
        size < 72.dp -> 5.dp
        size < 100.dp -> 7.dp
        else -> 9.dp
    }
    val useInlineCenter = size < 92.dp
    val anim = remember { Animatable(0f) }
    LaunchedEffect(value, maxValue) {
        val target = if (maxValue > 0) value.toFloat() / maxValue else 0f
        anim.animateTo(target.coerceIn(0f, 1f), tween(600, easing = FastOutSlowInEasing))
    }

    val a11yLabel = "$value de $maxValue"

    Column(
        modifier = modifier.semantics { contentDescription = a11yLabel },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sw = resolvedStroke.toPx()
                val r = (this.size.minDimension - sw) / 2f
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val arcSize = Size(r * 2f, r * 2f)
                val topLeft = Offset(center.x - r, center.y - r)
                drawArc(
                    color = c.bgTertiary,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(sw, cap = StrokeCap.Round),
                )
                drawArc(
                    color = scoreColor,
                    startAngle = -90f,
                    sweepAngle = 360f * anim.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(sw, cap = StrokeCap.Round),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(resolvedStroke + 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints(contentAlignment = Alignment.Center) {
                    val innerMin = min(maxWidth.value, maxHeight.value).coerceAtLeast(1f)
                    val scoreFontSize = when {
                        useInlineCenter -> (innerMin * 0.44f).coerceIn(14f, 20f)
                        else -> (innerMin * 0.36f).coerceIn(22f, 40f)
                    }.sp
                    val denomFontSize = when {
                        useInlineCenter -> (innerMin * 0.22f).coerceIn(8f, 11f)
                        else -> (innerMin * 0.18f).coerceIn(9f, 12f)
                    }.sp
                    val scoreStyle = AriaText.metricSm.copy(
                        fontSize = scoreFontSize,
                        lineHeight = scoreFontSize,
                        fontWeight = FontWeight.SemiBold,
                    )
                    val denomStyle = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = denomFontSize,
                        lineHeight = denomFontSize,
                        letterSpacing = 0.sp,
                    )

                    if (useInlineCenter) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = value.toString(),
                                style = scoreStyle,
                                color = c.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Visible,
                                softWrap = false,
                            )
                            Text(
                                text = "/$maxValue",
                                style = denomStyle,
                                color = c.textTertiary,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 1.dp, bottom = (scoreFontSize.value * 0.08f).dp),
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            Text(
                                text = value.toString(),
                                style = scoreStyle,
                                color = c.textPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Text(
                                text = "/ $maxValue",
                                style = denomStyle,
                                color = c.textTertiary,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = AriaText.labelMd,
                color = c.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
