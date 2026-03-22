package kfs.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kfs.tetris.comp.*;
import kfs.tetris.ecs.Entity;
import kfs.tetris.ecs.KfsWorld;

import java.util.List;

public class World extends KfsWorld {

    public final int[][] grid = new int[KfsConst.GRID_H][KfsConst.GRID_W];
    public final boolean[][] flashGrid = new boolean[KfsConst.GRID_H][KfsConst.GRID_W];

    private Entity playerEntity;
    private Entity nextPieceEntity;
    private boolean gameOver;
    private boolean levelComplete;
    private float gravityInterval = 0.8f;
    private SoundManager sounds;

    public World() {
        super();
    }

    public void setSounds(SoundManager sounds) {
        this.sounds = sounds;
    }

    public SoundManager getSounds() {
        return sounds;
    }

    public void initGame(int level, String mapFile, int initialScore) {
        reset();
        clearGrid();
        gameOver = false;
        levelComplete = false;

        int flashCount = 0;
        if (mapFile != null) {
            flashCount = loadLevel(mapFile);
        }

        playerEntity = createEntity();
        PlayerComp player = new PlayerComp(level, flashCount);
        player.score = initialScore;
        addComponent(playerEntity, player);

        int nextType = Tetromino.randomType();
        nextPieceEntity = createEntity();
        addComponent(nextPieceEntity, new NextPieceComp(Tetromino.getShape(nextType), nextType));
    }

    private void clearGrid() {
        for (int y = 0; y < KfsConst.GRID_H; y++) {
            for (int x = 0; x < KfsConst.GRID_W; x++) {
                grid[y][x] = 0;
                flashGrid[y][x] = false;
            }
        }
    }

    public int loadLevel(String mapFile) {
        FileHandle file = Gdx.files.internal(mapFile);
        if (!file.exists()) {
            Gdx.app.log("World", "Map not found: " + mapFile);
            return 0;
        }

        String text = file.readString();
        String[] parts = text.split("---", 2);

        // Parse metadata
        if (parts.length > 1) {
            String meta = parts[0].trim();
            for (String line : meta.split("\n")) {
                line = line.trim();
                if (line.startsWith("speed:")) {
                    gravityInterval = Float.parseFloat(line.substring(6).trim());
                }
            }
        }

        // Parse grid
        String gridPart = parts.length > 1 ? parts[1].trim() : parts[0].trim();
        String[] lines = gridPart.split("\n");

        int flashCount = 0;
        int startRow = Math.max(0, KfsConst.GRID_H - lines.length);

        for (int i = 0; i < lines.length && (startRow + i) < KfsConst.GRID_H; i++) {
            String line = lines[i];
            for (int x = 0; x < Math.min(line.length(), KfsConst.GRID_W); x++) {
                char c = line.charAt(x);
                if (c >= '1' && c <= '7') {
                    grid[startRow + i][x] = c - '0';
                } else if (c == 'F') {
                    // Flash block: assign random color 1-7
                    grid[startRow + i][x] = (int) (Math.random() * 7) + 1;
                    flashGrid[startRow + i][x] = true;
                    flashCount++;
                }
            }
        }

        return flashCount;
    }

    public float getGravityInterval() {
        return gravityInterval;
    }

    public boolean canPlace(int[][] shape, int px, int py) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) continue;
                int gx = px + c;
                int gy = py + r;
                if (gx < 0 || gx >= KfsConst.GRID_W || gy >= KfsConst.GRID_H) return false;
                if (gy >= 0 && grid[gy][gx] != 0) return false;
            }
        }
        return true;
    }

    public void lockPiece(int[][] shape, int px, int py) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    int gx = px + c;
                    int gy = py + r;
                    if (gy >= 0 && gy < KfsConst.GRID_H && gx >= 0 && gx < KfsConst.GRID_W) {
                        grid[gy][gx] = shape[r][c];
                    }
                }
            }
        }
    }

    public Entity getPlayerEntity() { return playerEntity; }
    public Entity getNextPieceEntity() { return nextPieceEntity; }

    public PlayerComp getPlayer() {
        return getComponent(playerEntity, PlayerComp.class);
    }

    public NextPieceComp getNextPiece() {
        return getComponent(nextPieceEntity, NextPieceComp.class);
    }

    public int getScore() {
        PlayerComp p = getPlayer();
        return p != null ? p.score : 0;
    }

    public void addScore(int points) {
        PlayerComp p = getPlayer();
        if (p != null) p.score += points;
    }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public boolean isLevelComplete() { return levelComplete; }
    public void setLevelComplete(boolean levelComplete) { this.levelComplete = levelComplete; }

    public Entity spawnEntity() {
        return createEntity();
    }

    public Entity getActivePiece() {
        List<Entity> active = getEntitiesWith(ActiveComp.class);
        return active.isEmpty() ? null : active.get(0);
    }

    public ShapeComp getActiveShape() {
        Entity e = getActivePiece();
        return e != null ? getComponent(e, ShapeComp.class) : null;
    }
}
