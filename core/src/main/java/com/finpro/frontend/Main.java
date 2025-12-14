package com.finpro.frontend;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finpro.frontend.factories.LevelFactory;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.states.GameState;
import com.finpro.frontend.states.GameStateManager;
import com.finpro.frontend.states.MenuState;

public class Main extends ApplicationAdapter {
    private GameStateManager gsm;
    private SpriteBatch spriteBatch;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.push(new MenuState(gsm));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(spriteBatch);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gsm != null) {
            // gsm.dispose() if implemented
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
    }
}
