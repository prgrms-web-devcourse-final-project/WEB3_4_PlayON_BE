package com.ll.playon.domain.guild.guild.entity;

import com.ll.playon.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "weekly_popular_guild", indexes = {
        @Index(name = "idx_week_start_date", columnList = "week_start_date"),
        @Index(name = "idx_week_start_date_post_count", columnList = "week_start_date, post_count DESC")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyPopularGuild extends BaseEntity {

    @Column(name = "guild_id", nullable = false)
    private Long guildId;

    @Column(name = "post_count", nullable = false)
    private Long postCount;

    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;
}
