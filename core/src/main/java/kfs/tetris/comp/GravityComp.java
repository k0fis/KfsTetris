package kfs.tetris.comp;

import kfs.tetris.ecs.KfsComp;

public class GravityComp implements KfsComp {
    public float interval;
    public float timer;

    public GravityComp(float interval) {
        this.interval = interval;
        this.timer = 0;
    }

    public boolean tick(float delta) {
        timer += delta;
        if (timer >= interval) {
            timer = 0;
            return true;
        }
        return false;
    }
}
