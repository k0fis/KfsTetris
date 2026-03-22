package kfs.tetris.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kfs.tetris.KfsMain;

public class BaseScreen extends ScreenAdapter {

    protected final KfsMain game;
    protected final Texture background;
    protected final Image backgroundImage;

    protected final Stage stage;
    protected final Skin skin;
    protected final BitmapFont fontSmall;
    protected final BitmapFont fontMiddle;
    protected final BitmapFont fontBig;

    protected BaseScreen(KfsMain game, boolean makeDarkerBackground) {
        this.game = game;
        this.background = new Texture(Gdx.files.internal("white.png"));
        this.backgroundImage = new Image(background);
        stage = new Stage(new ScreenViewport());

        // Dark background
        backgroundImage.setColor(0.05f, 0.05f, 0.15f, 1f);
        backgroundImage.setWidth(stage.getWidth());
        backgroundImage.setHeight(stage.getHeight());
        stage.addActor(backgroundImage);

        if (makeDarkerBackground) {
            Texture overlayTex = new Texture(Gdx.files.internal("white.png"));
            Image darkOverlay = new Image(overlayTex);
            darkOverlay.setColor(0, 0, 0, 0.80f);
            darkOverlay.setFillParent(true);
            stage.addActor(darkOverlay);
        }

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        fontBig = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-32.fnt"));
        fontMiddle = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-16.fnt"));
        fontSmall = new BitmapFont(Gdx.files.internal("fonts/PressStart2P-10.fnt"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundImage.setWidth(stage.getWidth());
        backgroundImage.setHeight(stage.getHeight());
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
        skin.dispose();
        fontSmall.dispose();
        fontBig.dispose();
        fontMiddle.dispose();
    }

    protected TextButton.TextButtonStyle getTextButtonStyle(BitmapFont font, Color fontColor) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.9f, 0.8f, 0.7f, 0.75f));
        pixmap.fill();

        Texture pixmapTex = new Texture(pixmap);
        pixmap.dispose();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(pixmapTex));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = drawable;
        buttonStyle.down = drawable.tint(Color.YELLOW);
        buttonStyle.fontColor = fontColor;
        buttonStyle.downFontColor = fontColor;

        return buttonStyle;
    }
}
