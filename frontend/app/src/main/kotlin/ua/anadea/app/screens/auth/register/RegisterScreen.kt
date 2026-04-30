package ua.anadea.app.screens.auth.register

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.anadea.app.screens.NavigationRoute.AuthRoute.Dashboard
import ua.anadea.domain.data.UserRole

@Route("register")
class RegisterScreen : KComposite(), KoinComponent {

    private val viewModel: RegisterViewModel by inject()
    private val screenScope: CoroutineScope by inject()

    private var effectJob: Job? = null
    private var stateJob: Job? = null

    private lateinit var nameField: TextField
    private lateinit var emailField: EmailField
    private lateinit var passwordField: PasswordField
    private lateinit var roleComboBox: ComboBox<UserRole>
    private lateinit var registerButton: Button
    private lateinit var backButton: Button
    private lateinit var testUserButton: Button

    private val root = ui {
        verticalLayout {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER

            verticalLayout {
                width = "400px"
                alignItems = FlexComponent.Alignment.STRETCH

                h1("Register") {
                    style.set("text-align", "center")
                }

                nameField = textField("Name") {
                    placeholder = "Enter your name"
                    valueChangeMode = ValueChangeMode.LAZY
                    valueChangeTimeout = 1000
                    addValueChangeListener { event ->
                        viewModel.onEvent(RegisterContract.Event.NameChanged(event.value))
                    }
                }

                emailField = emailField("Email") {
                    placeholder = "Enter your email"
                    valueChangeMode = ValueChangeMode.LAZY
                    valueChangeTimeout = 1000
                    addValueChangeListener { event ->
                        viewModel.onEvent(RegisterContract.Event.EmailChanged(event.value))
                    }
                }

                passwordField = passwordField("Password") {
                    placeholder = "Enter your password"
                    valueChangeMode = ValueChangeMode.LAZY
                    valueChangeTimeout = 1000
                    addValueChangeListener { event ->
                        viewModel.onEvent(RegisterContract.Event.PasswordChanged(event.value))
                    }
                }

                roleComboBox = comboBox("Role") {
                    setItems(*UserRole.entries.toTypedArray())
                    value = UserRole.USER
                    addValueChangeListener { event ->
                        event.value?.let { viewModel.onEvent(RegisterContract.Event.RoleChanged(it)) }
                    }
                }

                testUserButton = button("Test User Credentials") {
                    onLeftClick {
                        nameField.value = "TestName"
                        emailField.value = "test@gmail.com"
                        passwordField.value = "passtest"
                        roleComboBox.value = UserRole.USER
                        viewModel.onEvent(RegisterContract.Event.NameChanged("TestName"))
                        viewModel.onEvent(RegisterContract.Event.EmailChanged("test@gmail.com"))
                        viewModel.onEvent(RegisterContract.Event.PasswordChanged("passtest"))
                        viewModel.onEvent(RegisterContract.Event.RoleChanged(UserRole.USER))
                    }
                }

                registerButton = button("Register") {
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    onLeftClick {
                        viewModel.onEvent(RegisterContract.Event.Register)
                    }
                }

                backButton = button("Back") {
                    onLeftClick {
                        viewModel.onEvent(RegisterContract.Event.BackButtonClicked)
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
                        is RegisterContract.Effect.Navigation.Back -> ui.navigate("")
                        is RegisterContract.Effect.Navigation.ToDashboard -> ui.navigate(Dashboard.route)
                        is RegisterContract.Effect.AuthResult.Success -> Notification.show(effect.message)
                        is RegisterContract.Effect.AuthResult.Error -> Notification.show(effect.message)
                        is RegisterContract.Effect.ValidationError.NameError -> {
                            nameField.isInvalid = true
                            nameField.errorMessage = effect.message
                        }
                        is RegisterContract.Effect.ValidationError.EmailError -> {
                            emailField.isInvalid = true
                            emailField.errorMessage = effect.message
                        }
                        is RegisterContract.Effect.ValidationError.PasswordError -> {
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
                    
                    nameField.isEnabled = !loading
                    emailField.isEnabled = !loading
                    passwordField.isEnabled = !loading
                    roleComboBox.isEnabled = !loading
                    registerButton.isEnabled = !loading
                    backButton.isEnabled = !loading
                    testUserButton.isEnabled = !loading
                    
                    if (loading) {
                        registerButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST)
                    } else {
                        registerButton.removeThemeVariants(ButtonVariant.LUMO_CONTRAST)
                        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    }

                    nameField.isInvalid = state.nameError != null
                    nameField.errorMessage = state.nameError
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
