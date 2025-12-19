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

    // ... getters

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    // ... existing getters
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

        // Calculate rotation
        float angle = velocity.angleDeg();

        // Draw centered? Or just at position (bottom-left of bounds)
        // Texture might be larger than 8x8.
        // Let's assume we draw it at matching size or slightly scaled?
        // Let's draw it at width/height for now, but rotated.

        batch.draw(texture,
                position.x, position.y,
                width / 2, height / 2, // Origin center
                width, height, // Width height
                1, 1, // scale
                angle, // rotation
                0, 0, // srcX, srcY
                texture.getWidth(), texture.getHeight(), // srcWidth, srcHeight
                false, false // flip
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
