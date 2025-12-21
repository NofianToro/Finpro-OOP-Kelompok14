package com.finpro.frontend.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class EndCreditsState implements GameState {
    private final GameStateManager gsm;
    private BitmapFont font;
    private float scrollY;
    private final String[] credits = {
        "Thank you for playing",
        "ESCAPE FROM I-CELL",
        "",
        "Created by:",
        "Fauzan",
        "Nofi",
        "Toro",
        "",
        "Asset Credits:",
        "Pixel Adventure (itch.io)",
        "LibGDX Team",
        "",
        "Press ESC to Skip"
    };

    public EndCreditsState(GameStateManager gsm) {
        this.gsm = gsm;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.5f);
        this.scrollY = -300; // Start below screen
    }

    @Override
    public void update(float delta) {
        scrollY += 100 * delta; // Scroll speed

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || scrollY > Gdx.graphics.getHeight() + 400) {
            gsm.set(new MenuState(gsm));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        
        float startY = scrollY;
        for (String line : credits) {
            font.draw(batch, line, 100, startY);
            startY -= 40; // Line spacing
        }
        
        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
