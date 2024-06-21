plugins {
    id("common")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
}

application {
    mainClass.set("no.nav.dagpenger.distribuering.AppKt")
}
