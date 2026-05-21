package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.fiap.ariachallenge.ui.aria.AriaHairline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Badge
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun AchievementsCard(
    userPoints: Int,
    userBadges: List<String>,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val totalBadges = Badge.all.size
    val unlockedCount = userBadges.size
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.surface)
            .border(BorderStroke(0.5.dp, c.borderTertiary), shape)
            .padding(14.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_total_points).uppercase(),
            style = AriaText.labelMd,
            color = c.textSecondary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.profile_points_value, userPoints),
                style = AriaText.metricSm,
                color = c.textPrimary,
            )
            Text(
                text = stringResource(R.string.profile_badges_progress, unlockedCount, totalBadges),
                style = AriaText.labelMd,
                color = c.textTertiary,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        AriaHairline()
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.profile_badges_title).uppercase(),
            style = AriaText.labelMd,
            color = c.textSecondary,
        )

        LazyRow(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(Badge.all) { badge ->
                BadgeDisplay(
                    badge = badge,
                    unlocked = userBadges.contains(badge.id),
                )
            }
        }
    }
}

@Composable
fun PerformanceWidget(
    userPoints: Int,
    userBadges: List<String>,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val totalBadges = Badge.all.size
    val lastUnlockedBadge = Badge.all.lastOrNull { userBadges.contains(it.id) }
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.surface)
            .border(BorderStroke(0.5.dp, c.borderTertiary), shape)
            .heightIn(min = 96.dp)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_your_performance).uppercase(),
                style = AriaText.labelMd,
                color = c.textSecondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_points_display, userPoints),
                style = AriaText.metricSm,
                color = c.textPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.home_badges_unlocked, userBadges.size, totalBadges),
                style = AriaText.labelMd,
                color = c.textTertiary,
            )
        }

        if (lastUnlockedBadge != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.primarySubtle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = lastUnlockedBadge.toIcon(),
                    contentDescription = stringResource(lastUnlockedBadge.nameRes),
                    modifier = Modifier.size(24.dp),
                    tint = c.primaryMain,
                )
            }
        }
    }
}
