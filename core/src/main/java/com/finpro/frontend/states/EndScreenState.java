package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.finpro.frontend.GameManager;
import com.finpro.frontend.services.BackendService;
import java.util.Map;

public class EndScreenState implements GameState {
    private final GameStateManager gsm;
    private final Map<Integer, Long> levelTimes;
    private final long totalTime;
    
    private Stage stage;
    private Skin skin;
    private boolean scoreSubmitted = false;

    public EndScreenState(GameStateManager gsm, Map<Integer, Long> levelTimes, long totalTime) {
        this.gsm = gsm;
        this.levelTimes = levelTimes;
        this.totalTime = totalTime;
        
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        createBasicSkin();
        buildUI();
    }
    
    private void createBasicSkin() {
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);
        
        // Button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);
        
        // Label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
    }
    
    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        Label title = new Label("CONGRATULATIONS!", skin);
        title.setFontScale(2f);
        table.add(title).padBottom(20).row();
        
        Label subTitle = new Label("You Escaped From I-CELL!", skin);
        table.add(subTitle).padBottom(30).row();
        
        // Display Level Times
        for (int i = 1; i <= 5; i++) {
            long time = levelTimes.getOrDefault(i, 0L);
            table.add(new Label("Level " + i + ": " + formatTime(time), skin)).padBottom(5).row();
        }
        
        table.add(new Label("Total Time: " + formatTime(totalTime), skin)).padTop(20).padBottom(40).row();
        
        TextButton nextButton = new TextButton("Continue to Credits", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new EndCreditsState(gsm));
            }
        });
        table.add(nextButton).width(200).height(50);
        
        submitScore();
    }
    
    private void submitScore() {
        if (!scoreSubmitted) {
            long l1 = levelTimes.getOrDefault(1, 0L);
            long l2 = levelTimes.getOrDefault(2, 0L);
            long l3 = levelTimes.getOrDefault(3, 0L);
            long l4 = levelTimes.getOrDefault(4, 0L);
            long l5 = levelTimes.getOrDefault(5, 0L);
            
            GameManager.getInstance().submitScore(l1, l2, l3, l4, l5, new BackendService.RequestCallback() {
                @Override
                public void onSuccess(String response) {
                    Gdx.app.log("EndScreen", "Score submitted successfully");
                }

                @Override
                public void onError(String error) {
                    Gdx.app.error("EndScreen", "Failed to submit score: " + error);
                }
            });
            scoreSubmitted = true;
        }
    }
    
    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long ms = millis % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
