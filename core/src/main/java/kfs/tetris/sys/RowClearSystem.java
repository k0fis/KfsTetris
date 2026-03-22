package kfs.tetris.sys;

import kfs.tetris.KfsConst;
import kfs.tetris.World;
import kfs.tetris.comp.PlayerComp;
import kfs.tetris.ecs.KfsSystem;

import java.util.ArrayList;
import java.util.List;

public class RowClearSystem implements KfsSystem {

    private final World world;

    public RowClearSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        // Only check when no active piece (just locked)
        if (world.getActivePiece() != null) return;

        PlayerComp player = world.getPlayer();
        if (player == null) return;

        List<Integer> fullRows = new ArrayList<>();
        for (int y = KfsConst.GRID_H - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < KfsConst.GRID_W; x++) {
                if (world.grid[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                fullRows.add(y);
            }
        }

        if (fullRows.isEmpty()) return;

        // Count flash blocks in cleared rows
        int flashCleared = 0;
        for (int y : fullRows) {
            for (int x = 0; x < KfsConst.GRID_W; x++) {
                if (world.flashGrid[y][x]) {
                    flashCleared++;
                }
            }
        }

        // Remove rows (from bottom to top to maintain indices)
        for (int y : fullRows) {
            removeRow(y);
        }

        // Score
        int lines = fullRows.size();
        int level = player.level;
        int lineScore;
        switch (lines) {
            case 1: lineScore = 100 * level; break;
            case 2: lineScore = 300 * level; break;
            case 3: lineScore = 500 * level; break;
            case 4: lineScore = 800 * level; break;
            default: lineScore = 100 * lines * level; break;
        }
        player.score += lineScore;
        player.score += flashCleared * 50;
        player.linesCleared += lines;
        player.flashRemaining -= flashCleared;
        if (player.flashRemaining < 0) player.flashRemaining = 0;
    }

    private void removeRow(int row) {
        // Shift everything above down
        for (int y = row; y > 0; y--) {
            System.arraycopy(world.grid[y - 1], 0, world.grid[y], 0, KfsConst.GRID_W);
            System.arraycopy(world.flashGrid[y - 1], 0, world.flashGrid[y], 0, KfsConst.GRID_W);
        }
        // Clear top row
        for (int x = 0; x < KfsConst.GRID_W; x++) {
            world.grid[0][x] = 0;
            world.flashGrid[0][x] = false;
        }
    }
}
