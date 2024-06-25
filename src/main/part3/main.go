package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"sync"
)

// Globale, (le sync non si passano per riferimento)
var wg sync.WaitGroup
var subWg sync.WaitGroup

type Attempt struct {
	content string
	id      int // Id del player.
}

func main() {
	wg.Add(1)

	var nPlayers = 5
	var max = 100
	MainChan := make(chan string)       //canale principale dove si scambiano lo stato
	resultChannel := make(chan Attempt) //canale dei risultati dove si scambiano i tentativi

	go launchOracle(max, nPlayers, MainChan, resultChannel)
	for i := 0; i < nPlayers; i++ {
		go launchPlayers(max, i, MainChan, resultChannel)
	}

	wg.Wait()
}

// Define l'oracolo
func launchOracle(max int, nPlayers int, mainChan chan string, resultChannel chan Attempt) {
	secretNumber := rand.Intn(max + 1)
	isGuessed := false
	for !isGuessed {
		//fa partire i player
		for i := 0; i < nPlayers; i++ {
			mainChan <- "Start"
		}
		// Gestiamo i messaggi dei numeri proposti, per ciascun player.
		for i := 0; i < nPlayers; i++ {
			attempt := <-resultChannel
			numReceived, err := strconv.Atoi(attempt.content)
			if err != nil {
				fmt.Println("Error:", err)
				return
			}
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
	//se esce dal while ha vinto
	for i := 0; i < nPlayers; i++ {
		mainChan <- "finish"
	}
}

// Define giocatore
func launchPlayers(max int, id int, mainChan chan string, resultChannel chan Attempt) {
	wg.Add(1)
	var min int = 0
	for true {
		// Continuo finchè qualcuno non ha indovinato.
		message := <-mainChan
		if message == "Start" {
			var num = rand.Intn(max-min+1) + min
			// player# scrive nel canale il suo tentativo
			resultChannel <- Attempt{content: strconv.Itoa(num), id: id}
			// Attendo la risposta dall'oracolo.
			message := <-mainChan
			switch message {
			case "lower":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is greater")
				max = num - 1
				subWg.Done()

			case "greater":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is lower")
				min = num + 1
				subWg.Done()

			case "winnerNotMe":
				// Accade quando il numero è corretto ma è stato gestito dopo a un altro messaggio di vittoria.
				fmt.Println("[player: ", id, "] attemp: correct but too late")
				subWg.Done()
			default:
				fmt.Println("[player: ", id, "] attemp: ", num, " -> winner")
				subWg.Done()

			}
		} else if message == "finish" {
			fmt.Println("[player: ", id, "] -> finish")
			break
		}
	}
	defer wg.Done() // Segnalo il completamento.
}
