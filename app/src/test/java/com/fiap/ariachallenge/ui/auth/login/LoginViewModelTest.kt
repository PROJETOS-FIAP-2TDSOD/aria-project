package com.fiap.ariachallenge.ui.auth.login

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import com.fiap.ariachallenge.MainDispatcherRule
import com.fiap.ariachallenge.data.mock.MockUsers
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository

private class LoginAuthStub(
    private val loginResult: Result<User>,
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<User> = loginResult
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
    ): Result<User> = Result.failure(UnsupportedOperationException())
    override suspend fun logout() = Unit
    override suspend fun getCurrentUser(): User? = null
    override suspend fun recoverPassword(email: String): Result<Unit> = Result.success(Unit)
}

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun login_blankEmail_setsErrEmail() = runTest {
        val vm = LoginViewModel(LoginAuthStub(Result.failure(Exception("should not call"))))
        vm.onEmailChange("")
        vm.onPasswordChange("secret")
        vm.login()
        assertEquals("ERR_EMAIL", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun login_blankPassword_setsErrPassword() = runTest {
        val vm = LoginViewModel(LoginAuthStub(Result.failure(Exception("should not call"))))
        vm.onEmailChange("a@b.com")
        vm.onPasswordChange("   ")
        vm.login()
        assertEquals("ERR_PASSWORD", vm.uiState.value.error)
    }

    @Test
    fun login_success_setsRole() = runTest {
        val user = MockUsers.currentGestor
        val vm = LoginViewModel(LoginAuthStub(Result.success(user)))
        vm.onEmailChange(user.email)
        vm.onPasswordChange("aria123")
        vm.login()
        assertNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(UserRole.GESTOR, vm.uiState.value.loginSuccess)
    }

    @Test
    fun login_failure_setsMessage() = runTest {
        val vm =
            LoginViewModel(LoginAuthStub(Result.failure(Exception("ERR_INVALID_CREDENTIALS"))))
        vm.onEmailChange("x@y.com")
        vm.onPasswordChange("wrong")
        vm.login()
        assertEquals("ERR_INVALID_CREDENTIALS", vm.uiState.value.error)
        assertTrue(vm.uiState.value.loginSuccess == null)
    }
}
