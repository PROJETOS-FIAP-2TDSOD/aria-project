package com.fiap.ariachallenge.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.UserRole

@Composable
@ReadOnlyComposable
fun IdeaCategory.localizedName(): String = stringResource(getDisplayNameRes())

@Composable
@ReadOnlyComposable
fun IdeaStatus.localizedName(): String = stringResource(getDisplayNameRes())

@Composable
@ReadOnlyComposable
fun UserRole.localizedName(): String = stringResource(getDisplayNameRes())

@Composable
@ReadOnlyComposable
fun ProjectStatus.localizedName(): String = stringResource(getDisplayNameRes())

@Composable
@ReadOnlyComposable
fun OrientationPriority.localizedName(): String = stringResource(getDisplayNameRes())

@Composable
@ReadOnlyComposable
fun stringFromRes(@StringRes resId: Int?): String? = resId?.let { stringResource(it) }
