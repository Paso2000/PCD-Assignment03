package pcd.part2B;

import pcd.part2B.GUI.SudokuGUI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SudokuPlayerImpl implements SudokuPlayer, Serializable {

    String id;

    Grid grid;

    SudokuGUI gui;

    SudokuPlayerImpl(String id, Grid grid) throws RemoteException {
        this.id = id;
        this.grid = grid;
        this.gui = new SudokuGUI(this);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public synchronized String getId() throws RemoteException {
        return id;
    }

    @Override
    public synchronized Grid getGrid() throws RemoteException {
        return grid;
    }

    @Override
    public synchronized void updateGrid(Grid grid) throws RemoteException {
        this.grid = grid;
        this.gui.updateGrid(grid);
    }

    @Override
    public void notifyPlayerEntered(String idPlayer) throws RemoteException {
        this.gui.notifyPlayerEntered(idPlayer);
    }

    @Override
    public void notifyPlayerExited(String idPlayer) throws RemoteException {
        this.gui.notifyPlayerExited(idPlayer);
    }

    @Override
    public void notifyNewAuthor(String idPlayer) throws RemoteException {
        this.gui.notifyNewAuthor(idPlayer);
    }
}
