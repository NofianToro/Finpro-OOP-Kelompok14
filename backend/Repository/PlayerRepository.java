package Repository;

import Model.Player;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerRepository extends BaseRepository<Player, UUID> {

    @Override
    public void save(Player player) {
        UUID id = getId(player);
        dataMap.put(id, player);
        allData.add(player);
    }

    @Override
    public UUID getId(Player entity) {
        return entity.getPlayerId();
    }

    public Optional<Player> findByUsername(String username) {
        return allData.stream()
                .filter(player -> player.getUsername().equals(username))
                .findFirst();
    }

    public List<Player> findTopPlayersByHighScore(int limit) {
        return allData.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getHighScore(), p1.getHighScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Player> findByHighscoreGreaterThan(int minScore) {
        return allData.stream()
                .filter(player -> player.getHighScore() > minScore)
                .collect(Collectors.toList());
    }

    public boolean existByUsername(String username) {
        return allData.stream()
                .anyMatch(player -> player.getUsername().equals(username));
    }

    public List<Player> findAllByOrderByTotalCoinsDesc() {
        return allData.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getTotalCoins(), p1.getTotalCoins()))
                .collect(Collectors.toList());
    }

    public List<Player> findAllByOrderByTotalDistanceTravelledDesc() {
        return allData.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getTotalDistance(), p1.getTotalDistance()))
                .collect(Collectors.toList());
    }
}