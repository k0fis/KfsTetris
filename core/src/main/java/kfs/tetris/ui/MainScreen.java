package kfs.tetris.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import kfs.tetris.KfsConst;
import kfs.tetris.KfsMain;
import kfs.tetris.ScoreClient;

public class MainScreen extends BaseScreen {

    public MainScreen(KfsMain game) {
        super(game, false);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFillParent(true);
        stage.addActor(scrollPane);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(fontBig, Color.CYAN);
        table.add(new Label("KFS TETRIS", titleStyle)).padBottom(10).row();

        Label.LabelStyle subStyle = new Label.LabelStyle(fontSmall, Color.GRAY);
        table.add(new Label("Tetris 2 Edition", subStyle)).padBottom(30).row();

        // HI-SCORE async load
        Label.LabelStyle hiStyle = new Label.LabelStyle(fontSmall, Color.YELLOW);
        Label hiScoreLabel = new Label("HI-SCORE: ...", hiStyle);
        table.add(hiScoreLabel).padBottom(30).row();

        ScoreClient.getTopScores(1, new ScoreClient.TopScoresCallback() {
            @Override
            public void onSuccess(java.util.List<ScoreClient.ScoreEntry> scores) {
                if (!scores.isEmpty()) {
                    hiScoreLabel.setText("HI-SCORE: " + scores.get(0).score);
                } else {
                    hiScoreLabel.setText("HI-SCORE: ---");
                }
            }
            @Override
            public void onError(String message) {
                hiScoreLabel.setText("HI-SCORE: OFFLINE");
            }
        });

        TextButton.TextButtonStyle buttonStyle = getTextButtonStyle(fontBig, Color.BLACK);

        TextButton playButton = new TextButton("Play", buttonStyle);
        playButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton leaderboardButton = new TextButton("Leaderboard", buttonStyle);
        leaderboardButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton musicButton = new TextButton("Music play", buttonStyle);
        musicButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton music2Button = new TextButton("Music stop", buttonStyle);
        music2Button.getColor().a = KfsConst.BUTTON_TRANSPARENCY;
        TextButton quitButton = new TextButton("Quit", buttonStyle);
        quitButton.getColor().a = KfsConst.BUTTON_TRANSPARENCY;

        float buttonWidth = 350f;
        float buttonHeight = 80f;
        table.defaults().width(buttonWidth).height(buttonHeight).pad(15f);

        table.add(playButton).row();
        table.add(leaderboardButton).row();
        table.add(musicButton).row();
        table.add(music2Button).row();
        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            table.add(quitButton).row();
        }

        playButton.addListener(e -> {
            if (playButton.isPressed()) {
                game.setScreen(new LevelSelectScreen(game));
            }
            return false;
        });

        leaderboardButton.addListener(e -> {
            if (leaderboardButton.isPressed()) {
                game.setScreen(new LeaderboardScreen(game));
            }
            return false;
        });

        musicButton.addListener(e -> {
            if (musicButton.isPressed()) {
                game.music.play();
            }
            return false;
        });

        music2Button.addListener(e -> {
            if (music2Button.isPressed()) {
                game.music.stop();
            }
            return false;
        });

        quitButton.addListener(e -> {
            if (quitButton.isPressed()) {
                Gdx.app.exit();
            }
            return false;
        });

        stage.setScrollFocus(scrollPane);
    }
}
