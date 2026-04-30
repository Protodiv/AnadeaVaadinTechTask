package ua.anadea.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module
import ua.anadea.app.screens.auth.login.LoginViewModel
import ua.anadea.app.screens.auth.register.RegisterViewModel
import ua.anadea.app.screens.main.dashboard.DashboardViewModel
import ua.anadea.app.screens.main.user.UserViewModel

val viewModelModule = module {
    factory { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    factory { LoginViewModel(get(), get(), get()) }
    factory { RegisterViewModel(get(), get(), get()) }
    factory { DashboardViewModel(get(), get(), get()) }
    factory { UserViewModel(get(), get(), get()) }
}
