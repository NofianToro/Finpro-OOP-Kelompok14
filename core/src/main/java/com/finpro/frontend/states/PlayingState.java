package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
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
import com.finpro.frontend.observers.LevelListener;
import com.finpro.frontend.pools.PortalPool;
import com.finpro.frontend.pools.ProjectilePool;
import com.finpro.frontend.strategies.LinearMovementStrategy;
import com.finpro.frontend.strategies.ProjectileMovementStrategy;

import java.util.Iterator;

public class PlayingState implements GameState, LevelListener {

    private final GameStateManager gsm;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float mapWidth, mapHeight;

    private Player player;
    private Array<Wall> walls;
    private LevelFactory levelFactory;
    private InputHandler inputHandler;

    private PortalPool portalPool;
    private ProjectilePool projectilePool;
    private PortalFactory portalFactory;
    private Array<Projectile> activeProjectiles;
    private Array<Portal> activePortals;
    private ProjectileMovementStrategy linearStrategy;

    private final int screenWidth;
    private final int screenHeight;

    private final String[] levels = { "map/intro.tmx", "map/level_1.tmx", "map/level_2.tmx", "map/level_3.tmx", "map/level_4.tmx" };
    private int currentLevelIndex = 0;

    public PlayingState(GameStateManager gsm) {
        this.gsm = gsm;
        this.shapeRenderer = new ShapeRenderer();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        loadLevel(currentLevelIndex);

        inputHandler = new InputHandler();
        inputHandler.setShootCommand(new ShootCommand(this));

        GameManager.getInstance().startGame();
    }

    private void loadLevel(int levelIndex) {
        if (map != null)
            map.dispose();
        if (activeProjectiles != null)
            activeProjectiles.clear();
        if (activePortals != null)
            activePortals.clear();

        try {
            map = new TmxMapLoader().load(levels[levelIndex]);
        } catch (Exception e) {
            Gdx.app.exit();
            return;
        }

        if (mapRenderer == null) {
            mapRenderer = new OrthogonalTiledMapRenderer(map);
        } else {
            mapRenderer.setMap(map);
        }

        calculateMapDimensions();

        levelFactory = new LevelFactory(map);
        walls = levelFactory.parseWalls();

        Vector2 spawnPos = levelFactory.getPlayerSpawnPoint(map);
        player = new Player(spawnPos.x, spawnPos.y);
        player.addListener(this);

        if (portalPool == null)
            portalPool = new PortalPool();
        if (projectilePool == null)
            projectilePool = new ProjectilePool();
        if (portalFactory == null)
            portalFactory = new PortalFactory(portalPool);

        activeProjectiles = new Array<>();
        activePortals = new Array<>();
        linearStrategy = new LinearMovementStrategy();

        camera.position.set(player.getBounds().x, player.getBounds().y, 0);
        camera.update();
    }

