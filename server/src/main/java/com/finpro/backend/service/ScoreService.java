package com.finpro.backend.service;

import com.finpro.backend.dto.LeaderboardEntryDTO;
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
        if (score.getTotalTime() == 0) {
            long total = score.getLevel1Time() + score.getLevel2Time() + score.getLevel3Time() +
                    score.getLevel4Time() + score.getLevel5Time();
            score.setTotalTime(total);
        }

        scoreRepository.save(score);

        playerRepository.findById(score.getPlayerId()).ifPresent(player -> {
            player.updateBestTime(score.getTotalTime());
            playerRepository.save(player);
        });
    }

    public List<Score> getScoresByPlayerId(UUID playerId) {
        return scoreRepository.findByPlayerId(playerId);
    }

    public List<LeaderboardEntryDTO> getLeaderboard(int limit) {
        return scoreRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Score::getTotalTime))
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaderboardEntryDTO> getLeaderboardByLevel(int level, int limit) {
        return scoreRepository.findAll().stream()
                .sorted((s1, s2) -> Long.compare(getLevelTime(s1, level), getLevelTime(s2, level)))
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LeaderboardEntryDTO convertToDTO(Score score) {
        String username = playerRepository.findById(score.getPlayerId())
                .map(Player::getUsername)
                .orElse("Unknown"); // Fallback

        return new LeaderboardEntryDTO(
                username,
                score.getTotalTime(),
                score.getLevel1Time(),
                score.getLevel2Time(),
                score.getLevel3Time(),
                score.getLevel4Time(),
                score.getLevel5Time());
    }

    private long getLevelTime(Score s, int level) {
        switch (level) {
            case 1:
                return s.getLevel1Time();
            case 2:
                return s.getLevel2Time();
            case 3:
                return s.getLevel3Time();
            case 4:
                return s.getLevel4Time();
            case 5:
                return s.getLevel5Time();
            default:
                return Long.MAX_VALUE;
        }
    }

    public List<Score> getRecentScores() {
        return scoreRepository.findAll().stream()
                .sorted(Comparator.comparing(Score::getPlayedAt).reversed())
                .collect(Collectors.toList());
    }
}
