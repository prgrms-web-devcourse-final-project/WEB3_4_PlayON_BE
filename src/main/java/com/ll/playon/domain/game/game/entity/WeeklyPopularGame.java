package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_popular_game", indexes = {
        @Index(name = "idx_week_start_date", columnList = "week_start_date"),
        @Index(name = "idx_week_start_date_play_count", columnList = "week_start_date, play_count DESC")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyPopularGame extends BaseEntity {
    @Column(name = "game_id")
    private Long gameId;

    @Column(name = "play_count")
    private Long playCount;

    @Column(name = "week_start_date")
    private LocalDate weekStartDate;

    // TODO: 파티 게임 연결 후 사용
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gameId", referencedColumnName = "id", insertable = false, updatable = false)
//    private SteamGame steamGame;
}
