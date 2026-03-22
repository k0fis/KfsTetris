package kfs.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import kfs.tetris.*;
import kfs.tetris.comp.*;
import kfs.tetris.sys.*;

public class TetrisScreen extends ScreenAdapter {

    private final KfsMain game;
    private final String mapPath;
    private final World world;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont fontSmall;
    private BitmapFont fontMiddle;
    private Texture blockTexture;
    private float flashTimer;

    private int screenW, screenH;
    private int cellSize;
    private int gridOffsetX, gridOffsetY;

    public TetrisScreen(KfsMain game, String mapPath) {
        this.game = game;
        this.mapPath = mapPath;

        world = new World();
        int level = game.getMapIndex(mapPath);
        world.initGame(level, mapPath);

        world.addSys(new InputSystem(world));
        world.addSys(new GravitySystem(world));
        world.addSys(new RowClearSystem(world));
        world.addSys(new LevelCheckSystem(world));
        world.addSys(new SpawnSystem(world));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontSmall = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-10.fnt"));
        fontMiddle = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-16.fnt"));

        // Create a simple white block texture
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        blockTexture = new Texture(pix);
        pix.dispose();

        updateLayout();
    }

    private void updateLayout() {
        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();

        // Calculate cell size to fit grid in ~60% of screen width
        int gridAreaW = (int) (screenW * 0.6f);
        int gridAreaH = screenH - 20;

        cellSize = Math.min(gridAreaW / KfsConst.GRID_W, gridAreaH / KfsConst.GRID_H);
        if (cellSize < 4) cellSize = 4;

        int gridPixelW = cellSize * KfsConst.GRID_W;
        int gridPixelH = cellSize * KfsConst.GRID_H;

        gridOffsetX = 10;
        gridOffsetY = (screenH - gridPixelH) / 2;
    }

    @Override
    public void resize(int width, int height) {
        updateLayout();
    }

    @Override
    public void render(float delta) {
        flashTimer += delta;

        // Update
        if (!world.isGameOver() && !world.isLevelComplete()) {
            world.update(delta);
        }

        // Check end conditions
        if (world.isLevelComplete()) {
            game.setScreen(new GameOverScreen(game, world.getScore(), mapPath));
            return;
        }
        if (world.isGameOver()) {
            game.setScreen(new LevelDoneScreen(game, mapPath));
            return;
        }

        // Render
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw grid background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.15f, 1f);
        shapeRenderer.rect(gridOffsetX, gridOffsetY, cellSize * KfsConst.GRID_W, cellSize * KfsConst.GRID_H);
        shapeRenderer.end();

