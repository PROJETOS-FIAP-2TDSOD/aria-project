package com.fiap.ariachallenge.ui.aria

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun AriaEmptyState(
    title: String,
    sub: String,
    icon: ImageVector = Icons.Outlined.Lightbulb,
    cta: String? = null,
    onCta: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(c.bgSecondary)
                .border(0.5.dp, c.borderTertiary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = c.textTertiary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = AriaText.titleMd, color = c.textPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = sub,
            style = AriaText.bodyMd,
            color = c.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 260.dp),
        )
        if (cta != null && onCta != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.widthIn(max = 260.dp).fillMaxWidth()) {
                AriaPrimaryBtn(text = cta, onClick = onCta)
            }
        }
    }
}

@Composable
fun AriaSectionEmptyCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    AriaCard(modifier = modifier.fillMaxWidth(), padding = 16.dp) {
        Text(
            text = message,
            style = AriaText.bodyMd,
            color = c.textTertiary,
        )
    }
}

@Composable
fun AriaErrorState(
    onRetry: () -> Unit,
    title: String? = null,
    sub: String? = null,
    modifier: Modifier = Modifier,
) {
    val resolvedTitle = title ?: stringResource(R.string.state_error_title)
    val resolvedSub = sub ?: stringResource(R.string.state_error_load_failed)
    val c = AriaTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(c.errorBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = c.error, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = resolvedTitle, style = AriaText.titleMd, color = c.textPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = resolvedSub,
            style = AriaText.bodyMd,
            color = c.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 260.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.widthIn(max = 200.dp).fillMaxWidth()) {
            AriaSecondaryBtn(text = stringResource(R.string.action_retry), onClick = onRetry)
        }
    }
}

@Composable
fun AriaLoadingSkeleton(modifier: Modifier = Modifier) {
    val c = AriaTheme.colors
    val transition = rememberInfiniteTransition(label = "skel")
    val alpha by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), repeatMode = RepeatMode.Reverse),
        label = "alpha",
    )
    Column(modifier = modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.bgSecondary)
                        .alpha(alpha)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.bgSecondary)
                        .alpha(alpha)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(c.bgSecondary)
                .alpha(alpha)
        )
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.bgSecondary)
                    .alpha(alpha)
            )
        }
    }
}

