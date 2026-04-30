package ua.anadea.domain.data.token

sealed class AuthState {
    object Authenticated : AuthState()
    object LoggedOut : AuthState()
}