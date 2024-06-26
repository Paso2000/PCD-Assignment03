package pcd.part2B;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface SudokuServer extends Remote {

    // Nome costante del server da usare per il binding nel registro RMI
    String SERVER_NAME = "SudokuServer";

    void createGrid() throws RemoteException;

    void joinGrid(String idGrid) throws RemoteException;

    Set<String> getGrids() throws RemoteException;

    void updatePlayers(Grid grid) throws RemoteException;

    void notifyPlayerExited(String idGrid, String idPlayer) throws RemoteException;

    void notifyGameOver(String idGrid, String idPlayer) throws RemoteException;

    void notifyCheckSudokuNotCorrect(String idGrid, String idPlayer) throws RemoteException;
}
