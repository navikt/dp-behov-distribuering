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
    implementation("io.ktor:ktor-serialization-jackson3:${libs.versions.ktor.get()}")
    implementation("no.nav.dagpenger:oauth2-klient:2026.05.04-10.36.d489859941f7")

    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest.assertions)
    testImplementation(libs.bundles.naisful.rapid.and.rivers.test)
}

application {
    mainClass.set("no.nav.dagpenger.distribuering.AppKt")
}
