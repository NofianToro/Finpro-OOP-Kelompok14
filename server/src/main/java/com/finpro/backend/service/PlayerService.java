package com.finpro.backend.service;

import com.finpro.backend.model.Player;
import com.finpro.backend.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void createPlayer(Player player) {
        playerRepository.save(player);
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public List<Player> getLeaderboardByHighScore(int limit) {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Player::getHighScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
