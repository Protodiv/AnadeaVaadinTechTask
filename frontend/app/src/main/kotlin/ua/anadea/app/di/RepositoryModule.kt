package ua.anadea.app.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.anadea.data.AdminRepositoryImpl
import ua.anadea.data.AuthRepositoryImpl
import ua.anadea.data.UserRepositoryImpl
import ua.anadea.data.token.TokenRepositoryImp
import ua.anadea.domain.repository.AdminRepository
import ua.anadea.domain.repository.AuthRepository
import ua.anadea.domain.repository.TokenRepository
import ua.anadea.domain.repository.UserRepository

val repositoryModule = module {
    single<TokenRepository> { TokenRepositoryImp() }
    single<AuthRepository> { AuthRepositoryImpl(get(named("PublicClient")), get()) }
    single<UserRepository> { UserRepositoryImpl(get(named("AuthClient"))) }
    single<AdminRepository> { AdminRepositoryImpl(get(named("AuthClient"))) }
}
