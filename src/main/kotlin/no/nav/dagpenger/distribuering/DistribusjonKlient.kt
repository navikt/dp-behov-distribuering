package no.nav.dagpenger.distribuering

import com.fasterxml.jackson.annotation.JsonInclude
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.jackson.jackson

interface DistribusjonKlient {
    suspend fun distribuerJournalpost(request: Request): Response

    data class Request(
        val journalpostId: String,
        val distribusjonstype: String = "VEDTAK",
        val bestillendFagsystem: String = "AO01",
        val dokumentProdApp: String = "dagpenger ny",
    )

    data class Response(
        val bestillingId: String,
    )
}

class DistribusjonHttpKlient(
    url: String,
    tokenProvider: () -> String,
    engine: HttpClientEngine = CIO.create(),
) : DistribusjonKlient {
    private val httpClient =
        HttpClient(engine = engine) {
            defaultRequest {
                url(url)
                bearerAuth(tokenProvider())
            }
            install(ContentNegotiation) {
                jackson {
                    setSerializationInclusion(JsonInclude.Include.NON_NULL)
                }
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }

    override suspend fun distribuerJournalpost(request: DistribusjonKlient.Request): DistribusjonKlient.Response {
        return httpClient.post {
            setBody(request)
        }.body<DistribusjonKlient.Response>()
    }
}
