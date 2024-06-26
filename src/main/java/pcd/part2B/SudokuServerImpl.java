package pcd.part2B;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SudokuServerImpl implements SudokuServer {
    private final Map<String, List<SudokuPlayer>> players = new ConcurrentHashMap<>();
    private final Map<String, String> authors = new ConcurrentHashMap<>();
    private int gridCounter = 0;
    private int playerCounter = 0;

    public SudokuServerImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public synchronized void createGrid() throws RemoteException {
        String idGrid = "grid-" + gridCounter++;
        Grid grid = new Grid(idGrid, SudokuGenerator.generateSudoku());
        // Crea ed esporta il player
        String idPlayer = "player-" + playerCounter++;
        SudokuPlayer player = new SudokuPlayerImpl(idPlayer, grid);
        // Ottiene il registro RMI
        Registry registry = LocateRegistry.getRegistry();
        // Registra il player
        registry.rebind(idPlayer, player);
        players.put(idGrid, new ArrayList<>(List.of(player)));
        authors.put(idGrid, idPlayer);
        player.startGUI();
    }

    @Override
    public synchronized void joinGrid(String idGrid) throws RemoteException {
        // Ottiene il registro RMI
        Registry registry = LocateRegistry.getRegistry();
        // Ottiene il player autore della griglia
        SudokuPlayer author = null;
        try {
            String idAuthor = authors.get(idGrid);
            author = (SudokuPlayer) registry.lookup(idAuthor);
            String idPlayer = "player-" + playerCounter++;
            SudokuPlayer player = new SudokuPlayerImpl(idPlayer, author.getGrid());
            players.get(idGrid).forEach(p -> {
                try {
                    p.notifyPlayerEntered(idPlayer);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            // Registra il player
            registry.rebind(idPlayer, player);
            players.get(idGrid).add(player);
            player.startGUI();
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Set<String> getGrids() throws RemoteException {
        return this.players.keySet();
    }

    @Override
    public synchronized void updatePlayers(Grid grid) throws RemoteException {
        this.players.get(grid.getId()).forEach(player -> {
            try {
                player.updateGrid(grid);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public synchronized void notifyPlayerExited(String idGrid, String idPlayer) throws RemoteException {
        // Rimuove il player dai player della partita
        List<SudokuPlayer> currentPlayers = this.players.get(idGrid);
        Optional<SudokuPlayer> player = currentPlayers.stream().filter(p -> {
            try {
                return p.getId().equals(idPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).findFirst();
        player.ifPresent(currentPlayers::remove);
        this.players.put(idGrid, currentPlayers);
        this.playerCounter--;

        // Notifica l'uscita del player dalla partita a ogni giocatore della partita stessa
        currentPlayers.forEach(p -> {
            try {
                p.notifyPlayerExited(idPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        // Nel caso in cui il giocatore uscito dalla griglia sia l'ultimo giocatore rimasto a partecipare cancello la partita
        if (this.players.get(idGrid).isEmpty()) {
            this.players.remove(idGrid);
            this.gridCounter--;
        }
        // Altrimenti, se il player uscente era l'autore della griglia va eletto un nuovo player della stessa partita come autore
        // E va rimpiazzato nel registro il vecchio Player autore col nuovo
        else if (authors.get(idGrid).equals(idPlayer)) {
            Random rand = new Random();
            SudokuPlayer newAuthor = currentPlayers.get(rand.nextInt(currentPlayers.size()));
            this.authors.put(idGrid, newAuthor.getId());
            // Notifica solo al nuovo player che è il nuovo author
            newAuthor.notifyNewAuthor(idPlayer);
        }
    }

    @Override
    public void notifyGameOver(String idGrid, String idPlayer) throws RemoteException {
        players.get(idGrid).forEach(player -> {
            try {
                player.notifyGameOver(idPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void notifyCheckSudokuNotCorrect(String idGrid, String idPlayer) throws RemoteException {
        players.get(idGrid).forEach(player -> {
            try {
                player.notifyCheckSudokuNotCorrect(idPlayer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
