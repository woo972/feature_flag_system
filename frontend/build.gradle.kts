plugins {
    id("com.github.node-gradle.node") version "7.0.2" // Gradle이 Node.js와 연동할 수 있는 플러그인

}

// Node.js 설정
configure<com.github.gradle.node.NodeExtension> {
    version.set("18.17.0")
    npmVersion.set("9.6.7")
    download.set(true)
}

// React 빌드 태스크
tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildReact") {
    dependsOn("npmInstall")
    args.set(listOf("run", "build"))
    inputs.files("package.json", "package-lock.json")
    inputs.dir("src")
    inputs.dir("public")
    outputs.dir("build")
}

// 개발 서버 시작 태스크
tasks.register<com.github.gradle.node.npm.task.NpmTask>("startDev") {
    dependsOn("npmInstall")
    args.set(listOf("run", "start"))
}

// 테스트 태스크
tasks.register<com.github.gradle.node.npm.task.NpmTask>("testReact") {
    dependsOn("npmInstall")
    args.set(listOf("run", "test", "--", "--coverage", "--watchAll=false"))
}

