package com.dsm.miprofeenlinea.presentacion.chart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChartViewModel(
    private val userId: String,
    private val role: DashboardRole,
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _period = MutableStateFlow(DashboardPeriod.LAST_7_DAYS)
    val period: StateFlow<DashboardPeriod> = _period.asStateFlow()

    init {
        refresh()
    }

    fun selectPeriod(period: DashboardPeriod) {
        if (_period.value == period) return
        _period.value = period
        refresh()
    }

    fun refresh() {
        if (userId.isBlank()) {
            _uiState.value = DashboardUiState.Error("No se encontro una sesion activa.")
            return
        }

        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            runCatching {
                repository.loadDashboard(userId, role, _period.value)
            }.onSuccess { data ->
                _uiState.value = if (data.kpis.isEmpty() && data.estadoCount.isEmpty()) {
                    DashboardUiState.Empty("Aun no hay actividad para este periodo.")
                } else {
                    DashboardUiState.Content(data)
                }
            }.onFailure {
                _uiState.value = DashboardUiState.Error("No se pudo cargar el dashboard. Revisa tu conexion e intenta nuevamente.")
            }
        }
    }
}

class ChartViewModelFactory(
    private val context: Context,
    private val userId: String,
    private val role: DashboardRole
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val localDataSource = DashboardLocalDataSource(context)
        val repository = DashboardRepository(localDataSource = localDataSource)
        return ChartViewModel(userId, role, repository) as T
    }
}
