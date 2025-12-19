import Model.Player;
import Model.Score;
import Repository.PlayerRepository;
import Repository.ScoreRepository;
import Service.PlayerService;
import Service.ScoreService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        PlayerRepository playerRepository = new PlayerRepository();
        ScoreRepository scoreRepository = new ScoreRepository();

        // Initialize services with dependencies
        PlayerService playerService = new PlayerService(playerRepository);
        ScoreService scoreService = new ScoreService(scoreRepository, playerRepository, playerService);

        System.out.println("=== CS 4 ===\n");

        // 1. Create players
        Player player1 = new Player("NanaBanana");
        Player player2 = new Player("Yingko");
        Player player3 = new Player("LegdontWork");

        playerService.createPlayer(player1);
        playerService.createPlayer(player2);
        playerService.createPlayer(player3);

        System.out.println("Players created\n");

        // 2. Display all players
        System.out.println("All players:");
        List<Player> allPlayers = playerService.getAllPlayers();
        for (Player player : allPlayers) {
            player.showDetail();
        }

        // 3. Create scores for players
        Score score1 = new Score(player1.getPlayerId(), 1500, 50, 3000);
        Score score2 = new Score(player1.getPlayerId(), 2000, 75, 4500);
        Score score3 = new Score(player2.getPlayerId(), 1800, 60, 3500);
        Score score4 = new Score(player3.getPlayerId(), 1200, 40, 2500);
        Score score5 = new Score(player3.getPlayerId(), 2500, 90, 5000);

        scoreService.createScore(score1);
        scoreService.createScore(score2);
        scoreService.createScore(score3);
        scoreService.createScore(score4);
        scoreService.createScore(score5);

        System.out.println("Scores created!\n");

        // 4. Display player stats after scoring
        System.out.println("Player Score:");
        for (Player player : allPlayers) {
            player.showDetail();
        }


        // Ini isi  apa aja
        // Top players by high score
        System.out.println("Top 2 players by high score");
        List<Player> topPlayers = playerService.getLeaderboardByHighScore(2);
        for (Player player : topPlayers) {
            player.showDetail();
        }

        // Scores for a specific player
        System.out.println("All scores for " + player1.getUsername() + ":");
        List<Score> player1Scores = scoreService.getScoresByPlayerId(player1.getPlayerId());
        for (Score score : player1Scores) {
            score.showDetail();
        }

        // Get leaderboard
        System.out.println("Top 3 scores overall:");
        List<Score> topScores = scoreService.getLeaderboard(3);
        for (Score score : topScores) {
            score.showDetail();
        }

        // Find player by username
        System.out.println("earching for player 'NanaBanana':");
        Optional<Player> foundPlayer = playerService.getPlayerByUsername("NanaBanana");
        if (foundPlayer.isPresent()) {
            foundPlayer.get().showDetail();
        } else {
            System.out.println("Player not found!");
        }

        // Total coins and distance for a player
        System.out.println("Totals for " + player3.getUsername() + ":");
        int totalCoins = scoreService.getTotalCoinsByPlayerId(player3.getPlayerId());
        int totalDistance = scoreService.getTotalDistanceByPlayerId(player3.getPlayerId());
        System.out.println("Total coins: " + totalCoins);
        System.out.println("Total distance: " + totalDistance + "\n");

        // Get recent scores
        System.out.println("Recent scores (ordered by creation time):");
        List<Score> recentScores = scoreService.getRecentScores();
        for (Score score : recentScores) {
            score.showDetail();
        }

    }
}