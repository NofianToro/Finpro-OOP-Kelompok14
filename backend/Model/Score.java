package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Score {
    private UUID scoreId;
    private UUID playerId;
    private Player player;
    private int value;
    private int coinsCollected;
    private int distance;
    private LocalDateTime createdAt;

    public Score(UUID playerId, int score, int coinsCollected, int distance) {
        this.scoreId = UUID.randomUUID();
        this.playerId = playerId;
        this.value = score;
        this.coinsCollected = coinsCollected;
        this.distance = distance;
        this.createdAt = LocalDateTime.now();
    }

    public void showDetail() {
        System.out.println("Score ID: " + scoreId);
        System.out.println("Player ID: " + playerId);
        System.out.println("Score Value: " + value);
        System.out.println("Coins Collected: " + coinsCollected);
        System.out.println("Distance: " + distance);
        System.out.println("Created At: " + createdAt);
        System.out.println();
    }

    public int getValue() {
        return value;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public int getDistance() {
        return distance;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}