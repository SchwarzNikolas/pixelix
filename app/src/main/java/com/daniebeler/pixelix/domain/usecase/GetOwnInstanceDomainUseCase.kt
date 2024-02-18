package com.daniebeler.pixelix.domain.usecase

import com.daniebeler.pixelix.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetOwnInstanceDomainUseCase(
    private val repository: StorageRepository
) {
    operator fun invoke(): Flow<String> = flow {
        repository.getBaseUrlFromStorage().collect { url ->
            val res = url.substringAfter("https://")
            emit(res)
        }
    }
}