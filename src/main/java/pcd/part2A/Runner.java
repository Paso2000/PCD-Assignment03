package pcd.part2A;

import java.awt.*;

public class Runner {
    //to run gui
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
    //to run solver
    /*public static void main(String[] args) {
        SudokuGrid grid = new SudokuGrid();

        // Imposta una griglia di esempio (puoi cambiarla con qualsiasi altra griglia)
        int[][] exampleGrid = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        // Popola la griglia con i valori dell'esempio, marcando le celle predefinite come fisse
        for (int row = 0; row < SudokuGrid.SIZE; row++) {
            for (int col = 0; col < SudokuGrid.SIZE; col++) {
                if (exampleGrid[row][col] != 0) {
                    grid.setCell(row, col, exampleGrid[row][col], true);
                }
            }
        }

        System.out.println("Griglia iniziale:");
        System.out.println(grid);

        SudokuSolver solver = new SudokuSolver();
        if (solver.solve(grid)) {
            System.out.println("Griglia risolta:");
            System.out.println(grid);
        } else {
            System.out.println("Impossibile risolvere la griglia.");
        }
    }
*/
}
