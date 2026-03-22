package kfs.tetris.comp;

import kfs.tetris.ecs.KfsComp;

public class ShapeComp implements KfsComp {
    public int[][] shape;
    public int x, y;

    public ShapeComp(int[][] shape, int x, int y) {
        this.shape = shape;
        this.x = x;
        this.y = y;
    }
}
