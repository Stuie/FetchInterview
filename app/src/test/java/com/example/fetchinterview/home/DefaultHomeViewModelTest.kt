package com.example.fetchinterview.home

import com.example.data.ItemRepository
import com.example.fetchinterview.NetworkStatusTracker
import com.example.models.Item
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultHomeViewModelTest {

    private val repository: ItemRepository = mockk()
    private val networkStatusTracker: NetworkStatusTracker = mockk()
    private val networkStatusFlow = MutableStateFlow(false)

    private val testItem = Item(1, 1, "Test item")

    @BeforeEach
    fun setup() {
        coEvery { networkStatusTracker.networkStatus } returns networkStatusFlow
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given no network and no data, emits NoNetworkAndNoData after Loading`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        coEvery { repository.getStoredItems() } returns emptyList()
        coEvery { repository.refreshItems() } returns Result.failure(Exception("No network"))

        val viewModel = DefaultHomeViewModel(repository, networkStatusTracker)

        networkStatusFlow.value = false

        advanceUntilIdle()

        assertEquals(UiState.NoNetworkAndNoData, viewModel.uiState.value)
    }

    @Test
    fun `given network with data, emits Loaded after Loading`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val items = listOf(testItem)

        coEvery { repository.getStoredItems() } returns items
        coEvery { repository.refreshItems() } returns Result.success(Unit)

        val viewModel = DefaultHomeViewModel(repository, networkStatusTracker)

        networkStatusFlow.value = true

        advanceUntilIdle()

        assertEquals(UiState.Loaded(items), viewModel.uiState.value)
    }

    @Test
    fun `given no network with data, emits NoNetworkWithData after Loading`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val items = listOf(testItem)

        coEvery { repository.getStoredItems() } returns items
        coEvery { repository.refreshItems() } returns Result.failure(Exception("No network"))

        val viewModel = DefaultHomeViewModel(repository, networkStatusTracker)

        networkStatusFlow.value = false

        advanceUntilIdle()

        assertEquals(UiState.NoNetworkWithData(items), viewModel.uiState.value)
    }

    @Test
    fun `given network with refresh error, emits Error state after Loading`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val errorMessage = "Failed to refresh items"

        coEvery { repository.getStoredItems() } returns emptyList()
        coEvery { repository.refreshItems() } returns Result.failure(Exception(errorMessage))

        val viewModel = DefaultHomeViewModel(repository, networkStatusTracker)

        networkStatusFlow.value = true

        advanceUntilIdle()

        assertEquals(UiState.Error(errorMessage), viewModel.uiState.value)
    }
}
