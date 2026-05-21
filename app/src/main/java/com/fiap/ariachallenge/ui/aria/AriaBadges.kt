package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

enum class AriaStatus {
    Pending, Review, Approved, Rejected, Project, Active, Running
}

@Composable
fun AriaStatusBadge(status: AriaStatus, modifier: Modifier = Modifier) {
    val c = AriaTheme.colors
    val label = when (status) {
        AriaStatus.Pending -> stringResource(R.string.aria_status_pending)
        AriaStatus.Review -> stringResource(R.string.aria_status_review)
        AriaStatus.Approved -> stringResource(R.string.aria_status_approved)
        AriaStatus.Rejected -> stringResource(R.string.aria_status_rejected)
        AriaStatus.Project -> stringResource(R.string.aria_status_project)
        AriaStatus.Active -> stringResource(R.string.aria_status_active)
        AriaStatus.Running -> stringResource(R.string.aria_status_running)
    }
    val (bg, fg) = when (status) {
        AriaStatus.Pending -> c.warningBg to c.accentDark
        AriaStatus.Review -> c.primarySubtle to c.primaryMain
        AriaStatus.Approved -> c.successBg to c.success
        AriaStatus.Rejected -> c.errorBg to c.error
        AriaStatus.Project -> c.infoBg to c.info
        AriaStatus.Active -> c.successBg to c.success
        AriaStatus.Running -> c.infoBg to c.info
    }
    AriaPillLabel(text = label.uppercase(), bg = bg, fg = fg, modifier = modifier)
}

@Composable
fun AriaPillLabel(
    text: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = AriaText.labelMd,
        color = fg,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun AriaScoreBadge(value: Int, modifier: Modifier = Modifier) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(c.bgTertiary)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = stringResource(R.string.label_score_uppercase), style = AriaText.labelMd, color = c.textSecondary.copy(alpha = 0.6f))
        Text(
            text = value.toString(),
            color = c.textSecondary,
            style = TextStyle(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.sp,
            ),
        )
    }
}

@Composable
fun AriaCategoryTag(text: String, modifier: Modifier = Modifier) {
    val c = AriaTheme.colors
    Text(
        text = text.uppercase(),
        style = AriaText.labelMd,
        color = c.textSecondary,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(BorderStroke(0.5.dp, c.borderSecondary), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
