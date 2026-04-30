plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.docker.conventions)
}

val mServiceName = "auth"

dockerConfig {
    serviceName.set(mServiceName)
}

dependencies {
    implementation(libs.kotlin.reflect)

    implementation(libs.sbs.webmvc)
    implementation(libs.sbs.security)
    implementation(libs.sbs.actuator)
    implementation(libs.sbs.validation)

    implementation(libs.sbs.data.jpa)
    runtimeOnly(libs.org.postgresql)
    implementation(libs.fasterxml.jackson.module.kotlin)

    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    implementation(libs.bundles.jjwt)
    implementation(libs.nimbus.jose.jwt)

}

dependencyManagement {
    imports {
        mavenBom(libs.spring.cloud.bom.get().toString())
    }
}

tasks.bootJar {
    archiveFileName.set("$mServiceName.jar")
}

tasks.test {
    useJUnitPlatform()
}
