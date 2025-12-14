package com.finpro.frontend.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.finpro.frontend.Player;
import com.finpro.frontend.states.PlayingState;

public class ShootCommand implements Command {
    private PlayingState playingState;

    public ShootCommand(PlayingState playingState) {
        this.playingState = playingState;
    }

    @Override
    public void execute(Player player, float delta) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            playingState.shoot("BLUE", mouseX, mouseY);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            playingState.shoot("ORANGE", mouseX, mouseY);
        }
    }
}
