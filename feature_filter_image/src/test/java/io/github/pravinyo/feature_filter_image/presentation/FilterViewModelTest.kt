package io.github.pravinyo.feature_filter_image.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import io.github.pravinyo.common.data.OperationState
import io.github.pravinyo.common.editor.EditorUI
import io.github.pravinyo.feature_filter_image.TestCoroutineRule
import io.github.pravinyo.feature_filter_image.dependencyInjection.DaggerViewModelTestComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.rules.TestRule
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import javax.inject.Inject

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilterViewModelTest{

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val testCoroutineRule2 = TestCoroutineRule()

    @Inject lateinit var fakeEditorUI: EditorUI

    @BeforeAll
    fun daggerSetup() {
        MockitoAnnotations.initMocks(this)
        DaggerViewModelTestComponent.builder()
                .build().inject(this)
    }

    @Nested
    @DisplayName("Given Editor is available")
    inner class EditorUITest {
        lateinit var filterViewModel: FilterViewModel
        lateinit var spyEditor: EditorUI

        @BeforeEach
        fun setup() {
            spyEditor = spy(fakeEditorUI)
            filterViewModel = FilterViewModel { spyEditor }
        }

        @Test
        fun `check observer is returned` () {
            val res = filterViewModel.state()
            assertEquals(OperationState.Success, res.value)
        }

        @Test
        fun `when filter called, execute editor filter Command` () {
            testCoroutineRule.runBlockingTest {
                val filterMode = OperationType.NegativeFilter
                filterViewModel.filter(filterMode)

                verify(spyEditor)
                        .execute(any())
            }
        }

        @Test
        fun `when contrast called, execute editor contrast Command` () {
            testCoroutineRule2.runBlockingTest {
                filterViewModel.negativeFilter()
                verify(spyEditor).execute(any())
            }
        }
    }
}