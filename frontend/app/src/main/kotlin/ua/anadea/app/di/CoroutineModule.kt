package ua.anadea.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val coroutineModule = module {
    factory { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}