package com.finpro.frontend.commands;
import com.finpro.frontend.Player;

public class MoveLeftCommand implements Command {
    private Player player;

    @Override
    public void execute(Player player, float delta) {
        player.moveLeft(delta);
    }
}
