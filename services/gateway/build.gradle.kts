plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.docker.conventions)
}

val mServiceName = "gateway"

dockerConfig {
    serviceName.set(mServiceName)
}

dependencies {
    implementation(libs.kotlin.reflect)

    implementation(libs.sbs.actuator)
    implementation(libs.sbs.security)
    implementation(libs.sbs.oauth2)

    implementation(libs.scs.gateway.server.webflux)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.cloud.bom.get().toString())
    }
}

tasks.bootJar {
    archiveFileName.set("$mServiceName.jar")
}