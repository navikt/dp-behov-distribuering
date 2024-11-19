package no.nav.dagpenger.distribuering

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

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
            validate { it.demandValue("@event_name", "behov") }
            validate { it.demandAll("@behov", listOf(BEHOV_NAVN)) }
            validate { it.requireKey("journalpostId") }
            validate { it.rejectKey("@løsning") }
        }.register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        val journalpostId = packet["journalpostId"].asText()
        kotlin.runCatching {
            withLogging(journalpostId, packet) {
                runBlocking {
                    val response =
                        distribusjonKlient.distribuerJournalpost(
                            DistribusjonKlient.Request(
                                journalpostId = journalpostId,
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
            logger.error(it) { "Feil på kall mot joark" }
            throw it
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
