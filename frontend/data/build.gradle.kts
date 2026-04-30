plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.serialization)
}
dependencies{
    implementation(projects.frontend.domain)

    implementation(ui.kotlin.coroutines)
    implementation(ui.bundles.ktor.common)
    implementation(ui.ktor.client.auth)
    implementation(libs.kotlinx.serialization.json)
}