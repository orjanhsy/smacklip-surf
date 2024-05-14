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

### Sekvensdiagram - her må teksten omformuleres

```mermaid
sequenceDiagram
    participant User
    participant App
    participant ViewModel
    participant Repository
    participant API

    User->>App: Åpner appen
    alt Ikke tilkoblet til Internett
        App->User: Viser popup med melding om å koble til Internett
    else Tilkoblet til Internett
    App->>App: Laster Splash Screen
        App->>App: Sjekker for tilkobling til Internett
        App->>ViewModel: Opretter ViewModel
        ViewModel->>Repository: Oppdaterer data fra Repository
        Repository->>API: Henter data fra API
        API-->>Repository: Sender data
        Repository-->>ViewModel: Oppdaterer ViewModel
        ViewModel -->>App: Oppdaterer state
        App->>User: Viser HomeScreen
        User->>App: Velger ett surf location card fra listen
        App ->>ViewModel: Gir beskjed til viewmodel
        ViewModel->>Repository: Oppdater 'areaInFocus'
        Repository-->>ViewModel: Sender state til ViewModel
        ViewModel -->>App: Oppdaterer state
        App->>User: Viser AreaScreen med lokasjonsdetaljer
        User->>App: Trykker på en dag
        App ->>ViewModel: Gir beskjed til viewmodel
        ViewModel->>Repository: Oppdaterer 'dayInFocus'
        Repository-->>ViewModel: Sender state til ViewModel
        ViewModel -->>App: Oppdaterer state
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

```

## Use-case
* Brukeren sjekker forhold fra SearchBar i MapSreen
#### Primæraktør
* Bølgesurfer
#### Pre-betingelser
* Brukeren har appen lastet ned
#### Post-betingelser
* Lokasjonen brukeren ønsker å sjekke er tilgjengelig i appen
### Hovedflyt
1. Aktør åpner appen
2. Appen viser SplashScreen
3. Aktøren navigerer til MapScreen via BottomBar
4. Aktør Søker etter lokasjonen i Searchbar
5. Aktør trykker på lokasjonen i SearchBar
6. Appen (MapBox) "flyr" til lokasjonen
7. Aktør trykker på gjeldende pil
8. Appen viser kortet med informasjon om lokasjonen
9. Aktør trykker på "gå til" knapp
10. Appen viser AreaScreen fot valgt lokasjon
11. Aktør velger dag hen vil sjekke
12. Appen viser prognose og forhold time for time
### ALternativ flyt
1. Aktør åpner appen
2. Appen viser at hen ikke er pålogget internett
3. Aktør logger på og går videre, eller sjekker ikke forhold

```mermaid
sequenceDiagram
    participant User
    participant App
    participant ViewModel
    participant Repository
    participant API
    Participant MapBox

    User->>App: Åpner appen
    alt Ikke tilkoblet til Internett
        App->User: Viser popup med melding om å koble til Internett
    else Tilkoblet til Internett
    App->>App: Laster Splash Screen
        App->>ViewModel: Opretter ViewModel
        ViewModel->>Repository: Oppdaterer data fra Repository
        Repository->>API: Henter data fra API
        API-->>Repository: Sender data
        Repository-->>ViewModel: Oppdaterer ViewModel
        ViewModel -->>App: Oppdaterer state
        App->>User: Viser HomeScreen
        User->>App: Naviger til MapScreen via BottomBar
        App->>MapBox: Henter kart fra MapBox
        MapBox-->>App: Viser kart
        App->>User: Viser MapScreen
        User->>App: Søker etter SurfArea i SearchBar
        App->>User: Viser lokasjoner i søkefeltet
        User->>App: Velger SurfArea og trykker
        App ->>ViewModel: Gir beskjed til ViewModel
        ViewModel->>Repository: Oppdaterer 'areaInFocus'
        Repository-->>ViewModel: Oppdaterer ViewModel
        ViewModel -->>App: Oppdaterer state
        App->>User: Viser SurfAreaScreen med lokasjonsdetaljer
        User->>App: Trykker på en dag
        App ->>ViewModel: Gir beskjed til viewmodel
        ViewModel->>Repository: Oppdaterer 'dayInFocus'
        Repository-->>ViewModel: Oppdaterer ViewModel
        ViewModel -->>App: Oppdaterer state
        App->>User: Viser DailyScreen med prognose time for time
    end
```


### Aktivitetsdiagram
```mermaid
graph TD;
    Start((Start)) --> Åpner_appen;
    Åpner_appen --> Valg{   };
    Valg -->|Internett_ok| Gå_til_MapScreen;
    Valg -->|Internett_ikke_ok| Logg_på_nett;
    Gå_til_MapScreen --> Søk_etter_lokasjon;
    Søk_etter_lokasjon --> Velg_lokasjon;
    Velg_lokasjon --> Velg_dag;
    
    Velg_dag --> Sjekk_forhold;
    Sjekk_forhold --> Slutt((Slutt));
    Logg_på_nett -->|Logget_på| Åpner_appen;
    Logg_på_nett -->|Ikke_logget_på| Slutt;

```
