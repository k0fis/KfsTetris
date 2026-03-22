package kfs.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private final Sound rotate;
    private final Sound move;
    private final Sound drop;
    private final Sound hardDrop;
    private final Sound lineClear;
    private final Sound tetris;
    private final Sound levelComplete;
    private final Sound gameOver;

    private boolean muted = false;

    public SoundManager() {
        rotate = load("sounds/rotate.wav");
        move = load("sounds/move.wav");
        drop = load("sounds/drop.wav");
        hardDrop = load("sounds/hard_drop.wav");
        lineClear = load("sounds/line_clear.wav");
        tetris = load("sounds/tetris.wav");
        levelComplete = load("sounds/level_complete.wav");
        gameOver = load("sounds/game_over.wav");
    }

    private Sound load(String path) {
        try {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Failed to load: " + path);
            return null;
        }
    }

    private void play(Sound s, float volume) {
        if (s != null && !muted) s.play(volume);
    }

    public boolean isMuted() { return muted; }
    public void toggleMute() { muted = !muted; }

    public void playRotate()        { play(rotate, 0.4f); }
    public void playMove()          { play(move, 0.3f); }
    public void playDrop()          { play(drop, 0.5f); }
    public void playHardDrop()      { play(hardDrop, 0.6f); }
    public void playLineClear()     { play(lineClear, 0.7f); }
    public void playTetris()        { play(tetris, 0.8f); }
    public void playLevelComplete() { play(levelComplete, 0.8f); }
    public void playGameOver()      { play(gameOver, 0.7f); }

    public void dispose() {
        if (rotate != null) rotate.dispose();
        if (move != null) move.dispose();
        if (drop != null) drop.dispose();
        if (hardDrop != null) hardDrop.dispose();
        if (lineClear != null) lineClear.dispose();
        if (tetris != null) tetris.dispose();
        if (levelComplete != null) levelComplete.dispose();
        if (gameOver != null) gameOver.dispose();
    }
}
