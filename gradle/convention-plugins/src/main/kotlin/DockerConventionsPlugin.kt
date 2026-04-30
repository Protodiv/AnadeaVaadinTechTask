import org.gradle.api.provider.Property
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.springframework.boot.buildpack.platform.build.PullPolicy
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
interface DockerImageExtension {
    val serviceName: Property<String>
    val namespace: Property<String>
    val registryUrl: Property<String>
    val imageTag: Property<String>
}

class DockerConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("dockerConfig", DockerImageExtension::class.java)

        ext.namespace.convention(project.providers.gradleProperty("namespace"))
        ext.registryUrl.convention(project.providers.gradleProperty("registryUrl"))
        ext.imageTag.convention(project.providers.gradleProperty("imageTag"))

        val mImageNameProvider = ext.serviceName.map { "${ext.namespace.get()}-$it" }
        val fullImageNameProvider = mImageNameProvider.map { "${ext.registryUrl.get()}/$it:${ext.imageTag.get()}" }

        project.tasks.named("bootBuildImage", BootBuildImage::class.java) {
            imageName.set(mImageNameProvider)
            builder.set("paketobuildpacks/builder-jammy-base:latest")
            runImage.set("paketobuildpacks/run-jammy-base:latest")
            imagePlatform.set("linux/amd64")
            pullPolicy.set(PullPolicy.IF_NOT_PRESENT)
            cleanCache.set(false)
            environment.set(mapOf("BP_JVM_VERSION" to "21"))
        }

        project.tasks.register("tagImage", Exec::class.java) {
            dependsOn("bootBuildImage")
            doFirst {
                commandLine("docker", "tag", "${mImageNameProvider.get()}:${ext.imageTag.get()}", fullImageNameProvider.get())
            }
        }

        project.tasks.register("pushImage", Exec::class.java) {
            dependsOn("tagImage")
            doFirst {
                commandLine("docker", "push", fullImageNameProvider.get())
            }
        }
    }
}