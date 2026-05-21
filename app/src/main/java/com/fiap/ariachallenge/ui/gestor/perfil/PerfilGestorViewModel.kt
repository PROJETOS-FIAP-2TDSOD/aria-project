package com.fiap.ariachallenge.ui.gestor.perfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import javax.inject.Inject

data class PerfilGestorUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0
)

@HiltViewModel
class PerfilGestorViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val projectRepository: IProjectRepository,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilGestorUiState())
    val uiState: StateFlow<PerfilGestorUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        load()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update { state ->
                    if (state.user == user) state else state.copy(user = user)
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            val projects = projectRepository.getAllProjects().first()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = user,
                    totalProjects = projects.size,
                    activeProjects = projects.count { p -> p.status != ProjectStatus.CONCLUIDO && p.status != ProjectStatus.CANCELADO },
                    completedProjects = projects.count { p -> p.status == ProjectStatus.CONCLUIDO }
                )
            }
        }
    }

    fun onAvatarPicked(uri: Uri) {
        viewModelScope.launch {
            userRepository.updateAvatarFromContentUri(uri.toString())
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
