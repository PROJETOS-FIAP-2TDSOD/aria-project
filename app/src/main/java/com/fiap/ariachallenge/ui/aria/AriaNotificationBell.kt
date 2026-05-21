package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.operador.notificacoes.NotificacoesViewModel
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

@Composable
fun AriaNotificationBell(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    viewModel: NotificacoesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val unread = uiState.notifications.count { !it.isRead }
    val c = AriaTheme.colors
    val iconTint = tint ?: c.textSecondary

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = stringResource(R.string.cd_notifications_bell),
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        when {
            unread in 1..9 -> Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(c.error)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = unread.toString(),
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            unread >= 10 -> Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(c.error)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "9+",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            else -> Unit
        }
    }
}
