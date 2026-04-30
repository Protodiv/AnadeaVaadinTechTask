package ua.anadea.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Configuration
class KeyConfig(
    val jwtConfig: JwtConfig,
) {

    @Bean
    fun keyPair(): KeyPair {
        try {
            val privateKeyBytes = parseKey(jwtConfig.privateKeyPath)
            val publicKeyBytes = parseKey(jwtConfig.publicKeyPath)

            val keyFactory = KeyFactory.getInstance("RSA")

            val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
            val privateKey = keyFactory.generatePrivate(privateKeySpec)

            val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
            val publicKey = keyFactory.generatePublic(publicKeySpec)

            return KeyPair(publicKey, privateKey)
        } catch (e: Exception) {
            throw RuntimeException("Error loading keys", e)
        }
    }

    private fun parseKey(path: String): ByteArray {
        val bytes = Files.readAllBytes(Paths.get(path))
        val content = String(bytes)
        if (content.contains("-----BEGIN")) {
            val cleaned = content
                .replace(Regex("-----(BEGIN|END) (.*?)-----"), "")
                .replace(Regex("\\s"), "")
            return Base64.getDecoder().decode(cleaned)
        }
        return bytes
    }

    @Bean
    fun jwkSet(keyPair: KeyPair): JWKSet {
        val rsaKey = RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private as RSAPrivateKey)
            .keyID(generateKeyId(keyPair.public))
            .build()
        return JWKSet(rsaKey)
    }

    private fun generateKeyId(publicKey: PublicKey): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.encoded).take(16)
    }
}