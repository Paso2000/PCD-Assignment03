package pcd.part2A;

import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final Random RANDOM = new Random();

    public static int[][] generateSudoku() {
        int[][] grid = new int[SIZE][SIZE];
        fillGrid(grid);
        removeNumbers(grid, 60); // Rimuove circa 40 numeri per creare il puzzle
        return grid;
    }

    private static boolean fillGrid(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    int[] numbers = generateRandomNumbers();
                    for (int num : numbers) {
                        if (isSafe(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (fillGrid(grid)) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static int[] generateRandomNumbers() {
        int[] numbers = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = i + 1;
        }
        for (int i = SIZE - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }
        return numbers;
    }

    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (grid[row][x] == num || grid[x][col] == num ||
                    grid[row - row % 3 + x / 3][col - col % 3 + x % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private static void removeNumbers(int[][] grid, int count) {
        int removed = 0;
        while (removed < count) {
            int row = new Random().nextInt(SIZE);
            int col = new Random().nextInt(SIZE);
            if (grid[row][col] != 0) {
                grid[row][col] = 0;
                removed++;
            }
        }
    }


}


