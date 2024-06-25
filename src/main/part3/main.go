package main

import (
	"fmt"       // stampa
	"math/rand" // numeri casuali
	"strconv"   // conversione di stringhe
	"sync"      // sincronizzazione goroutine
)

// Waitgroup (barrier) utilizzati per attendere che tutte le goroutine vengano terminate
// Sono globali poichè le sync non si passano per riferimento
var wg sync.WaitGroup
var subWg sync.WaitGroup

type Attempt struct {
	content string // tentativo di proposta di numero da indovinare
	id      int    // id del player
}

func main() {
	//wg.Add(1) ---> removed from Lucia :)

	var nPlayers = 5
	var max = 100
	mainChan := make(chan string)       // canale principale dove si scambiano lo stato
	resultChannel := make(chan Attempt) // canale dei risultati dove si scambiano i tentativi

	go launchOracle(max, nPlayers, mainChan, resultChannel)
	wg.Add(nPlayers)
	for i := 0; i < nPlayers; i++ {
		go launchPlayers(max, i, mainChan, resultChannel)
	}

	// Fa in modo che il main attenda che tutte le goroutine abbiano terminato
	wg.Wait()
}

// Definisce l'oracolo
func launchOracle(max int, nPlayers int, mainChan chan string, resultChannel chan Attempt) {
	secretNumber := rand.Intn(max + 1)
	isGuessed := false
	for !isGuessed {
		// Fa partire i player lanciando un messaggio "Start" sul mainChan
		for i := 0; i < nPlayers; i++ {
			mainChan <- "start"
		}
		// Gestisce i tentativi di ciascun player
		for i := 0; i < nPlayers; i++ {
			// Riceve un tentativo da resultChannel
			attempt := <-resultChannel
			numReceived, err := strconv.Atoi(attempt.content)
			if err != nil {
				fmt.Println("Error:", err)
				return
			}
			// Verifica il tentativo del giocatore
			if numReceived > secretNumber {
				mainChan <- "lower"
			} else if numReceived < secretNumber {
				mainChan <- "greater"
			} else if isGuessed {
				mainChan <- "winnerNotMe"
			} else {
				mainChan <- "winner"
				isGuessed = true
			}
		}
		subWg.Add(5)
		subWg.Wait()
		fmt.Println("Fine di un turno.")

	}
	// Invia un messaggio finish sul mainChan quando il numero è stato indovinato
	for i := 0; i < nPlayers; i++ {
		mainChan <- "finish"
	}
}

// Definisce il giocatore
func launchPlayers(max int, id int, mainChan chan string, resultChannel chan Attempt) {
	// wg.Add(1) ---> removed from Lucia :)
	var min int = 0
	// Ciclo infinito nel quale il player attende messaggi dal mainChan fino a quando qualcuno non indovina
	for true {
		message := <-mainChan
		if message == "start" {
			var num = rand.Intn(max-min+1) + min
			// Il player# scrive nel canale il suo tentativo
			resultChannel <- Attempt{content: strconv.Itoa(num), id: id}
			// Attende la risposta dall'oracolo
			message := <-mainChan
			switch message {
			case "lower":
				fmt.Println("[player: ", id, "] attempt: ", num, " -> secret number is greater")
				max = num - 1
				subWg.Done()

			case "greater":
				fmt.Println("[player: ", id, "] attempt: ", num, " -> secret number is lower")
				min = num + 1
				subWg.Done()

			case "winnerNotMe":
				// Accade quando il numero è corretto ma è stato gestito dopo a un altro messaggio di vittoria.
				fmt.Println("[player: ", id, "] attempt: correct but too late")
				subWg.Done()

			default:
				fmt.Println("[player: ", id, "] attempt: ", num, " -> winner")
				subWg.Done()
			}
		} else if message == "finish" {
			fmt.Println("[player: ", id, "] -> finish")
			break
		}
	}
	defer wg.Done() // Segnala il completamento
}
