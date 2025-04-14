package com.ll.playon.domain.game.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_steam_genre_name", columnList = "name")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SteamGenre extends BaseTime {

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    @Builder.Default
    private List<SteamGame> games = new ArrayList<>();

    public SteamGenre(String name) {
        this.name = name;
    }
}
