package kfs.tetris.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import kfs.tetris.Tetromino;
import kfs.tetris.World;
import kfs.tetris.comp.GravityComp;
import kfs.tetris.comp.ShapeComp;
import kfs.tetris.ecs.Entity;
import kfs.tetris.ecs.KfsSystem;

public class InputSystem implements KfsSystem {

    private final World world;
    private float moveTimer;
    private static final float MOVE_DELAY = 0.12f;
    private boolean hardDropped;
    private boolean spaceWasPressed;

    public InputSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        Entity active = world.getActivePiece();
        if (active == null) return;

        ShapeComp shape = world.getComponent(active, ShapeComp.class);
        GravityComp gravity = world.getComponent(active, GravityComp.class);
        if (shape == null || gravity == null) return;

        moveTimer -= delta;

        // Left
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (moveTimer <= 0) {
                if (world.canPlace(shape.shape, shape.x - 1, shape.y)) {
                    shape.x--;
                }
                moveTimer = MOVE_DELAY;
            }
        }
        // Right
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (moveTimer <= 0) {
                if (world.canPlace(shape.shape, shape.x + 1, shape.y)) {
                    shape.x++;
                }
                moveTimer = MOVE_DELAY;
            }
        } else {
            moveTimer = 0;
        }

        // Rotate (Up or Z)
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            int[][] rotated = Tetromino.rotateCW(shape.shape);
            if (world.canPlace(rotated, shape.x, shape.y)) {
                shape.shape = rotated;
            } else if (world.canPlace(rotated, shape.x - 1, shape.y)) {
                shape.shape = rotated;
                shape.x--;
            } else if (world.canPlace(rotated, shape.x + 1, shape.y)) {
                shape.shape = rotated;
                shape.x++;
            }
        }

        // Soft drop
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            gravity.interval = 0.03f;
        } else {
            gravity.interval = world.getGravityInterval();
        }

        // Hard drop
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (spacePressed && !spaceWasPressed) {
            while (world.canPlace(shape.shape, shape.x, shape.y + 1)) {
                shape.y++;
            }
            world.lockPiece(shape.shape, shape.x, shape.y);
            world.deleteEntity(active);
        }
        spaceWasPressed = spacePressed;
    }
}
