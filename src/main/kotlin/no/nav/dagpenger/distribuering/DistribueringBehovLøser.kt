package no.nav.dagpenger.distribuering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import io.ktor.client.plugins.ClientRequestException
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.runBlocking

private val logger = KotlinLogging.logger { }
private val sikkerlogg = KotlinLogging.logger("tjenestekall")

internal class DistribueringBehovLøser(
    rapidsConnection: RapidsConnection,
    private val distribusjonKlient: DistribusjonKlient,
) :
    River.PacketListener {
    companion object {
        const val BEHOV_NAVN = "DistribueringBehov"
    }

    init {
        River(rapidsConnection).apply {
            precondition {
                it.requireValue("@event_name", "behov")
                it.requireAll("@behov", listOf(BEHOV_NAVN))
                it.forbid("@løsning")
            }
            validate { it.requireKey("journalpostId") }
            validate { it.interestedIn("fagsystem") }
        }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
        metadata: MessageMetadata,
        meterRegistry: MeterRegistry,
    ) {
        val journalpostId = packet["journalpostId"].asText()

        val fagsystem =
            when (packet["fagsystem"].isMissingNode) {
                true -> "Arena"
                false -> packet["fagsystem"].asText()
            }
        val bestillendeFagsystem =
            when (fagsystem) {
                "Dagpenger" -> Fagsystem.DAGPENGER.kode
                "Arena" -> Fagsystem.ARENA.kode
                else -> throw IllegalStateException("Ugyldig fagsystem: $fagsystem")
            }

        withLoggingContext("journalpostId" to journalpostId) {
            if (journalpostId in emptySet<String>()) {
                logger.info { "Skipper journalpostId $journalpostId fra distribuering behovløser" }
                return
            }

            kotlin.runCatching {
                withLogging(journalpostId, packet) {
                    runBlocking {
                        val response =
                            distribusjonKlient.distribuerJournalpost(
                                DistribusjonKlient.Request(
                                    journalpostId = journalpostId,
                                    bestillendeFagsystem = bestillendeFagsystem,
                                ),
                            )
                        packet["@løsning"] =
                            mapOf(
                                BEHOV_NAVN to
                                    mapOf(
                                        "distribueringId" to response.bestillingsId,
                                    ),
                            )

                        val message = packet.toJson()
                        context.publish(message)
                        message
                    }
                }
            }.onFailure {
                if (it is ClientRequestException && it.response.status.value == 409) {
                    logger.info { "Journalpost allerede distribuert for journalpost: $journalpostId" }
                } else {
                    logger.error(it) { "Feil på kall mot joark" }
                    throw it
                }
            }
        }
    }

    private fun withLogging(
        jp: String,
        packet: JsonMessage,
        block: () -> String,
    ) {
        logger.info { "Løser behov for distribusjon av journalpost $jp" }
        try {
            block().let { løsning ->
                logger.info { "Løst behov for distribusjon av journalpost $jp" }
                sikkerlogg.info { "Løst behov for distribusjon av journalpost $jp med løsning $løsning" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Kunne ikke løse behov for $jp. Feil er ${e.message} " }
            sikkerlogg.error(e) { "Kunne ikke løse behov for pakke ${packet.toJson()}." }
            throw e
        }
    }
}
