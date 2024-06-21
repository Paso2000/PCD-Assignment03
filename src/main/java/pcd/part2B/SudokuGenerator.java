package pcd.part2B;
import java.util.*;

public class SudokuGenerator {
    private int[][] board;
    private static final int SIZE = 9;

    public SudokuGenerator() {
        board = new int[SIZE][SIZE];
    }

    public boolean generateBoard() {
        fillDiagonalRegions();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    ArrayList<Integer> numbers = new ArrayList<>();
                    for (int num = 1; num <= SIZE; num++) {
                        if (isSafe(i, j, num)) {
                            numbers.add(num);
                        }
                    }
                    Collections.shuffle(numbers);
                    for (int num : numbers) {
                        if (isSafe(i, j, num)) {
                            board[i][j] = num;
                            if (generateBoard()) {
                                return true;
                            } else {
                                board[i][j] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        removeElements(20);
        return true;
    }

    private void fillDiagonalRegions() {
        for (int i = 0; i < SIZE; i = i + 3) {
            fillBox(i, i);
        }
    }

    private void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                do {
                    num = randomGenerator(SIZE);
                }
                while (!isSafe(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    private boolean isSafe(int row, int col, int num) {
        return (unUsedInRow(row, num) &&
                unUsedInCol(col, num) &&
                unUsedInBox(row - row % 3, col - col % 3, num));
    }

    private boolean unUsedInRow(int i, int num) {
        for (int j = 0; j < SIZE; j++) {
            if (board[i][j] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean unUsedInCol(int j, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][j] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[rowStart + i][colStart + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeElements(int num) {
        int count = 0;
        while (count < num) {
            int cellId = randomGenerator(SIZE * SIZE);

            int i = (cellId / SIZE);
            int j = cellId % 9;
            if (j != 0) {
                j = j - 1;
            }

            if (board[i][j] != 0) {
                count++;
                board[i][j] = 0;
            }
        }
    }
}
