# Funzionamento 

## Actors:
- Player 
1) contiene l'istanza della griglia di gioco,
2) contiene tutti i player, 
3) deve essere eletto un leader che funge da postino per tutti gli altri nodi Player della stessa partita del quale ogni Player detiene una Ref)
- Games 
1) contiene una mappa Partita -> Player Leader e una mappa <ActorRef, List<ActorRef>> che mappa il leader da tutti i componenti della partita)
- Grid 
1) FE del PlayerActor
2) contiene una griglia

Altre cose:
Main
GUI

## Fasi
0.
- crea un nuovo GamesActor
1. Parte la GUI inziale lanciata dal Main
2. Un giocatore crea una nuova partita:
- crea nuovo PlayerActor che diventa leader
- crea un nuovo GridActor contenente un istanza del PlayerActor
- mando un messaggio a GamesActor per comunicargli l'inizio della nuova partita e il relativo player
3. Un giocatore entra in una partita
- crea un nuovo PlayerActor
- il nuovo Player manda un messaggio al Games per avvisare di essere entrato (nella griglia che stabilisce il Games)
- il Games manda un messaggio al Player leader che
    - registra l'ingresso del player nella griglia aggiungendolo ai players
    - manda un messaggio a tutti gli altri players della partita per avvisarli del nuovo giocatore
    - invia al nuovo player tutti i dati relativi alla partita
4. Un giocatore inserisce un numero
5.


## Fault tolerance:
Cosa succede se crolla il leader?