        // Draw grid border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(gridOffsetX - 1, gridOffsetY - 1,
            cellSize * KfsConst.GRID_W + 2, cellSize * KfsConst.GRID_H + 2);
        shapeRenderer.end();

        batch.begin();

        // Draw locked blocks
        for (int y = 0; y < KfsConst.GRID_H; y++) {
            for (int x = 0; x < KfsConst.GRID_W; x++) {
                if (world.grid[y][x] != 0) {
                    Color c = KfsConst.getColor(world.grid[y][x]);
                    if (world.flashGrid[y][x]) {
                        // Flash: pulsating alpha
                        float pulse = 0.5f + 0.5f * (float) Math.sin(flashTimer * 6.0);
                        batch.setColor(c.r, c.g, c.b, pulse);
                    } else {
                        batch.setColor(c);
                    }
                    drawCell(x, y);
                }
            }
        }

        // Draw active piece
        ShapeComp active = world.getActiveShape();
        if (active != null) {
            // Draw ghost piece first
            int ghostY = active.y;
            while (world.canPlace(active.shape, active.x, ghostY + 1)) {
                ghostY++;
            }
            if (ghostY != active.y) {
                for (int r = 0; r < active.shape.length; r++) {
                    for (int c2 = 0; c2 < active.shape[r].length; c2++) {
                        if (active.shape[r][c2] != 0) {
                            Color gc = KfsConst.getColor(active.shape[r][c2]);
                            batch.setColor(gc.r, gc.g, gc.b, 0.25f);
                            drawCell(active.x + c2, ghostY + r);
                        }
                    }
                }
            }

            // Draw actual piece
            for (int r = 0; r < active.shape.length; r++) {
                for (int c2 = 0; c2 < active.shape[r].length; c2++) {
                    if (active.shape[r][c2] != 0) {
                        batch.setColor(KfsConst.getColor(active.shape[r][c2]));
                        drawCell(active.x + c2, active.y + r);
                    }
                }
            }
        }

        batch.setColor(Color.WHITE);

        // Draw HUD
        drawHUD();

        batch.end();

        // Draw grid lines
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 0.5f);
        for (int x = 0; x <= KfsConst.GRID_W; x++) {
            shapeRenderer.line(gridOffsetX + x * cellSize, gridOffsetY,
                gridOffsetX + x * cellSize, gridOffsetY + KfsConst.GRID_H * cellSize);
        }
        for (int y = 0; y <= KfsConst.GRID_H; y++) {
            shapeRenderer.line(gridOffsetX, gridOffsetY + y * cellSize,
                gridOffsetX + KfsConst.GRID_W * cellSize, gridOffsetY + y * cellSize);
        }
        shapeRenderer.end();
    }

    private void drawCell(int gx, int gy) {
        if (gy < 0) return;
        int px = gridOffsetX + gx * cellSize;
        // Y is flipped: grid row 0 = top of screen
        int py = gridOffsetY + (KfsConst.GRID_H - 1 - gy) * cellSize;
        int margin = Math.max(1, cellSize / 8);
        batch.draw(blockTexture, px + margin, py + margin, cellSize - margin * 2, cellSize - margin * 2);
    }

    private void drawHUD() {
        PlayerComp player = world.getPlayer();
        if (player == null) return;

        int hudX = gridOffsetX + KfsConst.GRID_W * cellSize + 20;
        int hudY = screenH - 30;

        // NEXT piece preview
        fontSmall.setColor(Color.GRAY);
        fontSmall.draw(batch, "NEXT", hudX, hudY);
        hudY -= 20;

        NextPieceComp next = world.getNextPiece();
        if (next != null) {
            int previewCellSize = Math.max(8, cellSize * 3 / 4);
            for (int r = 0; r < next.shape.length; r++) {
                for (int c = 0; c < next.shape[r].length; c++) {
                    if (next.shape[r][c] != 0) {
                        batch.setColor(KfsConst.getColor(next.shape[r][c]));
                        int px = hudX + c * previewCellSize;
                        int py = hudY - r * previewCellSize;
                        int m = Math.max(1, previewCellSize / 8);
                        batch.draw(blockTexture, px + m, py - previewCellSize + m,
                            previewCellSize - m * 2, previewCellSize - m * 2);
                    }
                }
            }
            batch.setColor(Color.WHITE);
            hudY -= (next.shape.length + 1) * Math.max(8, cellSize * 3 / 4);
        }

        hudY -= 10;
        fontSmall.setColor(Color.GRAY);
        fontSmall.draw(batch, "SCORE", hudX, hudY);
        hudY -= 18;
        fontMiddle.setColor(Color.WHITE);
        fontMiddle.draw(batch, String.valueOf(player.score), hudX, hudY);
        hudY -= 30;

        fontSmall.setColor(Color.GRAY);
        fontSmall.draw(batch, "LEVEL", hudX, hudY);
        hudY -= 18;
        fontMiddle.setColor(Color.WHITE);
        fontMiddle.draw(batch, String.valueOf(player.level), hudX, hudY);
        hudY -= 30;

        fontSmall.setColor(Color.GRAY);
        fontSmall.draw(batch, "LINES", hudX, hudY);
        hudY -= 18;
        fontMiddle.setColor(Color.WHITE);
        fontMiddle.draw(batch, String.valueOf(player.linesCleared), hudX, hudY);
        hudY -= 30;

        fontSmall.setColor(Color.GRAY);
        fontSmall.draw(batch, "FLASH", hudX, hudY);
        hudY -= 18;
        float flashPulse = 0.5f + 0.5f * (float) Math.sin(flashTimer * 4.0);
        if (player.flashRemaining > 0) {
            fontMiddle.setColor(1f, flashPulse, 0f, 1f);
        } else {
            fontMiddle.setColor(Color.LIME);
        }
        fontMiddle.draw(batch, String.valueOf(player.flashRemaining), hudX, hudY);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (fontSmall != null) fontSmall.dispose();
        if (fontMiddle != null) fontMiddle.dispose();
        if (blockTexture != null) blockTexture.dispose();
    }
}
