# Appunti funzionamento Sudoku collaborativo con Attori Akka 
## Entità
### Attori:
- Player (contiene l'istanza della griglia di gioco, contiene tutti i player, deve essere eletto un leader che funge 
da postino per tutti gli altri nodi Player della stessa partita del quale ogni Player detiene una Ref)
  - oggetto Grid: istanza della griglia di Gioco
  - booleano isLeader: true se il PlayerActor è il player leader, false in caso contrario
  - Optional<List<ActorRef>>: lista degli altri player della partita, empty se il PlayerActor non è il player leader
- Games (gestore delle partite)
  - Map<Integer, ActorRef>: mappa Partita -> Player Leader
  - Map<ActorRef, List<ActorRef>>: mappa il leader a tutti i componenti della partita)
- Grid (FE del PlayerActor, contiene una griglia con tutti i numeri inseriti dai vari giocatori)

### Altri oggetti (non-attori):
- App GUI iniziale di scelta tra nuova partita e unisciti a una partita)
- Grid (?) per rappresentare la griglia che contiene i numeri e le caselle selezionate, contiene o due array
bidimensionali di cui uno di interi che rappresenta i numeri inseriti e uno (di cosa? di interi che ci mettiamo gli id?
o di ActorRef direttamente?) che rappresenta quali giocatori occupano le varie caselle
o un array sempre bidimensionale di Cell (nuovo oggetto che contiene un intero che rappresenta il valore e un ActorRef o un intero
- id del player - per rappresentare l'eventuale player che ha selezionato la cella)

## Funzionamento Sudoku
0. Il main viene lanciato 
   - crea un nuovo GamesActor
1. Parte la GUI iniziale lanciata dal Main
2. Un giocatore crea una nuova partita
   - crea nuovo PlayerActor che diventa leader
   - crea un nuovo GridActor contenente un istanza del PlayerActor
   - mando un messaggio a GamesActor per comunicargli l'inizio della nuova partita e il relativo PlayerActor che l'ha creata
3. Un giocatore entra in una partita
   - crea un nuovo PlayerActor
   - il nuovo PlayerActor manda un messaggio al GamesActor per avvisare di essere entrato 
   (nella griglia che stabilita dal GamesActor stesso)
   - il GamesActor manda un messaggio al PlayerActor leader che
       - registra l'ingresso del PlayerActor nella griglia aggiungendolo alla sua lista di players interna
       - manda un messaggio a tutti gli altri players della partita per avvisarli del nuovo giocatore
       - invia al nuovo PlayerActor tutti i dati relativi alla partita
4. Un giocatore inserisce un numero
   - il GridActor, dopo aver modificato il suo stato interno della griglia, invia un messaggio al suo PlayerActor 
   perchè possa modificare anche lui lo stato della griglia
   - il GridActor o il PlayerActor (?) verificano che il numero inserito termini o meno la partita
   - se Il numero inserito non termina la partita, il PlayerActor una volta modificata sua griglia interna notifica 
   al PlayerActor leader la nuova griglia di gioco
   - il PLayerActor leader notifica a sua volta la modifica della griglia di gioco a tutti i players della partita
   - se numero inserito termina la partita, il PlayerActor una volta modificata sua griglia interna notifica
   a tutti gli altri players la nuova griglia di gioco comunicandogli anche che la partita è terminata. sempre 
   se il numero termina la partita, il PlayerActor/GridActor che ha verificato la conclusione della partita
   invia un messaggio al GamesActor per notificare la terminazione della partita. Il GamesActor gestisce la terminazione
   rimuovendo dalle sue mappe interne i riferimenti alla partita appena terminata
5. Un giocatore seleziona una casella
   - il GridActor, dopo aver modificato il suo stato interno della griglia, invia un messaggio al suo PlayerActor
   perchè possa modificare anche lui lo stato della griglia
   - il PlayerActor una volta modificata sua griglia interna notifica a tutti gli altri players la nuova griglia di gioco
6. Un giocatore lascia una partita 
(Chi è il primo a ricevere l'evento di uscita dalla partita? di seguito suppongo che sia il GridActor)
   - il GridActor invia al suo PlayerActor il messaggio che informa che ha lasciato la partita e che deve eclissarsi
   - il PlayerActor uscente invia un messaggio al leader informandolo dell'uscita dalla partita
   - il PLayerActor leader aggiorna il suo stato interno poi propaga il messaggio a tutti gli altri PlayerActor 
   informandoli del PlayerActor uscente

## Fault tolerance
### Cosa succede se crolla il leader?
In ogni comunicazione con il player leader è necessario inserire un timeout per verificare che i messaggi arrivino correttamente
Nel caso in cui il leader non risponda abbastanza velocemente viene eletto un nuovo leader dal GamesActor la cui lista di player
viene recuperata dal GamesActor stesso, nella sua mappa <ActorRef, List<ActorRef>> che mappa il leader a tutti i componenti della partita