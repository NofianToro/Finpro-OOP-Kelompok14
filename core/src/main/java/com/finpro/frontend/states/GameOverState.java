package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverState implements GameState {

    private final GameStateManager gsm;
    private final OrthographicCamera uiCamera;
    private final BitmapFont font;

    public GameOverState(GameStateManager gsm) {
        this.gsm = gsm;

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        font = new BitmapFont();
        font.getData().setScale(2.5f);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gsm.set(new PlayingState(gsm));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gsm.set(new MenuState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(uiCamera.combined);

        batch.begin();

        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER",
            Gdx.graphics.getWidth() / 2f - 120f,
            Gdx.graphics.getHeight() / 2f + 40f);

        font.setColor(Color.WHITE);
        font.draw(batch, "Press R to Restart",
            Gdx.graphics.getWidth() / 2f - 140f,
            Gdx.graphics.getHeight() / 2f - 10f);

        font.draw(batch, "Press ESC to Menu",
            Gdx.graphics.getWidth() / 2f - 140f,
            Gdx.graphics.getHeight() / 2f - 50f);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
