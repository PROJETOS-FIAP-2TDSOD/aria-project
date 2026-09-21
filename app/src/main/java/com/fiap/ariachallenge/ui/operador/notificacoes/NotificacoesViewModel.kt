package com.fiap.ariachallenge.ui.operador.notificacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.repository.IUserRepository
import javax.inject.Inject

data class NotificacoesUiState(
    val isLoading: Boolean = true,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificacoesViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificacoesUiState())
    val uiState: StateFlow<NotificacoesUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first()
                val notifications = userRepository.getNotifications(user.id).first()
                _uiState.update { it.copy(isLoading = false, notifications = notifications) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun markRead(notificationId: String) {
        viewModelScope.launch {
            userRepository.markNotificationRead(notificationId)
            load()
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            userRepository.markAllNotificationsRead(user.id)
            load()
        }
    }
}