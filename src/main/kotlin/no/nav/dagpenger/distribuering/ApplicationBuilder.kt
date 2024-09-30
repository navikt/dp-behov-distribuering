package no.nav.dagpenger.distribuering

import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication

internal class ApplicationBuilder(
    config: Map<String, String>,
) : RapidsConnection.StatusListener {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    private val rapidsConnection: RapidsConnection = RapidApplication.create(config)

    init {
        rapidsConnection.register(this).also {
            DistribueringBehovLøser(
                rapidsConnection = rapidsConnection,
                distribusjonKlient =
                    DistribusjonHttpKlient(
                        url = Configuration.distribuerjournalpostUrl,
                        tokenProvider = Configuration.tokenProvider,
                    ),
            )
        }
    }

    fun start() = rapidsConnection.start()

    fun stop() = rapidsConnection.stop()

    override fun onStartup(rapidsConnection: RapidsConnection) {
        logger.info { "Starter opp dp-behov-distribuering" }
    }
}
