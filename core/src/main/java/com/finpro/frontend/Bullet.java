package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private Vector2 position;
    private Vector2 velocity;
    private float width = 32f;
    private float height = 32f;
    private boolean active;
    private Rectangle bounds;

    public Bullet() {
        position = new Vector2();
        velocity = new Vector2();
        bounds = new Rectangle();
        active = false;
    }

    public void init(float x, float y, float velX, float velY) {
        this.position.set(x, y);
        this.velocity.set(velX, velY);
        this.active = true;
        this.bounds.set(x, y, width, height);
    }

    public void update(float dt) {
        if (!active)
            return;
        position.add(velocity.x * dt, velocity.y * dt);
        bounds.setPosition(position);
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!active)
            return;
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(position.x, position.y, width, height);
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Texture texture) {
        if (!active)
            return;
        batch.draw(texture, position.x, position.y, width, height);
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
}
