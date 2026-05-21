package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fiap.ariachallenge.R
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

enum class AriaTrendDir { Up, Down, None }

@Composable
fun AriaCard(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    accent: Boolean = false,
    background: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val c = AriaTheme.colors
    val borderColor = if (accent) c.accentMain else c.borderTertiary
    val bg = background ?: c.surface
    val base = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(bg)
        .border(BorderStroke(0.5.dp, borderColor), RoundedCornerShape(12.dp))
    val withClick = if (onClick != null) base.clickable(onClick = onClick) else base
    Box(modifier = withClick.padding(padding)) {
        content()
    }
}

@Composable
fun AriaMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
    trendDir: AriaTrendDir = AriaTrendDir.None,
    sub: String? = null,
    accent: Boolean = false,
) {
    val c = AriaTheme.colors
    val bg = if (accent) c.primarySubtle else c.surface
    val borderColor = if (accent) Color.Transparent else c.borderTertiary

    val trendColor = when (trendDir) {
        AriaTrendDir.Up -> c.success
        AriaTrendDir.Down -> c.error
        AriaTrendDir.None -> c.textTertiary
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .let { m -> if (!accent) m.border(BorderStroke(0.5.dp, borderColor), RoundedCornerShape(12.dp)) else m }
            .heightIn(min = 96.dp)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label.uppercase(), style = AriaText.labelMd, color = c.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = AriaText.metricSm, color = c.textPrimary)
        if (trend != null || sub != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (trend != null) {
                    val trendIcon = when (trendDir) {
                        AriaTrendDir.Up -> Icons.AutoMirrored.Filled.TrendingUp
                        AriaTrendDir.Down -> Icons.AutoMirrored.Filled.TrendingDown
                        AriaTrendDir.None -> Icons.AutoMirrored.Filled.ArrowForward
                    }
                    Icon(
                        imageVector = trendIcon,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = trend,
                        color = trendColor,
                        style = AriaText.labelMd.copy(letterSpacing = 0.5.sp),
                    )
                }
                if (sub != null) {
                    Text(
                        text = sub,
                        color = c.textTertiary,
                        style = AriaText.labelMd.copy(letterSpacing = 0.5.sp),
                    )
                }
            }
        }
    }
}

@Composable
fun AriaAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    tone: AvatarTone = AvatarTone.Primary,
    avatarLocalPath: String? = null,
) {
    val c = AriaTheme.colors
    val (bg, fg) = when (tone) {
        AvatarTone.Primary -> c.primaryMain to Color.White
        AvatarTone.Accent -> c.accentMain to Color.White
        AvatarTone.Subtle -> c.primarySubtle to c.primaryMain
    }
    val initials = name.split(" ").mapNotNull { it.firstOrNull() }
        .take(2).joinToString("") { it.toString() }.uppercase()
    val photoFile = avatarLocalPath?.let { path -> File(path).takeIf { it.exists() } }
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        if (photoFile != null) {
            val cacheKey = "${photoFile.absolutePath}:${photoFile.lastModified()}"
            key(cacheKey) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoFile)
                        .diskCacheKey(cacheKey)
                        .memoryCacheKey(cacheKey)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.cd_user_avatar),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        } else {
            Text(
                text = initials,
                color = fg,
                style = TextStyle(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (size.value * 0.36f).sp,
                    letterSpacing = 0.5.sp,
                ),
            )
        }
    }
}

enum class AvatarTone { Primary, Accent, Subtle }
