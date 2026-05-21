package com.fiap.ariachallenge.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.aria.AriaAvatar
import com.fiap.ariachallenge.ui.aria.AvatarTone
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun EditableProfileAvatar(
    name: String,
    avatarLocalPath: String?,
    onImagePicked: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
    tone: AvatarTone = AvatarTone.Primary,
) {
    val c = AriaTheme.colors
    val changePhotoLabel = stringResource(R.string.profile_change_photo)
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) onImagePicked(uri)
    }

    val handlePickPhoto = {
        pickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = changePhotoLabel
                }
                .clip(CircleShape)
                .clickable(onClick = handlePickPhoto),
        ) {
            AriaAvatar(
                name = name,
                size = size,
                tone = tone,
                avatarLocalPath = avatarLocalPath,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(c.accentMain)
                .border(2.dp, c.surface, CircleShape)
                .clickable(onClick = handlePickPhoto)
                .semantics { contentDescription = changePhotoLabel },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = c.textOnAccent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
