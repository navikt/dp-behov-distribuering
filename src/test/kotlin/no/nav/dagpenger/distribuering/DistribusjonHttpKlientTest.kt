package no.nav.dagpenger.distribuering

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class DistribusjonHttpKlientTest {
    @Test
    fun testDistribuerJournalpostSuccess(): Unit =
        runBlocking {
            var httpRequestData: HttpRequestData? = null

            val mockEngine =
                MockEngine {
                    httpRequestData = it
                    respond(
                        content = """{"bestillingId":"12345"}""",
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }

            val distribusjonKlient =
                DistribusjonHttpKlient(
                    url = "https://mockapi.test",
                    tokenProvider = { "mock-token" },
                    engine = mockEngine,
                )

            val response =
                distribusjonKlient.distribuerJournalpost(
                    DistribusjonKlient.Request(
                        journalpostId = "test-journalpost",
                    ),
                )

            httpRequestData shouldNotBe null
            httpRequestData?.let {
                it.url.toString() shouldBe "https://mockapi.test"
                it.headers[HttpHeaders.Authorization] shouldBe "Bearer mock-token"
            }

            response.bestillingId shouldBe "12345"
        }

    @Test
    fun testDistribuerJournalpostFailure(): Unit =

        runBlocking {
            shouldThrow<ClientRequestException> {
                DistribusjonHttpKlient(
                    url = "https://mockapi.test",
                    tokenProvider = { "mock-token" },
                    engine =
                        MockEngine {
                            respondBadRequest()
                        },
                ).distribuerJournalpost(
                    DistribusjonKlient.Request(
                        journalpostId = "test-journalpost",
                    ),
                )
            }
        }
}
