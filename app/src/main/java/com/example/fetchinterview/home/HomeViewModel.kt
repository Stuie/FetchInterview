package com.example.fetchinterview.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ItemRepository
import com.example.fetchinterview.NetworkStatusTracker
import com.example.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    data object Loading : UiState()
    data object NoNetworkAndNoData : UiState()
    data class NoNetworkWithData(val items: List<Item>) : UiState()
    data class Loaded(val items: List<Item>) : UiState()
    data class Error(val message: String) : UiState()
}

interface HomeViewModel {
    val uiState: StateFlow<UiState>
    fun refreshItems()
}

@HiltViewModel
class DefaultHomeViewModel @Inject constructor(
    private val repository: ItemRepository,
    networkStatusTracker: NetworkStatusTracker,
) : HomeViewModel, ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    override val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            networkStatusTracker.networkStatus.collect { isConnected ->
                if (isDatabaseEmpty()) {
                    _uiState.value = if (!isConnected) {
                        // Full-screen message if there's no network and no data
                        UiState.NoNetworkAndNoData
                    } else {
                        UiState.Loading
                    }

                    if (isConnected) refreshItems()
                } else {
                    _uiState.value = if (isConnected) UiState.Loaded(repository.getStoredItems())
                    else UiState.NoNetworkWithData(repository.getStoredItems())
                }
            }
        }
    }

    override fun refreshItems() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.refreshItems()
            if (result.isSuccess) {
                val items = repository.getStoredItems()
                _uiState.value = UiState.Loaded(items)
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    // Helper method to check if the database has data
    private suspend fun isDatabaseEmpty(): Boolean {
        return repository.getStoredItems().isEmpty()
    }
}
