package com.fiap.ariachallenge.ui.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registeredUserRole: UserRole? = null,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, error = null) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, error = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, error = null) }
    fun onConfirmPasswordChange(v: String) = _uiState.update { it.copy(confirmPassword = v, error = null) }

    fun register() {
        val s = _uiState.value
        if (s.name.isBlank()) {
            _uiState.update { it.copy(error = "ERR_NAME") }
            return
        }
        if (s.email.isBlank() || !s.email.contains("@")) {
            _uiState.update { it.copy(error = "ERR_EMAIL") }
            return
        }
        if (s.password.length < 4) {
            _uiState.update { it.copy(error = "ERR_PASSWORD_SHORT") }
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.update { it.copy(error = "ERR_MISMATCH") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.register(
                name = s.name,
                email = s.email,
                password = s.password,
                role = UserRole.OPERADOR,
            ).fold(
                onSuccess = { user: User ->
                    _uiState.update {
                        it.copy(isLoading = false, registeredUserRole = user.role)
                    }
                },
                onFailure = { e ->
                    val code = when (e.message) {
                        "ERR_EMAIL_EXISTS" -> "ERR_EMAIL_EXISTS"
                        "ERR_INVALID" -> "ERR_INVALID"
                        else -> e.message ?: "ERR_UNKNOWN"
                    }
                    _uiState.update { it.copy(isLoading = false, error = code) }
                },
            )
        }
    }
}
