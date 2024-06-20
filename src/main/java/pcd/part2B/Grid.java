package pcd.part2B;

import java.io.Serial;
import java.io.Serializable;

public class Grid implements Serializable {

    private final String id;
    private final int[][] grid;
    private final boolean[][] selected;

    public Grid(String id) {
        this.id = id;
        this.grid = new int[9][9];
        this.selected = new boolean[9][9];
    }

    public void changeValue(int row, int column, int value) {
        grid[row][column] = value;
    }

    public void deleteValue(int row, int column) {
        grid[row][column] = 0;
    }

    public void selectCell(int row, int column) {
        selected[row][column] = true;
    }

    public void deselectCell(int row, int column) {
        selected[row][column] = false;
    }

    public int getValue(int row, int column) {
        return grid[row][column];
    }

    public boolean isSelected(int row, int column) {
        return selected[row][column];
    }

    public String getId() {
        return id;
    }
}
