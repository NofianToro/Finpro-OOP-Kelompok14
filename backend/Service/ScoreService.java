package Service;

import Model.Score;
import Repository.ScoreRepository;
import Repository.PlayerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScoreService {

    private ScoreRepository scoreRepository;
    private PlayerRepository playerRepository;
    private PlayerService playerService;

    public ScoreService(ScoreRepository scoreRepository, PlayerRepository playerRepository, PlayerService playerService) {
        this.scoreRepository = scoreRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    public Score createScore(Score score) {
        // Verify player exists
        if (!playerRepository.findById(score.getPlayerId()).isPresent()) {
            throw new RuntimeException("Player not found with ID: " + score.getPlayerId());
        }

        // Save the score
        scoreRepository.save(score);

        // Update player statistics
        playerService.updatePlayerStats(
                score.getPlayerId(), // Tidak perlu cast ke UUID
                score.getValue(),
                score.getCoinsCollected(),
                score.getDistance()
        );

        return score;
    }

    public Optional<Score> getScoreById(UUID scoreId) {
        return scoreRepository.findById(scoreId);
    }

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    public List<Score> getScoresByPlayerId(UUID playerId) {
        return scoreRepository.findByPlayerId(playerId);
    }

    public List<Score> getScoresByPlayerIdOrderByValue(UUID playerId) {
        return scoreRepository.findByPlayerIdOrderByValueDesc(playerId);
    }

    public List<Score> getLeaderboard(int limit) {
        return scoreRepository.findTopScores(limit);
    }

    public Optional<Score> getHighestScoreByPlayerId(UUID playerId) {
        return scoreRepository.findHighestScoreByPlayerId(playerId);
    }

    public List<Score> getScoresAboveValue(int minValue) {
        return scoreRepository.findByValueGreaterThan(minValue);
    }

    public List<Score> getRecentScores() {
        return scoreRepository.findAllByOrderByCreatedAtDesc();
    }

    public int getTotalCoinsByPlayerId(UUID playerId) {
        Integer total = scoreRepository.getTotalCoinsByPlayerId(playerId);
        return total != null ? total : 0;
    }

    public int getTotalDistanceByPlayerId(UUID playerId) {
        Integer total = scoreRepository.getTotalDistanceByPlayerId(playerId);
        return total != null ? total : 0;
    }

    public Score updateScore(UUID scoreId, Score updatedScore) {
        Score existingScore = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new RuntimeException("Score not found with ID: " + scoreId));
        scoreRepository.save(existingScore);
        return existingScore;
    }

    public void deleteScore(UUID scoreId) {
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new RuntimeException("Score not found with ID: " + scoreId));
        scoreRepository.deleteById(scoreId);
    }

    public void deleteScoresByPlayerId(UUID playerId) {
        List<Score> playerScores = scoreRepository.findByPlayerId(playerId);
        for (Score score : playerScores) {
            scoreRepository.delete(score);
        }
    }
}