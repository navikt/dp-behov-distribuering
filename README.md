## dp-behov-distribuering

## Formålet med appen
Sørg for at journalførte vedtaksbrev blir distribuert til dagpengesøker. 

## Hvordan fungerer appen
Ved å lytte på ```"DistribueringBehov"``` hendelser som publiseres fra [dp-saksbehandling](https://github.com/navikt/dp-saksbehandling)
så vil appen kalle dokdist api for distribuering med journalpostIden til vedtaksbrevet.

Når distribusjonen er ferdig vil appen publisere en ```"DistribusjonFerdig"``` hendelse som kvittering. 
[dp-saksbehandling](https://github.com/navikt/dp-saksbehandling) lytter på kvitteringen og oppdaterer status på Utsendingen.

## Begrensninger
1. Det er kun vedtaksbrev som kan distribueres via denne applikasjonen.
2. Det er kun ARENA vedtak som kan distribueres via denne applikasjonen foreløpig.

## Komme i gang
Gradle brukes som byggverktøy og er bundlet inn

`./gradlew build`

## Henvendelser
Spørsmål kan stilles som issues på github.

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #team-dagpenger-saksbehandlerflate
