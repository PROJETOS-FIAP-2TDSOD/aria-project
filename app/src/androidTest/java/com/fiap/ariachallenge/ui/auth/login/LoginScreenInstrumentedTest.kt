package com.fiap.ariachallenge.ui.auth.login

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.data.mock.MockUsers
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import com.fiap.ariachallenge.ui.test.AriaTestTags
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme

private class InstrumentedAuthStub(
    private val result: Result<User>,
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<User> = result
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

class LoginScreenInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginViewModel_blankEmail_showsLocalizedError() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = ctx.getString(R.string.auth_error_email_required)

        composeRule.setContent {
            AriaChallengeTheme {
                val vm = remember {
                    LoginViewModel(
                        InstrumentedAuthStub(
                            Result.failure(Exception("login must not be called")),
                        ),
                    )
                }
                val state by vm.uiState.collectAsState()
                LoginContent(
                    email = state.email,
                    password = state.password,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = vm::onEmailChange,
                    onPasswordChange = vm::onPasswordChange,
                    onLogin = vm::login,
                    onNavigateToRecover = {},
                    onNavigateToRegister = {},
                )
            }
        }

        composeRule.onNodeWithTag(AriaTestTags.LoginPassword).performTextInput("x")
        composeRule.onNodeWithTag(AriaTestTags.LoginSubmit).performClick()
        composeRule.onNodeWithText(expected).assertIsDisplayed()
    }

    @Test
    fun loginFields_haveTagsForTesting() {
        composeRule.setContent {
            AriaChallengeTheme {
                LoginContent(
                    email = "",
                    password = "",
                    isLoading = false,
                    error = null,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLogin = {},
                    onNavigateToRecover = {},
                    onNavigateToRegister = {},
                )
            }
        }
        composeRule.onNodeWithTag(AriaTestTags.LoginEmail).assertIsDisplayed()
        composeRule.onNodeWithTag(AriaTestTags.LoginPassword).assertIsDisplayed()
        composeRule.onNodeWithTag(AriaTestTags.LoginSubmit).assertIsDisplayed()
    }

    @Test
    fun loginViewModel_success_updatesLoginSuccess() {
        val observed = arrayOfNulls<UserRole>(1)
        composeRule.setContent {
            AriaChallengeTheme {
                val vm = remember {
                    LoginViewModel(
                        InstrumentedAuthStub(Result.success(MockUsers.currentOperador)),
                    )
                }
                val state by vm.uiState.collectAsState()
                LaunchedEffect(state.loginSuccess) {
                    val role = state.loginSuccess
                    if (role != null) {
                        observed[0] = role
                    }
                }
                LoginContent(
                    email = state.email,
                    password = state.password,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = vm::onEmailChange,
                    onPasswordChange = vm::onPasswordChange,
                    onLogin = vm::login,
                    onNavigateToRecover = {},
                    onNavigateToRegister = {},
                )
            }
        }

        composeRule.onNodeWithTag(AriaTestTags.LoginEmail).performTextInput("operador@test.com")
        composeRule.onNodeWithTag(AriaTestTags.LoginPassword).performTextInput("aria123")
        composeRule.onNodeWithTag(AriaTestTags.LoginSubmit).performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) { observed[0] != null }
        assertEquals(UserRole.OPERADOR, observed[0])
    }
}
