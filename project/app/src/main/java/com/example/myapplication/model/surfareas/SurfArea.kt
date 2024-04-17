package com.example.myapplication.model.surfareas

import com.example.myapplication.R


enum class SurfArea(
    val locationName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val direction: Int,
    val description: String,
    val modelName: String,
    val pointId: Int

    //nye = Sola, hellestø, Brusand, Sandvesand, Mjølhussand
) {
    HODDEVIK("Hoddevik",62.1237, 5.1615833, R.drawable.cover___hoddevik, 300, "Det kommer surfere fra hele verden for å oppleve bølgene i Hoddevik. Mye på grunn av det praktfulle landskapet med hvite sandstrender omkranset av høye fjellsider, men også fordi det ekstra godt egnet å surfe under ekstreme vindforhold fordi de høye fjellene skjermer stranden for den verste vinden. ", modelName = "stad20143x2v", pointId = 2),
    ERVIKA("Ervika", 62.166674, 5.115609,R.drawable.cover__ervika , 310, "Ervikstranda er en av de beste surfestrendene i Norge. Det er alltid mye bølger her som gjør det perfekt for avanserte surfere, men vær oppmerksom og ta forhåndsregler for det kan være en del undervannsstrømmer og vrakrester her. Her blir surfinga en fantastisk naturopplevelse med krystallklart vann og majestetiske fjell på alle kanter. På Ervikstranda har du nesten bølgegaranti, med bølger som slår inn både fra sørvest og nordvest.  ", modelName = "stad20143x2v", pointId = 2),

    //Lofoten
    SKAGSANDEN("Skagsanden", 68.107052, 13.295348, R.drawable.cover__skagsanden, 300, "Lofoten har noen av Norges og kanskje verdens vakreste strender. Omkranset av fjell og fjorder ligger kritthvite sandstrender og azurblått hav, de perfekte plassene for late sommerdager. Hvit sand og turkisblått vann. Små bølger av turkisblått vann vaskes forsiktig over den hvite sanden mens fjelltoppene stiger opp i den blå himmelen over. Ordene arktisk og strand er vanligvis ikke nevnt sammen i samme setning, men i Lofoten er det kanskje noen av de mest spektakulære strendene du noensinne vil se.", modelName = "lofoten2v", pointId = 9),
    UNSTAD("Unstad", 68.268527, 13.580834,R.drawable.cover__unstad , 320, "Surfing i Lofoten foregår i all hovedsak ved Unstad på Vestvågøy. Her kan du boltre deg på en 200 meter lang hvit sandstrand og i viltre, arktiske bølger. Her ligger Unstad Arctic Surf, en campingplass hvor erfarne og tøffe surfere camper, surfer, spiser og koser seg. Her møter du både lokale og internasjonale surfere, profesjonelle så vel som uerfarne drømmere. Venstrebølgen i havet ved Unstad er en av de beste i verden. Proffene kommer på høsten og vinteren til Lofoten, mens de snille sommerbølgene passer for nybegynnere.", modelName = "lofoten2v", pointId = 10),
    //GIMSTAD("Gimstad", 68.637591, 14.427877, 0, 270, "description", modelName = "vesteralen2v", pointId = 20),
    //SANDVIKBUKTA("Sandvikbukta", 68.757964, 14.470910, 0, 230, "description", modelName = "vesteralen2v", pointId = 20),

    //Sør-vest
    JAEREN("Boresanden", 58.800230, 5.548844, R.drawable.cover__jeren, 270, "Den lange og flotte Borestranden er et yndet turmål blant de lokale. På fine sommerdager er stranden ofte full av bade- og soleglade gjester. Vær imidlertid klar over at understrømningene ved stranden kan være utrolig sterke, så her må du være forsiktig når du svømmer, og du må passe godt på barn som leker i vannet. Strandlivet er ellers fint hele året, en tur på stranden om vinteren kan være riktig så forfriskende. ", modelName = "rogaland2v", pointId = 36),
    SOLA("Solastranden", 58.884963, 5.596460, R.drawable.cover_sola, 265, "Solastranden er langgrunn, derfor er den et populært sted for brettseilere og surfere på vindfulle dager. I nordenden finner du Sola Strand Hotel og Solastranden golfbane. I nærhet til stranden finner du også krigsminner, som bunkere og rester av kanonstillinger. Med Solastranden som utgangspunkt, kan du også ta turen langs stranden og kysten fem kilometer sørover til Vigdel for en flott gåtur med bademuligheter, både på strand og langs svaberg og klipper.", modelName = "vesteralen2v", pointId = 20),
    HELLESTØ("Hellestø", 58.841397, 5.556923, R.drawable.cover_hellesto, 285, "På Jæren er strendene hvite, naturen vakker, himmelen høy og horisonten vid. Kysten langs Jæren ligger åpen mot havet og er et mekka for surfere. Jæren ligger like utenfor Stavanger og er et flatt og vidstrakt område med mildt klima hele året. Her finner du milevis med hvite sandstrender, sanddyner, og flere lakseelver. Jæren har trolig Norges beste surfeforhold, også for nybegynnere, og flere surfeskoler som villig lærer deg surfekunstens regler.", modelName = "vesteralen2v", pointId = 20),
    BRUSAND("Brusandstranden", 58.533791, 5.743437, R.drawable.cover_brusand, 215, "Skal du prøve brett for først gang, kan det lønne seg å starte bølgesurfingen på strendene på Sola, Bore, Brusand og Hellestø. Her er vannet varmt og bølgestørrelsen liten. Jæren ligger like utenfor Stavanger og er et flatt og vidstrakt område med mildt klima hele året. Her finner du milevis med hvite sandstrender, sanddyner, og flere lakseelver. Jæren har trolig Norges beste surfeforhold, også for nybegynnere, og flere surfeskoler som villig lærer deg surfekunstens regler.", modelName = "vesteralen2v", pointId = 20),


    STAVASANDEN("Stavasanden", 59.233526, 5.183540, R.drawable.cover_stavasanden, 320, "For nybegynnere innen bølgesurfing er Stavasand ett godt alternativ. Flotte strender og nærhet til storhavet gjør Karmøy til en ideell plass å drive surfing, kiting og vindsurfing. Det er gode forhold for surfing sommer og tidlig høst, spesielt for nybegynnere, med relativt liten bølgestørrelse. Kraftig vind og åpent hav gjør vinteren til høysesong for de mer erfarne. Havbunnens topografi sørger for god surf break og surfere valfartes fra hele regionen om somrene for å ”catche” den perfekte bølgen.\n", modelName = "rogaland2v", pointId = 55),
    SANDVESAND("Sandvesand", 59.170507, 5.194763, R.drawable.cover_sandvesanden, 230, "Flotte strender og nærhet til storhavet gjør Karmøy til en ideell plass å drive surfing, kiting og vindsurfing. Det er gode forhold for surfing sommer og tidlig høst, spesielt for nybegynnere, med varmt vann og relativt liten bølgestørrelse.Havbunnens topografi sørger for god surf break og surfere valfartes fra hele regionen om somrene for å \"catche\" den perfekte bølgen. Sandvesand anbefales for mer erfarne surfere. ", modelName = "vesteralen2v", pointId = 20),
    MJØLHUSSAND("Mjølhussand", 59.168651, 5.196098, R.drawable.cover_mjolhussanden, 275, "Flotte strender og nærhet til storhavet gjør Karmøy til en ideell plass å drive surfing, kiting og vindsurfing. Det er gode forhold for surfing sommer og tidlig høst, spesielt for nybegynnere, med varmt vann og relativt liten bølgestørrelse.Havbunnens topografi sørger for god surf break og surfere valfartes fra hele regionen om somrene for å \"catche\" den perfekte bølgen. Sandvesand anbefales for mer erfarne surfere. ", modelName = "vesteralen2v", pointId = 20),


    //Østlandet
    SALTSTEIN("Saltstein", 58.969619, 9.832590, R.drawable.cover_saltstein, 190, "Saltstein er hele østlandets lokale surfespot. Oppdaget og surfet for første gang på 80-tallet. Saltsteins historie er rik, surferne her er mange. Bli kjent med Saltstein og østlandets perle her. Saltstein ligger like ved Oddanesand Camping, helt presist på Mølen. Parkering skjer i sørskauen, og det er ikke lov å kjøre helt ned til spotten. Det er ikke lenger lov å parkere på Oddanesand Camping, og om du tar beina fatt gjennom campingplassen:", modelName = "ytre_oslofjord2v", pointId = 1),


    //for tests
    NORDKAPP("Nordkapp",71.1655, 25.7992, 0, 360, "description", modelName = "nordkapp2v", pointId = 5),
    FEDJE("Fedje",60.7789, 4.71486, 0, 360, "description", modelName = "bremanger2v", pointId = 127),

}

