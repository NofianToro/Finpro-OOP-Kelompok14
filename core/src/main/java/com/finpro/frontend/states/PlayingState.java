package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.finpro.frontend.GameManager;
import com.finpro.frontend.Player;
import com.finpro.frontend.commands.InputHandler;
import com.finpro.frontend.factories.LevelFactory;
import com.finpro.frontend.obstacles.Wall;
import com.finpro.frontend.states.GameState;
import com.finpro.frontend.states.GameStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayingState implements GameState {
    private final GameStateManager gsm;
    private final ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private Player player;
    private Array<Wall> walls;
    private LevelFactory levelFactory;
    private InputHandler inputHandler;

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
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();

        levelFactory = new LevelFactory();
        walls = levelFactory.createLevel();

        player = new Player(levelFactory.getStartPosition());

        inputHandler = new InputHandler();

        GameManager.getInstance().startGame();
    }

    @Override
    public void update(float delta) {
        inputHandler.handleInput(player, delta);
        player.update(delta, walls);
        camera.update();
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

        player.render(shapeRenderer);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
