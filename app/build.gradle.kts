plugins {
    java
}

group = "org.lenerki"
version = "1.0.1"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    compileOnly(files("../../libs/HytaleServer.jar"))


    implementation("com.moandjiezana.toml:toml4j:0.7.2")


    testImplementation(libs.junit)
}

tasks.jar {
    archiveBaseName.set("InstantHealth")

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}