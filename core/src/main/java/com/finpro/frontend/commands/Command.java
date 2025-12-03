package com.finpro.frontend.commands;

import com.finpro.frontend.Player;

public interface Command {
    public void execute(Player player, float delta);
}
