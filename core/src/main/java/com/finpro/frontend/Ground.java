package com.finpro.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import static com.badlogic.gdx.math.Intersector.overlaps;

public class Ground {
    private static final float GROUND_HEIGHT = 50f;
    private Rectangle collider;

    public Ground() {
        collider = new Rectangle(0, 0, Gdx.graphics.getWidth() * 2, GROUND_HEIGHT);
        this.collider = new Rectangle(0, 0, Gdx.graphics.getWidth() * 2, GROUND_HEIGHT);
    }

    public void update(float cameraX) {
        this.collider.width = Gdx.graphics.getWidth() * 2;
        this.collider.x = cameraX - Gdx.graphics.getWidth() / 2f - 500;
        this.collider.y = 0;

    }

    public boolean isColliding(Rectangle playerCollider) {
        return overlaps(collider, playerCollider);
    }

    public float getTopY() {
        return GROUND_HEIGHT;
    }

    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.rect(collider.x, collider.y, collider.width, collider.height);
    }

}
