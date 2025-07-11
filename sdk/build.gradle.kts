plugins {
    kotlin("jvm")
}
dependencies {
    implementation(project(":shared"))
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}