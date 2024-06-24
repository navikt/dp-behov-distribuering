package no.nav.dagpenger.distribuering

import kotlinx.coroutines.runBlocking
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class DistribueringBehovLøser(
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
        runBlocking {
            val journalpostId = packet["journalpostId"].asText()
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
    }
}
