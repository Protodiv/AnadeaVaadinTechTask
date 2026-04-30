package ua.anadea.data.token

import ua.anadea.domain.repository.TokenRepository

class TokenRepositoryImp: TokenRepository {

    var accessToken: String? = null

    override fun saveBearerToken(token: String) {
        accessToken = token
    }

    override fun getBearerToken(): String? {
        return accessToken
    }
}