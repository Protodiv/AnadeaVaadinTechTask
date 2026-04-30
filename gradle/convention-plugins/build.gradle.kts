plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.0.3")
    implementation("org.springframework.boot:spring-boot-buildpack-platform:4.0.3")
}

gradlePlugin {
    plugins {
        create("dockerConventions") {
            id = "docker-conventions"
            implementationClass = "DockerConventionsPlugin"
        }
    }
}
