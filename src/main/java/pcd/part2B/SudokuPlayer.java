package pcd.part2B;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SudokuPlayer extends Remote {

    String getId() throws RemoteException;

    Grid getGrid() throws RemoteException;

    void updateGrid(Grid grid) throws RemoteException;

    void notifyPlayerEntered(String idPlayer) throws RemoteException;

    void notifyPlayerExited(String idPlayer) throws RemoteException;

    void notifyNewAuthor(String idPlayer) throws RemoteException;
}
