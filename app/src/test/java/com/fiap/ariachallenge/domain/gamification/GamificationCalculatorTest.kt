package com.fiap.ariachallenge.domain.gamification

import com.fiap.ariachallenge.data.mock.MockIdeas
import com.fiap.ariachallenge.domain.model.Badge
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationCalculatorTest {

    @Test
    fun calculatePoints_emptyList_returnsZero() {
        assertEquals(0, GamificationCalculator.calculatePoints(emptyList()))
    }

    @Test
    fun calculatePoints_submittedApprovedAndProject() {
        val base = MockIdeas.allIdeas.first()
        val ideas = listOf(
            base.copy(status = IdeaStatus.APROVADA, score = 85),
            base.copy(id = "idea_2", status = IdeaStatus.EM_PROJETO),
        )
        val expected =
            2 * GamificationPoints.SUBMIT_IDEA +
                GamificationPoints.APPROVED_IDEA +
                GamificationPoints.HIGH_SCORE_BONUS +
                GamificationPoints.PROJECT_CREATED
        assertEquals(expected, GamificationCalculator.calculatePoints(ideas))
    }

    @Test
    fun calculateBadgeIds_unlocksExpectedBadges() {
        val base = MockIdeas.allIdeas.first()
        val ideas = buildList {
            add(base.copy(status = IdeaStatus.APROVADA, score = 90))
            add(base.copy(id = "idea_2", status = IdeaStatus.EM_PROJETO))
            repeat(4) { index ->
                add(base.copy(id = "idea_extra_$index", status = IdeaStatus.AGUARDANDO_ANALISE))
            }
        }
        val badges = GamificationCalculator.calculateBadgeIds(ideas)
        assertTrue(badges.contains(Badge.FIRST_IDEA.id))
        assertTrue(badges.contains(Badge.APPROVED_IDEA.id))
        assertTrue(badges.contains(Badge.HIGH_SCORER.id))
        assertTrue(badges.contains(Badge.PROJECT_CREATOR.id))
        assertTrue(badges.contains(Badge.INNOVATOR_5.id))
    }
}
