package com.finpro.frontend.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface GameState {
    abstract void update(float delta);
    abstract void render(SpriteBatch batch);
    abstract void dispose();
}
