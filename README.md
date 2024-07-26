## dp-behov-distribuering

## Formålet med appen
Sørge for at journalførte vedtaksbrev blir distribuert til dagpengesøker. 

## Hvordan fungerer appen
Appen lytter på ```"DistribueringBehov"``` hendelser publisert av [dp-saksbehandling](https://github.com/navikt/dp-saksbehandling),
og kaller DokDist-applikasjonens API for distribuering. Vedtaksbrevets journalpostId sendes med til APIet.
Hvordan brevet faktisk distribueres er utenfor denne applikasjonens ansvarsområde.

Når distribusjonen er ferdig, vil appen publisere en ```"DistribusjonFerdig"``` hendelse som kvittering. 
[dp-saksbehandling](https://github.com/navikt/dp-saksbehandling) lytter på kvitteringen og oppdaterer status på utsendingen.

## Begrensninger
Det er foreløpig kun vedtaksbrev for Arena-vedtak som kan distribueres via denne applikasjonen.
Applikasjonen er satt til å distribuere vedtaksbrevene på dagtid (07:00 - 23:00).

## Komme i gang
Gradle brukes som byggverktøy og er bundlet inn.

`./gradlew build`

## Henvendelser
Spørsmål kan stilles som issues på github.

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #team-dagpenger-saksbehandlerflate
