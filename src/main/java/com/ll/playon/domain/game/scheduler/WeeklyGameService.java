package com.ll.playon.domain.game.scheduler;

import com.ll.playon.domain.game.game.entity.WeeklyPopularGame;
import com.ll.playon.domain.game.scheduler.repository.WeeklyGameRepository;
import com.ll.playon.domain.party.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeeklyGameService {
    private final PartyRepository partyRepository;
    private final WeeklyGameRepository weeklyGameRepository;

    @Transactional
    public void updatePopularGames(LocalDateTime fromDate, LocalDateTime toDate, LocalDate weekStart) {
        final int limit = 3;

        List<Map<String, Object>> result = partyRepository.findTopGamesByPartyLastWeek(fromDate, toDate, limit);

        List<WeeklyPopularGame> list = result.stream()
                .map(row -> WeeklyPopularGame.builder()
                        .gameId(((Number) row.get("gameId")).longValue())
                        .playCount(((Number) row.get("playCount")).longValue())
                        .weekStartDate(weekStart)
                        .build())
                .toList();

        weeklyGameRepository.saveAll(list);
    }
}
