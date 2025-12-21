package com.finpro.frontend.utils;

import com.finpro.frontend.observers.TimerObserver;
import java.util.ArrayList;
import java.util.List;

public class GameTimer {
    private List<TimerObserver> observers;
    private long gameStartTime;
    private long levelStartTime;
    private boolean isRunning;

    public GameTimer() {
        this.observers = new ArrayList<>();
        this.gameStartTime = System.currentTimeMillis();
        this.levelStartTime = System.currentTimeMillis();
        this.isRunning = true;
    }

    public void addObserver(TimerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TimerObserver observer) {
        observers.remove(observer);
    }

    public void resetLevelTimer() {
        this.levelStartTime = System.currentTimeMillis();
    }

    public void update() {
        if (!isRunning)
            return;

        long currentTime = System.currentTimeMillis();
        long levelTime = currentTime - levelStartTime;
        long totalTime = currentTime - gameStartTime;

        notifyObservers(levelTime, totalTime);
    }

    private void notifyObservers(long levelTime, long totalTime) {
        for (TimerObserver observer : observers) {
            observer.onTick(levelTime, totalTime);
        }
    }

    public long getLevelDuration() {
        if (!isRunning)
            return 0; // Or track paused duration
        return System.currentTimeMillis() - levelStartTime;
    }

    public void stop() {
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;
    }
}
