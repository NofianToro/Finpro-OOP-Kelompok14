package com.finpro.frontend.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.finpro.frontend.Player;
import com.finpro.frontend.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class InputHandler {
    private Map<Integer, Command> continuousCommands = new HashMap<>();
    private Map<Integer, Command> singlePressCommands = new HashMap<>();

    public InputHandler() {
        // Set Default Controls
        continuousCommands.put(Input.Keys.A, new MoveLeftCommand());
        continuousCommands.put(Input.Keys.D, new MoveRightCommand());
        singlePressCommands.put(Input.Keys.W, new JumpCommand());
    }

    // Method to change bindings at runtime
    public void rebindKey(int oldKey, int newKey, boolean isContinuous) {
        Map<Integer, Command> targetMap = isContinuous ? continuousCommands : singlePressCommands;

        if (targetMap.containsKey(oldKey)) {
            Command cmd = targetMap.remove(oldKey);
            targetMap.put(newKey, cmd);
        }
    }

    public void handleInput(Player player, float delta) {
        // 1. Handle Continuous inputs (Movement)
        for (Map.Entry<Integer, Command> entry : continuousCommands.entrySet()) {
            if (Gdx.input.isKeyPressed(entry.getKey())) {
                entry.getValue().execute(player, delta);
            }
        }

        // 2. Handle Single Press inputs (Jump)
        for (Map.Entry<Integer, Command> entry : singlePressCommands.entrySet()) {
            if (Gdx.input.isKeyJustPressed(entry.getKey())) {
                entry.getValue().execute(player, delta);
            }
        }

        // 3. Handle Mouse/Shoot (Generic Commands)
        // For now, we manually execute shoot command if registered, or we could add a
        // list of 'AlwaysExecuteCommands'
        if (shootCommand != null) {
            shootCommand.execute(player, delta);
        }
    }

    private Command shootCommand;

    public void setShootCommand(Command cmd) {
        this.shootCommand = cmd;
    }
}
