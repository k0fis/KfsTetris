package kfs.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import kfs.tetris.KfsConst;
import kfs.tetris.KfsMain;

public class LevelSelectScreen extends BaseScreen {

    public LevelSelectScreen(KfsMain game) {
        super(game, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        stage.addActor(table);

        Label.LabelStyle style = new Label.LabelStyle(fontBig, Color.CYAN);
        table.add(new Label("Select Level", style)).colspan(2).padBottom(20).row();

        Table levelList = new Table();
        ScrollPane scrollPane = new ScrollPane(levelList, skin);
        scrollPane.setFadeScrollBars(false);

        var buttonStyle = getTextButtonStyle(fontSmall, Color.WHITE);
        for (FileHandle file : game.getMaps()) {
            TextButton levelButton = new TextButton(file.nameWithoutExtension(), buttonStyle);
            levelButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
            levelButton.addListener(e -> {
                if (levelButton.isPressed()) {
                    game.setScreen(new TetrisScreen(game, file.path()));
                }
                return false;
            });
            levelList.add(levelButton).width(300).height(24).pad(2).row();
        }

        table.add(scrollPane).expand().fill().colspan(2).row();

        TextButton backButton = new TextButton("Main Menu", buttonStyle);
        backButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        backButton.addListener(e -> {
            if (backButton.isPressed()) {
                game.setScreen(new MainScreen(game));
            }
            return false;
        });

        table.add(backButton).width(300).height(24).padTop(10);
        stage.setScrollFocus(scrollPane);
    }
}
