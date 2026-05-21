package com.fiap.ariachallenge.data.remote

import com.fiap.ariachallenge.data.local.InMemoryApiDataStore
import com.fiap.ariachallenge.data.local.InMemoryApiSnapshot
import com.fiap.ariachallenge.data.mock.MockIdeas
import com.fiap.ariachallenge.data.mock.MockOrientations
import com.fiap.ariachallenge.data.mock.MockProjects
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.Project
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

@Singleton
class InMemoryApiStore @Inject constructor(
    private val apiDataStore: InMemoryApiDataStore,
) {
    val ideas = MutableStateFlow(MockIdeas.allIdeas.toMutableList())
    val projects = MutableStateFlow(MockProjects.allProjects.toMutableList())
    val orientations = MutableStateFlow(MockOrientations.allOrientations.toMutableList())

    init {
        runBlocking(Dispatchers.IO) {
            apiDataStore.read()?.let { snapshot ->
                ideas.value = snapshot.ideas.toMutableList()
                projects.value = snapshot.projects.toMutableList()
                orientations.value = snapshot.orientations.toMutableList()
            }
        }
    }

    fun replaceIdeas(list: List<Idea>) {
        ideas.value = list.toMutableList()
        persistSnapshot()
    }

    fun replaceProjects(list: List<Project>) {
        projects.value = list.toMutableList()
        persistSnapshot()
    }

    fun replaceOrientations(list: List<Orientation>) {
        orientations.value = list.toMutableList()
        persistSnapshot()
    }

    fun persistSnapshot() = runBlocking(Dispatchers.IO) {
        apiDataStore.write(
            InMemoryApiSnapshot(
                ideas = ideas.value,
                projects = projects.value,
                orientations = orientations.value,
            ),
        )
    }
}
