package kfs.tetris.sys;

import kfs.tetris.World;
import kfs.tetris.comp.PlayerComp;
import kfs.tetris.ecs.KfsSystem;

public class LevelCheckSystem implements KfsSystem {

    private final World world;

    public LevelCheckSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        if (world.isGameOver() || world.isLevelComplete()) return;
        if (world.getActivePiece() != null) return;

        PlayerComp player = world.getPlayer();
        if (player == null) return;

        if (player.flashRemaining <= 0) {
            // Level complete bonus
            player.score += 50;
            world.setLevelComplete(true);
        }
    }
}
