package com.finpro.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.finpro.frontend.services.BackendService;

public class GameManager {

    private static GameManager instance;

    private int score;
    private boolean gameActive;

    private BackendService backendService;
    private String currentPlayerId;

    private GameManager() {
        gameActive = false;
        backendService = new BackendService();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void startGame() {
        this.score = 0;
        this.gameActive = true;
        System.out.println("Game Started!");
    }

    public void setScore(int newScore) {
        if (this.gameActive) {
            this.score = newScore;
        }
    }

    public int getScore() {
        return this.score;
    }

    public boolean isGameActive() {
        return this.gameActive;
    }

    public void stopGame() {
        this.gameActive = false;
    }

    // Backend Service
    public void registerPlayer(String username) {
        backendService.createPlayer(username, new BackendService.RequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JsonValue jsonValue = new JsonReader().parse(response);
                    currentPlayerId = jsonValue.getString("playerId");
                    Gdx.app.log("GameManager", "Player ID Saved: " + currentPlayerId);
                } catch (Exception e) {
                    Gdx.app.error("GameManager", "Failed to parse player ID", e);
                }
            }

            @Override
            public void onError(String error) {
                Gdx.app.error("GameManager", "Register Error: " + error);
            }
        });
    }

    public BackendService getBackendService() {
        return backendService;
    }

    public void submitScore(long l1, long l2, long l3, long l4, long l5, BackendService.RequestCallback callback) {
        if (currentPlayerId == null) {
            Gdx.app.error("GameManager", "Cannot submit score: No Player ID");
            callback.onError("No Player ID");
            return;
        }
        backendService.submitScore(currentPlayerId, l1, l2, l3, l4, l5, callback);
    }
}
