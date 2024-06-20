package pcd.part2B;

import java.awt.*;
import java.lang.module.ModuleReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SudokuServerImpl implements SudokuServer {
    private final Map<String, SudokuPlayer> authors = new ConcurrentHashMap<>();
    private final Map<String, List<SudokuPlayer>> players = new ConcurrentHashMap<>();
    private int gridCounter = 0;
    private int playerCounter = 0;
    //private Set<Color> assignedColors;

    public SudokuServerImpl() throws RemoteException {
        //this.assignedColors = new HashSet<>();
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public synchronized void createPlayer() throws RemoteException {
        String idGrid = "grid-" + gridCounter++;
        Grid grid = new Grid(idGrid);
        // Crea ed esporta il player
        String idPlayer = "player-" + playerCounter++;
        SudokuPlayer player = new SudokuPlayerImpl(idPlayer, grid, true, this);
        // Ottiene il registro RMI
        Registry registry = LocateRegistry.getRegistry();
        // Registra il server
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
            SudokuPlayer player = new SudokuPlayerImpl(idPlayer, author.getGrid(), false, this);
            players.get(idGrid).add(player);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getGrids() throws RemoteException {
        return this.players.keySet();
    }

    @Override
    public void updatePlayers(Grid grid) throws RemoteException {
        this.players.get(grid.getId()).forEach(player -> {
            try {
                player.updateGrid(grid);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
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
