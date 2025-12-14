package com.finpro.frontend.strategies;

import com.badlogic.gdx.math.Vector2;

public interface ProjectileMovementStrategy {
    void move(Vector2 position, Vector2 velocity, float deltaTime);
}
