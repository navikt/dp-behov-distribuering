package no.nav.dagpenger.distribuering

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

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
    }

    private fun withLogging(
        jp: String,
        packet: JsonMessage,
        block: () -> String,
    ) {
        logger.info { "Løser behov for distribusjon av journalpost $jp" }
        sikkerlogg.info { "Løser behov for distribusjon av journalpost for ${packet.toJson()}" }
        try {
            block().let { løsning ->
                logger.info { "Løst behov for distribusjon av journalpost $jp" }
                sikkerlogg.info { "Løst behov for distribusjon av journalpost $jp med løsning $løsning" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Kunne ikke løse behov for $jp. Feil er ${e.message} " }
            sikkerlogg.error(e) { "Kunne ikke løse behov for pakke $packet." }
            throw e
        }
    }
}
