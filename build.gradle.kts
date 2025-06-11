
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "surik.simyan.locdots"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_23)
        localImageName.set("locdots-backend-image")
        imageTag.set("0.0.1-preview")
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.mongodb.driver.core)
    implementation(libs.mongodb.driver.sync)
    implementation(libs.bson)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.core)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
