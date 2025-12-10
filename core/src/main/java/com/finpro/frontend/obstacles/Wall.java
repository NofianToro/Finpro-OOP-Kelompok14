package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    private Rectangle bounds;
    private boolean isExit;

    public Wall(float x, float y, float size, boolean isExit) {
        this.bounds = new Rectangle(x, y, size, size);
        this.isExit = isExit;
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (isExit) {
            shapeRenderer.setColor(Color.BLUE); // Exit zone
        } else {
            shapeRenderer.setColor(0.6f, 0.4f, 0.2f, 1f);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isExit() {
        return isExit;
    }
}
