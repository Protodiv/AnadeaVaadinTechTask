package ua.anadea.app.di

import org.koin.dsl.module

val appModule = module {
    includes(coroutineModule)
    includes(networkModule)
    includes(repositoryModule)
    includes(viewModelModule)
}