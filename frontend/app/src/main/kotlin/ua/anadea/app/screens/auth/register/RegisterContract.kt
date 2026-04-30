package ua.anadea.app.screens.auth.register

import ua.anadea.domain.data.UserRole

class RegisterContract {
    data class State(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val role: UserRole = UserRole.USER,
        val isLoading: Boolean = false,
        val nameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null
    )

    sealed class Event {
        data class NameChanged(val name: String) : Event()
        data class EmailChanged(val email: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class RoleChanged(val role: UserRole) : Event()
        object Register : Event()
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
            data class NameError(val message: String) : ValidationError()
            data class EmailError(val message: String) : ValidationError()
            data class PasswordError(val message: String) : ValidationError()
        }
    }
}
