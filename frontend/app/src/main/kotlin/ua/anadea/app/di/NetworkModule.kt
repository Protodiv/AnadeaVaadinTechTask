package ua.anadea.app.di

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.anadea.data.AdminRepositoryImpl
import ua.anadea.data.AuthRepositoryImpl
import ua.anadea.data.UserRepositoryImpl
import ua.anadea.domain.repository.AdminRepository
import ua.anadea.domain.repository.AuthRepository
import ua.anadea.domain.repository.TokenRepository
import ua.anadea.domain.repository.UserRepository
import ua.anadea.data.token.TokenManager
import ua.anadea.data.token.TokenRepositoryImp


val networkModule = module {

    single<String>(named("baseUrl")) { 
        System.getenv("APP_BASE_URL") ?: "http://localhost"
    }

    single<Json>{
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

    }
    single<CookiesStorage> { AcceptAllCookiesStorage() }

    single<HttpClient>(named("PublicClient")) {
        HttpClient(CIO) {

            expectSuccess = false

            install(HttpCookies) {
                storage = get()
            }

            install(ContentNegotiation){
                json(get())
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            defaultRequest {
                url(get<String>(named("baseUrl")))
                contentType(ContentType.Application.Json)
            }
        }
    }

    single<HttpClient>(named("AuthClient")) {
        HttpClient(CIO) {

            expectSuccess = false

            install(HttpCookies) {
                storage = get()
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            install(ContentNegotiation){
                json(get())
            }

            defaultRequest {
                url(get<String>(named("baseUrl")))
                contentType(ContentType.Application.Json)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        get<TokenManager>().getBearerToken()
                    }
                    refreshTokens {
                        get<TokenManager>().refreshToken()
                    }
                }
            }
        }
    }

    single<TokenManager> { TokenManager(get(), get()) }
}