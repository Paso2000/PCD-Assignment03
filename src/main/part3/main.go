package main

import (
	"fmt"
	"math/rand"
	"strconv"
)

// Globale, (le sync non si passano per riferimento)

type Attempt struct {
	content string
	id      int // Id del player.
}

func main() {

	var nPlayers = 5
	var max = 100
	mainChan := make(chan string)     //canale principale dove si scambiano lo stato
	attemptChan := make(chan Attempt) //canale dei risultati dove si scambiano i tentativi
	roundChan := make(chan string)    //canale per la sincronizzazione dei turni
	gameChan := make(chan bool)       //canale per la sincronizzazione della partita

	go launchOracle(max, nPlayers, mainChan, attemptChan, roundChan)
	for i := 0; i < nPlayers; i++ {
		go launchPlayers(max, i, mainChan, attemptChan, roundChan, gameChan)
	}
	for i := 0; i < nPlayers; i++ {
		<-gameChan
	}
}

// Define l'oracolo
func launchOracle(max int, nPlayers int, mainChan chan string, attemptChan chan Attempt, roundChan chan string) {
	secretNumber := rand.Intn(max + 1)
	isGuessed := false
	for !isGuessed {
		//fa partire i player
		for i := 0; i < nPlayers; i++ {
			mainChan <- "Start"
		}
		// Gestiamo i messaggi dei numeri proposti, per ciascun player.
		for i := 0; i < nPlayers; i++ {
			attempt := <-attemptChan
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
				mainChan <- "winnerLate"
			} else {
				mainChan <- "winner"
				isGuessed = true
			}
		}
		for i := 0; i < nPlayers; i++ {
			<-roundChan
		}
		fmt.Println("Fine di un turno.")

	}
	//se esce dal while ha vinto
	for i := 0; i < nPlayers; i++ {
		mainChan <- "finish"
	}
}

// Define giocatore
func launchPlayers(max int, id int, mainChan chan string, attemptChan chan Attempt, roundChan chan string, gameChan chan bool) {
	var min int = 0
	for true {
		// Continuo finchè qualcuno non ha indovinato.
		message := <-mainChan
		if message == "Start" {
			var num = rand.Intn(max-min+1) + min
			// player# scrive nel canale il suo tentativo
			attemptChan <- Attempt{content: strconv.Itoa(num), id: id}
			// Attendo la risposta dall'oracolo.
			message := <-mainChan
			switch message {
			case "lower":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is greater")
				max = num - 1

			case "greater":
				fmt.Println("[player: ", id, "] attemp: ", num, " -> secret number is lower")
				min = num + 1

			case "winnerLate":
				// Accade quando il numero è corretto ma è stato gestito dopo a un altro messaggio di vittoria.
				fmt.Println("[player: ", id, "] attemp: correct but too late")

			default:
				fmt.Println("[player: ", id, "] attemp: ", num, " -> winner")
			}
			roundChan <- "turno finito"
		} else if message == "finish" {
			fmt.Println("[player: ", id, "] -> finish")
			gameChan <- true
			break
		}
	}
}
