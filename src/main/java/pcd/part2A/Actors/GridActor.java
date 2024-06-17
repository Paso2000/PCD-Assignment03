package pcd.part2A.Actors;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.GridActorContext;
import pcd.part2A.sudoku.Cell;

public class GridActor extends AbstractBehavior<GridActorContext> {
    private Cell[][] grid;
    public static final int SIZE = 9;

    public GridActor() {
        super();
        grid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell(0, false); // inizialmente tutte le celle sono vuote e non fisse
            }
        }
    }

    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, int value, boolean isFixed) {
        grid[row][col].setValue(value);
        grid[row][col].setFixed(isFixed);
    }

    public boolean isComplete() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j].getValue() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(grid[i][j].toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Receive<GridActorContext> createReceive() {
        return null;
    }
}

