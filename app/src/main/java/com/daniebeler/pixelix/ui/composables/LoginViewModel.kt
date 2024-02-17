package com.daniebeler.pixelix.ui.composables

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.daniebeler.pixelix.domain.model.Application
import com.daniebeler.pixelix.domain.repository.CountryRepository
import com.daniebeler.pixelix.domain.repository.StorageRepository
import com.daniebeler.pixelix.utils.Navigate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: CountryRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val domainRegex: Regex = "^(https:\\/\\/www\\.|https:\\/\\/)?[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})(\\.[a-zA-Z0-9]{2,})?$".toRegex()

    private var _authApplication: Application? = null
    var customUrl: String by mutableStateOf("")
    var isValidUrl: Boolean by mutableStateOf(false)

    var loading: Boolean by mutableStateOf(false)


    private suspend fun registerApplication(): String {
        _authApplication = repository.createApplication()
        if (_authApplication != null) {
            storageRepository.storeClientId(_authApplication!!.clientId)
            storageRepository.storeClientSecret(_authApplication!!.clientSecret)
            return _authApplication!!.clientId
        }
        return ""
    }

    private suspend fun setBaseUrl(_baseUrl: String): String {
        var baseUrl = _baseUrl
        if (!baseUrl.startsWith("https://")) {
            baseUrl = "https://$baseUrl"
        }
        repository.storeBaseUrl(baseUrl)
        return baseUrl
    }

    fun domainChanged() {
        isValidUrl = domainRegex.matches(customUrl)
    }

    suspend fun login(baseUrl: String, context: Context) {
        loading = true
        if (domainRegex.matches(baseUrl)) {
            val newBaseUrl = setBaseUrl(baseUrl)
            val clientId = registerApplication()

            if (clientId.isNotEmpty()) {
                openUrl(context, clientId, newBaseUrl)
            }
        }
        loading = false
    }

    private fun openUrl(context: Context, clientId: String, baseUrl: String) {
        val url =
            "${baseUrl}/oauth/authorize?response_type=code&redirect_uri=pixelix-android-auth://callback&client_id=" + clientId
        Navigate().openUrlInApp(context, url)
    }
}