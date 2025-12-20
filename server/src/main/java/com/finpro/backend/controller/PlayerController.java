package com.finpro.backend.controller;

import com.finpro.backend.model.Player;
import com.finpro.backend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestParam String username) {
        Optional<Player> existing = playerService.getPlayerByUsername(username);
        if (existing.isPresent()) {
            return ResponseEntity.ok(existing.get());
        }
        Player newPlayer = new Player(username);
        playerService.createPlayer(newPlayer);
        return ResponseEntity.ok(newPlayer);
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }
    
    @GetMapping("/{username}")
    public ResponseEntity<Player> getPlayer(@PathVariable String username) {
        return playerService.getPlayerByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        return playerService.getLeaderboardByHighScore(limit);
    }
}
