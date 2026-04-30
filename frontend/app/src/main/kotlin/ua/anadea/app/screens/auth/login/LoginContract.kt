package ua.anadea.app.screens.auth.login

class LoginContract {
    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null
    )

    sealed class Event {
        data class EmailChanged(val email: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        object Login : Event()
        object BackButtonClicked : Event()
    }

    sealed class Effect {
        sealed class AuthResult : Effect() {
            data class Success(val message: String) : AuthResult()
            data class Error(val message: String) : AuthResult()
        }
        sealed class Navigation : Effect() {
            object Back : Navigation()
            object ToDashboard : Navigation()
        }
        sealed class ValidationError : Effect() {
            data class EmailError(val message: String) : ValidationError()
            data class PasswordError(val message: String) : ValidationError()
        }
    }
}
