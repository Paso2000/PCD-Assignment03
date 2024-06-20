package pcd.part2B;

import pcd.part2B.GUI.StartGUI;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerLauncher {

    public static void main(String[] args) {
        try {
            // Ottiene il registro RMI
            Registry registry = LocateRegistry.getRegistry();

            // Crea il server e lo esporta
            SudokuServer server = new SudokuServerImpl();

            // Registra il server
            registry.rebind("SudokuServer", server);

            System.out.println("Server launched.");

            // Mostra la finestra iniziale
            new StartGUI(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
