package kfs.tetris.comp;

import kfs.tetris.ecs.KfsComp;

public class NextPieceComp implements KfsComp {
    public int[][] shape;
    public int type;

    public NextPieceComp(int[][] shape, int type) {
        this.shape = shape;
        this.type = type;
    }
}
