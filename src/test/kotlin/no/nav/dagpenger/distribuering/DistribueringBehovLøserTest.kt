package no.nav.dagpenger.distribuering

import io.kotest.assertions.json.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

internal class DistribueringBehovLøserTest {
    private val testRapid = TestRapid()

    @Test
    fun `Løser behov`() {
        val journalpostId = "12345"
        val bestillingId = "xxx"

        val distribusjonKlient =
            mockk<DistribusjonKlient>().also {
                coEvery {
                    it.distribuerJournalpost(
                        DistribusjonKlient.Request(
                            journalpostId = journalpostId,
                        ),
                    )
                } returns
                    DistribusjonKlient.Response(
                        bestillingId = bestillingId,
                    )
            }
        DistribueringBehovLøser(
            rapidsConnection = testRapid,
            distribusjonKlient = distribusjonKlient,
        )

        testRapid.sendTestMessage(
            testMelding(
                journalpostId = journalpostId,
            ),
        )

        testRapid.inspektør.size shouldBe 1
        //language=JSON
        testRapid.inspektør.message(0).toString() shouldEqualSpecifiedJsonIgnoringOrder """
            {
              "@event_name": "behov",
              "journalpostId": "$journalpostId",
              "@behov": [
                "DistribueringBehov"
              ],
              "@løsning": {
                "DistribueringBehov": {
                  "distribueringId": "$bestillingId"
                }
              }
            }
            """
    }

    private fun testMelding(journalpostId: String): String {
        //language=JSON
        return """
            {
              "@event_name": "behov",
              "@behovId": "1abdc524-d86f-429c-a330-62b87f6948af",
              "@behov": [
                "DistribueringBehov"
              ],
              "journalpostId": "$journalpostId",
              "@id": "cd88ba99-3920-4d25-ae68-46d9805eeaa8"
            }
            """.trimIndent()
    }
}
