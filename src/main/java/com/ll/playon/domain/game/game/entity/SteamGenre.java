package com.ll.playon.domain.game.game.entity;

import com.ll.playon.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SteamGenre extends BaseTime {
    @Column(unique = true)
    private String genre;

    @ManyToMany(mappedBy = "genres")
    private List<SteamGame> games = new ArrayList<>();

    public SteamGenre(String genre) {
        this.genre = genre;
    }
}
