package kfs.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import kfs.tetris.ui.MainScreen;

import java.util.List;
import java.util.stream.Stream;

public class KfsMain extends Game {

    public MusicManager music;
    public SoundManager sounds;
    public String lastPlayerName = "";

    @Override
    public void create() {
        music = new MusicManager("music/");
        sounds = new SoundManager();
        setScreen(new MainScreen(this));
    }

    public List<FileHandle> getMaps() {
        FileHandle levelDir = Gdx.files.internal("maps");
        if (!levelDir.exists() || !levelDir.isDirectory()) {
            Gdx.app.log("KfsMain", "No levels found in /assets/maps/");
            return List.of();
        }

        return Stream.of(levelDir.list())
            .filter(file -> file.name().endsWith(".txt"))
            .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
            .toList();
    }

    public String getMap(String before) {
        String prev = "";
        for (FileHandle file : getMaps()) {
            if (before.equals(prev)) {
                return file.path();
            }
            prev = file.path();
        }
        return null;
    }

    public int getMapIndex(String mapPath) {
        List<FileHandle> maps = getMaps();
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i).path().equals(mapPath)) {
                return i + 1;
            }
        }
        return 1;
    }
}
