package com.finpro.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID scoreId;

    private UUID playerId;

    private long level1Time;
    private long level2Time;
    private long level3Time;
    private long level4Time;
    private long level5Time;
    private long totalTime;

    private LocalDateTime playedAt;

    public Score(UUID playerId, long level1Time, long level2Time, long level3Time, long level4Time, long level5Time) {
        this.playerId = playerId;
        this.level1Time = level1Time;
        this.level2Time = level2Time;
        this.level3Time = level3Time;
        this.level4Time = level4Time;
        this.level5Time = level5Time;
        this.totalTime = level1Time + level2Time + level3Time + level4Time + level5Time;
        this.playedAt = LocalDateTime.now();
    }

    // Default constructor for Jackson
    public Score() {
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getLevel1Time() {
        return level1Time;
    }

    public long getLevel2Time() {
        return level2Time;
    }

    public long getLevel3Time() {
        return level3Time;
    }

    public long getLevel4Time() {
        return level4Time;
    }

    public long getLevel5Time() {
        return level5Time;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setScoreId(UUID scoreId) {
        this.scoreId = scoreId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setLevel1Time(long level1Time) {
        this.level1Time = level1Time;
    }

    public void setLevel2Time(long level2Time) {
        this.level2Time = level2Time;
    }

    public void setLevel3Time(long level3Time) {
        this.level3Time = level3Time;
    }

    public void setLevel4Time(long level4Time) {
        this.level4Time = level4Time;
    }

    public void setLevel5Time(long level5Time) {
        this.level5Time = level5Time;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
