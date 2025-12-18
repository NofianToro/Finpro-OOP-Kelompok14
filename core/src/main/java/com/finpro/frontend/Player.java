package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.observers.LevelListener;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle collider;
    private boolean isOnGround;
    private boolean isFinished = false;
    private boolean hasPortalMomentum = false;
    private Array<LevelListener> listeners;
    private final float WIDTH = 50f;
    private final float HEIGHT = 50f;
    private final float GRAVITY = 2000f;
    private final float JUMP_FORCE = 570f;
    private final float SPEED = 300f;
    private final float TERMINAL_VELOCITY = -1000f;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.collider = new Rectangle(startX, startY, WIDTH, HEIGHT);
        this.listeners = new Array<>();
    }

    public void addListener(LevelListener listener) {
        listeners.add(listener);
    }

    private void notifyLevelFinished() {
        for (LevelListener listener : listeners) {
            listener.onLevelFinished();
        }
    }

    public void update(float delta, Array<Wall> walls) {
        velocity.y -= GRAVITY * delta;
        if (velocity.y < TERMINAL_VELOCITY)
            velocity.y = TERMINAL_VELOCITY;
        position.x += velocity.x * delta;
        updateCollider();
        checkCollisions(walls, true);
        position.y += velocity.y * delta;
        updateCollider();
        isOnGround = false;
        checkCollisions(walls, false);

        // Apply air resistance only to portal momentum
        if (hasPortalMomentum) {
            velocity.x *= 0.984f;
            if (Math.abs(velocity.x) < 10f) {
                velocity.x = 0;
                hasPortalMomentum = false;
            }
        }
    }

    private void checkCollisions(Array<Wall> walls, boolean isXAxis) {
        for (Wall wall : walls) {
            if (collider.overlaps(wall.getBounds())) {
                if (wall.isExit()) {
                    if (!isFinished) {
                        isFinished = true;
                        notifyLevelFinished();
                    }
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
        hasPortalMomentum = false; // Clear portal momentum when player provides input
    }

    public void moveRight(float delta) {
        velocity.x = SPEED;
        hasPortalMomentum = false; // Clear portal momentum when player provides input
    }

    public void stopHorizontalMovement() {
        // Don't stop if we have portal momentum
        if (!hasPortalMomentum) {
            velocity.x = 0;
        }
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

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
        // Mark that we have portal momentum if there's horizontal velocity
        if (Math.abs(x) > 0.1f) {
            hasPortalMomentum = true;
        }
    }
}
