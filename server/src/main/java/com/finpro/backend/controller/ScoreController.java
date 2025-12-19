package com.finpro.backend.controller;

import com.finpro.backend.model.Score;
import com.finpro.backend.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping
    public ResponseEntity<Score> submitScore(@RequestBody Score score) {
        scoreService.createScore(score);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/leaderboard")
    public List<Score> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        return scoreService.getLeaderboard(limit);
    }

    @GetMapping("/recent")
    public List<Score> getRecent() {
        return scoreService.getRecentScores();
    }

    @GetMapping("/player/{playerId}")
    public List<Score> getPlayerScores(@PathVariable UUID playerId) {
        return scoreService.getScoresByPlayerId(playerId);
    }
}
