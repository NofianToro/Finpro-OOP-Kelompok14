package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.strategies.ProjectileMovementStrategy;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float width = 8f;
    private float height = 8f;
    private boolean active;
    private String type;
    private Rectangle bounds;

    private ProjectileMovementStrategy movementStrategy;

    private Vector2 previousPosition;

    public Projectile() {
        position = new Vector2();
        previousPosition = new Vector2();
        velocity = new Vector2();
        bounds = new Rectangle();
        active = false;
    }

    public void init(float x, float y, float velX, float velY, String type, ProjectileMovementStrategy strategy) {
        this.position.set(x, y);
        this.previousPosition.set(x, y);
        this.velocity.set(velX, velY);
        this.type = type;
        this.movementStrategy = strategy;
        this.active = true;
        this.bounds.set(x, y, width, height);
    }

    public void update(float dt) {
        if (!active)
            return;

        previousPosition.set(position);

        if (movementStrategy != null) {
            movementStrategy.move(position, velocity, dt);
        }
        bounds.setPosition(position);
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!active)
            return;

        if ("ORANGE".equals(type)) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.CYAN);
        }
        shapeRenderer.rect(position.x, position.y, width, height);
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Texture texture) {
        if (!active)
            return;

        float angle = velocity.angleDeg(); // Calculate rotation

        batch.draw(texture,
                position.x, position.y,
                width / 2, height / 2,
                width, height,
                1, 1,
                angle,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false
        );
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getType() {
        return type;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
