package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Player implements ShowDetail{
    private UUID playerId;
    private String username;
    private int highScore;
    private int totalCoins;
    private int totalDistance;
    private LocalDateTime createdAt;

    public Player(String username) {
        this.playerId = UUID.randomUUID();
        this.username = username;
        this.highScore = 0;
        this.totalCoins = 0;
        this.totalDistance = 0;
        this.createdAt = LocalDateTime.now();
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

    @Override
    public void showDetail() {
        System.out.println("Player ID: " + playerId);
        System.out.println("Username: " + username);
        System.out.println("High Score: " + highScore);
        System.out.println("Total Coins: " + totalCoins);
        System.out.println("Total Distance: " + totalDistance);
        System.out.println("Created At: " + createdAt);
        System.out.println();
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