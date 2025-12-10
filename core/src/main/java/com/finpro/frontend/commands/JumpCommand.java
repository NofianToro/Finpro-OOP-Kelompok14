package com.finpro.frontend.commands;

import com.finpro.frontend.Player;

public class JumpCommand implements Command {
    Player player;

    @Override
    public void execute(Player player, float delta) {
        player.jump();
    }
}
