package com.fiap.ariachallenge.ui.auth.recover

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.fiap.ariachallenge.R
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import javax.inject.Inject

data class RecoverUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    @StringRes val errorRes: Int? = null,
    val error: String? = null,
)

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverUiState())
    val uiState: StateFlow<RecoverUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, error = null, errorRes = null) }

    fun recoverPassword() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(errorRes = R.string.recover_error_invalid_email) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.recoverPassword(email)
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) } },
                onFailure = { e ->
                    val errorRes = when (e.message) {
                        "ERR_INVALID_EMAIL" -> R.string.auth_error_invalid_email
                        else -> null
                    }
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = errorRes, error = if (errorRes == null) e.message else null)
                    }
                }
            )
        }
    }
}
