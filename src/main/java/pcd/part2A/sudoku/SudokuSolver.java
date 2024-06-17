package pcd.part2A.sudoku;

import pcd.part2A.Actors.GridActor;

public class SudokuSolver {
    public boolean isValidMove(GridActor grid, int row, int col, int value) {
        return !isInRow(grid, row, value) && !isInColumn(grid, col, value) && !isInBox(grid, row, col, value);
    }

    private boolean isInRow(GridActor grid, int row, int value) {
        for (int col = 0; col < GridActor.SIZE; col++) {
            if (grid.getCell(row, col).getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInColumn(GridActor grid, int col, int value) {
        for (int row = 0; row < GridActor.SIZE; row++) {
            if (grid.getCell(row, col).getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBox(GridActor grid, int row, int col, int value) {
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid.getCell(boxRowStart + i, boxColStart + j).getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean solve(GridActor grid) {
        for (int row = 0; row < GridActor.SIZE; row++) {
            for (int col = 0; col < GridActor.SIZE; col++) {
                if (grid.getCell(row, col).getValue() == 0) { // trova una cella vuota
                    for (int value = 1; value <= GridActor.SIZE; value++) {
                        if (isValidMove(grid, row, col, value)) {
                            grid.getCell(row, col).setValue(value);
                            if (solve(grid)) {
                                return true;
                            } else {
                                grid.getCell(row, col).setValue(0); // backtracking
                            }
                        }
                    }
                    return false; // nessun numero valido trovato, ritorna falso per backtracking
                }
            }
        }
        return true; // griglia completata
    }
}
