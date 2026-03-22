package kfs.tetris.sys;

import kfs.tetris.KfsConst;
import kfs.tetris.Tetromino;
import kfs.tetris.World;
import kfs.tetris.comp.*;
import kfs.tetris.ecs.Entity;
import kfs.tetris.ecs.KfsSystem;

public class SpawnSystem implements KfsSystem {

    private final World world;
    private float spawnDelay;
    private static final float SPAWN_WAIT = 0.15f;

    public SpawnSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        if (world.isGameOver() || world.isLevelComplete()) return;
        if (world.getActivePiece() != null) return;

        spawnDelay += delta;
        if (spawnDelay < SPAWN_WAIT) return;
        spawnDelay = 0;

        NextPieceComp next = world.getNextPiece();
        if (next == null) return;

        int[][] shape = next.shape;
        int spawnX = KfsConst.GRID_W / 2 - shape[0].length / 2;
        int spawnY = 0;

        // Adjust spawnY: try to start above the visible grid if needed
        // Find the first row with a filled cell
        for (int r = 0; r < shape.length; r++) {
            boolean hasCell = false;
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) { hasCell = true; break; }
            }
            if (hasCell) {
                spawnY = -r;
                break;
            }
        }

        if (!world.canPlace(shape, spawnX, spawnY)) {
            world.setGameOver(true);
            return;
        }

        Entity piece = world.spawnEntity();
        world.addComponent(piece, new ShapeComp(shape, spawnX, spawnY));
        world.addComponent(piece, new GravityComp(world.getGravityInterval()));
        world.addComponent(piece, new ActiveComp());

        // Generate next piece
        int nextType = Tetromino.randomType();
        next.shape = Tetromino.getShape(nextType);
        next.type = nextType;
    }
}
