package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Ground {
    private Rectangle bounds;

    public Ground(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(Color.GREEN);
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
