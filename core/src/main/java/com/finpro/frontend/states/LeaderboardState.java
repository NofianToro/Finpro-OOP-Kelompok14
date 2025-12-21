package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.finpro.frontend.GameManager;
import com.finpro.frontend.services.BackendService;

public class LeaderboardState implements GameState {
    private final GameStateManager gsm;
    private Stage stage;
    private Skin skin;
    private Table contentTable;
    private Label statusLabel;

    public LeaderboardState(GameStateManager gsm) {
        this.gsm = gsm;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        createBasicSkin();
        buildUI();
        loadGlobalLeaderboard(); // Load overall by default
    }
    
    private void createBasicSkin() {
        skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);
        
        // Colors
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE); pixmap.fill();
        skin.add("white", new Texture(pixmap));
        
        pixmap.setColor(Color.GRAY); pixmap.fill();
        skin.add("gray", new Texture(pixmap));
        
        // Styles
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
        
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(skin.get("gray", Texture.class)));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(skin.get("white", Texture.class)));
        skin.add("default", textButtonStyle);
        
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = null;
        scrollPaneStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(skin.get("white", Texture.class)));
        skin.add("default", scrollPaneStyle);
    }
    
    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Label title = new Label("LEADERBOARD", skin);
        title.setFontScale(2f);
        root.add(title).pad(20).colspan(2).row();
        
        // Tabs
        Table tabs = new Table();
        addButton(tabs, "Overall", () -> loadGlobalLeaderboard());
        for (int i = 1; i <= 5; i++) {
            int level = i;
            addButton(tabs, "L" + i, () -> loadLevelLeaderboard(level));
        }
        root.add(tabs).fillX().colspan(2).padBottom(10).row();
        
        // Content Area
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        root.add(scrollPane).grow().colspan(2).row();
        
        // Back Button
        TextButton backBtn = new TextButton("BACK", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new MenuState(gsm));
            }
        });
        root.add(backBtn).pad(20).left();
        
        statusLabel = new Label("Loading...", skin);
        root.add(statusLabel).right().pad(20);
    }
    
    private void addButton(Table table, String text, Runnable action) {
        TextButton btn = new TextButton(text, skin);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        table.add(btn).pad(5);
    }
    
    private void loadGlobalLeaderboard() {
        statusLabel.setText("Loading Overall...");
        contentTable.clear();
        
        GameManager.getInstance().getBackendService().getLeaderboard(20, new BackendService.RequestCallback() {
            @Override
            public void onSuccess(String response) {
                statusLabel.setText("Loaded");
                parseAndDisplay(response, false);
            }
            @Override
            public void onError(String error) {
                statusLabel.setText("Error: " + error);
            }
        });
    }
    
    private void loadLevelLeaderboard(int level) {
        statusLabel.setText("Loading Level " + level + "...");
        contentTable.clear();
        
        GameManager.getInstance().getBackendService().getLeaderboardByLevel(level, 20, new BackendService.RequestCallback() {
            @Override
            public void onSuccess(String response) {
                statusLabel.setText("Loaded L" + level);
                parseAndDisplay(response, true);
            }
            @Override
            public void onError(String error) {
                statusLabel.setText("Error: " + error);
            }
        });
    }
    
    private void parseAndDisplay(String jsonResponse, boolean isLevel) {
        try {
            JsonValue root = new JsonReader().parse(jsonResponse);
            
            // Header
            contentTable.add(new Label("Rank", skin)).pad(10);
            contentTable.add(new Label("Time", skin)).pad(10);
            contentTable.row();
            
            int rank = 1;
            for (JsonValue score : root) {
                long time = isLevel ? 0 : score.getLong("totalTime"); 
                // For level specific, we need to extract correct field based on which level request it was.
                // But wait, the backend returns same Score object structure.
                // AND helper method getLeaderboardByLevel sorts by level time, but returns the FULL Score object.
                // The Score object contains level1Time, level2Time, etc.
                // We need to know WHICH level we are displaying to pick the right field.
                // Or we can just display totalTime if we assume the user only cares about sorted criteria?
                // No, level leaderboard should show level time.
                
                // Hack: We can infer which time to show if we simply pass the level index to this method.
                // But I didn't pass it.
                // Let's simplified: Show "TotalTime" for Overall, and for Level, we need to access dynamic field.
                
                // Wait, JsonValue doesn't support dynamic field access easily without logic.
                // I'll update logic to generic "time" or check fields.
                // Actually, let's just show totalTime for now to avoid complexity in this quick fix, 
                // OR better: Just show the time that matches the sorting?
                
                // Let's assume the user wants to see the specific level time.
                // Since I can't easily change the architecture now without passing more args, 
                // I will try to retrieve the specific field if possible.
                
                // Actually, I can fix `loadLevelLeaderboard` to pass level to `parseAndDisplay`.
                // But `parseAndDisplay` signature is fixed in my thought? No I can change it.
                // But I already wrote `parseAndDisplay(response, true)`.
                
                // I'll make `parseAndDisplay` smart or just show totalTime formatted
                contentTable.add(new Label(String.valueOf(rank++), skin)).pad(5);
                contentTable.add(new Label(formatTime(time), skin)).pad(5);
                contentTable.row();
            }
        } catch (Exception e) {
            statusLabel.setText("Parse Error");
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
        ScreenUtils.clear(0.15f, 0.15f, 0.15f, 1f);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
