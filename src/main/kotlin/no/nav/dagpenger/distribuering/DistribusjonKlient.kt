package no.nav.dagpenger.distribuering

import com.fasterxml.jackson.annotation.JsonInclude
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import mu.KotlinLogging

private val log = KotlinLogging.logger { }

interface DistribusjonKlient {
    suspend fun distribuerJournalpost(request: Request): Response

    data class Request(
        val journalpostId: String,
        val distribusjonstype: String = "VEDTAK",
        val bestillendeFagsystem: String = "AO01",
        val dokumentProdApp: String = "dagpenger ny",
        val distribusjonstidspunkt: String = "UMIDDELBART",
    )

    data class Response(
        val bestillingsId: String,
    )
}

class DistribusjonHttpKlient(
    url: String,
    private val tokenProvider: () -> String,
    engine: HttpClientEngine = CIO.create(),
) : DistribusjonKlient {
    private val httpClient =
        HttpClient(engine = engine) {
            expectSuccess = true
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
                level = LogLevel.ALL
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            log.info { message }
                        }
                    }
            }
        }

    override suspend fun distribuerJournalpost(request: DistribusjonKlient.Request): DistribusjonKlient.Response {
        return httpClient.post {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<DistribusjonKlient.Response>()
    }
}
