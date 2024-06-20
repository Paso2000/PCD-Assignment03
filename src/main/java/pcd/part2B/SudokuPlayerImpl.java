package pcd.part2B;

import pcd.part2B.GUI.SudokuGUI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SudokuPlayerImpl implements SudokuPlayer, Serializable {

    String id;

    Grid grid;

    boolean isAuthor;

    SudokuGUI gui;

    SudokuPlayerImpl(String id, Grid grid, boolean isAuthor, SudokuServer server) throws RemoteException {
        this.id = id;
        this.grid = grid;
        this.isAuthor = isAuthor;
        this.gui = new SudokuGUI(grid, server);
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
    public synchronized boolean isAuthor() throws RemoteException {
        return isAuthor;
    }

    @Override
    public synchronized void updateGrid(Grid grid) throws RemoteException {
        this.gui.updateGrid(grid);
    }
}
