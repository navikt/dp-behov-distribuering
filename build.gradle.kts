plugins {
    id("common")
    application
    alias(libs.plugins.shadow.jar)
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
    implementation(libs.bundles.ktor.client)
    implementation("io.ktor:ktor-serialization-jackson:${libs.versions.ktor.get()}")
    implementation("no.nav.dagpenger:oauth2-klient:2024.12.10-14.29.b14a663ac6da")

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest.assertions)
    testImplementation(libs.bundles.naisful.rapid.and.rivers.test)
}

application {
    mainClass.set("no.nav.dagpenger.distribuering.AppKt")
}
