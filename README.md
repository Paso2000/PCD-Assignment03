# PCD Assignment #03 - v 1.0-20240513
L’assignment verte sull'applicazione delle tecniche di programmazione concorrente e distribuita basate su scambio di 
messaggi viste nel moduli 3.*. Si compone di due macro parti, più una (terza parte) facoltativa.

## PRIMA PARTE
Nella prima macro parte si richiede di affrontare il problema illustrato nel primo assignment (entrambi i punti) 
utilizzando un approccio concorrente basato su attori.

## SECONDA PARTE

Nella seconda macro parte si introduce un nuovo problema, che deve essere affrontato in due modi diversi:
una soluzione basata su scambio di messaggi, scegliendo - in alternativa - o un approccio ad attori oppure basato su MOM;
una soluzione basata su oggetti distribuiti.

### Descrizione del problema
Si vuole realizzare una versione distribuita cooperativa del gioco del Sudoku ("Cooperative Sudoku").
L'applicazione deve permettere a giocatori in rete di poter creare dinamicamente nuove griglie da risolvere
e/o partecipare alla risoluzione di griglie già create ancora non risolte.

Per poter immettere o cambiare un valore in una casella, un giocatore deve prima selezionare la casella.
L'applicazione deve permettere a ogni giocatore di vedere – in modo consistente – sia lo stato della griglia, 
sia le caselle correntemente selezionate da parte degli altri giocatori.

Come requisito, si richiede di adottare una soluzione decentralizzata peer-to-peer, per cui:
sia possibile partecipare dinamicamente alla risoluzione di una griglia specificando l'indirizzo di un qualsiasi 
giocatore che sta già partecipando alla risoluzione [*]
qualsiasi giocatore possa entrare o uscire dinamicamente (anche a causa di crash), 
incluso chi ha creato la griglia.


[*] Nel caso si adotti un approccio basato su MOM, è possibile considerare – come punto di contatto – il nodo dove c'è 
il broker (o uno dei nodi dove c'è il broker, nel caso si considerino MOM che supportano forme di clustering/federazione).


Si richiede di sviluppare una soluzione in due modi diversi

primo modo usando un approccio basato su scambio di messaggi, usando attori distribuiti oppure MOM.
secondo modo utilizzando un approccio basato su Distributed Object Computing, utilizzando Java RMI.

## LA CONSEGNA

La consegna consiste in una cartella “Assignment-03” compressa (formato zip)  da sottoporre sul sito del corso, 
contenente tre sottodirectory diverse (part1, part2A e part2B, part3). Ogni sottodirectory deve contenere:
una directory src con i sorgenti del programma
una directory doc con breve relazione in PDF (report.pdf), che includa analisi del problema e  dell’architettura 
proposta, utilizzando i diagrammi ritenuti più efficaci allo scopo.
