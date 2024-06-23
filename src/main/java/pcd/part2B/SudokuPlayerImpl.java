package pcd.part2B;

import pcd.part2B.GUI.SudokuGUI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SudokuPlayerImpl implements SudokuPlayer {

    String id;

    Grid grid;

    SudokuGUI gui;

    SudokuPlayerImpl(String id, Grid grid) throws RemoteException {
        this.id = id;
        this.grid = grid;
        this.gui = new SudokuGUI(id);
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public void startGUI() throws RemoteException {
        this.gui.startGUI();
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

    @Override
    public void notifyGameOver(String idPlayer) throws RemoteException {
        this.gui.notifyGameOver(idPlayer);
    }

    @Override
    public void notifyCheckSudokuNotCorrect(String idPlayer) throws RemoteException {
        this.gui.notifyCheckSudokuNotCorrect(idPlayer);
    }
}
