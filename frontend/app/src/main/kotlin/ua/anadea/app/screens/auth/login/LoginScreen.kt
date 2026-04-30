package ua.anadea.app.screens.auth.login

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.anadea.app.screens.NavigationRoute.AuthRoute.Dashboard

@Route("login")
class LoginScreen : KComposite(), KoinComponent {

    private val viewModel: LoginViewModel by inject()
    private val screenScope: CoroutineScope by inject()

    private var effectJob: Job? = null
    private var stateJob: Job? = null

    private lateinit var emailField: EmailField
    private lateinit var passwordField: PasswordField
    private lateinit var loginButton: Button
    private lateinit var backButton: Button
    private lateinit var adminButton: Button
    private lateinit var userButton: Button

    private val root = ui {
        verticalLayout {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER

            verticalLayout {
                width = "400px"
                alignItems = FlexComponent.Alignment.STRETCH

                h1("Login") {
                    style.set("text-align", "center")
                }

                emailField = emailField("Email") {
                    placeholder = "Enter your email"
                    valueChangeMode = ValueChangeMode.LAZY
                    valueChangeTimeout = 1000
                    addValueChangeListener { event ->
                        viewModel.onEvent(LoginContract.Event.EmailChanged(event.value))
                    }
                }

                passwordField = passwordField("Password") {
                    placeholder = "Enter your password"
                    valueChangeMode = ValueChangeMode.LAZY
                    valueChangeTimeout = 1000
                    addValueChangeListener { event ->
                        viewModel.onEvent(LoginContract.Event.PasswordChanged(event.value))
                    }
                }

                horizontalLayout {
                    justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
                    adminButton = button("Admin Credentials") {
                        onLeftClick {
                            emailField.value = "user1@gmail.com"
                            passwordField.value = "pass1"
                            viewModel.onEvent(LoginContract.Event.EmailChanged("user1@gmail.com"))
                            viewModel.onEvent(LoginContract.Event.PasswordChanged("pass1"))
                        }
                    }
                    userButton = button("User Credentials") {
                        onLeftClick {
                            emailField.value = "user2@gmail.com"
                            passwordField.value = "pass2"
                            viewModel.onEvent(LoginContract.Event.EmailChanged("user2@gmail.com"))
                            viewModel.onEvent(LoginContract.Event.PasswordChanged("pass2"))
                        }
                    }
                }

                loginButton = button("Login") {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick {
                        viewModel.onEvent(LoginContract.Event.Login)
                    }
                }

                backButton = button("Back") {
                    onLeftClick {
                        viewModel.onEvent(LoginContract.Event.BackButtonClicked)
                    }
                }
            }
        }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)

        val ui = UI.getCurrent()
        effectJob = viewModel.effect
            .onEach { effect ->
                ui.access {
                    when (effect) {
                        is LoginContract.Effect.Navigation.Back -> ui.navigate("")
                        is LoginContract.Effect.Navigation.ToDashboard -> ui.navigate(Dashboard.route)
                        is LoginContract.Effect.AuthResult.Success -> Notification.show(effect.message)
                        is LoginContract.Effect.AuthResult.Error -> Notification.show(effect.message)
                        is LoginContract.Effect.ValidationError.EmailError -> {
                            emailField.isInvalid = true
                            emailField.errorMessage = effect.message
                        }
                        is LoginContract.Effect.ValidationError.PasswordError -> {
                            passwordField.isInvalid = true
                            passwordField.errorMessage = effect.message
                        }
                    }
                }
            }
            .launchIn(screenScope)

        stateJob = viewModel.state
            .onEach { state ->
                ui.access {
                    val loading = state.isLoading
                    
                    emailField.isEnabled = !loading
                    passwordField.isEnabled = !loading
                    loginButton.isEnabled = !loading
                    backButton.isEnabled = !loading
                    adminButton.isEnabled = !loading
                    userButton.isEnabled = !loading
                    
                    if (loading) {
                        loginButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST)
                    } else {
                        loginButton.removeThemeVariants(ButtonVariant.LUMO_CONTRAST)
                        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    }

                    emailField.isInvalid = state.emailError != null
                    emailField.errorMessage = state.emailError
                    passwordField.isInvalid = state.passwordError != null
                    passwordField.errorMessage = state.passwordError
                }
            }
            .launchIn(screenScope)
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        super.onDetach(detachEvent)
        effectJob?.cancel()
        stateJob?.cancel()
    }
}
