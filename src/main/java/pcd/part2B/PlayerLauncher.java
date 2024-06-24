package pcd.part2B;

import pcd.part2B.GUI.StartGUI;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PlayerLauncher {

    public static void main(String[] args) {
        try {
            // Ottiene il registro RMI
            Registry registry = LocateRegistry.getRegistry();

            // Ottiene il server
            SudokuServer server = (SudokuServer) registry.lookup(SudokuServer.SERVER_NAME);

            // Lancia la GUI iniziale
            StartGUI gui = new StartGUI(server);
            gui.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
