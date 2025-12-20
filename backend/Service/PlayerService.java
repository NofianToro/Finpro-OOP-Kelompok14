package Service;


import Model.Player;
import Model.Score;
import Repository.PlayerRepository;
import Repository.ScoreRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerService {

    private PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Create a new player
     * @param player the player to create
     * @return the created player
     * @throws RuntimeException if username already exists
     */
    public Player createPlayer(Player player) {
        if (existsByUsername(player.getUsername())) {
            throw new RuntimeException("Username already exists: " + player.getUsername());
        }
        playerRepository.save(player);
        return player;
    }

    /**
     * Get player by ID
     * @param playerId the player ID
     * @return Optional containing the player if found
     */
    public Optional<Player> getPlayerById(UUID playerId) {
        return playerRepository.findById(playerId);
    }

    /**
     * Get player by username
     * @param username the username
     * @return Optional containing the player if found
     */
    public Optional<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    /**
     * Get all players
     * @return List of all players
     */
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Update player profile
     * @param playerId the player ID
     * @param updatedPlayer the updated player data
     * @return the updated player
     * @throws RuntimeException if player not found
     */
    public Player updatePlayer(UUID playerId, Player updatedPlayer) {
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));

        // Update username jika berbeda dan tersedia
        if (updatedPlayer.getUsername() != null &&
                !updatedPlayer.getUsername().equals(existingPlayer.getUsername())) {

            if (existsByUsername(updatedPlayer.getUsername())) {
                throw new RuntimeException("Username already exists: " + updatedPlayer.getUsername());
            }
            existingPlayer.setUsername(updatedPlayer.getUsername());
        }

        // Update high score jika lebih tinggi
        if (updatedPlayer.getHighScore() > existingPlayer.getHighScore()) {
            existingPlayer.setHighScore(updatedPlayer.getHighScore());
        }

        // Update fields lainnya (cara sama)
        if (updatedPlayer.getTotalCoins() > existingPlayer.getTotalCoins()) {
            existingPlayer.setTotalCoins(updatedPlayer.getTotalCoins());
        }

        if (updatedPlayer.getTotalDistance() > existingPlayer.getTotalDistance()) {
            existingPlayer.setTotalDistance(updatedPlayer.getTotalDistance());
        }

        playerRepository.save(existingPlayer);
        return existingPlayer;
    }

    /**
     * Delete player by ID
     * @param playerId the player ID
     * @throws RuntimeException if player not found
     */
    public void deletePlayer(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));
        playerRepository.deleteById(playerId);
    }

    public void deletePlayerByUsername(String username) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Player not found with username: " + username));
        playerRepository.delete(player);
    }

    /**
     * Update player statistics based on a new score
     * @param playerId the player ID
     * @param scoreValue the new score value
     * @param coinsCollected coins collected in this game
     * @param distanceTravelled distance travelled in this game
     * @return the updated player
     */
    public Player updatePlayerStats(UUID playerId, int scoreValue, int coinsCollected, int distanceTravelled) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));

        // Update high score if this score is higher
        player.updateHighScore(scoreValue);

        // Add coins and distance to totals
        player.addCoins(coinsCollected);
        player.addDistance(distanceTravelled);

        playerRepository.save(player);
        return player;
    }

    /**
     * Get leaderboard by high score
     * @param limit maximum number of players to return
     * @return List of top players by high score
     */
    public List<Player> getLeaderboardByHighScore(int limit) {
        return playerRepository.findTopPlayersByHighScore(limit);
    }

    /**
     * Get leaderboard by total coins
     * @return List of players ordered by total coins
     */
    public List<Player> getLeaderboardByTotalCoins() {
        return playerRepository.findAllByOrderByTotalCoinsDesc();
    }

    /**
     * Get leaderboard by total distance
     * @return List of players ordered by total distance
     */
    public List<Player> getLeaderboardByTotalDistance() {
        return playerRepository.findAllByOrderByTotalDistanceTravelledDesc();
    }

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return playerRepository.findByUsername(username).isPresent();
    }
}