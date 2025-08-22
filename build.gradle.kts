plugins {
    java
}

allprojects {
    group = "com.featureflag"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.9")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        implementation("org.apache.commons:commons-lang3:3.13.0")

        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
        testCompileOnly("org.projectlombok:lombok:1.18.30")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

        testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
        testImplementation("org.mockito:mockito-core:5.5.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
