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
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
