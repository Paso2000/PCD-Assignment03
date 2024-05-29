package pcd.part2A.sudoku;

public class Cell {
    private int value;
    private boolean isFixed;

    public Cell(int value, boolean isFixed) {
        this.value = value;
        this.isFixed = isFixed;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (!isFixed) {
            this.value = value;
        }
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    @Override
    public String toString() {
        return isFixed ? "[" + value + "]" : " " + value + " ";
    }
}
