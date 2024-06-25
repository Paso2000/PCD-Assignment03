package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"sync"
)

// globale cosi tutti la vedono
var wg sync.WaitGroup

type Msg struct {
	content string
	id      int // Id del player.
}

func main() {
	wg.Add(1)

	var nPlayers = 5
	var max = 100

	//initialization i canali di comunicazione
	MainChan := make(chan string)
	resultChannel := make(chan Msg)

	var subWg sync.WaitGroup

	//lancio l'oracolo
	go launchOracle(max, nPlayers, MainChan, resultChannel, subWg)

	//lacio il player
	for i := 0; i < nPlayers; i++ {
		//i è il nome del player
		subWg.Add(1)
		go launchPlayers(max, i, MainChan, resultChannel, subWg)
	}
	wg.Wait()
}

// Define l'oracolo
func launchOracle(max int, nPlayers int, mainChan chan string, resultChannel chan Msg, subWg sync.WaitGroup) {
	secretNumber := rand.Intn(max + 1)
	isGuessed := false

	//finchè non indovinano
	for !isGuessed {
		for i := 0; i < nPlayers; i++ {
			mainChan <- "Start"
		}
		// Gestiamo i messaggi dei numeri proposti, per ciascun player.
		for i := 0; i < nPlayers; i++ {
			//prendo i valore dal canale dei risultati
			attempt := <-resultChannel
			numReceived, err := strconv.Atoi(attempt.content) // Lo convertiamo.
			if err != nil {
				fmt.Println("Errore durante la conversione della stringa in intero:", err)
				return
			}
			// In base al numero gli indichiamo se è basso, alto, è quello indovinato oppure ho indovinato ma in ritardo rispetto ad un altro.
			if numReceived > secretNumber {
				mainChan <- "lower"
			} else if numReceived < secretNumber {
				mainChan <- "greater"
			} else if isGuessed {
				mainChan <- "winnerNotMe"
			} else {
				mainChan <- "winner"
				//winnerId = attempt.id
				isGuessed = true // Impostiamo il flag al fine di fare inviare il messaggio di fine.
			}
		}
		subWg.Wait()
		fmt.Println("Fine di un turno.")

	}
	//se esce dal while ha vinto
	for i := 0; i < nPlayers; i++ {
		mainChan <- "finish"
	}
}

// Define giocatore
func launchPlayers(max int, id int, mainChan chan string, resultChannel chan Msg, subWg sync.WaitGroup) {
	wg.Add(1) // Aggiungiamo un elemento al gruppo di attesa.
	subWg.Done()
	var min int = 0
	for true { // Continuo finchè qualcuno non ha indovinato.
		message := <-mainChan // Aspetto il messaggio di inizio.
		if message == "Start" {
			var num = rand.Intn(max-min+1) + min                     // Aggiorno il range ad ogni iterazione.
			resultChannel <- Msg{content: strconv.Itoa(num), id: id} // invio il numero random del tentativo sul canale pubblico per non determinismo.
			message := <-mainChan                                    // Attendo la risposta dall'oracolo.
			switch message {
			case "lower":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is greater")
				max = num - 1
			case "greater":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is lower")
				min = num + 1
			case "winnerNotMe":
				// Accade quando il numero è corretto ma è stato gestito dopo a un altro messaggio di vittoria.
				fmt.Println("[player: ", id, "] attemp: correct but too late")
			default:
				fmt.Println("[player: ", id, "] attemp: ", num, " -> winner")
			}
		} else if message == "finish" {
			fmt.Println("[player: ", id, "] -> finish")
			break
		}
	}
	defer wg.Done() // Segnalo il completamento.
}
