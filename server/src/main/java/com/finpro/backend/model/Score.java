package com.finpro.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID scoreId;

    private UUID playerId;

    private int score;
    private int coinsCollected;
    private int distanceTraveled;
    private LocalDateTime playedAt;

    public Score(UUID playerId, int score, int coinsCollected, int distanceTraveled) {
        this.playerId = playerId;
        this.score = score;
        this.coinsCollected = coinsCollected;
        this.distanceTraveled = distanceTraveled;
        this.playedAt = LocalDateTime.now();
    }

    // Default constructor for Jackson
    public Score() {
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getScore() {
        return score;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }
}
