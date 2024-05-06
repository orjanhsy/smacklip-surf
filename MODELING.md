```mermaid
sequenceDiagram
    participant User
    participant App
    participant API

    User->>App: Åpner appen
    App->>App: Viser Splash Screen
    App->>API: Henter data fra API
    API-->>App: Sender data
    App->>App: Laster HomeScreen med surf location cards
    App->>User: Viser HomeScreen
    User->>App: Velger ett surf location card
    App->>App: Laster AreaScreen for valgte lokasjoner
    App->>User: Viser AreaScreen med lokasjondetasljer
    User->>App: Trykker på en dag
    App->>App: Laster TodayScreen for valgt dag
    App->>User: Viser TodayScreen med prognose time for time