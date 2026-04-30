package ua.anadea.data.token

import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.anadea.domain.data.token.AuthState
import ua.anadea.domain.repository.AuthRepository
import ua.anadea.domain.repository.TokenRepository

class TokenManager(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated)
    val authState: StateFlow<AuthState> = _authState

    fun getBearerToken(): BearerTokens? {
        return tokenRepository.getBearerToken()?.let {
            BearerTokens(it,null)
        }
    }

    suspend fun refreshToken(): BearerTokens?{
        val authResponse = try {
            authRepository.refresh()
        }catch (e: Exception){
            _authState.value = AuthState.LoggedOut
            return null
        }

        val accessToken = authResponse.accessToken ?: run {
            _authState.value = AuthState.LoggedOut
            return null
        }

        tokenRepository.saveBearerToken(accessToken)
        _authState.value = AuthState.Authenticated

        return BearerTokens(accessToken,null)
    }

    fun authentificate(){
        _authState.value = AuthState.Authenticated
    }
}