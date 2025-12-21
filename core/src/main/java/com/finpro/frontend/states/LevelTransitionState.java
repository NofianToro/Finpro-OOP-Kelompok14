package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class LevelTransitionState implements GameState {

    private final GameStateManager gsm;
    private final int nextLevelIndex;

    private float alpha = 0f;
    private boolean ready = false;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    public LevelTransitionState(GameStateManager gsm, int nextLevelIndex) {
        this.gsm = gsm;
        this.nextLevelIndex = nextLevelIndex;

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void update(float delta) {
        if (!ready) {
            alpha += delta;
            if (alpha >= 1f) {
                alpha = 1f;
                ready = true;
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // sementara: lanjut game biasa (tanpa index)
            gsm.set(new PlayingState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        if (ready) {
            batch.begin();
            font.setColor(Color.WHITE);

            float x = Gdx.graphics.getWidth() / 2f - 150f;
            float y = Gdx.graphics.getHeight() / 2f;
            font.draw(batch, "Press SPACE to continue", x, y);

            batch.end();
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
