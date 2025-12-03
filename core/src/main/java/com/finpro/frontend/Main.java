package com.finpro.frontend;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.finpro.frontend.commands.InputHandler;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private Player player;
    private Ground ground;
    private GameManager gameManager;
    private InputHandler inputHandler;

    private OrthographicCamera camera;
    private float cameraOffset = 0.2f;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        gameManager = GameManager.getInstance();

        camera = new OrthographicCamera(1280f, 720f);
        camera.setToOrtho(false);

        player = new Player(new Vector2(0, Gdx.graphics.getHeight() / 2f));
        inputHandler = new InputHandler();
        ground = new Ground();

        gameManager.startGame();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(Color.BLACK);

        inputHandler.handleInput(player, delta);
        update(delta);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ground.renderShape(shapeRenderer);
        player.renderShape(shapeRenderer);
        shapeRenderer.end();
    }

    private void update(float delta) {

        // Update player logic (gravity, etc.)
        player.update(delta);

        // Placeholder playing update
        ground.update(camera.position.x);
        player.checkBoundaries(ground, Gdx.graphics.getHeight());
    }

    private void updateCamera(float delta) {
        camera.position.x = player.getPosition().x + player.getWidth() + cameraOffset;
        camera.update();
    }

    @Override
    public void dispose() {
        gameManager.stopGame();
        shapeRenderer.dispose();
    }
}
