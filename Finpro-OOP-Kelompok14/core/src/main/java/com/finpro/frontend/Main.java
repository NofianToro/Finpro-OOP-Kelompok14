package com.finpro.frontend;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finpro.frontend.factories.LevelFactory;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.commands.InputHandler;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private Player player;
    private Array<Wall> walls;
    private LevelFactory levelFactory;
    private InputHandler inputHandler;
    private GameManager gameManager;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        gameManager = GameManager.getInstance();

        camera = new OrthographicCamera(1280f, 720f);
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();

        levelFactory = new LevelFactory();
        walls = levelFactory.createLevel();

        player = new Player(levelFactory.getStartPosition());

        inputHandler = new InputHandler();

        gameManager.startGame();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        float delta = Gdx.graphics.getDeltaTime();

        inputHandler.handleInput(player, delta);
        player.update(delta, walls);

        if (player.isLevelFinished()) {
            System.out.println("Level Finished");
        }

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Generate Map
        for (Wall wall : walls) {
            wall.render(shapeRenderer);
        }

        player.render(shapeRenderer);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
