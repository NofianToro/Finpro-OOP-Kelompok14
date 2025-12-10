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

    // Player Size 24px, Wall Size 32px
    private final float WIDTH = 50f;
    private final float HEIGHT = 50f;

    // Physics constants
    private final float GRAVITY = 2000f;
    private final float JUMP_FORCE = 800f;
    private final float SPEED = 300f;

    public Player(Vector2 startPos) {
        this.position = startPos;
        this.velocity = new Vector2(0, 0);
        this.collider = new Rectangle(startPos.x, startPos.y, WIDTH, HEIGHT);
    }

    public void update(float delta, Array<Wall> walls) {
        // 1. Terapkan Gravitasi
        velocity.y -= GRAVITY * delta;

        // 2. Update Posisi X (Horizontal)
        position.x += velocity.x * delta;
        updateCollider();
        checkCollisions(walls, true); // Cek tabrakan samping

        // 3. Update Posisi Y (Vertikal)
        position.y += velocity.y * delta;
        updateCollider();
        isOnGround = false; // Reset ground
        checkCollisions(walls, false); // Cek tabrakan atas/bawah

        // Reset velocity horizontal (agar movement stop kalau tombol dilepas)
        // Kalau pakai InputHandler yang men-set velocity terus menerus, baris ini bisa dihapus/disesuaikan
        velocity.x = 0;
    }

    private void checkCollisions(Array<Wall> walls, boolean isXAxis) {
        for (Wall wall : walls) {
            if (collider.overlaps(wall.getBounds())) {
                // Cek apakah ini Exit
                if (wall.isExit()) {
                    isFinished = true;
                }

                // Logika Collision Fisik
                if (isXAxis) {
                    // Tabrakan Samping
                    if (velocity.x > 0) position.x = wall.getBounds().x - WIDTH;
                    else if (velocity.x < 0) position.x = wall.getBounds().x + wall.getBounds().width;
                } else {
                    // Tabrakan Atas/Bawah
                    if (velocity.y < 0) { // Jatuh ke lantai
                        position.y = wall.getBounds().y + wall.getBounds().height;
                        isOnGround = true;
                        velocity.y = 0;
                    } else if (velocity.y > 0) { // Mentok atap
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

    // Gerakan
    public void moveLeft(float delta) { velocity.x = -SPEED; }
    public void moveRight(float delta) { velocity.x = SPEED; }
    public void jump() { if(isOnGround) velocity.y = JUMP_FORCE; }

    public void render(ShapeRenderer sr) {
        sr.setColor(Color.RED);
        sr.rect(position.x, position.y, WIDTH, HEIGHT);
    }

    public boolean isLevelFinished() { return isFinished; }
}
