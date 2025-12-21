package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Portal {
    private Vector2 position;
    private Rectangle bounds;
    private final float width = 10f;
    private final float height = 64f;
    private String type;
    private boolean active;
    private Portal linkedPortal;

    public enum Orientation {
        VERTICAL, HORIZONTAL
    }

    private Orientation orientation;

    private Vector2 normal;

    public Portal() {
        position = new Vector2();
        bounds = new Rectangle();
        active = false;
        orientation = Orientation.VERTICAL;
        normal = new Vector2();
    }

    public void init(float x, float y, String type, Orientation orientation, Vector2 normal) {
        this.position.set(x, y);
        this.type = type;
        this.orientation = orientation;
        this.normal.set(normal);
        this.active = true;

        // Adjust logical bounds based on orientation
        if (orientation == Orientation.HORIZONTAL) {
            // Swap width/height from default
            this.bounds.set(x, y, height, width);
            // Note: height becomes width, width becomes height
        } else {
            this.bounds.set(x, y, width, height);
        }
    }

    public Vector2 getNormal() {
        return normal;
    }

    private float stateTime;

    public void update(float delta) {
        if (active) {
            stateTime += delta;
        }
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch,
            com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> animation) {
        if (!active)
            return;

        com.badlogic.gdx.graphics.g2d.TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        // Determine rotation and size
        float rotation = 0;
        float drawX = position.x;
        float drawY = position.y;
        float drawW = currentFrame.getRegionWidth(); // Default size from sprite
        // Logic size
        float logicW = (orientation == Orientation.HORIZONTAL) ? bounds.height : bounds.width;
        float logicH = (orientation == Orientation.HORIZONTAL) ? bounds.width : bounds.height;

        float drawWidth = currentFrame.getRegionWidth();
        float drawHeight = currentFrame.getRegionHeight();

        // Calculate center of the logical bounds
        float cx = position.x + bounds.width / 2f;
        float cy = position.y + bounds.height / 2f;

        if (orientation == Orientation.HORIZONTAL) {
            batch.draw(currentFrame,
                    cx - drawWidth / 2f, cy - drawHeight / 2f,
                    drawWidth / 2f, drawHeight / 2f,
                    drawWidth, drawHeight,
                    1, 1,
                    90f);
        } else {
            // Vertical - No rotation
            batch.draw(currentFrame,
                    cx - drawWidth / 2f, cy - drawHeight / 2f,
                    drawWidth, drawHeight);
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!active)
            return;

        if ("ORANGE".equals(type)) {
            shapeRenderer.setColor(Color.ORANGE);
        } else {
            shapeRenderer.setColor(Color.CYAN);
        }

        if (orientation == Orientation.HORIZONTAL) {
            shapeRenderer.rect(position.x, position.y, height, width);
        } else {
            shapeRenderer.rect(position.x, position.y, width, height);
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setLinkedPortal(Portal other) {
        this.linkedPortal = other;
    }

    public Portal getLinkedPortal() {
        return linkedPortal;
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

    public Vector2 getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }
}
