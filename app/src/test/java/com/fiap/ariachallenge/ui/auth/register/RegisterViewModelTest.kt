package com.fiap.ariachallenge.ui.auth.register

import com.fiap.ariachallenge.MainDispatcherRule
import com.fiap.ariachallenge.data.mock.MockUsers
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

private class RegisterAuthStub(
    private val registerResult: Result<User>,
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<User> =
        Result.failure(UnsupportedOperationException())

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
    ): Result<User> = registerResult

    override suspend fun logout() = Unit
    override suspend fun getCurrentUser(): User? = null
    override suspend fun recoverPassword(email: String): Result<Unit> = Result.success(Unit)
}

class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun register_blankName_setsErrName() = runTest {
        val vm = RegisterViewModel(RegisterAuthStub(Result.failure(Exception("should not call"))))
        vm.onNameChange("")
        vm.onEmailChange("novo@aria.com")
        vm.onPasswordChange("aria123")
        vm.onConfirmPasswordChange("aria123")
        vm.register()
        assertEquals("ERR_NAME", vm.uiState.value.error)
    }

    @Test
    fun register_invalidEmail_setsErrEmail() = runTest {
        val vm = RegisterViewModel(RegisterAuthStub(Result.failure(Exception("should not call"))))
        vm.onNameChange("Novo Operador")
        vm.onEmailChange("invalid")
        vm.onPasswordChange("aria123")
        vm.onConfirmPasswordChange("aria123")
        vm.register()
        assertEquals("ERR_EMAIL", vm.uiState.value.error)
    }

    @Test
    fun register_passwordMismatch_setsErrMismatch() = runTest {
        val vm = RegisterViewModel(RegisterAuthStub(Result.failure(Exception("should not call"))))
        vm.onNameChange("Novo Operador")
        vm.onEmailChange("novo@aria.com")
        vm.onPasswordChange("aria123")
        vm.onConfirmPasswordChange("other")
        vm.register()
        assertEquals("ERR_MISMATCH", vm.uiState.value.error)
    }

    @Test
    fun register_success_setsOperadorRole() = runTest {
        val user = MockUsers.operador1.copy(email = "novo@aria.com")
        val vm = RegisterViewModel(RegisterAuthStub(Result.success(user)))
        vm.onNameChange(user.name)
        vm.onEmailChange(user.email)
        vm.onPasswordChange("aria123")
        vm.onConfirmPasswordChange("aria123")
        vm.register()
        assertNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(UserRole.OPERADOR, vm.uiState.value.registeredUserRole)
    }
}
