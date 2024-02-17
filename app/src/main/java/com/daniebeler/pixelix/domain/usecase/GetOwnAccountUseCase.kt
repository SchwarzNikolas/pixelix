package com.daniebeler.pixelix.domain.usecase

import com.daniebeler.pixelix.common.Resource
import com.daniebeler.pixelix.domain.model.Account
import com.daniebeler.pixelix.domain.repository.AccountRepository
import com.daniebeler.pixelix.domain.repository.CountryRepository
import com.daniebeler.pixelix.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetOwnAccountUseCase(
    private val storageRepository: StorageRepository,
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<Resource<Account>> = flow {
        emit(Resource.Loading())
        val accountId = storageRepository.getAccountId().first()
        if (accountId.isNotBlank()) {
            accountRepository.getAccount(accountId).collect { res ->
                emit(res)
            }
        }
    }
}