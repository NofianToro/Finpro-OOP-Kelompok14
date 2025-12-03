package com.finpro.frontend.commands;

import com.finpro.frontend.Player;

public class MoveRightCommand implements Command {
    Player player;

    @Override
    public void execute(Player player, float delta) {
        player.moveRight(delta);
    }
}
