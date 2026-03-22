package kfs.tetris;

import com.badlogic.gdx.graphics.Color;

public class KfsConst {

    public static final int GRID_W = 10;
    public static final int GRID_H = 20;

    public static final float BUTTON_TRANSPARENCY = 0.75f;

    public static final Color[] COLORS = {
        Color.CYAN,                          // I = 1
        Color.YELLOW,                        // O = 2
        new Color(0.6f, 0.2f, 0.8f, 1f),    // T = 3 (purple)
        Color.GREEN,                         // S = 4
        Color.RED,                           // Z = 5
        Color.BLUE,                          // L = 6
        Color.ORANGE                         // J = 7
    };

    public static Color getColor(int colorIndex) {
        if (colorIndex < 1 || colorIndex > COLORS.length) return Color.WHITE;
        return COLORS[colorIndex - 1];
    }
}
