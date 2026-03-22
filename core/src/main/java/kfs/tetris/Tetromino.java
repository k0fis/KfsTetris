package kfs.tetris;

public class Tetromino {

    // Shape definitions: 1-based color index used in cells
    // I=1, O=2, T=3, S=4, Z=5, L=6, J=7
    public static final int[][][] SHAPES = {
        {{1, 1, 1, 1}},                    // I
        {{2, 2}, {2, 2}},                  // O
        {{0, 3, 0}, {3, 3, 3}},            // T
        {{4, 4, 0}, {0, 4, 4}},            // S
        {{0, 5, 5}, {5, 5, 0}},            // Z
        {{6, 0, 0}, {6, 6, 6}},            // L
        {{0, 0, 7}, {7, 7, 7}},            // J
    };

    public static int[][] getShape(int type) {
        int[][] src = SHAPES[type];
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }

    public static int getColorIndex(int type) {
        return type + 1;
    }

    public static int[][] rotateCW(int[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - i - 1] = shape[i][j];
            }
        }
        return rotated;
    }

    public static int randomType() {
        return (int) (Math.random() * SHAPES.length);
    }
}
