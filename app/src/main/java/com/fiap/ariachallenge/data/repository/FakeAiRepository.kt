package com.fiap.ariachallenge.data.repository



import android.content.Context

import com.fiap.ariachallenge.data.mock.MockAi

import com.fiap.ariachallenge.domain.model.AiAnalyzeBrief

import com.fiap.ariachallenge.domain.model.AiAnalyticsBundle

import com.fiap.ariachallenge.domain.model.AiDashboardMonthInsight

import com.fiap.ariachallenge.domain.model.AiOrientationAssist

import com.fiap.ariachallenge.domain.model.AiScoreBreakdownItem

import com.fiap.ariachallenge.domain.model.AiSimilarIdeaSuggestion

import com.fiap.ariachallenge.domain.model.AiTextInsight

import com.fiap.ariachallenge.domain.model.AiTimelineEvent

import com.fiap.ariachallenge.domain.repository.IAiRepository

import com.fiap.ariachallenge.domain.repository.IIdeaRepository

import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject

import javax.inject.Singleton

import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.first



@Singleton

class FakeAiRepository @Inject constructor(

    @ApplicationContext private val context: Context,

    private val ideaRepository: IIdeaRepository,

) : IAiRepository {



    private val configuration get() = context.resources.configuration



    override suspend fun getOperadorHomeSuggestion(userId: String): AiSimilarIdeaSuggestion? {

        delay(400)

        val sourceIdeaId = ideaRepository.getAllIdeas().first()

            .filter { it.author.id == userId }

            .maxByOrNull { it.updatedAt }

            ?.id

            ?: return null

        return MockAi.getOperadorHomeSuggestion(configuration, sourceIdeaId = sourceIdeaId)

    }



    override suspend fun getGestorHomeInsights(): List<AiTextInsight> {

        delay(350)

        return MockAi.getGestorHomeInsights(configuration)

    }



    override suspend fun getIdeaAnalysis(ideaId: String): List<AiTextInsight> {

        delay(450)

        return MockAi.getIdeaAnalysis(configuration, ideaId)

    }



    override suspend fun getScoreBreakdown(ideaId: String): List<AiScoreBreakdownItem> {

        delay(300)

        return MockAi.getScoreBreakdown(configuration, ideaId)

    }



    override suspend fun getIdeaTimeline(ideaId: String): List<AiTimelineEvent> {

        delay(250)

        return MockAi.getIdeaTimeline(configuration, ideaId)

    }



    override suspend fun getAnalyzeBrief(ideaId: String): AiAnalyzeBrief {

        delay(400)

        return MockAi.getAnalyzeBrief(configuration, ideaId)

    }



    override suspend fun getOrientationAssist(): AiOrientationAssist {

        delay(500)

        return MockAi.getOrientationAssist(configuration)

    }



    override suspend fun getDashboardMonthInsight(month: Int): AiDashboardMonthInsight {

        delay(200)

        return MockAi.getDashboardMonthInsight(configuration, month)

    }



    override suspend fun getAnalyticsBundle(): AiAnalyticsBundle {

        delay(500)

        return MockAi.getAnalyticsBundle(configuration)

    }

}

