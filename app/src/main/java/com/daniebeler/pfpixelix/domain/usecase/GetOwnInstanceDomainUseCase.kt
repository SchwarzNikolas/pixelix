package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.domain.repository.AuthRepository

class GetOwnInstanceDomainUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): String {
        val currentLoginData = repository.getCurrentLoginData()
        currentLoginData?.let {
            return repository.getCurrentLoginData()!!.baseUrl.substringAfter("https://")
        }
        return ("https://err.or")
    }
}