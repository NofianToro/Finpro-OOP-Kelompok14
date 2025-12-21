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

    private long bestTime; // Total time in milliseconds (lower is better)
    private LocalDateTime createdAt;

    public Player(String username) {
        this.username = username;
        this.bestTime = Long.MAX_VALUE; // Initialize with max value since lower is better
        this.createdAt = LocalDateTime.now();
    }

    // Default constructor for Jackson
    public Player() {
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void updateBestTime(long time) {
        if (time < this.bestTime) {
            this.bestTime = time;
        }
    }

    public String getUsername() {
        return username;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }
}
