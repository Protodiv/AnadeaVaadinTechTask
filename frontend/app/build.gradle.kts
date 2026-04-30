plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(ui.plugins.vaadin)
}

val serviceName = "frontend"
val namespace: String by project
val mImageName = "$namespace-$serviceName"
val registryUrl: String by project
val imageTag: String by project
val fullImageName = "$registryUrl/$mImageName:$imageTag"

defaultTasks("clean", "build")

dependencies {
    implementation(projects.frontend.domain)
    implementation(projects.frontend.data)

    implementation(ui.bundles.koin)
    implementation(ui.bundles.ktor.common)
    implementation(ui.ktor.client.auth)

    // Karibu-DSL dependency
    implementation(ui.karibu.dsl)

    // Vaadin
    implementation(ui.vaadin.core)
    if (!vaadin.effective.productionMode.get()) {
        implementation(ui.vaadin.dev)
    }
    implementation(ui.vaadin.boot)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass = "ua.anadea.app.MainKt"
}

tasks.register<Exec>("dockerBuildUi") {
    workingDir = project.rootDir
    commandLine(
        "docker", "build",
        "--progress=plain",
        "-t", "$mImageName:$imageTag",
        "-f", "frontend/app/Dockerfile",
        "."
    )
}

tasks.register<Exec>("tagImage") {
    dependsOn("dockerBuildUi")
    commandLine("docker", "tag", "$mImageName:$imageTag", fullImageName)
}

tasks.register<Exec>("pushImage") {
    dependsOn("tagImage")
    commandLine("docker", "push", fullImageName)
}