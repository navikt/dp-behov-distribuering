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
        try {
            runBlocking {
                val respond =
                    distribusjonKlient.distribuerJournalpost(
                        DistribusjonKlient.Request(
                            journalpostId = journalpostId,
                        ),
                    )

                packet["@løsning"] =
                    mapOf(
                        BEHOV_NAVN to
                            mapOf(
                                "distribueringId" to respond.bestillingId,
                            ),
                    )

                context.publish(packet.toJson())
            }
        } catch (e: Exception) {
            logger.error(e) { "Kunne ikke løse behov for $journalpostId. Feil er ${e.message} " }
            sikkerlogg.error(e) { "Kunne ikke løse behov for pakke $packet." }
        }
    }
}
