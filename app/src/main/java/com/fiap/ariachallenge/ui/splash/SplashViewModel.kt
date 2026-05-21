package com.fiap.ariachallenge.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import javax.inject.Inject

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NavigateToLogin : SplashUiState()
    data class NavigateToHome(val role: UserRole) : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            delay(1800)
            val user = authRepository.getCurrentUser()
            _uiState.value = if (user != null) {
                SplashUiState.NavigateToHome(user.role)
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }
}
