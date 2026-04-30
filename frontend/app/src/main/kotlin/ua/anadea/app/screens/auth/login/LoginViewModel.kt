package ua.anadea.app.screens.auth.login

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ua.anadea.data.token.TokenManager
import ua.anadea.domain.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val viewModelScope: CoroutineScope,
    private val tokenManager: TokenManager,
) {
    private val _state = MutableStateFlow(LoginContract.State())
    var state = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private fun setEffect(effect: LoginContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun onEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email, emailError = null)
            }
            is LoginContract.Event.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password, passwordError = null)
            }
            is LoginContract.Event.Login -> {
                if (validateInputs(state.value.email, state.value.password)) {
                    login(state.value.email, state.value.password)
                }
            }
            is LoginContract.Event.BackButtonClicked ->
                setEffect(LoginContract.Effect.Navigation.Back)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        if (email.isBlank() || !email.contains("@")) {
            _state.value = _state.value.copy(emailError = "Invalid email address")
            setEffect(LoginContract.Effect.ValidationError.EmailError("Invalid email address"))
            isValid = false
        }
        if (password.length < 4) {
            _state.value = _state.value.copy(passwordError = "Password must be at least 4 characters")
            setEffect(LoginContract.Effect.ValidationError.PasswordError("Password must be at least 4 characters"))
            isValid = false
        }
        return isValid
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val authResult = authRepository.login(
                    email = email,
                    password = password
                )

                authResult.message?.let {
                    setEffect(LoginContract.Effect.AuthResult.Success(it))
                }
                tokenManager.authentificate()
                delay(1000)
                setEffect(LoginContract.Effect.Navigation.ToDashboard)
            } catch (e: Exception) {
                setEffect(LoginContract.Effect.AuthResult.Error(e.message ?: "An unknown error occurred"))
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
