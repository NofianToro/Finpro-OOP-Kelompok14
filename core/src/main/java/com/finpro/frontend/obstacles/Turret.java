package com.finpro.frontend.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.finpro.frontend.Bullet;
import com.finpro.frontend.pools.BulletPool;

public class Turret {
    private Vector2 position;
    private float width = 32f;
    private float height = 32f;

    private float angle; // Current angle in degrees
    private float stateTime; // kept but unused to satisfy lint for now or potential future animation

    private enum State {
        WAITING_RIGHT, // Angle 0
        ROTATING_LEFT, // 0 -> 180
        WAITING_LEFT, // Angle 180
        ROTATING_RIGHT // 180 -> 0
    }

    private State currentState;
    private float timer;
    private final float WAIT_TIME = 1.0f; // Time to wait before shooting
    private final float ROTATION_SPEED = 90f; // Degrees per second

    private BulletPool bulletPool;

    public Turret(float x, float y, BulletPool pool) {
        this.position = new Vector2(x, y);
        this.bulletPool = pool;
        this.angle = 0f;
        this.currentState = State.WAITING_RIGHT;
        this.timer = 0f;
    }

    public void update(float delta, Array<Bullet> activeBullets) {
        switch (currentState) {
            case WAITING_RIGHT:
                timer += delta;
                if (timer >= WAIT_TIME) {
                    shoot(activeBullets, 0); // Shoot Right
                    timer = 0;
                    currentState = State.ROTATING_LEFT;
                }
                break;

            case ROTATING_LEFT:
                angle += ROTATION_SPEED * delta;
                if (angle >= 180f) {
                    angle = 180f;
                    currentState = State.WAITING_LEFT;
                    timer = 0;
                }
                break;

            case WAITING_LEFT:
                timer += delta;
                if (timer >= WAIT_TIME) {
                    shoot(activeBullets, 180); // Shoot Left
                    timer = 0;
                    currentState = State.ROTATING_RIGHT;
                }
                break;

            case ROTATING_RIGHT:
                angle -= ROTATION_SPEED * delta;
                if (angle <= 0f) {
                    angle = 0f;
                    currentState = State.WAITING_RIGHT;
                    timer = 0;
                }
                break;
        }
    }

    private void shoot(Array<Bullet> activeBullets, float shootAngle) {
        Bullet b = bulletPool.obtain();

        // Offset Y to match player height (approx 25px up from turret center/base)
        float spawnY = position.y + 25f;
        float spawnX;

        float speed = 400f;
        float velX = 0;

        if (shootAngle == 0) {
            spawnX = position.x + width + 5; // Slight offset right
            velX = speed;
        } else { // 180
            spawnX = position.x - 5; // Slight offset left
            velX = -speed;
        }

        b.init(spawnX, spawnY - 4, velX, 0); // bullet height is 8, so -4 to center
        activeBullets.add(b);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(position.x, position.y, width, height);

        // Draw gun barrel
        float barrelLen = 20f;
        float barrelX = position.x + width / 2f;
        float barrelY = position.y + height / 2f;

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rectLine(
                barrelX, barrelY,
                barrelX + MathUtils.cosDeg(angle) * barrelLen,
                barrelY + MathUtils.sinDeg(angle) * barrelLen,
                6f);
    }

    public Vector2 getPosition() {
        return position;
    }
}
