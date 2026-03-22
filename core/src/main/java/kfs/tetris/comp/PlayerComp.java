package kfs.tetris.comp;

import kfs.tetris.ecs.KfsComp;

public class PlayerComp implements KfsComp {
    public int score;
    public int level;
    public int linesCleared;
    public int flashRemaining;

    public PlayerComp(int level, int flashRemaining) {
        this.score = 0;
        this.level = level;
        this.linesCleared = 0;
        this.flashRemaining = flashRemaining;
    }
}
