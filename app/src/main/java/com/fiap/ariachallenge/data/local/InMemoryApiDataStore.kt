package com.fiap.ariachallenge.data.local

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.fiap.ariachallenge.data.remote.dto.IdeaDto
import com.fiap.ariachallenge.data.remote.dto.OrientationDto
import com.fiap.ariachallenge.data.remote.dto.ProjectDto
import com.fiap.ariachallenge.data.remote.toDomain
import com.fiap.ariachallenge.data.remote.toDto
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.Project
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

data class InMemoryApiSnapshot(
    val ideas: List<Idea>,
    val projects: List<Project>,
    val orientations: List<Orientation>,
)

@Singleton
class InMemoryApiDataStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val gson = Gson()
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) },
    )

    suspend fun read(): InMemoryApiSnapshot? {
        val json = dataStore.data.first()[SNAPSHOT_JSON].orEmpty()
        if (json.isBlank()) return null
        return runCatching {
            val root = gson.fromJson<Map<String, String>>(json, mapType)
            val ideas = gson.fromJson<List<IdeaDto>>(root["ideas"], ideaListType).map { it.toDomain() }
            val projects = gson.fromJson<List<ProjectDto>>(root["projects"], projectListType).map { it.toDomain() }
            val orientations = gson.fromJson<List<OrientationDto>>(root["orientations"], orientationListType)
                .map { it.toDomain() }
            InMemoryApiSnapshot(ideas = ideas, projects = projects, orientations = orientations)
        }.getOrNull()
    }

    suspend fun write(snapshot: InMemoryApiSnapshot) {
        val payload = mapOf(
            "ideas" to gson.toJson(snapshot.ideas.map { it.toDto() }),
            "projects" to gson.toJson(snapshot.projects.map { it.toDto() }),
            "orientations" to gson.toJson(snapshot.orientations.map { it.toDto() }),
        )
        val json = gson.toJson(payload)
        dataStore.edit { prefs ->
            if (snapshot.ideas.isEmpty() && snapshot.projects.isEmpty() && snapshot.orientations.isEmpty()) {
                prefs.remove(SNAPSHOT_JSON)
            } else {
                prefs[SNAPSHOT_JSON] = json
            }
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "aria_api_snapshot"
        private val SNAPSHOT_JSON = stringPreferencesKey("snapshot_json")
        private val mapType = object : TypeToken<Map<String, String>>() {}.type
        private val ideaListType = object : TypeToken<List<IdeaDto>>() {}.type
        private val projectListType = object : TypeToken<List<ProjectDto>>() {}.type
        private val orientationListType = object : TypeToken<List<OrientationDto>>() {}.type
    }
}
