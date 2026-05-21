package com.fiap.ariachallenge.domain.model

import androidx.annotation.StringRes
import com.fiap.ariachallenge.R

enum class Badge(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
) {
    FIRST_IDEA(
        id = "first_idea",
        nameRes = R.string.badge_first_idea_name,
        descriptionRes = R.string.badge_first_idea_desc,
    ),
    INNOVATOR_5(
        id = "innovator_5",
        nameRes = R.string.badge_innovator_5_name,
        descriptionRes = R.string.badge_innovator_5_desc,
    ),
    APPROVED_IDEA(
        id = "approved_idea",
        nameRes = R.string.badge_approved_name,
        descriptionRes = R.string.badge_approved_desc,
    ),
    HIGH_SCORER(
        id = "high_scorer",
        nameRes = R.string.badge_high_scorer_name,
        descriptionRes = R.string.badge_high_scorer_desc,
    ),
    PROJECT_CREATOR(
        id = "project_creator",
        nameRes = R.string.badge_project_name,
        descriptionRes = R.string.badge_project_desc,
    );

    companion object {
        val all: List<Badge> = entries
        fun fromId(id: String): Badge? = entries.find { it.id == id }
    }
}
