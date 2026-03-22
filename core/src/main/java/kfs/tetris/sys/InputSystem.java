package kfs.tetris.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import kfs.tetris.SoundManager;
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
    private boolean spaceWasPressed;

    // Touch state
    private boolean touchRotateTriggered;
    private boolean touchDropTriggered;

    static final int BTN_H = 64;
    static final int BTN_GAP = 4;
    static final int BTN_ZONE = BTN_H + BTN_GAP * 2;

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

        boolean movedByTouch = false;
        boolean rotateByTouch = false;
        boolean dropByTouch = false;

        // Touch controls
        if (Gdx.input.isTouched()) {
            int tx = Gdx.input.getX();
            int ty = Gdx.graphics.getHeight() - Gdx.input.getY();
            int sw = Gdx.graphics.getWidth();

            if (ty < BTN_ZONE) {
                int zone = tx * 4 / sw;
                if (zone == 0) {
                    // LEFT
                    movedByTouch = true;
                    if (moveTimer <= 0) {
                        if (world.canPlace(shape.shape, shape.x - 1, shape.y)) {
                            shape.x--;
                        }
                        moveTimer = MOVE_DELAY;
                    }
                } else if (zone == 3) {
                    // RIGHT
                    movedByTouch = true;
                    if (moveTimer <= 0) {
                        if (world.canPlace(shape.shape, shape.x + 1, shape.y)) {
                            shape.x++;
                        }
                        moveTimer = MOVE_DELAY;
                    }
                } else if (zone == 1 && !touchRotateTriggered) {
                    rotateByTouch = true;
                    touchRotateTriggered = true;
                } else if (zone == 2 && !touchDropTriggered) {
                    dropByTouch = true;
                    touchDropTriggered = true;
                }
            }
        } else {
            touchRotateTriggered = false;
            touchDropTriggered = false;
        }

        // Keyboard: Left
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (moveTimer <= 0) {
                if (world.canPlace(shape.shape, shape.x - 1, shape.y)) {
                    shape.x--;
                }
                moveTimer = MOVE_DELAY;
            }
        }
        // Keyboard: Right
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (moveTimer <= 0) {
                if (world.canPlace(shape.shape, shape.x + 1, shape.y)) {
                    shape.x++;
                }
                moveTimer = MOVE_DELAY;
            }
        } else if (!movedByTouch) {
            moveTimer = 0;
        }

        // Rotate (keyboard or touch)
        boolean keyRotate = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.Z);
        if (keyRotate || rotateByTouch) {
            int[][] rotated = Tetromino.rotateCW(shape.shape);
            boolean rotated_ok = false;
            if (world.canPlace(rotated, shape.x, shape.y)) {
                shape.shape = rotated;
                rotated_ok = true;
            } else if (world.canPlace(rotated, shape.x - 1, shape.y)) {
                shape.shape = rotated;
                shape.x--;
                rotated_ok = true;
            } else if (world.canPlace(rotated, shape.x + 1, shape.y)) {
                shape.shape = rotated;
                shape.x++;
                rotated_ok = true;
            }
            if (rotated_ok) {
                SoundManager s = world.getSounds();
                if (s != null) s.playRotate();
            }
        }

        // Soft drop
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            gravity.interval = 0.03f;
        } else {
            gravity.interval = world.getGravityInterval();
        }

        // Hard drop (keyboard or touch)
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if ((spacePressed && !spaceWasPressed) || dropByTouch) {
            while (world.canPlace(shape.shape, shape.x, shape.y + 1)) {
                shape.y++;
            }
            world.lockPiece(shape.shape, shape.x, shape.y);
            world.deleteEntity(active);
            SoundManager s = world.getSounds();
            if (s != null) s.playHardDrop();
        }
        spaceWasPressed = spacePressed;
    }
}
