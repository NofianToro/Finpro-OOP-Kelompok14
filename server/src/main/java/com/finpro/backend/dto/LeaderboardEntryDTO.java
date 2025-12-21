package com.finpro.backend.dto;

import java.util.UUID;

public class LeaderboardEntryDTO {
    private String username;
    private long totalTime;
    private long level1Time;
    private long level2Time;
    private long level3Time;
    private long level4Time;
    private long level5Time;

    // Default constructor
    public LeaderboardEntryDTO() {
    }

    public LeaderboardEntryDTO(String username, long totalTime, long l1, long l2, long l3, long l4, long l5) {
        this.username = username;
        this.totalTime = totalTime;
        this.level1Time = l1;
        this.level2Time = l2;
        this.level3Time = l3;
        this.level4Time = l4;
        this.level5Time = l5;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getLevel1Time() {
        return level1Time;
    }

    public void setLevel1Time(long level1Time) {
        this.level1Time = level1Time;
    }

    public long getLevel2Time() {
        return level2Time;
    }

    public void setLevel2Time(long level2Time) {
        this.level2Time = level2Time;
    }

    public long getLevel3Time() {
        return level3Time;
    }

    public void setLevel3Time(long level3Time) {
        this.level3Time = level3Time;
    }

    public long getLevel4Time() {
        return level4Time;
    }

    public void setLevel4Time(long level4Time) {
        this.level4Time = level4Time;
    }

    public long getLevel5Time() {
        return level5Time;
    }

    public void setLevel5Time(long level5Time) {
        this.level5Time = level5Time;
    }
}
