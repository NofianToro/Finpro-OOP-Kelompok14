package com.finpro.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    public final float WIDTH = 50f; // Logical width
    public final float HEIGHT = 50f; // Logical height (Hitbox)
    private final float GRAVITY = 2000f;
    private final float JUMP_FORCE = 570f;
    private final float SPEED = 300f;
    private final float TERMINAL_VELOCITY = -1000f;

    // Graphics
    private Texture idleTex, runTex, jumpTex;
    private Animation<TextureRegion> idleAnim, runAnim, jumpAnim;
    private Array<TextureRegion> hands;
    private Texture gunTex;
    private float stateTime;
    private boolean facingRight = true;

    // Hand logic
    // 1-5: Left hand from bottom to top?
    // User said: "Hand ... file1,2,3,4,5 (posisi tangan kiri dari bawah hingga ke
    // atas)"
    // And "memegang gun (/Gun)"
    // We will assume 1-10 covers a range.

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.collider = new Rectangle(startX, startY, WIDTH, HEIGHT);
        this.listeners = new Array<>();
        initGraphics();
    }

    private void initGraphics() {
        // Body - Idle (192 width / 4 frames = 48)
        idleTex = new Texture("assets/player/Body/Idle1.png");
        TextureRegion[][] idleTmp = TextureRegion.split(idleTex, 48, 48);
        idleAnim = new Animation<>(0.15f, idleTmp[0]);
        idleAnim.setPlayMode(Animation.PlayMode.LOOP);

        // Body - Run (288 width / 6 frames = 48)
        runTex = new Texture("assets/player/Body/Run1.png");
        TextureRegion[][] runTmp = TextureRegion.split(runTex, 48, 48);
        runAnim = new Animation<>(0.1f, runTmp[0]);
        runAnim.setPlayMode(Animation.PlayMode.LOOP);

        // Body - Jump (Assuming similar format, usually jump has separate Up/Down or
        // loop)
        // Let's check Jump1 size? Or just assume 48x48.
        jumpTex = new Texture("assets/player/Body/Jump1.png");
        // Assuming Jump1 is maybe just 1 frame or strip. Let's try split 48.
        TextureRegion[][] jumpTmp = TextureRegion.split(jumpTex, 48, 48);
        jumpAnim = new Animation<>(0.1f, jumpTmp[0]);

        // Hands (1-5)
        hands = new Array<>();
        for (int i = 1; i <= 5; i++) {
            // User said Hand files are "1.png" to "5.png"
            hands.add(new TextureRegion(new Texture("assets/player/Hand/" + i + ".png")));
        }

        // Gun
        gunTex = new Texture("assets/player/Gun/10_1.png");
    }

    public void addListener(LevelListener listener) {
        listeners.add(listener);
    }

    private void notifyLevelFinished() {
        for (LevelListener listener : listeners) {
            listener.onLevelFinished();
        }
    }

    public void update(float delta, Array<Wall> walls, float mapWidth, float mapHeight) {
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

        // Map Boundary Checks
        if (position.x < 0)
            position.x = 0;
        if (position.x > mapWidth - WIDTH)
            position.x = mapWidth - WIDTH;
        if (position.y < 0) { // If falls below map
            position.y = 0; // Or handle death/respawn
            isOnGround = true;
            velocity.y = 0;
        }

        updateCollider();

        // Apply air resistance only to portal momentum
        if (hasPortalMomentum) {
            velocity.x *= 0.984f;
            if (Math.abs(velocity.x) < 10f) {
                velocity.x = 0;
                hasPortalMomentum = false;
            }
        }

        stateTime += delta;
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

    public void dispose() {
        if (idleTex != null)
            idleTex.dispose();
        if (runTex != null)
            runTex.dispose();
        if (jumpTex != null)
            jumpTex.dispose();
        if (gunTex != null)
            gunTex.dispose();
        // Hands regions share textures if we loaded individually?
        // We loaded new Texture for each hand frame in the loop.
        for (TextureRegion tr : hands) {
            tr.getTexture().dispose();
        }
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

    public void render(SpriteBatch batch, Vector2 mousePos) {
        // Determine Facing
        boolean flipX = mousePos.x < (position.x + WIDTH / 2f);
        this.facingRight = !flipX; // Update local state for projectile or other logic

        // Select Body Animation
        TextureRegion currentBodyFrame;
        if (!isOnGround) {
            currentBodyFrame = jumpAnim.getKeyFrame(stateTime);
        } else if (Math.abs(velocity.x) > 10f) {
            currentBodyFrame = runAnim.getKeyFrame(stateTime, true);
        } else {
            currentBodyFrame = idleAnim.getKeyFrame(stateTime, true);
        }

        // Draw Body
        // Body texture is 48x48. Logic WIDTH is 50. Scale slightly?
        // Let's just draw 50x50.
        // Flip if needed
        if (currentBodyFrame.isFlipX() != flipX) {
            currentBodyFrame.flip(true, false);
        }

        batch.draw(currentBodyFrame, position.x, position.y, WIDTH, HEIGHT);

        // CALCULATE ARM ANGLE
        float centerX = position.x + WIDTH / 2f;
        float centerY = position.y + HEIGHT / 2f; // Shoulder height?
        // Shoulder is usually slightly higher than center for a 48px sprite.
        // Let's assume center for now.

        float angleDeg = MathUtils.atan2(mousePos.y - centerY, mousePos.x - centerX) * MathUtils.radDeg;

        // Normalize angle for Hand Index
        // Hand 1 (index 0) = Bottom?
        // Hand 1 (0) -> Hand 5 (4) -> Hand 10 (9)
        // User said "bawah hingga ke atas".
        // Let's assume range is -90 (Down) to 90 (Up).
        // If facing Left, the angle is 180 to 90 (Up) and -180 to -90 (Down).

        float relativeAngle = angleDeg;
        if (flipX) {
            // If facing left (-180 to 180), we want to map it to -90 to 90 relative to
            // "Left".
            // Angle 180 -> 0 relative.
            // Angle 90 -> 90.
            // Angle -90 -> -90.
            if (relativeAngle > 0)
                relativeAngle = 180 - relativeAngle; // 135 -> 45
            else
                relativeAngle = -180 - relativeAngle; // -135 -> -45
        }

        // Clamp relative angle -90 to 90
        float clampedAngle = MathUtils.clamp(relativeAngle, -90, 90);

        // Map -90..90 to Index 0..4 (5 frames)
        float t = (clampedAngle + 90f) / 180f;
        int handIndex = (int) (t * 4); // 0 to 4
        handIndex = MathUtils.clamp(handIndex, 0, 4);

        TextureRegion handRegion = hands.get(handIndex);

        // Draw Hand
        // User said "too far from body".
        // Let's assume the hand sprite's pivot (shoulder) is at (0, 16) [Left-Middle of
        // 32x32]?
        // Left-align hand to body center (tucked in).

        float handDrawX = centerX - 16 + (flipX ? 5 : -5); // Tucked in closer
        float handDrawY = centerY - 16; // Centered vertically on shoulder

        if (handRegion.isFlipX() != flipX)
            handRegion.flip(true, false);

        batch.draw(handRegion,
                handDrawX, handDrawY,
                16, 16, // origin
                32, 32, // size
                1, 1, // scale
                0);

        // Gun Rendering
        // Calculate visual angle from index (discrete steps)
        // 0 -> -90, 4 -> 90.
        float visualAngle = -90f + (handIndex / 4f) * 180f;

        // Arm Length
        // User said "Gun not connected".
        // Reduce arm length to bring it closer to hand tip.
        float armLength = 14f;

        // Angle corrections?
        // If visualAngle is -90 (Down), cos is 0, sin is -1.
        // Offset Y = -14. Gun is below.

        float gunOffsetX = MathUtils.cosDeg(visualAngle) * armLength;
        float gunOffsetY = MathUtils.sinDeg(visualAngle) * armLength;

        if (flipX) {
            gunOffsetX = -gunOffsetX;
        }

        // Adjust Gun Center
        // Gun 14x10. Center is 7, 5.

        // Fine tune position
        float gunX = centerX + (flipX ? 5 : -5) + gunOffsetX - 7;
        float gunY = centerY + gunOffsetY - 5;

        batch.draw(gunTex, gunX, gunY, 14, 10);

    }

    // Legacy ShapeRenderer
    public void render(ShapeRenderer sr) {
        // bounds debug
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
