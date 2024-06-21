package no.nav.dagpenger.distribuering

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding

internal object Configuration {
    const val APP_NAME = "dp-behov-distribuering"

    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "RAPID_APP_NAME" to "dp-behov-distribuering",
                "KAFKA_CONSUMER_GROUP_ID" to "dp-behov-distribuering-v1",
                "KAFKA_RAPID_TOPIC" to "teamdagpenger.rapid.v1",
                "KAFKA_RESET_POLICY" to "latest",
            ),
        )

    val properties =
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding defaultProperties

    val config: Map<String, String> =
        properties.list().reversed().fold(emptyMap()) { map, pair ->
            map + pair.second
        }
}
