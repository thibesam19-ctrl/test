package com.axiom.aicoach.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncEpoch: Long = 0L,
    val pendingOperations: Int = 0,
    val lastError: String? = null,
)

@Singleton
class SyncCoordinator @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
) {
    private val _syncState = MutableStateFlow(SyncState())
    val syncState: Flow<SyncState> = _syncState.asStateFlow()

    fun recordPendingOperation() {
        _syncState.value = _syncState.value.copy(
            pendingOperations = _syncState.value.pendingOperations + 1
        )
    }

    fun recordSyncSuccess() {
        _syncState.value = SyncState(
            isSyncing = false,
            lastSyncEpoch = System.currentTimeMillis(),
            pendingOperations = 0,
        )
    }

    fun recordSyncError(error: String) {
        _syncState.value = _syncState.value.copy(isSyncing = false, lastError = error)
    }
}
