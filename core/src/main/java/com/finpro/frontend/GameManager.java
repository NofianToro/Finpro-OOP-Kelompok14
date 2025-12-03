package com.finpro.frontend;

public class GameManager {

    private static GameManager instance;

    private int score;
    private boolean gameActive;

    private GameManager() {
        this.score = 0;
        this.gameActive = false;
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
}
