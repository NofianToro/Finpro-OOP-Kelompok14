package com.finpro.frontend.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public class GameStateManager {
    private final Stack<GameState> states;
    public GameStateManager() {
        this.states = new Stack<>();
    }

    public void push(GameState state) {
        this.states.push(state);
    }

    public void pop() {
        this.states.pop();
    }

    public void set(GameState state) {
        pop();
        push(state);
    }

    public void update(float delta) {
        this.states.peek().update(delta);
    }

    public void render(SpriteBatch batch) {
        this.states.peek().render(batch);
    }
}
