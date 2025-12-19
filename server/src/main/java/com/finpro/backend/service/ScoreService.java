package com.finpro.backend.service;

import com.finpro.backend.model.Player;
import com.finpro.backend.model.Score;
import com.finpro.backend.repository.PlayerRepository;
import com.finpro.backend.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final PlayerRepository playerRepository;

    public ScoreService(ScoreRepository scoreRepository, PlayerRepository playerRepository) {
        this.scoreRepository = scoreRepository;
        this.playerRepository = playerRepository;
    }

    public void createScore(Score score) {
        scoreRepository.save(score);

        // Update player stats
        playerRepository.findById(score.getPlayerId()).ifPresent(player -> {
            player.updateHighScore(score.getScore());
            player.addCoins(score.getCoinsCollected());
            player.addDistance(score.getDistanceTraveled());
            playerRepository.save(player);
        });
    }

    public List<Score> getScoresByPlayerId(UUID playerId) {
        return scoreRepository.findByPlayerId(playerId);
    }

    public List<Score> getLeaderboard(int limit) {
        return scoreRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Score::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Score> getRecentScores() {
        return scoreRepository.findAll().stream()
                .sorted(Comparator.comparing(Score::getPlayedAt).reversed())
                .collect(Collectors.toList());
    }

    public int getTotalCoinsByPlayerId(UUID playerId) {
        return scoreRepository.findByPlayerId(playerId).stream()
                .mapToInt(Score::getCoinsCollected)
                .sum();
    }

    public int getTotalDistanceByPlayerId(UUID playerId) {
        return scoreRepository.findByPlayerId(playerId).stream()
                .mapToInt(Score::getDistanceTraveled)
                .sum();
    }
}
