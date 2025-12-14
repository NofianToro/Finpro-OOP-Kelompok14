package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finpro.frontend.GameManager;
import com.finpro.frontend.Player;
import com.finpro.frontend.Portal;
import com.finpro.frontend.Projectile;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.commands.ShootCommand;
import com.finpro.frontend.factories.LevelFactory;
import com.finpro.frontend.factories.PortalFactory;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.pools.PortalPool;
import com.finpro.frontend.pools.ProjectilePool;
import com.finpro.frontend.strategies.LinearMovementStrategy;
import com.finpro.frontend.strategies.ProjectileMovementStrategy;

import java.util.Iterator;

public class PlayingState implements GameState {
    private final GameStateManager gsm;
    private final ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private Player player;
    private Array<Wall> walls;
    private LevelFactory levelFactory;
    private InputHandler inputHandler;

    // Portal Mechanics
    private PortalPool portalPool;
    private ProjectilePool projectilePool;
    private PortalFactory portalFactory;

    private Array<Projectile> activeProjectiles;
    private Array<Portal> activePortals;

    // Strategy instance (stateless or shared)
    private ProjectileMovementStrategy linearStrategy;

    private final OrthographicCamera camera;
    private final float cameraOffset = 0.2f;
    private final int screenWidth;
    private final int screenHeight;

    private int lastLoggedScore = -1;

    public PlayingState(GameStateManager gsm) {

        this.gsm = gsm;
        this.shapeRenderer = new ShapeRenderer();
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1280f, 720f);
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();

        levelFactory = new LevelFactory();
        walls = levelFactory.createLevel();

        player = new Player(levelFactory.getStartPosition());

        // Initialize Portal System
        portalPool = new PortalPool();
        projectilePool = new ProjectilePool();
        portalFactory = new PortalFactory(portalPool);

        activeProjectiles = new Array<>();
        activePortals = new Array<>(); // Max 2 portals normally
        linearStrategy = new LinearMovementStrategy();

        inputHandler = new InputHandler();
        inputHandler.setShootCommand(new ShootCommand(this));

        GameManager.getInstance().startGame();
    }

    @Override
    public void update(float delta) {
        inputHandler.handleInput(player, delta);
        player.update(delta, walls);

        updateProjectiles(delta);
        updatePortals(delta);

        camera.update();
    }

    private void updateProjectiles(float delta) {
        Iterator<Projectile> iter = activeProjectiles.iterator();
        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(delta);

            // Check Wall Collision
            for (Wall wall : walls) {
                if (p.getBounds().overlaps(wall.getBounds())) {
                    spawnPortal(p, wall);
                    p.setActive(false);
                    projectilePool.free(p);
                    iter.remove();
                    break;
                }
            }

            // Remove if out of bounds or too far (simple check)
            if (p.isActive() && (p.getBounds().x < 0 || p.getBounds().x > 2000 || p.getBounds().y < 0)) {
                p.setActive(false);
                projectilePool.free(p);
                iter.remove();
            }
        }
    }

    private void updatePortals(float delta) {
        // Here we handle teleportation logic
        // Here we handle teleportation logic
        for (Portal portal : activePortals) {
            if (player.getBounds().overlaps(portal.getBounds()) && portal.getLinkedPortal() != null) {
                Portal exit = portal.getLinkedPortal();

                // Calculate target position with offset
                // Offset should be large enough to clear the wall: Player Width or slightly
                // more
                float offsetDistance = 60f;
                float targetX = exit.getPosition().x + (exit.getNormal().x * offsetDistance);
                float targetY = exit.getPosition().y + (exit.getNormal().y * offsetDistance);

                // Center alignment
                if (exit.getOrientation() == Portal.Orientation.HORIZONTAL) {
                    // Check bounds to ensure we don't spawn half-in wall if portal is near edge?
                    // For now, center on X axis of portal
                    targetX = exit.getPosition().x + (exit.getBounds().width / 2) - (player.getBounds().width / 2);
                    targetY = exit.getPosition().y + (exit.getNormal().y * offsetDistance);
                } else {
                    targetX = exit.getPosition().x + (exit.getNormal().x * offsetDistance);
                    targetY = exit.getPosition().y + (exit.getBounds().height / 2) - (player.getBounds().height / 2);
                }

                // Check for Wall Collisions at Target
                boolean safe = true;
                Rectangle testRect = new Rectangle(targetX, targetY, player.getBounds().width,
                        player.getBounds().height);
                for (Wall w : walls) {
                    if (testRect.overlaps(w.getBounds())) {
                        safe = false;
                        break;
                    }
                }

                if (safe) {
                    player.setPosition(targetX, targetY);
                    // Reset or Redirect Velocity?
                    // Ideally maintain momentum or boost it out
                    // For now, minimal clear velocity out
                    // player.setVelocity(exit.getNormal().x * 300, exit.getNormal().y * 300);
                }
            }
        }
    }

    public void shoot(String type, float screenX, float screenY) {
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0));

        float pX = player.getBounds().x + player.getBounds().width / 2;
        float pY = player.getBounds().y + player.getBounds().height / 2;

        Vector2 direction = new Vector2(worldCoordinates.x - pX, worldCoordinates.y - pY).nor();
        float speed = 600f; // Projectile speed

        Projectile p = projectilePool.obtain();
        p.init(pX, pY, direction.x * speed, direction.y * speed, type, linearStrategy);
        activeProjectiles.add(p);
    }

    private void spawnPortal(Projectile p, Wall wall) {
        // Remove existing portal of same color
        Iterator<Portal> iter = activePortals.iterator();
        while (iter.hasNext()) {
            Portal existing = iter.next();
            if (existing.getType().equals(p.getType())) {
                existing.setActive(false);
                portalPool.free(existing);
                iter.remove();
            }
        }

        // Refined Orientation Logic:
        // Check if the projectile was vertically overlapping the wall in the previous
        // frame.
        // If it was NOT overlapping Y, it means it entered from Top or Bottom ->
        // Horizontal.
        // If it WAS overlapping Y, it means it entered from Side -> Vertical.

        float prevY = p.getPreviousPosition().y;
        float prevTop = prevY + p.getBounds().height;
        float wallY = wall.getBounds().y;
        float wallTop = wall.getBounds().y + wall.getBounds().height;

        boolean wasOverlappingY = prevY < wallTop && prevTop > wallY;
        boolean isHorizontal = !wasOverlappingY;

        // Create new portal
        Portal newPortal = portalFactory.createPortal(
                p.getBounds().x,
                p.getBounds().y,
                p.getType(),
                isHorizontal,
                p.getVelocity().x,
                p.getVelocity().y);
        activePortals.add(newPortal);

        // Link portals if 2 exist
        if (activePortals.size == 2) {
            Portal p1 = activePortals.get(0);
            Portal p2 = activePortals.get(1);
            p1.setLinkedPortal(p2);
            p2.setLinkedPortal(p1);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        float delta = Gdx.graphics.getDeltaTime();

        if (player.isLevelFinished()) {
            System.out.println("Level Finished");
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Generate Map
        for (Wall wall : walls) {
            wall.render(shapeRenderer);
        }

        // Render Portals
        for (Portal portal : activePortals) {
            portal.render(shapeRenderer);
        }

        // Render Projectiles
        for (Projectile p : activeProjectiles) {
            p.render(shapeRenderer);
        }

        player.render(shapeRenderer);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
