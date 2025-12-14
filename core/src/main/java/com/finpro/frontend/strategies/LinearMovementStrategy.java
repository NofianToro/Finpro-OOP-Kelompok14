package com.finpro.frontend.strategies;

import com.badlogic.gdx.math.Vector2;

public class LinearMovementStrategy implements ProjectileMovementStrategy {
    @Override
    public void move(Vector2 position, Vector2 velocity, float deltaTime) {
        position.mulAdd(velocity, deltaTime);
    }
}