    @Override
    public void onLevelFinished() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.length) {
            loadLevel(currentLevelIndex);
        } else {
            currentLevelIndex = 0;
            loadLevel(currentLevelIndex);
        }
    }

    private void calculateMapDimensions() {
        MapProperties prop = map.getProperties();
        int mapW = prop.get("width", Integer.class);
        int mapH = prop.get("height", Integer.class);
        int tileW = prop.get("tilewidth", Integer.class);
        int tileH = prop.get("tileheight", Integer.class);

        mapWidth = mapW * tileW;
        mapHeight = mapH * tileH;
    }

    @Override
    public void update(float delta) {
        inputHandler.handleInput(player, delta);
        player.update(delta, walls);
        updateProjectiles(delta);
        updatePortals(delta);
        updateCamera();
    }

    private void updateCamera() {
        float camX = player.getBounds().x + player.getBounds().width / 2f;
        float camY = player.getBounds().y + player.getBounds().height / 2f;

        camX = MathUtils.clamp(camX, screenWidth / 2f, mapWidth - screenWidth / 2f);
        camY = MathUtils.clamp(camY, screenHeight / 2f, mapHeight - screenHeight / 2f);

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    private void updateProjectiles(float delta) {
        Iterator<Projectile> iter = activeProjectiles.iterator();
        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(delta);

            for (Wall wall : walls) {
                if (p.getBounds().overlaps(wall.getBounds())) {

                    if (wall.isBlackWall()) {
                        p.setActive(false);
                        projectilePool.free(p);
                        iter.remove();
                        return;
                    }

                    spawnPortal(p, wall);
                    p.setActive(false);
                    projectilePool.free(p);
                    iter.remove();
                    return;
                }
            }

            if (p.getBounds().x < 0 || p.getBounds().x > mapWidth ||
                    p.getBounds().y < 0 || p.getBounds().y > mapHeight) {
                p.setActive(false);
                projectilePool.free(p);
                iter.remove();
            }
        }
    }

    private void updatePortals(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetPortals();
        }

        for (Portal portal : activePortals) {
            if (player.getBounds().overlaps(portal.getBounds()) && portal.getLinkedPortal() != null) {
                teleportPlayer(portal);
            }
        }
    }

    private void resetPortals() {
        for (Portal p : activePortals) {
            p.setActive(false);
            portalPool.free(p);
        }
        activePortals.clear();
    }

    private void teleportPlayer(Portal entryPortal) {
        Portal exit = entryPortal.getLinkedPortal();
        float offset = 50f;

        float targetX = exit.getPosition().x;
        float targetY = exit.getPosition().y;

        // Get player velocity
        Vector2 currentVelocity = player.getVelocity();
        Vector2 entryNormal = entryPortal.getNormal();
        Vector2 exitNormal = exit.getNormal();

        // Calculate velocity
        float speedIntoPortal = -(currentVelocity.x * entryNormal.x + currentVelocity.y * entryNormal.y);

        // Calculate perpendicular velocity
        float entryTangentX = -entryNormal.y;
        float entryTangentY = entryNormal.x;
        float tangentSpeed = currentVelocity.x * entryTangentX + currentVelocity.y * entryTangentY;

        // Exit tangent (perpendicular to exit normal)
        float exitTangentX = -exitNormal.y;
        float exitTangentY = exitNormal.x;

        // New velocity: speed coming OUT of exit portal along exit normal + tangent
        // component
        float newVelX = speedIntoPortal * exitNormal.x + tangentSpeed * exitTangentX;
        float newVelY = speedIntoPortal * exitNormal.y + tangentSpeed * exitTangentY;

        // Calculate exit position
        if (exit.getOrientation() == Portal.Orientation.HORIZONTAL) {
            targetX += (exit.getBounds().width - player.getBounds().width) / 2f;
            targetY += exit.getNormal().y * offset;
        } else {
            targetX += exit.getNormal().x * offset;
            targetY += (exit.getBounds().height - player.getBounds().height) / 2f;
        }

        player.setPosition(targetX, targetY);
        player.setVelocity(newVelX, newVelY);
    }

    public void shoot(String type, float screenX, float screenY) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));

        float pX = player.getBounds().x + player.getBounds().width / 2f;
        float pY = player.getBounds().y + player.getBounds().height / 2f;

        Vector2 dir = new Vector2(world.x - pX, world.y - pY).nor();
        float speed = 800f;

        Projectile p = projectilePool.obtain();
        p.init(pX, pY, dir.x * speed, dir.y * speed, type, linearStrategy);
        activeProjectiles.add(p);
    }

    private void spawnPortal(Projectile p, Wall wall) {
        Iterator<Portal> iter = activePortals.iterator();
        while (iter.hasNext()) {
            Portal existing = iter.next();
            if (existing.getType().equals(p.getType())) {
                existing.setActive(false);
                portalPool.free(existing);
                iter.remove();
            }
        }

        Rectangle pr = p.getBounds();
        Rectangle wr = wall.getBounds();

        float iw = Math.min(pr.x + pr.width, wr.x + wr.width) - Math.max(pr.x, wr.x);
        float ih = Math.min(pr.y + pr.height, wr.y + wr.height) - Math.max(pr.y, wr.y);

        boolean horizontal = iw > ih;

        float portalLength = 64f;
        float padding = 4f;
        float portalX, portalY;

        // 1. Initial Clamping to the Parent Wall
        if (horizontal) {
            float prCenterX = pr.x + pr.width / 2f;
            portalX = prCenterX - portalLength / 2f;
            portalX = MathUtils.clamp(portalX, wr.x + padding, wr.x + wr.width - portalLength - padding);
            portalY = pr.y;
        } else {
            float prCenterY = pr.y + pr.height / 2f;
            portalY = prCenterY - portalLength / 2f;
            portalY = MathUtils.clamp(portalY, wr.y + padding, wr.y + wr.height - portalLength - padding);
            portalX = pr.x;
        }

        // 2. Obstruction Check: Push away from other overlapping walls
        Rectangle proposed = new Rectangle(portalX, portalY,
                horizontal ? portalLength : 10f,
                horizontal ? 10f : portalLength);

        for (Wall other : walls) {
            if (other == wall)
                continue; // Don't check against the wall we are attached to

            if (proposed.overlaps(other.getBounds())) {
                Rectangle ow = other.getBounds();

                if (horizontal) {
                    // Check if obstruction is to the Right or Left
                    // We compare centers to decide push direction
                    if (ow.x + ow.width / 2f > proposed.x + proposed.width / 2f) {
                        // Obstruction is on the Right -> Push Left
                        portalX = ow.x - portalLength - padding;
                    } else {
                        // Obstruction is on the Left -> Push Right
                        portalX = ow.x + ow.width + padding;
                    }
                } else {
                    // Vertical: Check Top or Bottom
                    if (ow.y + ow.height / 2f > proposed.y + proposed.height / 2f) {
                        // Obstruction is Above -> Push Down
                        portalY = ow.y - portalLength - padding;
                    } else {
                        // Obstruction is Below -> Push Up
                        portalY = ow.y + ow.height + padding;
                    }
                }
            }
        }

        // 3. Final Re-Clamping (ensure we didn't push it off the parent wall)
        if (horizontal) {
            portalX = MathUtils.clamp(portalX, wr.x + padding, wr.x + wr.width - portalLength - padding);
        } else {
            portalY = MathUtils.clamp(portalY, wr.y + padding, wr.y + wr.height - portalLength - padding);
        }

        // 4. Check for overlaps with other active portals
        Rectangle finalProposed = new Rectangle(portalX, portalY,
                horizontal ? portalLength : 10f,
                horizontal ? 10f : portalLength);

        for (Portal active : activePortals) {
            if (active.getBounds().overlaps(finalProposed)) {
                return; // Cancel spawn if overlapping another portal
            }
        }

        Portal portal = portalFactory.createPortal(
                portalX, portalY, p.getType(), horizontal,
                p.getVelocity().x, p.getVelocity().y);

        activePortals.add(portal);

        if (activePortals.size == 2) {
            activePortals.get(0).setLinkedPortal(activePortals.get(1));
            activePortals.get(1).setLinkedPortal(activePortals.get(0));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.PURPLE);

        mapRenderer.setView(camera);
        mapRenderer.render();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Portal portal : activePortals)
            portal.render(shapeRenderer);
        for (Projectile p : activeProjectiles)
            p.render(shapeRenderer);
        player.render(shapeRenderer);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        if (map != null)
            map.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
    }
}
