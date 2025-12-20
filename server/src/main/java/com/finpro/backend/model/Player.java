package com.finpro.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID playerId;

    @Column(unique = true, nullable = false)
    private String username;

    private int highScore;
    private int totalCoins;
    private int totalDistance;
    private LocalDateTime createdAt;

    public Player(String username) {
        this.username = username;
        this.highScore = 0;
        this.totalCoins = 0;
        this.totalDistance = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Default constructor for Jackson
    public Player() {
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void updateHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
        }
    }

    public void addCoins(int coins) {
        this.totalCoins += coins;
    }

    public void addDistance(int distance) {
        this.totalDistance += distance;
    }

    public String getUsername() {
        return username;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setTotalCoins(int totalCoins) {
        this.totalCoins = totalCoins;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }
}
