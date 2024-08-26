## Om prosjektet
Dette er et teamarbeid gjennomført som prosjekt i emnet *IN2000 – Software Engineering med prosjektarbeid* ved Universitet i Oslo.

## Dokumentasjon
Applikasjonens er dokumentert gjennom kommentarer i koden. Markdown filen ARCHITECTURE.md gir oversikt over arkitekturen ved en arkitekturtegning, en tekstlig beskrivelse og en oversikt over API-nivå, verktøy og de API-ene vi har benyttet.


## Informasjon om biblioteker
Se mer i ARCHITECTURE.md

* Proto DataStore
* KTOR
* LocalDateTime
* MapBox
* Jetpack Compose
* Material3

## Redegjørelse for varsler
* Vi har én "warning" i IDE-en i SurfAreaScreen og DailyScreen.  Material3.TopAppBar biblioteket er avviklet. Vi så ikke en rask måte å fikse det på så vi valgte å fortsette å benytte det.
* I modelmappen har mange klasser warnings på atributtnavn siden vi ikke har brukt @SerializedName
* Vi får en del "warnings" i Gradle på at en del av bibliotekene ikke er helt oppdatert. 
