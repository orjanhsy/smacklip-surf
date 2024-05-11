# Modellering

## Use-case
* Brukeren skal sjekke forholdene på en gitt lokasjon på et gitt tidspunkt
#### Primæraktør
* Bølgesurfer
#### Pre-betingelser
* Brukeren har appen lastet ned
#### Post-betingelser 
* Lokasjonen brukeren ønsker å sjekke er tilgjengelig i appen
### Hovedflyt
1. Aktør åpner appen
2. Appen viser SplashScreen
3. Aktør leter etter lokasjonen hen vil sjekke i listen med lokasjoner
4. Aktør trykker på gitt lokasjon
5. Appen viser AreaScreen
6. Aktør velger dag hen vil sjekke
7. Appen viser prognose og forhold time for time

### ALternativ flyt
1. Aktør åpner appen
2. Appen viser at hen ikke er pålogget internett
3. Aktør logger på og går videre, eller sjekker ikke forhold

### Sekvensdiagram

```mermaid
sequenceDiagram
    participant User
    participant App
    participant Repository
    participant API

    User->>App: Åpner appen
    alt Ikke tilkoblet til Internett
        App->User: Viser popup med melding om å koble til Internett
    else Tilkoblet til Internett
    App->>App: Laster Splash Screen
        App->>App: Sjekker for tilkobling til Internett
        App->>Repository: Henter data fra Repository
        Repository->>API: Henter data fra API
        API-->>Repository: Sender data
        Repository-->>App: Sender data til App
        App->>App: Laster HomeScreen med surf location cards
        App->>User: Viser HomeScreen
        User->>App: Velger ett surf location card fra listen
        App->>App: Laster AreaScreen for valgte lokasjoner
        App->>User: Viser AreaScreen med lokasjonsdetaljer
        User->>App: Trykker på en dag
        App->>App: Laster TodayScreen for valgt dag
        App->>User: Viser TodayScreen med prognose time for time
    end
```



### Aktivitetsdiagram
```mermaid
graph TD;
    Start((Start)) --> Åpner_appen;
    Åpner_appen --> Valg{   };
    Valg -->|Internett_ok| Velg_lokasjon;
    Valg -->|Internett_ikke_ok| Logg_på_nett;
    Velg_lokasjon --> Velg_dag;
    Velg_dag --> Sjekk_forhold;
    Sjekk_forhold --> Slutt((Slutt));
    Logg_på_nett -->|Logget_på| Åpner_appen;
    Logg_på_nett -->|Ikke_logget_på| Slutt;
