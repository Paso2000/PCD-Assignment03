# Parte 2B - Java RMI
## Sudoku Collaborativo

### Architettura
#### Oggetti distribuiti
- SudokuServer
ha una Map<String, SudokuGames> per tenere traccia di tutte le partite
- SudokuGame
ha una List<Player> per tenere traccia di tutti i player che fanno parte della partita

#### Altri oggetti non distribuiti
- Player (inutile?)
- SudokuGUI
- Grid

#### Funzionalità
0. Il ServerLauncher viene lanciato
    - crea un nuovo SudokuServer e lo distribuisce
1. Parte la GUI iniziale (lanciata dal ServerLauncher), che apre due opzioni:
   1. Un giocatore crea una nuova partita
       - il server crea un nuovo SudokuGame inserendo nei players solo la GUI del giocatore corrente e lo distribuisce
       - il game aggiorna le gui di tutti players in questo caso 1
   2. Un giocatore entra in una partita
       - il server ottiene il SudokuGame scelto dal registro e lo modifica aggiungendo trai suoi player il nuovo giocatore
       - il game aggiorna le gui di tutti i players per avvisare dell'arrivo di un nuovo giocatore
3. Un giocatore lascia una partita
    - la GUI informa il game che un giocatore ha lasciato la partita
    - il game rimuove il giocatore dai players e informa tutti i giocatori di aggiungere un messaggio alla GUI
    - il game aggiorna le gui di tutti i players per avvisare dell'arrivo di un nuovo giocatore
4. Un giocatore inserisce un numero/Un giocatore seleziona una casella
    - la GUI del giocatore informa il game della modifica 
    - il game fa partire l'aggiornamento della GUI a tutti i players

