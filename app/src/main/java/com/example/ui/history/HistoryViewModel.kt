package com.example.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationEntity
import com.example.data.repository.CalculationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val items: List<CalculationEntity> = emptyList(),
    val showClearDialog: Boolean = false
)

class HistoryViewModel(
    private val repository: CalculationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _showClearDialog = MutableStateFlow(false)
    val showClearDialog: StateFlow<Boolean> = _showClearDialog.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HistoryUiState> = combine(
        _searchQuery,
        _showOnlyFavorites,
        _showClearDialog
    ) { query, onlyFavs, showDialog ->
        Triple(query, onlyFavs, showDialog)
    }.flatMapLatest { (query, onlyFavs, showDialog) ->
        val sourceFlow = if (query.isNotBlank()) {
            repository.searchCalculations(query)
        } else if (onlyFavs) {
            repository.favoriteCalculations
        } else {
            repository.allCalculations
        }

        combine(sourceFlow) { itemsArray ->
            val all = itemsArray[0]
            val filtered = if (onlyFavs && query.isNotBlank()) {
                all.filter { it.isFavorite }
            } else {
                all
            }
            HistoryUiState(
                searchQuery = query,
                showOnlyFavorites = onlyFavs,
                items = filtered,
                showClearDialog = showDialog
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun setShowClearDialog(show: Boolean) {
        _showClearDialog.value = show
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentFav)
        }
    }

    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllCalculations()
            _showClearDialog.value = false
        }
    }
}
