import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.xml.sax.InputSource
import java.io.StringReader

plugins {
    java
    jacoco
}

allprojects {
    group = "com.featureflag"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    if (name != "frontend") {

        apply(plugin = "java")
        apply(plugin = "jacoco")

        java {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        extensions.configure<JacocoPluginExtension>("jacoco") {
            toolVersion = "0.8.10"
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

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            finalizedBy(tasks.named("jacocoTestReport"))
        }

        tasks.withType<JacocoReport>().configureEach {
            dependsOn(tasks.withType<Test>())
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
            doLast {
                val xmlReport = reports.xml.outputLocation.get().asFile
                if (!xmlReport.exists()) {
                    println("JaCoCo :: ${project.path} xml report not found at ${xmlReport.absolutePath}")
                    return@doLast
                }

                val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
                runCatching {
                    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
                    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
                    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
                }
                factory.isValidating = false
                factory.isNamespaceAware = false
                val documentBuilder = factory.newDocumentBuilder().apply {
                    setEntityResolver { publicId, systemId -> InputSource(StringReader(""))  }
                }
                val document = runCatching { documentBuilder.parse(xmlReport) }.getOrElse {
                    println("JaCoCo :: ${project.path} failed to parse report: ${it.message}")
                    return@doLast
                }
                val counters = document.getElementsByTagName("counter")
                var covered = 0L
                var missed = 0L
                for (index in 0 until counters.length) {
                    val node = counters.item(index)
                    val attributes = node.attributes ?: continue
                    val typeAttr = attributes.getNamedItem("type") ?: continue
                    if (typeAttr.nodeValue == "LINE") {
                        val coveredAttr = attributes.getNamedItem("covered")
                        val missedAttr = attributes.getNamedItem("missed")
                        if (coveredAttr != null) covered += coveredAttr.nodeValue.toLong()
                        if (missedAttr != null) missed += missedAttr.nodeValue.toLong()
                    }
                }
                val total = covered + missed
                val coverage = if (total == 0L) 0.0 else covered * 100.0 / total
                println(
                    "JaCoCo :: ${project.path} line coverage: " +
                            String.format("%.2f", coverage) +
                            "% ($covered/$total lines covered)"
                )
            }
        }
    }
}

// Frontend 빌드 통합
tasks.register("buildFrontend") {
    dependsOn(":frontend:buildReact")
    group = "build"
    description = "Build the React frontend"
}

tasks.register("startFrontendDev") {
    dependsOn(":frontend:startDev")
    group = "application"
    description = "Start the React frontend development server"
}

tasks.register("testFrontend") {
    dependsOn(":frontend:testReact")
    group = "verification"
    description = "Run React frontend tests"
}

// 전체 빌드에 frontend 포함
tasks.named("build") {
    dependsOn("buildFrontend")
}
