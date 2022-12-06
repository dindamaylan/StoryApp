package com.dindamaylan.storyapp.ui.others

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dindamaylan.storyapp.data.repository.AuthUser
import com.dindamaylan.storyapp.utils.CoroutinesRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class OthersViewModelTest {
    @get:Rule
    var coroutinesRule = CoroutinesRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authUser: AuthUser
    private lateinit var othersViewModel: OthersViewModel
    private val dummyToken = ""

    @Before
    fun setUp() {
        othersViewModel = OthersViewModel(authUser)
    }

    @Test
    fun `when logout, make sure call authUser to call saveAuthToken and make auth is empty`(): Unit = runTest {
        `when` (authUser.saveAuthToken(dummyToken)).thenReturn(Unit)
        othersViewModel.authToken(dummyToken)
        Mockito.verify(authUser).saveAuthToken(dummyToken)
    }
}