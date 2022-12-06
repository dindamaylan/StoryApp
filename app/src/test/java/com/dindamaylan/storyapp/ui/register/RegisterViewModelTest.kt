package com.dindamaylan.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dindamaylan.storyapp.data.remote.retrofit.response.ResponseRegister
import com.dindamaylan.storyapp.data.repository.AuthUser
import com.dindamaylan.storyapp.utils.CoroutinesRule
import com.dindamaylan.storyapp.utils.HelperDummy
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
class RegisterViewModelTest{
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authUser: AuthUser
    private lateinit var registerViewModel: RegisterViewModel
    private val dummy = HelperDummy.createUserDummy()
    private val dummyName = "dinda"
    private val dummyEmail = "dina@mail.com"
    private val dummyPassword = "admin123"

    @Before
    fun setUp(){
        registerViewModel = RegisterViewModel(authUser)
    }

    @Test
    fun `When Failed, make sure result not null and return error with exception`(): Unit = runTest {
        val expected: Flow<Result<ResponseRegister>> = flowOf(
            Result.failure(Exception("register failed"))
        )

        `when` (authUser.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = registerViewModel.register(dummyName, dummyEmail, dummyPassword).first()
        Mockito.verify(authUser).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.first(), actual)
    }

    @Test
    fun `when success, make sure result not null and return success`(): Unit = runTest {
        val expected = flow {
            emit(Result.success(dummy))
        }

        `when`(authUser.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        val actual = registerViewModel.register(dummyName, dummyEmail, dummyPassword).first()
        Mockito.verify(authUser).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.first(), actual)
    }

}