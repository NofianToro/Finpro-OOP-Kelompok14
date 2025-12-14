package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.finpro.frontend.obstacles.Wall;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle collider;
    private boolean isOnGround;
    private boolean isFinished = false;

    private final float WIDTH = 50f;
    private final float HEIGHT = 50f;
    private final float GRAVITY = 2000f;
    private final float JUMP_FORCE = 800f;
    private final float SPEED = 300f;

    public Player(Vector2 startPos) {
        this.position = startPos;
        this.velocity = new Vector2(0, 0);
        this.collider = new Rectangle(startPos.x, startPos.y, WIDTH, HEIGHT);
    }

    public void update(float delta, Array<Wall> walls) {
        velocity.y -= GRAVITY * delta;

        position.x += velocity.x * delta;
        updateCollider();
        checkCollisions(walls, true);

        position.y += velocity.y * delta;
        updateCollider();
        isOnGround = false; // Reset ground
        checkCollisions(walls, false);

        velocity.x = 0;
    }

    private void checkCollisions(Array<Wall> walls, boolean isXAxis) {
        for (Wall wall : walls) {
            if (collider.overlaps(wall.getBounds())) {
                if (wall.isExit()) {
                    isFinished = true;
                }

                if (isXAxis) {
                    if (velocity.x > 0)
                        position.x = wall.getBounds().x - WIDTH;
                    else if (velocity.x < 0)
                        position.x = wall.getBounds().x + wall.getBounds().width;
                } else {
                    if (velocity.y < 0) {
                        position.y = wall.getBounds().y + wall.getBounds().height;
                        isOnGround = true;
                        velocity.y = 0;
                    } else if (velocity.y > 0) {
                        position.y = wall.getBounds().y - HEIGHT;
                        velocity.y = 0;
                    }
                }
                updateCollider();
            }
        }
    }

    private void updateCollider() {
        collider.setPosition(position.x, position.y);
    }

    public void moveLeft(float delta) {
        velocity.x = -SPEED;
    }

    public void moveRight(float delta) {
        velocity.x = SPEED;
    }

    public void jump() {
        if (isOnGround)
            velocity.y = JUMP_FORCE;
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(Color.RED);
        sr.rect(position.x, position.y, WIDTH, HEIGHT);
    }

    public boolean isLevelFinished() {
        return isFinished;
    }

    public Rectangle getBounds() {
        return collider;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        updateCollider();
    }
}
