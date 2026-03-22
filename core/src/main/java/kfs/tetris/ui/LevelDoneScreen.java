package kfs.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import kfs.tetris.KfsConst;
import kfs.tetris.KfsMain;

public class LevelDoneScreen extends BaseScreen {

    private final String mapPath;

    public LevelDoneScreen(KfsMain game, String mapPath) {
        super(game, false);
        this.mapPath = mapPath;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        stage.addActor(table);

        Label.LabelStyle style = new Label.LabelStyle(fontBig, Color.RED);
        Label title = new Label("GAME OVER", style);
        table.add(title).padBottom(40).row();
        title.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(0.5f), Actions.fadeIn(0.5f))));

        var bstyle = getTextButtonStyle(fontBig, Color.BROWN);

        TextButton retry = new TextButton("Try Again", bstyle);
        retry.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        retry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TetrisScreen(game, mapPath));
            }
        });

        TextButton menu = new TextButton("Main Menu", bstyle);
        menu.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game));
            }
        });

        table.add(retry).width(350).height(80).padBottom(20).row();
        table.add(menu).width(350).height(80).padBottom(20).row();
    }
}
