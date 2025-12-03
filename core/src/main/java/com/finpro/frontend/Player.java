package com.finpro.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private Vector2 position;
    private Vector2 velocity;
    private float gravity = 2000f;
    private float force = 200000f;
    private float maxVerticalSpeed = 700f;
    private Rectangle collider;
    private float width = 64f;
    private float height = 64f;
    private boolean isOnGround = false;

    // Sistem kecepatan
    private float Speed = 0f;
    private float distanceTravelled = 0f;

    public Player(Vector2 startPosition) {
        this.position = startPosition;
        this.velocity = new Vector2(Speed, 0);
        this.collider = new Rectangle(this.position.x, this.position.y,this.width, this.height);
    }

    public void update(float delta) {
        updateDistanceAndSpeed(delta);
        updatePosition(delta);
        applyGravity(delta);
        updateCollider();
    }

    private void updateDistanceAndSpeed(float delta) {
        this.distanceTravelled += this.velocity.x * delta;
    }

    private void updatePosition(float delta) {
        this.position.x += this.velocity.x * delta;
        this.position.y += this.velocity.y * delta;
    }

    private void applyGravity(float delta) {
        this.velocity.y -= this.gravity * delta;
        this.velocity.x = this.Speed;

        if (this.velocity.y > this.maxVerticalSpeed) {
            this.velocity.y = this.maxVerticalSpeed;
        }

        if (this.velocity.y < -this.maxVerticalSpeed) {
            this.velocity.y = -this.maxVerticalSpeed;
        }
    }

    public void jump(float delta) {
        if (this.isOnGround) {
            this.velocity.y += this.force * delta;
            this.isOnGround = false;
        }
    }

    private void move(float delta, boolean movingLeft, boolean movingRight) {
        float speed = 500f * delta;
        if (movingLeft) this.position.x -= speed;
        if (movingRight) this.position.x += speed;
    }

    public void moveLeft(float delta) {
        this.position.x -= 500f * delta;
    }

    public void moveRight(float delta) {
        this.position.x += 500f * delta;
    }

    private void updateCollider() {
        this.collider.x = this.position.x;
        this.collider.y = this.position.y;
    }

    public void checkBoundaries(Ground ground, float ceilingY) {
        if (ground.isColliding(this.collider)) {
            this.position.y = ground.getTopY();
            this.isOnGround = true;

        }

        if (this.collider.y + this.height >= ceilingY) {
            this.position.y = ceilingY - this.height;
        }
    }

    public void renderShape(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(this.position.x, this.position.y, this.width, this.height);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Rectangle getCollider() {
        return this.collider;
    }

    public float getDistanceTraveled() {
        return this.distanceTravelled / 10f;
    }
}
