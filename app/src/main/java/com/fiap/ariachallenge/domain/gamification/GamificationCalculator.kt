package com.fiap.ariachallenge.domain.gamification

import com.fiap.ariachallenge.domain.model.Badge
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus

object GamificationCalculator {

    fun calculatePoints(ideas: List<Idea>): Int {
        val submitted = ideas.size
        val approved = ideas.count { it.status == IdeaStatus.APROVADA }
        val highScore = ideas.count { (it.score ?: 0) >= GamificationPoints.HIGH_SCORE_THRESHOLD }
        val inProject = ideas.count { it.status == IdeaStatus.EM_PROJETO }

        return submitted * GamificationPoints.SUBMIT_IDEA +
            approved * GamificationPoints.APPROVED_IDEA +
            highScore * GamificationPoints.HIGH_SCORE_BONUS +
            inProject * GamificationPoints.PROJECT_CREATED
    }

    fun calculateBadgeIds(ideas: List<Idea>): List<String> {
        val badges = mutableListOf<String>()

        if (ideas.isNotEmpty()) {
            badges += Badge.FIRST_IDEA.id
        }
        if (ideas.size >= 5) {
            badges += Badge.INNOVATOR_5.id
        }
        if (ideas.any { it.status == IdeaStatus.APROVADA }) {
            badges += Badge.APPROVED_IDEA.id
        }
        if (ideas.any { (it.score ?: 0) >= GamificationPoints.HIGH_SCORE_THRESHOLD }) {
            badges += Badge.HIGH_SCORER.id
        }
        if (ideas.any { it.status == IdeaStatus.EM_PROJETO }) {
            badges += Badge.PROJECT_CREATOR.id
        }

        return badges
    }
}
