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

    private float timer;
    private final float WAIT_TIME = 2.0f; // Slower fire rate
    private float fixedAngle = 0f; // Default to Right. Friend can change this to 180 for Left.

    private BulletPool bulletPool;

    public Turret(float x, float y, BulletPool pool) {
        this.position = new Vector2(x, y);
        this.bulletPool = pool;
        this.timer = 0f;
    }

    public void update(float delta, Array<Bullet> activeBullets) {
        timer += delta;
        if (timer >= WAIT_TIME) {
            shoot(activeBullets);
            timer = 0;
        }
    }

    private void shoot(Array<Bullet> activeBullets) {
        Bullet b = bulletPool.obtain();

        // Offset Y to match player height
        float spawnY = position.y + 25f;
        float spawnX;

        float speed = 400f;
        float velX = 0;

        if (fixedAngle == 0) {
            spawnX = position.x + width + 5;
            velX = speed;
        } else { // 180
            spawnX = position.x - 5;
            velX = -speed;
        }

        b.init(spawnX, spawnY - 4, velX, 0);
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
                barrelX + MathUtils.cosDeg(fixedAngle) * barrelLen,
                barrelY + MathUtils.sinDeg(fixedAngle) * barrelLen,
                6f);
    }

    public Vector2 getPosition() {
        return position;
    }
}
