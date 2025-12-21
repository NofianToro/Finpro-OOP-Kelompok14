package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.finpro.frontend.Bullet;
import com.finpro.frontend.GameManager;
import com.finpro.frontend.Player;
import com.finpro.frontend.Portal;
import com.finpro.frontend.Projectile;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.commands.ShootCommand;
import com.finpro.frontend.factories.LevelFactory;
import com.finpro.frontend.factories.PortalFactory;
import com.finpro.frontend.obstacles.Turret;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.observers.LevelListener;
import com.finpro.frontend.observers.TimerObserver;
import com.finpro.frontend.pools.BulletPool;
import com.finpro.frontend.pools.PortalPool;
import com.finpro.frontend.pools.ProjectilePool;
import com.finpro.frontend.strategies.LinearMovementStrategy;
import com.finpro.frontend.strategies.ProjectileMovementStrategy;
import com.finpro.frontend.utils.GameTimer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlayingState implements GameState, LevelListener, TimerObserver {

    private final GameStateManager gsm;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float mapWidth, mapHeight;
    private Player player;
    private Array<Wall> walls;
    private Array<Turret> turrets;
    private LevelFactory levelFactory;
    private InputHandler inputHandler;
    private GameTimer gameTimer;
    private String timerTextLevel = "";
    private String timerTextTotal = "";
    private Map<Integer, Long> levelTimes;
    private BitmapFont timerFont;
    private PortalPool portalPool;
    private ProjectilePool projectilePool;
    private BulletPool bulletPool;
    private PortalFactory portalFactory;
    private Array<Projectile> activeProjectiles;
    private Array<Bullet> activeBullets;
    private Array<Portal> activePortals;
    private ProjectileMovementStrategy linearStrategy;
    private Texture bluePortalTex, orangePortalTex;
    private Texture bulletBlueTex, bulletOrangeTex, bulletRedTex;
    private Animation<TextureRegion> bluePortalAnim, orangePortalAnim;
    private SpriteBatch batch;
    private final int screenWidth;
    private final int screenHeight;

    private final String[] levels = { "map/intro.tmx", "map/level_1.tmx", "map/level_2.tmx", "map/level_3.tmx",
            "map/level_4.tmx", "map/level_5.tmx" };"map/level_4.tmx","map/level_5.tmx"};
    private int currentLevelIndex = 0;

    private boolean isLevelTransitioning = false;
    private GlyphLayout layout;

    public PlayingState(GameStateManager gsm) {
        this.gsm = gsm;
        this.shapeRenderer = new ShapeRenderer();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        inputHandler = new InputHandler();
        inputHandler.setShootCommand(new ShootCommand(this));

        GameManager.getInstance().startGame();

        timerFont = new BitmapFont();
        timerFont.setColor(Color.WHITE);
        timerFont.getData().setScale(1.5f);

        layout = new GlyphLayout();

        gameTimer = new GameTimer();
        gameTimer.addObserver(this);
        levelTimes = new HashMap<>();

        loadLevel(currentLevelIndex);

        gameTimer.start();
    }

    private void loadLevel(int levelIndex) {
        // Reset flag transisi setiap load level
        isLevelTransitioning = false;

        if (map != null)
            map.dispose();
        if (activeProjectiles != null)
            activeProjectiles.clear();
        if (activeBullets != null)
            activeBullets.clear();
        if (activePortals != null)
            activePortals.clear();

        if (gameTimer != null)
            gameTimer.resetLevelTimer();

        try {
            map = new TmxMapLoader().load(levels[levelIndex]);
        } catch (Exception e) {
            Gdx.app.error("PlayingState", "Failed to load map: " + levels[levelIndex]);
            if (levelIndex == 0)
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
        if (bulletPool == null)
            bulletPool = new BulletPool();
        if (portalFactory == null)
            portalFactory = new PortalFactory(portalPool);

        activeProjectiles = new Array<>();
        activeBullets = new Array<>();
        activePortals = new Array<>();
        turrets = levelFactory.parseTurrets(bulletPool);

        linearStrategy = new LinearMovementStrategy();

        if (batch == null)
            batch = new SpriteBatch();

        if (bluePortalTex == null) {
            bluePortalTex = new Texture("assets/portal_strip_blue.png");
            TextureRegion[][] tmp = TextureRegion.split(bluePortalTex, bluePortalTex.getWidth() / 8,
                    bluePortalTex.getHeight());
                    bluePortalTex.getHeight());
            bluePortalAnim = new Animation<>(0.1f, tmp[0]);
            bluePortalAnim.setPlayMode(Animation.PlayMode.LOOP);
        }
        if (orangePortalTex == null) {
            orangePortalTex = new Texture("assets/portal_strip_orange.png");
            TextureRegion[][] tmp = TextureRegion.split(orangePortalTex, orangePortalTex.getWidth() / 8,
                    orangePortalTex.getHeight());
                    orangePortalTex.getHeight());
            orangePortalAnim = new Animation<>(0.1f, tmp[0]);
            orangePortalAnim.setPlayMode(Animation.PlayMode.LOOP);
        }

        if (bulletBlueTex == null) {
            bulletBlueTex = new Texture("assets/player/Bullet/bullet_blue.png");
        }
        if (bulletOrangeTex == null) {
            bulletOrangeTex = new Texture("assets/player/Bullet/bullet_orange.png");
        }
        if (bulletRedTex == null) {
            bulletRedTex = new Texture("assets/player/Bullet/bullet_red.png");
        }
        camera.position.set(player.getBounds().x, player.getBounds().y, 0);
        camera.update();
    }

    @Override
    public void onLevelFinished() {
        if (isLevelTransitioning)
            return;

        // Record level time
        long duration = gameTimer.getLevelDuration();
        levelTimes.put(currentLevelIndex, duration);
        Gdx.app.log("PlayingState", "Level " + currentLevelIndex + " finished in " + duration + "ms");

        // Aktifkan mode transisi (layar hitam)
        isLevelTransitioning = true;

    }

    private void proceedToNextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.length) {
            loadLevel(currentLevelIndex);
        } else {
            // Calculate total time
            long totalTime = 0;
            for (int i = 1; i <= 5; i++) {
                totalTime += levelTimes.getOrDefault(i, 0L);
            }

            Gdx.app.log("PlayingState", "Game Finished! Total Time: " + totalTime);
            gameTimer.stop();
            gameTimer.removeObserver(this);
            gsm.set(new EndScreenState(gsm, levelTimes, totalTime));
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

    public void update(float delta) {
        if (isLevelTransitioning) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                proceedToNextLevel();
            }
            return;
        }

        if (gameTimer != null)
            gameTimer.update();

        inputHandler.handleInput(player, delta);
        player.update(delta, walls, mapWidth, mapHeight);

        updateTurrets(delta);
        updateProjectiles(delta);
        updateBullets(delta);
        updatePortals(delta);
        updateCamera();
    }

    // ... (Metode updateTurrets, updateBullets, updateCamera, updateProjectiles,
    // updatePortals, resetPortals, teleportPlayer, shoot, spawnPortal TETAP SAMA)
    // ...

    private void updateTurrets(float delta) {
        for (Turret turret : turrets) {
            turret.update(delta, activeBullets);
        }
    }

    private void updateBullets(float delta) {
        Iterator<Bullet> iter = activeBullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            b.update(delta);
            for (Wall wall : walls) {
                if (b.getBounds().overlaps(wall.getBounds())) {
                    b.setActive(false);
                    bulletPool.free(b);
                    iter.remove();
                    break;
                }
            }
            if (activeBullets.contains(b, true)) {
                if (b.getBounds().x < 0 || b.getBounds().x > mapWidth ||
                        b.getBounds().y < 0 || b.getBounds().y > mapHeight) {
                        b.getBounds().y < 0 || b.getBounds().y > mapHeight) {
                    b.setActive(false);
                    bulletPool.free(b);
                    iter.remove();
                }
            }
        }
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

                    if (wall.isBlackWall() || wall.isExit()) {
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
        Vector2 currentVelocity = player.getVelocity();
        Vector2 entryNormal = entryPortal.getNormal();
        Vector2 exitNormal = exit.getNormal();
        float speedIntoPortal = -(currentVelocity.x * entryNormal.x + currentVelocity.y * entryNormal.y);

        float entryTangentX = -entryNormal.y;
        float entryTangentY = entryNormal.x;
        float tangentSpeed = currentVelocity.x * entryTangentX + currentVelocity.y * entryTangentY;

        float exitTangentX = -exitNormal.y;
        float exitTangentY = exitNormal.x;

        float newVelX = speedIntoPortal * exitNormal.x + tangentSpeed * exitTangentX;
        float newVelY = speedIntoPortal * exitNormal.y + tangentSpeed * exitTangentY;

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

        float prevY = p.getPreviousPosition().y;
        float prevTop = prevY + p.getBounds().height;
        float wallY = wall.getBounds().y;
        float wallTop = wall.getBounds().y + wall.getBounds().height;

        boolean wasOverlappingY = prevY < wallTop && prevTop > wallY;
        boolean horizontal = !wasOverlappingY;

        float portalLength = 64f;
        float portalDepth = 10f;
        float padding = 4f;

        float portalX, portalY;

        Rectangle wr = wall.getBounds();
        Rectangle pr = p.getBounds();

        if (horizontal) {
            portalX = pr.x + pr.width / 2f - portalLength / 2f;
            portalX = MathUtils.clamp(portalX, wr.x + padding, wr.x + wr.width - portalLength - padding);

            if (p.getVelocity().y < 0) {
                portalY = wr.y + wr.height;
            } else {
                portalY = wr.y - portalDepth;
            }
        } else {
            portalY = pr.y + pr.height / 2f - portalLength / 2f;
            portalY = MathUtils.clamp(portalY, wr.y + padding, wr.y + wr.height - portalLength - padding);

            if (p.getVelocity().x > 0) {
                portalX = wr.x - portalDepth;
            } else {
                portalX = wr.x + wr.width;
            }
        }

        Rectangle proposed = new Rectangle(portalX, portalY,
                horizontal ? portalLength : portalDepth,
                horizontal ? portalDepth : portalLength);
                horizontal ? portalLength : portalDepth,
                horizontal ? portalDepth : portalLength);

        for (Wall other : walls) {
            if (other == wall)
                continue;
            if (proposed.overlaps(other.getBounds())) {
                return;
            }
        }

        for (Portal active : activePortals) {
            if (active.getBounds().overlaps(proposed)) {
                return;
            }
        }

        Portal portal = portalFactory.createPortal(
                portalX, portalY, p.getType(), horizontal,
                p.getVelocity().x, p.getVelocity().y);
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

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Portal portal : activePortals) {
            portal.update(Gdx.graphics.getDeltaTime());
            if ("BLUE".equals(portal.getType())) {
                portal.render(batch, bluePortalAnim);
            } else {
                portal.render(batch, orangePortalAnim);
            }
        }

        Vector3 mousePos3 = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mousePos3.x, mousePos3.y);

        player.render(batch, mousePos);

        for (Projectile p : activeProjectiles) {
            String type = p.getType();
            if ("ORANGE".equals(type)) {
                p.render(batch, bulletOrangeTex);
            } else {
                p.render(batch, bulletBlueTex);
            }
        }

        // Render Turret Bullets
        for (Bullet b : activeBullets) {
            b.render(batch, bulletRedTex);
        }

        renderTimerHUD(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Turret turret : turrets) {
            turret.render(shapeRenderer);
        }
        shapeRenderer.end();

        if (isLevelTransitioning) {
            // Aktifkan blending agar bisa transparansi (optional, jika ingin hitam pekat
            // alpha = 1)
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(camera.projection);
            shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, screenHeight);
            shapeRenderer.updateMatrices();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 1f);
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
            batch.getProjectionMatrix().setToOrtho2D(0, 0, screenWidth, screenHeight);
            batch.setProjectionMatrix(batch.getProjectionMatrix());

            String text1 = "Level Complete!";
            String text2 = "Press SPACE to Continue";

            timerFont.getData().setScale(2.0f);
            layout.setText(timerFont, text1);
            timerFont.draw(batch, text1, (screenWidth - layout.width) / 2, (screenHeight / 2) + 50);

            timerFont.getData().setScale(1.5f);
            layout.setText(timerFont, text2);
            timerFont.draw(batch, text2, (screenWidth - layout.width) / 2, (screenHeight / 2) - 50);

            batch.end();
            shapeRenderer.setProjectionMatrix(camera.combined);
        }
    }

    private void renderTimerHUD(SpriteBatch batch) {
        if (timerFont != null) {
            timerFont.getData().setScale(1.5f);
            timerFont.draw(batch, timerTextLevel, 10, screenHeight - 10);
            timerFont.draw(batch, timerTextTotal, 10, screenHeight - 35);
        }
    }

    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long ms = millis % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, ms);
    }

    @Override
    public void onTick(long levelTime, long totalTime) {
        timerTextLevel = "Level: " + formatTime(levelTime);
        timerTextTotal = "Total: " + formatTime(totalTime);
    }

    @Override
    public void dispose() {
        if (timerFont != null)
            timerFont.dispose();
        shapeRenderer.dispose();
        if (batch != null)
            batch.dispose();
        if (bluePortalTex != null)
            bluePortalTex.dispose();
        if (orangePortalTex != null)
            orangePortalTex.dispose();
        if (bulletBlueTex != null)
            bulletBlueTex.dispose();
        if (bulletOrangeTex != null)
            bulletOrangeTex.dispose();

        if (map != null)
            map.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (player != null)
            player.dispose();
    }
}
