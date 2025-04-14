package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_long_playtime_game", indexes = {
        @Index(name = "idx_week_start_date", columnList = "week_start_date"),
        @Index(name = "idx_week_start_date_playtime", columnList = "week_start_date, total_playtime DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WeeklyLongPlaytimeGame extends BaseEntity {
    @Column(name = "appid", nullable = false)
    private Long appid;

    @Column(name = "total_playtime", nullable = false)
    private Long totalPlaytime;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
}
