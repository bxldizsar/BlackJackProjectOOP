# BlackJackProjectOOP

Két játékos online blackjack Java Swing felülettel. A két gép egy közös Supabase táblán keresztül játszik.
Egyetemi OOP projekt.

## Előfeltételek

- JDK 11 vagy újabb
- Apache NetBeans (beépített Maven)
- Git

## Indítás NetBeansben

1. File > Open Project, és válaszd ki a repó mappáját.
2. Másold le a `config.example.properties` fájlt `config.properties` néven, és töltsd ki. Ez nem kerül fel a GitHubra.
3. Run Project (zöld háromszög): megnyílik a 600x600-as zöld ablak.
4. Jobb klikk a projekten > Test: lefutnak a JUnit tesztek.

## Csomagok

- `blackjack`: App, az ablak és a képernyőcsere (közös)
- `blackjack.model`: GameState és az enumok (közös); Card, Deck, Hand (A)
- `blackjack.logic`: a játék szabályai (A)
- `blackjack.net`: GameRepository (közös); Supabase kapcsolat és szinkron (B)
- `blackjack.storage`: hands.csv mentés (B)
- `blackjack.ui`: képernyők (A és B)
