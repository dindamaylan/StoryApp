package com.dindamaylan.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseLogin
import com.dindamaylan.storyapp.data.repository.AuthUser
import com.dindamaylan.storyapp.utils.CoroutinesRule
import com.dindamaylan.storyapp.utils.HelperDummy
import com.dindamaylan.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authUser: AuthUser
    private lateinit var loginViewModel: LoginViewModel
    private val dummy = HelperDummy.generateUserDummy()
    private val dummyEmail = "dina@mail.com"
    private val dummyPassword = "admin123"
    private val dummyToken = "token"

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(authUser)
    }

    @Test
    fun `When Failed, make sure result not null and return error with exception`(): Unit = runTest {
        val expected: Flow<Result<ResponseLogin>> = flowOf(
            Result.failure(Exception("login failed"))
        )

        `when` (authUser.login(dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = loginViewModel.login(dummyEmail, dummyPassword).first()
        Mockito.verify(authUser).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.first(), actual)
    }

    @Test
    fun `when success, make sure result not null and return success`(): Unit = runTest {
        val expected = flow {
            emit(Result.success(dummy))
        }

        `when`(authUser.login(dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = loginViewModel.login(dummyEmail, dummyPassword).first()
        Mockito.verify(authUser).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.first(), actual)
    }

    @Test
    fun `when success login make sure auth token not empty`(): Unit = runTest {
        val expected = flow{ emit(dummyToken) }

        `when` (authUser.getTokenAuth()).thenReturn(expected)

        val actual = loginViewModel.getAuthToken.getOrAwaitValue()

        Mockito.verify(authUser).getTokenAuth()
        Assert.assertTrue(actual.isNotEmpty())

    }

    @Test
    fun `when call authToken make sure to call authUser saveAuthToken`(): Unit = runTest {
        `when` (authUser.saveAuthToken(dummyToken)).thenReturn(Unit)
        loginViewModel.authToken(dummyToken)
        Mockito.verify(authUser).saveAuthToken(dummyToken)
    }

    @Test
    fun `when success logout make sure auth token is empty`(): Unit = runTest {
        val expected = flow{ emit("") }

        `when` (authUser.getTokenAuth()).thenReturn(expected)

        val actual = loginViewModel.getAuthToken.getOrAwaitValue()

        Mockito.verify(authUser).getTokenAuth()
        Assert.assertTrue(actual.isEmpty())

    }
}