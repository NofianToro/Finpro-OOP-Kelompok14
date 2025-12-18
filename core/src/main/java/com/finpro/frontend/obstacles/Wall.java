package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
    private Rectangle bounds;
    private boolean isExit = false;
    private boolean isBlackWall = false;

    public Wall(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() { return bounds; }
    public boolean isExit() { return isExit; }
    public void setExit(boolean isExit) { this.isExit = isExit; }

    public boolean isBlackWall() { return isBlackWall; }
    public void setBlackWall(boolean isBlackWall) { this.isBlackWall = isBlackWall; }

    public void render(ShapeRenderer sr) {
        if (isExit) {
            sr.setColor(Color.GREEN);
        } else if (isBlackWall) {
            sr.setColor(Color.BLACK); //Black for BlackWall
        } else {
            sr.setColor(Color.RED);   //Red for Common Wall
        }
        sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
