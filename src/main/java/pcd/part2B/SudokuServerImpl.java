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
    private final Map<String, SudokuPlayer> authors = new ConcurrentHashMap<>();
    private int gridCounter = 0;
    private int playerCounter = 0;
    //private Set<Color> assignedColors;

    public SudokuServerImpl() throws RemoteException {
        //this.assignedColors = new HashSet<>();
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public synchronized void createGrid() throws RemoteException {
        String idGrid = "grid-" + gridCounter++;
        Grid grid = new Grid(idGrid);
        // Crea ed esporta il player
        String idPlayer = "player-" + playerCounter++;
        SudokuPlayer player = new SudokuPlayerImpl(idPlayer, grid);
        // Ottiene il registro RMI
        Registry registry = LocateRegistry.getRegistry();
        // Registra il player con la chiave della griglia
        registry.rebind(idGrid, player);
        players.put(idGrid, new ArrayList<>(List.of(player)));
        authors.put(idGrid, player);
    }

    @Override
    public synchronized void joinGrid(String idGrid) throws RemoteException {
        // Ottiene il registro RMI
        Registry registry = LocateRegistry.getRegistry();
        // Ottiene il player autore della griglia
        SudokuPlayer author = null;
        try {
            author = (SudokuPlayer) registry.lookup(idGrid);
            String idPlayer = "player-" + playerCounter++;
            SudokuPlayer player = new SudokuPlayerImpl(idPlayer, author.getGrid());
            players.get(idGrid).forEach(p -> {
                try {
                    p.notifyPlayerEntered(idPlayer);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            players.get(idGrid).add(player);
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
        else if (authors.get(idGrid).getId().equals(idPlayer)) {
            Random rand = new Random();
            SudokuPlayer newAuthor = currentPlayers.get(rand.nextInt(currentPlayers.size()));
            this.authors.put(idGrid, newAuthor);
            // Ottiene il registro RMI
            Registry registry = LocateRegistry.getRegistry();
            // Rimpiazza sul registro il player author utilizzando la chiave della griglia
            registry.rebind(idGrid, newAuthor);
            // Notifica solo al nuovo player che è il nuovo author
            newAuthor.notifyNewAuthor(idPlayer);
        }
    }

    /*
    private Color getRandomColor() {
        // Crea un array con tutti i colori predefiniti
        Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
                Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE,
                Color.PINK, Color.RED, Color.WHITE, Color.YELLOW};

        // Crea un nuovo oggetto Random
        Random rand = new Random();

        // Seleziona un colore casuale dall'array
        return colors[rand.nextInt(colors.length)];
    }
    */
}
