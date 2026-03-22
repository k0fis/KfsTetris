package kfs.tetris.sys;

import kfs.tetris.SoundManager;
import kfs.tetris.World;
import kfs.tetris.comp.ActiveComp;
import kfs.tetris.comp.GravityComp;
import kfs.tetris.comp.ShapeComp;
import kfs.tetris.ecs.Entity;
import kfs.tetris.ecs.KfsSystem;

public class GravitySystem implements KfsSystem {

    private final World world;

    public GravitySystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        Entity active = world.getActivePiece();
        if (active == null) return;

        GravityComp gravity = world.getComponent(active, GravityComp.class);
        ShapeComp shape = world.getComponent(active, ShapeComp.class);
        if (gravity == null || shape == null) return;

        if (gravity.tick(delta)) {
            if (world.canPlace(shape.shape, shape.x, shape.y + 1)) {
                shape.y++;
            } else {
                // Lock piece
                world.lockPiece(shape.shape, shape.x, shape.y);
                world.deleteEntity(active);
                SoundManager s = world.getSounds();
                if (s != null) s.playDrop();
            }
        }
    }
}
