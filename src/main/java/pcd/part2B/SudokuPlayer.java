package pcd.part2B;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SudokuPlayer extends Remote {

    String getId() throws RemoteException;

    Grid getGrid() throws RemoteException;

    boolean isAuthor() throws RemoteException;

    void updateGrid(Grid grid) throws RemoteException;
}
