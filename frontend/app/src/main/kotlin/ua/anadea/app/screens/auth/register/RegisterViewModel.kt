package ua.anadea.app.screens.auth.register

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.anadea.data.token.TokenManager
import ua.anadea.domain.data.UserRole
import ua.anadea.domain.repository.AuthRepository

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val viewModelScope: CoroutineScope,
    private val tokenManager: TokenManager,
) {
    private val _state = MutableStateFlow(RegisterContract.State())
    var state = _state.asStateFlow()

    private val _effect = Channel<RegisterContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private fun setEffect(effect: RegisterContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun onEvent(event: RegisterContract.Event) {
        when (event) {
            is RegisterContract.Event.NameChanged -> {
                _state.value = _state.value.copy(name = event.name, nameError = null)
            }
            is RegisterContract.Event.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email, emailError = null)
            }
            is RegisterContract.Event.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password, passwordError = null)
            }
            is RegisterContract.Event.RoleChanged -> {
                _state.value = _state.value.copy(role = event.role)
            }
            is RegisterContract.Event.Register -> {
                with(state.value){
                    if (validateInputs(name, email, password)) {
                        register(name, email, password, role)
                    }
                }
            }
            is RegisterContract.Event.BackButtonClicked ->
                setEffect(RegisterContract.Effect.Navigation.Back)
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        var isValid = true
        if (name.isBlank()) {
            _state.value = _state.value.copy(nameError = "Name is required")
            setEffect(RegisterContract.Effect.ValidationError.NameError("Name is required"))
            isValid = false
        }
        if (email.isBlank() || !email.contains("@")) {
            _state.value = _state.value.copy(emailError = "Invalid email address")
            setEffect(RegisterContract.Effect.ValidationError.EmailError("Invalid email address"))
            isValid = false
        }
        if (password.length < 4) {
            _state.value = _state.value.copy(passwordError = "Password must be at least 4 characters")
            setEffect(RegisterContract.Effect.ValidationError.PasswordError("Password must be at least 4 characters"))
            isValid = false
        }
        return isValid
    }

    private fun register(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val authResult = authRepository.register(
                    name = name,
                    email = email,
                    password = password,
                    role = role
                )

                authResult.message?.let {
                    setEffect(RegisterContract.Effect.AuthResult.Success(it))
                }
                tokenManager.authentificate()
                delay(1000)
                setEffect(RegisterContract.Effect.Navigation.ToDashboard)
            } catch (e: Exception) {
                setEffect(RegisterContract.Effect.AuthResult.Error(e.message ?: "An unknown error occurred"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
